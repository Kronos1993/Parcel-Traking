package com.kronos.zipcargo.data.di

import com.kronos.zipcargo.data.repository.application.LocalLocalRepositoryImpl
import com.kronos.zipcargo.data.repository.application.RemoteApplicationRepositoryImpl
import com.kronos.zipcargo.domain.repository.LocalRepository
import com.kronos.zipcargo.domain.repository.RemoteRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun provideLocalRepository(impl: LocalLocalRepositoryImpl): LocalRepository

    @Binds
    abstract fun provideRemoteRepository(impl: RemoteApplicationRepositoryImpl): RemoteRepository
}
