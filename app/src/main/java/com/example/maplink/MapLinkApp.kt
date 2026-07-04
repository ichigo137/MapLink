package com.example.maplink

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.maplink.navigation.NavGraph

@Composable
fun MapLinkApp() {

    val navController = rememberNavController()

    NavGraph(navController)
}