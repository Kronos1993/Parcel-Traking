package com.kronos.data.local.event.dao

import androidx.room.*
import com.kronos.data.local.event.entity.EventEntity
import com.kronos.data.local.parcel.entity.ParcelEntity

@Dao
interface EventDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(eventEntity: EventEntity)

    @Query("SELECT * FROM EVENT ORDER BY DATE_UPDATED DESC")
    suspend fun listAll(): List<EventEntity>

    @Query("SELECT * FROM EVENT WHERE PARCEL = :trackingNumber ORDER BY DATE_UPDATED DESC")
    suspend fun listAll(trackingNumber:String): List<EventEntity>

    @Query("SELECT COUNT(ID) FROM EVENT WHERE PARCEL = :trackingNumber and READ = 0")
    suspend fun countUnreadByParcel(trackingNumber:String): Int

    @Query("SELECT COUNT(ID) FROM EVENT WHERE READ = 0")
    suspend fun countUnread(): Int

    @Query("UPDATE EVENT set READ = 1")
    suspend fun setAllRead()

    @Delete
    suspend fun deleteEvent(eventEntity: EventEntity)

}