package com.example.myhipmi.ui.screen.event

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.myhipmi.data.local.UserSessionManager
import com.example.myhipmi.data.remote.request.EventRequest
import com.example.myhipmi.data.remote.retrofit.ApiConfig
import com.example.myhipmi.ui.components.MyHipmiTopBar
import com.example.myhipmi.ui.components.MenuDrawer
import com.example.myhipmi.ui.theme.GreenPrimary
import com.example.myhipmi.ui.theme.White
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import android.widget.Toast

@Composable
fun AddEventScreen(navController: NavHostController) {
    var isMenuVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val sessionManager = remember { UserSessionManager(context) }
    val calendar = remember { Calendar.getInstance() }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                MyHipmiTopBar(
                    title = "Event",
                    onBackClick = { navController.popBackStack() },
                    onMenuClick = { isMenuVisible = true }
                )
            },

            ) { innerPadding ->
            // State variables
            var namaEvent by remember { mutableStateOf("") }
            var tanggal by remember { mutableStateOf("") }
            var waktu by remember { mutableStateOf("") }
            var tempat by remember { mutableStateOf("") }
            var dresscode by remember { mutableStateOf("") }
            var penyelenggara by remember { mutableStateOf("") }
            var contactPerson by remember { mutableStateOf("") }
            var deskripsi by remember { mutableStateOf("") }

            var isVisible by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) {
                delay(100)
                isVisible = true
            }
            var isLoading by remember { mutableStateOf(false) }
            var errorMessage by remember { mutableStateOf<String?>(null) }
            var successMessage by remember { mutableStateOf<String?>(null) }
            val coroutineScope = rememberCoroutineScope()
            val apiService = remember { ApiConfig.getApiService() }
            var fileUri by remember { mutableStateOf<Uri?>(null) }
            
            // Ambil id_pengurus dari session
            val idPengurus = remember { sessionManager.getIdPengurus() }
            val filePickerLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.GetContent()
            ) { uri: Uri? ->
                fileUri = uri
            }
            val datePickerDialog = remember {
                DatePickerDialog(
                    context,
                    { _, year, month, dayOfMonth ->
                        val selectedCalendar = Calendar.getInstance()
                        selectedCalendar.set(year, month, dayOfMonth)
                        // Format tanggal menjadi YYYY-MM-DD (format standar DB)
                        tanggal = SimpleDateFormat(
                            "yyyy-MM-dd",
                            Locale.getDefault()
                        ).format(selectedCalendar.time)
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                )
            }

            val timePickerDialog = remember {
                TimePickerDialog(
                    context,
                    { _, hourOfDay, minute ->
                        val selectedCalendar = Calendar.getInstance()
                        selectedCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        selectedCalendar.set(Calendar.MINUTE, minute)
                        // Format waktu menjadi HH:MM
                        waktu = SimpleDateFormat("HH:mm", Locale.getDefault()).format(selectedCalendar.time)
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true // format 24 jam
                )
            }
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFFFFFFF))
                    .padding(innerPadding)
                    .padding(horizontal = 20.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                // Header
                item {
                    AnimatedVisibility(
                        visible = isVisible,
                        enter = fadeIn() + slideInVertically(initialOffsetY = { -20 })
                    ) {
                        Column {
                            Text(
                                text = "Tambah Event Baru",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1F2937),
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            Text(
                                text = "Lengkapi informasi event di bawah ini",
                                fontSize = 14.sp,
                                color = Color(0xFF6B7280)
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }
                }

                // Error Message
                item {
                    AnimatedVisibility(
                        visible = !errorMessage.isNullOrBlank(),
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .background(
                                    color = Color(0xFFFEE2E2),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = null,
                                tint = Color(0xFFDC2626),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = errorMessage ?: "",
                                color = Color(0xFFDC2626),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                // Success Message
                item {
                    AnimatedVisibility(
                        visible = !successMessage.isNullOrBlank(),
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .background(
                                    color = Color(0xFFD1FAE5),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = GreenPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = successMessage ?: "",
                                color = GreenPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                // Input Fields with animation
                item {
                    AnimatedVisibility(
                        visible = isVisible,
                        enter = fadeIn(animationSpec = tween(400, delayMillis = 100)) +
                                slideInVertically(initialOffsetY = { 30 })
                    ) {
                        Column {
                            ModernTextField(
                                label = "Nama Event",
                                value = namaEvent,
                                onValueChange = { namaEvent = it },
                                icon = Icons.Default.Event
                            )
                            ModernTextField(
                                label = "Tanggal",
                                value = if (tanggal.isNotBlank()) tanggal else "Pilih Tanggal",
                                onValueChange = { },
                                icon = Icons.Default.CalendarMonth,
                                readOnly = true,
                                onClick = { datePickerDialog.show() }
                            )
                            ModernTextField(
                                label = "Waktu",
                                value = if (waktu.isNotBlank()) waktu else "Pilih Waktu",
                                onValueChange = { },
                                icon = Icons.Default.AccessTime,
                                readOnly = true,
                                onClick = { timePickerDialog.show() }
                            )
                            ModernTextField(
                                label = "Tempat",
                                value = tempat,
                                onValueChange = { tempat = it },
                                icon = Icons.Default.LocationOn
                            )
                            ModernTextField(
                                label = "Dresscode",
                                value = dresscode,
                                onValueChange = { dresscode = it },
                                icon = Icons.Default.Checkroom
                            )
                            ModernTextField(
                                label = "Penyelenggara",
                                value = penyelenggara,
                                onValueChange = { penyelenggara = it },
                                icon = Icons.Default.Person
                            )
                            ModernTextField(
                                label = "Contact Person",
                                value = contactPerson,
                                onValueChange = { contactPerson = it },
                                icon = Icons.Default.Phone
                            )
                            ModernTextField(
                                label = "Deskripsi",
                                value = deskripsi,
                                onValueChange = { deskripsi = it },
                                icon = Icons.Default.Description,
                                singleLine = false,
                                minLines = 3
                            )
                        }
                    }
                }

                // Kolom Upload File
                item {
                    AnimatedVisibility(
                        visible = isVisible,
                        enter = fadeIn(animationSpec = tween(400, delayMillis = 200)) +
                                slideInVertically(initialOffsetY = { 30 })
                    ) {
                        Column {
                            Spacer(modifier = Modifier.height(8.dp))
                            ModernFileDragAndDropArea(onClick = { filePickerLauncher.launch("image/*") })
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }
                }

                // Tombol "Tambah"
                item {
                    AnimatedVisibility(
                        visible = isVisible,
                        enter = fadeIn(animationSpec = tween(400, delayMillis = 300)) +
                                slideInVertically(initialOffsetY = { 30 })
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedButton(
                                onClick = { navController.popBackStack() },
                                enabled = !isLoading,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(52.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = GreenPrimary
                                ),
                                border = ButtonDefaults.outlinedButtonBorder.copy(
                                    width = 1.5.dp
                                )
                            ) {
                                Text(
                                    text = "Batal",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            Button(
                                onClick = {
                                    // Validasi input wajib
                                    errorMessage = null
                                    successMessage = null
                                    
                                    if (namaEvent.isBlank() || tanggal.isBlank() || waktu.isBlank() || tempat.isBlank() || penyelenggara.isBlank()) {
                                        errorMessage = "Nama event, tanggal, waktu, tempat, dan penyelenggara wajib diisi"
                                        return@Button
                                    }

                                    // Validasi id_pengurus
                                    if (idPengurus == null) {
                                        errorMessage = "Anda belum login. Silakan login terlebih dahulu."
                                        return@Button
                                    }

                                    // Lakukan panggilan API di Coroutine
                                    coroutineScope.launch {
                                        isLoading = true
                                        try {
                                            val request = EventRequest(
                                                idPengurus = idPengurus,
                                                namaEvent = namaEvent,
                                                tanggal = tanggal,
                                                waktu = waktu,
                                                tempat = tempat,
                                                dresscode = dresscode.takeIf { it.isNotBlank() },
                                                penyelenggara = penyelenggara,
                                                contactPerson = contactPerson.takeIf { it.isNotBlank() },
                                                deskripsi = deskripsi.takeIf { it.isNotBlank() },
                                                posterUrl = fileUri?.toString()
                                            )

                                            val response = apiService.createEvent(request)

                                            if (response.isSuccessful) {
                                                successMessage = "Event berhasil ditambahkan"
                                                Toast.makeText(context, "Event berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                                                // Tunggu sebentar sebelum navigasi ke EventScreen
                                                delay(1000)
                                                // Navigasi langsung ke EventScreen
                                                // Hapus AddEventScreen dari back stack dan kembali ke EventScreen
                                                navController.navigate("event") {
                                                    // Hapus AddEventScreen dari back stack
                                                    popUpTo("add_event") { inclusive = true }
                                                    // Navigasi ke EventScreen
                                                    launchSingleTop = true
                                                }
                                            } else {
                                                val errorBody = response.errorBody()?.string()
                                                errorMessage = errorBody?.takeIf { it.isNotBlank() }
                                                    ?: "Gagal menambahkan event (${response.code()})"
                                            }

                                        } catch (e: Exception) {
                                            errorMessage = "Terjadi kesalahan koneksi: ${e.localizedMessage ?: "Unknown error"}"
                                            e.printStackTrace()
                                        } finally {
                                            isLoading = false
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(52.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = GreenPrimary
                                ),
                                shape = RoundedCornerShape(12.dp),
                                enabled = !isLoading // Disable saat loading
                            ) {
                                if (isLoading) { // Tampilkan loading indicator
                                    CircularProgressIndicator(
                                        color = White,
                                        strokeWidth = 2.dp,
                                        modifier = Modifier.size(24.dp)
                                    )
                                } else {
                                    Icon(
                                        Icons.Default.Add,
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Tambah Event",
                                        color = White,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
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
                userName = "Nagita Slavina",
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
                    // TODO: Handle logout
                }
            )
        }
    }
}

    // Modern File Upload Area
    @Composable
    fun ModernFileDragAndDropArea(onClick: () -> Unit) {
        Surface(
            onClick = onClick,
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .shadow(
                    elevation = 2.dp,
                    shape = RoundedCornerShape(16.dp)
                )
                .border(
                    width = 2.dp,
                    color = GreenPrimary.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(16.dp)
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(
                            color = GreenPrimary.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudUpload,
                        contentDescription = "Upload foto",
                        tint = GreenPrimary,
                        modifier = Modifier.size(32.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Upload Gambar Event",
                    color = Color(0xFF1F2937),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Klik atau tarik file ke sini",
                    color = Color(0xFF9CA3AF),
                    fontSize = 13.sp
                )
            }
        }
    }

        // Modern TextField Component
        @Composable
        fun ModernTextField(
            label: String,
            value: String,
            onValueChange: (String) -> Unit,
            icon: androidx.compose.ui.graphics.vector.ImageVector,
            placeholder: String = "",
            singleLine: Boolean = true,
            minLines: Int = 1,
            readOnly: Boolean = false,
            onClick: (() -> Unit)? = null
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            ) {
                Text(
                    text = label,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF374151),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Wrap dalam Box dengan clickable untuk field yang memiliki onClick
                if (onClick != null) {
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
                            onValueChange = { },
                            readOnly = true,
                            enabled = false, // Disable untuk mencegah keyboard muncul
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = {
                                Text(
                                    text = placeholder.ifEmpty { "Masukkan $label" },
                                    color = Color(0xFFD1D5DB)
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = GreenPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            singleLine = singleLine,
                            minLines = minLines,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GreenPrimary,
                                unfocusedBorderColor = Color(0xFFE5E7EB),
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedTextColor = Color(0xFF1F2937),
                                unfocusedTextColor = Color(0xFF1F2937),
                                cursorColor = GreenPrimary,
                                disabledTextColor = Color(0xFF1F2937),
                                disabledBorderColor = Color(0xFFE5E7EB),
                                disabledContainerColor = Color.White
                            )
                        )
                    }
                } else {
                    OutlinedTextField(
                        value = value,
                        onValueChange = onValueChange,
                        readOnly = readOnly,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(
                                text = placeholder.ifEmpty { "Masukkan $label" },
                                color = Color(0xFFD1D5DB)
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = GreenPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        singleLine = singleLine,
                        minLines = minLines,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GreenPrimary,
                            unfocusedBorderColor = Color(0xFFE5E7EB),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedTextColor = Color(0xFF1F2937),
                            unfocusedTextColor = Color(0xFF1F2937),
                            cursorColor = GreenPrimary
                        )
                    )
                }
            }
        }


