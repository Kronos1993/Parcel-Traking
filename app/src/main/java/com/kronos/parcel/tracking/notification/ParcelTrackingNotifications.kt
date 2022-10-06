package com.kronos.parcel.tracking.notification

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.kronos.core.notification.INotifications
import com.kronos.core.notification.NotificationType
import com.kronos.parcel.tracking.MainActivity
import com.kronos.parcel.tracking.NOTIFICATION_CHANNEL
import com.kronos.parcel.tracking.ParcelTrackingApplication
import javax.inject.Inject


class ParcelTrackingNotifications @Inject constructor() : INotifications {
    override fun createNotification(
        title: String,
        description: String,
        group: String,
        notificationsId: NotificationType,
        iconDrawable: Int,
        context: Context
    ) {
        val intent = Intent(context, MainActivity::class.java)
        var pendingIntent: PendingIntent? = null

        if (notificationsId == NotificationType.GENERAL) {
            /*intent = new Intent(context, MainActivity.class);
            Bundle bundle = new Bundle();
            //bundle.putInt("go_to", R.id.nav_general_notification);
            intent.putExtras(bundle);
            intent.setAction("notificaciones");
            bundle.putBoolean("private", false);
            intent.putExtras(bundle);*/
        } else if (notificationsId == NotificationType.PARCEL_STATUS) {
            /*intent = new Intent(context, MainActivity.class);
               Bundle bundle = new Bundle();
                //bundle.putInt("go_to", R.id.nav_general_notification);
                intent.putExtras(bundle);
                intent.setAction("notificaciones");
                bundle.putBoolean("private", false);
                intent.putExtras(bundle);*/
        }

        pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_MUTABLE);

        val notificationManager: NotificationManagerCompat = NotificationManagerCompat.from(context)
        val notification: Notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL)
            .setSmallIcon(iconDrawable)
            .setContentTitle(title)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(description)
                    .setBigContentTitle(title)
            )
            .setContentText(
                description.substring(
                    0,
                    description.toCharArray().size / 2
                )
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setGroup(group)
            .setAutoCancel(true)
            //.setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(notificationsId.ordinal, notification)
    }

    override fun hideNotification(notificationType: NotificationType, context: Context) {
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.cancel(notificationType.ordinal)
    }
}