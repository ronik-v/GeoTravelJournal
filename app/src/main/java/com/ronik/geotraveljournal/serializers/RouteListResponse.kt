package com.ronik.geotraveljournal.serializers

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class RouteListResponse(
    val status: Boolean,
    val result: List<Route>
) : Parcelable

@Parcelize
@Serializable
data class Route(
    val id: Int,
    val title: String,
    val createdAt: String
) : Parcelable

@Parcelize
@Serializable
data class RouteDetailResponse(
    val status: Boolean,
    val result: RouteDetail
) : Parcelable

@Parcelize
@Serializable
data class RouteDetail(
    val title: String,
    val description: String,
    val distance: Double,
    val route: List<RoutePoint>,
    val createdAt: String
) : Parcelable

@Parcelize
@Serializable
data class RoutePoint(
    val lat: Double,
    val lon: Double
) : Parcelable