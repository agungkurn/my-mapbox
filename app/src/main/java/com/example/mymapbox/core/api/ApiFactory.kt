package com.example.mymapbox.core.api

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiFactory {
	private const val BASE_URL = "https://api.mapbox.com/"

	private val client = OkHttpClient.Builder()
		.addInterceptor(
			HttpLoggingInterceptor().apply {
				level = HttpLoggingInterceptor.Level.BODY
			}
		).build()
	private val gson = GsonBuilder().serializeNulls().create()

	val service = Retrofit.Builder()
		.baseUrl(BASE_URL)
		.addConverterFactory(GsonConverterFactory.create(gson))
		.client(client)
		.build()
		.create(ApiService::class.java)
}