package com.example.myhipmi.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Event
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myhipmi.ui.theme.PrimaryGreen

@Composable
fun BottomNavBar(
    selectedIndex: Int = 0,
    onHome: () -> Unit,
    onKas: () -> Unit,
    onRapat: () -> Unit,
    onPiket: () -> Unit,
    onEvent: () -> Unit
) {
    val items = listOf(
        Triple("Home", Icons.Default.Home, onHome),
        Triple("Kas", Icons.Default.Payments, onKas),
        Triple("Rapat", Icons.Default.Groups, onRapat),
        Triple("Piket", Icons.Default.CalendarMonth, onPiket),
        Triple("Event", Icons.Default.Event, onEvent)
    )

    Surface(
        color = Color(0xFFDDECCF),
        shadowElevation = 16.dp,
        tonalElevation = 8.dp,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEachIndexed { index, item ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 8.dp)
                        .clickable {
                            item.third.invoke()
                        }
                ) {
                    Icon(
                        imageVector = item.second,
                        contentDescription = item.first,
                        tint = if (selectedIndex == index) PrimaryGreen else Color.Gray,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = item.first,
                        color = if (selectedIndex == index) PrimaryGreen else Color.Gray,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}