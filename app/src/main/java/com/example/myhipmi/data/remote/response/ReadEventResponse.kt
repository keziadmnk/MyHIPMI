package com.example.myhipmi.data.remote.response

import com.google.gson.annotations.SerializedName

data class ReadEventResponse(
    @SerializedName("message")
    val message: String,

    @SerializedName("events")
    val events: List<EventItemResponse>? = null
)

data class EventItemResponse(
    @SerializedName("id_event")
    val idEvent: Int,

    @SerializedName("nama_event")
    val namaEvent: String,

    @SerializedName("tanggal")
    val tanggal: String, // YYYY-MM-DD

    @SerializedName("waktu")
    val waktu: String, // HH:MM:SS

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
    val posterUrl: String? = null,

    @SerializedName("Creator")
    val creator: EventCreator? = null,

    @SerializedName("Bidang")
    val bidang: EventBidang? = null
)

data class EventCreator(
    @SerializedName("id_pengurus")
    val idPengurus: Int,
    @SerializedName("nama_pengurus")
    val namaPengurus: String
)

data class EventBidang(
    @SerializedName("id_bidang")
    val idBidang: Int,
    @SerializedName("nama_bidang")
    val namaBidang: String
)