package com.kronos.parcel.tracking.ui.user

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kronos.core.extensions.asLiveData
import com.kronos.core.view_model.ParentViewModel
import com.kronos.domain.model.statistics.StatisticsModel
import com.kronos.domain.model.user.UserModel
import com.kronos.domain.repository.parcel.ParcelLocalRepository
import com.kronos.domain.repository.statistics.StatisticsLocalRepository
import com.kronos.domain.repository.user.UserLocalRepository
import com.kronos.logger.LoggerType
import com.kronos.logger.interfaces.ILogger
import com.kronos.parcel.tracking.MainState
import com.kronos.parcel.tracking.ui.user.state.UserState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    private var userLocalRepository: UserLocalRepository,
    private var statisticsLocalRepository: StatisticsLocalRepository,
    private var parcelLocalRepository: ParcelLocalRepository,
    private var logger: ILogger,
) : ParentViewModel() {

    private val _state = MutableLiveData<MainState>()
    val state = _state.asLiveData()

    private val _user = MutableLiveData<UserModel>()
    val user = _user.asLiveData()

    val userLogged = MutableLiveData<Int>()
    val userNotLogged = MutableLiveData<Int>()

    private val _statistics = MutableLiveData<StatisticsModel>()
    val statistics = _statistics.asLiveData()

    private fun setState(state: MainState) {
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
            logger.write(this::class.java.name, LoggerType.INFO,"User deleted")
            setState(UserState.Loading(false))
            setState(UserState.UserNotLogged)
        }
    }

    fun getUserLocal() {
        //userLogged.value = View.GONE
        //userNotLogged.value = View.VISIBLE
        setState(UserState.Loading(true))
        viewModelScope.launch {
            val user = userLocalRepository.getUser()
            setState(UserState.Loading(false))
            if (user.name.isNotEmpty()){
                _user.value = user
                getStatisticsLocal()
                logger.write(this::class.java.name,LoggerType.INFO,"User ${user.name} loaded")
                setState(UserState.UserLogged)
            }
            else {
                getBoxUser("","")
            }
        }
    }

    private fun getStatisticsLocal() {
        setState(UserState.Loading(true))
        var stats = StatisticsModel()
        viewModelScope.launch{
            val call = async {
                stats = statisticsLocalRepository.get()
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)

                val allItems = parcelLocalRepository.listAll()
                val allReceivedItems = parcelLocalRepository.listAllParcelReceived()
                val itemLastMonth = parcelLocalRepository.listParcelAddedAfter(calendar.timeInMillis)

                stats.addedLastMonth = itemLastMonth.size
                stats.archived = parcelLocalRepository.listAllParcelHistory().size
                stats.inTransit = parcelLocalRepository.listAllParcelInTransit().size
                stats.received = allReceivedItems.size

                var countAll = 0.0
                allItems.forEach {
                    countAll+=it.price
                }
                var countLastMonth = 0.0
                itemLastMonth.forEach {
                    countLastMonth+=it.price
                }
                val formatter = DecimalFormat("#.##")
                stats.moneyExpended = formatter.format(countAll).toDouble()
                stats.moneyExpendedLastMonth = formatter.format(countLastMonth).toDouble()

            }
            call.await()
            _statistics.value = stats
            setState(UserState.Loading(false))
        }
    }
}

