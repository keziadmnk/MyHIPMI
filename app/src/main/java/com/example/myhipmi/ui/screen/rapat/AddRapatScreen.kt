package com.example.myhipmi.ui.screen.rapat

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myhipmi.ui.components.MyHipmiTopBar

import com.example.myhipmi.ui.theme.GreenMain
import com.example.myhipmi.ui.theme.GreenPrimary
import com.example.myhipmi.ui.theme.White
import com.example.myhipmi.ui.viewmodel.RapatViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.myhipmi.data.local.UserSessionManager
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRapatScreen(navController: NavHostController) {
    val viewModel: RapatViewModel = viewModel()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()
    val context = LocalContext.current
    val sessionManager = remember { UserSessionManager(context) }
    val loggedInUserId = sessionManager.getIdPengurus()
    val loggedInUserName = sessionManager.getNamaPengurus()
    var namaRapat by remember { mutableStateOf("") }
    var tanggal by remember { mutableStateOf("") }
    var dateSelectedMillis by remember { mutableStateOf(0L) }
    var startTime by remember { mutableStateOf("") }
    var endTime by remember { mutableStateOf("") }
    var lokasi by remember { mutableStateOf("") }
    var deskripsi by remember { mutableStateOf("") }

    var showDatePicker by remember { mutableStateOf(false) }
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }

    val currentTimeMillis = remember { System.currentTimeMillis() }
    val datePickerState = rememberDatePickerState(
        initialDisplayedMonthMillis = currentTimeMillis,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= currentTimeMillis - 86400000L
            }
        }
    )
    val startTimePickerState = rememberTimePickerState()
    val endTimePickerState = rememberTimePickerState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(100)
        isVisible = true
    }

    LaunchedEffect(successMessage, errorMessage) {
        successMessage?.let {
            navController.navigate("rapat") {
                popUpTo("rapat") { inclusive = true }
            }
        }
        errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                MyHipmiTopBar(
                    title = "Rapat",
                    onBackClick = { navController.popBackStack() }
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFFFFFF))
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            item {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { -20 })
                ) {
                    Column {
                        Text(
                            text = "Agenda Rapat Baru",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1F2937),
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Text(
                            text = "Lengkapi informasi rapat di bawah ini",
                            fontSize = 14.sp,
                            color = Color(0xFF6B7280)
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
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
            item {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(animationSpec = tween(400, delayMillis = 100)) +
                            slideInVertically(initialOffsetY = { 30 })
                ) {
                    Column {
                        ModernTextField(
                            label = "Nama Rapat",
                            value = namaRapat,
                            onValueChange = { namaRapat = it },
                            icon = Icons.Default.Event,
                            isRequired = true
                        )
                        ModernTextField(
                            label = "Tanggal",
                            value = if (tanggal.isNotBlank()) tanggal else "Pilih Tanggal",
                            onValueChange = { },
                            icon = Icons.Default.CalendarMonth,
                            readOnly = true,
                            onClick = { showDatePicker = true },
                            isRequired = true
                        )
                        ModernTextField(
                            label = "Jam Mulai",
                            value = if (startTime.isNotBlank()) startTime else "Pilih Jam Mulai",
                            onValueChange = { },
                            icon = Icons.Default.AccessTime,
                            readOnly = true,
                            onClick = { showStartTimePicker = true },
                            isRequired = true
                        )
                        ModernTextField(
                            label = "Jam Selesai",
                            value = if (endTime.isNotBlank()) endTime else "Pilih Jam Selesai",
                            onValueChange = { },
                            icon = Icons.Default.AccessTime,
                            readOnly = true,
                            onClick = { showEndTimePicker = true },
                            isRequired = true
                        )
                        ModernTextField(
                            label = "Lokasi",
                            value = lokasi,
                            onValueChange = { lokasi = it },
                            icon = Icons.Default.LocationOn,
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
            item {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(animationSpec = tween(400, delayMillis = 200)) +
                            slideInVertically(initialOffsetY = { 30 })
                ) {
                    Spacer(modifier = Modifier.height(8.dp))
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
                                if (namaRapat.isBlank()) {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Nama rapat harus diisi")
                                    }
                                    return@Button
                                }
                                if (tanggal.isBlank()) {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Tanggal harus dipilih")
                                    }
                                    return@Button
                                }
                                if (startTime.isBlank()) {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Jam mulai harus dipilih")
                                    }
                                    return@Button
                                }
                                if (endTime.isBlank()) {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Jam selesai harus dipilih")
                                    }
                                    return@Button
                                }
                                if (lokasi.isBlank()) {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Lokasi harus diisi")
                                    }
                                    return@Button
                                }
                                if (loggedInUserId == null) {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Anda belum login. Silakan login terlebih dahulu.")
                                    }
                                    navController.navigate("login")
                                    return@Button
                                }
                                viewModel.createAgenda(
                                    context = context,
                                    idPengurus = loggedInUserId,
                                    title = namaRapat,
                                    creatorId = loggedInUserId,
                                    creatorName = loggedInUserName ?: "",
                                    dateDisplay = tanggal,
                                    dateSelectedMillis = dateSelectedMillis,
                                    startTimeDisplay = startTime,
                                    endTimeDisplay = endTime,
                                    location = lokasi,
                                    description = deskripsi
                                )
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
                                    text = "Buat Rapat",
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
    }
        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                dateSelectedMillis = millis
                                val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.forLanguageTag("id-ID"))
                                tanggal = dateFormat.format(Date(millis))
                            }
                            showDatePicker = false
                        }
                     ) {
                        Text("OK", color = GreenPrimary)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("Batal", color = GreenPrimary)
                    }
                }
            ) {
                DatePicker(
                    state = datePickerState,
                    colors = DatePickerDefaults.colors(
                        selectedDayContainerColor = GreenPrimary,
                        todayContentColor = GreenPrimary,
                        todayDateBorderColor = GreenPrimary
                    )
                )
            }
        }
        if (showStartTimePicker) {
            TimePickerDialog(
                onDismissRequest = { showStartTimePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val hour = startTimePickerState.hour
                            val minute = startTimePickerState.minute
                            startTime = String.format(Locale.forLanguageTag("id-ID"), "%02d:%02d WIB", hour, minute)
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
                            if (startTime.isNotBlank()) {
                                val startParts = startTime.replace(" WIB", "").split(":")
                                val startHour = startParts[0].toInt()
                                val startMinute = startParts[1].toInt()
                                
                                val startTotalMinutes = startHour * 60 + startMinute
                                val endTotalMinutes = hour * 60 + minute
                                
                                if (endTotalMinutes <= startTotalMinutes) {
                                    showEndTimePicker = false // Tutup dialog dulu
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Jam selesai harus lebih dari jam mulai")
                                    }
                                    return@TextButton
                                }
                            }
                            
                            endTime = String.format(Locale.forLanguageTag("id-ID"), "%02d:%02d WIB", hour, minute)
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

    }
}
