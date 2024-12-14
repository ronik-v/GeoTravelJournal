package com.ronik.geotraveljournal.helpers

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.Polyline
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.mapview.MapView

class RouteFollower(
    private val context: Context,
    private val mapView: MapView,
    private val routePoints: List<Point>
) {
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)
    private var locationCallback: LocationCallback? = null
    private var marker: PlacemarkMapObject? = mapView.map.mapObjects.addPlacemark(routePoints.first())
    private var routeLine: MapObjectCollection = mapView.map.mapObjects.addCollection()

    fun clearData() {
        marker.let {
            if (it != null) {
                mapView.map.mapObjects.remove(it)
            }
        }

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
                }
            }
        }
        if (ActivityCompat.checkSelfPermission(
                mapView.context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                mapView.context,
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
        locationCallback?.let {
            fusedLocationClient.removeLocationUpdates(it)
        }
    }

    private fun drawRoute() {
        val polyline = Polyline(routePoints)
        val polylineMapObject = routeLine.addPolyline(polyline)
        polylineMapObject.setStrokeColor(0xFF0000FF.toInt())
    }

    private fun updateMarkerPosition(location: Point) {
        marker?.geometry = location
    }

    private fun moveCameraToLocation(location: Point) {
        mapView.map.move(
            CameraPosition(location, 14f, 0f, 0f)
        )
    }

    private fun checkLocationPermissions(): Boolean {
        val fineLocationGranted = ContextCompat.checkSelfPermission(
            context, android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseLocationGranted = ContextCompat.checkSelfPermission(
            context, android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return fineLocationGranted && coarseLocationGranted
    }
}
