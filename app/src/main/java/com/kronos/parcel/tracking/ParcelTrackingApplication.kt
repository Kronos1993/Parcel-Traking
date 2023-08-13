package com.kronos.parcel.tracking

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.app.job.JobService
import android.content.ComponentName
import android.content.Context
import android.os.Build
import android.util.Log
import com.kronos.core.extensions.formatDate
import com.kronos.core.notification.INotifications
import com.kronos.domain.repository.event.EventLocalRepository
import com.kronos.domain.repository.parcel.ParcelLocalRepository
import com.kronos.domain.repository.parcel.ParcelRemoteRepository
import com.kronos.logger.LoggerType
import com.kronos.logger.interfaces.ILogger
import com.kronos.parcel.tracking.job.ParcelTrackingNotificationJob
import com.kronos.parcel.tracking.job.notificationJobId
import dagger.hilt.android.HiltAndroidApp
import java.lang.Exception
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject


const val NOTIFICATION_CHANNEL = "PARCEL_TRACKING_NOTIFICATION_CHANNEL"
const val TAG = "ParcelTrackingApp"


@HiltAndroidApp
class ParcelTrackingApplication:Application(){

    @Inject
    lateinit var logger: ILogger

    override fun onCreate() {
        super.onCreate()
        createNotificationChanel()
        scheduleJob(applicationContext, 3600000L)
        try {
            logger.configure()
            logger.write(this::class.java.name, LoggerType.INFO,"App open on ${Date().formatDate("dd-MM-yyyy")}")
        }catch (e: Exception){
        }
    }

    private fun createNotificationChanel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL, NOTIFICATION_CHANNEL, NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.description = NOTIFICATION_CHANNEL
            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun scheduleJob(context: Context, periodic: Long) {

        val componentName = ComponentName(context, ParcelTrackingNotificationJob::class.java)
        var jobInfo: JobInfo? = null

        jobInfo = JobInfo.Builder(notificationJobId, componentName)
            .setPersisted(true)
            .setPeriodic(periodic)
            .build()

        val scheduler =
            context.getSystemService(JobService.JOB_SCHEDULER_SERVICE) as JobScheduler
        val resultCode = scheduler.schedule(jobInfo!!)
        if (resultCode == JobScheduler.RESULT_SUCCESS) {
            Log.d(TAG, "Job service schedule success $notificationJobId")
        } else {
            Log.d(TAG, "Job service schedule failed $notificationJobId")
        }
    }

}