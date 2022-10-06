package com.kronos.data.repository.parcel

import com.kronos.data.data_source.parcel.ParcelRemoteDataSource
import com.kronos.domain.model.parcel.ParcelModel
import com.kronos.domain.repository.parcel.ParcelRemoteRepository
import javax.inject.Inject

class ParcelRemoteApplicationRepositoryImpl @Inject constructor(
    private val parcelRemoteLocalDataSource: ParcelRemoteDataSource
) : ParcelRemoteRepository {
    override suspend fun searchParcel(tracking: String): ParcelModel {
        return parcelRemoteLocalDataSource.searchParcel(tracking)
    }

}