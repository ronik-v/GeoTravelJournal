package com.ronik.geotraveljournal.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.ronik.geotraveljournal.Config
import com.ronik.geotraveljournal.R
import com.yandex.mapkit.MapKitFactory

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.setApiKey(Config.mapApiKey)
        MapKitFactory.initialize(this)

        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            val fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
            val mapFragment = MapFragment()

            fragmentTransaction.replace(R.id.fragment_map, mapFragment)
            fragmentTransaction.commit()
        }
    }

    fun openMapWithRoute(route: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("route", route)
        startActivity(intent)
    }
}

