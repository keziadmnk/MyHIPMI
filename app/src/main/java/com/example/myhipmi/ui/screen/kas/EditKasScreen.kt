package com.example.myhipmi.ui.screen.kas

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.myhipmi.utils.createImageUri

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditKasScreen(
    navController: NavController,
    kasId: Int, // Terima ID Kas
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

    // State form
    var deskripsi by remember { mutableStateOf("") }
    var nominalString by remember { mutableStateOf("") }
    var fileUri by remember { mutableStateOf<Uri?>(null) }
    var currentPosterUrl by remember { mutableStateOf<String?>(null) }
    var status by remember { mutableStateOf("") } // Simpan status saat ini

    // Isi form ketika data detail berhasil diambil
    LaunchedEffect(selectedKas) {
        selectedKas?.let {
            deskripsi = it.deskripsi
            nominalString = it.nominal.toLong().toString() // Hapus desimal jika ada
            currentPosterUrl = it.buktiTransferUrl
            status = it.status
        }
    }

    // Effect untuk menangani hasil update/delete
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
    
    // Dialog Konfirmasi Hapus
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Batalkan Pembayaran?") },
            text = { Text("Status pembayaran akan di-reset menjadi Pending. Bukti transfer saat ini akan dihapus dan Anda perlu mengupload ulang.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteKas(kasId, userId)
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Batalkan & Reset", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDeleteDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            MyHipmiTopBar(
                title = "Edit Kas",
                onBackClick = { (navController as NavHostController).popBackStack() },
                actions = {
                    // Tombol Hapus di pojok kanan atas
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Hapus",
                            tint = Color.Red
                        )
                    }
                }
            )
        },
        containerColor = KasScreenBackground
    ) { paddingValues ->
        if (kasState is KasState.Loading && selectedKas == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = KasDarkGreen)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                // Field Nominal
                Text("Nominal Pembayaran", fontWeight = FontWeight.Medium, color = KasDarkGreen, modifier = Modifier.padding(bottom = 8.dp))
                OutlinedTextField(
                    value = nominalString,
                    onValueChange = { nominalString = it },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(8.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = KasAccentGreen
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Field Deskripsi
                Text("Deskripsi / Bulan", fontWeight = FontWeight.Medium, color = KasDarkGreen, modifier = Modifier.padding(bottom = 8.dp))
                OutlinedTextField(
                    value = deskripsi,
                    onValueChange = { deskripsi = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = KasAccentGreen
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Upload Bukti
                Text("Bukti Pembayaran", fontWeight = FontWeight.Medium, color = KasDarkGreen, modifier = Modifier.padding(bottom = 8.dp))
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
                    } else if (!currentPosterUrl.isNullOrEmpty()) {
                        AsyncImage(
                            model = currentPosterUrl,
                            contentDescription = "Preview Lama",
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
                    ) { Text("Ubah (Kamera)") }
                    
                    Button(
                        onClick = { galleryLauncher.launch("image/*") },
                        colors = ButtonDefaults.buttonColors(containerColor = KasAccentGreen, contentColor = KasDarkGreen),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).padding(start = 4.dp)
                    ) { Text("Ubah (Galeri)") }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Tombol Simpan
                Button(
                    onClick = {
                         if (deskripsi.isBlank() || nominalString.isBlank()) {
                            Toast.makeText(context, "Data tidak boleh kosong", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        val nominal = nominalString.toDoubleOrNull() ?: 0.0
                        
                        viewModel.updateKas(
                            context = context,
                            id = kasId,
                            userId = userId,
                            deskripsi = deskripsi,
                            nominal = nominal,
                            status = status, // Pertahankan status lama (atau ubah jadi 'pending' jika diedit user)
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
                        Icon(Icons.Default.Save, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Simpan Perubahan")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditKasScreenPreview() {
    MyHIPMITheme {
        EditKasScreen(rememberNavController(), kasId = 1)
    }
}
