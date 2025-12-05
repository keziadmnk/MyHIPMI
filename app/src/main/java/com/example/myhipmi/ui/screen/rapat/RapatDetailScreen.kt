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

    // State untuk menyimpan data absen dari API
    var absenTimestamp by remember { mutableStateOf<String?>(null) }
    var absenPhotoUrl by remember { mutableStateOf<String?>(null) }
    var isLoadingAbsen by remember { mutableStateOf(false) }
    
    // Debug: Log semua parameter penting saat screen dibuka
    LaunchedEffect(Unit) {
        android.util.Log.d("RapatDetailScreen", "=== SCREEN OPENED ===")
        android.util.Log.d("RapatDetailScreen", "idAgenda: $idAgenda")
        android.util.Log.d("RapatDetailScreen", "isDone: $isDone")
        android.util.Log.d("RapatDetailScreen", "loggedInUserId: $loggedInUserId")
        android.util.Log.d("RapatDetailScreen", "title: $title")
    }

    // Ambil timestamp absen dari local storage jika ada
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

    // Ambil data absen dari API - SELALU coba ambil jika user sudah absen
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
                
                // Panggil API untuk mendapatkan absen berdasarkan agenda
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
                        
                        // Debug: Print SEMUA absen dalam list dengan detail lengkap
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
                        
                        // Cari absen dari user yang sedang login
                        val userAbsen = absenList.find { absen ->
                            absen.idPengurus == loggedInUserId
                        }
                        
                        if (userAbsen != null) {
                            val timestamp = userAbsen.timestamp
                            val photoUrl = userAbsen.photobuktiUrl
                            
                            absenTimestamp = timestamp
                            absenPhotoUrl = photoUrl
                            
                            // Simpan ke local storage juga
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

    // Launcher untuk mengambil foto - deklarasi ini harus di atas
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

    // Permission launcher untuk kamera
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

    // Fungsi untuk validasi waktu absen
    fun isValidAbsenTime(): Boolean {
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
                return false
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

            return currentTotalMinutes in startTotalMinutes..endTotalMinutes
        } catch (e: Exception) {
            android.util.Log.e("RapatDetailScreen", "Error validating absen time: ${e.message}", e)
            return false
        }
    }

    // Handle viewModel messages
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
                    onBackClick = { navController.popBackStack() },
                    onMenuClick = { isMenuVisible = true }
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .background(White)
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Spacer(modifier = Modifier.height(8.dp))

                RapatDetailInfoRow(Icons.Default.DateRange, date, PrimaryGreen)
                RapatDetailInfoRow(Icons.Default.Schedule, "$startTime - $endTime", PrimaryGreen)
                RapatDetailInfoRow(Icons.Default.Place, location, PrimaryGreen)

                Spacer(modifier = Modifier.height(20.dp))

                if (isDone) {
                    // Fungsi untuk format timestamp absen
                    fun formatAbsenTimestamp(timestamp: String?): String {
                        android.util.Log.d("RapatDetailScreen", "formatAbsenTimestamp called with: '$timestamp'")
                        return try {
                            if (timestamp.isNullOrBlank()) {
                                android.util.Log.w("RapatDetailScreen", "Timestamp is null or blank")
                                return "waktu tidak tersedia"
                            }
                            
                            // Coba parse dengan berbagai format timestamp
                            val date = try {
                                // Format 1: ISO 8601 UTC (dari backend Node.js/Sequelize)
                                // Contoh: "2025-12-05T05:28:19.000Z"
                                android.util.Log.d("RapatDetailScreen", "Trying ISO 8601 format...")
                                val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                                isoFormat.timeZone = java.util.TimeZone.getTimeZone("UTC")
                                isoFormat.parse(timestamp)
                            } catch (e: Exception) {
                                try {
                                    // Format 2: MySQL datetime format
                                    // Contoh: "2025-12-05 12:28:19"
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
                                // Konversi ke WIB (GMT+7)
                                val wibFormat = java.util.Calendar.getInstance()
                                wibFormat.time = date
                                
                                val outputDateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
                                val outputTimeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                                
                                // Set timezone ke WIB untuk output
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

                    // Tampilkan pesan dengan loading state atau timestamp sebenarnya
                    android.util.Log.d("RapatDetailScreen", "🎨 Rendering absen UI:")
                    android.util.Log.d("RapatDetailScreen", "   - isLoadingAbsen: $isLoadingAbsen")
                    android.util.Log.d("RapatDetailScreen", "   - absenTimestamp: '$absenTimestamp'")
                    android.util.Log.d("RapatDetailScreen", "   - isDone: $isDone")
                    
                    if (isLoadingAbsen) {
                        Text(
                            text = "Memuat data absensi...",
                            color = PrimaryGreen,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                        android.util.Log.d("RapatDetailScreen", "📱 Showing: 'Memuat data absensi...'")
                    } else {
                        val absenTimeText = formatAbsenTimestamp(absenTimestamp)
                        android.util.Log.d("RapatDetailScreen", "📱 Formatted time text: '$absenTimeText'")
                        
                        Text(
                            text = "Anda telah mengisi absen pada $absenTimeText.",
                            color = PrimaryGreen,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                        android.util.Log.d("RapatDetailScreen", "📱 Displayed: 'Anda telah mengisi absen pada $absenTimeText.'")
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Tampilkan foto bukti absen
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
                    
                    AsyncImage(
                        model = photoToDisplay,
                        contentDescription = "Foto Bukti Absen",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, BorderLight, RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop,
                        error = androidx.compose.ui.res.painterResource(android.R.drawable.ic_menu_gallery),
                        placeholder = androidx.compose.ui.res.painterResource(android.R.drawable.ic_menu_gallery)
                    )
                } else {
                    val isValidTime = isValidAbsenTime()
                    val currentTime = Calendar.getInstance()
                    val currentTimeString = String.format(Locale.getDefault(), "%02d:%02d",
                        currentTime.get(Calendar.HOUR_OF_DAY),
                        currentTime.get(Calendar.MINUTE))

                    if (!isValidTime) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = RedPrimary.copy(alpha = 0.08f)
                            ),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.5.dp, RedPrimary.copy(alpha = 0.25f)),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.Start
                            ) {
                                // Header dengan icon
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(RedPrimary.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Schedule,
                                            contentDescription = null,
                                            tint = RedPrimary,
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Absensi Tidak Tersedia",
                                            fontSize = 17.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = RedPrimary,
                                            lineHeight = 22.sp
                                        )
                                        Text(
                                            text = "Waktu absensi telah terlewat",
                                            fontSize = 13.sp,
                                            color = TextSecondary,
                                            modifier = Modifier.padding(top = 2.dp)
                                        )
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                // Divider
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(1.dp)
                                        .background(RedPrimary.copy(alpha = 0.15f))
                                )
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                // Informasi waktu yang diperbolehkan
                                Text(
                                    text = "Absensi hanya dapat diisi pada:",
                                    fontSize = 13.sp,
                                    color = TextSecondary,
                                    fontWeight = FontWeight.Medium
                                )
                                
                                Spacer(modifier = Modifier.height(12.dp))
                                
                                // Info tanggal
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DateRange,
                                        contentDescription = null,
                                        tint = RedPrimary.copy(alpha = 0.7f),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "Tanggal",
                                            fontSize = 12.sp,
                                            color = TextSecondary
                                        )
                                        Text(
                                            text = date,
                                            fontSize = 14.sp,
                                            color = TextPrimary,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                                
                                // Info waktu
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Schedule,
                                        contentDescription = null,
                                        tint = RedPrimary.copy(alpha = 0.7f),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "Waktu Absensi",
                                            fontSize = 12.sp,
                                            color = TextSecondary
                                        )
                                        Text(
                                            text = "$startTime - $endTime WIB",
                                            fontSize = 14.sp,
                                            color = TextPrimary,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                // Footer dengan waktu sekarang
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(RedPrimary.copy(alpha = 0.08f))
                                        .padding(horizontal = 12.dp, vertical = 10.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Info,
                                            contentDescription = null,
                                            tint = RedPrimary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Waktu sekarang: ",
                                            fontSize = 12.sp,
                                            color = TextSecondary
                                        )
                                        Text(
                                            text = "$currentTimeString WIB",
                                            fontSize = 12.sp,
                                            color = RedPrimary,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        Text("Silakan ambil foto selama rapat berlangsung.", fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(12.dp))

                        if (imageUri == null) {
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
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.CameraAlt, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text("Ambil Foto Absensi", fontSize = 16.sp)
                            }
                        } else {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                            ) {
                                Column {
                                    AsyncImage(
                                        model = imageUri,
                                        contentDescription = "Preview Foto Absensi",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(200.dp),
                                        contentScale = ContentScale.Crop
                                    )

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        OutlinedButton(
                                            onClick = {
                                                imageUri = null
                                                photoFile = null
                                            },
                                            modifier = Modifier.weight(1f),
                                            colors = ButtonDefaults.outlinedButtonColors(
                                                contentColor = PrimaryGreen
                                            ),
                                            border = BorderStroke(1.dp, PrimaryGreen)
                                        ) {
                                            Icon(Icons.Default.Refresh, contentDescription = null)
                                            Spacer(Modifier.width(4.dp))
                                            Text("Ambil Ulang")
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
                                                        
                                                        // Simpan timestamp dari response API
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
                                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                                            modifier = Modifier.weight(2f)
                                        ) {
                                            Icon(Icons.Default.Save, contentDescription = null)
                                            Spacer(Modifier.width(4.dp))
                                            Text("Simpan Absensi", color = Color.White)
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

@Composable
fun RapatDetailInfoRow(icon: ImageVector, text: String, tint: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 3.dp)
    ) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, fontSize = 14.sp, color = TextPrimary)
    }
}
