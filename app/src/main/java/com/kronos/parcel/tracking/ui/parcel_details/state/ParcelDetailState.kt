package com.kronos.parcel.tracking.ui.parcel_details.state

import com.kronos.parcel.tracking.MainState
import java.util.*

sealed class ParcelDetailState: MainState() {
    data class Loading(val loading: Boolean) : ParcelDetailState()
    data class Error(val error: Hashtable<String, String>) : ParcelDetailState()
}