package com.kronos.data.local.event.dao

import androidx.room.*
import com.kronos.data.local.event.entity.EventEntity
import com.kronos.data.local.parcel.entity.ParcelEntity

@Dao
interface EventDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(eventEntity: EventEntity)

    @Query("SELECT * FROM EVENT ")
    suspend fun listAll(): List<EventEntity>

    @Query("SELECT * FROM EVENT WHERE PARCEL = :trackingNumber")
    suspend fun listAll(trackingNumber:String): List<EventEntity>

    @Delete
    suspend fun deleteEvent(eventEntity: EventEntity)

}