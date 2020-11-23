package com.example.mymapbox.core.model

import com.google.gson.annotations.SerializedName

data class SearchResponse(

	@field:SerializedName("features")
	val features: List<FeaturesItem> = emptyList(),

	@field:SerializedName("attribution")
	val attribution: String? = null,
)

data class FeaturesItem(

	@field:SerializedName("place_name")
	val placeName: String? = null,

	@field:SerializedName("place_type")
	val placeType: List<String> = emptyList(),

	@field:SerializedName("center")
	val center: List<Double> = emptyList(),

	@field:SerializedName("context")
	val context: List<ContextItem> = emptyList(),

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("text")
	val text: String? = null,

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("relevance")
	val relevance: Double? = null,
)

data class ContextItem(

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("text")
	val text: String? = null,
)
