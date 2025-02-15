package com.ronik.geotraveljournal.activity.journal

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.ronik.geotraveljournal.navigation.RouteHistoryNav
import com.ronik.geotraveljournal.serializers.Route
import com.ronik.geotraveljournal.serializers.RouteDetail
import com.ronik.geotraveljournal.serializers.RoutePoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class RouteHistoryActivity : AppCompatActivity() {

    private val routes = listOf(
        Route(1, "Клин → Солнечногорск", "2025-01-04T20:25:11.901755Z"),
        Route(2, "Клин → Зеленоград", "2025-01-04T20:40:11.150786Z"),
        Route(3, "Клин → д. Головково", "2025-01-04T20:42:09.585452Z")
    )

    private val routeDetails = mapOf(
        1 to RouteDetail(
            title = "Клин → Солнечногорск",
            description = "Маршрут по трассе M-10 через Солнечногорск",
            distance = 38.5,
            route = listOf(
                RoutePoint(56.3335, 36.7300),
                RoutePoint(56.3123, 36.7102),
                RoutePoint(56.2945, 36.6956),
                RoutePoint(56.2702, 36.6750),
                RoutePoint(56.2451, 36.6553),
                RoutePoint(56.2204, 36.6385),
                RoutePoint(56.1952, 36.6214),
                RoutePoint(56.1736, 36.6102),
                RoutePoint(56.1504, 36.6001),
                RoutePoint(56.1263, 36.5882),
            ),
            createdAt = "2025-01-04T20:25:11.901755Z"
        ),
        2 to RouteDetail(
            title = "Клин → Зеленоград",
            description = "Маршрут по трассе M-10 через Поварово",
            distance = 58.2,
            route = listOf(
                RoutePoint(56.3335, 36.7300),
                RoutePoint(56.3123, 36.7102),
                RoutePoint(56.2945, 36.6956),
                RoutePoint(56.2702, 36.6750),
                RoutePoint(56.2451, 36.6553),
                RoutePoint(56.2204, 36.6385),
                RoutePoint(56.1952, 36.6214),
                RoutePoint(56.1736, 36.6102),
                RoutePoint(56.1504, 36.6001),
                RoutePoint(56.1263, 36.5882),
                RoutePoint(56.1001, 36.5725),
                RoutePoint(56.0753, 36.5561),
                RoutePoint(56.0520, 36.5405),
                RoutePoint(56.0302, 36.5231),
                RoutePoint(56.0103, 36.5085),
                RoutePoint(55.9942, 36.4954),
            ),
            createdAt = "2025-01-04T20:40:11.150786Z"
        ),
        3 to RouteDetail(
            title = "Клин → д. Головково",
            description = "Маршрут по местным дорогам",
            distance = 23.4,
            route = listOf(
                RoutePoint(56.3335, 36.7300),
                RoutePoint(56.3201, 36.7253),
                RoutePoint(56.3054, 36.7152),
                RoutePoint(56.2910, 36.7054),
                RoutePoint(56.2785, 36.6983),
                RoutePoint(56.2658, 36.6885),
                RoutePoint(56.2531, 36.6754),
                RoutePoint(56.2414, 36.6610),
                RoutePoint(56.2303, 36.6452),
                RoutePoint(56.2201, 36.6305),
                RoutePoint(56.2103, 36.6154),
                RoutePoint(56.2004, 36.6001),
            ),
            createdAt = "2025-01-04T20:42:09.585452Z"
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RouteHistoryNav(
                routes = routes,
                routeDetails = routeDetails,
                coroutineScope = CoroutineScope(Dispatchers.Main)
            )
        }
    }
}
