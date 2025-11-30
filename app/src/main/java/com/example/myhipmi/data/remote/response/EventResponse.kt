package com.example.myhipmi.data.remote.response

import com.google.gson.annotations.SerializedName

data class EventResponse(
    @SerializedName("message")
    val message: String,

    @SerializedName("event")
    val event: EventData? = null
)

data class EventData(
    @SerializedName("id_event")
    val idEvent: Int,

    @SerializedName("nama_event")
    val namaEvent: String,

    @SerializedName("created_at")
    val createdAt: String,
    // ... tambahkan field lain yang diperlukan
)