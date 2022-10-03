package com.kronos.zipcargo.data.data_source

import com.kronos.zipcargo.domain.model.parcel.ParcelModel


interface LocalDataSource {
    suspend fun saveParcel(parcel: ParcelModel): ParcelModel
    suspend fun listAllParcelLocal(): List<ParcelModel>
    suspend fun listParcelHistory(): List<ParcelModel>
    suspend fun listAll(): List<ParcelModel>


}