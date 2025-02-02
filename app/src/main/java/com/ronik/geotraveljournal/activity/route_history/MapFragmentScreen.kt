package com.ronik.geotraveljournal.activity.route_history

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentTransaction
import com.ronik.geotraveljournal.R
import com.ronik.geotraveljournal.activity.MapFragment

@Composable
fun MapFragmentScreen(routeString: String?) {
    val context = LocalContext.current
    val activity = context as? AppCompatActivity ?: return

    LaunchedEffect(routeString) {
        if (routeString != null) {
            val fragmentTransaction: FragmentTransaction = activity.supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(
                R.id.fragment_map,
                MapFragment().apply {
                    arguments = Bundle().apply {
                        putString("route", routeString)
                    }
                }
            )
            fragmentTransaction.commit()
        }
    }
}
