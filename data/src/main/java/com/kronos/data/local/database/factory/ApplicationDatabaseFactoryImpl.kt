package com.kronos.data.local.database.factory

import android.content.Context
import com.kronos.core.io.PersistenceOptions
import com.kronos.core.persistance.di.InternalDbPersistenceOptions
import com.kronos.data.local.LocalDatabaseFactory
import com.kronos.data.local.database.ApplicationDatabase
import com.kronos.data.local.extensions.RoomDatabaseLoader
import com.kronos.data.local.extensions.createLocalDatabase
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ApplicationDatabaseFactoryImpl @Inject constructor(
    @ApplicationContext private val appContext: Context,
    @InternalDbPersistenceOptions private val persistenceOptions: PersistenceOptions
) :
    LocalDatabaseFactory {

    var localDatabase: ApplicationDatabase? = null

    override fun loadLocalDatabase(): ApplicationDatabase? {
        return if (localDatabase != null && localDatabase!!.isOpen) {
            localDatabase
        } else {
            localDatabase = RoomDatabaseLoader.createLocalDatabase(
                appContext,
                persistenceOptions,
                "box.db"
            )
            localDatabase
        }
    }
}