package com.ronik.geotraveljournal.activity.route_history

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ronik.geotraveljournal.serializers.RouteDetail

@Composable
fun RouteDetailScreen(
    routeDetail: RouteDetail,
    onBack: () -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = routeDetail.title,
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Описание: ${routeDetail.description}",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Дистанция: ${routeDetail.distance} км",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Создано: ${routeDetail.createdAt}",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Маршрут:",
            style = MaterialTheme.typography.titleMedium
        )
        routeDetail.route.forEach { point ->
            Text(
                text = "(${point.lat}, ${point.lon})",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onBack) {
            Text(text = "Назад")
        }
    }
}
