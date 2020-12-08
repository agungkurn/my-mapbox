package com.example.mymapbox.search

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mymapbox.core.model.FeaturesItem
import com.example.mymapbox.databinding.FragmentPlaceSummaryBinding
import com.example.mymapbox.navigation.NavigationActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class PlaceSummaryFragment private constructor() : BottomSheetDialogFragment() {
	companion object {
		private var destinationFeatures: FeaturesItem? = null

		fun getInstance(featuresItem: FeaturesItem): PlaceSummaryFragment {
			if (this.destinationFeatures == null) {
				this.destinationFeatures = featuresItem
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
			it.tvPlaceName.text = destinationFeatures?.text ?: "No name"
			it.tvAddress.text = destinationFeatures?.placeName ?: "-"

			it.btnNavigate.setOnClickListener {
				startActivity(Intent(context, NavigationActivity::class.java))
			}
		}
	}

	override fun onDismiss(dialog: DialogInterface) {
		destinationFeatures = null
		super.onDismiss(dialog)
	}
}