package com.kronos.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kronos.data.local.event.dao.EventDao
import com.kronos.data.local.event.entity.EventEntity
import com.kronos.data.local.parcel.dao.ParcelDao
import com.kronos.data.local.parcel.entity.ParcelEntity

@Database(
    entities = [ParcelEntity::class,EventEntity::class],
    version = 1,
    exportSchema = false
)
abstract class ApplicationDatabase : RoomDatabase() {
    abstract fun parcelDao(): ParcelDao
    abstract fun eventDao(): EventDao
}