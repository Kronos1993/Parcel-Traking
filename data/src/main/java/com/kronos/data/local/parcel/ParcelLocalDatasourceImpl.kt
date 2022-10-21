package com.kronos.data.local.parcel

import com.kronos.data.data_source.parcel.ParcelLocalDataSource
import com.kronos.data.di.ApplicationDatabaseFactory
import com.kronos.data.local.LocalDatabaseFactory
import com.kronos.data.local.database.ApplicationDatabase
import com.kronos.data.local.parcel.mapper.toDomain
import com.kronos.data.local.parcel.mapper.toEntity
import com.kronos.domain.model.parcel.ParcelModel
import javax.inject.Inject

class ParcelLocalDatasourceImpl @Inject constructor(
    @ApplicationDatabaseFactory private val databaseFactory: LocalDatabaseFactory,
) : ParcelLocalDataSource {
    override suspend fun saveParcel(parcel: ParcelModel): ParcelModel {
        var result = ParcelModel(trackingNumber = "", status = "", imageUrl = "")

        try {
            val parcelEntity = parcel.toEntity()

            val internalDb = databaseFactory.loadLocalDatabase() as ApplicationDatabase
            internalDb.parcelDao().insertOrUpdate(parcelEntity)

            result = internalDb.parcelDao()
                .getParcelByTrackingNumber(parcelEntity.trackingNumber).let {
                    it.toDomain()
                }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        return result
    }

    override suspend fun deleteParcel(parcel: ParcelModel): ParcelModel {
        try {
            val parcelEntity = parcel.toEntity()

            val internalDb = databaseFactory.loadLocalDatabase() as ApplicationDatabase
            internalDb.parcelDao().deleteParcel(parcelEntity)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return parcel
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

    override suspend fun listParcelAddedAfter(after: Long): List<ParcelModel> {
        var result = listOf<ParcelModel>()

        try {
            val internalDb = databaseFactory.loadLocalDatabase() as ApplicationDatabase
            result = internalDb.parcelDao().listParcelAddedAfter(after).map {
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

    override suspend fun listAllParcelInTransit(): List<ParcelModel> {
        var result = listOf<ParcelModel>()

        try {
            val internalDb = databaseFactory.loadLocalDatabase() as ApplicationDatabase
            result = internalDb.parcelDao().listAllParcelInTransit().map {
                it.toDomain()
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        return result
    }

}
