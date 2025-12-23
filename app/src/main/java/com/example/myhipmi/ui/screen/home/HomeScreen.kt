package com.example.myhipmi.ui.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myhipmi.ui.components.BottomNavBar
import com.example.myhipmi.ui.components.MenuDrawer
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import com.example.myhipmi.data.remote.retrofit.ApiConfig
import com.example.myhipmi.utils.EventStatusHelper
import com.example.myhipmi.utils.EventStatus
import kotlinx.coroutines.launch
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import com.example.myhipmi.data.remote.response.NotificationItem
import com.example.myhipmi.data.local.UserSessionManager
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myhipmi.ui.viewmodel.HomeViewModel
import com.example.myhipmi.ui.viewmodel.TotalKasState
import androidx.compose.runtime.collectAsState
import java.text.NumberFormat

data class NavItem(val route: String, val index: Int)

val bottomBarItems = listOf(
    NavItem("home", 0),
    NavItem("kas", 1),
    NavItem("rapat", 2),
    NavItem("piket", 3),
    NavItem("event", 4)
)

@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: HomeViewModel = viewModel(),
    onNavigateToKas: () -> Unit = {},
    onNavigateToRapat: () -> Unit = {},
    onNavigateToPiket: () -> Unit = {},
    onNavigateToEvent: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToAbout: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    var isMenuVisible by remember { mutableStateOf(false) }
    var activeEventCount by remember { mutableStateOf(0) }
    var activeRapatCount by remember { mutableStateOf(0) }
    var recentNotifications by remember { mutableStateOf<List<NotificationItem>>(emptyList()) }
    var refreshTrigger by remember { mutableStateOf(0) }
    val scope = rememberCoroutineScope()
    
    val totalKasState by viewModel.totalKasState.collectAsState()
    val context = LocalContext.current
    val sessionManager = remember { UserSessionManager(context) }
    val loggedInUserId = sessionManager.getIdPengurus()
    var hariPiket by remember { mutableStateOf<String?>(null) }
    var isLoadingHariPiket by remember { mutableStateOf(true) }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    LaunchedEffect(currentRoute) {
        if (currentRoute == "home") {
            refreshTrigger++
        }
    }

    LaunchedEffect(loggedInUserId, refreshTrigger) {
        if (loggedInUserId != null) {
            isLoadingHariPiket = true
            scope.launch {
                try {
                    val response = ApiConfig.getApiService().getPengurusById(loggedInUserId)
                    if (response.isSuccessful && response.body()?.success == true) {
                        val pengurus = response.body()?.data
                        hariPiket = pengurus?.jadwalPiket?.hariPiket ?: "Belum ditentukan"
                    } else {
                        hariPiket = "Belum ditentukan"
                    }
                } catch (e: Exception) {
                    android.util.Log.e("HomeScreen", "❌ Error fetching piket schedule: ${e.message}")
                    hariPiket = "Belum ditentukan"
                } finally {
                    isLoadingHariPiket = false
                }
            }
        } else {
            hariPiket = "Belum ditentukan"
            isLoadingHariPiket = false
        }
    }

    LaunchedEffect(refreshTrigger) {
        android.util.Log.d("HomeScreen", "📱 Fetching data... trigger=$refreshTrigger")

        viewModel.fetchTotalKas()
        
        scope.launch {
            try {
                val eventsResponse = ApiConfig.getApiService().getEvents()
                if (eventsResponse.isSuccessful) {
                    val events = eventsResponse.body()?.events ?: emptyList()
                    android.util.Log.d("HomeScreen", "✅ Events fetched: ${events.size} events")
                    activeEventCount = events.count { event ->
                        val status = EventStatusHelper.getEventStatus(event.tanggal, event.waktu)
                        status == EventStatus.ONGOING || status == EventStatus.UPCOMING
                    }
                    android.util.Log.d("HomeScreen", "🔢 Active events: $activeEventCount")
                }

                val rapatResponse = ApiConfig.getApiService().getAllAgenda()
                if (rapatResponse.isSuccessful && rapatResponse.body()?.success == true) {
                    val allAgenda = rapatResponse.body()?.data ?: emptyList()
                    android.util.Log.d("HomeScreen", "✅ Agenda fetched: ${allAgenda.size} agenda")

                    activeRapatCount = allAgenda.count { agenda ->
                        // Skip jika sudah selesai (isDone = true)
                        if (agenda.isDone) {
                            android.util.Log.d("HomeScreen", "⏭️ Skipped (done): ${agenda.title}")
                            return@count false
                        }
                        
                        try {
                            val currentTime = java.util.Calendar.getInstance()
                            val endParts = agenda.endTimeDisplay.replace(" WIB", "").split(":")
                            val endHour = endParts[0].toInt()
                            val endMinute = endParts[1].toInt()
                            val dateParts = agenda.date.split("-")
                            val year = dateParts[0].toInt()
                            val month = dateParts[1].toInt() - 1
                            val day = dateParts[2].toInt()

                            val agendaEndTime = java.util.Calendar.getInstance().apply {
                                set(java.util.Calendar.YEAR, year)
                                set(java.util.Calendar.MONTH, month)
                                set(java.util.Calendar.DAY_OF_MONTH, day)
                                set(java.util.Calendar.HOUR_OF_DAY, endHour)
                                set(java.util.Calendar.MINUTE, endMinute)
                                set(java.util.Calendar.SECOND, 0)
                                set(java.util.Calendar.MILLISECOND, 0)
                            }
                            val isActive = currentTime.before(agendaEndTime) || currentTime == agendaEndTime
                            
                            if (isActive) {
                                android.util.Log.d("HomeScreen", "✓ Active: ${agenda.title} - ends at $year-${month+1}-$day $endHour:$endMinute")
                            } else {
                                android.util.Log.d("HomeScreen", "⏭️ Skipped (expired): ${agenda.title}")
                            }
                            
                            isActive
                        } catch (e: Exception) {
                            android.util.Log.e("HomeScreen", "❌ Error parsing agenda '${agenda.title}': ${e.message}")
                            android.util.Log.e("HomeScreen", "   date=${agenda.date}, endTimeDisplay=${agenda.endTimeDisplay}")
                            false
                        }
                    }
                    android.util.Log.d("HomeScreen", "🔢 Active rapat: $activeRapatCount (isDone=false & not expired)")
                }

                val notifResponse = ApiConfig.getApiService().getNotifications()
                if (notifResponse.isSuccessful) {
                    val allNotifs = notifResponse.body()?.notifications ?: emptyList()
                    recentNotifications = allNotifs.take(3)
                    android.util.Log.d("HomeScreen", "🔔 Notifications fetched: ${allNotifs.size} total, showing ${recentNotifications.size}")
                }
            } catch (e: Exception) {
                android.util.Log.e("HomeScreen", "❌ Error fetching data: ${e.message}")
            }
        }
    }
    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFE3ECDA),
            Color(0xFFF0F6F0),
            Color(0xFFFFFFFF),
            Color(0xFFFFFFFF),
            Color(0xFFFFFFFF)
        )
    )
    
    val kasValue = when (val state = totalKasState) {
        is TotalKasState.Success -> formatRupiah(state.totalKas)
        is TotalKasState.Loading -> "Memuat..."
        is TotalKasState.Error -> "Gagal"
        else -> "Rp 0"
    }

    Scaffold(
        bottomBar = {
            BottomNavBarContainer(
                navController = navController,
                onHome = { navController.navigate("home") },
                onKas = { navController.navigate("kas") },
                onRapat = { navController.navigate("rapat") },
                onPiket = { navController.navigate("piket") },
                onEvent = { navController.navigate("event") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = gradientBrush)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Selamat Datang",
                        color = Color(0xFF2D3319),
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                    Spacer(Modifier.height(4.dp))
                    val currentDate = remember {
                        val calendar = Calendar.getInstance()
                        val dayNames = arrayOf("Minggu", "Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu")
                        val dayName = dayNames[calendar.get(Calendar.DAY_OF_WEEK) - 1]
                        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
                        "$dayName, ${dateFormat.format(calendar.time)}"
                    }
                    Text(
                        currentDate,
                        color = Color(0xFF6B7280),
                        fontSize = 14.sp
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier.clickable { 
                            navController.navigate("notifications") 
                        }
                    ) {
                        Icon(
                            Icons.Default.Notifications,
                            contentDescription = "notif",
                            tint = Color(0xFF4A5D23),
                            modifier = Modifier.size(28.dp)
                        )
                        // Red dot notification indicator
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(Color.Red, CircleShape)
                                .align(Alignment.TopEnd)
                        )
                    }
                    Icon(
                        Icons.Default.Menu,
                        contentDescription = "menu",
                        tint = Color(0xFF4A5D23),
                        modifier = Modifier
                            .size(28.dp)
                            .clickable { isMenuVisible = true }
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SummaryCard(
                    title = "Kas Terkumpul",
                    value = kasValue,
                    icon = Icons.Default.AccountBalanceWallet,
                    bgColor = Color(0xFFBDD99E),
                    iconBgColor = Color(0xFFA8CC82),
                    modifier = Modifier.weight(1f)
                )
                SummaryCard(
                    title = "Event Aktif",
                    value = "$activeEventCount Event",
                    icon = Icons.Default.Campaign,
                    bgColor = Color(0xFFF5E8A0),
                    iconBgColor = Color(0xFFEDD97A),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(12.dp))

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SummaryCard(
                    title = "Jadwal Piket",
                    value = if (isLoadingHariPiket) "Memuat..." else (hariPiket ?: "Belum ditentukan"),
                    icon = Icons.Default.CalendarToday,
                    bgColor = Color(0xFF4A5D23),
                    iconBgColor = Color(0xFF3A4D13),
                    isLightText = true,
                    modifier = Modifier.weight(1f)
                )
                SummaryCard(
                    title = "Rapat Aktif",
                    value = "$activeRapatCount Rapat",
                    icon = Icons.Default.EventNote,
                    bgColor = Color(0xFFD9D9D9),
                    iconBgColor = Color(0xFFC4C4C4),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(28.dp))

            Text(
                "Menu Utama",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color(0xFF2D3319)
            )

            Spacer(Modifier.height(16.dp))
            MenuItemCard(
                title = "Bayar Kas",
                description = "Lihat histori dan bayar iuran kas",
                icon = Icons.Default.AccountBalanceWallet,
                iconBgColor = Color(0xFFDFF5E1),
                iconColor = Color(0xFF4A5D23),
                onClick = { navController.navigate("kas") }
            )
            
            Spacer(Modifier.height(12.dp))
            
            MenuItemCard(
                title = "Lihat Event",
                description = "Daftar event dan kegiatan terbaru",
                icon = Icons.Default.Campaign,
                iconBgColor = Color(0xFFFFF4E6),
                iconColor = Color(0xFFEA580C),
                onClick = { navController.navigate("event") }
            )
            
            Spacer(Modifier.height(12.dp))
            
            MenuItemCard(
                title = "Lihat Agenda Rapat",
                description = "Jadwal rapat dan absensi rapat",
                icon = Icons.Default.EventNote,
                iconBgColor = Color(0xFFFEE2E2),
                iconColor = Color(0xFFDC2626),
                onClick = { navController.navigate("rapat") }
            )
            
            Spacer(Modifier.height(12.dp))
            
            MenuItemCard(
                title = "Absen Piket",
                description = "Lihat jadwal dan isi absen piket",
                icon = Icons.Default.Assignment,
                iconBgColor = Color(0xFFE0E7FF),
                iconColor = Color(0xFF4F46E5),
                onClick = { navController.navigate("piket") }
            )
        }
    }
    MenuDrawer(
        isVisible = isMenuVisible,
        onDismiss = { isMenuVisible = false },
        userName = sessionManager.getNamaPengurus() ?: "Pengguna",
        onProfileClick = {
            navController.navigate("profile")
            isMenuVisible = false
        },
        onAboutClick = { 
            navController.navigate("about")
            isMenuVisible = false
        },
        onLogoutClick = {
            isMenuVisible = false
            sessionManager.clearSession()
            navController.navigate("login") {
                popUpTo(0) { inclusive = true }
            }
        }
    )
}

@Composable
fun SummaryCard(
    title: String,
    value: String,
    icon: ImageVector,
    bgColor: Color,
    iconBgColor: Color,
    isLightText: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(20.dp),
                clip = false
            )
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .padding(20.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(iconBgColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isLightText) Color.White else Color(0xFF2D3319),
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(Modifier.height(16.dp))

        Text(
            title,
            color = if (isLightText) Color.White.copy(alpha = 0.9f) else Color(0xFF2D3319),
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(Modifier.height(4.dp))

        Text(
            value,
            color = if (isLightText) Color.White else Color(0xFF2D3319),
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
    }
}
fun formatNotificationTime(dateString: String): String {
    return try {
        val parts = dateString.split("T")
        if (parts.size == 2) {
            val timePart = parts[1].split(".")[0]
            val timeComponents = timePart.split(":")
            val hour = timeComponents[0].toInt()
            val minute = timeComponents[1].toInt()
            val notifTime = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
            }
            val now = Calendar.getInstance()
            val diffInMinutes = (now.timeInMillis - notifTime.timeInMillis) / (1000 * 60)
            
            when {
                diffInMinutes < 60 -> "${diffInMinutes.toInt()} menit"
                diffInMinutes < 1440 -> "${(diffInMinutes / 60).toInt()} jam"
                else -> "${(diffInMinutes / 1440).toInt()} hari"
            }
        } else {
            "baru saja"
        }
    } catch (e: Exception) {
        "baru saja"
    }
}

@Composable
fun ActivityItem(
    text: String,
    time: String,
    icon: ImageVector,
    bgColor: Color,
    iconBgColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(16.dp),
                clip = false
            )
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(iconBgColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF2D3319),
                modifier = Modifier.size(24.dp)
            )
        }
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text,
                color = Color(0xFF1F1F1F),
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                modifier = Modifier.weight(1f)
            )

            Text(
                time,
                color = Color(0xFF6B7280),
                fontSize = 13.sp
            )
        }
    }
}

@Composable
fun MenuItemCard(
    title: String,
    description: String,
    icon: ImageVector,
    iconBgColor: Color,
    iconColor: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                clip = false
            )
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(iconBgColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(28.dp)
            )
        }
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                title,
                color = Color(0xFF1F2937),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Spacer(Modifier.height(4.dp))
            Text(
                description,
                color = Color(0xFF6B7280),
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = Color(0xFF9CA3AF),
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun BottomNavBarContainer(
    navController: NavHostController,
    onHome: () -> Unit,
    onKas: () -> Unit,
    onRapat: () -> Unit,
    onPiket: () -> Unit,
    onEvent: () -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route?.substringBefore('/')
    val selectedIndex = bottomBarItems.find { it.route == currentRoute }?.index ?: 0 //
    Surface(
        color = Color(0xFFDDECCF),
        shadowElevation = 4.dp,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        BottomNavBar(
            selectedIndex = selectedIndex,
            onHome = onHome,
            onKas = onKas,
            onRapat = onRapat,
            onPiket = onPiket,
            onEvent = onEvent
        )
    }
}

fun formatRupiah(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    return format.format(amount).replace("Rp", "Rp ")
}
