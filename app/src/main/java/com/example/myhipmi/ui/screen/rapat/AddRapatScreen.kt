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
import com.example.myhipmi.data.local.UserSessionManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRapatScreen(navController: NavHostController) {
    val viewModel: RapatViewModel = viewModel()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()

    // Ambil context dan session manager
    val context = LocalContext.current
    val sessionManager = remember { UserSessionManager(context) }

    // Ambil data user yang login (nullable — jangan pakai fallback hardcoded)
    val loggedInUserId = sessionManager.getIdPengurus()
    val loggedInUserName = sessionManager.getNamaPengurus()

    var isMenuVisible by remember { mutableStateOf(false) }
    
    // State input form
    var namaRapat by remember { mutableStateOf("") }
    var tanggal by remember { mutableStateOf("") }
    var dateSelectedMillis by remember { mutableStateOf(0L) }
    var startTime by remember { mutableStateOf("") }
    var endTime by remember { mutableStateOf("") }
    var lokasi by remember { mutableStateOf("") }
    var deskripsi by remember { mutableStateOf("") }

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
                    title = "Rapat",
                    onBackClick = { navController.popBackStack() },
                    onMenuClick = { isMenuVisible = true }
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { innerPadding ->
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
                    text = "Agenda Rapat Baru",
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

                            // Pastikan user sudah login
                            if (loggedInUserId == null) {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Anda belum login. Silakan login terlebih dahulu.")
                                }
                                navController.navigate("login")
                                return@Button
                            }

                            // Simpan data rapat baru ke backend
                            viewModel.createAgenda(
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
                        Text(text = "Buat", color = White)
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

        // Menu Drawer (tampilan nama diambil dari session, gunakan placeholder netral jika null)
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
fun RapatTextField(label: String, value: String, onValueChange: (String) -> Unit) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        shadowElevation = 4.dp,
        color = GreenMain,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(text = label, color = Color(0xFFB0B0B0)) },
            singleLine = label != "Deskripsi",
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                cursorColor = GreenPrimary
            )
        )
    }
}

@Composable
fun RapatClickableField(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        shadowElevation = 4.dp,
        color = GreenMain,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable(onClick = onClick)
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            label = { Text(text = label, color = Color(0xFFB0B0B0)) },
            readOnly = true,
            enabled = false,
            trailingIcon = {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = GreenPrimary
                )
            },
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .clickable(onClick = onClick),
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = Color.Black,
                disabledBorderColor = Color.Transparent,
                disabledLabelColor = Color(0xFFB0B0B0),
                disabledContainerColor = Color.Transparent,
                disabledTrailingIconColor = GreenPrimary
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
