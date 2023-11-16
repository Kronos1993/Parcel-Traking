package com.kronos.data.remote.retrofit.parcel.mapper

import com.kronos.data.remote.retrofit.parcel.dto.ParcelDto
import com.kronos.domain.model.parcel.ParcelModel

fun ParcelDto.toParcelModel(trackingNumber:String): ParcelModel =
    ParcelModel(
        trackingNumber = trackingNumber,
        notes = "",
        status = status.let { it ?: "not found" },
        imageUrl = imageUrl.let { it ?: "not found" }
    )
