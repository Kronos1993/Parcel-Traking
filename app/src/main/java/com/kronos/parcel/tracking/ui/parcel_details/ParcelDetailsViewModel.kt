package com.kronos.parcel.tracking.ui.parcel_details

import android.content.Context
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kronos.core.extensions.asLiveData
import com.kronos.core.extensions.formatDate
import com.kronos.core.view_model.ParentViewModel
import com.kronos.domain.model.event.EventModel
import com.kronos.domain.model.parcel.ParcelModel
import com.kronos.domain.repository.event.EventLocalRepository
import com.kronos.domain.repository.parcel.ParcelLocalRepository
import com.kronos.parcel.tracking.MainState
import com.kronos.parcel.tracking.R
import com.kronos.parcel.tracking.ui.notifications.EventAdapter
import com.kronos.parcel.tracking.ui.parcel_details.state.ParcelDetailState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ParcelDetailsViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    private var parcelLocalRepository: ParcelLocalRepository,
    private var eventLocalRepository: EventLocalRepository,
) : ParentViewModel() {

    private val _parcel = MutableLiveData<ParcelModel>()
    val parcel = _parcel.asLiveData()

    private val _old_tracking = MutableLiveData<String>()
    val old_tracking = _old_tracking.asLiveData()

    private val _eventList = MutableLiveData<List<EventModel>>()
    val eventList = _eventList.asLiveData()

    val eventAdapter: EventAdapter = EventAdapter()

    private val _state = MutableLiveData<MainState>()
    val state = _state.asLiveData()

    fun setState(state: MainState) {
        _state.value = state
    }

    fun postState(state: MainState) {
        _state.postValue(state)
    }

    fun postParcel(parcel: ParcelModel) {
        _parcel.value = parcel
        _old_tracking.value = parcel.trackingNumber
    }


    private fun postEventList(list: List<EventModel>) {
        _eventList.postValue(list)
    }

    fun getEvents() {
        setState(ParcelDetailState.Loading(true))
        viewModelScope.launch {
            var list = eventLocalRepository.listAllByParcel(parcel.value?.trackingNumber.orEmpty())
            postEventList(list)
            setState(ParcelDetailState.Loading(false))
        }
    }

    fun updateParcelFields(parcelTrackingNumber:String,parcelName:String){
        viewModelScope.launch(Dispatchers.IO){
            parcel.value!!.name = parcelName
            parcel.value!!.trackingNumber = parcelTrackingNumber
            parcel.value!!.dateUpdated = Calendar.getInstance().timeInMillis
            var call = async {
                parcelLocalRepository.saveParcel(parcel.value!!)
                eventLocalRepository.saveEvent(
                    EventModel(
                        0,
                        context.getString(R.string.parcel_updated_event).format(old_tracking.value),
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
            call.await()
            updateEvents()
            postState(MainState.NewEvent)
        }
    }

    private fun updateEvents(){
        viewModelScope.launch{
            var call = async {
                eventList.value!!.forEach {
                    it.parcel = parcel.value!!.trackingNumber
                    eventLocalRepository.saveEvent(it)
                }
            }
            call.await()
            getEvents()
        }
    }


}