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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.myhipmi.ui.components.MyHipmiTopBar
import com.example.myhipmi.ui.screen.home.BottomNavBarContainer
import com.example.myhipmi.ui.theme.*

@Composable
fun RapatScreen(navController: NavHostController) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Berlangsung", "Selesai")

    Scaffold(
        topBar = {
            MyHipmiTopBar(
                title = "Agenda Rapat",
                onBackClick = { navController.popBackStack() }
            )
        },
        bottomBar = {
            BottomNavBarContainer(
                navController = navController,
                onHome = { navController.navigate("home") },
                onKas = { navController.navigate("kas") },
                onRapat = { /* Sudah di sini */ },
                onPiket = { navController.navigate("piket") },
                onEvent = { navController.navigate("event") }
            )
        },
        floatingActionButton = {
            if (selectedTab == 0) {
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

            // === Konten Tab ===
            when (selectedTab) {
                0 -> RapatListContent(navController, getRapatBerlangsung(), isSelesai = false)
                1 -> RapatListContent(navController, getRapatSelesai(), isSelesai = true)
            }
        }
    }
}

@Composable
fun RapatListContent(navController: NavHostController, rapatList: List<RapatItem>, isSelesai: Boolean) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(rapatList) { rapat ->
            RapatCard(navController, rapat, isSelesai)
        }
    }
}

@Composable
fun RapatCard(navController: NavHostController, rapat: RapatItem, isSelesai: Boolean) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = rapat.cardColor),
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
                Text(
                    text = rapat.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

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
                                    "rapat_detail/${rapat.title}/${rapat.date}/${rapat.time}/${rapat.location}/${rapat.isDone}"
                                )
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Info, contentDescription = null, tint = GreenPrimary)
                            }
                        )

                        if (!isSelesai) {
                            DropdownMenuItem(
                                text = { Text("Edit") },
                                onClick = { showMenu = false },
                                leadingIcon = {
                                    Icon(Icons.Default.Edit, contentDescription = null, tint = BluePrimary)
                                }
                            )
                        }

                        DropdownMenuItem(
                            text = { Text("Hapus", color = RedPrimary) },
                            onClick = { showMenu = false },
                            leadingIcon = {
                                Icon(Icons.Default.Delete, contentDescription = null, tint = RedPrimary)
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text("Dibuat oleh: ${rapat.creator}", fontSize = 12.sp, color = TextSecondary)

            Spacer(modifier = Modifier.height(8.dp))
            RapatDetailRow(Icons.Default.CalendarToday, rapat.date, RedPrimary)
            RapatDetailRow(Icons.Default.AccessTime, rapat.time, BluePrimary)
            RapatDetailRow(Icons.Default.LocationOn, rapat.location, YellowPrimary)

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { navController.navigate(
                    "rapat_detail/${rapat.title}/${rapat.date}/${rapat.time}/${rapat.location}/${rapat.isDone}"
                )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (rapat.isDone) PrimaryGreen else Color.White,
                    contentColor = if (rapat.isDone) Color.White else PrimaryGreen
                ),
                border = if (!rapat.isDone) BorderStroke(1.dp, PrimaryGreen) else null,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = if (rapat.isDone) "Anda sudah mengisi absen" else "Isi Absensi Kehadiran",
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun RapatDetailRow(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String, tint: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, fontSize = 14.sp, color = TextPrimary)
    }
}

data class RapatItem(
    val title: String,
    val creator: String,
    val date: String,
    val time: String,
    val location: String,
    val isDone: Boolean,
    val cardColor: Color
)

// Dummy Data
fun getRapatBerlangsung(): List<RapatItem> = listOf(
    RapatItem("Rapat Pleno 3", "Nagita Siwiya", "27 Oktober 2025", "14:00 WIB", "Ruang Rapat 2 Lt.3", false, CardGreen),
    RapatItem("Rapat Evaluasi Bulanan", "Dodi Pranata", "29 Oktober 2025", "09:00 WIB", "Ruang Utama Kantor", false, CardGreen)
)

fun getRapatSelesai(): List<RapatItem> = listOf(
    RapatItem("Rapat Pleno 2", "Refli Ahmad", "13 Juli 2025", "19:00 WIB", "Airo Cafe & Resto", true, CardGreen),
    RapatItem("Rapat Pleno 1", "Nagita Siwiya", "11 April 2025", "13:45 WIB", "Seminar PKM", true, CardGreen)
)
