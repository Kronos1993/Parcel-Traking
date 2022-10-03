package com.kronos.zipcargo.data.local.parcel.mapper

import com.kronos.zipcargo.data.local.parcel.entity.ParcelEntity
import com.kronos.zipcargo.data.remote.retrofit.UrlConstants
import com.kronos.zipcargo.domain.model.parcel.ParcelModel


internal fun ParcelEntity.toDomain(): ParcelModel =
    ParcelModel(
        id = id,
        trackingNumber = trackingNumber,
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
        status = status.orEmpty(),
        imageUrl = imageUrl.orEmpty(),
        name = name.orEmpty(),
        history = history,
        dateAdded = dateAdded,
        dateUpdated = dateUpdated
    )
