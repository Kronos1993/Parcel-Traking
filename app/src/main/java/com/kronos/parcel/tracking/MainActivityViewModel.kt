package com.kronos.parcel.tracking

import android.content.Context
import android.os.Bundle
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

    private val _bundle = MutableLiveData<Bundle?>()
    val bundle = _bundle.asLiveData()

    fun setBundle(bundle: Bundle?) {
        _bundle.value = bundle
    }

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