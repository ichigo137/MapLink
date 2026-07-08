package com.example.maplink.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.maplink.data.repository.FriendRequestsScreen
import com.example.maplink.service.LocationServiceManager
import com.example.maplink.ui.auth.login.LoginScreen
import com.example.maplink.ui.auth.register.RegisterScreen
import com.example.maplink.ui.screens.HomeScreen
import com.example.maplink.ui.screens.MapScreen
import com.example.maplink.ui.screens.friends.FriendsScreen
import com.example.maplink.ui.screens.profile.ProfileScreen
import com.example.maplink.ui.screens.profile.ProfileViewModel
import com.example.maplink.ui.search.SearchScreen
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.maplink.ui.screens.profile.LocationPermissionHandler

@Composable
fun NavGraph(
    navController: NavHostController
) {

    val context = LocalContext.current

    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()

    val startDestination =
        if (auth.currentUser != null) {
            Routes.Home
        } else {
            Routes.Login
        }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        composable(Routes.Login) {

            LoginScreen(
                onLogin = {

                    navController.navigate(Routes.Home) {

                        popUpTo(Routes.Login) {
                            inclusive = true
                        }

                        launchSingleTop = true
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

                        launchSingleTop = true
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

                onFriends = {
                    navController.navigate(Routes.FRIENDS)
                },

                onProfile = {
                    navController.navigate(Routes.Profile)
                },

                onLogout = {

                    val uid = auth.currentUser?.uid

                    if (uid == null) {

                        LocationServiceManager.stop(context)
                        auth.signOut()

                        navController.navigate(Routes.Login) {
                            popUpTo(0) {
                                inclusive = true
                            }

                            launchSingleTop = true
                        }

                    } else {

                        firestore.collection("users")
                            .document(uid)
                            .update(
                                mapOf(
                                    "online" to false,
                                    "lastUpdated" to Timestamp.now()
                                )
                            )
                            .addOnCompleteListener {

                                LocationServiceManager.stop(context)

                                auth.signOut()

                                navController.navigate(Routes.Login) {
                                    popUpTo(0) {
                                        inclusive = true
                                    }

                                    launchSingleTop = true
                                }
                            }
                    }
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

            val profileViewModel: ProfileViewModel = viewModel()

            val locationSharingEnabled by
            profileViewModel.locationSharingEnabled.collectAsState()

            val isUpdating by
            profileViewModel.isUpdating.collectAsState()

            LocationPermissionHandler(
                onPermissionsReady = {
                    profileViewModel.setLocationSharingEnabled(true)
                }
            ) { requestEnableLocationSharing, permissionIssue ->

                ProfileScreen(
                    locationSharingEnabled = locationSharingEnabled,
                    isUpdating = isUpdating,
                    permissionIssue = permissionIssue,

                    onLocationSharingChanged = { enabled ->

                        if (enabled) {

                            requestEnableLocationSharing()

                        } else {

                            profileViewModel
                                .setLocationSharingEnabled(false)
                        }
                    }
                )
            }
        }

        composable(Routes.Map) {
            MapScreen()
        }

        composable(Routes.FRIENDS) {
            FriendsScreen()
        }
    }
}