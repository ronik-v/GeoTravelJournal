package com.ronik.geotraveljournal.activity.route_history

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.ronik.geotraveljournal.R
import com.ronik.geotraveljournal.activity.MapFragment

@Composable
fun MapFragmentScreen() {
    val context = LocalContext.current

    AndroidView(
        factory = { ctx ->
            val fragmentManager = (ctx as? AppCompatActivity)?.supportFragmentManager
            val existingFragment = fragmentManager?.findFragmentByTag(MapFragment::class.java.simpleName)

            if (existingFragment == null) {
                val fragment = MapFragment()
                fragmentManager?.beginTransaction()?.apply {
                    replace(R.id.mapview, fragment, MapFragment::class.java.simpleName)
                    commit()
                }
                fragment
            } else {
                existingFragment as MapFragment
            }
            existingFragment?.requireView() ?: throw IllegalStateException("map fragment is null")
        }
    )
}
