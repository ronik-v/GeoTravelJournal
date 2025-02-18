package com.ronik.geotraveljournal.utils

import com.ronik.geotraveljournal.network.JournalDetailEntry
import com.ronik.geotraveljournal.network.JournalPreviewEntry
import com.ronik.geotraveljournal.serializers.Route
import com.ronik.geotraveljournal.serializers.RouteDetail
import com.ronik.geotraveljournal.serializers.RoutePoint

fun JournalPreviewEntry.toRoute(): Route {
    return Route(
        id = this.id.toInt(),
        title = this.title,
        createdAt = this.createdAt
    )
}

fun JournalDetailEntry.toRouteDetail(): RouteDetail {
    return RouteDetail(
        title = this.title,
        description = this.description,
        distance = this.distance,
        route = this.route.map { pointMap ->
            val lat = pointMap["lat"]?.toDoubleOrNull() ?: 0.0
            val lon = pointMap["lon"]?.toDoubleOrNull() ?: 0.0
            RoutePoint(lat, lon)
        },
        createdAt = this.createdAt
    )
}
