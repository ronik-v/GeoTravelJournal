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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
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
                Column(
                    modifier = Modifier
                        .fillMaxHeight(0.8f)
                        .padding(16.dp)
                        .background(MaterialTheme.colorScheme.background),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(onClick = {
                            isEditing = !isEditing
                        }) {
                            Icon(Icons.Default.Edit, contentDescription = "Редактировать")
                        }
                        IconButton(onClick = {
                            viewModel.deleteRoute(detail.id)
                            onDismiss()
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Удалить маршрут")
                        }
                    }

                    if (isEditing) {
                        OutlinedTextField(
                            value = editedTitle,
                            onValueChange = { editedTitle = it },
                            label = { Text("Заголовок") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = editedDescription,
                            onValueChange = { editedDescription = it },
                            label = { Text("Описание") },
                            modifier = Modifier.fillMaxWidth()
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
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "Сохранить изменения")
                        }
                    } else {
                        Text(text = detailForUI.title, style = MaterialTheme.typography.titleLarge)
                        Text(text = "Описание: ${detailForUI.description}")
                    }

                    Text(text = "Дистанция: ${detailForUI.distance} км")
                    Text(text = "Создано: $formattedDate")

                    Spacer(modifier = Modifier.padding(8.dp))

                    Button(
                        onClick = {
                            val routePoints = detail.route.joinToString(";") { "${it["lat"]},${it["lon"]}" }
                            val encodedRoute = URLEncoder.encode(routePoints, "UTF-8")
                            navController.navigate("mapFragment?routePoints=$encodedRoute")
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Открыть на карте")
                    }

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
}
