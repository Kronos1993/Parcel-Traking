package com.kronos.zipcargo.data.di

import com.kronos.zipcargo.data.remote.retrofit.parcel.api.ParcelApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
class ApiModule {

    @Provides
    fun provideApplicationApi(retrofit: Retrofit): ParcelApi {
        return retrofit.create(ParcelApi::class.java)
    }
}