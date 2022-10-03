package com.kronos.zipcargo.data.remote.retrofit

import com.kronos.zipcargo.data.data_source.RemoteDataSource
import com.kronos.zipcargo.data.remote.retrofit.parcel.api.ParcelApi
import com.kronos.zipcargo.data.remote.retrofit.parcel.mapper.toParcelModel
import com.kronos.zipcargo.domain.model.parcel.ParcelModel
import java.lang.Exception
import javax.inject.Inject

class ParcelRemoteDatasourceImpl @Inject constructor(
    private val parcelApi: ParcelApi,
) : RemoteDataSource {

    override suspend fun searchParcel(tracking: String): ParcelModel {
        var result: ParcelModel =
            try{
                parcelApi.searchParcel(tracking).execute().let {
                    if (it.isSuccessful && it.body() != null) {
                        it.body()!!.toParcelModel(tracking)
                    } else {
                        ParcelModel(trackingNumber = tracking, status = "not found", imageUrl = "")
                    }
                }
            }catch (e:Exception){
                ParcelModel(trackingNumber = tracking, status = "not found", imageUrl = "", fail = e.message.orEmpty())
            }
        return result
    }

}
