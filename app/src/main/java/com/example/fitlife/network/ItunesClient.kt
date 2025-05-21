package com.example.fitlife.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Retrofit 客户端单例，用来构建 ItunesApiService
 */
object ItunesClient {

    private const val BASE_URL = "https://itunes.apple.com/"

    val api: ItunesApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ItunesApiService::class.java)
    }
}
