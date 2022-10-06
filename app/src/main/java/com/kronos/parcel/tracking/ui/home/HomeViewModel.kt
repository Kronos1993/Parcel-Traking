package com.kronos.parcel.tracking.ui.home

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kronos.core.extensions.asLiveData
import com.kronos.core.notification.INotifications
import com.kronos.core.notification.NotificationType
import com.kronos.core.view_model.ParentViewModel
import com.kronos.domain.model.event.EventModel
import com.kronos.domain.model.parcel.ParcelModel
import com.kronos.domain.repository.event.EventLocalRepository
import com.kronos.domain.repository.parcel.ParcelLocalRepository
import com.kronos.domain.repository.parcel.ParcelRemoteRepository
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
    var notification: INotifications,
) : ParentViewModel() {
    private val _parcelList = MutableLiveData<List<ParcelModel>>()
    val parcelList = _parcelList.asLiveData()

    val parcelAdapter: ParcelAdapter = ParcelAdapter()

    private val _state = MutableLiveData<HomeState>()
    val state = _state.asLiveData()

    fun setState(state: HomeState) {
        _state.value = state
    }

    fun postState(state: HomeState) {
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
            getParcels()
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
                            refreshParcel(parcelModel,index)
                        }
                    } else {
                        postState(HomeState.Refreshing(false))
                    }
                }
            }
        }
    }

    fun refreshParcel(parcel: ParcelModel,current:Int) {
        viewModelScope.launch(Dispatchers.IO) {
            lateinit var parcelUpdate: ParcelModel
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
                    eventLocalRepository.saveEvent(
                        EventModel(
                            0,
                            context.getString(R.string.notification_title).format(parcel.name),
                            context.getString(R.string.notification_details)
                                .format(parcel.trackingNumber, parcel.status, parcelUpdate.status),
                            false,
                            parcel.trackingNumber,
                            Calendar.getInstance().timeInMillis,
                            Calendar.getInstance().timeInMillis,
                        )
                    )
                    parcel.status = parcelUpdate.status
                }
                parcel.dateUpdated = parcelUpdate.dateUpdated
            }
            call.await()
            Log.e("REFRESH PARCEL", "refreshParcelOneByOne POS: $current")
            Log.e("REFRESH PARCEL", "refreshParcelOneByOne Parcel update: ${parcelUpdate.name}")
            Log.e("REFRESH PARCEL", "refreshParcelOneByOne Parcel update: ${parcelUpdate}")
            Log.e("REFRESH PARCEL", "refreshParcelOneByOne Current Parcel: ${parcel.name}")
            Log.e("REFRESH PARCEL", "refreshParcelOneByOne Current Parcel: ${parcel}")
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
}