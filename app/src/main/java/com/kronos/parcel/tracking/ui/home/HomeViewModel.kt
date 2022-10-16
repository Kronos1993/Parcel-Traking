package com.kronos.parcel.tracking.ui.home

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kronos.core.extensions.asLiveData
import com.kronos.core.extensions.formatDate
import com.kronos.core.notification.INotifications
import com.kronos.core.notification.NotificationType
import com.kronos.core.view_model.ParentViewModel
import com.kronos.data.remote.retrofit.UrlProvider
import com.kronos.domain.model.event.EventModel
import com.kronos.domain.model.parcel.ParcelModel
import com.kronos.domain.repository.event.EventLocalRepository
import com.kronos.domain.repository.parcel.ParcelLocalRepository
import com.kronos.domain.repository.parcel.ParcelRemoteRepository
import com.kronos.domain.repository.statistics.StatisticsLocalRepository
import com.kronos.domain.repository.user.UserLocalRepository
import com.kronos.parcel.tracking.MainState
import com.kronos.parcel.tracking.R
import com.kronos.parcel.tracking.ui.home.state.HomeState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    private var parcelLocalRepository: ParcelLocalRepository,
    private var parcelRemoteRepository: ParcelRemoteRepository,
    private var eventLocalRepository: EventLocalRepository,
    private var statisticsLocalRepository: StatisticsLocalRepository,
    var urlProvider: UrlProvider,
    private var userRepository: UserLocalRepository,
    var notification: INotifications,
) : ParentViewModel() {
    private val _parcelList = MutableLiveData<List<ParcelModel>>()
    val parcelList = _parcelList.asLiveData()

    val parcelAdapter: ParcelAdapter = ParcelAdapter()

    private val _state = MutableLiveData<MainState>()
    val state = _state.asLiveData()

    fun setState(state: MainState) {
        _state.value = state
    }

    fun postState(state: MainState) {
        _state.postValue(state)
    }

    private fun postParcelList(parcelList: List<ParcelModel>) {
        _parcelList.postValue(parcelList)
    }

    fun getParcels() {
        setState(HomeState.Loading(true))
        viewModelScope.launch {
            var list = parcelLocalRepository.listAllParcelLocal()
            postParcelList(list)
            setState(HomeState.Loading(false))
        }
    }

    fun toHistory(itemAt: ParcelModel) {
        itemAt.history = true
        setState(HomeState.Loading(true))
        viewModelScope.launch {
            parcelLocalRepository.saveParcel(itemAt)
            logParcelToHistory(itemAt)
            increaseArchivedStatistics()
            getParcels()
        }
    }

    private fun logParcelToHistory(item: ParcelModel) {
        viewModelScope.launch {
            eventLocalRepository.saveEvent(
                EventModel(
                    0,
                    context.getString(R.string.parcel_updated_event).format(item.name),
                    context.getString(R.string.parcel_updated_to_archive_event_details).format(
                        item.name,
                        Date(item.dateUpdated).formatDate(context.getString(R.string.date_format))
                    ),
                    false,
                    item.trackingNumber,
                    Calendar.getInstance().timeInMillis,
                    Calendar.getInstance().timeInMillis,
                )
            )
        }
        setState(MainState.NewEvent)
    }

    private fun logParcelStatusUpdated(item: ParcelModel, oldStatus: String, newStatus: String) {
        viewModelScope.launch {
            eventLocalRepository.saveEvent(
                EventModel(
                    0,
                    context.getString(R.string.notification_title).format(item.name),
                    context.getString(R.string.notification_details)
                        .format(item.trackingNumber, oldStatus, newStatus),
                    false,
                    item.trackingNumber,
                    Calendar.getInstance().timeInMillis,
                    Calendar.getInstance().timeInMillis,
                )
            )
            setState(MainState.NewEvent)
        }
    }

    private fun logParcelAdded(item: ParcelModel) {
        viewModelScope.launch {
            eventLocalRepository.saveEvent(
                EventModel(
                    0,
                    context.getString(R.string.parcel_added_event).format(item.name),
                    context.getString(R.string.parcel_added_event_details).format(
                        item.name,
                        Date(item.dateUpdated).formatDate(context.getString(R.string.date_format))
                    ),
                    false,
                    item.trackingNumber,
                    Calendar.getInstance().timeInMillis,
                    Calendar.getInstance().timeInMillis,
                )
            )
        }
    }

    fun getNewParcel(trackingNumber: String, trackingName: String) {
        var parcel = ParcelModel(trackingNumber = "", status = "", imageUrl = "")
        setState(HomeState.Loading(true))
        viewModelScope.launch(Dispatchers.IO) {
            val call = async {
                parcel = parcelRemoteRepository.searchParcel(trackingNumber.orEmpty())
            }
            call.await()
            parcel.name = trackingName
            val save = async {
                parcelLocalRepository.saveParcel(parcel)
            }
            save.await()
            logParcelAdded(parcel)
            increaseTotalParcelStatistics()
            postState(HomeState.Loading(false))
            postState(HomeState.Search)
        }
    }

    fun refreshParcels() {
        viewModelScope.launch {
            setState(HomeState.Refreshing(true))
            viewModelScope.launch(Dispatchers.IO) {
                parcelLocalRepository.listAllParcelLocal().let {
                    if (it.isNotEmpty()) {
                        _parcelList.postValue(it)
                        it.forEachIndexed { index, parcelModel ->
                            refreshParcel(parcelModel, index)
                        }
                    } else {
                        postState(HomeState.Refreshing(false))
                    }
                }
            }
        }
    }

    fun refreshParcel(parcel: ParcelModel, current: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            lateinit var parcelUpdate: ParcelModel
            var oldState = parcel.status
            val call = async {
                parcelUpdate = parcelRemoteRepository.searchParcel(parcel.trackingNumber)
                if (parcelUpdate.status != "not found" && parcel.status != parcelUpdate.status) {
                    parcel.imageUrl = parcelUpdate.imageUrl
                    notification.createNotification(
                        context.getString(R.string.notification_title).format(parcel.name),
                        context.getString(R.string.notification_details)
                            .format(parcel.trackingNumber, parcel.status, parcelUpdate.status),
                        NotificationType.GENERAL.name,
                        NotificationType.PARCEL_STATUS,
                        com.kronos.resources.R.drawable.ic_notifications,
                        context
                    )
                    logParcelStatusUpdated(parcel, oldState, parcelUpdate.status)
                    parcel.status = parcelUpdate.status
                    if (parcelUpdate.status.contains("Entregado")){
                        if (oldState.contains("tránsito")){
                            decreaseTransitStatistics()
                        }
                        increaseReceivedStatistics()
                    }
                }
                parcel.dateUpdated = parcelUpdate.dateUpdated
            }
            call.await()
            if (parcelUpdate.fail.isEmpty()) {
                val save = async {
                    parcelLocalRepository.saveParcel(parcel)
                }
                save.await()
            } else {
                var currentError = Hashtable<String, String>()
                currentError["error"] = parcelUpdate.fail
                postState(HomeState.Error(currentError))
            }
            postState(HomeState.Refreshing(false))
        }
    }

    fun increaseTotalParcelStatistics() {
        viewModelScope.launch {
            if (!userRepository.getUser().name.isNullOrEmpty()) {
                var statisticsModel = statisticsLocalRepository.get()
                statisticsModel.added +=1
                statisticsLocalRepository.saveStatistics(statisticsModel)
            }
        }
    }

    fun increaseReceivedStatistics() {
        viewModelScope.launch {
            if (!userRepository.getUser().name.isNullOrEmpty()) {
                var statisticsModel = statisticsLocalRepository.get()
                statisticsModel.received +=1
                statisticsLocalRepository.saveStatistics(statisticsModel)
            }
        }
    }

    fun increaseArchivedStatistics() {
        viewModelScope.launch {
            if (!userRepository.getUser().name.isNullOrEmpty()) {
                var statisticsModel = statisticsLocalRepository.get()
                statisticsModel.archived +=1
                statisticsLocalRepository.saveStatistics(statisticsModel)
            }
        }
    }

    fun increaseTransitStatistics() {
        viewModelScope.launch {
            if (!userRepository.getUser().name.isNullOrEmpty()) {
                var statisticsModel = statisticsLocalRepository.get()
                statisticsModel.inTransit +=1
                statisticsLocalRepository.saveStatistics(statisticsModel)
            }
        }
    }

    fun decreaseTransitStatistics() {
        viewModelScope.launch {
            if (!userRepository.getUser().name.isNullOrEmpty()) {
                var statisticsModel = statisticsLocalRepository.get()
                statisticsModel.inTransit -=1
                statisticsLocalRepository.saveStatistics(statisticsModel)
            }
        }
    }
}