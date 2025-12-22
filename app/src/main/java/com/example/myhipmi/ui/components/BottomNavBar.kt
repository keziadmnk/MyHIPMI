package com.example.myhipmi.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myhipmi.ui.theme.GreenPrimary

data class NavItem(
    val label: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)

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
        NavItem("Home", Icons.Default.Home, onHome),
        NavItem("Kas", Icons.Default.Payments, onKas),
        NavItem("Rapat", Icons.Default.Groups, onRapat),
        NavItem("Piket", Icons.Default.CalendarMonth, onPiket),
        NavItem("Event", Icons.Default.Event, onEvent)
    )

    Surface(
        color = Color.White,
        shadowElevation = 8.dp,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEachIndexed { index, item ->
                NavBarItem(
                    item = item,
                    isSelected = selectedIndex == index,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun NavBarItem(
    item: NavItem,
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1f,
        animationSpec = tween(durationMillis = 200),
        label = "scale"
    )

    val iconColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else Color(0xFF6B7280),
        animationSpec = tween(durationMillis = 200),
        label = "iconColor"
    )

    val textColor by animateColorAsState(
        targetValue = if (isSelected) GreenPrimary else Color(0xFF9CA3AF),
        animationSpec = tween(durationMillis = 200),
        label = "textColor"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clickable(
                onClick = item.onClick,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )
            .padding(vertical = 4.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(56.dp)
                .scale(scale)
        ) {
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(GreenPrimary)
                )
            }
            Icon(
                imageVector = item.icon,
                contentDescription = item.label,
                tint = iconColor,
                modifier = Modifier.size(26.dp)
            )
        }

        Spacer(Modifier.height(4.dp))

        Text(
            text = item.label,
            color = textColor,
            fontSize = 12.sp,
            style = MaterialTheme.typography.labelSmall
        )
    }
}