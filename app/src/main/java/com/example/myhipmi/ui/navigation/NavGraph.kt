package com.example.myhipmi.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.myhipmi.ui.screen.home.HomeScreen
import com.example.myhipmi.ui.screen.kas.KasScreen
import com.example.myhipmi.ui.screen.rapat.RapatScreen
import com.example.myhipmi.ui.screen.piket.PiketScreen
import com.example.myhipmi.ui.screen.event.EventScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") { HomeScreen() }
        composable("kas") { KasScreen(navController) }
        composable("rapat") { RapatScreen(navController) }
        composable("piket") { PiketScreen(navController) }
        composable("event") { EventScreen(navController) }
    }
}