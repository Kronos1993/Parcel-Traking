package com.kronos.zipcargo.data.di

import com.kronos.zipcargo.data.data_source.LocalDataSource
import com.kronos.zipcargo.data.data_source.RemoteDataSource
import com.kronos.zipcargo.data.local.parcel.ParcelLocalDatasourceImpl
import com.kronos.zipcargo.data.remote.retrofit.ParcelRemoteDatasourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {
    @Binds
    abstract fun provideApplicationDataSource(implLocal: ParcelLocalDatasourceImpl): LocalDataSource

    @Binds
    abstract fun provideRemoteApplicationDataSource(implParcel: ParcelRemoteDatasourceImpl): RemoteDataSource
}
