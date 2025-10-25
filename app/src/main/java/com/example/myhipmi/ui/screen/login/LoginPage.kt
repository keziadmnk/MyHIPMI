package com.example.myhipmi.ui.screen.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myhipmi.R
import com.example.myhipmi.ui.theme.*

@Composable
fun LoginPage(
    onLoginSuccess: () -> Unit,
    onBack: () -> Unit
) {
    // 🟢 State untuk input email & password
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(GreenLight, BackgroundLight)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(24.dp)
                .background(
                    color = White,
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(24.dp)
        ) {
            // 🟩 Logo
            Image(
                painter = painterResource(id = R.drawable.myhipmi_logo),
                contentDescription = "MyHIPMI Logo",
                modifier = Modifier
                    .fillMaxWidth(0.35f)
                    .padding(bottom = 16.dp)
            )

            Text(
                text = "Selamat Datang",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = TextDark,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                ),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // 🟡 Input Email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(8.dp)
            )

            // 🟡 Input Password
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(8.dp)
            )

            // 🟢 Tombol Masuk
            Button(
                onClick = {
                    // Langsung navigasi ke Home (tanpa validasi)
                    onLoginSuccess()
                },
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Masuk",
                    color = TextLight,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
