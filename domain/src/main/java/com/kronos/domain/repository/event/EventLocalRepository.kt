package com.kronos.domain.repository.event

import com.kronos.domain.model.event.EventModel


interface EventLocalRepository {
    suspend fun saveEvent(event:EventModel): EventModel

    suspend fun deleteEvent(event:EventModel): EventModel

    suspend fun listAll(): List<EventModel>

    suspend fun listAllByParcel(trackingNumber:String): List<EventModel>

    suspend fun countEventNotReadByParcel(trackingNumber:String): Int

    suspend fun countEventNotRead(): Int

    suspend fun setAllEventRead()
}
