package com.kronos.data.local.parcel.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "PARCEL"/*,
    indices = [
        Index(value = ["ID"], unique = true),
        Index(value = ["TRACKING_NUMBER"], unique = true)
    ]*/
)
data class ParcelEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID") val id: Int,
    @ColumnInfo(name = "TRACKING_NUMBER") val trackingNumber: String,
    @ColumnInfo(name = "NAME") val name: String,
    @ColumnInfo(name = "HISTORY", defaultValue = "0") val history: Boolean,
    @ColumnInfo(name = "DATE_ADDED") val dateAdded: Long,
    @ColumnInfo(name = "DATE_UPDATED") val dateUpdated: Long,
    @ColumnInfo(name = "STATUS") val status: String? = null,
    @ColumnInfo(name = "IMAGE_URL") val imageUrl: String? = null,
)

