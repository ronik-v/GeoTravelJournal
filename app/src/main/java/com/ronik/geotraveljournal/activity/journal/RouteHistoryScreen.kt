package com.ronik.geotraveljournal.activity.journal

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ronik.geotraveljournal.serializers.Route
import com.ronik.geotraveljournal.serializers.RouteDetail
import com.ronik.geotraveljournal.utils.GeoTravelTheme
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun RouteHistoryScreen(
    routes: List<Route>,
    routeDetailsProvider: (Route) -> RouteDetail,
    onRouteClick: (RouteDetail) -> Unit,
    navController: NavController
) {
    val groupedRoutes = routes
        .map { route ->
            val dateTime = LocalDateTime.parse(route.createdAt, DateTimeFormatter.ISO_DATE_TIME)
            val monthYearKey = dateTime.format(DateTimeFormatter.ofPattern("MMMM (yyyy)", Locale("ru")))
            monthYearKey to route
        }
        .groupBy { it.first }

    GeoTravelTheme {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            IconButton(
                onClick = { navController.navigate("mapFragment") },
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }

            Text(
                text = "История маршрутов",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(16.dp),
                color = MaterialTheme.colorScheme.onBackground
            )

            LazyColumn {
                groupedRoutes.forEach { (monthYear, routes) ->
                    item {
                        Text(
                            text = monthYear.replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
                        )
                    }

                    itemsIndexed(routes) { _, (_, route) ->
                        RouteItem(route = route, onClick = {
                            val routeDetail = routeDetailsProvider(route)
                            onRouteClick(routeDetail)
                        })
                    }
                }
            }
        }
    }
}

@Composable
fun RouteItem(route: Route, onClick: () -> Unit) {
    val dateTime = LocalDateTime.parse(route.createdAt, DateTimeFormatter.ISO_DATE_TIME)
    val formattedDate = dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))

    GeoTravelTheme {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clickable { onClick() },
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = route.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Создано: $formattedDate",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
