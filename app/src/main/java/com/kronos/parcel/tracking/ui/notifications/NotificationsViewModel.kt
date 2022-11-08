package com.kronos.parcel.tracking.ui.notifications

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kronos.core.extensions.asLiveData
import com.kronos.core.view_model.ParentViewModel
import com.kronos.domain.model.event.EventModel
import com.kronos.domain.repository.event.EventLocalRepository
import com.kronos.parcel.tracking.ui.parcel_details.state.ParcelDetailState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel  @Inject constructor(
    private var eventLocalRepository: EventLocalRepository,
) : ParentViewModel() {

    private val _eventList = MutableLiveData<List<EventModel>>()
    val eventList = _eventList.asLiveData()

    var eventAdapter: EventAdapter? = EventAdapter()

    private val _state = MutableLiveData<ParcelDetailState>()
    val state = _state.asLiveData()

    fun setState(state: ParcelDetailState) {
        _state.value = state
    }


    private fun postEventList(list: List<EventModel>) {
        _eventList.postValue(list)
    }

    fun getEvents() {
        setState(ParcelDetailState.Loading(true))
        viewModelScope.launch {
            var list = eventLocalRepository.listAll()
            postEventList(list)
            setState(ParcelDetailState.Loading(false))
        }
    }

    fun deleteEvent(eventModel: EventModel) {
        setState(ParcelDetailState.Loading(true))
        viewModelScope.launch {
            eventLocalRepository.deleteEvent(eventModel)
            getEvents()
            setState(ParcelDetailState.Loading(false))
        }
    }

}