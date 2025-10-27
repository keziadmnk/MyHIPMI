package com.example.myhipmi.ui.screen.menu

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myhipmi.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tentang Aplikasi", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Notifikasi */ }) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notification")
                    }
                    IconButton(onClick = { /* Menu drawer */ }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF4F9F3)
                )
            )
        },
        bottomBar = {
            // kalau kamu punya NavigationBar di bawah, panggil composable-nya di sini
        }
    ) { innerPadding ->
        AboutContent(Modifier.padding(innerPadding))
    }
}

@Composable
fun AboutContent(modifier: Modifier = Modifier) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // === Bagian Tentang Aplikasi ===
        Text(
            text = "Tentang Aplikasi",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FFF5)),
            border = BorderStroke(1.dp, Color(0xFFD7EBD1))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
//                Image(
////                    painter = painterResource(id = R.drawable.logo_hipmi),
//                    contentDescription = "Logo MyHIPMI",
//                    modifier = Modifier
//                        .size(60.dp)
//                        .padding(end = 16.dp),
//                    contentScale = ContentScale.Crop
//                )

                Column {
                    Text("MyHIPMI", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(
                        "Aplikasi mobile untuk membantu kepengurusan Unit Kegiatan Mahasiswa HIPMI PT Universitas Andalas.",
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Versi 1.1.0", fontSize = 12.sp, color = Color.Gray)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // === Bagian Tim Pengembang ===
        Text(
            text = "Tim Pengembang",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(12.dp))

//        DeveloperCard(name = "Nayla Thahira Medilan", nim = "231512006", photo = R.drawable.nayla)
//        DeveloperCard(name = "Kezia Valerina Damanik", nim = "231512010", photo = R.drawable.kezia)
//        DeveloperCard(name = "Fachri Akbar", nim = "231512004", photo = R.drawable.fachri)
//        DeveloperCard(name = "Aisyah Insani Aulia", nim = "231512024", photo = R.drawable.aisyah)
    }
}

@Composable
fun DeveloperCard(name: String, nim: String, photo: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FFF5)),
        border = BorderStroke(1.dp, Color(0xFFD7EBD1))
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = photo),
                contentDescription = name,
                modifier = Modifier
                    .size(45.dp)
                    .background(Color.White, CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(text = nim, fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}
