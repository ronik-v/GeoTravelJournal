package com.ronik.geotraveljournal.activity.route_history

import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.ronik.geotraveljournal.R
import com.ronik.geotraveljournal.activity.MapFragment

@Composable
fun MapFragmentScreen(route: String?) {
    val activity = LocalContext.current as AppCompatActivity

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            FrameLayout(context).apply {
                id = R.id.fragment_map
            }
        },
        update = { container ->
            val fm = activity.supportFragmentManager
            val existingFragment = fm.findFragmentById(container.id) as? MapFragment
            if (existingFragment == null) {
                val newFragment = MapFragment().apply {
                    arguments = Bundle().apply { putString("route", route) }
                }
                fm.beginTransaction()
                    .replace(container.id, newFragment, MapFragment::class.java.simpleName)
                    .commit()
            }
        }
    )
}
