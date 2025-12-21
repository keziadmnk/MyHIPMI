package com.example.myhipmi.ui.screen.event

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.net.Uri
import android.view.PixelCopy.request
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.example.myhipmi.utils.FileUtil
import com.example.myhipmi.utils.toPlainRequestBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody

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
                        
                        // Validasi: Cek apakah tanggal di masa lalu
                        val today = Calendar.getInstance()
                        today.set(Calendar.HOUR_OF_DAY, 0)
                        today.set(Calendar.MINUTE, 0)
                        today.set(Calendar.SECOND, 0)
                        today.set(Calendar.MILLISECOND, 0)
                        
                        selectedCalendar.set(Calendar.HOUR_OF_DAY, 0)
                        selectedCalendar.set(Calendar.MINUTE, 0)
                        selectedCalendar.set(Calendar.SECOND, 0)
                        selectedCalendar.set(Calendar.MILLISECOND, 0)
                        
                        if (selectedCalendar.before(today)) {
                            Toast.makeText(context, "Tanggal event tidak boleh di masa lalu", Toast.LENGTH_SHORT).show()
                        } else {
                            tanggal = SimpleDateFormat(
                                "yyyy-MM-dd",
                                Locale.getDefault()
                            ).format(selectedCalendar.time)
                        }
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                ).apply {
                    // Set minimum date to today
                    datePicker.minDate = System.currentTimeMillis()
                }
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
                                icon = Icons.Default.Event,
                                isRequired = true
                            )
                            ModernTextField(
                                label = "Tanggal",
                                value = if (tanggal.isNotBlank()) tanggal else "Pilih Tanggal",
                                onValueChange = { },
                                icon = Icons.Default.CalendarMonth,
                                readOnly = true,
                                onClick = { datePickerDialog.show() },
                                isRequired = true
                            )
                            ModernTextField(
                                label = "Waktu",
                                value = if (waktu.isNotBlank()) waktu else "Pilih Waktu",
                                onValueChange = { },
                                icon = Icons.Default.AccessTime,
                                readOnly = true,
                                onClick = { timePickerDialog.show() },
                                isRequired = true
                            )
                            ModernTextField(
                                label = "Tempat",
                                value = tempat,
                                onValueChange = { tempat = it },
                                icon = Icons.Default.LocationOn,
                                isRequired = true
                            )
                            ModernTextField(
                                label = "Dresscode",
                                value = dresscode,
                                onValueChange = { dresscode = it },
                                icon = Icons.Default.Checkroom,
                                isRequired = true
                            )
                            ModernTextField(
                                label = "Penyelenggara",
                                value = penyelenggara,
                                onValueChange = { penyelenggara = it },
                                icon = Icons.Default.Person,
                                isRequired = true
                            )
                            ModernTextField(
                                label = "Contact Person",
                                value = contactPerson,
                                onValueChange = { contactPerson = it },
                                icon = Icons.Default.Phone,
                                isRequired = true
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
                            ModernFileDragAndDropArea(
                                onClick = { filePickerLauncher.launch("image/*") },
                                fileUri = fileUri
                            )
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
                                        android.util.Log.d("AddEventScreen", "Button clicked!")
                                        // Validasi input wajib
                                        errorMessage = null
                                        successMessage = null
                                        
                                        android.util.Log.d("AddEventScreen", "Validating inputs...")
                                        android.util.Log.d("AddEventScreen", "namaEvent: '$namaEvent', tanggal: '$tanggal', waktu: '$waktu', tempat: '$tempat', penyelenggara: '$penyelenggara'")
                                        
                                        if (namaEvent.isBlank() || tanggal.isBlank() || waktu.isBlank() || tempat.isBlank() || 
                                            dresscode.isBlank() || penyelenggara.isBlank() || contactPerson.isBlank()) {
                                            errorMessage = "Semua field wajib diisi kecuali deskripsi"
                                            android.util.Log.d("AddEventScreen", "Validation failed: missing required fields")
                                            return@Button
                                        }

                                        // Validasi id_pengurus
                                        if (idPengurus == null) {
                                            errorMessage = "Anda belum login. Silakan login terlebih dahulu."
                                            android.util.Log.d("AddEventScreen", "Validation failed: idPengurus is null")
                                            return@Button
                                        }
                                        
                                        android.util.Log.d("AddEventScreen", "Validation passed, starting API call...")


                                        coroutineScope.launch {
                                            isLoading = true
                                            try {
                                                android.util.Log.d("AddEventScreen", "Starting event creation...")
                                                android.util.Log.d("AddEventScreen", "File URI: $fileUri")
                                                
                                                val posterPart = fileUri?.let {
                                                    try {
                                                        android.util.Log.d("AddEventScreen", "Converting URI to File...")
                                                        val file = FileUtil.from(context, it)
                                                        android.util.Log.d("AddEventScreen", "File created: ${file.name}, size: ${file.length()} bytes")
                                                        
                                                        val requestFile = file
                                                            .asRequestBody("image/*".toMediaType())

                                                        val part = MultipartBody.Part.createFormData(
                                                            "poster",
                                                            file.name,
                                                            requestFile
                                                        )
                                                        android.util.Log.d("AddEventScreen", "MultipartBody.Part created successfully")
                                                        part
                                                    } catch (e: Exception) {
                                                        android.util.Log.e("AddEventScreen", "Error creating file part: ${e.message}", e)
                                                        null
                                                    }
                                                }
                                                
                                                android.util.Log.d("AddEventScreen", "Poster part: ${if (posterPart != null) "Created" else "Null"}")

                                                val response = apiService.createEvent(
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

                                                android.util.Log.d("AddEventScreen", "Response code: ${response.code()}, isSuccessful: ${response.isSuccessful()}")

                                                if (response.isSuccessful) {
                                                    val responseBody = response.body()
                                                    val posterUrl = responseBody?.event?.posterUrl
                                                    android.util.Log.d("AddEventScreen", "Event created successfully!")
                                                    android.util.Log.d("AddEventScreen", "Poster URL from response: $posterUrl")
                                                    
                                                    if (posterUrl != null) {
                                                        android.util.Log.d("AddEventScreen", "✅ Foto berhasil disimpan ke database: $posterUrl")
                                                    } else {
                                                        android.util.Log.w("AddEventScreen", "⚠️ Poster URL null - foto mungkin tidak dikirim")
                                                    }
                                                    
                                                    successMessage = "Event berhasil ditambahkan${if (posterUrl != null) " dengan foto" else ""}"
                                                    Toast.makeText(context, "Event berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                                                    delay(1000)
                                                    navController.navigate("event") {

                                                        popUpTo("add_event") { inclusive = true }
                                                        launchSingleTop = true
                                                    }
                                                } else {
                                                    val errorBody = response.errorBody()?.string()
                                                    android.util.Log.e("AddEventScreen", "❌ Error response: ${response.code()}, body: $errorBody")
                                                    errorMessage = errorBody?.takeIf { it.isNotBlank() }
                                                        ?: "Gagal menambahkan event (${response.code()})"
                                                }

                                            } catch (e: Exception) {
                                                android.util.Log.e("AddEventScreen", "Exception occurred: ${e.message}", e)
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
                                    enabled = !isLoading
                                ) {
                                    if (isLoading) {
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

            MenuDrawer(
                isVisible = isMenuVisible,
                onDismiss = { isMenuVisible = false },
                userName = sessionManager.getNamaPengurus() ?: "Pengurus",
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


    @Composable
    fun ModernFileDragAndDropArea(
        onClick: () -> Unit,
        fileUri: Uri? = null
    ) {
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
                    color = if (fileUri != null) GreenPrimary else GreenPrimary.copy(alpha = 0.3f),
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
                if (fileUri != null) {

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.LightGray.copy(alpha = 0.2f))
                    ) {
                        AsyncImage(
                            model = fileUri,
                            contentDescription = "Preview Gambar",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

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
    }


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
            onClick: (() -> Unit)? = null,
            isRequired: Boolean = false
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            ) {
                Row {
                    Text(
                        text = label,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF374151),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    if (isRequired) {
                        Text(
                            text = " *",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Red,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                }


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
                            enabled = false,
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
