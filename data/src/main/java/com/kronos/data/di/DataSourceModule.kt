package com.kronos.data.di

import com.kronos.data.data_source.event.EventLocalDataSource
import com.kronos.data.data_source.parcel.ParcelLocalDataSource
import com.kronos.data.data_source.parcel.ParcelRemoteDataSource
import com.kronos.data.data_source.statistics.StatisticsLocalDataSource
import com.kronos.data.data_source.user.UserLocalDataSource
import com.kronos.data.local.event.EventLocalDatasourceImpl
import com.kronos.data.local.parcel.ParcelLocalDatasourceImpl
import com.kronos.data.local.statistics.StatisticsLocalDatasourceImpl
import com.kronos.data.local.user.UserLocalDatasourceImpl
import com.kronos.data.remote.retrofit.ParcelRemoteDatasourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {
    @Binds
    abstract fun provideApplicationDataSource(implLocal: ParcelLocalDatasourceImpl): ParcelLocalDataSource

    @Binds
    abstract fun provideRemoteApplicationDataSource(implParcel: ParcelRemoteDatasourceImpl): ParcelRemoteDataSource

    @Binds
    abstract fun provideEventLocalDataSource(implEvent: EventLocalDatasourceImpl): EventLocalDataSource

    @Binds
    abstract fun provideUserLocalDataSource(implUser: UserLocalDatasourceImpl): UserLocalDataSource

    @Binds
    abstract fun provideStatisticsLocalDataSource(implStat: StatisticsLocalDatasourceImpl): StatisticsLocalDataSource
}
