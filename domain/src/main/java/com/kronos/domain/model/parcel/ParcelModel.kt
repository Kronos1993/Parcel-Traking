package com.kronos.domain.model.parcel

import java.io.Serializable
import java.util.*

data class ParcelModel(
    var trackingNumber: String,
    var notes: String,
    var status: String,
    var imageUrl: String?,
    var id: Int = 0,
    var name: String = "",
    var history: Boolean = false,
    val dateAdded: Long = Calendar.getInstance().timeInMillis,
    var dateUpdated: Long = Calendar.getInstance().timeInMillis,
    var statusDate: String = "",
    val fail: String = "",
    var loading: Boolean = false,
):Serializable
