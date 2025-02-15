package com.ronik.geotraveljournal.activity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.ronik.geotraveljournal.R
import com.ronik.geotraveljournal.navigation.AppNavigation
import com.ronik.geotraveljournal.utils.GeoTravelTheme

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setContent {
            GeoTravelTheme {
                AppNavigation()
            }
        }
    }
}

