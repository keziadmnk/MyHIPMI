package com.example.myhipmi.ui.screen.event

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.myhipmi.R
import com.example.myhipmi.ui.components.MyHipmiTopBar
import com.example.myhipmi.ui.screen.home.BottomNavBarContainer
import com.example.myhipmi.ui.theme.BluePrimary
import com.example.myhipmi.ui.theme.CardGreen
import com.example.myhipmi.ui.theme.GreenPrimary
import com.example.myhipmi.ui.theme.RedPrimary
import com.example.myhipmi.ui.theme.SecondaryGreen
import com.example.myhipmi.ui.theme.TextPrimary
import com.example.myhipmi.ui.theme.TextSecondary
import com.example.myhipmi.ui.theme.White

@Composable
fun EventScreen(navController: NavHostController) {
    Scaffold(
        topBar = {
            MyHipmiTopBar(
                title = "Event HIPMI",
                onBackClick = { navController.popBackStack() }
            )
        },
        bottomBar = {
            BottomNavBarContainer(
                onHome = { navController.navigate("home") },
                onKas = { navController.navigate("kas") },
                onRapat = { navController.navigate("rapat") },
                onPiket = { navController.navigate("piket") },
                onEvent = { /* already here */ }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(White)
                .padding(innerPadding)
        ) {
            // Tombol Broadcast
            Button(
                onClick = { /* Aksi Broadcast */ },
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(text = "+ Broadcast", color = White)
            }

            // Daftar Event
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(3) {
                    EventCard(
                        title = "Seminar Kewirausahaan Nasional",
                        date = "15 Oktober 2025",
                        time = "14:00 WIB",
                        location = "Aula PKM UNAND",
                        description = "Seminar nasional tentang bisnis digital dan strategi kewirausahaan modern."
                    )
                }
            }
        }
    }
}

@Composable
fun EventCard(
    title: String,
    date: String,
    time: String,
    location: String,
    description: String
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SecondaryGreen),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = TextPrimary
                )
                Row {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = BluePrimary,
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

            Spacer(modifier = Modifier.height(8.dp))

            // Konten Event
            Row(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.LightGray)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.myhipmi_logo),
                        contentDescription = "Event Thumbnail",
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    DetailRow(Icons.Default.CalendarMonth, date)
                    DetailRow(Icons.Default.AccessTime, time)
                    DetailRow(Icons.Default.LocationOn, location)
                    Text(
                        text = description,
                        fontSize = 12.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Tombol Detail
            Button(
                onClick = { /* Lihat detail event */ },
                colors = ButtonDefaults.buttonColors(containerColor = CardGreen),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Lihat Detail Event", color = TextPrimary)
            }
        }
    }
}

@Composable
fun DetailRow(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = GreenPrimary,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = text, fontSize = 12.sp, color = TextPrimary)
    }
}
