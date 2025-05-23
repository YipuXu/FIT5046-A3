package com.example.fitlife.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.fitlife.model.Track
import androidx.compose.ui.Alignment
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow

/**
 * Music player card component
 *
 * @param track The song information to be displayed
 * @param onPlay Callback when clicking to play, pass in previewUrl
 * @param modifier External modifier
 */
@Composable
fun MusicPlayerSection(
    track: Track,
    onPlay: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Cover image
            AsyncImage(
                model = track.artworkUrl100,
                contentDescription = track.trackName,
                modifier = Modifier
                    .size(48.dp)
                    .padding(end = 12.dp),
                contentScale = ContentScale.Crop
            )

            // Song information
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = track.trackName,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = track.artistName,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            // Play Button
            IconButton(onClick = { onPlay(track.previewUrl) }) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "play",
                    tint = Color(0xFF1976D2),
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}
