package com.ronik.geotraveljournal.activity.journal

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ronik.geotraveljournal.serializers.RouteDetail
import com.ronik.geotraveljournal.utils.GeoTravelTheme
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteDetailScreen(
    routeDetail: RouteDetail,
    onDismiss: () -> Unit,
    navController: NavController
) {
    val formattedDate = LocalDateTime.parse(routeDetail.createdAt, DateTimeFormatter.ISO_DATE_TIME)
        .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))

    val context = LocalContext.current

    GeoTravelTheme {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight(0.8f)
                    .padding(16.dp)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { /* TODO: Редактировать */ }) {
                        Icon(Icons.Default.Edit, contentDescription = "Редактировать")
                    }
                    IconButton(onClick = { /* TODO: Удалить */ }) {
                        Icon(Icons.Default.Delete, contentDescription = "Удалить")
                    }
                }

                Text(text = routeDetail.title, style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(8.dp))
                Text(text = "Описание: ${routeDetail.description}")
                Spacer(Modifier.height(8.dp))
                Text(text = "Дистанция: ${routeDetail.distance} км")
                Spacer(Modifier.height(8.dp))
                Text(text = "Создано: $formattedDate")

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = {
                        val routePoints = routeDetail.route.joinToString(";") { "${it.lat},${it.lon}" }
                        val encodedRoute = java.net.URLEncoder.encode(routePoints, "UTF-8")
                        Log.d("ROUTE", "route = $encodedRoute")
                        //onDismiss()
                        navController.navigate("mapFragment?routePoints=$encodedRoute")
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Открыть на карте")
                }

                Spacer(Modifier.height(8.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Закрыть")
                }
            }
        }
    }
}
