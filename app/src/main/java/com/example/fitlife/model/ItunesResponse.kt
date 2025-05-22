package com.example.fitlife.model

data class ItunesResponse(
    val resultCount: Int,      // 返回结果数量
    val results: List<Track>   // 歌曲列表
)
