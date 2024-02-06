package com.kronos.data.local.parcel.dao

import androidx.room.*
import com.kronos.data.local.parcel.entity.ParcelEntity

@Dao
interface ParcelDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(parcel: ParcelEntity)

    @Query("SELECT * FROM PARCEL WHERE TRACKING_NUMBER = :trackingNumber")
    suspend fun getParcelByTrackingNumber(trackingNumber: String): ParcelEntity

    @Query("SELECT * FROM PARCEL WHERE HISTORY = 0 ORDER BY DATE_ADDED DESC")
    suspend fun listParcels(): List<ParcelEntity>

    @Query("SELECT * FROM PARCEL ORDER BY DATE_ADDED DESC")
    suspend fun listAll(): List<ParcelEntity>

    @Query("SELECT * FROM PARCEL WHERE HISTORY = 1 ORDER BY DATE_ADDED DESC")
    suspend fun listHistory(): List<ParcelEntity>

    @Query("SELECT * FROM PARCEL WHERE STATUS like '%tránsito%' OR STATUS like '%Aduanas%' OR STATUS like '%Bodega%'")
    suspend fun listAllParcelInTransit(): List<ParcelEntity>

    @Query("SELECT * FROM PARCEL WHERE DATE_ADDED >= :after")
    suspend fun listParcelAddedAfter(after:Long): List<ParcelEntity>
    
    @Delete
    suspend fun deleteParcel(parcel:ParcelEntity)

}