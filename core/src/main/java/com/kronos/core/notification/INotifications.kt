package com.kronos.core.notification

import android.content.Context

interface INotifications {

    fun createNotification(title:String ,  description:String, group:NotificationGroup , notificationsId:NotificationType, iconDrawable:Int, context:Context)
    fun hideNotification(notificationType: NotificationType,context:Context)

}