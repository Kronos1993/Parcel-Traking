package com.kronos.data.di

import com.kronos.data.repository.event.LocalEventLocalRepositoryImpl
import com.kronos.data.repository.parcel.LocalParcelLocalRepositoryImpl
import com.kronos.data.repository.parcel.ParcelRemoteApplicationRepositoryImpl
import com.kronos.data.repository.user.UserLocalRepositoryImpl
import com.kronos.domain.repository.event.EventLocalRepository
import com.kronos.domain.repository.parcel.ParcelLocalRepository
import com.kronos.domain.repository.parcel.ParcelRemoteRepository
import com.kronos.domain.repository.user.UserLocalRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun provideLocalRepository(impl: LocalParcelLocalRepositoryImpl): ParcelLocalRepository

    @Binds
    abstract fun provideRemoteRepository(impl: ParcelRemoteApplicationRepositoryImpl): ParcelRemoteRepository

    @Binds
    abstract fun provideEventLocalRepository(impl: LocalEventLocalRepositoryImpl): EventLocalRepository

    @Binds
    abstract fun provideUserLocalRepository(impl: UserLocalRepositoryImpl): UserLocalRepository
}
