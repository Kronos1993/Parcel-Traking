package com.kronos.zipcargo.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kronos.zipcargo.data.local.parcel.dao.ParcelDao
import com.kronos.zipcargo.data.local.parcel.entity.ParcelEntity

@Database(
    entities = [ParcelEntity::class],
    version = 1,
    exportSchema = false
)
abstract class ApplicationDatabase : RoomDatabase() {
    abstract fun parcelDao(): ParcelDao
}