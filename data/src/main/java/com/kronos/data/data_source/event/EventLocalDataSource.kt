package com.kronos.data.data_source.event

import com.kronos.domain.model.event.EventModel


interface EventLocalDataSource {
    suspend fun saveEvent(eventModel: EventModel): EventModel
    suspend fun deleteEvent(eventModel: EventModel): EventModel
    suspend fun listAll(): List<EventModel>
    suspend fun listAllByParcel(trackingNumber: String): List<EventModel>
    suspend fun countEventNotReadByParcel(trackingNumber: String): Int
    suspend fun countEventNotRead(): Int
    suspend fun setAllEventRead()
    suspend fun deleteEvent(trackingNumber: String)


}