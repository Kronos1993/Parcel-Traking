package com.kronos.data.di

import com.kronos.data.local.LocalDatabaseFactory
import com.kronos.data.local.database.ApplicationDatabase
import com.kronos.data.local.event.dao.EventDao
import com.kronos.data.local.parcel.dao.ParcelDao
import com.kronos.data.local.statistics.dao.StatisticsDao
import com.kronos.data.local.user.dao.UserDao
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

    @Provides
    fun provideUserDao(@ApplicationDatabaseFactory factory: LocalDatabaseFactory): UserDao {
        return (factory.loadLocalDatabase() as ApplicationDatabase).userDao()
    }

    @Provides
    fun provideStatisticsDao(@ApplicationDatabaseFactory factory: LocalDatabaseFactory): StatisticsDao {
        return (factory.loadLocalDatabase() as ApplicationDatabase).statisticsDao()
    }
}