package com.kronos.data.local.parcel.mapper

import com.kronos.data.local.parcel.entity.ParcelEntity
import com.kronos.domain.model.parcel.ParcelModel


internal fun ParcelEntity.toDomain(): ParcelModel =
    ParcelModel(
        id = id,
        trackingNumber = trackingNumber,
        notes = notes,
        status = status.orEmpty(),
        imageUrl = imageUrl.orEmpty(),
        name = name.orEmpty(),
        history = history,
        dateAdded = dateAdded,
        dateUpdated = dateUpdated
    )

internal fun ParcelModel.toEntity(): ParcelEntity =
    ParcelEntity(
        id = id,
        trackingNumber = trackingNumber,
        notes = notes,
        status = status.orEmpty(),
        imageUrl = imageUrl.orEmpty(),
        name = name.orEmpty(),
        history = history,
        dateAdded = dateAdded,
        dateUpdated = dateUpdated
    )

