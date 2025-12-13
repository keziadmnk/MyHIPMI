package com.example.myhipmi

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.myhipmi.ui.navigation.NavGraph
import com.example.myhipmi.ui.theme.MyHIPMITheme
import com.google.firebase.messaging.FirebaseMessaging

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
        
        setContent {
            MyHIPMITheme {
                MyHipmiApp()
            }
        }
    }
}

@Composable
fun MyHipmiApp() {
    val navController = rememberNavController()
    NavGraph(navController = navController)
}
