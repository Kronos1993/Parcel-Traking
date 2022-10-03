package com.kronos.parcel.traking.ui.history

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kronos.core.extensions.asLiveData
import com.kronos.core.view_model.ParentViewModel
import com.kronos.zipcargo.domain.model.parcel.ParcelModel
import com.kronos.zipcargo.domain.repository.LocalRepository
import com.kronos.parcel.traking.ui.home.ParcelAdapter
import com.kronos.parcel.traking.ui.history.state.HistoryState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel  @Inject constructor(
    var localRepository: LocalRepository,
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
            var list = localRepository.listAllParcelHistory()
            postParcelList(list)
            setState(HistoryState.Loading(false))
        }
    }

}