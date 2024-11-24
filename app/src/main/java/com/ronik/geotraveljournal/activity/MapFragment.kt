package com.ronik.geotraveljournal.activity

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.ronik.geotraveljournal.helpers.Location
import com.ronik.geotraveljournal.R
import com.ronik.geotraveljournal.adapter.SearchAddressFilterAdapter
import com.yandex.mapkit.Animation
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
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.map.PolylineMapObject
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.search.Response
import com.yandex.mapkit.search.SearchFactory
import com.yandex.mapkit.search.SearchManager
import com.yandex.mapkit.search.SearchManagerType
import com.yandex.mapkit.search.SearchOptions
import com.yandex.mapkit.search.Session
import com.yandex.mapkit.map.VisibleRegionUtils
import com.yandex.runtime.image.ImageProvider


class MapFragment : Fragment() {
    private lateinit var mapView: MapView
    private lateinit var searchManager: SearchManager
    private lateinit var drivingRouter: DrivingRouter
    private lateinit var searchContainer: LinearLayout
    private lateinit var searchAutoComplete: AutoCompleteTextView
    private lateinit var searchButton: Button
    private lateinit var routeButton: Button
    private lateinit var resetRouteButton: ImageButton
    private lateinit var searchIcon: ImageButton
    private lateinit var location: Location
    private var currentRoute: DrivingSession.DrivingRouteListener? = null
    private var userPlacemark: PlacemarkMapObject? = null
    private val LOCATION_PERMISSION_REQUEST_CODE = 1
    private val placemarkTapListener = MapObjectTapListener { _, point ->
        Toast.makeText(this.context, "Точка на карте (${point.longitude}, ${point.latitude})", Toast.LENGTH_SHORT).show()
        true
    }
    private val routeObjects = mutableListOf<PolylineMapObject>()
    private val suggestionList = mutableListOf<String>()
    private var suggestionResults = mutableListOf<Point>()
    private lateinit var suggestionsAdapter: ArrayAdapter<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val rootView = inflater.inflate(R.layout.fragment_map, container, false)

        mapView = rootView.findViewById(R.id.mapview)
        searchAutoComplete = rootView.findViewById(R.id.searchEditText)
        routeButton = rootView.findViewById(R.id.routeButton)
        resetRouteButton = rootView.findViewById(R.id.resetRouteButton)
        searchContainer = rootView.findViewById(R.id.searchContainer)
        searchIcon = rootView.findViewById(R.id.searchIcon)
        searchButton = rootView.findViewById(R.id.searchButton)
        suggestionsAdapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_dropdown_item_1line, suggestionList
        )
        searchAutoComplete.setAdapter(suggestionsAdapter)

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        MapKitFactory.initialize(requireContext())
        location = Location(requireContext())

        searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED)
        drivingRouter = DirectionsFactory.getInstance().createDrivingRouter(DrivingRouterType.COMBINED)

        if (ContextCompat.checkSelfPermission(mapView.context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(mapView.context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mapView.context as Activity, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        }

        suggestionsAdapter = SearchAddressFilterAdapter(
            mapView.context, android.R.layout.simple_dropdown_item_1line, suggestionList
        )
        searchAutoComplete.setAdapter(suggestionsAdapter)
        searchAutoComplete.threshold = 1


        searchContainer.visibility = View.GONE

        searchIcon.setOnClickListener {
            val isVisible = searchContainer.visibility == View.VISIBLE
            searchContainer.visibility = if (isVisible) View.GONE else View.VISIBLE
        }

        searchAutoComplete.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString()
                if (query.isNotEmpty()) {
                    fetchSuggestions(query)
                }
            }

            override fun afterTextChanged(s: android.text.Editable?) {}
        })

        searchAutoComplete.setOnItemClickListener { _, _, position, _ ->
            val selectedPoint = suggestionResults[position]
            val selectedName = suggestionList[position]
            Toast.makeText(mapView.context, "Выбрана точка: $selectedName (${selectedPoint.latitude}, ${selectedPoint.longitude})", Toast.LENGTH_SHORT).show()
            buildRouteTo(selectedPoint)
        }

        searchButton.setOnClickListener {
            val query = searchAutoComplete.text.toString()
            if (query.isNotEmpty()) {
                searchLocation(query)
            } else {
                Toast.makeText(mapView.context, "Введите адрес для поиска", Toast.LENGTH_SHORT).show()
            }
        }

        routeButton.setOnClickListener {
            location.current { coordinates ->
                buildRouteTo(coordinates)
            }
        }

        resetRouteButton.setOnClickListener {
            resetRoute()
        }

        val imageProvider = ImageProvider.fromResource(mapView.context, R.drawable.ic_pin)
        val geoPointer = mapView.map.mapObjects.addPlacemark().apply {
            location.current { coordinates -> geometry = coordinates }
            setIcon(imageProvider)
        }
        geoPointer.addTapListener(placemarkTapListener)

        location.current { coordinates ->
            mapView.map.move(CameraPosition(coordinates, 17.0f, 150.0f, 30.0f))
        }
    }

    private fun fetchSuggestions(query: String) {
        if (query.isNotEmpty()) {
            val searchOptions = SearchOptions()
            searchManager.submit(query, VisibleRegionUtils.toPolygon(mapView.map.visibleRegion), searchOptions, object : Session.SearchListener {
                override fun onSearchResponse(response: Response) {
                    suggestionList.clear()
                    suggestionResults.clear()

                    response.collection.children.forEach { item ->
                        val name = item.obj?.name
                        val point = item.obj?.geometry?.get(0)?.point
                        if (name != null && point != null) {
                            suggestionList.add(name)
                            suggestionResults.add(point)
                        }
                    }

                    Log.d("SearchResponse", "Suggestions: $suggestionList")
                    suggestionsAdapter.notifyDataSetChanged()
                }

                override fun onSearchError(error: com.yandex.runtime.Error) {
                    Log.e("SearchError", "Search error: ${error}")
                }
            })
        }
    }

    private fun searchLocation(query: String) {
        val searchOptions = SearchOptions()
        searchManager.submit(query, VisibleRegionUtils.toPolygon(mapView.map.visibleRegion), searchOptions, object : Session.SearchListener {
            override fun onSearchResponse(response: Response) {
                val point = response.collection.children.firstOrNull()?.obj?.geometry?.get(0)?.point
                point?.let { buildRouteTo(it) }
            }

            override fun onSearchError(error: com.yandex.runtime.Error) {
                Toast.makeText(mapView.context, "Ошибка поиска", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun moveToUserLocation(userLocation: Point) {
        if (userPlacemark == null) {
            userPlacemark = mapView.map.mapObjects.addPlacemark(userLocation).apply {
                setIcon(ImageProvider.fromResource(mapView.context, R.drawable.ic_pin))
            }
        } else {
            userPlacemark?.geometry = userLocation
        }

        mapView.map.move(
            CameraPosition(userLocation, 17.0f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 1.5f)
        ) { Log.d("CameraMove", "Camera finished moving to user location") }
    }

    private fun buildRouteTo(destination: Point) {
        resetRoute()

        location.current { userLocation ->
            moveToUserLocation(userLocation)

            val requestPoints = listOf(
                RequestPoint(userLocation, RequestPointType.WAYPOINT, null, null),
                RequestPoint(destination, RequestPointType.WAYPOINT, null, null)
            )

            val vehicleOptions = VehicleOptions()

            val drivingRouteListener = object : DrivingSession.DrivingRouteListener {
                override fun onDrivingRoutes(routes: List<DrivingRoute>) {
                    if (routes.isNotEmpty()) {
                        val routePolyline = mapView.map.mapObjects.addPolyline(routes[0].geometry)
                        routeObjects.add(routePolyline)
                    } else {
                        Toast.makeText(mapView.context, "Маршруты не найдены", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onDrivingRoutesError(error: com.yandex.runtime.Error) {
                    Toast.makeText(mapView.context, "Ошибка построения маршрута: $error", Toast.LENGTH_SHORT).show()
                }
            }

            drivingRouter.requestRoutes(requestPoints, DrivingOptions(), vehicleOptions, drivingRouteListener)
        }
    }

    private fun resetRoute() {
        routeObjects.forEach { route ->
            if (route.isValid) {
                mapView.map.mapObjects.remove(route)
            }
        }
        routeObjects.clear()
        currentRoute = null
        userPlacemark?.let {
            if (it.isValid) {
                mapView.map.mapObjects.remove(it)
                userPlacemark = null
            }
        }
    }

    @Deprecated("")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                location.current { coordinates ->
                    mapView.map.move(CameraPosition(coordinates, 17.0f, 0.0f, 0.0f))
                }
            } else {
                Toast.makeText(context, "Разрешение на использование геолокации отклонено", Toast.LENGTH_SHORT).show()
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

    override fun onResume() {
        super.onResume()
        MapKitFactory.getInstance().onStart()
        mapView.onStart()
    }
}
