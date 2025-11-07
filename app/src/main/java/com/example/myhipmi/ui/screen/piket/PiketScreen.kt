// app/src/main/java/com/example/myhipmi/ui/screen/piket/PiketScreen.kt (MODIFIKASI)

package com.example.myhipmi.ui.screen.piket

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold // <-- Import ini mungkin perlu
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.navigation.NavHostController
import com.example.myhipmi.ui.components.MyHipmiTopBar
import com.example.myhipmi.ui.components.MenuDrawer
import com.example.myhipmi.ui.screen.home.BottomNavBarContainer

@Composable
fun PiketScreen(navController: NavHostController) {
    var isMenuVisible by remember { mutableStateOf(false) }
    
    Box(modifier = androidx.compose.ui.Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                MyHipmiTopBar(
                    title = "Halaman Piket",
                    onBackClick = { navController.popBackStack() },
                    onMenuClick = { isMenuVisible = true }
                )
            },
        bottomBar = {
            BottomNavBarContainer(
                navController = navController,
                onHome = { navController.navigate("home") },
                onKas = { navController.navigate("kas") },
                onRapat = { navController.navigate("rapat") },
                onPiket = { /* Do nothing, already here */ },
                onEvent = { navController.navigate("event") }
            )
        }
    ) { innerPadding ->
        Text("Halaman Piket", modifier = androidx.compose.ui.Modifier.padding(innerPadding))
    }
    }
    
        // Menu Drawer
        MenuDrawer(
            isVisible = isMenuVisible,
            onDismiss = { isMenuVisible = false },
            userName = "Nagita Slavina",
            userRole = "Sekretaris Umum",
            onProfileClick = {
                isMenuVisible = false
                navController.navigate("profile")
            },
            onAboutClick = {
                isMenuVisible = false
                navController.navigate("about")
            },
            onLogoutClick = {
                isMenuVisible = false
                // TODO: Handle logout
            }
        )
    }