package com.example.mymapbox

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.mymapbox.databinding.FragmentMapBinding
import com.mapbox.android.core.location.LocationEngineRequest
import com.mapbox.geojson.Feature
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource

class MapFragment : Fragment() {
	private val RC_LOCATION_PERMISSION = 100

	private val MARKER_LOCATION_ID = "source"
	private val MARKER_ICON_ID = "icon"
	private val MARKER_LAYER_ID = "layer"

	private val viewModel by activityViewModels<MainViewModel>()
	private var binding: FragmentMapBinding? = null
	private var savedInstanceState: Bundle? = null

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
	): View? {
		// Inflate the layout for this fragment
		binding = FragmentMapBinding.inflate(inflater, container, false)
		return binding?.root
	}

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)

		if (ContextCompat.checkSelfPermission(
				requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
			) == PackageManager.PERMISSION_GRANTED
		) {
			this.savedInstanceState = savedInstanceState
			prepareMap()
		} else {
			ActivityCompat.requestPermissions(
				requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
				RC_LOCATION_PERMISSION
			)
		}
	}

	private fun prepareMap() {
		binding?.mapView?.onCreate(savedInstanceState)
		binding?.mapView?.getMapAsync {
			it.uiSettings.attributionGravity = Gravity.BOTTOM
			it.uiSettings.logoGravity = Gravity.BOTTOM

			it.setStyle(
				Style.Builder()
					.fromUri(Style.TRAFFIC_DAY)
					.withImage(
						MARKER_ICON_ID,
						BitmapFactory.decodeResource(
							resources, R.drawable.mapbox_marker_icon_default
						)
					)
			) { style ->
				activateLocationComponent(it, style)
				observeSearchResult(it, style)
			}
		}
	}

	@SuppressLint("MissingPermission")
	private fun activateLocationComponent(map: MapboxMap, style: Style) {
		map.locationComponent.also {
			it.activateLocationComponent(
				LocationComponentActivationOptions.builder(requireContext(), style)
					.useDefaultLocationEngine(true)
					.locationEngineRequest(
						LocationEngineRequest.Builder(500)
							.setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
							.build()
					)
					.build()
			)

			it.isLocationComponentEnabled = true
			it.renderMode = RenderMode.COMPASS
			it.cameraMode = CameraMode.TRACKING

			it.lastKnownLocation?.let { location ->
				moveCamera(LatLng(location), map)

				binding?.fabMyLocation?.setOnClickListener {
					moveCamera(LatLng(location), map)
				}
			}
		}
	}

	private fun moveCamera(latLong: LatLng, map: MapboxMap) {
		val cameraPosition = CameraPosition.Builder()
			.target(latLong)
			.zoom(15.0)
			.build()

		map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 1000)
	}

	private fun observeSearchResult(mapbox: MapboxMap, style: Style) {
		viewModel.foundPlacePosition.observe(viewLifecycleOwner) { (placeName, latLong) ->
			// remove marker
			style.getLayer(MARKER_LAYER_ID)?.let {
				style.removeLayer(it)
			}
			style.getSource(MARKER_LOCATION_ID)?.let {
				style.removeSource(it)
			}

			// add marker
			style.addSource(
				GeoJsonSource(
					MARKER_LOCATION_ID,
					Feature.fromGeometry(Point.fromLngLat(latLong.longitude, latLong.latitude))
				)
			)
			style.addLayer(
				SymbolLayer(MARKER_LAYER_ID, MARKER_LOCATION_ID)
					.withProperties(PropertyFactory.iconImage(MARKER_ICON_ID))
			)

			moveCamera(latLong, mapbox)
		}
	}

	override fun onStart() {
		super.onStart()
		binding?.mapView?.onStart()
	}

	override fun onResume() {
		super.onResume()
		binding?.mapView?.onResume()
	}

	override fun onPause() {
		super.onPause()
		binding?.mapView?.onPause()
	}

	override fun onStop() {
		super.onStop()
		binding?.mapView?.onStop()
	}

	override fun onDestroy() {
		super.onDestroy()
		binding?.mapView?.onDestroy()
	}

	override fun onSaveInstanceState(outState: Bundle) {
		super.onSaveInstanceState(outState)
		binding?.mapView?.onSaveInstanceState(outState)
	}

	override fun onLowMemory() {
		super.onLowMemory()
		binding?.mapView?.onLowMemory()
	}

	override fun onRequestPermissionsResult(
		requestCode: Int, permissions: Array<out String>, grantResults: IntArray
	) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults)

		if (requestCode == RC_LOCATION_PERMISSION) {
			if (grantResults.first() == PackageManager.PERMISSION_GRANTED) {
				prepareMap()
			}
		}
	}
}