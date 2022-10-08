package com.kronos.parcel.tracking.ui.user

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.kronos.core.extensions.asLiveData
import com.kronos.core.view_model.ParentViewModel
import com.kronos.domain.model.user.UserModel
import com.kronos.domain.repository.user.UserLocalRepository
import com.kronos.parcel.tracking.MainState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class UserViewModel  @Inject constructor(
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



}