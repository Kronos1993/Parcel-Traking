package com.kronos.data.remote.retrofit

import android.util.Log
import com.kronos.data.data_source.parcel.ParcelRemoteDataSource
import com.kronos.data.remote.retrofit.parcel.api.ParcelApi
import com.kronos.data.remote.retrofit.parcel.dto.ParcelDto
import com.kronos.data.remote.retrofit.parcel.mapper.toParcelModel
import com.kronos.domain.model.parcel.ParcelModel
import retrofit2.Callback
import java.io.EOFException
import java.lang.Exception
import javax.inject.Inject

class ParcelRemoteDatasourceImpl @Inject constructor(
    private val parcelApi: ParcelApi,
) : ParcelRemoteDataSource {

    override suspend fun searchParcel(tracking: String): ParcelModel {
        var result: ParcelModel =
            try{
                parcelApi.searchParcel(tracking).execute().let {
                    if (it.isSuccessful && it.body() != null) {
                        it.body()!!.toParcelModel(tracking)
                    } else {
                        ParcelModel(trackingNumber = tracking, notes = "", status = "not found", imageUrl = "")
                    }
                }
            }catch (e:EOFException){
                e.printStackTrace()
                ParcelModel(trackingNumber = tracking, notes = "", status = "not found", imageUrl = "")
            }catch (e:Exception){
                e.printStackTrace()
                ParcelModel(trackingNumber = tracking, notes = "", status = "not found", imageUrl = "", fail = e.message.orEmpty())
            }
        Log.e("ParcelRemoteDatasource", "searchParcel: $result")
        return result
    }

    override fun searchParcelAsync(tracking: String, callback: Any) {
        parcelApi.searchParcel(tracking).enqueue(callback as Callback<ParcelDto?>)
    }

}
