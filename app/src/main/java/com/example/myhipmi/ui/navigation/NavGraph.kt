package com.example.myhipmi.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.myhipmi.ui.screen.kas.KasScreen
import com.example.myhipmi.ui.screen.event.AddEventScreen
import com.example.myhipmi.ui.screen.event.EventScreen
import com.example.myhipmi.ui.screen.home.HomeScreen
import com.example.myhipmi.ui.screen.landing.LandingPage
import com.example.myhipmi.ui.screen.login.LoginPage
import com.example.myhipmi.ui.screen.menu.AboutScreen
import com.example.myhipmi.ui.screen.piket.PiketScreen
import com.example.myhipmi.ui.screen.profile.ProfileScreen
import com.example.myhipmi.ui.screen.rapat.AddRapatScreen
import com.example.myhipmi.ui.screen.rapat.RapatDetailScreen
import com.example.myhipmi.ui.screen.rapat.RapatScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "landing"
    ) {
        // === Landing Page ===
        composable("landing") {
            LandingPage(
                onNavigateToLogin = { navController.navigate("login") }
            )
        }


        // === Login Page ===
        composable("login") {
            LoginPage(
                onLoginSuccess = { navController.navigate("home") },
                onBack = { navController.popBackStack() }
            )
        }

        // === Home ===
        composable("home") {
            HomeScreen(
                onNavigateToKas = { navController.navigate("kas") },
                onNavigateToRapat = { navController.navigate("rapat") },
                onNavigateToPiket = { navController.navigate("piket") },
                onNavigateToEvent = { navController.navigate("event") },
                onNavigateToProfile = { navController.navigate("profile") },
                onNavigateToAbout = { navController.navigate("about") },
                onLogout = { navController.navigate("login") }
            )
        }

        // === Menu utama ===
        composable("kas") {
            KasScreen(
                navController = navController,
                onHome = { navController.navigate("home") },
                onKas = { navController.navigate("kas") },
                onRapat = { navController.navigate("rapat") },
                onPiket = { navController.navigate("piket") },
                onEvent = { navController.navigate("event") }
            )
        }
        composable("rapat") { RapatScreen(navController) }
        composable("piket") { PiketScreen(navController) }
        composable("event") { EventScreen(navController) }

        // === Add screens ===
        composable("add_event") { AddEventScreen(navController) }
        composable("add_rapat") { AddRapatScreen(navController) }
        composable("profile") {
            ProfileScreen(navController = navController)
        }

        composable("about") {
            AboutScreen(navController = navController)
        }


        // === Detail Rapat ===
        composable(
            route = "rapat_detail/{title}/{date}/{time}/{location}/{isDone}",
            arguments = listOf(
                navArgument("title") { type = NavType.StringType },
                navArgument("date") { type = NavType.StringType },
                navArgument("time") { type = NavType.StringType },
                navArgument("location") { type = NavType.StringType },
                navArgument("isDone") { type = NavType.StringType }
            )
        ) { backStackEntry: NavBackStackEntry ->
            RapatDetailScreen(navController, backStackEntry)
        }
    }
}
