package com.example.myhipmi.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.myhipmi.ui.screen.kas.KasScreen
import com.example.myhipmi.ui.screen.kas.PembayaranKasScreen
import com.example.myhipmi.ui.screen.kas.TambahKasScreen
import com.example.myhipmi.ui.screen.kas.EditKasScreen
import com.example.myhipmi.ui.screen.kas.BayarTagihanScreen
import com.example.myhipmi.ui.screen.event.AddEventScreen
import com.example.myhipmi.ui.screen.event.DetailEventScreen
import com.example.myhipmi.ui.screen.event.EditEventScreen
import com.example.myhipmi.ui.screen.event.EventScreen
import com.example.myhipmi.ui.screen.home.HomeScreen
import com.example.myhipmi.ui.screen.landing.LandingPage
import com.example.myhipmi.ui.screen.login.LoginPage
import com.example.myhipmi.ui.screen.menu.AboutScreen
import com.example.myhipmi.ui.screen.menu.ProfileScreen
import com.example.myhipmi.ui.screen.piket.DetailPiketScreen
import com.example.myhipmi.ui.screen.piket.DetailRiwayatPiketScreen
import com.example.myhipmi.ui.screen.piket.PiketScreen
import com.example.myhipmi.ui.screen.rapat.AddRapatScreen
import com.example.myhipmi.ui.screen.rapat.EditRapatScreen
import com.example.myhipmi.ui.screen.rapat.RapatDetailScreen
import com.example.myhipmi.ui.screen.rapat.RapatScreen
import com.example.myhipmi.ui.screen.notification.NotificationScreen


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
                navController = navController
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
        composable("pembayaran_kas") {
            PembayaranKasScreen(navController = navController)
        }
        composable("tambah_kas") {
            TambahKasScreen(navController = navController)
        }
        
        // Rute untuk Bayar Tagihan (Khusus Pending)
        composable(
            route = "bayar_tagihan/{kasId}",
            arguments = listOf(
                navArgument("kasId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val kasId = backStackEntry.arguments?.getInt("kasId") ?: 0
            BayarTagihanScreen(navController = navController, kasId = kasId)
        }
        
        // Rute untuk Detail/Edit Kas (Khusus Lunas/History)
        composable(
            route = "edit_kas/{kasId}",
            arguments = listOf(
                navArgument("kasId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val kasId = backStackEntry.arguments?.getInt("kasId") ?: 0
            EditKasScreen(navController = navController, kasId = kasId)
        }

        composable("rapat") { RapatScreen(navController) }
        composable("piket") { PiketScreen(navController) }
        composable("piket/upload") { DetailPiketScreen(navController) }
        composable(
            route = "piket/detail/{idAbsenPiket}",
            arguments = listOf(
                navArgument("idAbsenPiket") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val idAbsenPiket = backStackEntry.arguments?.getInt("idAbsenPiket") ?: 0
            DetailRiwayatPiketScreen(navController = navController, idAbsenPiket = idAbsenPiket)
        }
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
            route = "rapat_detail/{idAgenda}/{title}/{date}/{startTime}/{endTime}/{location}/{isDone}",
            arguments = listOf(
                navArgument("idAgenda") { type = NavType.StringType },
                navArgument("title") { type = NavType.StringType },
                navArgument("date") { type = NavType.StringType },
                navArgument("startTime") { type = NavType.StringType },
                navArgument("endTime") { type = NavType.StringType },
                navArgument("location") { type = NavType.StringType },
                navArgument("isDone") { type = NavType.StringType }
            )
        ) { backStackEntry: NavBackStackEntry ->
            RapatDetailScreen(navController, backStackEntry)
        }
        
        // === Edit Rapat ===
        composable(
            route = "edit_rapat/{idAgenda}",
            arguments = listOf(
                navArgument("idAgenda") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val idAgenda = backStackEntry.arguments?.getInt("idAgenda") ?: 0
            EditRapatScreen(navController = navController, idAgenda = idAgenda)
        }

        // === Detail Event ===
        composable(
            "detail_event/{eventId}",
            arguments = listOf(
                navArgument("eventId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            // Ambil ID Event dari argumen, default ke 0 jika tidak ada (untuk keamanan)
            val eventId = backStackEntry.arguments?.getInt("eventId") ?: 0

            // Panggil DetailEventScreen dan kirimkan eventId
            DetailEventScreen(navController = navController, eventId = eventId)
        }
        composable(
            route = "edit_event/{eventId}",
            arguments = listOf(
                navArgument("eventId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getInt("eventId") ?: 0
            EditEventScreen(navController = navController, eventId = eventId)
        }
        
        // === Notification Screen ===
        composable("notifications") {
            NotificationScreen(navController = navController)
        }
    }
}
