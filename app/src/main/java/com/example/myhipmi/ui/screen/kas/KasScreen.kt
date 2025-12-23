package com.example.myhipmi.ui.screen.kas

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.myhipmi.data.local.UserSessionManager
import com.example.myhipmi.data.remote.response.KasItem

import com.example.myhipmi.ui.components.MyHipmiTopBar
import com.example.myhipmi.ui.screen.home.BottomNavBarContainer
import com.example.myhipmi.ui.theme.GreenPrimary
import com.example.myhipmi.ui.theme.MyHIPMITheme
import com.example.myhipmi.ui.theme.StatusBelum
import com.example.myhipmi.ui.theme.StatusLunas
import com.example.myhipmi.ui.viewmodel.KasState
import com.example.myhipmi.ui.viewmodel.KasViewModel
import com.example.myhipmi.utils.KasNotificationHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KasScreen(
    navController: NavController,
    viewModel: KasViewModel = viewModel(),
    onHome: () -> Unit,
    onKas: () -> Unit,
    onRapat: () -> Unit,
    onPiket: () -> Unit,
    onEvent: () -> Unit
) {
    val context = LocalContext.current
    val userSession = remember { UserSessionManager(context) }
    var userId by remember { mutableIntStateOf(0) }
    var userName by remember { mutableStateOf("") }
    var userRole by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        userSession.userId.collect { id ->
            if (id != null) {
                userId = id
                viewModel.getKas(id)
            }
        }
    }
    LaunchedEffect(Unit) {
        userSession.userName.collect { name -> userName = name ?: "User" }
    }
    LaunchedEffect(Unit) {
        userSession.userRole.collect { role -> userRole = role ?: "Anggota" }
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val savedStateHandle = navBackStackEntry?.savedStateHandle
    val addKasSuccess = savedStateHandle?.get<Boolean>("add_kas_success")
    val paymentSuccess = savedStateHandle?.get<Boolean>("payment_success")
    val kasActionMessage = savedStateHandle?.get<String>("kas_action_message")
    
    LaunchedEffect(addKasSuccess, paymentSuccess, kasActionMessage) {
        if (addKasSuccess == true) {
            Toast.makeText(context, "Tagihan berhasil ditambahkan", Toast.LENGTH_SHORT).show()
            if (userId != 0) viewModel.getKas(userId)
            savedStateHandle?.remove<Boolean>("add_kas_success")
        }
        if (paymentSuccess == true) {
            Toast.makeText(context, "Pembayaran berhasil dikirim", Toast.LENGTH_SHORT).show()
            if (userId != 0) viewModel.getKas(userId)
            savedStateHandle?.remove<Boolean>("payment_success")
        }
        if (kasActionMessage != null) {
            Toast.makeText(context, kasActionMessage, Toast.LENGTH_SHORT).show()
            if (userId != 0) viewModel.getKas(userId)
            savedStateHandle?.remove<String>("kas_action_message")
        }
    }

    val kasList by viewModel.kasList.collectAsState()
    val kasState by viewModel.kasState.collectAsState()


    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Tagihan / Pending", "Riwayat Lunas")
    
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                MyHipmiTopBar(
                    title = "Kas Saya",
                    onBackClick = { (navController as NavHostController).popBackStack() },
                    onNotificationClick = { (navController as NavHostController).navigate("notifications") },
                    actions = {
                        IconButton(onClick = { 
                            KasNotificationHelper.showKasNotification(context)
                            Toast.makeText(context, "Notifikasi Kas Dikirim", Toast.LENGTH_SHORT).show()
                        }) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = "Test Notification",
                                tint = GreenPrimary
                            )
                        }
                    }
                )
            },
            bottomBar = {
                BottomNavBarContainer(
                    navController = navController as NavHostController,
                    onHome = onHome,
                    onKas = onKas,
                    onRapat = onRapat,
                    onPiket = onPiket,
                    onEvent = onEvent
                )
            },
            containerColor = Color.White
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                
                // Tab Row
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = Color.White,
                    contentColor = GreenPrimary,
                    indicator = { tabPositions ->
                        androidx.compose.material3.TabRowDefaults.Indicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                            color = GreenPrimary
                        )
                    }
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = { Text(title, fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal) }
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))

                if (kasState is KasState.Loading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = GreenPrimary)
                    }
                } else {
                    val filteredTransactions = if (selectedTabIndex == 0) {
                        kasList.filter { it.status.equals("pending", ignoreCase = true) || it.status.equals("ditolak", ignoreCase = true) }
                    } else {
                        kasList.filter { it.status.equals("lunas", ignoreCase = true) }
                    }

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        if (filteredTransactions.isEmpty()) {
                            item {
                                Text(
                                    text = if (selectedTabIndex == 0) "Tidak ada tagihan pending" else "Belum ada riwayat lunas",
                                    modifier = Modifier.padding(16.dp),
                                    color = Color.Gray
                                )
                            }
                        } else {
                            items(filteredTransactions) { transaction ->
                                KasItemCard(
                                    transaction = transaction,
                                    onClick = {
                                        if (transaction.status.equals("lunas", ignoreCase = true)) {
                                            navController.navigate("edit_kas/${transaction.id}")
                                        } else {
                                            navController.navigate("bayar_tagihan/${transaction.id}")
                                        }
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { 
                        navController.navigate("tambah_kas")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Buat Tagihan", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

    }
}

@Composable
fun KasItemCard(transaction: KasItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .border(1.dp, Color(0xFFEEEEEE), RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(transaction.deskripsi, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("Rp ${transaction.nominal}", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.Black)
                Text(transaction.tanggal.take(10), fontSize = 12.sp, color = Color.Gray)
            }
            Column(horizontalAlignment = Alignment.End) {
                val isLunas = transaction.status.equals("lunas", ignoreCase = true)
                val isDitolak = transaction.status.equals("ditolak", ignoreCase = true)
                
                val (backgroundColor, textColor) = when {
                    isLunas -> StatusLunas to GreenPrimary
                    isDitolak -> Color.Red.copy(alpha = 0.2f) to Color.Red
                    else -> StatusBelum to Color(0xFFFFA500) 
                }
                
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(backgroundColor)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(transaction.status.uppercase(), color = textColor, fontSize = 12.sp)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun KasScreenPreview() {
    MyHIPMITheme {
        val navController = rememberNavController()
        KasScreen(
            navController = navController,
            onHome = {},
            onKas = {},
            onRapat = {},
            onPiket = {},
            onEvent = {}
        )
    }
}
