package com.example.maplink.navigation

sealed class Routes(val route: String) {

    data object Splash : Routes("splash")

    data object Login : Routes("login")

    data object Register : Routes("register")

    data object Home : Routes("home")

    data object Map : Routes("map")

}