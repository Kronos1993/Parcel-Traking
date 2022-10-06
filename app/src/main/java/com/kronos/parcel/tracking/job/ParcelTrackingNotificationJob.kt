package com.kronos.parcel.tracking.job

import android.app.job.JobParameters
import android.app.job.JobService
import android.util.Log
import com.kronos.core.notification.INotifications
import com.kronos.core.notification.NotificationType
import com.kronos.domain.model.parcel.ParcelModel
import com.kronos.domain.repository.parcel.ParcelLocalRepository
import com.kronos.domain.repository.parcel.ParcelRemoteRepository
import com.kronos.parcel.tracking.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

const val notificationJobId = 1

@AndroidEntryPoint
class ParcelTrackingNotificationJob : JobService() {

    private val TAG = "ParcelTrackingNotiJob"
    private var jobCancelled = false

    @Inject
    lateinit var parcelRemoteRepository: ParcelRemoteRepository

    @Inject
    lateinit var parcelLocalRepository: ParcelLocalRepository

    @Inject
    lateinit var notification: INotifications


    override fun onStartJob(params: JobParameters): Boolean {
        Log.d(TAG, "onStartJob")
        Log.d(TAG, "Current job started: ${params.jobId}")
        Log.d(TAG, "Current Job Params: ${params.jobId}")
        if (!jobCancelled) {
            doWork(params)
        }
        return true
    }

    override fun onStopJob(params: JobParameters): Boolean {
        Log.d(TAG, "onStopJob")
        Log.d(TAG, "Current job stopped: ${params.jobId}")
        jobCancelled = true
        return true
    }

    private fun doWork(params: JobParameters) {
        Log.d(TAG, "doWork")
        Log.d(TAG, "Current Do Work Params: ${params.jobId}")
        if (params != null && params.jobId == notificationJobId) {

        }
    }

    private fun refreshParcels(list: List<ParcelModel>) {
        runBlocking(Dispatchers.IO) {
            var list = parcelLocalRepository.listAllParcelLocal()
            list.forEach { parcelModel ->
                refreshParcel(parcelModel)
            }
        }

    }

    private fun refreshParcel(parcel: ParcelModel) {
        lateinit var parcelUpdate: ParcelModel
        runBlocking(Dispatchers.IO) {
            val call = async {
                parcelUpdate = parcelRemoteRepository.searchParcel(parcel.trackingNumber)
                if (parcelUpdate.status != "not found" && parcel.status != parcelUpdate.status) {
                    parcel.imageUrl = parcelUpdate.imageUrl
                    notification.createNotification(
                        getString(R.string.notification_title).format(parcel.name),
                        getString(R.string.notification_details)
                            .format(parcel.trackingNumber, parcel.status, parcelUpdate.status),
                        NotificationType.GENERAL.name,
                        NotificationType.PARCEL_STATUS,
                        com.kronos.resources.R.drawable.ic_notifications,
                        applicationContext
                    )
                    parcel.status = parcelUpdate.status
                }
                parcel.dateUpdated = parcelUpdate.dateUpdated
            }
            call.await()
            if (parcelUpdate.fail.isEmpty()) {
                val save = async {
                    parcelLocalRepository.saveParcel(parcel)
                }
                save.await()
            }
        }
    }




}





