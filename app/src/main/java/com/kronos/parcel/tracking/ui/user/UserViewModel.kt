package com.kronos.parcel.tracking.ui.user

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kronos.core.extensions.asLiveData
import com.kronos.core.view_model.ParentViewModel
import com.kronos.domain.model.user.UserModel
import com.kronos.domain.repository.user.UserLocalRepository
import com.kronos.parcel.tracking.MainState
import com.kronos.parcel.tracking.ui.user.state.UserState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    private var userLocalRepository: UserLocalRepository,
) : ParentViewModel() {

    private val _state = MutableLiveData<MainState>()
    val state = _state.asLiveData()

    private val _user = MutableLiveData<UserModel>()
    val user = _user.asLiveData()

    fun setState(state: MainState) {
        _state.value = state
    }

    fun postState(state: MainState) {
        _state.postValue(state)
    }

    fun getBoxUser(username: String, password: String) {

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

    fun deleteUser(user: UserModel) {
        setState(UserState.Loading(true))
        viewModelScope.launch {
            userLocalRepository.deleteUser()
            setState(UserState.Loading(false))
            setState(UserState.UserNotLogged)
        }
    }

    fun getUserLocal() {
        setState(UserState.Loading(true))
        viewModelScope.launch {
            var user = userLocalRepository.getUser()
            _user.value = user
            setState(UserState.Loading(false))
            if (user.isLogged())
                setState(UserState.UserLogged)
            else{
                getBoxUser("","")
                setState(UserState.UserNotLogged)
            }
        }
    }


}