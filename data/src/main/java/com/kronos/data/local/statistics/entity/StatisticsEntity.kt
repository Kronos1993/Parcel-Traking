package com.kronos.data.local.statistics.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "STATISTIC"/*,
    indices = [
        Index(value = ["ID"], unique = true),
        Index(value = ["TRACKING_NUMBER"], unique = true)
    ]*/
)
data class StatisticsEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID") val id: Int,
    @ColumnInfo(name = "PARCEL_ADDED") val added: Int,
    @ColumnInfo(name = "PARCEL_RECEIVED") val received: Int
)

