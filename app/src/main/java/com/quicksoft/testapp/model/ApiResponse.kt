package com.quicksoft.testapp.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse(
    @SerialName("ERROR")
    val error: Int,

    @SerialName("MESSAGE")
    val message: String,

    @SerialName("ip")
    val ip: String,

    @SerialName("request")
    val request: RequestData
)

@Serializable
data class RequestData(
    @SerialName("member_id")
    val memberId: String,

    @SerialName("api_password")
    val apiPassword: String,

    @SerialName("api_pin")
    val apiPin: String,

    @SerialName("number")
    val number: String
)
