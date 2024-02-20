package com.kronos.parcel.tracking.ui.parcel_details

import android.content.Context
import android.hardware.Camera
import androidx.databinding.Observable
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.journeyapps.barcodescanner.ScanOptions
import com.kronos.core.extensions.asLiveData
import com.kronos.core.extensions.formatDate
import com.kronos.core.view_model.ParentViewModel
import com.kronos.data.remote.retrofit.UrlProvider
import com.kronos.domain.model.event.EventModel
import com.kronos.domain.model.parcel.ParcelModel
import com.kronos.domain.repository.event.EventLocalRepository
import com.kronos.domain.repository.parcel.ParcelLocalRepository
import com.kronos.logger.LoggerType
import com.kronos.logger.interfaces.ILogger
import com.kronos.parcel.tracking.MainState
import com.kronos.parcel.tracking.R
import com.kronos.parcel.tracking.ui.notifications.EventAdapter
import com.kronos.parcel.tracking.ui.parcel_details.state.ParcelDetailState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ParcelDetailsViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    private var parcelLocalRepository: ParcelLocalRepository,
    private var eventLocalRepository: EventLocalRepository,
    private var urlProvider: UrlProvider,
    var logger:ILogger,
) : ParentViewModel() {

    private val _parcel = MutableLiveData<ParcelModel>()
    val parcel = _parcel.asLiveData()

    private val _old_tracking = MutableLiveData<String>()
    private val old_tracking = _old_tracking.asLiveData()

    private val _eventList = MutableLiveData<List<EventModel>>()
    val eventList = _eventList.asLiveData()

    var eventAdapter: WeakReference<EventAdapter> = WeakReference(EventAdapter())

    private val _state = MutableLiveData<MainState>()
    val state = _state.asLiveData()

    var trackingNumber = ObservableField<String?>()
    var trackingNumberError = ObservableField<String?>()
    var name = ObservableField<String?>()
    var nameError = ObservableField<String?>()
    var notes = ObservableField<String?>()

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

    private fun setState(state: MainState) {
        _state.value = state
    }

    fun postState(state: MainState) {
        _state.postValue(state)
    }

    fun postParcel(parcel: ParcelModel) {
        _parcel.value = parcel
        _old_tracking.value = parcel.trackingNumber
        trackingNumber.set(parcel.trackingNumber)
        name.set(parcel.name)
        notes.set(parcel.notes)
    }


    private fun postEventList(list: List<EventModel>) {
        _eventList.postValue(list)
    }

    fun getEvents() {
        postState(ParcelDetailState.Loading(true))
        viewModelScope.launch {
            val list = eventLocalRepository.listAllByParcel(parcel.value?.trackingNumber.orEmpty())
            postEventList(list)
            setState(ParcelDetailState.Loading(false))
        }
    }

    fun updateParcelFields(){
        viewModelScope.launch(Dispatchers.IO){
            parcel.value?.trackingNumber = trackingNumber.get().orEmpty()
            parcel.value?.name = name.get().orEmpty()
            parcel.value?.notes = notes.get().orEmpty()
            parcel.value?.dateUpdated = Calendar.getInstance().timeInMillis
            val call = async {
                parcelLocalRepository.saveParcel(parcel.value!!)
                eventLocalRepository.saveEvent(
                    EventModel(
                        0,
                        context.getString(R.string.parcel_updated_event).format(parcel.value?.name),
                        context.getString(R.string.parcel_data_updated_event_details).format(
                            Date(parcel.value!!.dateUpdated).formatDate(context.getString(R.string.date_format))
                        ),
                        false,
                        parcel.value!!.trackingNumber,
                        Calendar.getInstance().timeInMillis,
                        Calendar.getInstance().timeInMillis,
                    )
                )
            }
            logger.write(this::class.java.name, LoggerType.INFO,"Parcel ${parcel.value!!.trackingNumber} updated on ${Date().formatDate("dd-MM-yyyy")}")
            call.await()
            //updateEvents()
            getEvents()
            postState(MainState.NewEvent)
        }
    }

    private fun updateEvents(){
        viewModelScope.launch{
            val call = async {
                eventList.value!!.forEach {
                    it.parcel = parcel.value!!.trackingNumber
                    eventLocalRepository.saveEvent(it)
                }
            }
            call.await()
            getEvents()
        }
    }

    fun getImageUrl(parcel: ParcelModel):String{
        return urlProvider.getServerUrl() + parcel.imageUrl
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