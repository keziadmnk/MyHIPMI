package com.example.myhipmi.ui.screen.event

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudUpload // Mengganti CameraAlt dengan CloudUpload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.myhipmi.ui.components.MyHipmiTopBar
import com.example.myhipmi.ui.components.BottomNavBar
import com.example.myhipmi.ui.theme.GreenMain
import com.example.myhipmi.ui.theme.GreenPrimary
import com.example.myhipmi.ui.theme.White

@Composable
fun AddEventScreen(navController: NavHostController) {
    Scaffold(
        topBar = {
            MyHipmiTopBar(
                title = "Event",
                onBackClick = { navController.popBackStack() }
            )
        },

    ) { innerPadding ->
        // State variables
        var namaEvent by remember { mutableStateOf("") }
        var tanggal by remember { mutableStateOf("") }
        var waktu by remember { mutableStateOf("") }
        var tempat by remember { mutableStateOf("") }
        var dresscode by remember { mutableStateOf("") }
        var penyelenggara by remember { mutableStateOf("") }
        var contactPerson by remember { mutableStateOf("") }
        var deskripsi by remember { mutableStateOf("") }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(White)
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            // Header
            item {
                Text(
                    text = "Event HIPMI",
                    fontSize = 24.sp,
                    color = GreenPrimary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentSize(Alignment.Center)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Input Fields
            item { EventTextField("Nama Event", namaEvent) { namaEvent = it } }
            item { EventTextField("Tanggal", tanggal) { tanggal = it } }
            item { EventTextField("Waktu", waktu) { waktu = it } }
            item { EventTextField("Tempat", tempat) { tempat = it } }
            item { EventTextField("Dresscode", dresscode) { dresscode = it } }
            item { EventTextField("Penyelenggara", penyelenggara) { penyelenggara = it } }
            item { EventTextField("Contact Person", contactPerson) { contactPerson = it } }
            item { EventTextField("Deskripsi", deskripsi) { deskripsi = it } }

            // Kolom Drag/Upload File (menggantikan tombol upload foto)
            item {
                Spacer(modifier = Modifier.height(12.dp))
                FileDragAndDropArea(onClick = { /* aksi buka file picker */ })
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Tombol "Tambah" (di paling bawah, sejajar kanan)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End, // Rata kanan
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { /* aksi tambah */ },
                        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(text = "Tambah", color = White)
                    }
                }
            }
        }
    }
}

// Komponen baru untuk area drag-and-drop/upload file
@Composable
fun FileDragAndDropArea(onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        color = GreenMain,
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp) // Tinggi yang cukup untuk area drag
            .border(
                width = 2.dp,
                color = GreenPrimary.copy(alpha = 0.5f), // Border dashed/dotted style
                shape = RoundedCornerShape(10.dp)
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.CloudUpload,
                contentDescription = "Upload foto",
                tint = GreenPrimary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Tarik & Lepaskan File atau Klik untuk Unggah",
                color = Color(0xFFB0B0B0),
                fontSize = 14.sp
            )
        }
    }
}

// EventTextField function remains the same
@Composable
fun EventTextField(label: String, value: String, onValueChange: (String) -> Unit) {
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
                cursorColor = Color(0xFF4C7C34)
            )
        )
    }
}