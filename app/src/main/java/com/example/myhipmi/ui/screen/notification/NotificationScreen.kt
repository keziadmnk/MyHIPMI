package com.example.myhipmi.ui.screen.notification

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myhipmi.data.remote.response.NotificationItem
import com.example.myhipmi.data.remote.retrofit.ApiConfig
import com.example.myhipmi.ui.components.MyHipmiTopBar
import com.example.myhipmi.ui.components.MenuDrawer
import kotlinx.coroutines.launch

@Composable
fun NotificationScreen(navController: NavController) {
    var notifications by remember { mutableStateOf<List<NotificationItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isMenuVisible by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Fetch notifications saat screen dibuka
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val response = ApiConfig.getApiService().getNotifications()
                if (response.isSuccessful) {
                    notifications = response.body()?.notifications ?: emptyList()
                } else {
                    errorMessage = "Failed to load notifications"
                }
            } catch (e: Exception) {
                errorMessage = e.message ?: "Unknown error"
            } finally {
                isLoading = false
            }
        }
    }

    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            Color.White,
            Color.White
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White) // Solid white background
    ) {
        // TopBar
        MyHipmiTopBar(
            title = "Notifikasi",
            onBackClick = { navController.popBackStack() },
            onMenuClick = { isMenuVisible = true },
            onNotificationClick = { /* Already on notification screen */ }
        )

        // Content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                errorMessage != null -> {
                    Text(
                        text = "Error: $errorMessage",
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.error
                    )
                }
                notifications.isEmpty() -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Notifications,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Belum ada notifikasi",
                            color = Color.Gray,
                            fontSize = 16.sp
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(notifications) { notification ->
                            NotificationCard(notification)
                        }
                    }
                }
            }
        }
    }
    
    // Menu Drawer
    MenuDrawer(
        isVisible = isMenuVisible,
        onDismiss = { isMenuVisible = false },
        userName = "User",
        userRole = "Member",
        onProfileClick = { navController.navigate("profile") },
        onAboutClick = { navController.navigate("about") },
        onLogoutClick = { /* Implement logout */ }
    )
}

@Composable
fun NotificationCard(notification: NotificationItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon dengan background hijau
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = Color(0xFFD4E7C5),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Notifications,
                    contentDescription = null,
                    tint = Color(0xFF4A5D23),
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Konten
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = notification.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF1F1F1F)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = notification.body,
                    fontSize = 14.sp,
                    color = Color(0xFF666666)
                )
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            // Timestamp
            Text(
                text = formatTime(notification.created_at),
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}

fun formatTime(dateString: String): String {
    return try {
        // Hitung selisih waktu (contoh: "3 minutes", "2 hours", dll)
        val parts = dateString.split("T")
         if (parts.size == 2) {
            val timePart = parts[1].split(".")[0]
            val timeComponents = timePart.split(":")
            val hour = timeComponents[0]
            val minute = timeComponents[1]
            
            // Simplified - just show time
            "$hour:$minute"
        } else {
            "baru saja"
        }
    } catch (e: Exception) {
        "baru saja"
    }
}
