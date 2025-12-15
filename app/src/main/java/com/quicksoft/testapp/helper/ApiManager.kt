package com.quicksoft.testapp.helper

import android.content.Context
import androidx.annotation.Keep
import com.quicksoft.testapp.R
import com.quicksoft.testapp.client.RetrofitClient
import com.quicksoft.testapp.model.apidata
import com.quicksoft.testapp.service.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

@Keep
class ApiManager {

    private val apiService: ApiService =
        RetrofitClient.getClient().create(ApiService::class.java)

    interface FeedApiCallBack {
        fun onError(message: String)
        fun onSuccess(response: apidata)
    }



    fun fetchApiData(context: Context, member_id: String, api_password:String, api_pin:String, number: String, callback: FeedApiCallBack) {
        apiService.getFeed(member_id, api_password,api_pin, number).enqueue(object :
            Callback<apidata> {
            override fun onResponse(
                call: Call<apidata>,
                response: Response<apidata>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    callback.onSuccess(response.body()!!)
                } else {
                    callback.onError("Error")
                }
            }

            override fun onFailure(call: Call<apidata>, t: Throwable) {
                callback.onError(handleFailure(context, t))
            }
        })
    }



    private fun handleFailure(context: Context, throwable: Throwable): String {
        return when (throwable) {
            is SocketTimeoutException -> context.getString(R.string.zaman_asimi)
            is UnknownHostException -> context.getString(R.string.no_connections)
            is IOException -> context.getString(R.string.ag_hatasi)
            else -> context.getString(R.string.bilinmeyen_hata) + throwable.message
        }
    }
}
