package com.example.myhipmi.ui.screen.kas

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.myhipmi.ui.components.BottomNavBar
import com.example.myhipmi.ui.screen.home.BottomNavBarContainer
import com.example.myhipmi.ui.theme.MyHIPMITheme

data class Transaction(
    val name: String,
    val period: String,
    val amount: String,
    val status: String,
    val paymentDate: String? = null
)

val dummyTransactions = listOf(
    Transaction("Budi Santoso", "Oktober 2025", "Rp 50.000", "Lunas", "Dibayar pada: 10 Oktober 2025"),
    Transaction("Dedi Pratama", "Oktober 2025", "Rp 50.000", "Belum")
)

val lightGreen = Color(0xFFE6F5E6)
val darkGreen = Color(0xFF6E8B6E)
val accentGreen = Color(0xFFA5D6A7)
val statusLunas = Color(0xFFC8E6C9)
val statusBelum = Color(0xFFFFCDD2)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KasScreen(
    navController: NavController,
    onHome: () -> Unit,
    onKas: () -> Unit,
    onRapat: () -> Unit,
    onPiket: () -> Unit,
    onEvent: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Kas", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                    }
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        bottomBar = {
            BottomNavBarContainer(
                navController = navController as NavHostController, // <-- Cast diperlukan karena signature NavController, tapi Anda bisa ubah signature-nya.
                onHome = onHome,
                onKas = onKas,
                onRapat = onRapat,
                onPiket = onPiket,
                onEvent = onEvent
            )
        },
        containerColor = lightGreen
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = { /* TODO */ },
                    colors = ButtonDefaults.buttonColors(containerColor = accentGreen),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Bayar")
                    Text("Bayar", color = darkGreen)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            TotalKasCard()
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(dummyTransactions) { transaction ->
                    TransactionItem(transaction)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { /* TODO */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFF59D)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Lihat Riwayat Lengkap", color = Color.Black)
            }
        }
    }
}

@Composable
fun TotalKasCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = accentGreen)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Total Kas HIPMI", fontSize = 16.sp, color = darkGreen)
            Text("Rp 1.500.000", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                StatusIndicator(color = Color.Green, text = "18 Lunas")
                Spacer(modifier = Modifier.width(16.dp))
                StatusIndicator(color = Color.Yellow, text = "3 Belum Lunas")
            }
        }
    }
}

@Composable
fun StatusIndicator(color: Color, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text, fontSize = 12.sp, color = darkGreen)
    }
}

@Composable
fun TransactionItem(transaction: Transaction) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(transaction.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(transaction.period, fontSize = 14.sp, color = Color.Gray)
                Text(transaction.amount, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.Black)
                transaction.paymentDate?.let {
                    Text(it, fontSize = 12.sp, color = Color.Gray)
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                val (backgroundColor, textColor) = if (transaction.status == "Lunas") {
                    statusLunas to darkGreen
                } else {
                    statusBelum to Color.Red
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(backgroundColor)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(transaction.status, color = textColor, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    IconButton(onClick = { /*TODO*/ }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.Blue)
                    }
                    IconButton(onClick = { /*TODO*/ }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                    }
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
