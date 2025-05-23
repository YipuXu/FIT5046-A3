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
 * A ViewModel that pulls data from the iTunes Search API and exposes it to the UI
 */
class MusicViewModel : ViewModel() {

    private val _currentTrack = MutableStateFlow<Track?>(null)
    val currentTrack: StateFlow<Track?> = _currentTrack

    /**
     * Search for music and send the first result to _currentTrack
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
