package com.kronos.parcel.tracking.ui.parcel_details.state

import java.util.*

sealed class ParcelDetailState {
    data class Loading(val loading: Boolean) : ParcelDetailState()
    data class Error(val error: Hashtable<String, String>) : ParcelDetailState()
}