package com.kronos.parcel.tracking.ui.history

import android.content.Context
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
import com.kronos.parcel.tracking.ui.home.ParcelAdapter
import com.kronos.parcel.tracking.ui.history.state.HistoryState
import com.kronos.parcel.tracking.ui.home.state.HomeState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel  @Inject constructor(
    @ApplicationContext val context: Context,
    var parcelLocalRepository: ParcelLocalRepository,
    private var eventLocalRepository: EventLocalRepository,
) : ParentViewModel() {
    var parcel = ParcelModel(trackingNumber = "", status = "", imageUrl = "")

    private val _parcelList = MutableLiveData<List<ParcelModel>>()
    val parcelList = _parcelList.asLiveData()

    val parcelAdapter: ParcelAdapter = ParcelAdapter()

    private val _state = MutableLiveData<MainState>()
    val state = _state.asLiveData()

    fun setState(state: MainState) {
        _state.value = state
    }


    private fun postParcelList(parcelList: List<ParcelModel>) {
        _parcelList.postValue(parcelList)
    }

    fun getParcels() {
        setState(HistoryState.Loading(true))
        viewModelScope.launch {
            var list = parcelLocalRepository.listAllParcelHistory()
            postParcelList(list)
            setState(HistoryState.Loading(false))
        }
    }

    fun restoreParcel(itemAt: ParcelModel) {
        itemAt.history = false
        setState(HistoryState.Loading(true))
        viewModelScope.launch {
            parcelLocalRepository.saveParcel(itemAt)
            logParcelUnarchive(itemAt)
            getParcels()
        }
    }

    private fun logParcelUnarchive(item: ParcelModel) {
        viewModelScope.launch {
            eventLocalRepository.saveEvent(
                EventModel(
                    0,
                    context.getString(R.string.parcel_updated_event).format(item.name),
                    context.getString(R.string.parcel_updated_to_unarchive_event_details).format(
                        item.name,
                        Date(item.dateUpdated).formatDate(context.getString(R.string.date_format))
                    ),
                    false,
                    item.trackingNumber,
                    Calendar.getInstance().timeInMillis,
                    Calendar.getInstance().timeInMillis,
                )
            )
            setState(MainState.NewEvent)
        }
    }

    fun deleteParcel(parcel: ParcelModel) {
        setState(HistoryState.Loading(true))
        viewModelScope.launch {
            parcelLocalRepository.deleteParcel(parcel)
            eventLocalRepository.deleteEvent(parcel.trackingNumber)
            getParcels()
            setState(HistoryState.Loading(false))
        }
    }

}