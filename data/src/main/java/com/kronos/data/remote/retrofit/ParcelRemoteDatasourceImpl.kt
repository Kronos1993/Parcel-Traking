package com.kronos.data.remote.retrofit

import android.util.Log
import com.kronos.data.data_source.parcel.ParcelRemoteDataSource
import com.kronos.data.remote.retrofit.parcel.api.ParcelApi
import com.kronos.data.remote.retrofit.parcel.dto.ParcelDto
import com.kronos.data.remote.retrofit.parcel.mapper.toParcelModel
import com.kronos.domain.model.parcel.ParcelModel
import retrofit2.Callback
import java.io.EOFException
import javax.inject.Inject

class ParcelRemoteDatasourceImpl @Inject constructor(
    private val parcelApi: ParcelApi,
) : ParcelRemoteDataSource {

    override suspend fun searchParcel(trackingNumber: String): ParcelModel {
        val result: ParcelModel =
            try{
                parcelApi.searchParcel(trackingNumber).execute().let {
                    if (it.isSuccessful) {
                        val responseBody = it.body()

                        // Non-empty response
                        responseBody?.toParcelModel(trackingNumber)
                            ?: // Empty response
                            ParcelModel(trackingNumber = trackingNumber, notes = "", status = "not found", imageUrl = "")
                    } else {
                        // Handle unsuccessful response (e.g., HTTP error)
                        ParcelModel(trackingNumber = trackingNumber, notes = "", status = "not found", imageUrl = "")
                    }
                }
            }catch (e:EOFException){
                e.printStackTrace()
                ParcelModel(trackingNumber = trackingNumber, notes = "", status = "not found", imageUrl = "", fail = "Parcel not found")
            }catch (e:Exception){
                e.printStackTrace()
                ParcelModel(trackingNumber = trackingNumber, notes = "", status = "not found", imageUrl = "", fail = e.message.orEmpty())
            }
        Log.e("ParcelRemoteDatasource", "searchParcel: $result")
        return result
    }

    override fun searchParcelAsync(trackingNumber: String, callback: Any) {
        parcelApi.searchParcel(trackingNumber).enqueue(callback as Callback<ParcelDto?>)
    }

}

