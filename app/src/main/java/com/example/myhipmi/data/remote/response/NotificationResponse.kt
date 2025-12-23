package com.example.myhipmi.data.remote.response

import com.google.gson.annotations.SerializedName

data class NotificationResponse(
    val message: String,
    val notifications: List<NotificationItem>
)

data class NotificationItem(
    val id_notification: Int,
    val title: String,
    val body: String,
    val created_at: String
)
data class CreatePiketNotificationRequest(
    @SerializedName("id_pengurus")
    val idPengurus: Int
)

data class CreatePiketNotificationResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: NotificationItem?
)
