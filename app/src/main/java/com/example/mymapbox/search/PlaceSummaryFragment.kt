package com.example.mymapbox.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.mymapbox.core.model.FeaturesItem
import com.example.mymapbox.databinding.FragmentPlaceSummaryBinding

class PlaceSummaryFragment private constructor() : Fragment() {
	companion object {
		private var featuresItem: FeaturesItem? = null

		fun getInstance(featuresItem: FeaturesItem): PlaceSummaryFragment {
			if (this.featuresItem == null) {
				this.featuresItem = featuresItem
			}

			return PlaceSummaryFragment()
		}
	}

	private var binding: FragmentPlaceSummaryBinding? = null

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
	): View? {
		// Inflate the layout for this fragment
		binding = FragmentPlaceSummaryBinding.inflate(inflater, container, false)
		return binding?.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding?.let {
			it.tvPlaceName.text = featuresItem?.text ?: "No name"
			it.tvAddress.text = featuresItem?.placeName ?: "-"
		}
	}
}