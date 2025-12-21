package com.example.myhipmi.ui.screen.piket

import android.graphics.Bitmap
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.myhipmi.data.local.UserSessionManager
import com.example.myhipmi.data.remote.retrofit.ApiConfig
import com.example.myhipmi.data.remote.response.CreateAbsenPiketRequest
import com.example.myhipmi.ui.components.MenuDrawer
import com.example.myhipmi.ui.components.MyHipmiTopBar
import com.example.myhipmi.ui.screen.home.BottomNavBarContainer
import com.example.myhipmi.ui.theme.*
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailPiketScreen(
    navController: NavHostController
) {
    val context = LocalContext.current
    val sessionManager = remember { UserSessionManager(context) }
    val loggedInUserId = sessionManager.getIdPengurus()
    val loggedInUserName = sessionManager.getNamaPengurus()
    val apiService = remember { ApiConfig.getApiService() }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    
    var isMenuVisible by remember { mutableStateOf(false) }
    var deskripsi by remember { mutableStateOf("") }
    var jamMulai by remember { mutableStateOf("") }
    var jamSelesai by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    // State untuk id_jadwal_piket (akan diambil dari API atau session)
    var idJadwalPiket by remember { mutableStateOf<Int?>(null) }

    // Camera / preview states
    var capturedImage by remember { mutableStateOf<Bitmap?>(null) }
    var isImagePreviewVisible by remember { mutableStateOf(false) }
    
    // Time Picker states
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }
    val startTimePickerState = rememberTimePickerState()
    val endTimePickerState = rememberTimePickerState()
    
    // Tanggal hari ini (real time) untuk tampilan
    val tanggalHariIni = remember {
        val dateFormat = SimpleDateFormat("EEEE, d MMMM yyyy", Locale("id", "ID"))
        dateFormat.format(Date())
    }
    
    // Format tanggal untuk backend (YYYY-MM-DD) - menggunakan tanggal hari ini
    val tanggalFormatBackend = remember {
        val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        outputFormat.format(Date())
    }
    
    // Ambil id_jadwal_piket dari pengurus via API
    LaunchedEffect(loggedInUserId) {
        if (loggedInUserId != null) {
            scope.launch {
                try {
                    val response = apiService.getPengurusById(loggedInUserId)
                    if (response.isSuccessful && response.body()?.success == true) {
                        val pengurus = response.body()?.data
                        idJadwalPiket = pengurus?.idJadwalPiket
                        if (idJadwalPiket == null) {
                            scope.launch {
                                snackbarHostState.showSnackbar("Jadwal piket tidak ditemukan untuk pengurus ini")
                            }
                        }
                    }
                } catch (e: Exception) {
                    scope.launch {
                        snackbarHostState.showSnackbar("Error: ${e.message}")
                    }
                }
            }
        }
    }
    
    // Fungsi untuk convert Bitmap ke Base64
    fun bitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }
    
    // Fungsi untuk save absen piket
    fun saveAbsenPiket() {
        // Validasi semua field wajib
        if (deskripsi.isBlank()) {
            scope.launch {
                snackbarHostState.showSnackbar("Deskripsi harus diisi")
            }
            return
        }
        if (jamMulai.isBlank()) {
            scope.launch {
                snackbarHostState.showSnackbar("Jam mulai harus dipilih")
            }
            return
        }
        if (jamSelesai.isBlank()) {
            scope.launch {
                snackbarHostState.showSnackbar("Jam selesai harus dipilih")
            }
            return
        }
        if (capturedImage == null) {
            scope.launch {
                snackbarHostState.showSnackbar("Foto harus diambil")
            }
            return
        }
        if (loggedInUserId == null) {
            scope.launch {
                snackbarHostState.showSnackbar("Anda belum login. Silakan login terlebih dahulu.")
            }
            navController.navigate("login")
            return
        }
        if (idJadwalPiket == null) {
            scope.launch {
                snackbarHostState.showSnackbar("Jadwal piket tidak ditemukan")
            }
            return
        }
        
        isLoading = true
        errorMessage = null
        
        scope.launch {
            try {
                // Convert bitmap ke base64
                val fotoBase64 = bitmapToBase64(capturedImage!!)
                val fotoUrl = "data:image/jpeg;base64,$fotoBase64"
                
                // Parse jam dari format "HH:mm WIB" ke "HH:mm:ss"
                val jamMulaiFormatted = jamMulai.replace(" WIB", "").trim() + ":00"
                val jamSelesaiFormatted = jamSelesai.replace(" WIB", "").trim() + ":00"
                
                val request = CreateAbsenPiketRequest(
                    idPengurus = loggedInUserId,
                    idJadwalPiket = idJadwalPiket!!,
                    tanggalAbsen = tanggalFormatBackend,
                    jamMulai = jamMulaiFormatted,
                    jamSelesai = jamSelesaiFormatted,
                    keterangan = deskripsi,
                    fotoBukti = fotoUrl
                )
                
                val response = apiService.createAbsenPiket(request)
                
                if (response.isSuccessful && response.body()?.success == true) {
                    scope.launch {
                        snackbarHostState.showSnackbar("Absen piket berhasil disimpan")
                    }
                    // Navigate back to piket screen
                    navController.navigate("piket") {
                        popUpTo("piket") { inclusive = false }
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    errorMessage = errorBody ?: "Gagal menyimpan absen piket"
                    scope.launch {
                        snackbarHostState.showSnackbar(errorMessage ?: "Gagal menyimpan absen piket")
                    }
                }
            } catch (e: Exception) {
                errorMessage = "Error: ${e.message}"
                scope.launch {
                    snackbarHostState.showSnackbar(errorMessage ?: "Terjadi kesalahan")
                }
            } finally {
                isLoading = false
            }
        }
    }

    val takePicturePreviewLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            capturedImage = bitmap
            isImagePreviewVisible = true
        }
    }

    Scaffold(
        topBar = {
            MyHipmiTopBar(
                title = "Upload Absen Piket",
                onBackClick = { navController.popBackStack() },
                onMenuClick = { isMenuVisible = true }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        // hide bottom bar when showing image preview so focus is on the preview
        bottomBar = {
            if (!isImagePreviewVisible) {
                BottomNavBarContainer(
                    navController = navController,
                    onHome = { navController.navigate("home") },
                    onKas = { navController.navigate("kas") },
                    onRapat = { navController.navigate("rapat") },
                    onPiket = { /* already on Piket */ },
                    onEvent = { navController.navigate("event") }
                )
            }
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    White
                )
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
            ) {
                Spacer(Modifier.height(12.dp))

                // Kartu "Piket Hari Ini"
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 6.dp,
                            shape = RoundedCornerShape(14.dp),
                            clip = false
                        ),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = CardGreen),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            text = "Piket Hari Ini",
                            color = DarkGreen,
                            style = MaterialTheme.typography.labelMedium
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = tanggalHariIni,
                            color = TextPrimary,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Form Jam Mulai
                Row {
                    Text(
                        text = "Jam Mulai",
                        color = TextPrimary,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = " *",
                        color = Color.Red,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium
                    )
                }
                Spacer(Modifier.height(6.dp))
                PiketTimeField(
                    value = jamMulai,
                    placeholder = "Pilih jam mulai",
                    onClick = { showStartTimePicker = true }
                )
                
                Spacer(Modifier.height(12.dp))
                
                // Form Jam Selesai
                Row {
                    Text(
                        text = "Jam Selesai",
                        color = TextPrimary,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = " *",
                        color = Color.Red,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium
                    )
                }
                Spacer(Modifier.height(6.dp))
                PiketTimeField(
                    value = jamSelesai,
                    placeholder = "Pilih jam selesai",
                    onClick = { showEndTimePicker = true }
                )
                
                Spacer(Modifier.height(12.dp))

                // Label deskripsi
                Row {
                    Text(
                        text = "Deskripsi",
                        color = TextPrimary,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = " *",
                        color = Color.Red,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium
                    )
                }
                Spacer(Modifier.height(6.dp))

                // TextField deskripsi
                OutlinedTextField(
                    value = deskripsi,
                    onValueChange = { deskripsi = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                            tint = GreenPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GreenPrimary,
                        unfocusedBorderColor = Color(0xFFE5E7EB),
                        cursorColor = GreenPrimary,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedTextColor = Color(0xFF1F2937),
                        unfocusedTextColor = Color(0xFF1F2937)
                    ),
                    placeholder = {
                        Text("Tulis keterangan piket…", color = Color(0xFFD1D5DB))
                    }
                )

                Spacer(Modifier.height(18.dp))

                Text(
                    text = "Silakan ambil foto absensi piket petugas",
                    color = TextPrimary,
                    style = MaterialTheme.typography.labelLarge
                )
                Spacer(Modifier.height(10.dp))

                // Area unggah/ambil foto
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(SecondaryGreen),
                    contentAlignment = Alignment.Center
                ) {
                    // Border tipis seperti pada mockup
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clip(RoundedCornerShape(14.dp))
                            .background(SecondaryGreen)
                    )

                    // Tombol "Ambil foto" -> launch camera preview
                    Button(
                        onClick = {
                            // launch camera preview (TakePicturePreview)
                            takePicturePreviewLauncher.launch(null)
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GreenPrimary,
                            contentColor = TextLight
                        ),
                        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CameraAlt,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Ambil foto", style = MaterialTheme.typography.labelLarge)
                    }
                }

                Spacer(Modifier.height(24.dp))
                Spacer(Modifier.height(96.dp))
            }
            
            // Time Picker Dialogs
            if (showStartTimePicker) {
                TimePickerDialog(
                    onDismissRequest = { showStartTimePicker = false },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                val hour = startTimePickerState.hour
                                val minute = startTimePickerState.minute
                                jamMulai = String.format(Locale.forLanguageTag("id-ID"), "%02d:%02d WIB", hour, minute)
                                showStartTimePicker = false
                            }
                        ) {
                            Text("OK", color = GreenPrimary)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showStartTimePicker = false }) {
                            Text("Batal", color = GreenPrimary)
                        }
                    }
                ) {
                    TimePicker(
                        state = startTimePickerState,
                        colors = TimePickerDefaults.colors(
                            clockDialColor = GreenMain,
                            selectorColor = GreenPrimary,
                            periodSelectorSelectedContainerColor = GreenPrimary,
                            timeSelectorSelectedContainerColor = GreenPrimary
                        )
                    )
                }
            }
            
            if (showEndTimePicker) {
                TimePickerDialog(
                    onDismissRequest = { showEndTimePicker = false },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                val hour = endTimePickerState.hour
                                val minute = endTimePickerState.minute
                                jamSelesai = String.format(Locale.forLanguageTag("id-ID"), "%02d:%02d WIB", hour, minute)
                                showEndTimePicker = false
                            }
                        ) {
                            Text("OK", color = GreenPrimary)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showEndTimePicker = false }) {
                            Text("Batal", color = GreenPrimary)
                        }
                    }
                ) {
                    TimePicker(
                        state = endTimePickerState,
                        colors = TimePickerDefaults.colors(
                            clockDialColor = GreenMain,
                            selectorColor = GreenPrimary,
                            periodSelectorSelectedContainerColor = GreenPrimary,
                            timeSelectorSelectedContainerColor = GreenPrimary
                        )
                    )
                }
            }

            // Image preview bottom sheet overlay
            if (isImagePreviewVisible && capturedImage != null) {
                // dim background
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
                )

                // bottom sheet style card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                        colors = CardDefaults.cardColors(containerColor = White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Preview Foto Absen Piket",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimary
                            )
                            Spacer(Modifier.height(12.dp))

                            Image(
                                bitmap = capturedImage!!.asImageBitmap(),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(300.dp)
                                    .clip(RoundedCornerShape(12.dp)),
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop
                            )

                            Spacer(Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        // cancel preview and return to detail screen (no deletion)
                                        isImagePreviewVisible = false
                                        capturedImage = null
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary)
                                ) {
                                    Text("Batal")
                                }

                                Button(
                                    onClick = {
                                        isImagePreviewVisible = false
                                        saveAbsenPiket()
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = GreenPrimary, 
                                        contentColor = TextLight
                                    ),
                                    enabled = !isLoading
                                ) {
                                    if (isLoading) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(20.dp),
                                            color = TextLight,
                                            strokeWidth = 2.dp
                                        )
                                        Spacer(Modifier.width(8.dp))
                                    }
                                    Text("Simpan")
                                }
                            }

                            Spacer(Modifier.height(8.dp))
                        }
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
fun PiketTimeField(
    value: String,
    placeholder: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                onClick()
            }
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            enabled = false,
            placeholder = {
                Text(placeholder, color = Color(0xFFD1D5DB))
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.AccessTime,
                    contentDescription = null,
                    tint = GreenPrimary,
                    modifier = Modifier.size(20.dp)
                )
            },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = if (value.isBlank()) Color(0xFFD1D5DB) else Color(0xFF1F2937),
                disabledBorderColor = Color(0xFFE5E7EB),
                disabledContainerColor = Color.White,
                disabledLeadingIconColor = GreenPrimary
            )
        )
    }
}

@Composable
fun TimePickerDialog(
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    dismissButton: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = confirmButton,
        dismissButton = dismissButton,
        text = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                content()
            }
        },
        containerColor = White,
        shape = RoundedCornerShape(24.dp)
    )
}