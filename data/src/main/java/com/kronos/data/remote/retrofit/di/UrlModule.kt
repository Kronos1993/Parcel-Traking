package com.kronos.data.remote.retrofit.di

import com.kronos.data.remote.retrofit.UrlProvider
import com.kronos.data.remote.retrofit.UrlProviderImp
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UrlModule {
    @Singleton
    @Binds
    abstract fun provideUrl(impl: UrlProviderImp): UrlProvider
}
