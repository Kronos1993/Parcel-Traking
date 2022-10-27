package com.kronos.data.data_source.parcel

import com.kronos.domain.model.parcel.ParcelModel


interface ParcelRemoteDataSource {
    suspend fun searchParcel(trackingNumber: String): ParcelModel
    fun searchParcelAsync(trackingNumber: String,callback:Any)
}