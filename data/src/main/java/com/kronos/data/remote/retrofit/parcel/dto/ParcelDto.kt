/*
 * Copyright (c) 2022. Kronos
 * Created by Marcos Octavio Guerra Liso
 */

package com.kronos.data.remote.retrofit.parcel.dto

import com.google.gson.annotations.SerializedName

//models the object from zipcargo ws

data class ParcelDto(
    @SerializedName("Date")
    var date:String? = "",
    @SerializedName("Nelcon")
    var nelcon:String? = "",
    @SerializedName("Manager")
    var manager:String? = "",
    @SerializedName("ImageUrl")
    var imageUrl:String? = "",
    @SerializedName("PackageStatusOrder")
    var packageStatus:Int = -1,
    @SerializedName("WMessage")
    var wmessage:String? = "",
    @SerializedName("QMessage")
    var qmessage:String? = "",
    @SerializedName("Status")
    var status:String? = "",

    var tracking:String = "",
)
