package com.example.myhipmi.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.myhipmi.MainActivity
import com.example.myhipmi.R
import com.example.myhipmi.utils.KasNotificationHelper
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.d(TAG, "📩 Message received from: ${remoteMessage.from}")
        Log.d(TAG, "📩 Data payload: ${remoteMessage.data}")
        Log.d(TAG, "📩 Notification payload: ${remoteMessage.notification}")

        if (remoteMessage.data.isNotEmpty()) {
            val type = remoteMessage.data["type"]
            val title = remoteMessage.data["title"] ?: "Notifikasi Baru"
            val body = remoteMessage.data["body"] ?: "Ada pesan baru untuk Anda"

            if (type == "kas_reminder") {
                Log.d(TAG, "💰 Handling Kas Reminder Notification")
                KasNotificationHelper.showKasReminderNotification(this, title, body)
                return
            }
        }

        remoteMessage.notification?.let {
            val title = it.title ?: "Notifikasi Baru!"
            val body = it.body ?: "Ada notifikasi baru untuk Anda"
            Log.d(TAG, "🔔 Showing standard notification - Title: $title, Body: $body")
            showNotification(title, body)
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Refreshed token: $token")

    }

    private fun showNotification(title: String, message: String) {
        Log.d(TAG, "🔔 showNotification called with title: $title, message: $message")

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = System.currentTimeMillis().toInt()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "MyHIPMI Important Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifikasi penting untuk event dan agenda rapat"
                enableVibration(true)
                enableLights(true)
            }
            notificationManager.createNotificationChannel(channel)
            Log.d(TAG, "✅ Notification channel created: $CHANNEL_ID")
        }

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()

        notificationManager.notify(notificationId, notification)
        Log.d(TAG, "🚀 Notification posted with ID: $notificationId")
    }

    companion object {
        private const val TAG = "FCMService"
        private const val CHANNEL_ID = "myhipmi_important_notifications"
    }
}