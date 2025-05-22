package com.example.fitlife.network

import com.example.fitlife.model.ItunesResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * iTunes Search API 的 Retrofit 接口，只定义我们要用到的 searchMusic 方法
 */
interface ItunesApiService {
    @GET("search")
    suspend fun searchMusic(
        @Query("term") term: String,           // 搜索关键词
        @Query("media") media: String = "music", // 媒体类型固定为 music
        @Query("limit") limit: Int = 1         // 返回结果数量，默认 1
    ): ItunesResponse
}
