package com.kronos.data.local.parcel.dao

import androidx.room.*
import com.kronos.data.local.parcel.entity.ParcelEntity

@Dao
interface ParcelDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(parcel: ParcelEntity)

    @Query("SELECT * FROM PARCEL WHERE TRACKING_NUMBER = :trackingNumber")
    suspend fun getParcelByTrackingNumber(trackingNumber: String): ParcelEntity

    @Query("SELECT * FROM PARCEL WHERE HISTORY = 0")
    suspend fun listParcels(): List<ParcelEntity>

    @Query("SELECT * FROM PARCEL")
    suspend fun listAll(): List<ParcelEntity>

    @Query("SELECT * FROM PARCEL WHERE HISTORY = 1")
    suspend fun listHistory(): List<ParcelEntity>

    @Query("SELECT * FROM PARCEL WHERE DATE_ADDED >= :after")
    suspend fun listParcelAddedAfter(after:Long): List<ParcelEntity>
    
    @Delete
    suspend fun deleteParcel(parcel:ParcelEntity)

}