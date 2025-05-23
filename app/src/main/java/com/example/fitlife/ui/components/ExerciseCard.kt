package com.example.fitlife.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.fitlife.model.Exercise

@Composable
fun ExerciseCard(
    exercise: Exercise,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .padding(vertical = 8.dp, horizontal = 12.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = exercise.name,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(4.dp))

            // Major Muscles & Equipment
            Text(
                text = "Target: ${exercise.targetMuscles.joinToString()}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Equipment: ${exercise.equipments.joinToString()}",
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(Modifier.height(4.dp))

            // Secondary muscles
            if (exercise.secondaryMuscles.isNotEmpty()) {
                Text(
                    text = "Secondary: ${exercise.secondaryMuscles.joinToString()}",
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(Modifier.height(4.dp))
            }

            // steps
            Text(
                text = "Instructions:",
                style = MaterialTheme.typography.labelLarge
            )
            Spacer(Modifier.height(4.dp))
            exercise.instructions.forEachIndexed { index, step ->
                Text(
                    text = "${index + 1}. $step",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 8.dp, bottom = 2.dp)
                )
            }
        }
    }
}
