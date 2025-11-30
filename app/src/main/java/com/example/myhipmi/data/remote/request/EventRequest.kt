package com.example.myhipmi.data.remote.request

import com.google.gson.annotations.SerializedName

data class EventRequest(
    @SerializedName("id_pengurus")
    val idPengurus: Int,

    @SerializedName("nama_event")
    val namaEvent: String,

    @SerializedName("tanggal")
    val tanggal: String,

    @SerializedName("waktu")
    val waktu: String,

    @SerializedName("tempat")
    val tempat: String,

    @SerializedName("dresscode")
    val dresscode: String? = null,

    @SerializedName("penyelenggara")
    val penyelenggara: String,

    @SerializedName("contact_person")
    val contactPerson: String? = null,

    @SerializedName("deskripsi")
    val deskripsi: String? = null,

    @SerializedName("poster_url")
    val posterUrl: String? = null
)