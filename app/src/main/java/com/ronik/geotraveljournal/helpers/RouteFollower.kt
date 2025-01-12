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
import com.yandex.mapkit.geometry.Polyline
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class RouteFollower(
    private val context: Context,
    private val mapView: MapView,
    private val routePoints: List<Point>
) {
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)
    private var locationCallback: LocationCallback? = null
    private var marker: PlacemarkMapObject? = null
    private var routeLine: MapObjectCollection = mapView.map.mapObjects.addCollection()
    private var lastUserLocation: Point? = null

    init {
        marker = mapView.map.mapObjects.addPlacemark(
            routePoints.first(),
            ImageProvider.fromResource(context, R.drawable.navigator)
        )
    }

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

        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000).build()
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let {
                    val userLocation = Point(it.latitude, it.longitude)
                    updateMarkerPosition(userLocation)
                    moveCameraToLocation(userLocation)

                    if (!isUserOnRoute(userLocation)) {
                        notifyUserOffRoute()
                    }
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
    }

    private fun drawRoute() {
        val polyline = Polyline(routePoints)
        val polylineMapObject = routeLine.addPolyline(polyline)
        polylineMapObject.setStrokeColor(0xFF0000FF.toInt())
    }

    private fun updateMarkerPosition(location: Point) {
        marker?.geometry = location

        lastUserLocation?.let { previousLocation ->
            val bearing = calculateBearing(previousLocation, location)
            marker?.direction = bearing
        }

        lastUserLocation = location
    }

    private fun moveCameraToLocation(location: Point) {
        mapView.map.move(
            CameraPosition(location, 14f, 0f, 0f)
        )
    }

    private fun calculateBearing(from: Point, to: Point): Float {
        val lat1 = Math.toRadians(from.latitude)
        val lon1 = Math.toRadians(from.longitude)
        val lat2 = Math.toRadians(to.latitude)
        val lon2 = Math.toRadians(to.longitude)

        val deltaLon = lon2 - lon1
        val y = sin(deltaLon) * cos(lat2)
        val x = cos(lat1) * sin(lat2) - sin(lat1) * cos(lat2) * cos(deltaLon)

        return Math.toDegrees(atan2(y, x)).toFloat()
    }

    private fun isUserOnRoute(location: Point, tolerance: Double = 20.0): Boolean {
        return routePoints.any { haversineDistance(location, it) <= tolerance }
    }

    private fun haversineDistance(point1: Point, point2: Point): Double {
        val R = 6371000.0
        val dLat = Math.toRadians(point2.latitude - point1.latitude)
        val dLon = Math.toRadians(point2.longitude - point1.longitude)
        val lat1 = Math.toRadians(point1.latitude)
        val lat2 = Math.toRadians(point2.latitude)

        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(lat1) * cos(lat2) * sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return R * c
    }

    private fun notifyUserOffRoute() { /* TODO */}

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
