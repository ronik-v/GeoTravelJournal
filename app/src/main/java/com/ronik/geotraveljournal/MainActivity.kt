package com.ronik.geotraveljournal

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.RequestPoint
import com.yandex.mapkit.RequestPointType
import com.yandex.mapkit.directions.DirectionsFactory
import com.yandex.mapkit.directions.driving.DrivingOptions
import com.yandex.mapkit.directions.driving.DrivingRoute
import com.yandex.mapkit.directions.driving.DrivingRouter
import com.yandex.mapkit.directions.driving.DrivingRouterType
import com.yandex.mapkit.directions.driving.DrivingSession
import com.yandex.mapkit.directions.driving.VehicleOptions
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.search.Response
import com.yandex.mapkit.search.SearchFactory
import com.yandex.mapkit.search.SearchManager
import com.yandex.mapkit.search.SearchManagerType
import com.yandex.mapkit.search.SearchOptions
import com.yandex.mapkit.search.Session
import com.yandex.mapkit.map.VisibleRegionUtils
import com.yandex.runtime.image.ImageProvider

class MainActivity : AppCompatActivity() {
    private lateinit var mapView: MapView
    private lateinit var searchManager: SearchManager
    private lateinit var drivingRouter: DrivingRouter
    private lateinit var searchContainer: LinearLayout
    private lateinit var searchEditText: EditText
    private lateinit var searchButton: Button
    private lateinit var routeButton: Button
    private lateinit var searchIcon: ImageButton
    private lateinit var location: Location
    private var currentRoute: DrivingSession.DrivingRouteListener? = null
    private val LOCATION_PERMISSION_REQUEST_CODE = 1
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
        searchContainer = findViewById(R.id.searchContainer)
        searchEditText = findViewById(R.id.searchEditText)
        searchButton = findViewById(R.id.searchButton)
        routeButton = findViewById(R.id.routeButton)
        searchIcon = findViewById(R.id.searchIcon)

        searchContainer.visibility = View.GONE

        searchIcon.setOnClickListener {
            val isVisible = searchContainer.visibility == View.VISIBLE
            searchContainer.visibility = if (isVisible) View.GONE else View.VISIBLE
            Log.d("VisibilityCheck", if (isVisible) "Hiding search container" else "Showing search container")
        }

        location = Location(this)
        searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED)
        drivingRouter = DirectionsFactory.getInstance().createDrivingRouter(DrivingRouterType.COMBINED)

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        }

        searchButton.setOnClickListener {
            val query = searchEditText.text.toString()
            if (query.isNotEmpty()) {
                searchLocation(query)
            } else {
                Toast.makeText(this, "Введите адрес для поиска", Toast.LENGTH_SHORT).show()
            }
        }

        routeButton.setOnClickListener {
            location.current { coordinates ->
                buildRouteTo(coordinates)
            }
        }

        val imageProvider = ImageProvider.fromResource(this, R.drawable.ic_pin)
        val geoPointer = mapView.map.mapObjects.addPlacemark().apply {
            location.current { coordinates ->
                geometry = coordinates
            }
            setIcon(imageProvider)
        }
        geoPointer.addTapListener(placemarkTapListener)

        location.current { coordinates ->
            mapView.map.move(CameraPosition(coordinates, 17.0f, 150.0f, 30.0f))
        }
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
        mapView.map.mapObjects.clear()

        location.current { userLocation ->
            val requestPoints = listOf(
                RequestPoint(userLocation, RequestPointType.WAYPOINT, null, null),
                RequestPoint(destination, RequestPointType.WAYPOINT, null, null)
            )

            val vehicleOptions = VehicleOptions()
            currentRoute = object : DrivingSession.DrivingRouteListener {
                override fun onDrivingRoutes(routes: List<DrivingRoute>) {
                    if (routes.isNotEmpty()) {
                        mapView.map.mapObjects.addPolyline(routes[0].geometry)
                    }

                    mapView.map.move(CameraPosition(userLocation, 17.0f, 150.0f, 30.0f))
                }

                override fun onDrivingRoutesError(error: com.yandex.runtime.Error) {
                    Toast.makeText(this@MainActivity, "Ошибка маршрута", Toast.LENGTH_SHORT).show()
                }
            }
            drivingRouter.requestRoutes(requestPoints, DrivingOptions(), vehicleOptions, currentRoute!!)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Разрешение на доступ к местоположению предоставлено", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Необходимо предоставить разрешение на доступ к местоположению", Toast.LENGTH_SHORT).show()
            }
        }
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
