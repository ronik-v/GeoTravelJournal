package com.ronik.geotraveljournal.activity.route_history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ronik.geotraveljournal.serializers.Route

@Composable
fun RouteHistoryScreen(
    routes: List<Route>,
    onRouteClick: (Route) -> Unit
) {
    LazyColumn {
        items(routes) { route ->
            RouteItem(route, onClick = { onRouteClick(route) })
        }
    }
}

@Composable
fun RouteItem(route: Route, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = route.title,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Создано: ${route.createdAt}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
