package com.example.mymapbox

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mymapbox.core.SearchResultAdapter
import com.example.mymapbox.core.model.DataState
import com.example.mymapbox.core.model.FeaturesItem
import com.example.mymapbox.databinding.FragmentSearchBinding
import com.mapbox.mapboxsdk.geometry.LatLng

class SearchFragment : Fragment() {
	private val TAG = SearchFragment::class.java.simpleName
	private val viewModel: MainViewModel by activityViewModels()

	private var layout: FragmentSearchBinding? = null

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
	): View? {
		layout = FragmentSearchBinding.inflate(inflater, container, false)
		return layout?.root
	}

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)

		layout?.inputSearch?.editText?.setOnFocusChangeListener { v, hasFocus ->
			viewModel.isInSearchMode.value = hasFocus
		}

		layout?.inputSearch?.editText?.setOnKeyListener { v, keyCode, event ->
			if (event.keyCode == KeyEvent.KEYCODE_BACK) {
				layout?.inputSearch?.editText?.clearFocus()
				true
			} else {
				false
			}
		}

		layout?.rvSearch?.addItemDecoration(
			DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL)
		)

		setupSearchField()
	}

	private fun setupSearchField() {
		layout?.inputSearch?.editText?.doAfterTextChanged {
			it?.let {
				if (it.toString().isNotEmpty()) {
					viewModel.searchLocation(it.toString())
				}
			}
		}

		viewModel.searchResult.observe(viewLifecycleOwner) {
			when (it) {
				is DataState.Loading -> {
					layout?.pbSearch?.visibility = View.VISIBLE
					layout?.rvSearch?.visibility = View.GONE
				}
				is DataState.Success -> {
					layout?.pbSearch?.visibility = View.GONE
					layout?.rvSearch?.visibility = View.VISIBLE

					showSearchResult(it.data)
				}
				is DataState.Failed -> {
					layout?.pbSearch?.visibility = View.GONE
					layout?.rvSearch?.visibility = View.VISIBLE

					it.errorMessage?.let {
						Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
					}

					it.exception?.let {
						Log.e(
							TAG,
							"setupSearchField: ${it::class.java.simpleName}: ${it.localizedMessage}"
						)
					}
				}
			}
		}
	}

	private fun showSearchResult(items: List<FeaturesItem>) {
		SearchResultAdapter(items) {
			viewModel.foundPlacePosition.value = Pair(
				it.text ?: "No name", LatLng(it.center?.last()!!, it.center.first()!!)
			)
		}.also {
			layout?.rvSearch?.adapter = it
			it.notifyDataSetChanged()
		}
	}

	override fun onDestroyView() {
		super.onDestroyView()
		layout = null
	}
}