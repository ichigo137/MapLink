package com.example.maplink.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.maplink.ui.screens.HomeScreen
import com.example.maplink.ui.screens.MapScreen

@Composable
fun NavGraph(
    navController: NavHostController
) {

    NavHost(
        navController = navController,
        startDestination = Routes.Home.route
    ) {

        composable(Routes.Home.route) {
            HomeScreen(
                onShareLocationClick = {
                    navController.navigate(Routes.Map.route)
                }
            )
        }

        composable(Routes.Map.route) {
            MapScreen()
        }

    }
}