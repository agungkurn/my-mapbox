package com.example.mymapbox

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.mymapbox.core.api.ApiFactory
import com.example.mymapbox.core.model.SearchingState
import com.example.mymapbox.databinding.ActivityMainBinding
import com.example.mymapbox.preference.MapPreferenceFragment
import com.example.mymapbox.search.PlaceSummaryFragment
import com.example.mymapbox.search.SearchFragment
import com.example.mymapbox.search.SearchRepository
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mapbox.mapboxsdk.Mapbox

class MainActivity : AppCompatActivity() {
	private val binding by lazy {
		ActivityMainBinding.inflate(layoutInflater)
	}

	val bottomSheet by lazy {
		BottomSheetBehavior.from(binding.bottomSheet)
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
		bottomSheet.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
			override fun onSlide(bottomSheet: View, slideOffset: Float) {}

			override fun onStateChanged(bottomSheet: View, newState: Int) {
				when (newState) {
					BottomSheetBehavior.STATE_HIDDEN -> {
						binding.fabSearch.show()
						binding.fabSettings.show()
					}
					BottomSheetBehavior.STATE_COLLAPSED -> {
						if (viewModel.foundPlacePosition.value is SearchingState.Idle) {
							// If not in search mode, disable collapse
							this@MainActivity.bottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
						}
					}
					BottomSheetBehavior.STATE_EXPANDED -> {
						if (viewModel.foundPlacePosition.value is SearchingState.FoundPlace) {
							// If in search mode, disable expanded
							this@MainActivity.bottomSheet.state =
								BottomSheetBehavior.STATE_COLLAPSED
						}
					}
					BottomSheetBehavior.STATE_HALF_EXPANDED -> {
						if (viewModel.foundPlacePosition.value is SearchingState.FoundPlace) {
							// If in search mode, disable expanded
							this@MainActivity.bottomSheet.state =
								BottomSheetBehavior.STATE_COLLAPSED
						}
					}
				}
			}
		})

		binding.fabSearch.setOnClickListener {
			binding.fabSearch.hide()
			binding.fabSettings.hide()

			bottomSheet.state = BottomSheetBehavior.STATE_EXPANDED

			supportFragmentManager
				.beginTransaction()
				.replace(binding.layoutBottom.id, SearchFragment())
				.commit()
		}

		binding.fabSettings.setOnClickListener {
			binding.fabSearch.hide()
			binding.fabSettings.hide()

			bottomSheet.state = BottomSheetBehavior.STATE_HALF_EXPANDED

			supportFragmentManager
				.beginTransaction()
				.replace(binding.layoutBottom.id, MapPreferenceFragment())
				.commit()
		}

		binding.ivClose.setOnClickListener {
			viewModel.foundPlacePosition.value = SearchingState.Idle
		}

		bottomSheet.state = BottomSheetBehavior.STATE_HIDDEN

		viewModel.foundPlacePosition.observe(this) {
			if (it is SearchingState.FoundPlace) {
				// Hide irrelevant controls
				binding.fabSettings.hide()
				binding.fabSearch.hide()

				// Show bottom sheet
				bottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED

				// Show search result
				supportFragmentManager
					.beginTransaction()
					.replace(binding.layoutBottom.id, PlaceSummaryFragment.getInstance(it.data))
					.commit()
			} else {
				bottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
			}
		}
	}

	override fun onBackPressed() {
		if (viewModel.foundPlacePosition.value is SearchingState.FoundPlace
			|| bottomSheet.state != BottomSheetBehavior.STATE_HIDDEN
		) {
			// clear search result
			viewModel.foundPlacePosition.value = SearchingState.Idle
			return
		}

		super.onBackPressed()
	}
}