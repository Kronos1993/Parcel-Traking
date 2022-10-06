package com.kronos.data.local.event.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "EVENT"/*,
    indices = [
        Index(value = ["ID"], unique = true),
        Index(value = ["TRACKING_NUMBER"], unique = true)
    ]*/
)
data class EventEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID") val id: Int,
    @ColumnInfo(name = "TITLE") val name: String,
    @ColumnInfo(name = "BODY") val body: String,
    @ColumnInfo(name = "PARCEL", defaultValue = "") val parcel: String,
    @ColumnInfo(name = "READ", defaultValue = "0") val read: Boolean,
    @ColumnInfo(name = "DATE_ADDED") val dateAdded: Long,
    @ColumnInfo(name = "DATE_UPDATED") val dateUpdated: Long,
)

