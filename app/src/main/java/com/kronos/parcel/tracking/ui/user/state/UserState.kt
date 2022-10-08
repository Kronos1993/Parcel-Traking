package com.kronos.parcel.tracking.ui.user.state

import com.kronos.parcel.tracking.MainState
import java.util.*

sealed class UserState : MainState() {
    object UserNotLogged : UserState()
    object UserLogged : UserState()
    data class Loading(val loading: Boolean) : UserState()
    data class Error(val error: Hashtable<String, String>) : UserState()
}