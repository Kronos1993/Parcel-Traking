package com.kronos.parcel.tracking.ui.history.state

import com.kronos.parcel.tracking.MainState
import java.util.*

sealed class HistoryState : MainState() {
    data class Loading(val loading: Boolean) : HistoryState()
    data class Error(val error: Hashtable<String, String>) : HistoryState()
}