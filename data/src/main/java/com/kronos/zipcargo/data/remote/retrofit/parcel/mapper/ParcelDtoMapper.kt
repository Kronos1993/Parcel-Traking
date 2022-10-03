package com.kronos.zipcargo.data.remote.retrofit.parcel.mapper

import com.kronos.zipcargo.data.remote.retrofit.parcel.dto.ParcelDto
import com.kronos.zipcargo.domain.model.parcel.ParcelModel

internal fun ParcelDto.toParcelModel(trackingNumber:String): ParcelModel =
    ParcelModel(
        trackingNumber = trackingNumber,
        status = status,
        imageUrl = imageUrl
    )
