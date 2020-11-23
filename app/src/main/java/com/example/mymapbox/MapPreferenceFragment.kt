package com.example.mymapbox

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat

class MapPreferenceFragment : PreferenceFragmentCompat() {
	override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
		setPreferencesFromResource(R.xml.map_preferences, rootKey)
	}
}