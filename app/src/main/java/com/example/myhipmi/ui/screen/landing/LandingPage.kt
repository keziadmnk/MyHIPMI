package com.example.myhipmi.ui.screen.landing

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.myhipmi.R
import com.example.myhipmi.ui.theme.GreenDark
import com.example.myhipmi.ui.theme.GreenMedium

@Composable
fun LandingPage(
    onNavigateToLogin: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(GreenMedium, GreenDark)
                )
            )
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.myhipmi_logo), // ganti dengan logo kamu
            contentDescription = "MyHIPMI Logo",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .aspectRatio(1f)
        )

        // Navigasi otomatis ke login setelah delay 2 detik
        androidx.compose.runtime.LaunchedEffect(Unit) {
            kotlinx.coroutines.delay(5000)
            onNavigateToLogin()
        }
    }
}
