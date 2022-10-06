package com.kronos.parcel.tracking.notification.di

import com.kronos.core.notification.INotifications
import com.kronos.parcel.tracking.notification.ParcelTrackingNotifications
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class NotificationModule {

    @Binds
    abstract fun provideNotification(implLocal: ParcelTrackingNotifications): INotifications

}