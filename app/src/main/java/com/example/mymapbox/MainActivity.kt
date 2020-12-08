package com.example.mymapbox

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.mymapbox.core.api.ApiFactory
import com.example.mymapbox.core.model.SearchingState
import com.example.mymapbox.databinding.ActivityMainBinding
import com.example.mymapbox.preference.MapPreferenceFragment
import com.example.mymapbox.search.PlaceSummaryFragment
import com.example.mymapbox.search.SearchFragment
import com.example.mymapbox.search.SearchRepository
import com.mapbox.mapboxsdk.Mapbox

class MainActivity : AppCompatActivity() {
	private val binding by lazy {
		ActivityMainBinding.inflate(layoutInflater)
	}

	private lateinit var viewModel: MainViewModel

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		Mapbox.getInstance(this, getString(R.string.mapbox_access_token))
		setContentView(binding.root)

		val repository = SearchRepository.getInstance(ApiFactory.service)
		viewModel = ViewModelProvider(
			this, MainViewModel.getViewModelFactory(repository, application)
		)[MainViewModel::class.java]

		setupSearchLayout()
	}

	private fun setupSearchLayout() {
		binding.fabSearch.setOnClickListener {
			binding.fabSearch.hide()
			binding.fabSettings.hide()

			SearchFragment.getInstance()
				.show(supportFragmentManager, "search")
		}

		binding.fabSettings.setOnClickListener {
			binding.fabSearch.hide()
			binding.fabSettings.hide()

			MapPreferenceFragment.getInstance()
				.show(supportFragmentManager, "preferences")
		}

		viewModel.foundPlacePosition.observe(this) {
			if (it is SearchingState.FoundPlace) {
				// Show search result
				PlaceSummaryFragment.getInstance(it.data)
					.show(supportFragmentManager, "places")
			}
		}
	}

	override fun onBackPressed() {
		if (viewModel.foundPlacePosition.value is SearchingState.FoundPlace) {
			// clear search result
			viewModel.foundPlacePosition.value = SearchingState.Idle
		} else {
			super.onBackPressed()
		}
	}
}