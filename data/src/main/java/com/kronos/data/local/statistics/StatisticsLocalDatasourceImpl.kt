package com.kronos.data.local.statistics

import com.kronos.data.data_source.statistics.StatisticsLocalDataSource
import com.kronos.data.di.ApplicationDatabaseFactory
import com.kronos.data.local.LocalDatabaseFactory
import com.kronos.data.local.database.ApplicationDatabase
import com.kronos.data.local.statistics.mapper.toDomain
import com.kronos.data.local.statistics.mapper.toEntity
import com.kronos.domain.model.statistics.StatisticsModel
import javax.inject.Inject

class StatisticsLocalDatasourceImpl @Inject constructor(
    @ApplicationDatabaseFactory private val databaseFactory: LocalDatabaseFactory,
) : StatisticsLocalDataSource {
    override suspend fun saveStatistics(statisticsModel: StatisticsModel) {
        try {
            val entity = statisticsModel.toEntity()

            val internalDb = databaseFactory.loadLocalDatabase() as ApplicationDatabase
            internalDb.statisticsDao().insertOrUpdate(entity)

        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    override suspend fun delete() {
        try {
            val internalDb = databaseFactory.loadLocalDatabase() as ApplicationDatabase
            internalDb.statisticsDao().delete()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    override suspend fun get(column: String) {
        TODO("Not yet implemented")
    }

    override suspend fun get(): StatisticsModel {
        var result = StatisticsModel()

        try {
            val internalDb = databaseFactory.loadLocalDatabase() as ApplicationDatabase
            result = internalDb.statisticsDao().get().toDomain()

        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        return result
    }

}
