package com.kronos.data.repository.parcel

import com.kronos.data.data_source.parcel.ParcelLocalDataSource
import com.kronos.domain.model.parcel.ParcelModel
import com.kronos.domain.repository.parcel.ParcelLocalRepository
import javax.inject.Inject

class LocalParcelLocalRepositoryImpl @Inject constructor(
    private val parcelLocalDataSource: ParcelLocalDataSource
) : ParcelLocalRepository {
    override suspend fun saveParcel(parcel: ParcelModel): ParcelModel {
        return parcelLocalDataSource.saveParcel(parcel)
    }

    override suspend fun deleteParcel(parcel: ParcelModel): ParcelModel {
        return parcelLocalDataSource.deleteParcel(parcel)
    }

    override suspend fun listAllParcelLocal(): List<ParcelModel> {
        return parcelLocalDataSource.listAllParcelLocal()
    }

    override suspend fun listAllParcelHistory(): List<ParcelModel> {
        return parcelLocalDataSource.listParcelHistory()
    }

    override suspend fun listAllParcelInTransit(): List<ParcelModel> {
        return parcelLocalDataSource.listAllParcelInTransit()
    }

    override suspend fun listParcelAddedAfter(after: Long): List<ParcelModel> {
        return parcelLocalDataSource.listParcelAddedAfter(after)
    }

    override suspend fun listAll(): List<ParcelModel> {
        return parcelLocalDataSource.listAll()
    }


}