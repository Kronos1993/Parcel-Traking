package com.kronos.zipcargo.data.di

import com.kronos.zipcargo.data.local.LocalDatabaseFactory
import com.kronos.zipcargo.data.local.database.ApplicationDatabase
import com.kronos.zipcargo.data.local.parcel.dao.ParcelDao
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
}