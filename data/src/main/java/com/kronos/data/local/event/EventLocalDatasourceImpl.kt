package com.kronos.data.local.event

import com.kronos.data.data_source.event.EventLocalDataSource
import com.kronos.data.di.ApplicationDatabaseFactory
import com.kronos.data.local.LocalDatabaseFactory
import com.kronos.data.local.database.ApplicationDatabase
import com.kronos.data.local.event.mapper.toDomain
import com.kronos.data.local.event.mapper.toEntity
import com.kronos.domain.model.event.EventModel
import javax.inject.Inject

class EventLocalDatasourceImpl @Inject constructor(
    @ApplicationDatabaseFactory private val databaseFactory: LocalDatabaseFactory,
) : EventLocalDataSource {
    override suspend fun saveEvent(eventModel: EventModel): EventModel {
        try {
            val eventEntity = eventModel.toEntity()

            val internalDb = databaseFactory.loadLocalDatabase() as ApplicationDatabase
            internalDb.eventDao().insertOrUpdate(eventEntity)

        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        return eventModel
    }

    override suspend fun deleteEvent(eventModel: EventModel): EventModel {
        try {
            val eventEntity = eventModel.toEntity()

            val internalDb = databaseFactory.loadLocalDatabase() as ApplicationDatabase
            internalDb.eventDao().deleteEvent(eventEntity)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return eventModel
    }

    override suspend fun listAll(): List<EventModel> {
        var result = listOf<EventModel>()

        try {
            val internalDb = databaseFactory.loadLocalDatabase() as ApplicationDatabase
            result = internalDb.eventDao().listAll().map {
                it.toDomain()
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        return result
    }

    override suspend fun listAllByParcel(trackingNumber: String): List<EventModel> {
        var result = listOf<EventModel>()

        try {
            val internalDb = databaseFactory.loadLocalDatabase() as ApplicationDatabase
            result = internalDb.eventDao().listAll(trackingNumber).map {
                it.toDomain()
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        return result
    }

}
