package com.example.mymapbox

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.mymapbox.core.api.ApiFactory
import com.example.mymapbox.databinding.ActivityMainBinding
import com.mapbox.mapboxsdk.Mapbox

class MainActivity : AppCompatActivity() {
	private val layout by lazy {
		ActivityMainBinding.inflate(layoutInflater)
	}

	private lateinit var viewModel: MainViewModel

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		Mapbox.getInstance(this, getString(R.string.mapbox_access_token))
		setContentView(layout.root)

		val repository = SearchRepository.getInstance(ApiFactory.service)
		viewModel = ViewModelProvider(
			this, MainViewModel.getViewModelFactory(repository)
		)[MainViewModel::class.java]
	}
}