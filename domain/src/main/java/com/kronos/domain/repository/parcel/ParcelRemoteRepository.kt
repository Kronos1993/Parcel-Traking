package com.kronos.domain.repository.parcel

import com.kronos.domain.model.parcel.ParcelModel


interface ParcelRemoteRepository {
    suspend fun searchParcel(tracking: String): ParcelModel

    fun searchParcelAsync(trackingNumber: String,callback:Any)
}
