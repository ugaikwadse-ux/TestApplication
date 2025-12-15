package com.quicksoft.testapp.service


import androidx.annotation.Keep
import com.quicksoft.testapp.model.apidata
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

@Keep
interface ApiService {

    @GET("recharge")
    fun getFeed(
        @Query("member_id") member_id: String,
        @Query("api_password") api_password: String,
        @Query("api_pin") api_pin:String,
        @Query("number") number: String
    ): Call<apidata>
}
