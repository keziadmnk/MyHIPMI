package com.example.myhipmi.ui.screen.rapat

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
                onHome = { navController.navigate("home") },
                onKas = { navController.navigate("kas") },
                onRapat = {  },
                onPiket = { navController.navigate("piket") },
                onEvent = { navController.navigate("event") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("add_rapat") },
                containerColor = PrimaryGreen,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Rapat")
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(SecondaryGreen)
        ) {
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

            when (selectedTab) {
                0 -> RapatListContent(getRapatBerlangsung())
                1 -> RapatListContent(getRapatSelesai())
            }
        }
    }
}

@Composable
fun RapatListContent(rapatList: List<RapatItem>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(rapatList) { rapat ->
            RapatCard(rapat)
        }
    }
}

@Composable
fun RapatCard(rapat: RapatItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = rapat.cardColor),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = rapat.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Row {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = Color(0xFF4E7F44),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = RedPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Dibuat oleh: ${rapat.creator}",
                fontSize = 12.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = null,
                    tint = PrimaryGreen,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(rapat.date, fontSize = 14.sp, color = TextPrimary)
            }

            Spacer(modifier = Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AccessTime,
                    contentDescription = null,
                    tint = PrimaryGreen,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(rapat.time, fontSize = 14.sp, color = TextPrimary)
            }

            Spacer(modifier = Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = PrimaryGreen,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(rapat.location, fontSize = 14.sp, color = TextPrimary)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { /* TODO: isi absen */ },
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

// === Data dan Contoh Dummy ===
data class RapatItem(
    val title: String,
    val creator: String,
    val date: String,
    val time: String,
    val location: String,
    val isDone: Boolean,
    val cardColor: Color
)

fun getRapatBerlangsung(): List<RapatItem> = listOf(
    RapatItem(
        title = "Rapat Pleno 3",
        creator = "Nagita Siwiya",
        date = "15 Oktober 2025",
        time = "14:00 WIB",
        location = "Seminar PKM",
        isDone = false,
        cardColor = CardGreen
    )
)

fun getRapatSelesai(): List<RapatItem> = listOf(
    RapatItem(
        title = "Rapat Pleno 3",
        creator = "Refli Ahmad",
        date = "13 Juli 2025",
        time = "19:00 WIB",
        location = "Airo Cafe & Resto",
        isDone = true,
        cardColor = CardGreen
    ),
    RapatItem(
        title = "Rapat Pleno 3",
        creator = "Nagita Siwiya",
        date = "11 April 2025",
        time = "13:45 WIB",
        location = "Seminar PKM",
        isDone = true,
        cardColor = CardGreen
    )
)
