package com.ronik.geotraveljournal.helpers

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
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
import com.yandex.mapkit.Animation
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.PolylineMapObject
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

class RouteFollower(
    private val context: Context,
    private val mapView: MapView,
    private val routePoints: List<Point>
) {
    private val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    private var locationCallback: LocationCallback? = null
    private var marker = mapView.map.mapObjects.addPlacemark(routePoints.first(), ImageProvider.fromBitmap(getBitmapFromVectorDrawable(context, R.drawable.navigator)))
    private var routeLine: PolylineMapObject? = null
    private val routeGyroscope: RouteGyroscope

    init {
        routeGyroscope = RouteGyroscope(context) { azimuth -> rotateMarker(azimuth) }
        drawRoute()
    }

    private fun getBitmapFromVectorDrawable(context: Context, drawableId: Int): Bitmap {
        val drawable = ContextCompat.getDrawable(context, drawableId) ?: throw IllegalArgumentException("Drawable not found")
        val width = drawable.intrinsicWidth
        val height = drawable.intrinsicHeight
        drawable.setBounds(0, 0, width, height)

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.draw(canvas)

        return bitmap
    }

    private fun drawRoute(traveledPoints: List<Point> = emptyList(), remainingPoints: List<Point> = routePoints) {
        routeLine?.let { mapView.map.mapObjects.remove(it) }

        if (traveledPoints.isNotEmpty()) {
            mapView.map.mapObjects.addPolyline(
                com.yandex.mapkit.geometry.Polyline(traveledPoints)
            ).apply {
                setStrokeColor(0xFF87CEEB.toInt())
            }
        }

        if (remainingPoints.isNotEmpty()) {
            routeLine = mapView.map.mapObjects.addPolyline(
                com.yandex.mapkit.geometry.Polyline(remainingPoints)
            ).apply {
                setStrokeColor(0xFF0000FF.toInt())
            }
        }
    }

    fun startRouteFollowing() {
        if (!checkLocationPermissions()) {
            throw SecurityException("Location permission not granted")
        }

        startGyroscope()

        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000).build()
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let {
                    val userLocation = Point(it.latitude, it.longitude)
                    updateMarkerPosition(userLocation)
                    moveCameraAlongRoute(userLocation)
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

    private fun updateMarkerPosition(location: Point) {
        marker.geometry = location

        val closestIndex = routePoints.indexOfFirst { point ->
            haversineDistance(location, point) < 10.0
        }

        if (closestIndex != -1) {
            val traveledPoints = routePoints.subList(0, closestIndex + 1)
            val remainingPoints = routePoints.subList(closestIndex, routePoints.size)

            drawRoute(traveledPoints, remainingPoints)
        }
    }

    private fun haversineDistance(point1: Point, point2: Point): Double {
        val earthRadius = 6371000.0

        val lat1 = Math.toRadians(point1.latitude)
        val lon1 = Math.toRadians(point1.longitude)
        val lat2 = Math.toRadians(point2.latitude)
        val lon2 = Math.toRadians(point2.longitude)

        val dLat = lat2 - lat1
        val dLon = lon2 - lon1

        val a = sin(dLat / 2).pow(2.0) +
                cos(lat1) * cos(lat2) * sin(dLon / 2).pow(2.0)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return earthRadius * c
    }

    private fun moveCameraAlongRoute(location: Point) {
        val nextPoint = routePoints.find { it != location } ?: return
        val azimuth = calculateAzimuth(location, nextPoint)

        updateCameraView(location, azimuth)
    }

    private fun calculateAzimuth(from: Point, to: Point): Float {
        val deltaY = to.latitude - from.latitude
        val deltaX = to.longitude - from.longitude
        return Math.toDegrees(atan2(deltaY, deltaX)).toFloat()
    }

    private fun updateCameraView(location: Point, azimuth: Float) {
        mapView.map.move(
            CameraPosition(location, 14f, azimuth, 0f),
            Animation(Animation.Type.SMOOTH, 0.5f),
            null
        )
    }

    private fun getRotatedBitmap(source: Bitmap, angle: Float): Bitmap {
        val matrix = android.graphics.Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }

    private fun rotateMarker(azimuth: Float) {
        val rotatedBitmap = getRotatedBitmap(getBitmapFromVectorDrawable(context, R.drawable.navigator), azimuth)
        marker.setIcon(ImageProvider.fromBitmap(rotatedBitmap))

        updateCameraView(marker.geometry, azimuth)
    }

    private fun startGyroscope() = routeGyroscope.startListening()

    private fun stopGyroscope() = routeGyroscope.stopListening()

    private fun checkLocationPermissions(): Boolean {
        val fineLocationGranted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseLocationGranted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return fineLocationGranted && coarseLocationGranted
    }

    fun clearData() {
        marker?.let {
            if (it.isValid) {
                try {
                    mapView.map.mapObjects.remove(it)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        routeLine?.let {
            if (it.isValid) {
                try {
                    mapView.map.mapObjects.remove(it)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        routeLine = null
    }

}
