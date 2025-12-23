package com.example.myhipmi

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.myhipmi.ui.navigation.NavGraph
import com.example.myhipmi.ui.theme.MyHIPMITheme
import com.example.myhipmi.utils.KasNotificationHelper
import com.example.myhipmi.utils.PiketNotificationHelper
import com.example.myhipmi.worker.KasNotificationWorker
import com.example.myhipmi.worker.PiketNotificationWorker
import com.google.firebase.messaging.FirebaseMessaging
import java.util.Calendar
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseMessaging.getInstance().subscribeToTopic("events")
            .addOnCompleteListener { task ->
                var msg = "Subscribed to events topic"
                if (!task.isSuccessful) {
                    msg = "Failed to subscribe to events topic"
                }
                Log.d("FCM", msg)
            }
        FirebaseMessaging.getInstance().subscribeToTopic("agenda_rapat")
            .addOnCompleteListener { task ->
                var msg = "Subscribed to agenda_rapat topic"
                if (!task.isSuccessful) {
                    msg = "Failed to subscribe to agenda_rapat topic"
                }
                Log.d("FCM", msg)
            }
        FirebaseMessaging.getInstance().subscribeToTopic("kas_reminder")
            .addOnCompleteListener { task ->
                var msg = "Subscribed to kas_reminder topic"
                if (!task.isSuccessful) {
                    msg = "Failed to subscribe to kas_reminder topic"
                }
                Log.d("FCM", msg)
            }

        PiketNotificationHelper.createNotificationChannel(this)
        KasNotificationHelper.createNotificationChannel(this)
        schedulePiketNotification()
        scheduleKasNotification()
        setContent {
            MyHIPMITheme {
                MyHipmiApp()
            }
        }
    }
    
    private fun schedulePiketNotification() {
        val constraints = Constraints.Builder()
            .setRequiresCharging(false)
            .setRequiresBatteryNotLow(false)
            .build()
        val currentTime = Calendar.getInstance()
        val targetTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 35)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (before(currentTime)) {
                add(Calendar.DAY_OF_MONTH, 1)
            }
        }
        
        val initialDelayMillis = targetTime.timeInMillis - currentTime.timeInMillis
        val initialDelayMinutes = TimeUnit.MILLISECONDS.toMinutes(initialDelayMillis)
        
        Log.d("MainActivity", "Piket notification will trigger at: ${targetTime.time}")
        Log.d("MainActivity", "Initial delay: $initialDelayMinutes minutes")
        val workRequest = PeriodicWorkRequestBuilder<PiketNotificationWorker>(
            24, TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .setInitialDelay(initialDelayMinutes, TimeUnit.MINUTES)
            .build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "piket_notification_work",
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )
        
        Log.d("MainActivity", "Piket notification work scheduled for 00:35 daily")
    }

    private fun scheduleKasNotification() {
        val constraints = Constraints.Builder()
            .setRequiresCharging(false)
            .setRequiresBatteryNotLow(false)
            .build()
        val workRequest = PeriodicWorkRequestBuilder<KasNotificationWorker>(
            24, TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "kas_notification_work",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }

    private fun testPiketNotification() {
        Log.d("MainActivity", "Testing piket notification...")

        PiketNotificationHelper.showPiketNotification(this)
        Log.d("MainActivity", "Test notification sent directly")

        val testWorkRequest = OneTimeWorkRequestBuilder<PiketNotificationWorker>()
            .build()
        WorkManager.getInstance(this).enqueue(testWorkRequest)
        Log.d("MainActivity", "✅ Test worker enqueued")
    }

    private fun testKasNotification() {
        Log.d("MainActivity", "🧪 Testing kas notification...")
        KasNotificationHelper.showKasNotification(this)
        Log.d("MainActivity", "✅ Test kas notification sent directly")
        val testWorkRequest = OneTimeWorkRequestBuilder<KasNotificationWorker>()
            .build()
        WorkManager.getInstance(this).enqueue(testWorkRequest)
        Log.d("MainActivity", "✅ Test kas worker enqueued")
    }
}

@Composable
fun MyHipmiApp() {
    val navController = rememberNavController()
    NavGraph(navController = navController)
}
