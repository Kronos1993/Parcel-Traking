package com.kronos.data.remote.retrofit.parcel.mapper

import com.google.gson.Gson
import com.kronos.data.remote.retrofit.parcel.dto.ParcelDto
import com.kronos.domain.model.parcel.ParcelModel

fun String.toDto():ParcelDto = Gson().fromJson(this,ParcelDto::class.java)
fun ParcelDto.toParcelModel(trackingNumber:String): ParcelModel =
    ParcelModel(
        trackingNumber = trackingNumber,
        notes = "",
        status = status.let {
            if (status.isNullOrEmpty()){
                mapStatus(packageStatus)
            }else{
                "not found"
            }
        },
        statusDate = date.let { it ?: "" },
        imageUrl = imageUrl.let { it ?: "" }
    )

fun mapStatus(packageStatus:Int?):String{
    return if (packageStatus==null){
                "not found"
            }else{
                when(packageStatus){
                    1->{"Carga recibida en Miami"}
                    2->{"Carga entregada a agente Aereo"}
                    3->{"Carga en Proceso de Aduanas"}
                    4->{"Carga en Bodega Central"}
                    5->{"Carga disponible para entrega en sucursal"}
                    else -> {"Paquete entregado al cliente"}
                }
            }
}