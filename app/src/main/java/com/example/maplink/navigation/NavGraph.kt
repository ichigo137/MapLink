package com.example.maplink.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.maplink.ui.auth.login.LoginScreen
import com.example.maplink.ui.auth.register.RegisterScreen
import com.example.maplink.ui.screens.HomeScreen
import com.example.maplink.ui.screens.MapScreen
import com.example.maplink.ui.profile.ProfileScreen
import com.example.maplink.ui.requests.FriendRequestsScreen
import com.example.maplink.ui.search.SearchScreen
@Composable
fun NavGraph(
    navController: NavHostController
) {

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

        composable(Routes.Register) {
            RegisterScreen(
                onRegister = { _, _, _ ->

                    navController.navigate(Routes.Home) {
                        popUpTo(Routes.Login) {
                            inclusive = true
                        }
                    }

                },
                onLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.Home) {
            HomeScreen(
                onOpenMap = {
                    navController.navigate(Routes.Map)
                },

                onSearchUsers = {
                    navController.navigate(Routes.Search)
                },

                onFriendRequests = {
                    navController.navigate(Routes.Requests)
                },

                onProfile = {
                    navController.navigate(Routes.Profile)
                }
            )
        }

        composable(Routes.Search) {
            SearchScreen()
        }

        composable(Routes.Requests) {
            FriendRequestsScreen()
        }

        composable(Routes.Profile) {
            ProfileScreen()
        }

        composable(Routes.Map) {
            MapScreen()
        }
    }
}