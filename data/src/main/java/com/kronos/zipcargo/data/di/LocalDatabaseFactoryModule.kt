package com.kronos.zipcargo.data.di

import com.kronos.zipcargo.data.local.LocalDatabaseFactory
import com.kronos.zipcargo.data.local.database.factory.ApplicationDatabaseFactoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton


@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ApplicationDatabaseFactory

@Module
@InstallIn(SingletonComponent::class)
abstract class LocalDatabaseFactoryModule {

    @ApplicationDatabaseFactory
    @Singleton
    @Binds
    abstract fun provideApplicationDatabaseFactory(impl: ApplicationDatabaseFactoryImpl): LocalDatabaseFactory
}