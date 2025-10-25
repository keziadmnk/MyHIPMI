package com.example.myhipmi.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.myhipmi.ui.screen.event.AddEventScreen
import com.example.myhipmi.ui.screen.event.EventScreen
import com.example.myhipmi.ui.screen.home.HomeScreen
import com.example.myhipmi.ui.screen.kas.KasScreen
import com.example.myhipmi.ui.screen.landing.LandingPage
import com.example.myhipmi.ui.screen.login.LoginPage
import com.example.myhipmi.ui.screen.piket.PiketScreen
import com.example.myhipmi.ui.screen.rapat.AddRapatScreen
import com.example.myhipmi.ui.screen.rapat.RapatScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "landing"
    ) {
        composable("landing") {
            LandingPage(
                onNavigateToLogin = { navController.navigate("login") }
            )
        }

        composable("login") {
            LoginPage(
                onLoginSuccess = { navController.navigate("home") },
                onBack = { navController.popBackStack() }
            )
        }

        composable("home") {
            HomeScreen(
                onNavigateToKas = { navController.navigate("kas") },
                onNavigateToRapat = { navController.navigate("rapat") },
                onNavigateToPiket = { navController.navigate("piket") },
                onNavigateToEvent = { navController.navigate("event") }
            )
        }

        composable("kas") { KasScreen(navController) }
        composable("rapat") { RapatScreen(navController) }
        composable("piket") { PiketScreen(navController) }
        composable("event") { EventScreen(navController) }
        composable("add_event") { AddEventScreen(navController) }
        composable("add_rapat") { AddRapatScreen(navController) }

    }
}
