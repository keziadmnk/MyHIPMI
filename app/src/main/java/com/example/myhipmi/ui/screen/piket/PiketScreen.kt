package com.example.myhipmi.ui.screen.piket

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudUpload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.myhipmi.ui.components.MyHipmiTopBar

import com.example.myhipmi.ui.screen.home.BottomNavBarContainer
import com.example.myhipmi.ui.theme.*
import com.example.myhipmi.data.local.UserSessionManager
import com.example.myhipmi.data.remote.retrofit.ApiConfig
import com.example.myhipmi.data.remote.response.AbsenPiketData
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.launch

@Composable
fun PiketScreen(navController: NavHostController) {

    val context = LocalContext.current
    val sessionManager = remember { UserSessionManager(context) }
    val loggedInUserName = sessionManager.getNamaPengurus()
    val loggedInUserId = sessionManager.getIdPengurus()
    val apiService = remember { ApiConfig.getApiService() }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var hariPiket by remember { mutableStateOf<String?>(null) }
    var isLoadingHariPiket by remember { mutableStateOf(true) }
    var riwayatPiket by remember { mutableStateOf<List<AbsenPiketData>>(emptyList()) }
    var isLoadingRiwayat by remember { mutableStateOf(false) }
    LaunchedEffect(loggedInUserId) {
        if (loggedInUserId != null) {
            isLoadingHariPiket = true
            scope.launch {
                try {
                    val response = apiService.getPengurusById(loggedInUserId)
                    if (response.isSuccessful && response.body()?.success == true) {
                        val pengurus = response.body()?.data
                        hariPiket = pengurus?.jadwalPiket?.hariPiket
                    } else {
                        snackbarHostState.showSnackbar("Gagal memuat data pengurus")
                    }
                } catch (e: Exception) {
                    snackbarHostState.showSnackbar("Error: ${e.message}")
                } finally {
                    isLoadingHariPiket = false
                }
            }
        } else {
            isLoadingHariPiket = false
        }
    }
    val tanggalHariIniFilter = remember {
        val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        outputFormat.format(Date())
    }
    val hariIni = remember {
        val dayFormat = SimpleDateFormat("EEEE", Locale("id", "ID"))
        dayFormat.format(Date())
    }
    val isHariPiketSesuai = remember(hariPiket, hariIni) {
        hariPiket != null && hariPiket.equals(hariIni, ignoreCase = true)
    }
    val sudahAbsenHariIni = remember(riwayatPiket, tanggalHariIniFilter) {
        riwayatPiket.any { it.tanggalAbsen == tanggalHariIniFilter }
    }
    val isButtonEnabled = isHariPiketSesuai && !sudahAbsenHariIni
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

    fun loadRiwayatPiket() {
        if (loggedInUserId != null) {
            isLoadingRiwayat = true
            scope.launch {
                try {
                    val response = apiService.getAbsenPiketByPengurus(loggedInUserId)
                    if (response.isSuccessful && response.body()?.success == true) {
                        val allData = response.body()?.data ?: emptyList()
                        riwayatPiket = allData.filter { it.tanggalAbsen == tanggalHariIniFilter }
                    } else {
                        snackbarHostState.showSnackbar("Gagal memuat riwayat piket")
                    }
                } catch (e: Exception) {
                    snackbarHostState.showSnackbar("Error: ${e.message}")
                } finally {
                    isLoadingRiwayat = false
                }
            }
        }
    }
    LaunchedEffect(loggedInUserId) {
        loadRiwayatPiket()
    }

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    LaunchedEffect(currentBackStackEntry) {
        if (currentBackStackEntry?.destination?.route == "piket" && loggedInUserId != null) {
            loadRiwayatPiket()
        }
    }

    val tanggalSekarang = remember {
        val dateFormat = SimpleDateFormat("EEEE, d MMMM yyyy", Locale("id", "ID"))
        dateFormat.format(Date())
    }

    Box(Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                MyHipmiTopBar(
                    title = "Jadwal Piket",
                    onBackClick = { navController.popBackStack() }
                )
            },
            bottomBar = {
                BottomNavBarContainer(
                    navController = navController,
                    onHome = { navController.navigate("home") },
                    onKas = { navController.navigate("kas") },
                    onRapat = { navController.navigate("rapat") },
                    onPiket = { /* already here */ },
                    onEvent = { navController.navigate("event") }
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        White

                    )
                    .padding(innerPadding)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(bottom = 96.dp)
                ) {
                    item { Spacer(Modifier.height(12.dp)) }

                    item {
                        Text(
                            text = "Jadwal Piket",
                            color = TextPrimary,
                            style = MaterialTheme.typography.headlineSmall,
                            fontSize = 22.sp
                        )
                        Spacer(Modifier.height(12.dp))
                        PiketHariCard(
                            hariPiket = hariPiket ?: "Belum ditentukan",
                            isLoading = isLoadingHariPiket
                        )
                        
                        Spacer(Modifier.height(12.dp))
                        TodayPiketCard(
                            title = "Piket Hari Ini",
                            tanggal = tanggalSekarang,
                            isEnabled = isButtonEnabled,
                            isHariSesuai = isHariPiketSesuai,
                            sudahAbsen = sudahAbsenHariIni,
                            hariPiket = hariPiket,
                            onUploadClick = {
                                if (isButtonEnabled) {
                                    navController.navigate("piket/upload")
                                }
                            }
                        )

                        Spacer(Modifier.height(16.dp))
                    }
                    item {
                        Text(
                            text = "Riwayat Piket",
                            color = TextPrimary,
                            style = MaterialTheme.typography.titleLarge,
                            fontSize = 18.sp,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                        )
                        Spacer(Modifier.height(12.dp))
                    }
                    if (isLoadingRiwayat) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = GreenPrimary)
                            }
                        }
                    } else if (riwayatPiket.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Belum ada riwayat piket",
                                    color = TextSecondary,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    } else {
                        items(riwayatPiket) { absen ->
                            PiketHistoryCard(
                                tanggal = formatTanggal(absen.tanggalAbsen),
                                deskripsi = absen.keterangan,
                                status = "Sudah Absen",
                                onClick = {
                                    navController.navigate("piket/detail/${absen.idAbsenPiket}")
                                }
                            )
                            Spacer(Modifier.height(12.dp))
                        }
                    }

                    item { Spacer(Modifier.height(8.dp)) }
                }
            }
        }

    }
}

@Composable
private fun PiketHariCard(
    hariPiket: String,
    isLoading: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(14.dp),
                clip = false
            ),
        colors = CardDefaults.cardColors(containerColor = CardGreen),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = "Piket Hari",
                color = DarkGreen,
                style = MaterialTheme.typography.labelMedium
            )
            Spacer(Modifier.height(6.dp))
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = GreenPrimary
                )
            } else {
                Text(
                    text = hariPiket,
                    color = TextPrimary,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@Composable
private fun TodayPiketCard(
    title: String,
    tanggal: String,
    isEnabled: Boolean,
    isHariSesuai: Boolean,
    sudahAbsen: Boolean,
    hariPiket: String?,
    onUploadClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(14.dp),
                clip = false
            ),
        colors = CardDefaults.cardColors(containerColor = CardGreen),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = title,
                color = DarkGreen,
                style = MaterialTheme.typography.labelMedium
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = tanggal,
                color = TextPrimary,
                style = MaterialTheme.typography.titleMedium
            )
            if (!isEnabled) {
                Spacer(Modifier.height(8.dp))
                val pesanInfo = when {
                    sudahAbsen -> "✓ Anda sudah mengisi absen hari ini"
                    !isHariSesuai && hariPiket != null -> "Hari piket Anda: $hariPiket"
                    else -> "Tidak dapat mengisi absen"
                }
                Text(
                    text = pesanInfo,
                    color = if (sudahAbsen) GreenPrimary else TextSecondary,
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 12.sp
                )
            }
            
            Spacer(Modifier.height(12.dp))
            OutlinedButton(
                onClick = onUploadClick,
                enabled = isEnabled,
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, if (isEnabled) GreenPrimary else TextSecondary.copy(alpha = 0.3f)),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (isEnabled) SecondaryGreen else TextSecondary.copy(alpha = 0.1f),
                    contentColor = if (isEnabled) GreenDark else TextSecondary,
                    disabledContainerColor = TextSecondary.copy(alpha = 0.1f),
                    disabledContentColor = TextSecondary
                ),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Icon(Icons.Outlined.CloudUpload, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Isi Absensi Piket", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Composable
private fun PiketHistoryCard(
    tanggal: String,
    deskripsi: String,
    status: String,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(14.dp),
                clip = false
            )
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 14.dp, end = 12.dp, bottom = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = tanggal,
                    color = TextPrimary,
                    style = MaterialTheme.typography.titleSmall
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = deskripsi,
                    color = TextSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            StatusChip(text = status)
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
                .clip(CircleShape)
                .background(GreenPrimary)
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text = text,
            color = GreenPrimary,
            style = MaterialTheme.typography.labelMedium
        )
    }
}

