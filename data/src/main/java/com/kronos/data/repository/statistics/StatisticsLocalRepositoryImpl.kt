package com.kronos.data.repository.statistics

import com.kronos.data.data_source.statistics.StatisticsLocalDataSource
import com.kronos.domain.model.statistics.StatisticsModel
import com.kronos.domain.repository.statistics.StatisticsLocalRepository
import javax.inject.Inject

class StatisticsLocalRepositoryImpl @Inject constructor(
    private val statisticsLocalDataSource: StatisticsLocalDataSource
) : StatisticsLocalRepository {
    override suspend fun saveStatistics(staistics:StatisticsModel) {
        statisticsLocalDataSource.saveStatistics(staistics)
    }

    override suspend fun delete() {
        statisticsLocalDataSource.delete()
    }

    override suspend fun get(column: String) {
        statisticsLocalDataSource.get(column)
    }

    override suspend fun get():StatisticsModel {
        return statisticsLocalDataSource.get()
    }


}