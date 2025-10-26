package com.example.myhipmi.ui.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myhipmi.ui.components.BottomNavBar


@Composable
fun HomeScreen(
    onNavigateToKas: () -> Unit = {},
    onNavigateToRapat: () -> Unit = {},
    onNavigateToPiket: () -> Unit = {},
    onNavigateToEvent: () -> Unit = {}
) {
    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFE3ECDA),
            Color(0xFFF0F6F0),
            Color(0xFFFFFFFF),
            Color(0xFFFFFFFF),
            Color(0xFFFFFFFF)
        )
    )
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
                .background(brush = gradientBrush)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
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
                        color = Color(0xFF2D3319),
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Senin, 13 Oktober 2025",
                        color = Color(0xFF6B7280),
                        fontSize = 14.sp
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box {
                        Icon(
                            Icons.Default.Notifications,
                            contentDescription = "notif",
                            tint = Color(0xFF4A5D23),
                            modifier = Modifier.size(28.dp)
                        )
                        // Red dot notification indicator
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(Color.Red, CircleShape)
                                .align(Alignment.TopEnd)
                        )
                    }
                    Icon(
                        Icons.Default.Menu,
                        contentDescription = "menu",
                        tint = Color(0xFF4A5D23),
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // Summary cards
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SummaryCard(
                    title = "Kas Terkumpul",
                    value = "Rp 1.5jt",
                    icon = Icons.Default.AccountBalanceWallet,
                    bgColor = Color(0xFFBDD99E),
                    iconBgColor = Color(0xFFA8CC82),
                    modifier = Modifier.weight(1f)
                )
                SummaryCard(
                    title = "Event Aktif",
                    value = "2 Event",
                    icon = Icons.Default.Campaign,
                    bgColor = Color(0xFFF5E8A0),
                    iconBgColor = Color(0xFFEDD97A),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(12.dp))

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SummaryCard(
                    title = "Jadwal Piket",
                    value = "Senin",
                    icon = Icons.Default.CalendarToday,
                    bgColor = Color(0xFF4A5D23),
                    iconBgColor = Color(0xFF3A4D13),
                    isLightText = true,
                    modifier = Modifier.weight(1f)
                )
                SummaryCard(
                    title = "Agenda Rapat",
                    value = "Rapat Pleno 1",
                    icon = Icons.Default.EventNote,
                    bgColor = Color(0xFFD9D9D9),
                    iconBgColor = Color(0xFFC4C4C4),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(28.dp))

            Text(
                "Aktivitas Terbaru",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color(0xFF2D3319)
            )

            Spacer(Modifier.height(16.dp))

            // Activity items
            ActivityItem(
                text = "Waktunya bayar kas!",
                time = "3 minutes",
                icon = Icons.Default.AccountBalanceWallet,
                bgColor = Color(0xFFFFFFFF),
                iconBgColor = Color(0xFFB8E3BA)
            )
            Spacer(Modifier.height(12.dp))

            ActivityItem(
                text = "Event baru telah ditambahkan!",
                time = "1 hour",
                icon = Icons.Default.Campaign,
                bgColor = Color(0xFFFFFFFF),
                iconBgColor = Color(0xFFF1EAAA)
            )
            Spacer(Modifier.height(12.dp))

            ActivityItem(
                text = "Jadwal piket hari ini!",
                time = "2 days",
                icon = Icons.Default.EventNote,
                bgColor = Color(0xFFFFFFFF),
                iconBgColor = Color(0xFFF5C0C0)
            )
            Spacer(Modifier.height(12.dp))

            ActivityItem(
                text = "Waktunya bayar kas!",
                time = "2 days",
                icon = Icons.Default.CalendarToday,
                bgColor = Color(0xFFFFFFFF),
                iconBgColor = Color(0xFFC7D9F5)
            )
            Spacer(Modifier.height(12.dp))

            ActivityItem(
                text = "Agenda rapat baru!",
                time = "9 days",
                icon = Icons.Default.EventNote,
                bgColor = Color(0xFFFFFFFF),
                iconBgColor = Color(0xFFF5C0C0)
            )
        }
    }
}

@Composable
fun SummaryCard(
    title: String,
    value: String,
    icon: ImageVector,
    bgColor: Color,
    iconBgColor: Color,
    isLightText: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .padding(20.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(iconBgColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isLightText) Color.White else Color(0xFF2D3319),
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(Modifier.height(16.dp))

        Text(
            title,
            color = if (isLightText) Color.White.copy(alpha = 0.9f) else Color(0xFF2D3319),
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(Modifier.height(4.dp))

        Text(
            value,
            color = if (isLightText) Color.White else Color(0xFF2D3319),
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
    }
}

@Composable
fun ActivityItem(
    text: String,
    time: String,
    icon: ImageVector,
    bgColor: Color,
    iconBgColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon with circular background
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(iconBgColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF2D3319),
                modifier = Modifier.size(24.dp)
            )
        }

        // Text content
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text,
                color = Color(0xFF1F1F1F),
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                modifier = Modifier.weight(1f)
            )

            Text(
                time,
                color = Color(0xFF6B7280),
                fontSize = 13.sp
            )
        }
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