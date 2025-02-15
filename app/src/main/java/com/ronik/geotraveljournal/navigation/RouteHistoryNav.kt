package com.ronik.geotraveljournal.navigation

import MapFragmentScreen
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ronik.geotraveljournal.activity.journal.RouteDetailScreen
import com.ronik.geotraveljournal.activity.journal.RouteHistoryScreen
import com.ronik.geotraveljournal.serializers.Route
import com.ronik.geotraveljournal.serializers.RouteDetail
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun RouteHistoryNav(
    routes: List<Route>,
    routeDetails: Map<Int, RouteDetail>,
    coroutineScope: CoroutineScope,
) {
    val navController = rememberNavController()
    val selectedRouteDetail = remember { mutableStateOf<RouteDetail?>(null) }

    NavHost(navController, startDestination = "routeList") {
        composable("routeList") {
            RouteHistoryScreen(
                routes = routes,
                routeDetailsProvider = { route ->
                    routeDetails[route.id] ?: RouteDetail(
                        title = route.title,
                        description = "Нет данных о маршруте",
                        distance = 0.0,
                        route = emptyList(),
                        createdAt = route.createdAt
                    )
                },
                onRouteClick = { routeDetail ->
                    selectedRouteDetail.value = routeDetail
                    coroutineScope.launch {
                        navController.navigate("routeDetail")
                    }
                },
                navController = navController
            )
        }

        composable(
            route = "mapFragment?routePoints={routePoints}",
            arguments = listOf(navArgument("routePoints") {
                type = NavType.StringType
                defaultValue = ""
                nullable = true
            })
        ) { backStackEntry ->
            val routePoints = backStackEntry.arguments?.getString("routePoints")

            Log.d("ROUTE NAV", "route = $routePoints")
            MapFragmentScreen(
                navController = navController,
                routePoints = if (routePoints.isNullOrEmpty()) null else routePoints
            )
        }

        composable("routeDetail") {
            selectedRouteDetail.value?.let { routeDetail ->
                RouteDetailScreen(
                    routeDetail = routeDetail,
                    onDismiss = {
                        selectedRouteDetail.value = null
                        navController.popBackStack()
                    },
                    navController = navController
                )
            }
        }
    }
}
