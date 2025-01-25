package com.ronik.geotraveljournal.helpers

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.ronik.geotraveljournal.R
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider

class RouteFollower(
    private val context: Context,
    private val mapView: MapView,
    private val routePoints: List<Point>
) {
    private val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    private var locationCallback: LocationCallback? = null
    private var marker: PlacemarkMapObject? = null
    private var routeLine: MapObjectCollection = mapView.map.mapObjects.addCollection()
    private val routeGyroscope: RouteGyroscope

    init {
        marker = mapView.map.mapObjects.addPlacemark(
            routePoints.first(),
            ImageProvider.fromResource(context, R.drawable.navigator)
        )
        routeGyroscope = RouteGyroscope(context) { azimuth -> rotateMarker(azimuth) }
    }

    private fun startGyroscope() = routeGyroscope.startListening()

    private fun stopGyroscope() = routeGyroscope.stopListening()

    fun clearData() {
        marker?.let { mapView.map.mapObjects.remove(it) }
        routeLine.clear()
        marker = null
        routeLine = mapView.map.mapObjects.addCollection()
    }

    fun startRouteFollowing() {
        if (!checkLocationPermissions()) {
            throw SecurityException("Location permission not granted")
        }

        drawRoute()
        startGyroscope()

        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000).build()
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let {
                    val userLocation = Point(it.latitude, it.longitude)
                    updateMarkerPosition(userLocation)
                    moveCameraToLocation(userLocation)
                }
            }
        }

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback as LocationCallback,
            Looper.getMainLooper()
        )
    }

    fun stopRouteFollowing() {
        locationCallback?.let { fusedLocationClient.removeLocationUpdates(it) }
        stopGyroscope()
    }

    private fun drawRoute() {
        val polyline = routeLine.addPolyline(com.yandex.mapkit.geometry.Polyline(routePoints))
        polyline.setStrokeColor(0xFF0000FF.toInt())
    }

    private fun updateMarkerPosition(location: Point) {
        marker?.geometry = location
    }

    private fun moveCameraToLocation(location: Point) {
        mapView.map.move(CameraPosition(location, 14f, 0f, 0f))
    }

    private fun rotateMarker(azimuth: Float) = marker?.apply { direction = azimuth }

    private fun checkLocationPermissions(): Boolean {
        val fineLocationGranted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseLocationGranted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return fineLocationGranted && coarseLocationGranted
    }
}
