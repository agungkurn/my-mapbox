package com.example.mymapbox

import com.example.mymapbox.core.api.ApiService
import com.example.mymapbox.core.model.SearchResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class SearchRepository private constructor(private val apiService: ApiService) {
	companion object {
		private var instance: SearchRepository? = null

		fun getInstance(apiService: ApiService): SearchRepository {
			if (instance == null) {
				instance = SearchRepository(apiService)
			}
			return instance!!
		}
	}

	suspend fun searchLocation(keyword: String): SearchResponse {
		return try {
			val response = apiService.searchLocation(keyword)

			withContext(Dispatchers.Main) {
				if (response.isSuccessful) {
					response.body() ?: throw IOException("data is null")
				} else {
					throw HttpException(response)
				}
			}
		} catch (e: Exception) {
			throw e
		}
	}
}