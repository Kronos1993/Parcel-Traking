package com.kronos.parcel.tracking.ui.home.state

import java.util.*

sealed class HomeState {
    object Search : HomeState()
    object Searching : HomeState()
    data class Loading(val loading: Boolean) : HomeState()
    data class Refreshing(val loading: Boolean) : HomeState()
    data class Error(val error: Hashtable<String, String>) : HomeState()
}