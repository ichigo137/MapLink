package com.example.maplink.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.maplink.ui.screens.HomeScreen
import com.example.maplink.ui.screens.MapScreen
import com.example.maplink.ui.auth.login.LoginScreen
import com.example.maplink.ui.auth.register.RegisterScreen

@Composable
fun NavGraph() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.Login
    ) {
        composable(Routes.Login) {
            LoginScreen(
                onLogin = {
                    navController.navigate(Routes.Home) {
                        popUpTo(Routes.Login) {
                            inclusive = true
                        }
                    }
                },
                onRegister = {
                    navController.navigate(Routes.Register)
                }
            )
        }
        composable(Routes.Home) {
            HomeScreen(
                onShareLocation = {
                    navController.navigate(Routes.Map)
                }
            )
        }

        composable(Routes.Map) {
            MapScreen()
        }

        composable(Routes.Register) {
            RegisterScreen(
                onRegister = {
                    // Firebase next
                },
                onLogin = {
                    navController.popBackStack()
                }
            )
        }

    }
}