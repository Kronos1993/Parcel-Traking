package com.kronos.zipcargo.data.repository.application

import com.kronos.zipcargo.data.data_source.LocalDataSource
import com.kronos.zipcargo.domain.model.parcel.ParcelModel
import com.kronos.zipcargo.domain.repository.LocalRepository
import javax.inject.Inject

class LocalLocalRepositoryImpl @Inject constructor(
    private val localDataSource: LocalDataSource
) : LocalRepository {
    override suspend fun saveParcel(parcel: ParcelModel): ParcelModel {
        return localDataSource.saveParcel(parcel)
    }

    override suspend fun listAllParcelLocal(): List<ParcelModel> {
        return localDataSource.listAllParcelLocal()
    }

    override suspend fun listAllParcelHistory(): List<ParcelModel> {
        return localDataSource.listParcelHistory()
    }

    override suspend fun listAll(): List<ParcelModel> {
        return localDataSource.listAll()
    }


}