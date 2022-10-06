package com.kronos.domain.model.event

data class EventModel(
    val id: Int,
    val name: String,
    val body: String,
    val read: Boolean,
    val parcel: String = "",
    val dateAdded: Long,
    val dateUpdated: Long,
)
