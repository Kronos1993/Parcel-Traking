package com.kronos.data.data_source.parcel

import com.kronos.domain.model.parcel.ParcelModel


interface ParcelRemoteDataSource {

    suspend fun searchParcel(documentNumber: String): ParcelModel
}