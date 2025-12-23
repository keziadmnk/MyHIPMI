package com.example.myhipmi.ui.screen.notification

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.myhipmi.data.local.UserSessionManager
import com.example.myhipmi.data.remote.response.NotificationItem
import com.example.myhipmi.data.remote.retrofit.ApiConfig
import com.example.myhipmi.ui.components.MyHipmiTopBar
import com.example.myhipmi.ui.components.MenuDrawer
import com.example.myhipmi.ui.theme.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun NotificationScreen(navController: NavHostController) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val sessionManager = remember { UserSessionManager(context) }
    val loggedInUserId = sessionManager.getIdPengurus()
    val loggedInUserName = sessionManager.getNamaPengurus()
    val apiService = remember { ApiConfig.getApiService() }
    
    var allNotifications by remember { mutableStateOf<List<UnifiedNotificationItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isMenuVisible by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    fun loadAllNotifications() {
        isLoading = true
        scope.launch {
            try {
                val response = apiService.getNotifications()
                if (response.isSuccessful) {
                    val notifications = response.body()?.notifications ?: emptyList()
                    if (notifications.isNotEmpty()) {
                        android.util.Log.d("NotificationScreen", "First notification created_at: ${notifications[0].created_at}")
                    }
                    allNotifications = notifications.map { 
                        UnifiedNotificationItem(
                            id = it.id_notification,
                            title = it.title,
                            body = it.body,
                            created_at = it.created_at,
                            type = if (it.title.contains("Piket", ignoreCase = true)) NotificationType.PIKET else NotificationType.EVENT,
                            idAbsenPiket = null
                        )
                    }.sortedByDescending { 
                        try {
                            val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                            format.parse(it.created_at)?.time ?: 0L
                        } catch (e: Exception) {
                            0L
                        }
                    }
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
    LaunchedEffect(Unit) {
        sessionManager.setHasUnreadNotifications(false)
        loadAllNotifications()
    }
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    LaunchedEffect(currentBackStackEntry) {
        if (currentBackStackEntry?.destination?.route == "notifications") {
            sessionManager.setHasUnreadNotifications(false)
            loadAllNotifications()
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
            .background(Color.White)
    ) {
        MyHipmiTopBar(
            title = "Notifikasi",
            onBackClick = { navController.popBackStack() },
            onMenuClick = { isMenuVisible = true },
            onNotificationClick = { }
        )
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
                else -> {
                    if (allNotifications.isEmpty()) {
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
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(allNotifications) { notification ->
                                UnifiedNotificationCard(
                                    notification = notification,
                                    onClick = {
                                        if (notification.type == NotificationType.PIKET && notification.idAbsenPiket != null) {
                                            navController.navigate("piket/detail/${notification.idAbsenPiket}")
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    MenuDrawer(
        isVisible = isMenuVisible,
        onDismiss = { isMenuVisible = false },
        userName = loggedInUserName ?: "User",
        userRole = "Member",
        onProfileClick = {
            isMenuVisible = false
            navController.navigate("profile")
        },
        onAboutClick = {
            isMenuVisible = false
            navController.navigate("about")
        },
        onLogoutClick = {
            isMenuVisible = false
            sessionManager.clearSession()
            navController.navigate("login") {
                popUpTo("home") { inclusive = true }
            }
        }
    )
}
enum class NotificationType {
    EVENT,
    PIKET
}
data class UnifiedNotificationItem(
    val id: Int,
    val title: String,
    val body: String,
    val created_at: String,
    val type: NotificationType,
    val idAbsenPiket: Int? = null
)

@Composable
fun UnifiedNotificationCard(
    notification: UnifiedNotificationItem,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = false, onClick = onClick),
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
            Text(
                text = formatTime(notification.created_at),
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
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

            Text(
                text = formatTime(notification.created_at),
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}


fun formatTime(dateString: String): String {
    android.util.Log.d("NotificationScreen", "📅 formatTime input: $dateString")
    
    return try {
        val formats = listOf(
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",  // ISO 8601 dengan milliseconds dan Z
            "yyyy-MM-dd'T'HH:mm:ss'Z'",       // ISO 8601 tanpa milliseconds dengan Z
            "yyyy-MM-dd'T'HH:mm:ss.SSS",      // ISO 8601 dengan milliseconds tanpa Z
            "yyyy-MM-dd'T'HH:mm:ss",          // ISO 8601 tanpa milliseconds tanpa Z
            "yyyy-MM-dd HH:mm:ss"             // Format alternative
        )
        
        var date: Date? = null
        var usedFormat = ""
        
        for (formatPattern in formats) {
            try {
                val inputFormat = SimpleDateFormat(formatPattern, Locale.getDefault())
                inputFormat.timeZone = TimeZone.getTimeZone("Asia/Jakarta")
                date = inputFormat.parse(dateString)
                if (date != null) {
                    usedFormat = formatPattern
                    break
                }
            } catch (e: Exception) {
                continue
            }
        }
        
        if (date != null) {
            val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            outputFormat.timeZone = TimeZone.getTimeZone("Asia/Jakarta")
            val result = outputFormat.format(date)
            android.util.Log.d("NotificationScreen", "✅ formatTime output: $result (using format: $usedFormat)")
            result
        } else {
            android.util.Log.w("NotificationScreen", "⚠️ formatTime failed to parse, trying fallback")
            fallbackParseTime(dateString)
        }
    } catch (e: Exception) {
        android.util.Log.e("NotificationScreen", "❌ formatTime error: ${e.message}")
        fallbackParseTime(dateString)
    }
}
private fun fallbackParseTime(dateString: String): String {
    return try {
        val parts = dateString.split("T")
        if (parts.size == 2) {
            val timePart = parts[1].split(".")[0]
            val timeComponents = timePart.split(":")
            if (timeComponents.size >= 2) {
                val hour = timeComponents[0].padStart(2, '0')
                val minute = timeComponents[1].padStart(2, '0')
                "$hour:$minute"
            } else {
                "baru saja"
            }
        } else {
            "baru saja"
        }
    } catch (ex: Exception) {
        "baru saja"
    }
}

