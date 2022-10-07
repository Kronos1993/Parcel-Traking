package com.kronos.data.di

import com.kronos.data.local.LocalDatabaseFactory
import com.kronos.data.local.database.ApplicationDatabase
import com.kronos.data.local.event.dao.EventDao
import com.kronos.data.local.parcel.dao.ParcelDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class DaoModule {
    @Provides
    fun provideApplicationDao(@ApplicationDatabaseFactory factory: LocalDatabaseFactory): ParcelDao {
        return (factory.loadLocalDatabase() as ApplicationDatabase).parcelDao()
    }

    @Provides
    fun provideEventDao(@ApplicationDatabaseFactory factory: LocalDatabaseFactory): EventDao {
        return (factory.loadLocalDatabase() as ApplicationDatabase).eventDao()
    }
}