package com.example.common.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val JSON_GPT_BASE_URL = "https://api.jsongpt.com/"

    val jsonGptApi: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(JSON_GPT_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(ApiService::class.java)
    }
}