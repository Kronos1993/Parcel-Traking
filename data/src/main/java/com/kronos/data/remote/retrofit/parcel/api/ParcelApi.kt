package com.kronos.data.remote.retrofit.parcel.api

import com.kronos.data.remote.retrofit.parcel.dto.ParcelDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ParcelApi {

    @GET("/Tracking/SearchTracking")
    fun searchParcel(
        @Query("tracking") trackingNumber: String,
    ): Call<ParcelDto?>

}

