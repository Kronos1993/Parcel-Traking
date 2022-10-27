package com.kronos.data.remote.retrofit.parcel.mapper

import com.kronos.data.remote.retrofit.parcel.dto.ParcelDto
import com.kronos.domain.model.parcel.ParcelModel

fun ParcelDto.toParcelModel(trackingNumber:String): ParcelModel =
    ParcelModel(
        trackingNumber = trackingNumber,
        status = status,
        imageUrl = imageUrl
    )
