package com.example.fitlife.ui.home

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitlife.ui.components.ExerciseCard

@Composable
fun ExerciseRecommendation(
    viewModel: ExerciseViewModel = viewModel()
) {
    val exercises by viewModel.exercises.collectAsState()
    val hasNext by viewModel.hasNextPage.collectAsState()

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Recommended Exercises", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.weight(1f))
            Button(
                onClick = { viewModel.loadNextPage() },
                enabled = hasNext
            ) {
                Text("Next")
            }
        }

        Spacer(Modifier.height(8.dp))

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(exercises) { ex ->
                ExerciseCard(
                    exercise = ex,
                    modifier = Modifier.width(260.dp)
                )
            }
        }
    }
}

