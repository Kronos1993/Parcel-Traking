package com.kronos.zipcargo.domain.repository

import com.kronos.zipcargo.domain.model.parcel.ParcelModel


interface RemoteRepository {
    suspend fun searchParcel(tracking: String): ParcelModel
}
