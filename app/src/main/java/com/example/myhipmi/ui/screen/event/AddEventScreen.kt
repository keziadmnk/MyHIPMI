package com.example.myhipmi.ui.screen.event

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.myhipmi.ui.components.MyHipmiTopBar
import com.example.myhipmi.ui.theme.GreenPrimary
import com.example.myhipmi.ui.theme.White
import kotlinx.coroutines.delay

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
        
        var isVisible by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) {
            delay(100)
            isVisible = true
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFFFFFF))
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            // Header
            item {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { -20 })
                ) {
                    Column {
                        Text(
                            text = "Tambah Event Baru",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1F2937),
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Text(
                            text = "Lengkapi informasi event di bawah ini",
                            fontSize = 14.sp,
                            color = Color(0xFF6B7280)
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }

            // Input Fields with animation
            item {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(animationSpec = tween(400, delayMillis = 100)) + 
                            slideInVertically(initialOffsetY = { 30 })
                ) {
                    Column {
                        ModernTextField(
                            label = "Nama Event",
                            value = namaEvent,
                            onValueChange = { namaEvent = it },
                            icon = Icons.Default.Event
                        )
                        ModernTextField(
                            label = "Tanggal",
                            value = tanggal,
                            onValueChange = { tanggal = it },
                            icon = Icons.Default.CalendarMonth,
                            placeholder = "DD/MM/YYYY"
                        )
                        ModernTextField(
                            label = "Waktu",
                            value = waktu,
                            onValueChange = { waktu = it },
                            icon = Icons.Default.AccessTime,
                            placeholder = "HH:MM WIB"
                        )
                        ModernTextField(
                            label = "Tempat",
                            value = tempat,
                            onValueChange = { tempat = it },
                            icon = Icons.Default.LocationOn
                        )
                        ModernTextField(
                            label = "Dresscode",
                            value = dresscode,
                            onValueChange = { dresscode = it },
                            icon = Icons.Default.Checkroom
                        )
                        ModernTextField(
                            label = "Penyelenggara",
                            value = penyelenggara,
                            onValueChange = { penyelenggara = it },
                            icon = Icons.Default.Person
                        )
                        ModernTextField(
                            label = "Contact Person",
                            value = contactPerson,
                            onValueChange = { contactPerson = it },
                            icon = Icons.Default.Phone
                        )
                        ModernTextField(
                            label = "Deskripsi",
                            value = deskripsi,
                            onValueChange = { deskripsi = it },
                            icon = Icons.Default.Description,
                            singleLine = false,
                            minLines = 3
                        )
                    }
                }
            }

            // Kolom Upload File
            item {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(animationSpec = tween(400, delayMillis = 200)) + 
                            slideInVertically(initialOffsetY = { 30 })
                ) {
                    Column {
                        Spacer(modifier = Modifier.height(8.dp))
                        ModernFileDragAndDropArea(onClick = { /* aksi buka file picker */ })
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }

            // Tombol "Tambah"
            item {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(animationSpec = tween(400, delayMillis = 300)) + 
                            slideInVertically(initialOffsetY = { 30 })
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = GreenPrimary
                            ),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                width = 1.5.dp
                            )
                        ) {
                            Text(
                                text = "Batal",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        
                        Button(
                            onClick = { /* aksi tambah */ },
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = GreenPrimary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Tambah Event",
                                color = White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}

// Modern File Upload Area
@Composable
fun ModernFileDragAndDropArea(onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = 2.dp,
                color = GreenPrimary.copy(alpha = 0.3f),
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        color = GreenPrimary.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CloudUpload,
                    contentDescription = "Upload foto",
                    tint = GreenPrimary,
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Upload Gambar Event",
                color = Color(0xFF1F2937),
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Klik atau tarik file ke sini",
                color = Color(0xFF9CA3AF),
                fontSize = 13.sp
            )
        }
    }
}

// Modern TextField Component
@Composable
fun ModernTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    placeholder: String = "",
    singleLine: Boolean = true,
    minLines: Int = 1
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF374151),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { 
                Text(
                    text = placeholder.ifEmpty { "Masukkan $label" },
                    color = Color(0xFFD1D5DB)
                ) 
            },
            leadingIcon = {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = GreenPrimary,
                    modifier = Modifier.size(20.dp)
                )
            },
            singleLine = singleLine,
            minLines = minLines,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GreenPrimary,
                unfocusedBorderColor = Color(0xFFE5E7EB),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedTextColor = Color(0xFF1F2937),
                unfocusedTextColor = Color(0xFF1F2937),
                cursorColor = GreenPrimary
            )
        )
    }
}