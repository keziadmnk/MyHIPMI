package com.example.myhipmi.ui.screen.rapat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.myhipmi.ui.components.MyHipmiTopBar
import com.example.myhipmi.ui.components.MenuDrawer
import com.example.myhipmi.ui.theme.GreenMain
import com.example.myhipmi.ui.theme.GreenPrimary
import com.example.myhipmi.ui.theme.White

@Composable
fun AddRapatScreen(navController: NavHostController) {
    var isMenuVisible by remember { mutableStateOf(false) }
    
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                MyHipmiTopBar(
                    title = "Rapat",
                    onBackClick = { navController.popBackStack() },
                    onMenuClick = { isMenuVisible = true }
                )
            }
        ) { innerPadding ->
        // State input form
        var namaRapat by remember { mutableStateOf("") }
        var tanggal by remember { mutableStateOf("") }
        var waktu by remember { mutableStateOf("") }
        var lokasi by remember { mutableStateOf("") }
        var batasAbsensi by remember { mutableStateOf("") }
        var deskripsi by remember { mutableStateOf("") }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(White)
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            item {
                Text(
                    text = "Agenda Rapat Baru",
                    fontSize = 24.sp,
                    color = GreenPrimary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentSize(Alignment.Center)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Input Fields
            item { RapatTextField("Nama Rapat", namaRapat) { namaRapat = it } }
            item { RapatTextField("Tanggal", tanggal) { tanggal = it } }
            item { RapatTextField("Waktu", waktu) { waktu = it } }
            item { RapatTextField("Lokasi", lokasi) { lokasi = it } }
            item { RapatTextField("Batas Waktu Absensi", batasAbsensi) { batasAbsensi = it } }
            item { RapatTextField("Deskripsi", deskripsi) { deskripsi = it } }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            // TODO: Simpan data rapat
                            navController.popBackStack()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(text = "Buat", color = White)
                    }
                }
            }
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
fun RapatTextField(label: String, value: String, onValueChange: (String) -> Unit) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        shadowElevation = 4.dp,
        color = GreenMain,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(text = label, color = Color(0xFFB0B0B0)) },
            singleLine = label != "Deskripsi",
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                cursorColor = GreenPrimary
            )
        )
    }
}

