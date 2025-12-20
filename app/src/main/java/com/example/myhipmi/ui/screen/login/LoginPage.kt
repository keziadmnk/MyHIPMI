package com.example.myhipmi.ui.screen.login

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myhipmi.R
import com.example.myhipmi.data.local.UserSessionManager
import com.example.myhipmi.data.remote.request.LoginRequest
import com.example.myhipmi.data.remote.retrofit.ApiConfig
import com.example.myhipmi.ui.theme.GreenPrimary
import com.example.myhipmi.ui.theme.White
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalContext

@Composable
fun LoginPage(
    onLoginSuccess: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val sessionManager = remember { UserSessionManager(context) }
    val apiService = remember { ApiConfig.getApiService() }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val coroutineScope = rememberCoroutineScope()
    var passwordVisible by remember { mutableStateOf(false) }
    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        delay(100)
        isVisible = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFE8F5E9),
                        Color(0xFFF1F8F0),
                        Color(0xFFFFFFFF)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(animationSpec = tween(600)) + 
                    scaleIn(initialScale = 0.9f, animationSpec = tween(600))
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(32.dp)
                    .shadow(
                        elevation = 16.dp,
                        shape = RoundedCornerShape(16.dp),
                        spotColor = Color.Black.copy(alpha = 0.15f)
                    )
                    .background(
                        color = White,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(32.dp)
            ) {
                // Logo with animation
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .shadow(
                            elevation = 8.dp,
                            shape = CircleShape,
                            spotColor = GreenPrimary.copy(alpha = 0.3f)
                        )
                        .background(
                            color = Color(0xFFF1F8F0),
                            shape = CircleShape
                        )
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.myhipmi_logo),
                        contentDescription = "MyHIPMI Logo",
                        modifier = Modifier.size(60.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Selamat Datang",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937)
                )
                
                Text(
                    text = "Masuk ke akun MyHIPMI Anda",
                    fontSize = 14.sp,
                    color = Color(0xFF6B7280),
                    modifier = Modifier.padding(top = 4.dp, bottom = 32.dp)
                )

                // Email Field
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Email",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF374151),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = { Text("Email", color = Color(0xFFD1D5DB)) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = null,
                                tint = GreenPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GreenPrimary,
                            unfocusedBorderColor = Color(0xFFE5E7EB),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color(0xFFFAFAFA),
                            focusedTextColor = Color(0xFF1F2937),
                            unfocusedTextColor = Color(0xFF1F2937)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Password Field
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Password",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF374151),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = { Text("Password", color = Color(0xFFD1D5DB)) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = GreenPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                    tint = Color(0xFF9CA3AF)
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GreenPrimary,
                            unfocusedBorderColor = Color(0xFFE5E7EB),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color(0xFFFAFAFA),
                            focusedTextColor = Color(0xFF1F2937),
                            unfocusedTextColor = Color(0xFF1F2937)
                        )
                    )
                }

                // Error Message
                AnimatedVisibility(
                    visible = !errorMessage.isNullOrBlank(),
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp)
                            .background(
                                color = Color(0xFFFEE2E2),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = null,
                            tint = Color(0xFFDC2626),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = errorMessage ?: "",
                            color = Color(0xFFDC2626),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Login Button
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

                                if (hasToken && body != null) {
                                    // Simpan data user ke session
                                    val user = body.user
                                    if (user != null && user.idPengurus != null) {
                                        sessionManager.saveUserSession(
                                            idPengurus = user.idPengurus,
                                            namaPengurus = user.namaPengurus ?: "",
                                            emailPengurus = user.emailPengurus ?: "",
                                            jabatan = user.jabatan ?: "Anggota",
                                            token = body.token ?: ""
                                        )
                                    }
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
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GreenPrimary,
                        disabledContainerColor = GreenPrimary.copy(alpha = 0.6f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isLoading,
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 4.dp,
                        pressedElevation = 8.dp
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = White,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Login,
                            contentDescription = null,
                            tint = White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Masuk",
                            color = White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
