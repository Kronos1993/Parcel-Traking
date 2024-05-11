package com.kronos.data.local.statistics.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kronos.data.local.statistics.entity.StatisticsEntity

@Dao
interface StatisticsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(statisticsEntity: StatisticsEntity)

    @Query("SELECT :column FROM STATISTIC")
    suspend fun get(column: String): Int

    @Query("SELECT * FROM STATISTIC")
    suspend fun get(): StatisticsEntity?

    @Query("DELETE FROM STATISTIC")
    suspend fun delete()

}