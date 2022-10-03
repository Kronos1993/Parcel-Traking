package com.kronos.zipcargo.data.data_source

import com.kronos.zipcargo.domain.model.parcel.ParcelModel


interface RemoteDataSource {

    suspend fun searchParcel(documentNumber: String): ParcelModel
}