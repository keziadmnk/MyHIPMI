package com.example.myhipmi.data.remote.response

import com.google.gson.annotations.SerializedName

data class TotalKasResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String? = null,

    @SerializedName("total_kas")
    val totalKas: Double
)
