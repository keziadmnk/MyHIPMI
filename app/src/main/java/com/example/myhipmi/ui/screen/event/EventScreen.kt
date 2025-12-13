package com.example.myhipmi.ui.screen.event

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.myhipmi.R
import com.example.myhipmi.data.remote.response.EventItemResponse
import com.example.myhipmi.data.remote.response.ReadEventResponse
import com.example.myhipmi.data.remote.retrofit.ApiConfig
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import com.example.myhipmi.ui.components.MyHipmiTopBar
import com.example.myhipmi.ui.components.MenuDrawer
import com.example.myhipmi.ui.screen.home.BottomNavBarContainer
import com.example.myhipmi.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun EventScreen(navController: NavHostController) {
    var isMenuVisible by remember { mutableStateOf(false) }
    var eventList by remember { mutableStateOf<List<EventItemResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var eventToDelete by remember { mutableStateOf<EventItemResponse?>(null) }

    val apiService = remember { ApiConfig.getApiService() }
    val coroutineScope = rememberCoroutineScope()

    // Function untuk load events
    fun loadEvents() {
        coroutineScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = apiService.getEvents()

                if (response.isSuccessful) {
                    eventList = response.body()?.events ?: emptyList()
                } else {
                    val errorBody = response.errorBody()?.string()
                    errorMessage = errorBody?.takeIf { it.isNotBlank() }
                        ?: "Gagal memuat data event (${response.code()})"
                }
            } catch (e: Exception) {
                errorMessage = "Koneksi gagal: ${e.localizedMessage ?: e.message}"
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }
    fun handleDeleteConfirmed() {
        val event = eventToDelete ?: return
        coroutineScope.launch {
            try {
                val response = apiService.deleteEvent(event.idEvent)
                if (response.isSuccessful) {
                    // Muat ulang daftar setelah berhasil
                    loadEvents()
                    // Optional: Tampilkan Toast sukses
                    // Toast.makeText(context, response.body()?.message ?: "Event berhasil dihapus", Toast.LENGTH_SHORT).show()
                } else {
                    errorMessage = "Gagal menghapus event (${response.code()})"
                }
            } catch (e: Exception) {
                errorMessage = "Koneksi gagal saat menghapus: ${e.localizedMessage}"
            } finally {
                showDeleteDialog = false
                eventToDelete = null
            }
        }
    }

    // Load events saat pertama kali screen muncul
    LaunchedEffect(Unit) {
        loadEvents()
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                MyHipmiTopBar(
                    title = "Event HIPMI",
                    onBackClick = { navController.popBackStack() },
                    onMenuClick = { isMenuVisible = true }
                )
            },

        bottomBar = {
            BottomNavBarContainer(
                navController = navController,
                onHome = { navController.navigate("home") },
                onKas = { navController.navigate("kas") },
                onRapat = { navController.navigate("rapat") },
                onPiket = { navController.navigate("piket") },
                onEvent = { /* sudah di sini */ }
            )
        },
        floatingActionButton = {
            var fabVisible by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) {
                delay(400)
                fabVisible = true
            }
            
            AnimatedVisibility(
                visible = fabVisible,
                enter = scaleIn(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ) + fadeIn()
            ) {
                FloatingActionButton(
                    onClick = { navController.navigate("add_event") },
                    containerColor = GreenPrimary,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.size(64.dp)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Tambah Event",
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->

        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(innerPadding)
        ) {
            var cardsVisible by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) {
                delay(150)
                cardsVisible = true
            }
            
            // Loading State
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator(color = GreenPrimary)
                        Text(
                            text = "Memuat data event...",
                            color = TextSecondary,
                            fontSize = 14.sp
                        )
                    }
                }
            }
            // Error State
            else if (errorMessage != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = null,
                            tint = RedPrimary,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = errorMessage ?: "Terjadi kesalahan",
                            color = TextSecondary,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                        Button(
                            onClick = { loadEvents() },
                            colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                        ) {
                            Text("Coba Lagi")
                        }
                    }
                }
            }
            // Empty State
            else if (eventList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.EventBusy,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(64.dp)
                        )
                        Text(
                            text = "Belum ada event",
                            color = TextSecondary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Klik tombol + untuk menambah event baru",
                            color = TextSecondary,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            // Event List
            else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(eventList.size) { index ->
                        val event = eventList[index]
                        AnimatedVisibility(
                            visible = cardsVisible,
                            enter = fadeIn(
                                animationSpec = tween(500, delayMillis = index * 100)
                            ) + slideInVertically(
                                initialOffsetY = { 50 },
                                animationSpec = tween(500, delayMillis = index * 100)
                            )
                        ) {
                            EventCard(
                                navController = navController,
                                event = event,
                                onDeleteClick = { eventToDel ->
                                    eventToDelete = eventToDel
                                    showDeleteDialog = true   }
                            )
                        }
                    }
                }
            }
        }
    }

        AnimatedVisibility(
            visible = showDeleteDialog && eventToDelete != null,
            enter = fadeIn(animationSpec = tween(300)) + 
                   slideInVertically(
                       initialOffsetY = { it },
                       animationSpec = spring(
                           dampingRatio = Spring.DampingRatioMediumBouncy,
                           stiffness = Spring.StiffnessLow
                       )
                   ),
            exit = fadeOut(animationSpec = tween(300)) + 
                  slideOutVertically(
                      targetOffsetY = { it },
                      animationSpec = spring(
                          dampingRatio = Spring.DampingRatioMediumBouncy,
                          stiffness = Spring.StiffnessLow
                      )
                  )
        ) {
            if (eventToDelete != null) {
                DeleteEventBottomSheet(
                    eventName = eventToDelete?.namaEvent ?: "",
                    onDismiss = { 
                        showDeleteDialog = false
                        eventToDelete = null
                    },
                    onConfirmDelete = { handleDeleteConfirmed() }
                )
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

@Composable
fun EventCard(
    navController: NavHostController,
    event: EventItemResponse,
    onDeleteClick: (EventItemResponse) -> Unit
) {

    val formattedDate = try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
        val date = inputFormat.parse(event.tanggal)
        date?.let { outputFormat.format(it) } ?: event.tanggal
    } catch (e: Exception) {
        event.tanggal
    }

    // Format waktu dari HH:MM:SS ke HH:MM
    val formattedTime = try {
        event.waktu.substring(0, 5) + " WIB"
    } catch (e: Exception) {
        event.waktu
    }
    var showMenu by remember { mutableStateOf(false) }
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "cardScale"
    )

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = Color.Black.copy(alpha = 0.25f),
                ambientColor = Color.Black.copy(alpha = 0.2f)
            )
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable {
                navController.navigate("detail_event/${event.idEvent}")
            }
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Judul & Tombol Menu
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = event.namaEvent,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = Color(0xFF1F2937),
                    modifier = Modifier.weight(1f)
                )

                // Tombol titik tiga
                Box {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Menu",
                        tint = TextPrimary,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { showMenu = !showMenu }
                    )

                    // Popup Menu
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        modifier = Modifier.background(Color.White)
                    ) {

                        DropdownMenuItem(
                            text = { Text("Edit") },
                            onClick = {
                                showMenu = false
                                navController.navigate("edit_event/${event.idEvent}") // <--- Menuju layar edit
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Edit, contentDescription = null, tint = BluePrimary)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Hapus", color = RedPrimary) },
                            onClick = {
                                showMenu = false
                                onDeleteClick(event)
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Delete, contentDescription = null, tint = RedPrimary)
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Gambar & Detail
            Row(modifier = Modifier.fillMaxWidth()) {
                // Image with modern styling
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFF3F4F6))
                ) {
                    AsyncImage(
                        model = event.posterUrl,
                        contentDescription = "Poster Event",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(R.drawable.myhipmi_logo),
                        error = painterResource(R.drawable.myhipmi_logo)
                    )

                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    DetailRow(Icons.Default.CalendarMonth, formattedDate, Color(0xFFEF4444))
                    DetailRow(Icons.Default.AccessTime, formattedTime, Color(0xFF3B82F6))
                    DetailRow(Icons.Default.LocationOn, event.tempat, Color(0xFF8B5CF6))
                }
            }
        }
    }
}

@Composable
fun DeleteEventBottomSheet(
    eventName: String,
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
                interactionSource = remember { MutableInteractionSource() }
            )
    ) {
        // Bottom Sheet Card dengan animasi slide up dari bawah
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .clickable(
                    onClick = {},
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
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
                                imageVector = Icons.Default.Error,
                                contentDescription = "Warning",
                                tint = RedPrimary,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Judul
                        Text(
                            text = "Hapus Event?",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Pesan peringatan
                        Text(
                            text = "Item ini akan dihapus secara permanen. Tindakan ini tidak dapat dibatalkan.",
                            fontSize = 14.sp,
                            color = TextSecondary,
                            textAlign = TextAlign.Center,
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
                                text = "Hapus dari Daftar Event",
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
                            border = BorderStroke(1.5.dp, Color(0xFFE5E7EB)),
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

@Composable
fun DetailRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    iconColor: Color = GreenPrimary
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
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
