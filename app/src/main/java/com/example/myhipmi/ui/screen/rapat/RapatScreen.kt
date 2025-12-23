package com.example.myhipmi.ui.screen.rapat

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myhipmi.ui.components.MyHipmiTopBar

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
    val loggedInUserName = sessionManager.getNamaPengurus()

    val rapatBerlangsung by viewModel.rapatBerlangsung.collectAsState()
    val rapatSelesai by viewModel.rapatSelesai.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()
    var selectedTab by rememberSaveable { mutableStateOf(0) }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedRapat by remember { mutableStateOf<AgendaRapatData?>(null) }
    val tabs = listOf("Berlangsung", "Selesai")
    LaunchedEffect(selectedTab) {
        android.util.Log.d("RapatScreen", "Current selected tab: $selectedTab (${tabs[selectedTab]})")
    }
    var hasLoadedData by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if (!hasLoadedData) {
            android.util.Log.d("RapatScreen", "Initial load - loading data...")
            viewModel.loadAllAgenda()
            hasLoadedData = true
        }
    }
    val snackbarHostState = remember { SnackbarHostState() }
    var hasShownSuccessMessage by remember { mutableStateOf(false) }
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    LaunchedEffect(currentBackStackEntry) {
        val route = currentBackStackEntry?.destination?.route
        android.util.Log.d("RapatScreen", "Current route: $route")
        
        if (route == "rapat" && hasLoadedData) {
            android.util.Log.d("RapatScreen", "Back to screen - refreshing data...")
            viewModel.loadAllAgenda()

            if (!hasShownSuccessMessage && !successMessage.isNullOrBlank()) {
                android.util.Log.d("RapatScreen", "Showing success message: $successMessage")
                snackbarHostState.showSnackbar(successMessage!!)
                hasShownSuccessMessage = true
                viewModel.clearMessages()
            }
        }
    }
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
                    onBackClick = { navController.popBackStack() }
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            bottomBar = {
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
                if (selectedTab == 0 && !showDeleteDialog) {
                    FloatingActionButton(
                        onClick = { navController.navigate("add_rapat") },
                        containerColor = GreenPrimary,
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
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.White,
                    contentColor = GreenPrimary,
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            Modifier
                                .tabIndicatorOffset(tabPositions[selectedTab])
                                .height(3.dp),
                            color = GreenPrimary
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
                                    color = if (selectedTab == index) GreenPrimary else TextSecondary
                                )
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = GreenPrimary)
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
    val isExpired = remember(rapat) {
        try {
            val currentTime = Calendar.getInstance()
            val endParts = rapat.endTimeDisplay.replace(" WIB", "").split(":")
            val endHour = endParts[0].toInt()
            val endMinute = endParts[1].toInt()
            val agendaDateParts = rapat.dateDisplay.split(" ")
            val day = agendaDateParts[0].toInt()
            val monthName = agendaDateParts[1]
            val year = agendaDateParts[2].toInt()

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
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(16.dp),
                clip = false
            )
            .clickable {
                navController.navigate(
                    "rapat_detail/${rapat.idAgenda}/${rapat.title}/${rapat.dateDisplay}/${rapat.startTimeDisplay}/${rapat.endTimeDisplay}/${rapat.location}/${rapat.isDone}"
                )
            }
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = rapat.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = Color(0xFF1F2937)
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    val (statusText, statusColor) = when {
                        rapat.isDone -> Pair("Hadir", 0xFF10B981)
                        isExpired -> Pair("Tidak Hadir", 0xFFEF4444)
                        else -> Pair("Aktif", 0xFF3B82F6)
                    }
                    
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(statusColor).copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = statusText,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(statusColor),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
                Box {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Menu",
                        tint = TextPrimary,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { showMenu = !showMenu }
                    )

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        modifier = Modifier.background(Color.White)
                    ) {
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
            Text(
                "Dibuat oleh: ${rapat.creatorName}",
                fontSize = 12.sp,
                color = Color(0xFF9CA3AF),
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(12.dp))
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                RapatDetailRow(Icons.Default.CalendarToday, rapat.dateDisplay, Color(0xFFEF4444))
                RapatDetailRow(
                    Icons.Default.AccessTime,
                    "${rapat.startTimeDisplay} - ${rapat.endTimeDisplay}",
                    Color(0xFF3B82F6)
                )
                RapatDetailRow(Icons.Default.LocationOn, rapat.location, Color(0xFF8B5CF6))
            }
        }
    }
}

data class Tuple4<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

@Composable
fun RapatDetailRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    iconColor: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(androidx.compose.foundation.shape.CircleShape)
                .background(iconColor.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(14.dp)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            fontSize = 13.sp,
            color = Color(0xFF374151),
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun DeleteConfirmationBottomSheet(
    rapatTitle: String,
    onDismiss: () -> Unit,
    onConfirmDelete: () -> Unit
) {
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
                Text(
                    text = "Hapus Agenda Rapat?",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Item ini akan dihapus secara permanen. Tindakan ini tidak dapat dibatalkan.",
                    fontSize = 14.sp,
                    color = TextSecondary,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(28.dp))
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
