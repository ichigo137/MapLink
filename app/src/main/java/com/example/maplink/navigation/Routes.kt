package com.example.maplink.navigation

<<<<<<< HEAD
object Routes {
    const val Splash = "splash"
    const val Login = "login"
    const val Register = "register"
    const val Home = "home"

    const val Map = "map"
=======
sealed class Routes(val route: String) {

    data object Splash : Routes("splash")

    data object Login : Routes("login")

    data object Register : Routes("register")

    data object Home : Routes("home")

    data object Map : Routes("map")

>>>>>>> 074809c44897261d246e1b34d1b186b8818f7de4
}