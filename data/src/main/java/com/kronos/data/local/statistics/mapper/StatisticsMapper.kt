package com.kronos.data.local.statistics.mapper

import com.kronos.data.local.statistics.entity.StatisticsEntity
import com.kronos.domain.model.statistics.StatisticsModel


internal fun StatisticsEntity.toDomain(): StatisticsModel =
    StatisticsModel(
        id = id,
        added = added,
        received = received,
    )

internal fun StatisticsModel.toEntity(): StatisticsEntity =
    StatisticsEntity(
        id = id,
        added = added,
        received = received
    )

