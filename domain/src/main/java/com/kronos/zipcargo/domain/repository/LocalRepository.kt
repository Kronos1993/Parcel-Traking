package com.kronos.zipcargo.domain.repository

import com.kronos.zipcargo.domain.model.parcel.ParcelModel


interface LocalRepository {
    suspend fun saveParcel(parcel: ParcelModel): ParcelModel

    suspend fun listAllParcelLocal(): List<ParcelModel>

    suspend fun listAllParcelHistory(): List<ParcelModel>

    suspend fun listAll(): List<ParcelModel>
}
