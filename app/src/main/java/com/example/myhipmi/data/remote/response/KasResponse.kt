package com.example.myhipmi.data.remote.response

import com.google.gson.annotations.SerializedName

data class KasResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String? = null,

    @SerializedName("data")
    val data: List<KasItem>? = null
)

data class KasDetailResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String? = null,

    @SerializedName("data")
    val data: KasItem? = null
)

data class KasItem(
    @SerializedName("id")
    val id: Int,

    @SerializedName("user_id")
    val userId: Int,

    @SerializedName("deskripsi")
    val deskripsi: String,

    @SerializedName("nominal")
    val nominal: Double,

    @SerializedName("bukti_transfer_url")
    val buktiTransferUrl: String?,

    // Update: Support "tanggal" (dari list) dan "created_at" (dari create response)
    @SerializedName(value = "tanggal", alternate = ["created_at"])
    val tanggal: String,

    @SerializedName("status")
    val status: String,

    @SerializedName("user")
    val user: UserKas? = null
)

data class UserKas(
    @SerializedName("id_pengurus")
    val idPengurus: Int,

    @SerializedName("nama_pengurus")
    val namaPengurus: String,

    @SerializedName("email_pengurus")
    val emailPengurus: String,

    @SerializedName("jabatan")
    val jabatan: String
)
