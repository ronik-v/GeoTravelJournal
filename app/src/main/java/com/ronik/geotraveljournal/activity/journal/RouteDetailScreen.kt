package com.ronik.geotraveljournal.activity.journal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ronik.geotraveljournal.network.JournalUpdateEntry
import com.ronik.geotraveljournal.utils.GeoTravelTheme
import com.ronik.geotraveljournal.utils.toRouteDetail
import com.ronik.geotraveljournal.viewmodel.JournalViewModel
import java.net.URLEncoder
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteDetailScreen(
    routeId: Long,
    viewModel: JournalViewModel,
    onDismiss: () -> Unit,
    navController: NavController
) {
    val routeDetailEntry by viewModel.routeDetail.collectAsState()

    LaunchedEffect(routeId) {
        viewModel.fetchRouteDetail(routeId)
    }
    var isEditing by remember { mutableStateOf(false) }
    var editedTitle by remember { mutableStateOf("") }
    var editedDescription by remember { mutableStateOf("") }

    if (routeDetailEntry == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(modifier = Modifier.size(48.dp))
        }
    } else {
        val detail = routeDetailEntry!!
        val detailForUI = detail.toRouteDetail()
        LaunchedEffect(key1 = detail) {
            editedTitle = detail.title
            editedDescription = detail.description
        }
        val formattedDate = LocalDateTime.parse(
            detailForUI.createdAt,
            DateTimeFormatter.ISO_DATE_TIME
        ).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))

        GeoTravelTheme {
            ModalBottomSheet(
                onDismissRequest = onDismiss,
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFFE6F5E5),
                                    Color(0xFFF0FAF0)
                                )
                            )
                        )
                        .padding(8.dp)
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.large,
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White.copy(alpha = 0.9f)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxHeight(0.8f)
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = { isEditing = !isEditing }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Редактировать",
                                        tint = Color.Black
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        viewModel.deleteRoute(detail.id)
                                        onDismiss()
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Удалить маршрут",
                                        tint = Color.Black
                                    )
                                }
                            }

                            if (isEditing) {
                                OutlinedTextField(
                                    value = editedTitle,
                                    onValueChange = { editedTitle = it },
                                    label = { Text("Заголовок", color = Color.Black) },
                                    modifier = Modifier.fillMaxWidth(),
                                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                                        color = Color.Black
                                    )
                                )
                                OutlinedTextField(
                                    value = editedDescription,
                                    onValueChange = { editedDescription = it },
                                    label = { Text("Описание", color = Color.Black) },
                                    modifier = Modifier.fillMaxWidth(),
                                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                                        color = Color.Black
                                    )
                                )
                                Button(
                                    onClick = {
                                        viewModel.updateRoute(
                                            detail.id,
                                            JournalUpdateEntry(
                                                title = editedTitle,
                                                description = editedDescription,
                                                distance = detail.distance,
                                                route = detail.route
                                            )
                                        )
                                        isEditing = false
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = MaterialTheme.shapes.medium
                                ) {
                                    Text(text = "Сохранить изменения", color = Color.Black)
                                }
                            } else {
                                Text(
                                    text = detailForUI.title,
                                    style = MaterialTheme.typography.titleLarge,
                                    color = Color.Black
                                )
                                Text(
                                    text = "Описание: ${detailForUI.description}",
                                    color = Color.Black
                                )
                            }

                            Text(
                                text = "Дистанция: ${detailForUI.distance} км",
                                color = Color.Black
                            )
                            Text(
                                text = "Создано: $formattedDate",
                                color = Color.Black
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                onClick = {
                                    val routePoints = detail.route.joinToString(";") { "${it["lat"]},${it["lon"]}" }
                                    val encodedRoute = URLEncoder.encode(routePoints, "UTF-8")
                                    navController.navigate("mapFragment?routePoints=$encodedRoute")
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = MaterialTheme.shapes.medium
                            ) {
                                Text(text = "Открыть на карте", color = Color.Black)
                            }

                            Button(
                                onClick = onDismiss,
                                modifier = Modifier.fillMaxWidth(),
                                shape = MaterialTheme.shapes.medium
                            ) {
                                Text(text = "Закрыть", color = Color.Black)
                            }
                        }
                    }
                }
            }
        }
    }
}