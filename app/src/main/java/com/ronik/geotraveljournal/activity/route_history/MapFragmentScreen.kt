package com.ronik.geotraveljournal.activity.route_history

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.ronik.geotraveljournal.R
import com.ronik.geotraveljournal.activity.MapFragment
import com.ronik.geotraveljournal.serializers.RoutePoint

@Composable
fun MapFragmentScreen(routePoints: List<RoutePoint>? = null) {
    val context = LocalContext.current
    val fragmentManager = (context as? AppCompatActivity)?.supportFragmentManager

    LaunchedEffect(fragmentManager) {
        val existingFragment = fragmentManager?.findFragmentByTag(MapFragment::class.java.simpleName)
        val fragment = existingFragment ?: MapFragment()

        if (existingFragment == null) {
            fragmentManager?.beginTransaction()?.apply {
                replace(R.id.mapview, fragment, MapFragment::class.java.simpleName)
                commitNowAllowingStateLoss()
            }
        }
        routePoints?.let {
            val bundle = Bundle()
            bundle.putParcelableArrayList("routePoints", ArrayList(it))
            fragment.arguments = bundle
        }
    }

    AndroidView(factory = { ctx ->
        val fragment = fragmentManager?.findFragmentByTag(MapFragment::class.java.simpleName)
        fragment?.view ?: throw IllegalStateException("Fragment view is not created yet")
    })
}
