package com.example.myhipmi.data.remote.response

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
