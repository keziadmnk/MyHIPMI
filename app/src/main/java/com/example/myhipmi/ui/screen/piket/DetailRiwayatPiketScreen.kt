package com.example.myhipmi.ui.screen.piket

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.myhipmi.data.local.UserSessionManager
import com.example.myhipmi.data.remote.retrofit.ApiConfig
import com.example.myhipmi.data.remote.response.AbsenPiketData
import com.example.myhipmi.ui.components.MenuDrawer
import com.example.myhipmi.ui.components.MyHipmiTopBar
import com.example.myhipmi.ui.screen.home.BottomNavBarContainer
import com.example.myhipmi.ui.theme.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DetailRiwayatPiketScreen(
    navController: NavHostController,
    idAbsenPiket: Int
) {
    val context = LocalContext.current
    val sessionManager = remember { UserSessionManager(context) }
    val loggedInUserName = sessionManager.getNamaPengurus()
    val apiService = remember { ApiConfig.getApiService() }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    
    var isMenuVisible by remember { mutableStateOf(false) }
    var absenPiket by remember { mutableStateOf<AbsenPiketData?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    // Ambil data absen piket dari API
    LaunchedEffect(idAbsenPiket) {
        isLoading = true
        try {
            val response = apiService.getAbsenPiketById(idAbsenPiket)
            if (response.isSuccessful && response.body()?.success == true) {
                absenPiket = response.body()?.data
                if (absenPiket == null) {
                    errorMessage = "Data absen piket tidak ditemukan"
                }
            } else {
                errorMessage = "Gagal memuat data absen piket"
            }
        } catch (e: Exception) {
            errorMessage = "Error: ${e.message}"
        } finally {
            isLoading = false
        }
    }
    
    // Fungsi untuk format tanggal dari database (YYYY-MM-DD) ke format tampilan
    fun formatTanggal(tanggalDb: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = inputFormat.parse(tanggalDb)
            if (date != null) {
                val outputFormat = SimpleDateFormat("EEEE, d MMMM yyyy", Locale("id", "ID"))
                outputFormat.format(date)
            } else {
                tanggalDb
            }
        } catch (e: Exception) {
            tanggalDb
        }
    }
    
    // Fungsi untuk format jam dari database (HH:mm:ss) ke format tampilan
    fun formatJam(jamDb: String): String {
        return try {
            val inputFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            val time = inputFormat.parse(jamDb)
            if (time != null) {
                val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                outputFormat.format(time) + " WIB"
            } else {
                jamDb
            }
        } catch (e: Exception) {
            jamDb
        }
    }
    
    // Fungsi untuk decode base64 image
    fun decodeBase64Image(base64String: String): android.graphics.Bitmap? {
        return try {
            // Handle berbagai format base64
            val base64Image = when {
                base64String.contains(",") -> {
                    // Jika ada koma, ambil bagian setelah koma (setelah "data:image/...;base64,")
                    base64String.substringAfter(",")
                }
                base64String.startsWith("data:") -> {
                    // Jika dimulai dengan "data:", ambil setelah koma
                    base64String.substringAfter(",")
                }
                else -> {
                    // Jika tidak ada prefix, gunakan langsung
                    base64String
                }
            }.trim()
            
            // Decode base64
            val imageBytes = Base64.decode(base64Image, Base64.DEFAULT)
            
            // Decode bitmap
            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            
            if (bitmap == null) {
                android.util.Log.e("DetailRiwayatPiket", "Failed to decode bitmap from base64")
            }
            
            bitmap
        } catch (e: Exception) {
            android.util.Log.e("DetailRiwayatPiket", "Error decoding base64: ${e.message}")
            android.util.Log.e("DetailRiwayatPiket", "Base64 string length: ${base64String.length}")
            android.util.Log.e("DetailRiwayatPiket", "Base64 string preview: ${base64String.take(100)}")
            null
        }
    }

    Scaffold(
        topBar = {
            MyHipmiTopBar(
                title = "Detail Absen Piket",
                onBackClick = { navController.popBackStack() },
                onMenuClick = { isMenuVisible = true }
            )
        },
        bottomBar = {
            BottomNavBarContainer(
                navController = navController,
                onHome = { navController.navigate("home") },
                onKas = { navController.navigate("kas") },
                onRapat = { navController.navigate("rapat") },
                onPiket = { navController.navigate("piket") },
                onEvent = { navController.navigate("event") }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0f to BackgroundLight,
                        0.25f to White
                    )
                )
                .padding(innerPadding)
        ) {
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = PrimaryGreen)
                    }
                }
                errorMessage != null || absenPiket == null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ErrorOutline,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = TextSecondary
                            )
                            Spacer(Modifier.height(16.dp))
                            Text(
                                text = errorMessage ?: "Data tidak ditemukan",
                                color = TextSecondary,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp)
                    ) {
                        Spacer(Modifier.height(12.dp))
                        
                        // Card Informasi Utama
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = CardGreen),
                            border = BorderStroke(1.dp, BorderLight),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Text(
                                    text = "Informasi Absen Piket",
                                    color = DarkGreen,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(Modifier.height(16.dp))
                                
                                // Tanggal
                                DetailRow(
                                    icon = Icons.Default.CalendarToday,
                                    label = "Tanggal",
                                    value = formatTanggal(absenPiket!!.tanggalAbsen),
                                    iconTint = PrimaryGreen
                                )
                                
                                Spacer(Modifier.height(12.dp))
                                
                                // Jam Mulai
                                DetailRow(
                                    icon = Icons.Default.AccessTime,
                                    label = "Jam Mulai",
                                    value = formatJam(absenPiket!!.jamMulai),
                                    iconTint = PrimaryGreen
                                )
                                
                                Spacer(Modifier.height(12.dp))
                                
                                // Jam Selesai
                                DetailRow(
                                    icon = Icons.Default.AccessTime,
                                    label = "Jam Selesai",
                                    value = formatJam(absenPiket!!.jamSelesai),
                                    iconTint = PrimaryGreen
                                )
                                
                                Spacer(Modifier.height(12.dp))
                                
                                // Status
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = PrimaryGreen,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = "Status",
                                            color = TextSecondary,
                                            style = MaterialTheme.typography.labelMedium,
                                            fontSize = 12.sp
                                        )
                                        Spacer(Modifier.height(4.dp))
                                        StatusChip(text = "Sudah Absen")
                                    }
                                }
                            }
                        }
                        
                        Spacer(Modifier.height(16.dp))
                        
                        // Card Deskripsi
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = White),
                            border = BorderStroke(1.dp, BorderLight),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Text(
                                    text = "Deskripsi",
                                    color = TextPrimary,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(Modifier.height(12.dp))
                                Text(
                                    text = absenPiket!!.keterangan,
                                    color = TextPrimary,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                        
                        Spacer(Modifier.height(16.dp))
                        
                        // Card Foto Bukti
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = White),
                            border = BorderStroke(1.dp, BorderLight),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Text(
                                    text = "Foto Bukti",
                                    color = TextPrimary,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(Modifier.height(12.dp))
                                
                                val fotoBuktiString = absenPiket!!.fotoBukti
                                
                                // Decode base64 image
                                val bitmap = remember(fotoBuktiString) {
                                    decodeBase64Image(fotoBuktiString)
                                }
                                
                                when {
                                    bitmap != null -> {
                                        // Jika berhasil decode, tampilkan sebagai bitmap
                                        Image(
                                            bitmap = bitmap.asImageBitmap(),
                                            contentDescription = "Foto Bukti Piket",
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(10.dp)),
                                            contentScale = ContentScale.Fit
                                        )
                                    }
                                    else -> {
                                        // Jika gagal decode, tampilkan placeholder error
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(200.dp)
                                                .background(SecondaryGreen, RoundedCornerShape(10.dp)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Image,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(48.dp),
                                                    tint = TextSecondary
                                                )
                                                Spacer(Modifier.height(8.dp))
                                                Text(
                                                    text = "Gagal memuat gambar",
                                                    color = TextSecondary,
                                                    fontSize = 14.sp
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        
                        Spacer(Modifier.height(24.dp))
                    }
                }
            }
        }
    }
    
    // Menu drawer
    MenuDrawer(
        isVisible = isMenuVisible,
        onDismiss = { isMenuVisible = false },
        userName = loggedInUserName ?: "Pengurus",
        userRole = "Sekretaris Umum",
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

@Composable
private fun DetailRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    iconTint: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(12.dp))
        Column {
            Text(
                text = label,
                color = TextSecondary,
                style = MaterialTheme.typography.labelMedium,
                fontSize = 12.sp
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = value,
                color = TextPrimary,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun StatusChip(text: String) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(SecondaryGreen)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(androidx.compose.foundation.shape.CircleShape)
                .background(PrimaryGreen)
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text = text,
            color = PrimaryGreen,
            style = MaterialTheme.typography.labelMedium
        )
    }
}

