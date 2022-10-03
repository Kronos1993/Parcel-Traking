package com.kronos.zipcargo.data.repository.application

import com.kronos.zipcargo.data.data_source.RemoteDataSource
import com.kronos.zipcargo.domain.model.parcel.ParcelModel
import com.kronos.zipcargo.domain.repository.RemoteRepository
import javax.inject.Inject

class RemoteApplicationRepositoryImpl @Inject constructor(
    private val remoteLocalDataSource: RemoteDataSource
) : RemoteRepository {
    override suspend fun searchParcel(tracking: String): ParcelModel {
        return remoteLocalDataSource.searchParcel(tracking)
    }

}