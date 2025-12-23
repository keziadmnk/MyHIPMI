package com.example.myhipmi.ui.screen.kas

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.myhipmi.data.local.UserSessionManager
import com.example.myhipmi.ui.components.MyHipmiTopBar
import com.example.myhipmi.ui.theme.GreenPrimary
import com.example.myhipmi.ui.viewmodel.KasState
import com.example.myhipmi.ui.viewmodel.KasViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TambahKasScreen(
    navController: NavController,
    viewModel: KasViewModel = viewModel()
) {
    val context = LocalContext.current
    var deskripsi by remember { mutableStateOf("") }
    var nominalString by remember { mutableStateOf("") }
    
    val userSession = remember { UserSessionManager(context) }
    var userId by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        userSession.userId.collect { id ->
            if (id != null) userId = id
        }
    }

    val kasState by viewModel.kasState.collectAsState()
    LaunchedEffect(kasState) {
        when (val currentState = kasState) {
            is KasState.Success -> {
                navController.previousBackStackEntry?.savedStateHandle?.set("kas_action_message", currentState.message)
                viewModel.resetState()
                navController.popBackStack()
            }
            is KasState.Error -> {
                Toast.makeText(context, currentState.error, Toast.LENGTH_LONG).show()
                viewModel.resetState()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            MyHipmiTopBar(
                title = "Tambah Tagihan Kas",
                onBackClick = { navController.popBackStack() }
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            
            Text(
                text = "Nominal Tagihan",
                fontWeight = FontWeight.Medium,
                color = GreenPrimary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = nominalString,
                onValueChange = { newValue ->
                    nominalString = newValue.filter { it.isDigit() }
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Contoh: 50000") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(8.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = GreenPrimary,
                    unfocusedIndicatorColor = Color.LightGray
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Deskripsi / Bulan",
                fontWeight = FontWeight.Medium,
                color = GreenPrimary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = deskripsi,
                onValueChange = { deskripsi = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Contoh: Kas Bulan November") },
                shape = RoundedCornerShape(8.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = GreenPrimary,
                    unfocusedIndicatorColor = Color.LightGray
                )
            )

            Spacer(modifier = Modifier.weight(1f))

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
                        fileUri = null
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = kasState !is KasState.Loading,
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                shape = RoundedCornerShape(8.dp)
            ) {
                if (kasState is KasState.Loading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Simpan Tagihan", color = Color.White, fontSize = 16.sp)
                }
            }
        }
    }
}
