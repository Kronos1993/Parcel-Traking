package com.kronos.parcel.tracking.ui.parcel_details

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kronos.core.extensions.asLiveData
import com.kronos.core.view_model.ParentViewModel
import com.kronos.domain.model.event.EventModel
import com.kronos.domain.model.parcel.ParcelModel
import com.kronos.domain.repository.event.EventLocalRepository
import com.kronos.parcel.tracking.ui.notifications.EventAdapter
import com.kronos.parcel.tracking.ui.parcel_details.state.ParcelDetailState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ParcelDetailsViewModel @Inject constructor(
    private var eventLocalRepository: EventLocalRepository,
) : ParentViewModel() {

    private val _parcel = MutableLiveData<ParcelModel>()
    val parcel = _parcel.asLiveData()

    private val _eventList = MutableLiveData<List<EventModel>>()
    val eventList = _eventList.asLiveData()

    val eventAdapter: EventAdapter = EventAdapter()

    private val _state = MutableLiveData<ParcelDetailState>()
    val state = _state.asLiveData()

    fun setState(state: ParcelDetailState) {
        _state.value = state
    }

    fun postState(state: ParcelDetailState) {
        _state.postValue(state)
    }

    fun postParcel(parcel: ParcelModel) {
        _parcel.value = parcel
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


}