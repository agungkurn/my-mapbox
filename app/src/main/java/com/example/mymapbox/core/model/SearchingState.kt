package com.example.mymapbox.core.model

sealed class SearchingState {
	/**
	 * NotUsed: Never used before
	 * Idle: Has been used, but currently not searching
	 * FoundPlace: Found a location
	 */
	object NotUsed : SearchingState()
	object Idle : SearchingState()
	data class FoundPlace(val data: FeaturesItem) : SearchingState()
}