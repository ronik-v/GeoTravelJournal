package com.ronik.geotraveljournal.serializers

import kotlinx.serialization.Serializable

@Serializable
data class RouteListResponse(
    val status: Boolean,
    val result: List<Route>
)

@Serializable
data class Route(
    val id: Int,
    val title: String,
    val createdAt: String
)

@Serializable
data class RouteDetailResponse(
    val status: Boolean,
    val result: RouteDetail
)

@Serializable
data class RouteDetail(
    val title: String,
    val description: String,
    val distance: Double,
    val route: List<RoutePoint>,
    val createdAt: String
)

@Serializable
data class RoutePoint(
    val lat: Double,
    val lon: Double
)