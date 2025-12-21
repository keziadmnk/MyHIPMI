package com.example.myhipmi.ui.screen.kas

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.myhipmi.data.local.UserSessionManager
import com.example.myhipmi.ui.components.MyHipmiTopBar
import com.example.myhipmi.ui.theme.KasAccentGreen
import com.example.myhipmi.ui.theme.KasDarkGreen
import com.example.myhipmi.ui.viewmodel.KasState
import com.example.myhipmi.ui.viewmodel.KasViewModel
import com.example.myhipmi.utils.createImageUri
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BayarTagihanScreen(
    navController: NavController,
    kasId: Int,
    viewModel: KasViewModel = viewModel()
) {
    val context = LocalContext.current
    val userSession = remember { UserSessionManager(context) }
    var userId by remember { mutableIntStateOf(0) }
    
    // Ambil detail kas saat pertama kali dibuka
    LaunchedEffect(kasId) {
        viewModel.getKasDetail(kasId)
    }
    
    LaunchedEffect(Unit) {
        userSession.userId.collect { id ->
            if (id != null) userId = id
        }
    }

    val selectedKas by viewModel.selectedKas.collectAsState()
    val kasState by viewModel.kasState.collectAsState()

    // State untuk gambar
    var fileUri by remember { mutableStateOf<Uri?>(null) }
    
    // Camera & Gallery Launchers
    var tempPhotoUri by remember { mutableStateOf<Uri?>(null) }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) fileUri = tempPhotoUri
    }
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { fileUri = it }
    }

    // Effect untuk menangani hasil update
    LaunchedEffect(kasState) {
        when (kasState) {
            is KasState.Success -> {
                viewModel.resetState()
                
                // Set flag sukses ke backstack entry sebelumnya (KasScreen)
                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.set("payment_success", true)
                    
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
                title = "Bayar Tagihan",
                onBackClick = { (navController as NavHostController).popBackStack() }
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        if (kasState is KasState.Loading && selectedKas == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = KasDarkGreen)
            }
        } else {
            val kas = selectedKas
            if (kas != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp)
                ) {
                    // Informasi Tagihan (Read Only)
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Deskripsi Tagihan", fontSize = 12.sp, color = Color.Gray)
                            Text(kas.deskripsi, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            Text("Total Pembayaran", fontSize = 12.sp, color = Color.Gray)
                            Text(
                                formatRupiah(kas.nominal), 
                                fontSize = 20.sp, 
                                fontWeight = FontWeight.Bold, 
                                color = KasDarkGreen
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Upload Bukti
                    Text("Upload Bukti Transfer", fontWeight = FontWeight.Medium, color = KasDarkGreen, modifier = Modifier.padding(bottom = 8.dp))
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
                                contentDescription = "Preview Baru",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.PhotoCamera, null, tint = KasDarkGreen, modifier = Modifier.size(40.dp))
                                Text("Belum ada foto", color = Color.Gray)
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Button(
                            onClick = {
                                val uri = createImageUri(context)
                                tempPhotoUri = uri
                                cameraLauncher.launch(uri)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = KasAccentGreen, contentColor = KasDarkGreen),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f).padding(end = 4.dp)
                        ) { Text("Kamera") }
                        
                        Button(
                            onClick = { galleryLauncher.launch("image/*") },
                            colors = ButtonDefaults.buttonColors(containerColor = KasAccentGreen, contentColor = KasDarkGreen),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f).padding(start = 4.dp)
                        ) { Text("Galeri") }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Tombol Kirim
                    Button(
                        onClick = {
                             if (fileUri == null) {
                                Toast.makeText(context, "Mohon upload bukti transfer", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            
                            // Gunakan updateKas untuk mengupdate bukti transfer
                            // Ubah status menjadi "lunas" saat user mengirim bukti pembayaran
                            viewModel.updateKas(
                                context = context,
                                id = kasId,
                                userId = userId,
                                deskripsi = kas.deskripsi,
                                nominal = kas.nominal,
                                status = "lunas", // <--- PERBAIKAN DI SINI
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
                            Icon(Icons.Default.Send, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Kirim Bukti Pembayaran")
                        }
                    }
                }
            }
        }
    }
}

fun formatRupiah(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    return format.format(amount).replace("Rp", "Rp ")
}
