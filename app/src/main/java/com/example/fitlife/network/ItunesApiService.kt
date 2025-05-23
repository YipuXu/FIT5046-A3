package com.example.fitlife.network

import com.example.fitlife.model.ItunesResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit interface for iTunes Search API
 */
interface ItunesApiService {
    @GET("search")
    suspend fun searchMusic(
        @Query("term") term: String,
        @Query("media") media: String = "music",
        @Query("limit") limit: Int = 1
    ): ItunesResponse
}
