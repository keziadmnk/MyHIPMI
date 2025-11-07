package com.example.myhipmi.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myhipmi.R
import com.example.myhipmi.ui.components.BottomNavBar
import com.example.myhipmi.ui.components.MyHipmiTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailEventScreen(navController: NavController) {
    var selectedIndex by remember { mutableStateOf(4) }

    val darkGreen = Color(0xFF3B643A)
    val borderGreen = Color(0xFFD8E4A0)
    val descBgGreen = Color(0xFFEAF0D8)
    val redIcon = Color(0xFFB65B5B)
    val blueIcon = Color(0xFF5B82B6)
    val purpleIcon = Color(0xFFA56DB6)
    val blackIcon = Color(0xFF333333)
    val greenIcon = Color(0xFF3B643A)

    Scaffold(
        topBar = {
            MyHipmiTopBar(
                title = "Detail Event",
                onBackClick = { navController.popBackStack() }
            )
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF8FAF9))
                    .padding(innerPadding)
                    .padding(vertical = 16.dp, horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Gambar Poster di atas
                Image(
                    painter = painterResource(id = R.drawable.myhipmi_logo),
                    contentDescription = "Poster Seminar",
                    modifier = Modifier
                        .size(width = 180.dp, height = 220.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Card Info dengan border hijau muda
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    tonalElevation = 6.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            1.dp,
                            borderGreen,
                            RoundedCornerShape(16.dp)
                        )
                        .padding(24.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Judul event
                        Text(
                            text = "Seminar Kewirausahaan Nasional",
                            color = darkGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        // List info dengan icon berwarna
                        InfoItem(Icons.Default.CalendarToday, "15 Oktober 2025", redIcon)
                        InfoItem(Icons.Default.Schedule, "14:00 WIB", blueIcon)
                        InfoItem(Icons.Default.Place, "Seminar PKM", blackIcon)
                        InfoItem(Icons.Default.Checkroom, "Baju Kerja HIPMI", purpleIcon)
                        InfoItem(Icons.Default.Person, "Bidang 1 OKK", blackIcon)
                        InfoItem(Icons.Default.Call, "081234567810", greenIcon)

                        Spacer(modifier = Modifier.height(24.dp))

                        // Label Deskripsi
                        Text(
                            text = "Deskripsi",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = darkGreen,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        // Kotak Deskripsi
                        Surface(
                            color = descBgGreen,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 120.dp)
                        ) {
                            Text(
                                text = "Seminar nasional tentang bisnis digital dan strategi pemasaran modern.",
                                modifier = Modifier.padding(16.dp),
                                fontSize = 14.sp,
                                color = darkGreen,
                                lineHeight = 20.sp
                            )
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun InfoItem(icon: ImageVector, label: String, tint: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 6.dp)
    ) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = label, color = tint, fontSize = 14.sp)
    }
}
