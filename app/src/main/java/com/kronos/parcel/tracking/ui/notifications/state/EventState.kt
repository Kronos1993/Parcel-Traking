package com.kronos.parcel.tracking.ui.notifications.state

import java.util.*

sealed class EventState {
    data class Loading(val loading: Boolean) : EventState()
    data class Error(val error: Hashtable<String, String>) : EventState()
}