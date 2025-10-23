package com.example.myhipmi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myhipmi.ui.screen.landing.LandingPage
import com.example.myhipmi.ui.screen.login.LoginPage
import com.example.myhipmi.ui.screen.home.HomeScreen
import com.example.myhipmi.ui.screen.kas.KasScreen
import com.example.myhipmi.ui.screen.rapat.RapatScreen
import com.example.myhipmi.ui.screen.piket.PiketScreen
import com.example.myhipmi.ui.screen.event.EventScreen
import com.example.myhipmi.ui.theme.MyHIPMITheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MyHIPMITheme {
                MyHipmiApp()
            }
        }
    }
}

@Composable
fun MyHipmiApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "landing"
    ) {
        // Landing Page
        composable("landing") {
            LandingPage(
                onNavigateToLogin = { navController.navigate("login") }
            )
        }

        // Login Page
        composable("login") {
            LoginPage(
                onLoginSuccess = { navController.navigate("home") },
                onBack = { navController.popBackStack() }
            )
        }

        // Home (dengan Bottom Navigation)
        composable("home") {
            HomeScreen(
                onNavigateToKas = { navController.navigate("kas") },
                onNavigateToRapat = { navController.navigate("rapat") },
                onNavigateToPiket = { navController.navigate("piket") },
                onNavigateToEvent = { navController.navigate("event") }
            )
        }

        // Halaman Bottom Nav lainnya
        composable("kas") { KasScreen(navController) }
        composable("rapat") { RapatScreen(navController) }
        composable("piket") { PiketScreen(navController) }
        composable("event") { EventScreen(navController) }
    }
}
