package com.example.myhipmi.ui.screen.event

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.myhipmi.ui.theme.*

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
                onEvent = { /* sudah di sini */ }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("add_event") },
                containerColor = GreenPrimary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Event")
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(White)
                .padding(innerPadding)
        ) {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(3) {
                    EventCard(
                        navController = navController,
                        title = "Seminar Kewirausahaan Nasional",
                        date = "15 Oktober 2025",
                        time = "14:00 WIB",
                        location = "Seminar PKM",
                        description = "Seminar nasional tentang bisnis digital dan strategi kewirausahaan modern."
                    )
                }
            }
        }
    }
}

@Composable
fun EventCard(
    navController: NavHostController,
    title: String,
    date: String,
    time: String,
    location: String,
    description: String
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6E4)),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Judul & Tombol Menu
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

                // Tombol titik tiga
                Box {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Menu",
                        tint = TextPrimary,
                        modifier = Modifier
                            .size(22.dp)
                            .clickable { showMenu = !showMenu }
                    )

                    // Popup Menu
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        modifier = Modifier.background(Color.White)
                    ) {
                        DropdownMenuItem(
                            text = { Text("Detail") },
                            onClick = {
                                showMenu = false
                                navController.navigate("detail_event")
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Info, contentDescription = null, tint = GreenPrimary)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Edit") },
                            onClick = {
                                showMenu = false
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Edit, contentDescription = null, tint = BluePrimary)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Hapus", color = RedPrimary) },
                            onClick = {
                                showMenu = false
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Delete, contentDescription = null, tint = RedPrimary)
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Gambar & Detail
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
