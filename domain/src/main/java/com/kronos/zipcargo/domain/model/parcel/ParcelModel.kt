package com.kronos.zipcargo.domain.model.parcel

import java.util.*

data class ParcelModel(
    val trackingNumber: String,
    var status: String,
    var imageUrl: String,
    var id: Int = 0,
    var name: String = "",
    var history: Boolean = false,
    val dateAdded: Long = Calendar.getInstance().timeInMillis,
    val dateUpdated: Long = Calendar.getInstance().timeInMillis,
    val fail: String = ""
)
