package com.example.myhipmi.ui.screen.rapat

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myhipmi.ui.components.MyHipmiTopBar
import com.example.myhipmi.ui.components.MenuDrawer
import com.example.myhipmi.ui.screen.home.BottomNavBarContainer
import com.example.myhipmi.ui.theme.*
import com.example.myhipmi.ui.viewmodel.RapatViewModel
import com.example.myhipmi.data.remote.response.AgendaRapatData
import com.example.myhipmi.data.local.UserSessionManager
import androidx.compose.ui.platform.LocalContext
import java.util.*

@Composable
fun RapatScreen(navController: NavHostController) {
    val viewModel: RapatViewModel = viewModel()
    val context = LocalContext.current
    val sessionManager = remember { UserSessionManager(context) }
    // Ambil nama user (nullable) dan gunakan placeholder di UI jika null
    val loggedInUserName = sessionManager.getNamaPengurus()

    // Collect state dari ViewModel
    val rapatBerlangsung by viewModel.rapatBerlangsung.collectAsState()
    val rapatSelesai by viewModel.rapatSelesai.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()

    // Simpan selectedTab dengan rememberSaveable agar bertahan saat back navigation
    var selectedTab by rememberSaveable { mutableStateOf(0) }
    var isMenuVisible by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedRapat by remember { mutableStateOf<AgendaRapatData?>(null) }
    val tabs = listOf("Berlangsung", "Selesai")
    
    // Log untuk debug
    LaunchedEffect(selectedTab) {
        android.util.Log.d("RapatScreen", "Current selected tab: $selectedTab (${tabs[selectedTab]})")
    }

    // Track apakah sudah pernah load data
    var hasLoadedData by remember { mutableStateOf(false) }
    
    // Auto-refresh saat screen pertama kali dibuka
    LaunchedEffect(Unit) {
        if (!hasLoadedData) {
            android.util.Log.d("RapatScreen", "Initial load - loading data...")
            viewModel.loadAllAgenda()
            hasLoadedData = true
        }
    }

    // Refresh saat kembali dari screen lain (hanya sekali, smooth tanpa berkedip)
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    LaunchedEffect(currentBackStackEntry) {
        if (currentBackStackEntry?.destination?.route == "rapat" && hasLoadedData) {
            android.util.Log.d("RapatScreen", "Back to screen - refreshing data...")
            // Single refresh untuk efisiensi
            viewModel.loadAllAgenda()
        }
    }

    // Show snackbar untuk error atau success
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(errorMessage, successMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
        successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                MyHipmiTopBar(
                    title = "Agenda Rapat",
                    onBackClick = { navController.popBackStack() },
                    onMenuClick = { isMenuVisible = true },
                    onNotificationClick = { navController.navigate("notifications") }
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            bottomBar = {
                // Hilangkan bottom bar saat dialog hapus muncul
                if (!showDeleteDialog) {
                    BottomNavBarContainer(
                        navController = navController,
                        onHome = { navController.navigate("home") },
                        onKas = { navController.navigate("kas") },
                        onRapat = { /* Sudah di sini */ },
                        onPiket = { navController.navigate("piket") },
                        onEvent = { navController.navigate("event") }
                    )
                }
            },
            floatingActionButton = {
                // Hilangkan FAB saat dialog hapus muncul
                if (selectedTab == 0 && !showDeleteDialog) {
                    FloatingActionButton(
                        onClick = { navController.navigate("add_rapat") },
                        containerColor = PrimaryGreen,
                        contentColor = Color.White
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Tambah Rapat")
                    }
                }
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(Color(0xFFF8FAF9))
            ) {
                // === Tab Bar ===
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.White,
                    contentColor = PrimaryGreen,
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            Modifier
                                .tabIndicatorOffset(tabPositions[selectedTab])
                                .height(3.dp),
                            color = PrimaryGreen
                        )
                    }
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    text = title,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedTab == index) PrimaryGreen else TextSecondary
                                )
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Loading indicator
                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = PrimaryGreen)
                    }
                } else {

                    when (selectedTab) {
                        0 -> RapatListContent(
                            navController = navController,
                            rapatList = rapatBerlangsung,
                            isSelesai = false,
                            onDeleteClick = { rapat ->
                                selectedRapat = rapat
                                showDeleteDialog = true
                            }
                        )
                        1 -> RapatListContent(
                            navController = navController,
                            rapatList = rapatSelesai,
                            isSelesai = true,
                            onDeleteClick = { rapat ->
                                selectedRapat = rapat
                                showDeleteDialog = true
                            }
                        )
                    }
                }
            }
        }

        // Delete Confirmation Bottom Sheet - ditampilkan di atas semua komponen
        if (showDeleteDialog) {
            DeleteConfirmationBottomSheet(
                rapatTitle = selectedRapat?.title ?: "",
                onDismiss = { showDeleteDialog = false },
                onConfirmDelete = {
                    selectedRapat?.let { viewModel.deleteAgenda(it.idAgenda) }
                    showDeleteDialog = false
                }
            )
        }

        // Menu Drawer
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
fun RapatListContent(navController: NavHostController, rapatList: List<AgendaRapatData>, isSelesai: Boolean, onDeleteClick: (AgendaRapatData) -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        if (rapatList.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isSelesai) "Belum ada rapat yang selesai" else "Belum ada agenda rapat",
                    color = TextSecondary,
                    fontSize = 14.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(rapatList) { rapat ->
                    RapatCard(
                        navController = navController,
                        rapat = rapat,
                        isSelesai = isSelesai,
                        onDeleteClick = { onDeleteClick(rapat) }
                    )
                }
            }
        }
    }
}

@Composable
fun RapatCard(
    navController: NavHostController,
    rapat: AgendaRapatData,
    isSelesai: Boolean,
    onDeleteClick: () -> Unit = {}
) {
    var showMenu by remember { mutableStateOf(false) }

    // Cek apakah waktu rapat sudah lewat
    val isExpired = remember(rapat) {
        try {
            val currentTime = Calendar.getInstance()
            val endParts = rapat.endTimeDisplay.replace(" WIB", "").split(":")
            val endHour = endParts[0].toInt()
            val endMinute = endParts[1].toInt()

            // Parse tanggal agenda
            val agendaDateParts = rapat.dateDisplay.split(" ")
            val day = agendaDateParts[0].toInt()
            val monthName = agendaDateParts[1]
            val year = agendaDateParts[2].toInt()

            // Konversi nama bulan Indonesia ke angka
            val monthNumber = when (monthName.lowercase()) {
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
                else -> currentTime.get(Calendar.MONTH)
            }

            val agendaEndTime = Calendar.getInstance().apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, monthNumber)
                set(Calendar.DAY_OF_MONTH, day)
                set(Calendar.HOUR_OF_DAY, endHour)
                set(Calendar.MINUTE, endMinute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            currentTime.after(agendaEndTime)
        } catch (e: Exception) {
            false
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardGreen),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // === Header: Judul dan menu ===
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = rapat.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    // Status indicator
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        val (statusText, statusColor, statusIcon) = when {
                            rapat.isDone -> Triple("Sudah Absen", PrimaryGreen, Icons.Default.CheckCircle)
                            isExpired -> Triple("Waktu Habis", RedPrimary, Icons.Default.Schedule)
                            else -> Triple("Belum Absen", BluePrimary, Icons.Default.PendingActions)
                        }

                        Icon(
                            imageVector = statusIcon,
                            contentDescription = null,
                            tint = statusColor,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = statusText,
                            fontSize = 12.sp,
                            color = statusColor,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Box {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Menu",
                        tint = TextPrimary,
                        modifier = Modifier
                            .size(22.dp)
                            .clickable { showMenu = !showMenu }
                    )

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        modifier = Modifier.background(Color.White)
                    ) {
                        DropdownMenuItem(
                            text = { Text("Detail") },
                            onClick = {
                                showMenu = false
                                navController.navigate(
                                    "rapat_detail/${rapat.idAgenda}/${rapat.title}/${rapat.dateDisplay}/${rapat.startTimeDisplay}/${rapat.endTimeDisplay}/${rapat.location}/${rapat.isDone}"
                                )
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Info, contentDescription = null, tint = GreenPrimary)
                            }
                        )

                        if (!isSelesai) {
                            DropdownMenuItem(
                                text = { Text("Edit") },
                                onClick = { 
                                    showMenu = false
                                    navController.navigate("edit_rapat/${rapat.idAgenda}")
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Edit, contentDescription = null, tint = BluePrimary)
                                }
                            )
                        }

                        DropdownMenuItem(
                            text = { Text("Hapus", color = RedPrimary) },
                            onClick = {
                                showMenu = false
                                onDeleteClick()
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Delete, contentDescription = null, tint = RedPrimary)
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text("Dibuat oleh: ${rapat.creatorName}", fontSize = 12.sp, color = TextSecondary)

            Spacer(modifier = Modifier.height(8.dp))
            RapatDetailRow(Icons.Default.CalendarToday, rapat.dateDisplay, RedPrimary)
            // Show start and end time together
            RapatDetailRow(Icons.Default.AccessTime, "${rapat.startTimeDisplay} - ${rapat.endTimeDisplay}", BluePrimary)
            RapatDetailRow(Icons.Default.LocationOn, rapat.location, YellowPrimary)

            Spacer(modifier = Modifier.height(12.dp))

            // Button dengan logika yang disesuaikan
            val (buttonText, buttonEnabled, buttonColor, buttonTextColor) = when {
                rapat.isDone -> {
                    Tuple4("Sudah Mengisi Absen", false, PrimaryGreen, Color.White)
                }
                isExpired -> {
                    Tuple4("Waktu Absen Sudah Habis", false, Color.Gray, Color.White)
                }
                else -> {
                    Tuple4("Isi Absensi Kehadiran", true, Color.White, PrimaryGreen)
                }
            }

            Button(
                onClick = {
                    if (buttonEnabled) {
                        navController.navigate(
                            "rapat_detail/${rapat.idAgenda}/${rapat.title}/${rapat.dateDisplay}/${rapat.startTimeDisplay}/${rapat.endTimeDisplay}/${rapat.location}/${rapat.isDone}"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = buttonEnabled,
                colors = ButtonDefaults.buttonColors(
                    containerColor = buttonColor,
                    contentColor = buttonTextColor,
                    disabledContainerColor = buttonColor.copy(alpha = 0.6f),
                    disabledContentColor = buttonTextColor.copy(alpha = 0.6f)
                ),
                border = if (buttonEnabled && buttonColor == Color.White) BorderStroke(1.dp, PrimaryGreen) else null,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = buttonText,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// Helper class untuk multiple return values
data class Tuple4<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

@Composable
fun RapatDetailRow(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String, tint: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, fontSize = 14.sp, color = TextPrimary)
    }
}

@Composable
fun DeleteConfirmationBottomSheet(
    rapatTitle: String,
    onDismiss: () -> Unit,
    onConfirmDelete: () -> Unit
) {
    // Overlay abu-abu dengan efek blur
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable(
                onClick = onDismiss,
                indication = null,
                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
            )
    ) {
        // Bottom Sheet Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .clickable(
                    onClick = {},
                    indication = null,
                    interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                ),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            colors = CardDefaults.cardColors(containerColor = CardGreen),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Handle bar untuk visual bottom sheet
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(4.dp)
                        .background(
                            color = TextSecondary.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(2.dp)
                        )
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Icon peringatan
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(
                            color = RedPrimary.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(32.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Warning",
                        tint = RedPrimary,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Judul
                Text(
                    text = "Hapus Agenda Rapat?",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Pesan peringatan
                Text(
                    text = "Item ini akan dihapus secara permanen. Tindakan ini tidak dapat dibatalkan.",
                    fontSize = 14.sp,
                    color = TextSecondary,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Tombol Hapus
                Button(
                    onClick = onConfirmDelete,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RedPrimary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 2.dp,
                        pressedElevation = 4.dp
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Hapus dari Daftar Agenda",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Tombol Batalkan
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White,
                        contentColor = TextSecondary
                    ),
                    border = BorderStroke(1.5.dp, GrayBorder),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Batalkan",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}
