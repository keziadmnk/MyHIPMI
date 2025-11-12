package com.example.myhipmi.ui.screen.piket

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.myhipmi.ui.components.MenuDrawer
import com.example.myhipmi.ui.components.MyHipmiTopBar
import com.example.myhipmi.ui.screen.home.BottomNavBarContainer
import com.example.myhipmi.ui.theme.*

@Composable
fun DetailPiketScreen(
    navController: NavHostController,
    tanggalHariIni: String = "Senin, 27 Oktober 2025"
) {
    var isMenuVisible by remember { mutableStateOf(false) }
    var deskripsi by remember { mutableStateOf("") }

    // Camera / preview states
    var capturedImage by remember { mutableStateOf<Bitmap?>(null) }
    var isImagePreviewVisible by remember { mutableStateOf(false) }

    val takePicturePreviewLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            capturedImage = bitmap
            isImagePreviewVisible = true
        }
    }

    Scaffold(
        topBar = {
            MyHipmiTopBar(
                title = "Upload Absen Piket",
                onBackClick = { navController.popBackStack() },
                onMenuClick = { isMenuVisible = true }
            )
        },
        // hide bottom bar when showing image preview so focus is on the preview
        bottomBar = {
            if (!isImagePreviewVisible) {
                BottomNavBarContainer(
                    navController = navController,
                    onHome = { navController.navigate("home") },
                    onKas = { navController.navigate("kas") },
                    onRapat = { navController.navigate("rapat") },
                    onPiket = { /* already on Piket */ },
                    onEvent = { navController.navigate("event") }
                )
            }
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0f to BackgroundLight,
                        0.25f to White
                    )
                )
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
            ) {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "Upload Absen Piket",
                    color = TextPrimary,
                    style = MaterialTheme.typography.headlineSmall,
                    fontSize = 22.sp
                )
                Spacer(Modifier.height(12.dp))

                // Kartu "Piket Hari Ini"
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = CardGreen),
                    border = BorderStroke(1.dp, BorderLight),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            text = "Piket Hari Ini",
                            color = DarkGreen,
                            style = MaterialTheme.typography.labelMedium
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = tanggalHariIni,
                            color = TextPrimary,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Label deskripsi
                Text(
                    text = "Deskripsi",
                    color = TextPrimary,
                    style = MaterialTheme.typography.labelLarge
                )
                Spacer(Modifier.height(6.dp))

                // TextField deskripsi
                OutlinedTextField(
                    value = deskripsi,
                    onValueChange = { deskripsi = it },
                    modifier = Modifier
                        .fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryGreen,
                        unfocusedBorderColor = BorderLight,
                        cursorColor = GreenPrimary,
                        focusedContainerColor = SecondaryGreen,
                        unfocusedContainerColor = SecondaryGreen,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    placeholder = {
                        Text("Tulis keterangan singkat…", color = TextSecondary)
                    }
                )

                Spacer(Modifier.height(18.dp))

                Text(
                    text = "Silakan ambil foto absensi piket petugas",
                    color = TextPrimary,
                    style = MaterialTheme.typography.labelLarge
                )
                Spacer(Modifier.height(10.dp))

                // Area unggah/ambil foto
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(SecondaryGreen),
                    contentAlignment = Alignment.Center
                ) {
                    // Border tipis seperti pada mockup
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clip(RoundedCornerShape(14.dp))
                            .background(SecondaryGreen)
                    )

                    // Tombol "Ambil foto" -> launch camera preview
                    Button(
                        onClick = {
                            // launch camera preview (TakePicturePreview)
                            takePicturePreviewLauncher.launch(null)
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GreenPrimary,
                            contentColor = TextLight
                        ),
                        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CameraAlt,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Ambil foto", style = MaterialTheme.typography.labelLarge)
                    }
                }

                Spacer(Modifier.height(24.dp))
                Spacer(Modifier.height(96.dp))
            }

            // Image preview bottom sheet overlay
            if (isImagePreviewVisible && capturedImage != null) {
                // dim background
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
                )

                // bottom sheet style card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                        colors = CardDefaults.cardColors(containerColor = White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Preview Foto Absen Piket",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimary
                            )
                            Spacer(Modifier.height(12.dp))

                            Image(
                                bitmap = capturedImage!!.asImageBitmap(),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(300.dp)
                                    .clip(RoundedCornerShape(12.dp)),
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop
                            )

                            Spacer(Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        // cancel preview and return to detail screen (no deletion)
                                        isImagePreviewVisible = false
                                        capturedImage = null
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary)
                                ) {
                                    Text("Batal")
                                }

                                Button(
                                    onClick = {
                                        // emulate process then navigate back to piket schedule
                                        isImagePreviewVisible = false
                                        // clear captured image if you want
                                        capturedImage = null
                                        // navigate back to piket schedule
                                        navController.navigate("piket") {
                                            // avoid multiple copies on backstack if needed
                                            popUpTo("piket") { inclusive = false }
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary, contentColor = TextLight)
                                ) {
                                    Text("Proses")
                                }
                            }

                            Spacer(Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }

    // Menu drawer
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
            // TODO: handle logout
        }
    )
}