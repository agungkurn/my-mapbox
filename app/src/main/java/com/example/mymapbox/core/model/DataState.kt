package com.example.mymapbox.core.model

sealed class DataState<out T : Any> {
	object Loading : DataState<Nothing>()
	data class Success<out T : Any>(val data: T) : DataState<T>()
	data class Failed(
		var errorMessage: String? = null,
		var exception: Exception? = null
	) : DataState<Nothing>()
}