package com.example.downtimeguard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.downtimeguard.ui.theme.AppPickerScreen
import com.example.downtimeguard.ui.theme.MainScreenUI
import com.example.downtimeguard.ui.theme.viewmodel.AppUsageViewModel

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "home") {
//        composable("login") { LoginScreen(navController) }
//        composable("home") { HomeScreen(navController) }
//        composable("dashboard") { DashboardScreen(navController) }
        composable("home") { MainScreenUI(navController) }
        //sending data to the AppPickerScreen
        composable("appPicker"){
            val vm: AppUsageViewModel = hiltViewModel()
            val apps by vm.apps.observeAsState(emptyList())
            AppPickerScreen(
                navController =navController,
                viewModel = vm,
                onDone = { selected -> navController.popBackStack() }
            ) }
    }

    //home=>Dashboard
}