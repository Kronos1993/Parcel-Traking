package com.kronos.data.data_source.parcel

import com.kronos.domain.model.parcel.ParcelModel


interface ParcelLocalDataSource {
    suspend fun saveParcel(parcel: ParcelModel): ParcelModel
    suspend fun deleteParcel(parcel: ParcelModel): ParcelModel
    suspend fun listAllParcelLocal(): List<ParcelModel>
    suspend fun listParcelHistory(): List<ParcelModel>
    suspend fun listParcelAddedAfter(after:Long): List<ParcelModel>
    suspend fun listAll(): List<ParcelModel>
    suspend fun listAllParcelInTransit(): List<ParcelModel>
    suspend fun listAllParcelReceived(): List<ParcelModel>

}