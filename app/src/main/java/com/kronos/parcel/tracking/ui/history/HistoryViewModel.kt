package com.kronos.parcel.tracking.ui.history

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kronos.core.extensions.asLiveData
import com.kronos.core.view_model.ParentViewModel
import com.kronos.domain.model.parcel.ParcelModel
import com.kronos.domain.repository.parcel.ParcelLocalRepository
import com.kronos.parcel.tracking.ui.home.ParcelAdapter
import com.kronos.parcel.tracking.ui.history.state.HistoryState
import com.kronos.parcel.tracking.ui.home.state.HomeState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel  @Inject constructor(
    var parcelLocalRepository: ParcelLocalRepository,
) : ParentViewModel() {
    var parcel = ParcelModel(trackingNumber = "", status = "", imageUrl = "")

    private val _parcelList = MutableLiveData<List<ParcelModel>>()
    val parcelList = _parcelList.asLiveData()

    val parcelAdapter: ParcelAdapter = ParcelAdapter()

    private val _state = MutableLiveData<HistoryState>()
    val state = _state.asLiveData()

    fun setState(state: HistoryState) {
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
            getParcels()
        }
    }

    fun deleteParcel(parcel: ParcelModel) {
        setState(HistoryState.Loading(true))
        viewModelScope.launch {
            parcelLocalRepository.deleteParcel(parcel)
            getParcels()
            setState(HistoryState.Loading(false))
        }
    }

}