package com.example.myhipmi.ui.screen.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myhipmi.R
import com.example.myhipmi.data.remote.request.LoginRequest
import com.example.myhipmi.data.remote.retrofit.ApiConfig
import com.example.myhipmi.ui.theme.BackgroundLight
import com.example.myhipmi.ui.theme.GreenLight
import com.example.myhipmi.ui.theme.GreenPrimary
import com.example.myhipmi.ui.theme.TextDark
import com.example.myhipmi.ui.theme.TextLight
import com.example.myhipmi.ui.theme.White
import kotlinx.coroutines.launch

@Composable
fun LoginPage(
    onLoginSuccess: () -> Unit,
    onBack: () -> Unit
) {
    val apiService = remember { ApiConfig.getApiService() }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val coroutineScope = rememberCoroutineScope()

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

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
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
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(8.dp)
            )

            if (!errorMessage.isNullOrBlank()) {
                Text(
                    text = errorMessage!!,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                )
            }

            // 🟢 Tombol Masuk
            Button(
                onClick = {
                    if (email.isBlank() || password.isBlank()) {
                        errorMessage = "Email dan password wajib diisi"
                        return@Button
                    }

                    coroutineScope.launch {
                        isLoading = true
                        errorMessage = null

                        try {
                            val response = apiService.login(
                                LoginRequest(emailPengurus = email, password = password)
                            )

                            if (response.isSuccessful) {
                                val body = response.body()
                                val hasToken = !body?.token.isNullOrBlank()

                                if (hasToken) {
                                    onLoginSuccess()
                                } else {
                                    errorMessage = body?.message ?: "Login gagal"
                                }
                            } else {
                                val errorBody = response.errorBody()?.string()
                                errorMessage = errorBody?.takeIf { it.isNotBlank() }
                                    ?: "Login gagal (${response.code()})"
                            }
                        } catch (throwable: Exception) {
                            errorMessage = throwable.localizedMessage
                                ?: "Terjadi kesalahan tak terduga"
                        } finally {
                            isLoading = false
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = TextLight,
                        strokeWidth = 2.dp,
                        modifier = Modifier
                            .size(20.dp)
                    )
                } else {
                    Text(
                        text = "Masuk",
                        color = TextLight,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}
