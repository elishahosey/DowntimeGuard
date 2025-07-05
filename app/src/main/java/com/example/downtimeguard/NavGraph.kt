package com.example.downtimeguard
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.downtimeguard.ui.theme.DashboardScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "login") {
//        composable("login") { LoginScreen(navController) }
//        composable("home") { HomeScreen(navController) }
        composable("dashboard") { DashboardScreen(navController) }
        composable("home") { MainScreenUI(navController) }
    }
}