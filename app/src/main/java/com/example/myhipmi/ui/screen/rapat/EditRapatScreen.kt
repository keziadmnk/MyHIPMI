package com.example.myhipmi.ui.screen.rapat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myhipmi.ui.components.MyHipmiTopBar
import com.example.myhipmi.ui.components.MenuDrawer
import com.example.myhipmi.ui.theme.GreenMain
import com.example.myhipmi.ui.theme.GreenPrimary
import com.example.myhipmi.ui.theme.White
import com.example.myhipmi.ui.viewmodel.RapatViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Calendar
import com.example.myhipmi.data.local.UserSessionManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditRapatScreen(
    navController: NavHostController,
    idAgenda: Int
) {
    val viewModel: RapatViewModel = viewModel()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()

    // Ambil context dan session manager
    val context = LocalContext.current
    val sessionManager = remember { UserSessionManager(context) }

    var isMenuVisible by remember { mutableStateOf(false) }
    
    // State input form
    var namaRapat by remember { mutableStateOf("") }
    var tanggal by remember { mutableStateOf("") }
    var dateSelectedMillis by remember { mutableStateOf(0L) }
    var startTime by remember { mutableStateOf("") }
    var endTime by remember { mutableStateOf("") }
    var lokasi by remember { mutableStateOf("") }
    var deskripsi by remember { mutableStateOf("") }
    
    // State untuk data agenda yang sedang diedit
    var isDataLoaded by remember { mutableStateOf(false) }

    // Date and Time Picker states
    var showDatePicker by remember { mutableStateOf(false) }
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState()
    val startTimePickerState = rememberTimePickerState()
    val endTimePickerState = rememberTimePickerState()

    // Snackbar state
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Load data agenda yang akan diedit
    LaunchedEffect(idAgenda) {
        scope.launch {
            try {
                val response = viewModel.apiService.getAgendaById(idAgenda)
                if (response.isSuccessful && response.body()?.success == true) {
                    val agenda = response.body()?.data
                    agenda?.let {
                        namaRapat = it.title
                        tanggal = it.dateDisplay
                        startTime = it.startTimeDisplay
                        endTime = it.endTimeDisplay
                        lokasi = it.location ?: ""
                        deskripsi = it.description ?: ""
                        
                        // Parse tanggal untuk dateSelectedMillis
                        try {
                            val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.forLanguageTag("id-ID"))
                            val date = dateFormat.parse(it.dateDisplay)
                            dateSelectedMillis = date?.time ?: 0L
                        } catch (e: Exception) {
                            android.util.Log.e("EditRapatScreen", "Error parsing date: ${e.message}")
                        }
                        
                        isDataLoaded = true
                    }
                } else {
                    snackbarHostState.showSnackbar("Gagal memuat data rapat")
                    navController.popBackStack()
                }
            } catch (e: Exception) {
                snackbarHostState.showSnackbar("Error: ${e.message}")
                navController.popBackStack()
            }
        }
    }

    // Handle success/error messages
    LaunchedEffect(successMessage, errorMessage) {
        successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
            // Navigate back to rapat and force refresh setelah sukses
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
                    title = "Edit Rapat",
                    onBackClick = { navController.popBackStack() },
                    onMenuClick = { isMenuVisible = true }
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { innerPadding ->
            
            if (!isDataLoaded) {
                // Loading state
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = GreenPrimary)
                }
            } else {
                // Form sudah terisi
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(White)
                        .padding(innerPadding)
                        .padding(horizontal = 20.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    item {
                        Text(
                            text = "Edit Agenda Rapat",
                            fontSize = 24.sp,
                            color = GreenPrimary,
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentSize(Alignment.Center)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Input Fields
                    item { RapatTextField("Nama Rapat", namaRapat) { namaRapat = it } }

                    // Tanggal dengan DatePicker
                    item {
                        RapatClickableField(
                            label = "Tanggal",
                            value = tanggal,
                            icon = Icons.Default.CalendarToday,
                            onClick = { showDatePicker = true }
                        )
                    }

                    // Jam Mulai dengan TimePicker
                    item {
                        RapatClickableField(
                            label = "Jam Mulai",
                            value = startTime,
                            icon = Icons.Default.AccessTime,
                            onClick = { showStartTimePicker = true }
                        )
                    }

                    // Jam Selesai dengan TimePicker
                    item {
                        RapatClickableField(
                            label = "Jam Selesai",
                            value = endTime,
                            icon = Icons.Default.AccessTime,
                            onClick = { showEndTimePicker = true }
                        )
                    }

                    item { RapatTextField("Lokasi", lokasi) { lokasi = it } }
                    item { RapatTextField("Deskripsi", deskripsi) { deskripsi = it } }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = {
                                    // Validasi input
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

                                    // Update data rapat
                                    viewModel.updateAgenda(
                                        idAgenda = idAgenda,
                                        title = namaRapat,
                                        dateDisplay = tanggal,
                                        dateSelectedMillis = dateSelectedMillis,
                                        startTimeDisplay = startTime,
                                        endTimeDisplay = endTime,
                                        location = lokasi,
                                        description = deskripsi
                                    )
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                                shape = RoundedCornerShape(12.dp),
                                enabled = !isLoading
                            ) {
                                if (isLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color = White,
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                }
                                Text(text = "Simpan", color = White)
                            }
                        }
                    }
                }
            }
        }
        
        // DatePicker Dialog
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

        // Start Time Picker Dialog
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

        // End Time Picker Dialog
        if (showEndTimePicker) {
            TimePickerDialog(
                onDismissRequest = { showEndTimePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val hour = endTimePickerState.hour
                            val minute = endTimePickerState.minute
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

        // Menu Drawer
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
