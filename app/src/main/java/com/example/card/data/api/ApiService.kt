package com.example.card.data.api

import com.example.card.data.model.Version
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("version")
    suspend fun getVersion(@Query("code") code: String): Version
}