package com.ronik.geotraveljournal

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.yandex.mapkit.geometry.Point
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices


class Location(private val context: Context) {
    private val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    fun current(onLocationReceived: (Point) -> Unit) {
        getCoordinates { location ->
            val coordinates = location?.let {
                Point(it.latitude, it.longitude)
            } ?: this.base()

            onLocationReceived(coordinates)
        }
    }

    private fun base(): Point = Point(56.331590, 36.728727)

    // Get current user location
    private fun getCoordinates(onLocationReceived: (Location?) -> Unit) {
        // Check if location permission is granted
        if (ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            this.fusedLocationClient.lastLocation
                .addOnSuccessListener {
                    location: Location? -> onLocationReceived(location)
                }
                .addOnFailureListener {
                    onLocationReceived(null) // null if error
                }

        } else {
            Toast.makeText(context, "Необходимо предоставить разрешение на доступ к местоположению", Toast.LENGTH_SHORT).show()
            onLocationReceived(null)
        }
    }
}