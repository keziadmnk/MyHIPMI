package com.example.myhipmi.ui.screen.piket

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudUpload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.myhipmi.ui.components.MyHipmiTopBar
import com.example.myhipmi.ui.components.MenuDrawer
import com.example.myhipmi.ui.screen.home.BottomNavBarContainer
import com.example.myhipmi.ui.theme.*

@Composable
fun PiketScreen(navController: NavHostController) {
    var isMenuVisible by remember { mutableStateOf(false) }

    val riwayatPiket = remember {
        listOf(
            PiketItemData("Senin, 6 Oktober 2025", "Menyapu lantai", "Sudah Absen"),
            PiketItemData("Senin, 13 Oktober 2025", "Membuang sampah", "Sudah Absen"),
            PiketItemData("Senin, 20 Oktober 2025", "Membuang sampah", "Sudah Absen")
        )
    }

    Box(Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                MyHipmiTopBar(
                    title = "Jadwal Piket",
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
                    onPiket = { /* already here */ },
                    onEvent = { navController.navigate("event") }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            0f to BackgroundLight,
                            0.25f to White
                        )
                    )
                    .padding(innerPadding)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(bottom = 96.dp)
                ) {
                    item { Spacer(Modifier.height(12.dp)) }

                    item {
                        Text(
                            text = "Jadwal Piket",
                            color = TextPrimary,
                            style = MaterialTheme.typography.headlineSmall,
                            fontSize = 22.sp
                        )
                        Spacer(Modifier.height(12.dp))

                        TodayPiketCard(
                            title = "Piket Hari Ini",
                            tanggal = "Senin, 27 Oktober 2025",
                            onUploadClick = {
                                navController.navigate("piket/upload")
                            }
                        )

                        Spacer(Modifier.height(16.dp))
                    }

                    items(riwayatPiket) { item ->
                        PiketHistoryCard(data = item)
                        Spacer(Modifier.height(12.dp))
                    }

                    item { Spacer(Modifier.height(8.dp)) }
                }
            }
        }

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
private fun TodayPiketCard(
    title: String,
    tanggal: String,
    onUploadClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardGreen),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, BorderLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = title,
                color = DarkGreen,
                style = MaterialTheme.typography.labelMedium
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = tanggal,
                color = TextPrimary,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(12.dp))
            OutlinedButton(
                onClick = onUploadClick,
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, PrimaryGreen),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = SecondaryGreen,
                    contentColor = GreenDark
                ),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Icon(Icons.Outlined.CloudUpload, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Upload Bukti Piket", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Composable
private fun PiketHistoryCard(data: PiketItemData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        border = BorderStroke(1.dp, BorderLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 14.dp, end = 12.dp, bottom = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = data.tanggal,
                    color = TextPrimary,
                    style = MaterialTheme.typography.titleSmall
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = data.tugas,
                    color = TextSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            StatusChip(text = data.status)
        }
    }
}

@Composable
private fun StatusChip(text: String) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(SecondaryGreen)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(PrimaryGreen)
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text = text,
            color = PrimaryGreen,
            style = MaterialTheme.typography.labelMedium
        )
    }
}

private data class PiketItemData(
    val tanggal: String,
    val tugas: String,
    val status: String
)