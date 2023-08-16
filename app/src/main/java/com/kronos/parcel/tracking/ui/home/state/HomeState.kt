package com.kronos.parcel.tracking.ui.home.state

import com.kronos.parcel.tracking.MainState
import java.util.*

sealed class HomeState : MainState() {
    object Search : HomeState()
    data class Loading(val loading: Boolean) : HomeState()
    data class Refreshing(val loading: Boolean) : HomeState()
    data class Error(val error: Hashtable<String, String>) : HomeState()
    object Idle : HomeState()
}