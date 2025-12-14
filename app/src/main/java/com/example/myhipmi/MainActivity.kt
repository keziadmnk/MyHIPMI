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
import com.example.myhipmi.utils.PiketNotificationHelper
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
        
        // Setup notification channel untuk piket
        PiketNotificationHelper.createNotificationChannel(this)
        
        // Schedule periodic work untuk check piket setiap hari jam 6 pagi
        schedulePiketNotification()
        
        // UNCOMMENT BARIS INI UNTUK TEST NOTIFIKASI LANGSUNG (untuk testing)
         testPiketNotification()
        
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
}

@Composable
fun MyHipmiApp() {
    val navController = rememberNavController()
    NavGraph(navController = navController)
}
