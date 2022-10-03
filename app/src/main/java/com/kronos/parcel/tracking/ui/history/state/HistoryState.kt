package com.kronos.parcel.traking.ui.history.state

import java.util.*

sealed class HistoryState {
    data class Loading(val loading:Boolean) : HistoryState()
    data class Error(val error: Hashtable<String, String>): HistoryState()
}