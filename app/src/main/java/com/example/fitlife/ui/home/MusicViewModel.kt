// 文件路径：app/src/main/java/com/example/fitlife/ui/home/MusicViewModel.kt
package com.example.fitlife.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitlife.model.Track
import com.example.fitlife.network.ItunesClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * 用来从 iTunes Search API 拉取数据并暴露给 UI 的 ViewModel
 */
class MusicViewModel : ViewModel() {

    // 当前要展示的 Track（如果没有，就为 null）
    private val _currentTrack = MutableStateFlow<Track?>(null)
    val currentTrack: StateFlow<Track?> = _currentTrack

    /**
     * 按关键词搜索音乐，并把第一条结果发到 _currentTrack
     */
    fun search(term: String) {
        viewModelScope.launch {
            try {
                val resp = ItunesClient.api.searchMusic(term = term)
                if (resp.results.isNotEmpty()) {
                    _currentTrack.value = resp.results[0]
                }
            } catch (e: Exception) {
                Log.e("MusicVM", "search failed", e)
            }
        }
    }
}
