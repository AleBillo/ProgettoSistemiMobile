package com.example.common.api.quotes

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface QuotesApiService {
    @GET("json")
    suspend fun getQuote(
        @Query("prompt") prompt: String,
        @Query("quotes") quotesParam: String = "array of quotes"
    ): Response<QuotesApiResponse>
}