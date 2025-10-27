package com.example.myhipmi.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MenuDrawer(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    userName: String = "Nagita Slavina",
    userRole: String = "Sekretaris Umum",
    onProfileClick: () -> Unit = {},
    onAboutClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    // Layer luar
    Box(modifier = Modifier.fillMaxSize()) {
        // Background gelap di belakang drawer
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable { onDismiss() }
            )
        }

        // Drawer di kanan layar
        AnimatedVisibility(
            visible = isVisible,
            enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(), // masuk dari kanan
            exit = slideOutHorizontally(targetOffsetX = { it }) + fadeOut()  // keluar ke kanan
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalArrangement = Arrangement.End // 🟩 PENTING: Dorong ke kanan layar
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(0.75f), // 🟩 Lebar 3/4 layar
                    color = Color(0xFFF5F5F5),
                    shape = RoundedCornerShape(topStart = 24.dp, bottomStart = 24.dp),
                    shadowElevation = 8.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp)
                    ) {
                        // Header user info
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFBDD99E)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Profile",
                                    tint = Color.White,
                                    modifier = Modifier.size(40.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            Text(userName, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2D3319))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(userRole, fontSize = 14.sp, color = Color(0xFF6B7280))
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // Menu
                        MenuDrawerItem(
                            icon = Icons.Default.Person,
                            text = "Profile",
                            onClick = {
                                onProfileClick()
                                onDismiss()
                            }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        MenuDrawerItem(
                            icon = Icons.Default.Info,
                            text = "Tentang Aplikasi",
                            onClick = {
                                onAboutClick()
                                onDismiss()
                            }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        MenuDrawerItem(
                            icon = Icons.Default.ExitToApp,
                            text = "Keluar",
                            textColor = Color(0xFFDC2626),
                            onClick = {
                                onLogoutClick()
                                onDismiss()
                            }
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        Text(
                            "Version 1.0.0",
                            fontSize = 12.sp,
                            color = Color(0xFF9CA3AF),
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MenuDrawerItem(
    icon: ImageVector,
    text: String,
    textColor: Color = Color(0xFF2D3319),
    backgroundColor: Color = Color.White,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        color = backgroundColor,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = textColor,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = textColor
            )
        }
    }
}
