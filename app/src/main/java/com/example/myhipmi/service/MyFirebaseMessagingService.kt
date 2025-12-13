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
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        
        Log.d(TAG, "📩 Message received from: ${remoteMessage.from}")
        Log.d(TAG, "📩 Data payload: ${remoteMessage.data}")
        Log.d(TAG, "📩 Notification payload: ${remoteMessage.notification}")
        
        // Get title and body from notification OR data
        val title = remoteMessage.notification?.title 
                    ?: remoteMessage.data["title"] 
                    ?: "Event Baru Ditambahkan!"
        
        val body = remoteMessage.notification?.body 
                   ?: remoteMessage.data["body"] 
                   ?: "Ada event baru"
        
        Log.d(TAG, "🔔 Preparing notification - Title: $title, Body: $body")
        
        // ALWAYS show notification regardless of app state
        showNotification(title, body)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Refreshed token: $token")
        
        // Kirim token ke server jika diperlukan
        // sendRegistrationToServer(token)
    }

    private fun showNotification(title: String, message: String) {
        Log.d(TAG, "🔔 showNotification called with title: $title, message: $message")
        
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = System.currentTimeMillis().toInt()

        // Buat Channel untuk Android O ke atas
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Event Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifikasi untuk event baru"
                enableVibration(true)
                enableLights(true)
            }
            notificationManager.createNotificationChannel(channel)
            Log.d(TAG, "✅ Notification channel created")
        }

        // Intent untuk membuka aplikasi ketika notifikasi diklik
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Buat notifikasi
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Use system icon to ensure it works
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_SOUND) // Only sound, no flash
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()

        notificationManager.notify(notificationId, notification)
        Log.d(TAG, "🚀 Notification posted with ID: $notificationId")
    }

    companion object {
        private const val TAG = "FCMService"
        private const val CHANNEL_ID = "event_notifications"
    }
}
