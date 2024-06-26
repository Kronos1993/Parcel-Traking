package com.kronos.parcel.tracking.job

import android.app.job.JobParameters
import android.app.job.JobService
import android.util.Log
import com.kronos.core.extensions.formatDate
import com.kronos.core.notification.INotifications
import com.kronos.core.notification.NotificationGroup
import com.kronos.core.notification.NotificationType
import com.kronos.data.remote.retrofit.parcel.dto.ParcelDto
import com.kronos.data.remote.retrofit.parcel.mapper.toParcelModel
import com.kronos.domain.model.event.EventModel
import com.kronos.domain.model.parcel.ParcelModel
import com.kronos.domain.repository.event.EventLocalRepository
import com.kronos.domain.repository.parcel.ParcelLocalRepository
import com.kronos.domain.repository.parcel.ParcelRemoteRepository
import com.kronos.logger.LoggerType
import com.kronos.logger.interfaces.ILogger
import com.kronos.parcel.tracking.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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
        logger.write(
            this::class.java.name,
            LoggerType.INFO,
            "Current job started: ${params.jobId} on ${Date().formatDate("dd-MM-yyyy")}"
        )
        logger.write(this::class.java.name, LoggerType.INFO, "Current Job Params: ${params.jobId}")
        doWork(params)
        return true
    }

    override fun onStopJob(params: JobParameters): Boolean {
        Log.d(TAG, "onStopJob")
        Log.d(TAG, "Current job stopped: ${params.jobId}")
        logger.write(
            this::class.java.name,
            LoggerType.INFO,
            "Current job stopped: ${params.jobId} on ${Date().formatDate("dd-MM-yyyy")}"
        )
        return true
    }

    private fun doWork(params: JobParameters) {
        Log.d(TAG, "doWork")
        Log.d(TAG, "Current Do Work Params: ${params.jobId}")
        logger.write(
            this::class.java.name,
            LoggerType.INFO,
            "Current Do Work Params: ${params.jobId} on ${Date().formatDate("dd-MM-yyyy")}"
        )
        if (params.jobId == notificationJobId) {
            refreshParcels(params)
        }
    }

    private fun refreshParcels(params: JobParameters) {
        runBlocking(Dispatchers.IO) {
            logger.write(
                this::class.java.name,
                LoggerType.INFO,
                "Current Do Work Params: Refreshing parcels on ${Date().formatDate("dd-MM-yyyy")}"
            )
            val list = parcelLocalRepository.listAllParcelLocal()
            refreshParcel(list, 0, list.size, params)
        }
    }

    fun refreshParcel(parcels: List<ParcelModel>, current: Int, total: Int, params: JobParameters) {
        if (current < parcels.size) {
            val parcel = parcels[current]
            val callback = object : Callback<ParcelDto?> {
                override fun onResponse(
                    call: Call<ParcelDto?>,
                    response: Response<ParcelDto?>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        response.body().let {
                            val parcelUpdate = it!!.toParcelModel(parcel.trackingNumber)
                            parcel.dateUpdated = parcelUpdate.dateUpdated
                            if (parcelUpdate.status != "not found" && parcel.status != parcelUpdate.status) {
                                parcel.imageUrl = parcelUpdate.imageUrl
                                notification.createNotification(
                                    applicationContext.getString(R.string.notification_title)
                                        .format(parcel.name),
                                    applicationContext.getString(R.string.notification_details)
                                        .format(
                                            parcel.name,
                                            parcel.status,
                                            parcelUpdate.status
                                        ),
                                    NotificationGroup.GENERAL,
                                    NotificationType.PARCEL_STATUS,
                                    com.kronos.resources.R.drawable.ic_notifications,
                                    applicationContext
                                )
                                runBlocking(Dispatchers.IO) {
                                    val save = async {
                                        logParcelUpdate(parcel, parcelUpdate)
                                    }
                                    save.await()
                                }
                                parcel.status = parcelUpdate.status
                            }
                            logger.write(
                                this::class.java.name,
                                LoggerType.INFO,
                                "Current Do Work Params: ${parcel.name} updated on ${
                                    Date().formatDate(
                                        "dd-MM-yyyy"
                                    )
                                }"
                            )
                            if (parcelUpdate.fail.isEmpty()) {
                                runBlocking(Dispatchers.IO) {
                                    val save = async {
                                        parcelLocalRepository.saveParcel(parcel)
                                        logger.write(
                                            this::class.java.name,
                                            LoggerType.INFO,
                                            "Current Do Work Params: ${parcel.name} saved on ${
                                                Date().formatDate(
                                                    "dd-MM-yyyy"
                                                )
                                            }"
                                        )
                                    }
                                    save.await()
                                }
                            } else {
                                logger.write(
                                    this::class.java.name,
                                    LoggerType.INFO,
                                    "Current Do Work Params: error ocurred ${parcelUpdate.name} : ${parcelUpdate.fail} on ${
                                        Date().formatDate(
                                            "dd-MM-yyyy"
                                        )
                                    }"
                                )
                            }
                        }
                        val next = current + 1
                        refreshParcel(parcels, next, total, params)
                    } else {
                        val next = current + 1
                        refreshParcel(parcels, next, total, params)
                    }
                }

                override fun onFailure(call: Call<ParcelDto?>, t: Throwable) {
                    runBlocking(Dispatchers.IO) {
                        val save = async {
                            parcel.dateUpdated = Calendar.getInstance().timeInMillis
                            parcelLocalRepository.saveParcel(parcel)
                            logger.write(
                                this::class.java.name,
                                LoggerType.INFO,
                                "Current Do Work Params: ${parcel.name} saved on ${
                                    Date().formatDate(
                                        "dd-MM-yyyy"
                                    )
                                }"
                            )
                        }
                        save.await()
                    }
                    logger.write(
                        this::class.java.name,
                        LoggerType.INFO,
                        "Current Do Work Params: error ocurred ${t.message} on ${Date().formatDate("dd-MM-yyyy")}"
                    )
                    val next = current + 1
                    refreshParcel(parcels, next, total, params)
                }

            }
            parcelRemoteRepository.searchParcelAsync(parcel.trackingNumber, callback)
        } else {
            logger.write(
                this::class.java.name,
                LoggerType.INFO,
                "Current Do Work Params:finishing job on ${Date().formatDate("dd-MM-yyyy")}"
            )
            jobFinished(params, true)
        }
    }

    suspend private fun logParcelUpdate(parcel: ParcelModel, parcelUpdate: ParcelModel) {
        eventLocalRepository.saveEvent(
            EventModel(
                0,
                applicationContext.getString(R.string.parcel_updated_event).format(parcel.name),
                applicationContext.getString(R.string.notification_details)
                    .format(
                        parcel.name,
                        parcel.status,
                        parcelUpdate.status
                    ),
                false,
                parcel.trackingNumber,
                Calendar.getInstance().timeInMillis,
                Calendar.getInstance().timeInMillis,
            )
        )
    }


}






