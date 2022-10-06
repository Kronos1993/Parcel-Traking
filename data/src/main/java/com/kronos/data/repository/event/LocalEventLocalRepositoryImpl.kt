package com.kronos.data.repository.event

import com.kronos.data.data_source.event.EventLocalDataSource
import com.kronos.domain.model.event.EventModel
import com.kronos.domain.model.parcel.ParcelModel
import com.kronos.domain.repository.event.EventLocalRepository
import javax.inject.Inject

class LocalEventLocalRepositoryImpl @Inject constructor(
    private val eventLocalDataSource: EventLocalDataSource
) : EventLocalRepository {
    override suspend fun saveEvent(event: EventModel): EventModel {
        return eventLocalDataSource.saveEvent(event)
    }

    override suspend fun deleteEvent(event: EventModel): EventModel {
        return eventLocalDataSource.deleteEvent(event)
    }

    override suspend fun listAll(): List<EventModel> {
        return eventLocalDataSource.listAll()
    }

    override suspend fun listAllByParcel(trackingNumber: String): List<EventModel> {
        return eventLocalDataSource.listAllByParcel(trackingNumber)
    }

    override suspend fun countEventNotReadByParcel(trackingNumber: String): Int {
        return eventLocalDataSource.countEventNotReadByParcel(trackingNumber)
    }

    override suspend fun countEventNotRead(): Int {
        return eventLocalDataSource.countEventNotRead()
    }

    override suspend fun setAllEventRead() {
        eventLocalDataSource.setAllEventRead()
    }


}