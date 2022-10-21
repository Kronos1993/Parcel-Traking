package com.kronos.parcel.tracking.ui.user

import android.content.Context
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kronos.core.extensions.asLiveData
import com.kronos.core.view_model.ParentViewModel
import com.kronos.domain.model.statistics.StatisticsModel
import com.kronos.domain.model.user.UserModel
import com.kronos.domain.repository.parcel.ParcelLocalRepository
import com.kronos.domain.repository.statistics.StatisticsLocalRepository
import com.kronos.domain.repository.user.UserLocalRepository
import com.kronos.parcel.tracking.MainState
import com.kronos.parcel.tracking.ui.user.state.UserState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.*
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    private var userLocalRepository: UserLocalRepository,
    private var statisticsLocalRepository: StatisticsLocalRepository,
    private var parcelLocalRepository: ParcelLocalRepository,
) : ParentViewModel() {

    private val _state = MutableLiveData<MainState>()
    val state = _state.asLiveData()

    private val _user = MutableLiveData<UserModel>()
    val user = _user.asLiveData()

    val userLogged = MutableLiveData<Int>()
    val userNotLogged = MutableLiveData<Int>()

    private val _statistics = MutableLiveData<StatisticsModel>()
    val statistics = _statistics.asLiveData()

    fun setState(state: MainState) {
        _state.value = state
    }

    fun postState(state: MainState) {
        _state.postValue(state)
    }

    fun getBoxUser(username: String, password: String) {
        saveUser(UserModel("Marcos Octavio","Guerra Liso","+50760351870","mg@gmail.com","1970 82nd Ave, Doral, Miami, Florida"))
    }

    fun saveUser(user: UserModel) {
        setState(UserState.Loading(true))
        viewModelScope.launch {
            val call = async {
                userLocalRepository.saveUser(user)
            }
            call.await()
            getUserLocal()
        }
    }

    fun deleteUser() {
        setState(UserState.Loading(true))
        viewModelScope.launch {
            userLocalRepository.deleteUser()
            statisticsLocalRepository.delete()
            setState(UserState.Loading(false))
            setState(UserState.UserNotLogged)
        }
    }

    fun getUserLocal() {
        userLogged.value = View.GONE
        userNotLogged.value = View.VISIBLE
        setState(UserState.Loading(true))
        viewModelScope.launch {
            var user = userLocalRepository.getUser()
            setState(UserState.Loading(false))
            if (user!=null && user.name.isNotEmpty()){
                _user.value = user
                getStatisticsLocal()
                setState(UserState.UserLogged)
            }
            else {
                setState(UserState.UserNotLogged)
            }
        }
    }

    private fun getStatisticsLocal() {
        setState(UserState.Loading(true))
        var stats = StatisticsModel()
        viewModelScope.launch{
            var call = async {
                stats = statisticsLocalRepository.get()
                var calendar = Calendar.getInstance()
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                stats.addedLastMonth = parcelLocalRepository.listParcelAddedAfter(calendar.timeInMillis).size
                stats.archived = parcelLocalRepository.listAllParcelHistory().size
                stats.inTransit = parcelLocalRepository.listAllParcelInTransit().size
            }
            call.await()
            _statistics.value = stats
            setState(UserState.Loading(false))
        }
    }
}

