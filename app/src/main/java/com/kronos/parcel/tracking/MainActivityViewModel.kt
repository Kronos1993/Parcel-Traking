package com.kronos.parcel.tracking

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kronos.core.extensions.asLiveData
import com.kronos.core.view_model.ParentViewModel
import com.kronos.domain.repository.event.EventLocalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    private var eventLocalRepository: EventLocalRepository,
) : ParentViewModel(){

    private val _eventCount = MutableLiveData<Int>()
    val eventCount = _eventCount.asLiveData()

    fun getEventCount() {
        viewModelScope.launch {
            _eventCount.value = eventLocalRepository.countEventNotRead()
        }
    }

    fun setAllEventRead() {
        viewModelScope.launch(Dispatchers.IO) {
            var call = async {
                eventLocalRepository.setAllEventRead()
            }
            call.await()
            getEventCount()
        }
    }


}