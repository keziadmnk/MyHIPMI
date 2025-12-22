package com.example.myhipmi.ui.screen.rapat

import android.Manifest
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.myhipmi.ui.components.MyHipmiTopBar
import com.example.myhipmi.ui.components.MenuDrawer
import com.example.myhipmi.ui.theme.*
import com.example.myhipmi.ui.viewmodel.RapatViewModel
import com.example.myhipmi.data.local.UserSessionManager
import java.util.Calendar
import java.util.Locale
import kotlinx.coroutines.launch
import androidx.core.net.toUri
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date


@Composable
fun ModernInfoRow(icon: ImageVector, label: String, value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(GreenPrimary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = GreenPrimary,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                fontSize = 12.sp,
                color = Color(0xFF9CA3AF),
                fontWeight = FontWeight.Medium
            )
            Text(
                text = value,
                fontSize = 15.sp,
                color = Color(0xFF1F2937),
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

@Composable
fun TimeScheduleRow(icon: ImageVector, label: String, value: String) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFFEF4444).copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFFEF4444),
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column {
            Text(
                text = label,
                fontSize = 12.sp,
                color = Color(0xFF9CA3AF),
                fontWeight = FontWeight.Medium
            )
            Text(
                text = value,
                fontSize = 15.sp,
                color = Color(0xFF1F2937),
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

@Composable
fun RapatDetailScreen(navController: NavHostController, backStackEntry: NavBackStackEntry) {
    val viewModel: RapatViewModel = viewModel()
    val context = LocalContext.current
    val sessionManager = remember { UserSessionManager(context) }
    val loggedInUserId = sessionManager.getIdPengurus()
    val loggedInUserName = sessionManager.getNamaPengurus()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val idAgenda = backStackEntry.arguments?.getString("idAgenda")?.toIntOrNull() ?: 0
    val title = backStackEntry.arguments?.getString("title") ?: "Rapat"
    val date = backStackEntry.arguments?.getString("date") ?: "-"
    val startTime = backStackEntry.arguments?.getString("startTime") ?: "-"
    val endTime = backStackEntry.arguments?.getString("endTime") ?: "-"
    val location = backStackEntry.arguments?.getString("location") ?: "-"
    val isDone = backStackEntry.arguments?.getString("isDone")?.toBoolean() ?: false

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var isMenuVisible by remember { mutableStateOf(false) }
    var photoFile by remember { mutableStateOf<File?>(null) }
    var absenTimestamp by remember { mutableStateOf<String?>(null) }
    var absenPhotoUrl by remember { mutableStateOf<String?>(null) }
    var isLoadingAbsen by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        android.util.Log.d("RapatDetailScreen", "=== SCREEN OPENED ===")
        android.util.Log.d("RapatDetailScreen", "idAgenda: $idAgenda")
        android.util.Log.d("RapatDetailScreen", "isDone: $isDone")
        android.util.Log.d("RapatDetailScreen", "loggedInUserId: $loggedInUserId")
        android.util.Log.d("RapatDetailScreen", "title: $title")
    }

    LaunchedEffect(idAgenda) {
        val savedTimestamp = sessionManager.getAbsenTimestamp(idAgenda)
        android.util.Log.d("RapatDetailScreen", "Checking SharedPreferences for agenda $idAgenda: $savedTimestamp")
        if (savedTimestamp != null) {
            absenTimestamp = savedTimestamp
            android.util.Log.d("RapatDetailScreen", "✓ Loaded saved timestamp from SharedPreferences: $savedTimestamp")
        } else {
            android.util.Log.d("RapatDetailScreen", "✗ No saved timestamp in SharedPreferences")
        }
    }

    LaunchedEffect(idAgenda, loggedInUserId) {
        android.util.Log.d("RapatDetailScreen", "========================================")
        android.util.Log.d("RapatDetailScreen", "LaunchedEffect triggered")
        android.util.Log.d("RapatDetailScreen", "- idAgenda: $idAgenda")
        android.util.Log.d("RapatDetailScreen", "- loggedInUserId: $loggedInUserId")
        android.util.Log.d("RapatDetailScreen", "- isDone: $isDone")
        android.util.Log.d("RapatDetailScreen", "========================================")

        if (loggedInUserId != null && idAgenda > 0) {
            isLoadingAbsen = true
            try {
                android.util.Log.d("RapatDetailScreen", "🌐 Calling API: getAbsenByAgenda($idAgenda)")

                val response = viewModel.apiService.getAbsenByAgenda(idAgenda)

                android.util.Log.d("RapatDetailScreen", "📡 API Response:")
                android.util.Log.d("RapatDetailScreen", "  - isSuccessful: ${response.isSuccessful}")
                android.util.Log.d("RapatDetailScreen", "  - code: ${response.code()}")
                android.util.Log.d("RapatDetailScreen", "  - message: ${response.message()}")

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    android.util.Log.d("RapatDetailScreen", "  - body success: ${responseBody?.success}")
                    android.util.Log.d("RapatDetailScreen", "  - body data size: ${responseBody?.data?.size}")

                    if (responseBody?.success == true) {
                        val absenList = responseBody.data
                        android.util.Log.d("RapatDetailScreen", "✓ Received ${absenList.size} absen records")
                        android.util.Log.d("RapatDetailScreen", "🔍 Looking for idPengurus: $loggedInUserId")

                        absenList.forEachIndexed { index, absen ->
                            android.util.Log.d("RapatDetailScreen", "📋 Absen #${index + 1}:")
                            android.util.Log.d("RapatDetailScreen", "   - id_absenRapat: ${absen.idAbsenRapat}")
                            android.util.Log.d("RapatDetailScreen", "   - id_agenda: ${absen.idAgenda}")
                            android.util.Log.d("RapatDetailScreen", "   - id_pengurus: ${absen.idPengurus}")
                            android.util.Log.d("RapatDetailScreen", "   - timestamp: '${absen.timestamp}'")
                            android.util.Log.d("RapatDetailScreen", "   - photobuktiUrl: '${absen.photobuktiUrl}'")
                            android.util.Log.d("RapatDetailScreen", "   - status: ${absen.status}")
                            android.util.Log.d("RapatDetailScreen", "   - Match? ${absen.idPengurus == loggedInUserId}")
                        }

                        val userAbsen = absenList.find { absen ->
                            absen.idPengurus == loggedInUserId
                        }

                        if (userAbsen != null) {
                            val timestamp = userAbsen.timestamp
                            val photoUrl = userAbsen.photobuktiUrl

                            absenTimestamp = timestamp
                            absenPhotoUrl = photoUrl

                            sessionManager.saveAbsenTimestamp(idAgenda, timestamp)

                            android.util.Log.d("RapatDetailScreen", "✅✅✅ SUCCESS! ✅✅✅")
                            android.util.Log.d("RapatDetailScreen", "Found absen for idPengurus: $loggedInUserId")
                            android.util.Log.d("RapatDetailScreen", "Timestamp: '$timestamp'")
                            android.util.Log.d("RapatDetailScreen", "Photo URL: '$photoUrl'")
                            android.util.Log.d("RapatDetailScreen", "Saved to SharedPreferences")
                        } else {
                            android.util.Log.e("RapatDetailScreen", "❌❌❌ NOT FOUND! ❌❌❌")
                            android.util.Log.e("RapatDetailScreen", "No absen for idPengurus: $loggedInUserId")
                            android.util.Log.e("RapatDetailScreen", "Total records checked: ${absenList.size}")
                        }
                    } else {
                        android.util.Log.e("RapatDetailScreen", "❌ Response success=false")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    android.util.Log.e("RapatDetailScreen", "❌ API Error:")
                    android.util.Log.e("RapatDetailScreen", "  - Code: ${response.code()}")
                    android.util.Log.e("RapatDetailScreen", "  - Error Body: $errorBody")
                }
            } catch (e: Exception) {
                android.util.Log.e("RapatDetailScreen", "❌ Exception occurred:", e)
                android.util.Log.e("RapatDetailScreen", "  - Message: ${e.message}")
                android.util.Log.e("RapatDetailScreen", "  - Stack trace:")
                e.printStackTrace()
            } finally {
                isLoadingAbsen = false
                android.util.Log.d("RapatDetailScreen", "🏁 Finished loading (isLoadingAbsen = false)")
                android.util.Log.d("RapatDetailScreen", "   Current absenTimestamp value: '$absenTimestamp'")
            }
        } else {
            android.util.Log.e("RapatDetailScreen", "❌ Cannot fetch absen:")
            android.util.Log.e("RapatDetailScreen", "   - loggedInUserId: $loggedInUserId (is ${if (loggedInUserId == null) "NULL" else "OK"})")
            android.util.Log.e("RapatDetailScreen", "   - idAgenda: $idAgenda (is ${if (idAgenda <= 0) "INVALID" else "OK"})")
        }
    }

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        android.util.Log.d("RapatDetailScreen", "Take picture result: $isSuccess")
        if (isSuccess && photoFile != null) {
            val photoUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                photoFile!!
            )
            imageUri = photoUri
            android.util.Log.d("RapatDetailScreen", "Photo saved successfully: $photoUri")
        } else {
            android.util.Log.e("RapatDetailScreen", "Failed to take picture or photoFile is null")
            coroutineScope.launch {
                snackbarHostState.showSnackbar("Gagal mengambil foto. Silakan coba lagi.")
            }
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted && photoFile != null) {
            val photoUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                photoFile!!
            )
            android.util.Log.d("RapatDetailScreen", "Launching camera with URI: $photoUri")
            takePictureLauncher.launch(photoUri)
        } else {
            coroutineScope.launch {
                snackbarHostState.showSnackbar("Permission kamera diperlukan untuk mengambil foto absensi")
            }
        }
    }

    // Status: 0 = Belum dimulai, 1 = Sedang berlangsung, 2 = Sudah terlewat
    fun checkAbsenTimeStatus(): Int {
        try {
            val currentTime = Calendar.getInstance()
            val currentYear = currentTime.get(Calendar.YEAR)
            val currentMonth = currentTime.get(Calendar.MONTH)
            val currentDay = currentTime.get(Calendar.DAY_OF_MONTH)
            val currentHour = currentTime.get(Calendar.HOUR_OF_DAY)
            val currentMinute = currentTime.get(Calendar.MINUTE)

            val agendaDateParts = date.split(" ")
            val agendaDay = agendaDateParts[0].toInt()
            val monthName = agendaDateParts[1]
            val agendaYear = agendaDateParts[2].toInt()

            val agendaMonth = when (monthName.lowercase()) {
                "januari" -> Calendar.JANUARY
                "februari" -> Calendar.FEBRUARY
                "maret" -> Calendar.MARCH
                "april" -> Calendar.APRIL
                "mei" -> Calendar.MAY
                "juni" -> Calendar.JUNE
                "juli" -> Calendar.JULY
                "agustus" -> Calendar.AUGUST
                "september" -> Calendar.SEPTEMBER
                "oktober" -> Calendar.OCTOBER
                "november" -> Calendar.NOVEMBER
                "desember" -> Calendar.DECEMBER
                else -> -1
            }

            val isSameDate = (currentYear == agendaYear) &&
                    (currentMonth == agendaMonth) &&
                    (currentDay == agendaDay)

            if (!isSameDate) {
                return 2 // Beda tanggal = sudah terlewat
            }

            val startParts = startTime.replace(" WIB", "").split(":")
            val startHour = startParts[0].toInt()
            val startMinute = startParts[1].toInt()

            val endParts = endTime.replace(" WIB", "").split(":")
            val endHour = endParts[0].toInt()
            val endMinute = endParts[1].toInt()

            val currentTotalMinutes = currentHour * 60 + currentMinute
            val startTotalMinutes = startHour * 60 + startMinute
            val endTotalMinutes = endHour * 60 + endMinute

            return when {
                currentTotalMinutes < startTotalMinutes -> 0 // Belum dimulai
                currentTotalMinutes > endTotalMinutes -> 2 // Sudah terlewat
                else -> 1 // Sedang berlangsung
            }
        } catch (e: Exception) {
            android.util.Log.e("RapatDetailScreen", "Error checking absen time status: ${e.message}", e)
            return 2 // Default: sudah terlewat
        }
    }
    
    fun isValidAbsenTime(): Boolean = checkAbsenTimeStatus() == 1

    val vmError by viewModel.errorMessage.collectAsState()
    val vmSuccess by viewModel.successMessage.collectAsState()
    LaunchedEffect(vmError, vmSuccess) {
        vmError?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
            if (it.contains("sudah melakukan absen", ignoreCase = true)) {
                kotlinx.coroutines.delay(1500)
                navController.navigate("rapat") {
                    popUpTo("rapat") { inclusive = true }
                }
            }
        }
        vmSuccess?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                MyHipmiTopBar(
                    title = "Detail Rapat",
                    onBackClick = { navController.popBackStack() }
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .background(Color(0xFFF8F9FA))
                    .padding(20.dp)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(
                                        Brush.linearGradient(
                                            colors = listOf(
                                                GreenPrimary.copy(alpha = 0.15f),
                                                GreenPrimary.copy(alpha = 0.05f)
                                            )
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.EventNote,
                                    contentDescription = null,
                                    tint = GreenPrimary,
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Informasi Rapat",
                                    fontSize = 13.sp,
                                    color = Color(0xFF6B7280),
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = title,
                                    fontSize = 19.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1F2937),
                                    lineHeight = 26.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Divider(color = Color(0xFFE5E7EB), thickness = 1.dp)

                        Spacer(modifier = Modifier.height(20.dp))

                        ModernInfoRow(Icons.Default.DateRange, "Tanggal", date)
                        Spacer(modifier = Modifier.height(14.dp))
                        ModernInfoRow(Icons.Default.Schedule, "Waktu", "$startTime - $endTime")
                        Spacer(modifier = Modifier.height(14.dp))
                        ModernInfoRow(Icons.Default.Place, "Lokasi", location)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                if (isDone) {
                    fun formatAbsenTimestamp(timestamp: String?): String {
                        android.util.Log.d("RapatDetailScreen", "formatAbsenTimestamp called with: '$timestamp'")
                        return try {
                            if (timestamp.isNullOrBlank()) {
                                android.util.Log.w("RapatDetailScreen", "Timestamp is null or blank")
                                return "waktu tidak tersedia"
                            }

                            val date = try {
                                android.util.Log.d("RapatDetailScreen", "Trying ISO 8601 format...")
                                val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                                isoFormat.timeZone = java.util.TimeZone.getTimeZone("UTC")
                                isoFormat.parse(timestamp)
                            } catch (e: Exception) {
                                try {
                                    android.util.Log.d("RapatDetailScreen", "Trying MySQL format...")
                                    val mysqlFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                                    mysqlFormat.parse(timestamp)
                                } catch (e2: Exception) {
                                    android.util.Log.e("RapatDetailScreen", "Both formats failed!")
                                    null
                                }
                            }

                            android.util.Log.d("RapatDetailScreen", "Parsed date: $date")

                            if (date != null) {
                                val wibFormat = java.util.Calendar.getInstance()
                                wibFormat.time = date

                                val outputDateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
                                val outputTimeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

                                outputDateFormat.timeZone = java.util.TimeZone.getTimeZone("Asia/Jakarta")
                                outputTimeFormat.timeZone = java.util.TimeZone.getTimeZone("Asia/Jakarta")

                                val formattedDate = outputDateFormat.format(date)
                                val formattedTime = outputTimeFormat.format(date)
                                val result = "$formattedDate pukul $formattedTime WIB"

                                android.util.Log.d("RapatDetailScreen", "✓ Formatted result: $result")
                                result
                            } else {
                                android.util.Log.w("RapatDetailScreen", "Date parsing returned null")
                                "waktu tidak tersedia"
                            }
                        } catch (e: Exception) {
                            android.util.Log.e("RapatDetailScreen", "Error formatting timestamp '$timestamp': ${e.message}", e)
                            "waktu tidak tersedia"
                        }
                    }

                    android.util.Log.d("RapatDetailScreen", "🎨 Rendering absen UI:")
                    android.util.Log.d("RapatDetailScreen", "   - isLoadingAbsen: $isLoadingAbsen")
                    android.util.Log.d("RapatDetailScreen", "   - absenTimestamp: '$absenTimestamp'")
                    android.util.Log.d("RapatDetailScreen", "   - isDone: $isDone")

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(6.dp, RoundedCornerShape(20.dp)),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(
                                            Brush.linearGradient(
                                                colors = listOf(
                                                    Color(0xFF10B981),
                                                    Color(0xFF059669)
                                                )
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Absensi Berhasil",
                                        fontSize = 19.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1F2937)
                                    )
                                    if (isLoadingAbsen) {
                                        Text(
                                            text = "Memuat data...",
                                            fontSize = 13.sp,
                                            color = Color(0xFF6B7280),
                                            modifier = Modifier.padding(top = 2.dp)
                                        )
                                    } else {
                                        val absenTimeText = formatAbsenTimestamp(absenTimestamp)
                                        Text(
                                            text = absenTimeText,
                                            fontSize = 13.sp,
                                            color = Color(0xFF6B7280),
                                            modifier = Modifier.padding(top = 2.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            val photoToDisplay = when {
                                absenPhotoUrl != null && absenPhotoUrl!!.isNotBlank() -> {
                                    android.util.Log.d("RapatDetailScreen", "📷 Displaying photo from API: $absenPhotoUrl")
                                    absenPhotoUrl
                                }
                                imageUri != null -> {
                                    android.util.Log.d("RapatDetailScreen", "📷 Displaying photo from local URI: $imageUri")
                                    imageUri.toString()
                                }
                                else -> {
                                    android.util.Log.d("RapatDetailScreen", "📷 No photo available, showing placeholder")
                                    "https://via.placeholder.com/600x300.png?text=Foto+Rapat"
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(240.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .border(
                                        width = 2.dp,
                                        color = Color(0xFFE5E7EB),
                                        shape = RoundedCornerShape(16.dp)
                                    )
                            ) {
                                AsyncImage(
                                    model = photoToDisplay,
                                    contentDescription = "Foto Bukti Absen",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop,
                                    error = androidx.compose.ui.res.painterResource(android.R.drawable.ic_menu_gallery),
                                    placeholder = androidx.compose.ui.res.painterResource(android.R.drawable.ic_menu_gallery)
                                )

                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(12.dp)
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(Color(0xFF10B981))
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    val absenTimeStatus = checkAbsenTimeStatus()
                    val currentTime = Calendar.getInstance()
                    val currentTimeString = String.format(Locale.getDefault(), "%02d:%02d",
                        currentTime.get(Calendar.HOUR_OF_DAY),
                        currentTime.get(Calendar.MINUTE))

                    when (absenTimeStatus) {
                        0 -> { // Belum dimulai
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .shadow(6.dp, RoundedCornerShape(20.dp)),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color.White
                                ),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(56.dp)
                                                .clip(RoundedCornerShape(14.dp))
                                                .background(
                                                    Brush.linearGradient(
                                                        colors = listOf(
                                                            Color(0xFFFBBF24),
                                                            Color(0xFFF59E0B)
                                                        )
                                                    )
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Schedule,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(30.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(16.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = "Absensi Belum Dimulai",
                                                fontSize = 19.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF1F2937),
                                                lineHeight = 26.sp
                                            )
                                            Text(
                                                text = "Waktu absensi belum dimulai",
                                                fontSize = 13.sp,
                                                color = Color(0xFF6B7280),
                                                modifier = Modifier.padding(top = 2.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(20.dp))

                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color(0xFFFEF3C7))
                                            .border(
                                                width = 1.dp,
                                                color = Color(0xFFFBBF24).copy(alpha = 0.2f),
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                            .padding(16.dp)
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Info,
                                                contentDescription = null,
                                                tint = Color(0xFFF59E0B),
                                                modifier = Modifier.size(24.dp)
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = "Mohon Tunggu",
                                                fontSize = 14.sp,
                                                color = Color(0xFF1F2937),
                                                fontWeight = FontWeight.SemiBold,
                                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "Absensi dapat diisi mulai pukul $startTime",
                                                fontSize = 13.sp,
                                                color = Color(0xFF6B7280),
                                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                                lineHeight = 18.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        2 -> { // Sudah terlewat
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .shadow(6.dp, RoundedCornerShape(20.dp)),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color.White
                                ),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(56.dp)
                                                .clip(RoundedCornerShape(14.dp))
                                                .background(
                                                    Brush.linearGradient(
                                                        colors = listOf(
                                                            Color(0xFFEF4444),
                                                            Color(0xFFDC2626)
                                                        )
                                                    )
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.EventBusy,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(30.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(16.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = "Absensi Ditutup",
                                                fontSize = 19.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF1F2937),
                                                lineHeight = 26.sp
                                            )
                                            Text(
                                                text = "karena tidak berada pada waktu yang ditentukan",
                                                fontSize = 13.sp,
                                                color = Color(0xFF6B7280),
                                                modifier = Modifier.padding(top = 2.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(20.dp))

                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color(0xFFFEF2F2))
                                            .border(
                                                width = 1.dp,
                                                color = Color(0xFFEF4444).copy(alpha = 0.2f),
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                            .padding(16.dp)
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Info,
                                                contentDescription = null,
                                                tint = Color(0xFFEF4444),
                                                modifier = Modifier.size(24.dp)
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = "Anda belum mengisi absen",
                                                fontSize = 14.sp,
                                                color = Color(0xFF1F2937),
                                                fontWeight = FontWeight.SemiBold,
                                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "Waktu pengisian absensi ditutup",
                                                fontSize = 13.sp,
                                                color = Color(0xFF6B7280),
                                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                                lineHeight = 18.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        else -> { // Status 1: Sedang berlangsung
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .shadow(6.dp, RoundedCornerShape(20.dp)),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp)
                            ) {
                                if (imageUri == null) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(56.dp)
                                                .clip(RoundedCornerShape(14.dp))
                                                .background(
                                                    Brush.linearGradient(
                                                        colors = listOf(
                                                            Color(0xFF3B82F6),
                                                            Color(0xFF2563EB)
                                                        )
                                                    )
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.CameraAlt,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(30.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(16.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = "Absensi Tersedia",
                                                fontSize = 19.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF1F2937)
                                            )
                                            Text(
                                                text = "Ambil foto untuk absen",
                                                fontSize = 13.sp,
                                                color = Color(0xFF6B7280),
                                                modifier = Modifier.padding(top = 2.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(20.dp))

                                    Button(
                                        onClick = {
                                            if (isValidAbsenTime()) {
                                                try {
                                                    val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
                                                    val fileName = "HIPMI_ABSEN_${timeStamp}.jpg"

                                                    val picturesDir = File(context.getExternalFilesDir(null), "Pictures")
                                                    if (!picturesDir.exists()) {
                                                        picturesDir.mkdirs()
                                                    }

                                                    photoFile = File(picturesDir, fileName)
                                                    android.util.Log.d("RapatDetailScreen", "Photo file created: ${photoFile?.absolutePath}")

                                                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                                } catch (e: Exception) {
                                                    android.util.Log.e("RapatDetailScreen", "Error creating photo file: ${e.message}", e)
                                                    coroutineScope.launch {
                                                        snackbarHostState.showSnackbar("Gagal menyiapkan file foto. Silakan coba lagi.")
                                                    }
                                                }
                                            } else {
                                                coroutineScope.launch {
                                                    snackbarHostState.showSnackbar("Waktu absensi sudah tidak valid. Silakan refresh halaman.")
                                                }
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = GreenPrimary
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(56.dp),
                                        shape = RoundedCornerShape(14.dp),
                                        elevation = ButtonDefaults.buttonElevation(
                                            defaultElevation = 2.dp,
                                            pressedElevation = 6.dp
                                        )
                                    ) {
                                        Icon(
                                            Icons.Default.CameraAlt,
                                            contentDescription = null,
                                            modifier = Modifier.size(22.dp)
                                        )
                                        Spacer(Modifier.width(10.dp))
                                        Text(
                                            "Ambil Foto Absensi",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                } else {
                                    Text(
                                        text = "Preview Foto",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1F2937),
                                        modifier = Modifier.padding(bottom = 16.dp)
                                    )

                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(240.dp)
                                            .clip(RoundedCornerShape(16.dp))
                                            .border(
                                                width = 2.dp,
                                                color = Color(0xFFE5E7EB),
                                                shape = RoundedCornerShape(16.dp)
                                            )
                                    ) {
                                        AsyncImage(
                                            model = imageUri,
                                            contentDescription = "Preview Foto Absensi",
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(20.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        OutlinedButton(
                                            onClick = {
                                                imageUri = null
                                                photoFile = null
                                            },
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(52.dp),
                                            colors = ButtonDefaults.outlinedButtonColors(
                                                contentColor = GreenPrimary
                                            ),
                                            border = BorderStroke(1.5.dp, GreenPrimary),
                                            shape = RoundedCornerShape(14.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Refresh,
                                                contentDescription = null,
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(Modifier.width(6.dp))
                                            Text(
                                                "Ambil Ulang",
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }

                                        Button(
                                            onClick = {
                                                if (loggedInUserId == null) {
                                                    coroutineScope.launch {
                                                        snackbarHostState.showSnackbar("Anda belum login. Silakan login terlebih dahulu.")
                                                    }
                                                    navController.navigate("login")
                                                    return@Button
                                                }

                                                if (!isValidAbsenTime()) {
                                                    coroutineScope.launch {
                                                        snackbarHostState.showSnackbar("Waktu absensi sudah tidak valid. Absen tidak dapat disimpan.")
                                                    }
                                                    return@Button
                                                }

                                                android.util.Log.d("RapatDetailScreen", "Saving absen with photo URI: $imageUri")

                                                viewModel.createAbsen(
                                                    idAgenda = idAgenda,
                                                    idPengurus = loggedInUserId,
                                                    photoUrl = imageUri.toString(),
                                                    status = "present",
                                                    onSuccess = { response ->
                                                        android.util.Log.d("RapatDetailScreen", "createAbsen onSuccess called")
                                                        android.util.Log.d("RapatDetailScreen", "Response: $response")
                                                        android.util.Log.d("RapatDetailScreen", "Response data: ${response?.data}")

                                                        val timestamp = response?.data?.timestamp
                                                        android.util.Log.d("RapatDetailScreen", "Timestamp from response: $timestamp")

                                                        if (timestamp != null) {
                                                            sessionManager.saveAbsenTimestamp(idAgenda, timestamp)
                                                            android.util.Log.d("RapatDetailScreen", "✓ Saved absen timestamp to SharedPreferences: $timestamp for agenda $idAgenda")
                                                        } else {
                                                            android.util.Log.e("RapatDetailScreen", "✗ Timestamp is NULL in response!")
                                                        }

                                                        navController.navigate("rapat") {
                                                            popUpTo("rapat") { inclusive = true }
                                                        }
                                                    }
                                                )
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = GreenPrimary
                                            ),
                                            modifier = Modifier
                                                .weight(2f)
                                                .height(52.dp),
                                            shape = RoundedCornerShape(14.dp),
                                            elevation = ButtonDefaults.buttonElevation(
                                                defaultElevation = 2.dp,
                                                pressedElevation = 6.dp
                                            )
                                        ) {
                                            Icon(
                                                Icons.Default.Save,
                                                contentDescription = null,
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(Modifier.width(6.dp))
                                            Text(
                                                "Simpan Absensi",
                                                color = Color.White,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }
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
}
}