package com.kronos.zipcargo.data.local

import androidx.room.RoomDatabase

interface LocalDatabaseFactory {
    fun  loadLocalDatabase(): RoomDatabase?
}