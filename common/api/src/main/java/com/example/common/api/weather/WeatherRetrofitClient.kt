package com.example.common.api.weather

import android.content.Context
import com.example.common.api.R
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object WeatherRetrofitClient {

    private const val BASE_URL = "https://api.openweathermap.org/"

    lateinit var API_KEY: String
        private set

    private var isInitialized = false

    fun initialize(context: Context) {
        if (isInitialized) return
        API_KEY = context.applicationContext.getString(R.string.openweather_api_key)
        isInitialized = true
    }

    private val okHttpClient = OkHttpClient.Builder()
        .build()

    val openWeatherApi: WeatherApiService by lazy {
        if (!isInitialized) {
            throw IllegalStateException("WeatherRetrofitClient must be initialized by calling .initialize(context) before use.")
        }
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(WeatherApiService::class.java)
    }
}