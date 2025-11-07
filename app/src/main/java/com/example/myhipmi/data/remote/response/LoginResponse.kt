package com.example.myhipmi.data.remote.response

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("message")
    val message: String? = null,

    @SerializedName("token")
    val token: String? = null,

    @SerializedName("user")
    val user: UserData? = null
)

data class UserData(
    @SerializedName("id_pengurus")
    val idPengurus: Int? = null,

    @SerializedName("nama_pengurus")
    val namaPengurus: String? = null,

    @SerializedName("email_pengurus")
    val emailPengurus: String? = null
)

