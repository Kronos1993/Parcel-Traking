package com.kronos.parcel.tracking.ui.home

import android.content.Context
import android.hardware.Camera
import android.os.Bundle
import androidx.databinding.Observable
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.journeyapps.barcodescanner.ScanOptions
import com.kronos.core.extensions.asLiveData
import com.kronos.core.extensions.formatDate
import com.kronos.core.notification.INotifications
import com.kronos.core.notification.NotificationGroup
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
import com.kronos.logger.LoggerType
import com.kronos.logger.interfaces.ILogger
import com.kronos.parcel.tracking.MainState
import com.kronos.parcel.tracking.R
import com.kronos.parcel.tracking.ui.home.state.HomeState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
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
    var logger: ILogger,
) : ParentViewModel() {
    private val _parcelList = MutableLiveData<List<ParcelModel>>()
    val parcelList = _parcelList.asLiveData()

    var parcelAdapter: WeakReference<ParcelAdapter?> = WeakReference(ParcelAdapter())

    private val _state = MutableLiveData<MainState>()
    val state = _state.asLiveData()

    var trackingNumber = ObservableField<String?>()
    var name = ObservableField<String?>()
    var trackingNumberError = ObservableField<String?>()
    var nameError = ObservableField<String?>()

    private fun setState(state: MainState) {
        _state.value = state
    }

    private fun postState(state: MainState) {
        _state.postValue(state)
    }

    private fun postParcelList(parcelList: List<ParcelModel>) {
        _parcelList.postValue(parcelList)
    }

    fun getParcels() {
        setState(HomeState.Loading(true))
        viewModelScope.launch {
            val list = parcelLocalRepository.listAllParcelLocal()
            postParcelList(list)
            setState(HomeState.Loading(false))
            setState(HomeState.Idle)
        }
    }

    fun validateField() : Boolean{
        var valid = true
        if (trackingNumber.get().orEmpty().isEmpty()){
            valid = false
            trackingNumberError.set(context.getString(com.kronos.resources.R.string.required_field))
        }else{
            trackingNumberError.set(null)
        }
        if (name.get().orEmpty().isEmpty()){
            valid = false
            nameError.set(context.getString(com.kronos.resources.R.string.required_field))
        }else{
            nameError.set(null)
        }

        return valid
    }

    fun toHistory(itemAt: ParcelModel) {
        itemAt.history = true
        setState(HomeState.Loading(true))
        viewModelScope.launch {
            parcelLocalRepository.saveParcel(itemAt)
            logParcelToHistory(itemAt)
            logger.write(this::class.java.name,LoggerType.INFO,"Parcel ${itemAt.trackingNumber} to history on ${Date().formatDate("dd-MM-yyyy")}")
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

    fun getNewParcel() {
        var parcel = ParcelModel(trackingNumber = "", notes = "", status = "", imageUrl = "")
        setState(HomeState.Loading(true))
        viewModelScope.launch(Dispatchers.IO) {
            val call = async {
                parcel = parcelRemoteRepository.searchParcel(trackingNumber.get().orEmpty())
            }
            call.await()
            parcel.name = name.get().orEmpty()
            val save = async {
                parcelLocalRepository.saveParcel(parcel)
            }
            save.await()
            logParcelAdded(parcel)
            increaseTotalParcelStatistics()
            if (parcel.status.contains("Entregado"))
                increaseReceivedStatistics()
            logger.write(this::class.java.name,LoggerType.INFO,"Parcel ${parcel.trackingNumber} added on ${Date().formatDate("dd-MM-yyyy")}")
            name.set(null)
            trackingNumber.set(null)
            postState(HomeState.Loading(false))
            postState(HomeState.Search)
        }
    }

    fun refreshParcels() {
        viewModelScope.launch {
            setState(HomeState.Refreshing(true))
            parcelAdapter.get()?.currentList.let {
                if (it!=null && it.isNotEmpty()) {
                    val list = it.toMutableList()
                    it.forEachIndexed { index, parcelModel ->
                        parcelModel.loading = true
                        parcelAdapter.get()?.notifyItemChanged(index)
                    }
                    refreshParcel(list,0,it.size)
                } else {
                    postState(HomeState.Refreshing(false))
                }
            }
            postState(HomeState.Refreshing(false))
        }
    }

    private fun refreshParcel(parcels: MutableList<ParcelModel>, current: Int, total:Int) {
        if (current<parcels.size){
            val parcel = parcels[current]
            viewModelScope.launch(Dispatchers.IO) {
                lateinit var parcelUpdate: ParcelModel
                val oldState = parcel.status
                val call = async {
                    parcelUpdate = parcelRemoteRepository.searchParcel(parcel.trackingNumber)
                    if (parcelUpdate.status != "not found" && parcel.status != parcelUpdate.status) {
                        parcel.imageUrl = parcelUpdate.imageUrl
                        notification.createNotification(
                            context.getString(R.string.notification_title).format(parcel.name),
                            context.getString(R.string.notification_details)
                                .format(parcel.trackingNumber, parcel.status, parcelUpdate.status),
                            NotificationGroup.GENERAL,
                            NotificationType.PARCEL_STATUS,
                            com.kronos.resources.R.drawable.ic_notifications,
                            context
                        )
                        logParcelStatusUpdated(parcel, oldState, parcelUpdate.status)
                        parcel.status = parcelUpdate.status
                        if (parcelUpdate.status.contains("Entregado")) {
                            increaseReceivedStatistics()
                        }
                    }
                    parcel.dateUpdated = parcelUpdate.dateUpdated
                    if (parcelUpdate.fail.isEmpty()) {
                        val save = async {
                            parcelLocalRepository.saveParcel(parcel)
                        }
                        save.await()
                        logger.write(this::class.java.name,LoggerType.INFO,"Parcel ${parcel.trackingNumber} refreshed on ${Date().formatDate("dd-MM-yyyy")}")
                    } else {
                        val currentError = Hashtable<String, String>()
                        currentError["error"] = parcelUpdate.fail
                        postState(HomeState.Error(currentError))
                        //refreshParcel(parcels,current+1,total)
                        logger.write(this::class.java.name,LoggerType.ERROR,"Parcel ${parcelUpdate.trackingNumber} error: ${parcelUpdate.fail} on ${Date().formatDate("dd-MM-yyyy")}")
                    }
                    parcel.loading = false
                    viewModelScope.launch(Dispatchers.Main){
                        parcelAdapter.get()?.notifyItemChanged(current)
                    }
                }
                call.await()
                refreshParcel(parcels,current+1,total)
            }
        }else{
            postState(HomeState.Idle)
        }
    }

    private fun increaseTotalParcelStatistics() {
        viewModelScope.launch {
            if (userRepository.getUser().name.isNotEmpty()) {
                val statisticsModel = statisticsLocalRepository.get()
                statisticsModel.added += 1
                statisticsLocalRepository.saveStatistics(statisticsModel)
            }
        }
    }

    private fun increaseReceivedStatistics() {
        viewModelScope.launch {
            if (userRepository.getUser().name.isNotEmpty()) {
                val statisticsModel = statisticsLocalRepository.get()
                statisticsModel.received += 1
                statisticsLocalRepository.saveStatistics(statisticsModel)
            }
        }
    }

    fun observeTextChange() {
        trackingNumber.addOnPropertyChangedCallback(
            object : Observable.OnPropertyChangedCallback() {
                override fun onPropertyChanged(
                    sender: Observable?,
                    propertyId: Int
                ) {
                    if (trackingNumber.get()?.orEmpty()?.isNotEmpty() == true){
                        trackingNumberError.set(null)
                    }
                }
            }
        )

        name.addOnPropertyChangedCallback(
            object : Observable.OnPropertyChangedCallback() {
                override fun onPropertyChanged(
                    sender: Observable?,
                    propertyId: Int
                ) {
                    if (name.get()?.orEmpty()?.isNotEmpty() == true){
                        nameError.set(null)
                    }
                }
            }
        )
    }

    fun createBarcodeOptions(): ScanOptions {
        val options = ScanOptions()
        options.setDesiredBarcodeFormats(ScanOptions.ALL_CODE_TYPES)
        options.setPrompt(context.getString(R.string.tracking_number_barcode_scan))
        options.setOrientationLocked(false);
        options.setCameraId(Camera.CameraInfo.CAMERA_FACING_BACK) // Use a specific camera of the device
        options.setBeepEnabled(false)
        options.setBarcodeImageEnabled(true)
        return options
    }

}