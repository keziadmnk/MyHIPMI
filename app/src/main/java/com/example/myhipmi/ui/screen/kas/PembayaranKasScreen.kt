package com.example.myhipmi.ui.screen.kas

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.myhipmi.data.local.UserSessionManager
import com.example.myhipmi.ui.components.MyHipmiTopBar
import com.example.myhipmi.ui.theme.KasAccentGreen
import com.example.myhipmi.ui.theme.KasDarkGreen
import com.example.myhipmi.ui.theme.KasScreenBackground
import com.example.myhipmi.ui.theme.MyHIPMITheme
import com.example.myhipmi.ui.viewmodel.KasState
import com.example.myhipmi.ui.viewmodel.KasViewModel
import com.example.myhipmi.utils.ImagePicker
import com.example.myhipmi.utils.createImageUri

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PembayaranKasScreen(
    navController: NavController,
    viewModel: KasViewModel = viewModel()
) {
    val context = LocalContext.current
    var deskripsi by remember { mutableStateOf("") }
    var nominalString by remember { mutableStateOf("") }
    var fileUri by remember { mutableStateOf<Uri?>(null) }
    
    // User Session
    val userSession = remember { UserSessionManager(context) }
    var userId by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        userSession.userId.collect { id ->
            if (id != null) userId = id
        }
    }

    val kasState by viewModel.kasState.collectAsState()

    // Camera & Gallery Launchers
    var tempPhotoUri by remember { mutableStateOf<Uri?>(null) }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            fileUri = tempPhotoUri
        }
    }
    
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { fileUri = it }
    }

    // Effect untuk menangani hasil submit
    LaunchedEffect(kasState) {
        when (kasState) {
            is KasState.Success -> {
                Toast.makeText(context, (kasState as KasState.Success).message, Toast.LENGTH_SHORT).show()
                viewModel.resetState()
                (navController as NavHostController).popBackStack()
            }
            is KasState.Error -> {
                Toast.makeText(context, (kasState as KasState.Error).error, Toast.LENGTH_LONG).show()
                viewModel.resetState()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            MyHipmiTopBar(
                title = "Pembayaran Uang Kas",
                onBackClick = { (navController as NavHostController).popBackStack() }
            )
        },
        containerColor = KasScreenBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            
            // Field untuk Nominal
            Text(
                text = "Nominal Pembayaran",
                fontWeight = FontWeight.Medium,
                color = KasDarkGreen,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = nominalString,
                onValueChange = { nominalString = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Contoh: 50000") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(8.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = KasAccentGreen,
                    unfocusedIndicatorColor = Color.LightGray
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Field untuk deskripsi
            Text(
                text = "Deskripsi / Bulan",
                fontWeight = FontWeight.Medium,
                color = KasDarkGreen,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = deskripsi,
                onValueChange = { deskripsi = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Contoh: Pembayaran kas bulan Oktober") },
                shape = RoundedCornerShape(8.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = KasAccentGreen,
                    unfocusedIndicatorColor = Color.LightGray
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Area untuk upload bukti pembayaran
            Text(
                text = "Upload Bukti Pembayaran",
                fontWeight = FontWeight.Medium,
                color = KasDarkGreen,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White)
                    .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                if (fileUri != null) {
                    AsyncImage(
                        model = fileUri,
                        contentDescription = "Bukti Transfer",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoCamera,
                            contentDescription = "Camera Icon",
                            tint = KasDarkGreen,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Belum ada foto dipilih", color = Color.Gray)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { 
                        val uri = createImageUri(context)
                        tempPhotoUri = uri
                        cameraLauncher.launch(uri)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = KasAccentGreen,
                        contentColor = KasDarkGreen
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f).padding(end = 4.dp)
                ) {
                    Text("Kamera")
                }
                
                Button(
                    onClick = { 
                        galleryLauncher.launch("image/*")
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = KasAccentGreen,
                        contentColor = KasDarkGreen
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f).padding(start = 4.dp)
                ) {
                    Text("Galeri")
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Tombol Kirim
            Button(
                onClick = { 
                    if (userId == 0) {
                        Toast.makeText(context, "User session invalid", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (deskripsi.isBlank() || nominalString.isBlank()) {
                        Toast.makeText(context, "Mohon lengkapi data", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    val nominal = nominalString.toDoubleOrNull()
                    if (nominal == null || nominal <= 0) {
                        Toast.makeText(context, "Nominal tidak valid", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    
                    viewModel.createKas(
                        context = context,
                        userId = userId,
                        deskripsi = deskripsi,
                        nominal = nominal,
                        fileUri = fileUri
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = kasState !is KasState.Loading,
                colors = ButtonDefaults.buttonColors(containerColor = KasDarkGreen),
                shape = RoundedCornerShape(8.dp)
            ) {
                if (kasState is KasState.Loading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Kirim", color = Color.White, fontSize = 16.sp)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PembayaranKasScreenPreview() {
    MyHIPMITheme {
        PembayaranKasScreen(rememberNavController())
    }
}
