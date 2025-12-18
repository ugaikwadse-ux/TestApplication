package com.quicksoft.testapp.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

import com.google.gson.annotations.SerializedName

data class ApiResponse(
    @SerializedName("ERROR")
    val error: Int,

    @SerializedName("MESSAGE")
    val message: String?,

    @SerializedName("ip")
    val ip: String,

    @SerializedName("request")
    val request: RequestData
)

data class RequestData(
    @SerializedName("member_id")
    val memberId: String?,

    @SerializedName("api_password")
    val apiPassword: String?,

    @SerializedName("api_pin")
    val apiPin: String?,

    @SerializedName("number")
    val number: String?
)

