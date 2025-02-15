package com.ronik.geotraveljournal

import android.app.Application
import com.yandex.mapkit.MapKitFactory

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        MapKitFactory.setApiKey(Config.mapApiKey)
        MapKitFactory.initialize(this)
    }
}