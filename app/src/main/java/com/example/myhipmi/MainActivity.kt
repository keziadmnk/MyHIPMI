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
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Subscribe ke topic 'events' untuk menerima notifikasi
        FirebaseMessaging.getInstance().subscribeToTopic("events")
            .addOnCompleteListener { task ->
                var msg = "Subscribed to events topic"
                if (!task.isSuccessful) {
                    msg = "Failed to subscribe to events topic"
                }
                Log.d("FCM", msg)
            }
        
        // Subscribe ke topic 'agenda_rapat' untuk menerima notifikasi agenda rapat
        FirebaseMessaging.getInstance().subscribeToTopic("agenda_rapat")
            .addOnCompleteListener { task ->
                var msg = "Subscribed to agenda_rapat topic"
                if (!task.isSuccessful) {
                    msg = "Failed to subscribe to agenda_rapat topic"
                }
                Log.d("FCM", msg)
            }

        // Subscribe ke topic 'kas_reminder' untuk menerima notifikasi reminder kas
        FirebaseMessaging.getInstance().subscribeToTopic("kas_reminder")
            .addOnCompleteListener { task ->
                var msg = "Subscribed to kas_reminder topic"
                if (!task.isSuccessful) {
                    msg = "Failed to subscribe to kas_reminder topic"
                }
                Log.d("FCM", msg)
            }
        
        // Setup notification channel untuk piket
        PiketNotificationHelper.createNotificationChannel(this)
        
        // Setup notification channel untuk kas
        KasNotificationHelper.createNotificationChannel(this)
        
        // Schedule periodic work untuk check piket setiap hari jam 6 pagi
        schedulePiketNotification()
        
        // Schedule periodic work untuk check kas (misal setiap hari cek tanggal)
        scheduleKasNotification()
        
        // UNCOMMENT BARIS INI UNTUK TEST NOTIFIKASI LANGSUNG (untuk testing)
        // testPiketNotification()
        testKasNotification() // <-- Testing notifikasi kas
        
        setContent {
            MyHIPMITheme {
                MyHipmiApp()
            }
        }
    }
    
    private fun schedulePiketNotification() {
        // Constraints: butuh network dan device tidak dalam battery saver mode
        val constraints = Constraints.Builder()
            .setRequiresCharging(false)
            .setRequiresBatteryNotLow(false)
            .build()
        
        // Periodic work request - check setiap hari jam 6 pagi
        // WorkManager akan menjalankan worker setiap 24 jam sekali
        val workRequest = PeriodicWorkRequestBuilder<PiketNotificationWorker>(
            24, TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .build()
        
        // Enqueue dengan unique name agar tidak duplicate
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "piket_notification_work",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
        
        Log.d("MainActivity", "Piket notification work scheduled")
    }

    private fun scheduleKasNotification() {
        val constraints = Constraints.Builder()
            .setRequiresCharging(false)
            .setRequiresBatteryNotLow(false)
            .build()

        // Cek setiap hari apakah sudah waktunya bayar kas
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
    
    // Fungsi untuk test notifikasi langsung (untuk debugging)
    private fun testPiketNotification() {
        Log.d("MainActivity", "🧪 Testing piket notification...")
        
        // Test 1: Langsung show notifikasi (tanpa check hari)
        PiketNotificationHelper.showPiketNotification(this)
        Log.d("MainActivity", "✅ Test notification sent directly")
        
        // Test 2: Jalankan worker sekali untuk test
        val testWorkRequest = OneTimeWorkRequestBuilder<PiketNotificationWorker>()
            .build()
        WorkManager.getInstance(this).enqueue(testWorkRequest)
        Log.d("MainActivity", "✅ Test worker enqueued")
    }

    private fun testKasNotification() {
        Log.d("MainActivity", "🧪 Testing kas notification...")
        
        // Test 1: Langsung show notifikasi
        KasNotificationHelper.showKasNotification(this)
        Log.d("MainActivity", "✅ Test kas notification sent directly")
        
        // Test 2: Jalankan worker sekali untuk test
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
