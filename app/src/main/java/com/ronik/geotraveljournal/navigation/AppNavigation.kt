package com.ronik.geotraveljournal.navigation

import MapFragmentScreen
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ronik.geotraveljournal.activity.auth.AuthScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController, startDestination = "auth") {
        composable("auth") {

            AuthScreen(navController = navController)
        }
        composable("mapFragment") {
            MapFragmentScreen(
                navController = navController
           )
        }
    }
}
