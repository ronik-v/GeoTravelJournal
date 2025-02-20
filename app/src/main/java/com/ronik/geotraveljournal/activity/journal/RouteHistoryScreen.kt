package com.ronik.geotraveljournal.activity.journal

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ronik.geotraveljournal.serializers.Route
import com.ronik.geotraveljournal.utils.GeoTravelTheme
import com.ronik.geotraveljournal.utils.toRoute
import com.ronik.geotraveljournal.viewmodel.JournalViewModel
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun RouteHistoryScreen(
    viewModel: JournalViewModel,
    onRouteClick: (Long) -> Unit,
    navController: NavController
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val routes by viewModel.routes.collectAsState()
    var nextPage by remember { mutableIntStateOf(0) }
    var isLoading by remember { mutableStateOf(false) }

    val groupedRoutes = routes
        .map { route ->
            val dateTime = LocalDateTime.parse(
                route.createdAt,
                DateTimeFormatter.ISO_DATE_TIME
            )
            val monthYearKey = dateTime.format(DateTimeFormatter.ofPattern("MMMM (yyyy)", Locale("ru")))
            monthYearKey to route
        }
        .groupBy { it.first }

    LaunchedEffect(Unit) {
        viewModel.fetchRoutes(nextPage)
        nextPage++
    }

    val totalLazyItemsCount = routes.size + groupedRoutes.keys.size

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastIndex ->
                if (lastIndex != null &&
                    !isLoading &&
                    routes.size >= 10 &&
                    lastIndex >= totalLazyItemsCount - 1
                ) {
                    isLoading = true
                    coroutineScope.launch {
                        viewModel.fetchRoutes(nextPage)
                        nextPage++
                        isLoading = false
                    }
                }
            }
    }

    GeoTravelTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { navController.navigate("mapFragment") }
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Назад",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
                IconButton(
                    onClick = { viewModel.clearHistory() }
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Удалить всю историю",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(modifier = Modifier.padding(8.dp))

            Text(
                text = "История маршрутов",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(8.dp),
                color = MaterialTheme.colorScheme.onBackground
            )

            if (routes.isEmpty()) {
                EmptyRoutesPlaceholder(modifier = Modifier.fillMaxSize())
            } else {
                LazyColumn(state = listState) {
                    groupedRoutes.forEach { (monthYear, routePairs) ->
                        item {
                            Text(
                                text = monthYear.replaceFirstChar { it.uppercase() },
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
                            )
                        }
                        itemsIndexed(routePairs) { _, (_, route) ->
                            RouteItem(route = route.toRoute(), onClick = {
                                onRouteClick(route.id)
                            })
                        }
                    }
                    if (isLoading) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RouteItem(route: Route, onClick: () -> Unit) {
    val dateTime = LocalDateTime.parse(
        route.createdAt,
        DateTimeFormatter.ISO_DATE_TIME
    )
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
                Spacer(modifier = Modifier.padding(vertical = 4.dp))
                Text(
                    text = "Создано: $formattedDate",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
