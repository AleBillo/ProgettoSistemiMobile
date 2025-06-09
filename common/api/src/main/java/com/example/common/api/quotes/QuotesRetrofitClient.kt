package com.example.common.api.quotes

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object QuotesRetrofitClient {

    private const val JSON_GPT_BASE_URL = "https://api.jsongpt.com/"

    val jsonGptApi: QuotesApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(JSON_GPT_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(QuotesApiService::class.java)
    }
}