package com.example.mymapbox.core.api

import com.example.mymapbox.BuildConfig
import com.example.mymapbox.core.model.SearchResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
	@GET("geocoding/v5/mapbox.places/{keyword}.json")
	suspend fun searchLocation(
		@Path("keyword") keyword: String,
		@Query("access_token") token: String = BuildConfig.MAPS_API_KEY
	): Response<SearchResponse>
}