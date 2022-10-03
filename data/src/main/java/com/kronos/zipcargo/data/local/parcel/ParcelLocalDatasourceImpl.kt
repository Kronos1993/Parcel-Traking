package com.kronos.zipcargo.data.local.parcel

import com.kronos.zipcargo.data.data_source.LocalDataSource
import com.kronos.zipcargo.data.di.ApplicationDatabaseFactory
import com.kronos.zipcargo.data.local.LocalDatabaseFactory
import com.kronos.zipcargo.data.local.database.ApplicationDatabase
import com.kronos.zipcargo.data.local.parcel.mapper.toDomain
import com.kronos.zipcargo.data.local.parcel.mapper.toEntity
import com.kronos.zipcargo.domain.model.parcel.ParcelModel
import javax.inject.Inject

class ParcelLocalDatasourceImpl @Inject constructor(
    @ApplicationDatabaseFactory private val databaseFactory: LocalDatabaseFactory,
) : LocalDataSource {
    override suspend fun saveParcel(parcel: ParcelModel): ParcelModel {
        var result = ParcelModel(trackingNumber = "", status = "", imageUrl = "")

        try {
            val parcelEntity = parcel.toEntity()

            val internalDb = databaseFactory.loadLocalDatabase() as ApplicationDatabase
            internalDb.parcelDao().insertOrUpdate(parcelEntity)

            result = internalDb.parcelDao()
                .getParcelByTrackingNumber(parcelEntity.trackingNumber).let{
                    it.toDomain()
                }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        return result
    }

    override suspend fun listAllParcelLocal(): List<ParcelModel> {
        var result = listOf<ParcelModel>()

        try {
            val internalDb = databaseFactory.loadLocalDatabase() as ApplicationDatabase
            result = internalDb.parcelDao().listParcels().map {
                it.toDomain()
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        return result
    }

    override suspend fun listParcelHistory(): List<ParcelModel> {
        var result = listOf<ParcelModel>()

        try {
            val internalDb = databaseFactory.loadLocalDatabase() as ApplicationDatabase
            result = internalDb.parcelDao().listHistory().map {
                it.toDomain()
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        return result
    }

    override suspend fun listAll(): List<ParcelModel> {
        var result = listOf<ParcelModel>()

        try {
            val internalDb = databaseFactory.loadLocalDatabase() as ApplicationDatabase
            result = internalDb.parcelDao().listAll().map {
                it.toDomain()
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        return result
    }

}
