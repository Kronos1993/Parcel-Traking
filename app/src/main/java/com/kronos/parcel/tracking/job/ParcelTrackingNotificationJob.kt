package com.kronos.parcel.tracking.job

import android.app.job.JobParameters
import android.app.job.JobService
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.kronos.core.notification.INotifications
import com.kronos.core.notification.NotificationType
import com.kronos.domain.model.event.EventModel
import com.kronos.domain.model.parcel.ParcelModel
import com.kronos.domain.repository.event.EventLocalRepository
import com.kronos.domain.repository.parcel.ParcelLocalRepository
import com.kronos.domain.repository.parcel.ParcelRemoteRepository
import com.kronos.logger.LoggerType
import com.kronos.logger.interfaces.ILogger
import com.kronos.parcel.tracking.R
import com.kronos.parcel.tracking.ui.home.state.HomeState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.util.*
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
    lateinit var eventLocalRepository: EventLocalRepository

    @Inject
    lateinit var notification: INotifications

    @Inject
    lateinit var logger: ILogger

    override fun onStartJob(params: JobParameters): Boolean {
        Log.d(TAG, "onStartJob")
        Log.d(TAG, "Current job started: ${params.jobId}")
        Log.d(TAG, "Current Job Params: ${params.jobId}")
        logger.write(this::javaClass.name,LoggerType.INFO,"Current job started: ${params.jobId}")
        logger.write(this::javaClass.name,LoggerType.INFO,"Current Job Params: ${params.jobId}")
        if (!jobCancelled) {
            doWork(params)
        }
        return true
    }

    override fun onStopJob(params: JobParameters): Boolean {
        Log.d(TAG, "onStopJob")
        Log.d(TAG, "Current job stopped: ${params.jobId}")
        logger.write(this::javaClass.name,LoggerType.INFO,"Current job stopped: ${params.jobId}")
        jobCancelled = true
        return true
    }

    private fun doWork(params: JobParameters) {
        Log.d(TAG, "doWork")
        Log.d(TAG, "Current Do Work Params: ${params.jobId}")
        logger.write(this::javaClass.name,LoggerType.INFO,"Current Do Work Params: ${params.jobId}")
        if (params != null && params.jobId == notificationJobId) {
            refreshParcels()
        }
    }

    private fun refreshParcels() {
        GlobalScope.launch(Dispatchers.IO,start=CoroutineStart.LAZY){
            logger.write(this::javaClass.name,LoggerType.INFO,"Current Do Work Params: Refreshing parcels")
            var list = parcelLocalRepository.listAllParcelLocal()
            refreshParcel(list,0,list.size)
        }
    }

    fun refreshParcel(parcels: List<ParcelModel>, current: Int,total:Int) {
        if (current<parcels.size){
            var parcel = parcels[current]
            runBlocking(Dispatchers.IO) {
                lateinit var parcelUpdate: ParcelModel
                val call = async {
                    logger.write(this::javaClass.name,LoggerType.INFO,"Current Do Work Params: Current parcel to refresh ${parcel.name}")
                    parcelUpdate = parcelRemoteRepository.searchParcel(parcel.trackingNumber)
                    if (parcelUpdate.status != "not found" && parcel.status != parcelUpdate.status) {
                        parcel.imageUrl = parcelUpdate.imageUrl
                        notification.createNotification(
                            applicationContext.getString(R.string.notification_title).format(parcel.name),
                            applicationContext.getString(R.string.notification_details)
                                .format(parcel.trackingNumber, parcel.status, parcelUpdate.status),
                            NotificationType.GENERAL.name,
                            NotificationType.PARCEL_STATUS,
                            com.kronos.resources.R.drawable.ic_notifications,
                            applicationContext
                        )
                        parcel.status = parcelUpdate.status
                    }
                    parcel.dateUpdated = parcelUpdate.dateUpdated
                    logger.write(this::javaClass.name,LoggerType.INFO,"Current Do Work Params: ${parcel.name} updated")
                    if (parcelUpdate.fail.isEmpty()) {
                        val save = async {
                            parcelLocalRepository.saveParcel(parcel)
                            logger.write(this::javaClass.name,LoggerType.INFO,"Current Do Work Params: ${parcel.name} saved")
                        }
                        save.await()
                    } else {
                        logger.write(this::javaClass.name,LoggerType.INFO,"Current Do Work Params: error ocurred ${parcelUpdate.fail}")
                        refreshParcel(parcels,total,total)
                    }
                }
                call.await()
                var next = current+1
                refreshParcel(parcels,next,total)
            }
        }
    }




}





