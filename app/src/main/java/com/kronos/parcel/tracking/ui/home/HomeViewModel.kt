package com.kronos.parcel.traking.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kronos.core.extensions.asLiveData
import com.kronos.core.view_model.ParentViewModel
import com.kronos.zipcargo.domain.model.parcel.ParcelModel
import com.kronos.zipcargo.domain.repository.LocalRepository
import com.kronos.zipcargo.domain.repository.RemoteRepository
import com.kronos.parcel.traking.ui.home.state.HomeState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    var localRepository: LocalRepository,
    var remoteRepository: RemoteRepository,
) : ParentViewModel() {
    var parcel = ParcelModel(trackingNumber = "", status = "", imageUrl = "")

    private val _parcelList = MutableLiveData<List<ParcelModel>>()
    val parcelList = _parcelList.asLiveData()

    val parcelAdapter: ParcelAdapter = ParcelAdapter()

    private val _state = MutableLiveData<HomeState>()
    val state = _state.asLiveData()

    fun setState(state: HomeState) {
        _state.value = state
    }

    fun postState(state: HomeState) {
        _state.postValue(state)
    }

    private fun postParcelList(parcelList: List<ParcelModel>) {
        _parcelList.postValue(parcelList)
    }

    fun getParcels() {
        setState(HomeState.Loading(true))
        viewModelScope.launch {
            var list = localRepository.listAllParcelLocal()
            postParcelList(list)
            setState(HomeState.Loading(false))
        }
    }

    fun toHistory(itemAt: ParcelModel) {
        itemAt.history = true
        setState(HomeState.Loading(true))
        viewModelScope.launch {
            localRepository.saveParcel(itemAt)
            getParcels()
        }
    }

    fun getNewParcel(trackingNumber: String, trackingName: String) {
        setState(HomeState.Loading(true))
        viewModelScope.launch(Dispatchers.IO) {
            val call = async {
                parcel = remoteRepository.searchParcel(trackingNumber.orEmpty())
            }
            call.await()
            parcel.name = trackingName
            val save = async {
                localRepository.saveParcel(parcel)
            }
            save.await()
            postState(HomeState.Loading(false))
            postState(HomeState.Search)
        }
    }

    fun refreshParcels() {
        viewModelScope.launch {
            setState(HomeState.Refreshing(true))
            parcelList.value.orEmpty().forEach { parcelModel ->
                refreshParcel(parcelModel)
            }
        }
    }

    fun refreshParcel(parcel: ParcelModel) {
        viewModelScope.launch(Dispatchers.IO) {
            lateinit var parcelUpdate: ParcelModel
            val call = async {
                parcelUpdate = remoteRepository.searchParcel(parcel.trackingNumber)
                parcel.status = parcelUpdate.status
                parcel.imageUrl = parcelUpdate.imageUrl
            }
            call.await()
            if (parcelUpdate.fail.isEmpty()) {
                val save = async {
                    localRepository.saveParcel(parcel)
                }
                save.await()
            } else {
                var currentError = Hashtable<String, String>()
                currentError["error"] = parcelUpdate.fail
                postState(HomeState.Error(currentError))
            }
            postState(HomeState.Refreshing(false))
        }
    }
}