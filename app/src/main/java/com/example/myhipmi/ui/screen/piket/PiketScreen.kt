// app/src/main/java/com/example/myhipmi/ui/screen/piket/PiketScreen.kt (MODIFIKASI)

package com.example.myhipmi.ui.screen.piket

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold // <-- Import ini mungkin perlu
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.example.myhipmi.ui.components.MyHipmiTopBar // <-- Pastikan ini diimpor
import com.example.myhipmi.ui.screen.home.BottomNavBarContainer // <-- Pastikan ini diimpor

@Composable
fun PiketScreen(navController: NavHostController) {
    Scaffold(
        topBar = {
            MyHipmiTopBar(
                title = "Halaman Piket",
                onBackClick = { navController.popBackStack() }
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