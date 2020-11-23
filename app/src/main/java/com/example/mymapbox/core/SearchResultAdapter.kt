package com.example.mymapbox.core

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.mymapbox.core.model.FeaturesItem
import com.example.mymapbox.databinding.ListSearchBinding

class SearchResultAdapter(
	private val items: List<FeaturesItem>, private val onClick: (FeaturesItem) -> Unit
) : RecyclerView.Adapter<SearchResultAdapter.SearchResultViewHolder>() {

	private lateinit var layout: ListSearchBinding

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultViewHolder {
		layout = ListSearchBinding.inflate(LayoutInflater.from(parent.context))
		return SearchResultViewHolder(layout.root)
	}

	override fun onBindViewHolder(holder: SearchResultViewHolder, position: Int) {
		holder.bindItem(items[position])
	}

	override fun getItemCount() = items.size

	inner class SearchResultViewHolder(view: View) : RecyclerView.ViewHolder(view) {
		fun bindItem(item: FeaturesItem) {
			layout.tvPlaceTitle.text = item.text
			layout.tvPlaceName.text = item.placeName

			itemView.setOnClickListener {
				if (item.center?.first() != null && item.center.last() != null) {
					onClick(item)
				} else {
					Toast.makeText(
						layout.root.context, "Location is not available", Toast.LENGTH_SHORT
					).show()
				}
			}
		}
	}
}