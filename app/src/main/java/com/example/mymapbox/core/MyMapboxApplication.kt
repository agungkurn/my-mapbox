package com.example.mymapbox.core

import android.app.Application
import com.example.mymapbox.BuildConfig
import com.mapbox.search.MapboxSearchSdk
import com.mapbox.search.location.DefaultLocationProvider

class MyMapboxApplication : Application() {
	override fun onCreate() {
		super.onCreate()
		MapboxSearchSdk.initialize(
			this, BuildConfig.MAPS_API_KEY, DefaultLocationProvider(this)
		)
	}
}