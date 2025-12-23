package com.example.myhipmi.ui.screen.event

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.myhipmi.data.local.UserSessionManager
import com.example.myhipmi.data.remote.retrofit.ApiConfig
import com.example.myhipmi.ui.components.MyHipmiTopBar

import com.example.myhipmi.ui.theme.GreenPrimary
import com.example.myhipmi.ui.theme.White
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import com.example.myhipmi.utils.FileUtil
import com.example.myhipmi.utils.toPlainRequestBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody

@Composable
fun EditEventScreen(navController: NavHostController, eventId: Int) {

    val context = LocalContext.current
    val sessionManager = remember { UserSessionManager(context) }
    val calendar = remember { Calendar.getInstance() }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                MyHipmiTopBar(
                    title = "Edit Event",
                    onBackClick = { navController.popBackStack() }
                )
            },

            ) { innerPadding ->
            var namaEvent by remember { mutableStateOf("") }
            var tanggal by remember { mutableStateOf("") }
            var waktu by remember { mutableStateOf("") }
            var tempat by remember { mutableStateOf("") }
            var dresscode by remember { mutableStateOf("") }
            var penyelenggara by remember { mutableStateOf("") }
            var contactPerson by remember { mutableStateOf("") }
            var deskripsi by remember { mutableStateOf("") }
            var currentPosterUrl by remember { mutableStateOf<String?>(null) }

            var isVisible by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) {
                delay(100)
                isVisible = true
            }
            var isLoading by remember { mutableStateOf(true) }
            var isSubmitting by remember { mutableStateOf(false) }
            var errorMessage by remember { mutableStateOf<String?>(null) }
            var successMessage by remember { mutableStateOf<String?>(null) }
            val coroutineScope = rememberCoroutineScope()
            val apiService = remember { ApiConfig.getApiService() }
            var fileUri by remember { mutableStateOf<Uri?>(null) }


            val idPengurus = remember { sessionManager.getIdPengurus() }
            LaunchedEffect(eventId) {
                if (eventId > 0) {
                    isLoading = true
                    errorMessage = null
                    try {
                        val response = apiService.getEventById(eventId)

                        if (response.isSuccessful) {
                            val event = response.body()?.event
                            if (event != null) {
                                namaEvent = event.namaEvent
                                tanggal = event.tanggal
                                waktu = event.waktu.substring(0, 5)
                                tempat = event.tempat
                                dresscode = event.dresscode ?: ""
                                penyelenggara = event.penyelenggara
                                contactPerson = event.contactPerson ?: ""
                                deskripsi = event.deskripsi ?: ""
                                currentPosterUrl = event.posterUrl
                            } else {
                                errorMessage = "Data event tidak ditemukan."
                            }
                        } else {
                            errorMessage = "Gagal memuat data event (${response.code()})"
                        }
                    } catch (e: Exception) {
                        errorMessage = "Koneksi gagal: ${e.localizedMessage ?: e.message}"
                    } finally {
                        isLoading = false
                    }
                } else {
                    isLoading = false
                    errorMessage = "ID Event tidak valid."
                }
            }

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
                        waktu = SimpleDateFormat("HH:mm", Locale.getDefault()).format(selectedCalendar.time)
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
                )
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = GreenPrimary)
                    Text("Memuat data event...", modifier = Modifier.padding(top = 64.dp), color = GreenPrimary)
                }
            } else {
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
                                    text = "Edit Event", // <-- Ganti teks header
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1F2937),
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                                Text(
                                    text = "Ubah informasi event di bawah ini",
                                    fontSize = 14.sp,
                                    color = Color(0xFF6B7280)
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                            }
                        }
                    }

                    item {
                    }
                    item {
                    }
                    item {
                        AnimatedVisibility(
                            visible = isVisible,
                            enter = fadeIn(animationSpec = tween(400, delayMillis = 100)) +
                                    slideInVertically(initialOffsetY = { 30 })
                        ) {
                            Column {
                                ModernTextField(label = "Nama Event", value = namaEvent, onValueChange = { namaEvent = it }, icon = Icons.Default.Event)
                                ModernTextField(label = "Tanggal", value = if (tanggal.isNotBlank()) tanggal else "Pilih Tanggal", onValueChange = { }, icon = Icons.Default.CalendarMonth, readOnly = true, onClick = { datePickerDialog.show() })
                                ModernTextField(label = "Waktu", value = if (waktu.isNotBlank()) waktu else "Pilih Waktu", onValueChange = { }, icon = Icons.Default.AccessTime, readOnly = true, onClick = { timePickerDialog.show() })
                                ModernTextField(label = "Tempat", value = tempat, onValueChange = { tempat = it }, icon = Icons.Default.LocationOn)
                                ModernTextField(label = "Dresscode", value = dresscode, onValueChange = { dresscode = it }, icon = Icons.Default.Checkroom)
                                ModernTextField(label = "Penyelenggara", value = penyelenggara, onValueChange = { penyelenggara = it }, icon = Icons.Default.Person)
                                ModernTextField(label = "Contact Person", value = contactPerson, onValueChange = { contactPerson = it }, icon = Icons.Default.Phone)
                                ModernTextField(label = "Deskripsi", value = deskripsi, onValueChange = { deskripsi = it }, icon = Icons.Default.Description, singleLine = false, minLines = 3)
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
                                // Panggil versi MODIFIKASI dari komponen ini
                                ModernFileDragAndDropArea(
                                    onClick = { filePickerLauncher.launch("image/*") },
                                    fileUri = fileUri, // Poster baru jika dipilih
                                    currentPosterUrl = currentPosterUrl // Poster lama (URL)
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                            }
                        }
                    }

                    // Tombol "Simpan Perubahan"
                    item {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedButton(
                                onClick = { navController.popBackStack() },
                                enabled = !isSubmitting, // Ganti isLoading menjadi isSubmitting
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

                                    if (idPengurus == null) {
                                        errorMessage = "Anda belum login. Silakan login terlebih dahulu."
                                        return@Button
                                    }

                                    coroutineScope.launch {
                                        isSubmitting = true // <-- Gunakan isSubmitting
                                        try {

                                            val posterPart = fileUri?.let {
                                                try {
                                                    val file = FileUtil.from(context, it)
                                                    val requestFile = file.asRequestBody("image/*".toMediaType())

                                                    MultipartBody.Part.createFormData("poster", file.name, requestFile)
                                                } catch (e: Exception) {
                                                    null
                                                }
                                            }

                                            // PANGGIL updateEvent()
                                            val response = apiService.updateEvent(
                                                eventId, // KIRIM ID EVENT
                                                idPengurus.toString().toPlainRequestBody(),
                                                namaEvent.toPlainRequestBody(),
                                                tanggal.toPlainRequestBody(),
                                                waktu.toPlainRequestBody(),
                                                tempat.toPlainRequestBody(),
                                                penyelenggara.toPlainRequestBody(),
                                                dresscode.takeIf { it.isNotBlank() }?.toPlainRequestBody(),
                                                contactPerson.takeIf { it.isNotBlank() }?.toPlainRequestBody(),
                                                deskripsi.takeIf { it.isNotBlank() }?.toPlainRequestBody(),
                                                posterPart
                                            )


                                            if (response.isSuccessful) {
                                                successMessage = "Event berhasil diupdate!"
                                                Toast.makeText(context, "Event berhasil diupdate", Toast.LENGTH_SHORT).show()
                                                delay(1000)
                                                // Kembali ke halaman Event List
                                                navController.navigate("event") {
                                                    popUpTo("edit_event/$eventId") { inclusive = true }
                                                    launchSingleTop = true
                                                }
                                            } else {
                                                val errorBody = response.errorBody()?.string()
                                                errorMessage = errorBody?.takeIf { it.isNotBlank() }
                                                    ?: "Gagal mengupdate event (${response.code()})"
                                            }

                                        } catch (e: Exception) {
                                            errorMessage = "Terjadi kesalahan koneksi: ${e.localizedMessage ?: "Unknown error"}"
                                        } finally {
                                            isSubmitting = false
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
                                enabled = !isSubmitting
                            ) {
                                if (isSubmitting) {
                                    CircularProgressIndicator(
                                        color = White,
                                        strokeWidth = 2.dp,
                                        modifier = Modifier.size(24.dp)
                                    )
                                } else {
                                    Icon(
                                        Icons.Default.Edit, // <-- Ganti ikon
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Simpan Perubahan", // <-- Ganti teks
                                        color = White,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }


                }
            }

            // Menu Drawer (sama)
            // ...
        }
    }
}

// ================================================================================================
// MODIFIKASI HELPER COMPOSABLE (Tempatkan di EditEventScreen.kt atau file yang dapat diakses)
// ================================================================================================
// SALIN DAN MODIFIKASI ModernFileDragAndDropArea dari AddEventScreen.kt
// (Jika fungsi ini bukan global, Anda harus menyertakan versi ini di EditEventScreen.kt)

@Composable
fun ModernFileDragAndDropArea(
    onClick: () -> Unit,
    fileUri: Uri? = null,
    currentPosterUrl: String? = null // ARGUMEN BARU UNTUK URL LAMA
) {
    // Tentukan gambar yang akan ditampilkan: Poster baru (fileUri) > Poster lama (currentPosterUrl) > Default
    val displayImage: Any? = fileUri ?: currentPosterUrl?.takeIf { it.isNotBlank() }

    Surface(
        onClick = onClick,
        // ... (styling sama)
    ) {
        Column(
            // ... (modifier sama)
        ) {
            if (displayImage != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.LightGray.copy(alpha = 0.2f))
                ) {
                    AsyncImage(
                        model = displayImage, // Gunakan displayImage (bisa Uri lokal atau URL)
                        contentDescription = "Preview Gambar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    // ... (Ikon edit sama)
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.3f))
                            .clickable(onClick = onClick)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Ubah gambar",
                            tint = Color.White,
                            modifier = Modifier
                                .size(32.dp)
                                .align(Alignment.Center)
                        )
                    }
                }
            } else {
                // ... (Default upload area sama)
            }
        }
    }
}

// SALIN SEMUA FUNGSI HELPER LAINNYA (ModernTextField, dll.) dari AddEventScreen.kt ke EditEventScreen.kt
// (KECUALI jika sudah didefinisikan sebagai global/di-impor)
// ... (ModernTextField, dll.)