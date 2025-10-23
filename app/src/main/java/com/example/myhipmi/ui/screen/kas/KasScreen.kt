// app/src/main/java/com/example/myhipmi/ui/screen/kas/KasScreen.kt (MODIFIKASI)

package com.example.myhipmi.ui.screen.kas

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold // <-- Import ini mungkin perlu
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.example.myhipmi.ui.components.MyHipmiTopBar // <-- Pastikan ini diimpor
import com.example.myhipmi.ui.screen.home.BottomNavBarContainer // <-- Pastikan ini diimpor

@Composable
fun KasScreen(navController: NavHostController) {
    Scaffold(
        topBar = {
            MyHipmiTopBar(
                title = "Halaman Kas",
                onBackClick = { navController.popBackStack() }
            )
        },
        bottomBar = {
            BottomNavBarContainer(
                onHome = { navController.navigate("home") },
                onKas = { /* Do nothing, already here */ },
                onRapat = { navController.navigate("rapat") },
                onPiket = { navController.navigate("piket") },
                onEvent = { navController.navigate("event") }
            )
        }
    ) { innerPadding ->
        Text("Halaman Kas", modifier = androidx.compose.ui.Modifier.padding(innerPadding))
    }
}