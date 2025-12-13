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

    @SerializedName("poster_url")
    val posterUrl: String? = null,

    @SerializedName("created_at")
    val createdAt: String? = null
)

data class EventDetailResponse(
    val message: String,
    val event: EventItemResponse?
)

