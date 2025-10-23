package com.example.myhipmi.ui.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myhipmi.ui.components.BottomNavBar
import com.example.myhipmi.ui.theme.*

@Composable
fun HomeScreen(
    onNavigateToKas: () -> Unit = {},
    onNavigateToRapat: () -> Unit = {},
    onNavigateToPiket: () -> Unit = {},
    onNavigateToEvent: () -> Unit = {}
) {
    Scaffold(
        bottomBar = {
            BottomNavBarContainer(
                onHome = {},
                onKas = onNavigateToKas,
                onRapat = onNavigateToRapat,
                onPiket = onNavigateToPiket,
                onEvent = onNavigateToEvent
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(White)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Header
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Selamat Datang",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                    Text("Senin, 13 Oktober 2025", color = TextSecondary, fontSize = 14.sp)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Notifications,
                        contentDescription = "notif",
                        tint = PrimaryGreen,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Icon(
                        Icons.Default.Menu,
                        contentDescription = "menu",
                        tint = PrimaryGreen,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // Summary cards
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                SummaryCard("Kas Terkumpul", "Rp 1.5jt", CardGreen)
                SummaryCard("Event Aktif", "2 Event", CardYellow)
            }

            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                SummaryCard("Jadwal Piket", "Senin", CardDarkGreen, isLightText = true)
                SummaryCard("Agenda Rapat", "Rapat Pleno 1", CardGray)
            }

            Spacer(Modifier.height(24.dp))
            Text("Aktivitas Terbaru", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(Modifier.height(8.dp))

            // Activity items
            ActivityItem("Waktunya bayar kas!", "3 minutes", Color(0xFFCFEED0))
            ActivityItem("Event baru telah ditambahkan!", "1 hour", Color(0xFFF9F6C9))
            ActivityItem("Jadwal piket hari ini!", "2 days", Color(0xFFFAD7D7))
            ActivityItem("Waktunya bayar kas!", "2 days", Color(0xFFDEE8FA))
            ActivityItem("Agenda rapat baru!", "9 days", Color(0xFFFAD7D7))
        }
    }
}

@Composable
fun SummaryCard(title: String, value: String, color: Color, isLightText: Boolean = false) {
    Column(
        modifier = Modifier
            .width(160.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(color)
            .padding(16.dp)
    ) {
        Text(title, color = if (isLightText) White else TextPrimary, fontSize = 14.sp)
        Spacer(Modifier.height(4.dp))
        Text(
            value,
            color = if (isLightText) White else TextPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
    }
}

@Composable
fun ActivityItem(text: String, time: String, bgColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text, color = TextPrimary, fontWeight = FontWeight.Medium)
        Text(time, color = TextSecondary, fontSize = 12.sp)
    }
}

/**
 * Wrapper supaya BottomNavBar punya background hijau lembut bulat atas
 */
@Composable
fun BottomNavBarContainer(
    onHome: () -> Unit,
    onKas: () -> Unit,
    onRapat: () -> Unit,
    onPiket: () -> Unit,
    onEvent: () -> Unit
) {
    Surface(
        color = Color(0xFFDDECCF),
        shadowElevation = 4.dp,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        BottomNavBar(
            onHome = onHome,
            onKas = onKas,
            onRapat = onRapat,
            onPiket = onPiket,
            onEvent = onEvent
        )
    }
}
