package com.ronik.geotraveljournal

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.RequestPoint
import com.yandex.mapkit.RequestPointType
import com.yandex.mapkit.directions.DirectionsFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.search.SearchFactory
import com.yandex.mapkit.search.SearchManager
import com.yandex.mapkit.search.Session
import com.yandex.mapkit.search.SearchOptions
import com.yandex.mapkit.directions.driving.DrivingOptions
import com.yandex.mapkit.directions.driving.DrivingRouter
import com.yandex.mapkit.directions.driving.DrivingRoute
import com.yandex.mapkit.directions.driving.DrivingRouterType
import com.yandex.mapkit.directions.driving.DrivingSession
import com.yandex.mapkit.directions.driving.VehicleOptions
import com.yandex.mapkit.map.VisibleRegionUtils
import com.yandex.mapkit.search.Response
import com.yandex.mapkit.search.SearchManagerType
import com.yandex.runtime.image.ImageProvider

class MainActivity : AppCompatActivity() {
    private lateinit var mapView: MapView
    private lateinit var searchManager: SearchManager
    private lateinit var drivingRouter: DrivingRouter
    private lateinit var searchEditText: EditText
    private lateinit var searchButton: Button
    private lateinit var routeButton: Button

    private val placemarkTapListener = MapObjectTapListener { _, point ->
        Toast.makeText(this, "Точка на карте (${point.longitude}, ${point.latitude})", Toast.LENGTH_SHORT).show()
        true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.setApiKey(BuildAppConfig.mapApiKey)
        MapKitFactory.initialize(this)
        setContentView(R.layout.activity_main)

        mapView = findViewById(R.id.mapview)
        searchEditText = findViewById(R.id.searchEditText)
        searchButton = findViewById(R.id.searchButton)
        routeButton = findViewById(R.id.routeButton)

        searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED)
        drivingRouter = DirectionsFactory.getInstance().createDrivingRouter(DrivingRouterType.COMBINED)

        searchButton.setOnClickListener {
            val query = searchEditText.text.toString()
            searchLocation(query)
        }

        routeButton.setOnClickListener {
            val destination = Point(56.331590, 36.728727)
            buildRouteTo(destination)
        }

        val imageProvider = ImageProvider.fromResource(this, R.drawable.ic_pin)
        val geoPointer = mapView.map.mapObjects.addPlacemark().apply {
            geometry = Point(56.331590, 36.728727)
            setIcon(imageProvider)
        }
        geoPointer.addTapListener(placemarkTapListener)

        mapView.map.move(CameraPosition(Point(56.331590, 36.728727), 17.0f, 150.0f, 30.0f))
    }

    private fun searchLocation(query: String) {
        val searchOptions = SearchOptions()
        searchManager.submit(
            query,
            VisibleRegionUtils.toPolygon(mapView.map.visibleRegion),
            searchOptions,
            object : Session.SearchListener {
                override fun onSearchResponse(response: Response) {
                    val point = response.collection.children.firstOrNull()?.obj?.geometry?.get(0)?.point
                    point?.let { buildRouteTo(it) }
                }

                override fun onSearchError(error: com.yandex.runtime.Error) {
                    Toast.makeText(this@MainActivity, "Ошибка поиска", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    private fun buildRouteTo(destination: Point) {
        val userLocation = getCurrentLocation()
        val requestPoints = listOf(
            RequestPoint(userLocation, RequestPointType.WAYPOINT, null, null),
            RequestPoint(destination, RequestPointType.WAYPOINT, null, null)
        )

        val vehicleOptions = VehicleOptions()
        drivingRouter.requestRoutes(
            requestPoints, DrivingOptions(), vehicleOptions,
            object : DrivingSession.DrivingRouteListener {
                override fun onDrivingRoutes(routes: List<DrivingRoute>) {
                    if (routes.isNotEmpty()) {
                        mapView.map.mapObjects.addPolyline(routes[0].geometry)
                    }
                }

                override fun onDrivingRoutesError(error: com.yandex.runtime.Error) {
                    Toast.makeText(this@MainActivity, "Ошибка маршрута", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    private fun getCurrentLocation(): Point {
        return Point(56.331590, 36.728727)
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        mapView.onStart()
    }

    override fun onStop() {
        mapView.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }
}
