package com.example.fitlife.model

data class Track(
    val trackName: String,       // 歌曲名称，对应 JSON 的 "trackName"
    val artistName: String,      // 艺术家/歌手，对应 JSON 的 "artistName"
    val artworkUrl100: String,   // 封面图 URL（100×100），对应 JSON 的 "artworkUrl100"
    val previewUrl: String       // 30 秒试听音频 URL，对应 JSON 的 "previewUrl"
)
