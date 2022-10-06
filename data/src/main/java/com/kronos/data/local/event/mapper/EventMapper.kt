package com.kronos.data.local.event.mapper

import com.kronos.data.local.event.entity.EventEntity
import com.kronos.data.local.parcel.entity.ParcelEntity
import com.kronos.domain.model.event.EventModel
import com.kronos.domain.model.parcel.ParcelModel


internal fun EventEntity.toDomain(): EventModel =
    EventModel(
        id = id,
        name = name,
        body = body,
        read = read,
        parcel = parcel,
        dateAdded = dateAdded,
        dateUpdated = dateUpdated
    )

internal fun EventModel.toEntity(): EventEntity =
    EventEntity(
        id = id,
        name = name,
        body = body,
        read = read,
        parcel = parcel,
        dateAdded = dateAdded,
        dateUpdated = dateUpdated
    )

