package com.example.myhipmi.data.remote.response

import com.google.gson.annotations.SerializedName

data class RapatListResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("data")
    val data: List<AgendaRapatData>
)

data class RapatDetailResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("data")
    val data: AgendaRapatData
)

data class RapatActionResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: AgendaRapatData?
)

data class AgendaRapatData(
    @SerializedName("id_agenda")
    val idAgenda: Int,

    @SerializedName("id_pengurus")
    val idPengurus: Int,

    @SerializedName("title")
    val title: String,

    @SerializedName("creatorId")
    val creatorId: Int,

    @SerializedName("creatorName")
    val creatorName: String,

    @SerializedName("date")
    val date: String,

    @SerializedName("startAt")
    val startAt: String,

    @SerializedName("endAt")
    val endAt: String,

    @SerializedName("dateDisplay")
    val dateDisplay: String,

    @SerializedName("startTimeDisplay")
    val startTimeDisplay: String,

    @SerializedName("endTimeDisplay")
    val endTimeDisplay: String,

    @SerializedName("location")
    val location: String,

    @SerializedName("description")
    val description: String?,

    @SerializedName("isDone")
    val isDone: Boolean,

    @SerializedName("createdAt")
    val createdAt: String,

    @SerializedName("updatedAt")
    val updatedAt: String,

    @SerializedName("absensis")
    val absensis: List<AbsenRapatData>? = null
)

data class CreateRapatRequest(
    @SerializedName("id_pengurus")
    val idPengurus: Int,

    @SerializedName("title")
    val title: String,

    @SerializedName("creatorId")
    val creatorId: Int,

    @SerializedName("creatorName")
    val creatorName: String,

    @SerializedName("date")
    val date: String,

    @SerializedName("startAt")
    val startAt: String,

    @SerializedName("endAt")
    val endAt: String,

    @SerializedName("dateDisplay")
    val dateDisplay: String,

    @SerializedName("startTimeDisplay")
    val startTimeDisplay: String,

    @SerializedName("endTimeDisplay")
    val endTimeDisplay: String,

    @SerializedName("location")
    val location: String,

    @SerializedName("description")
    val description: String?
)

data class AbsenRapatResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: AbsenRapatData?
)
data class AbsenRapatData(
    @SerializedName("id_absenRapat")
    val idAbsenRapat: Int,

    @SerializedName("id_agenda")
    val idAgenda: Int,

    @SerializedName("id_pengurus")
    val idPengurus: Int,

    @SerializedName("timestamp")
    val timestamp: String,

    @SerializedName("photobuktiUrl")
    val photobuktiUrl: String?,

    @SerializedName("status")
    val status: String
)

data class CreateAbsenRequest(
    @SerializedName("id_agenda")
    val idAgenda: Int,

    @SerializedName("id_pengurus")
    val idPengurus: Int,

    @SerializedName("photobuktiUrl")
    val photobuktiUrl: String?,

    @SerializedName("status")
    val status: String = "present"
)

data class AbsenListResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("data")
    val data: List<AbsenRapatData>
)

