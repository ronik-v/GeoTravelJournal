package com.ronik.geotraveljournal.navigation

import MapFragmentScreen
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ronik.geotraveljournal.activity.journal.RouteDetailScreen
import com.ronik.geotraveljournal.activity.journal.RouteHistoryScreen
import com.ronik.geotraveljournal.viewmodel.RouteViewModel

@Composable
fun RouteHistoryNav(
    viewModel: RouteViewModel
) {
    val navController = rememberNavController()

    NavHost(navController, startDestination = "routeList") {
        composable("routeList") {
            RouteHistoryScreen(
                viewModel = viewModel,
                onRouteClick = { routeId ->
                    navController.navigate("routeDetail/$routeId")
                },
                navController = navController
            )
        }

        composable(
            route = "mapFragment?routePoints={routePoints}",
            arguments = listOf(
                navArgument("routePoints") {
                    type = NavType.StringType
                    defaultValue = ""
                    nullable = true
                }
            )
        ) { backStackEntry ->
            val routePoints = backStackEntry.arguments?.getString("routePoints")
            MapFragmentScreen(
                navController = navController,
                routePoints = if (routePoints.isNullOrEmpty()) null else routePoints
            )
        }

        composable(
            route = "routeDetail/{routeId}",
            arguments = listOf(
                navArgument("routeId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val routeId = backStackEntry.arguments?.getLong("routeId") ?: 0L
            RouteDetailScreen(
                routeId = routeId,
                viewModel = viewModel,
                onDismiss = { navController.popBackStack() },
                navController = navController
            )
        }
    }
}
