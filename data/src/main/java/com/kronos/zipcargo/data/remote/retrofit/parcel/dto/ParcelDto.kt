/*
 * Copyright (c) 2022. Kronos
 * Created by Marcos Octavio Guerra Liso
 */

package com.kronos.zipcargo.data.remote.retrofit.parcel.dto

import com.google.gson.annotations.SerializedName

//models the object from zipcargo ws

data class ParcelDto(
    @SerializedName("WMessage")
    var wmessage:String = "",
    @SerializedName("QMessage")
    var qmessage:String = "",
    @SerializedName("Nelcon")
    var nelcon:String = "",
    @SerializedName("Manager")
    var status:String = "",
    @SerializedName("ImageUrl")
    var imageUrl:String = "",

    var tracking:String = "",
)
