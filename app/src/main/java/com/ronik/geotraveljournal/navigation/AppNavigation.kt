package com.ronik.geotraveljournal.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ronik.geotraveljournal.activity.route_history.MapFragmentScreen
import com.ronik.geotraveljournal.activity.route_history.RouteDetailScreen
import com.ronik.geotraveljournal.activity.route_history.RouteHistoryScreen
import com.ronik.geotraveljournal.serializers.Route
import com.ronik.geotraveljournal.serializers.RouteDetail
import com.ronik.geotraveljournal.serializers.RoutePoint
import kotlinx.coroutines.launch

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val coroutineScope = rememberCoroutineScope()
    val selectedRouteDetail = remember { mutableStateOf<RouteDetail?>(null) }

    NavHost(navController, startDestination = "routeList") {
        composable("routeList") {
            val routes = listOf(
                Route(1, "Маршрут 1", "2025-01-04T20:25:11.901755Z"),
                Route(2, "Маршрут 2", "2025-01-04T20:40:11.150786Z"),
                Route(3, "Маршрут 3", "2025-01-04T20:42:09.585452Z")
            )

            RouteHistoryScreen(
                routes = routes,
                routeDetailsProvider = { route ->
                    RouteDetail(
                        title = route.title,
                        description = "Описание маршрута ${route.id}",
                        distance = 29.9,
                        route = listOf(RoutePoint(56.65655, 36.65655)),
                        createdAt = route.createdAt
                    )
                },
                onRouteClick = { routeDetail ->
                    coroutineScope.launch {
                        selectedRouteDetail.value = routeDetail
                    }
                },
                navController = navController
            )
        }
        composable("mapFragment") {
            MapFragmentScreen()
        }
    }

    selectedRouteDetail.value?.let { routeDetail ->
        RouteDetailScreen(
            routeDetail = routeDetail,
            onDismiss = { selectedRouteDetail.value = null }
        )
    }
}
