// app/src/main/java/com/example/myhipmi/ui/screen/rapat/RapatScreen.kt (MODIFIKASI)

package com.example.myhipmi.ui.screen.rapat

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold // <-- Import ini mungkin perlu
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.example.myhipmi.ui.components.MyHipmiTopBar // <-- Pastikan ini diimpor
import com.example.myhipmi.ui.screen.home.BottomNavBarContainer // <-- Pastikan ini diimpor

@Composable
fun RapatScreen(navController: NavHostController) {
    Scaffold(
        topBar = {
            MyHipmiTopBar(
                title = "Halaman Rapat",
                onBackClick = { navController.popBackStack() }
            )
        },
        bottomBar = {
            BottomNavBarContainer(
                onHome = { navController.navigate("home") },
                onKas = { navController.navigate("kas") },
                onRapat = { /* Do nothing, already here */ },
                onPiket = { navController.navigate("piket") },
                onEvent = { navController.navigate("event") }
            )
        }
    ) { innerPadding ->
        Text("Halaman Rapat", modifier = androidx.compose.ui.Modifier.padding(innerPadding))
    }
}