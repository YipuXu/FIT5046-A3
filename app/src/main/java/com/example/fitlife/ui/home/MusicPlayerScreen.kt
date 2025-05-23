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
 * Music player screen: searching for a default song, showing MusicPlayerSection
 */
@Composable
fun MusicPlayerScreen(
    viewModel: MusicViewModel = viewModel()
) {
    // Perform a search on startup
    LaunchedEffect(Unit) {
        viewModel.search("workout anthem")
    }

    val track by viewModel.currentTrack.collectAsState()

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (track == null) {
            // shows loading
            CircularProgressIndicator()
        } else {
            // display player
            MusicPlayerSection(
                track = track!!,
                onPlay = { previewUrl ->
                    try {
                        // play 30 seconds to listen
                        MediaPlayer().apply {
                            setDataSource(previewUrl)
                            prepare()
                            start()
                            setOnCompletionListener { release() }
                        }
                    } catch (e: Exception) {
                        Log.e("MusicPlayer", "Play failed", e)
                    }
                }
            )
        }
    }
}
