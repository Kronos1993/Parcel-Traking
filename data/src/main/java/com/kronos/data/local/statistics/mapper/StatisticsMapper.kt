package com.kronos.data.local.statistics.mapper

import com.kronos.data.local.event.entity.EventEntity
import com.kronos.data.local.parcel.entity.ParcelEntity
import com.kronos.data.local.statistics.entity.StatisticsEntity
import com.kronos.domain.model.event.EventModel
import com.kronos.domain.model.parcel.ParcelModel
import com.kronos.domain.model.statistics.StatisticsModel


internal fun StatisticsEntity.toDomain(): StatisticsModel =
    StatisticsModel(
        id = id,
        added = added,
        addedLastMonth = addedLastMonth,
        archived = archived,
        inTransit = inTransit,
        received = received,
    )

internal fun StatisticsModel.toEntity(): StatisticsEntity =
    StatisticsEntity(
        id = id,
        added = added,
        addedLastMonth = addedLastMonth,
        archived = archived,
        inTransit = inTransit,
        received = received
    )

