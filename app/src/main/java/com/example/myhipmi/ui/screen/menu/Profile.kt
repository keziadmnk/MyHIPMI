package com.example.myhipmi.ui.screen.menu

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myhipmi.ui.components.MenuDrawer
import com.example.myhipmi.ui.components.MyHipmiTopBar

@Composable
fun ProfileScreen(navController: NavController) {
    var isMenuVisible by remember { mutableStateOf(false) }
    var nama by remember { mutableStateOf("Nagita Slavina") }
    var jabatan by remember { mutableStateOf("Sekretaris Umum") }
    var jadwalPiket by remember { mutableStateOf("Senin") }
    var nomorHP by remember { mutableStateOf("") }
    var alamat by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                MyHipmiTopBar(
                    title = "Profile",
                    onBackClick = { navController.navigateUp() },
                    onMenuClick = { isMenuVisible = true },
                    onNotificationClick = { /* Handle notification */ }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF8FAF9))
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                // Profile Image with border
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .border(
                            width = 3.dp,
                            color = Color(0xFFBDD99E),
                            shape = CircleShape
                        )
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(Color(0xFFBDD99E)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile Picture",
                            tint = Color.White,
                            modifier = Modifier.size(70.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Edit Title
                Text(
                    text = "Edit",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6B9B4D)
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Form Card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White.copy(alpha = 0.7f),
                    border = BorderStroke(
                        2.dp,
                        Color(0xFFBDD99E)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {
                        // Nama
                        ProfileTextField(
                            label = "Nama",
                            value = nama,
                            onValueChange = { nama = it },
                            enabled = false
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Jabatan
                        ProfileTextField(
                            label = "Jabatan",
                            value = jabatan,
                            onValueChange = { jabatan = it },
                            enabled = false
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Jadwal Piket
                        ProfileTextField(
                            label = "Jadwal Piket",
                            value = jadwalPiket,
                            onValueChange = { jadwalPiket = it },
                            enabled = false
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Nomor HP
                        ProfileTextField(
                            label = "Nomor HP",
                            value = nomorHP,
                            onValueChange = { nomorHP = it },
                            placeholder = "Masukkan nomor HP",
                            enabled = true
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Alamat
                        ProfileTextField(
                            label = "Alamat",
                            value = alamat,
                            onValueChange = { alamat = it },
                            placeholder = "Masukkan alamat",
                            enabled = true,
                            singleLine = false,
                            minLines = 3
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Update Button
                Button(
                    onClick = {
                        // Handle update profile
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF6B9B4D)
                    )
                ) {
                    Text(
                        text = "Perbarui",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        // Menu Drawer
        MenuDrawer(
            isVisible = isMenuVisible,
            onDismiss = { isMenuVisible = false },
            userName = nama,
            userRole = jabatan,
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
private fun ProfileTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    enabled: Boolean = true,
    singleLine: Boolean = true,
    minLines: Int = 1
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF6B7280),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                if (placeholder.isNotEmpty()) {
                    Text(
                        text = placeholder,
                        color = Color(0xFFBBBBBB)
                    )
                }
            },
            enabled = enabled,
            singleLine = singleLine,
            minLines = minLines,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = Color(0xFF2D3319),
                disabledBorderColor = Color(0xFFE5E7EB),
                disabledContainerColor = Color(0xFFF9FAFB),
                focusedBorderColor = Color(0xFF6B9B4D),
                unfocusedBorderColor = Color(0xFFE5E7EB),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )
    }
}