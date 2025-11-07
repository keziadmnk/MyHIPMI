package com.example.myhipmi.data.remote.request

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("email_pengurus")
    val emailPengurus: String,

    @SerializedName("password")
    val password: String
)

