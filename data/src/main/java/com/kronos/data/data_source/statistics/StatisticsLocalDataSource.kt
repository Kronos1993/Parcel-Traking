package com.kronos.data.data_source.statistics

import com.kronos.domain.model.statistics.StatisticsModel


interface StatisticsLocalDataSource {
    suspend fun saveStatistics(statisticsModel: StatisticsModel)
    suspend fun delete()
    suspend fun get(column: String)
    suspend fun get(): StatisticsModel

}