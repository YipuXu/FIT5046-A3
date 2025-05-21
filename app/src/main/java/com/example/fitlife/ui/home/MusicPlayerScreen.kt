// 文件路径：app/src/main/java/com/example/fitlife/ui/home/MusicPlayerScreen.kt
package com.example.fitlife.ui.home

import android.media.MediaPlayer
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitlife.ui.components.MusicPlayerSection

/**
 * 音乐播放器屏幕：搜索一首默认歌曲，展示 MusicPlayerSection
 */
@Composable
fun MusicPlayerScreen(
    viewModel: MusicViewModel = viewModel()
) {
    // 启动时执行一次搜索
    LaunchedEffect(Unit) {
        viewModel.search("workout anthem")
    }

    // 订阅当前 Track
    val track by viewModel.currentTrack.collectAsState()

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (track == null) {
            // 数据尚未加载，显示加载中
            CircularProgressIndicator()
        } else {
            // 数据加载完成，展示播放器
            MusicPlayerSection(
                track = track!!,
                onPlay = { previewUrl ->
                    try {
                        // 简单播放 30 秒试听
                        MediaPlayer().apply {
                            setDataSource(previewUrl)
                            prepare()
                            start()
                            setOnCompletionListener { release() }
                        }
                    } catch (e: Exception) {
                        Log.e("MusicPlayer", "播放失败", e)
                    }
                }
            )
        }
    }
}
