package com.kronos.domain.repository.statistics

import com.kronos.domain.model.statistics.StatisticsModel


interface StatisticsLocalRepository {
    suspend fun saveStatistics(statisticsModel: StatisticsModel)
    suspend fun delete()
    suspend fun get(column: String)
    suspend fun get(): StatisticsModel
}
