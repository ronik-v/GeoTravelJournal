package com.ronik.geotraveljournal.navigation

import MapFragmentScreen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ronik.geotraveljournal.activity.auth.AuthScreen
import com.ronik.geotraveljournal.data.TokenManager

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current

    val token by TokenManager.getTokenFlow(context).collectAsState(initial = null)

    LaunchedEffect(token) {
        if (!token.isNullOrEmpty()) {
            navController.navigate("mapFragment") {
                popUpTo("auth") { inclusive = true }
            }
        }
    }

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
