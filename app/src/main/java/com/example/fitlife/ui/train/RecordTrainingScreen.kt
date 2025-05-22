package com.example.fitlife.ui.train

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.SentimentNeutral
import androidx.compose.material.icons.outlined.SentimentSatisfiedAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.fitlife.MyApplication
import com.example.fitlife.data.model.Workout
import com.example.fitlife.ui.components.BottomNavBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordTrainingScreen(
    currentRoute: String = "profile",
    onNavigateToHome: () -> Unit = {},
    onNavigateToCalendar: () -> Unit = {},
    onNavigateToMap: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    val context = LocalContext.current

    var trainingType by remember { mutableStateOf("Strength Training") }
    val trainingTypes = listOf("Strength Training", "Cardio", "Yoga", "Swimming")

    var duration by remember { mutableStateOf(30) }
    var calories by remember { mutableStateOf("") }
    var intensity by remember { mutableStateOf("Challenging") }
    var notes by remember { mutableStateOf(TextFieldValue("")) }
    var selectedDate by remember { mutableStateOf("Select Date") }
    var selectedTime by remember { mutableStateOf("Select Time") }

    val dateFormatter = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    val timeFormatter = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Record Workout",
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        },
        bottomBar = {
            BottomNavBar(
                currentRoute = currentRoute,
                onNavigateToHome = onNavigateToHome,
                onNavigateToCalendar = onNavigateToCalendar,
                onNavigateToMap = onNavigateToMap,
                onNavigateToProfile = onNavigateToProfile
            )
        }
    ) { padding ->
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            var expanded by remember { mutableStateOf(false) }
            Text("Workout Type")
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = trainingType,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                        .clickable { expanded = true }
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    trainingTypes.forEach {
                        DropdownMenuItem(
                            text = { Text(it) },
                            onClick = {
                                trainingType = it
                                expanded = false
                            }
                        )
                    }
                }
            }

            Button(onClick = {
                val calendar = Calendar.getInstance()
                DatePickerDialog(
                    context,
                    { _, year, month, dayOfMonth ->
                        calendar.set(year, month, dayOfMonth)
                        selectedDate = dateFormatter.format(calendar.time)
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            }, modifier = Modifier.fillMaxWidth()) {
                Text("Select Date: $selectedDate")
            }

            Button(onClick = {
                val calendar = Calendar.getInstance()
                TimePickerDialog(
                    context,
                    { _, hourOfDay, minute ->
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        calendar.set(Calendar.MINUTE, minute)
                        selectedTime = timeFormatter.format(calendar.time)
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
                ).show()
            }, modifier = Modifier.fillMaxWidth()) {
                Text("Select Time: $selectedTime")
            }

            Column {
                Text("Workout Duration (minutes)")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(0, 30, 60, 90, 120).forEach {
                        OutlinedButton(
                            onClick = { duration = it },
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (it == duration) Color.Blue.copy(alpha = 0.1f) else Color.Transparent
                            )
                        ) {
                            Text("$it")
                        }
                    }
                }
            }

            Text("Calories Burned (kcal)")
            OutlinedTextField(
                value = calories,
                onValueChange = { calories = it },
                label = { Text("Calories Burned (kcal)") },
                modifier = Modifier.fillMaxWidth()
            )

            Column {
                Text("Workout Intensity")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    listOf("Easy", "Moderate", "Challenging", "Hard").forEach {
                        val selected = it == intensity
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .size(60.dp)
                                .background(
                                    color = if (selected) Color.Blue.copy(alpha = 0.1f) else Color.LightGray.copy(alpha = 0.1f),
                                    shape = CircleShape
                                )
                                .clickable { intensity = it },
                        ) {
                            Icon(
                                imageVector = when (it) {
                                    "Easy" -> Icons.Outlined.SentimentSatisfiedAlt
                                    "Moderate" -> Icons.Outlined.SentimentNeutral
                                    "Challenging" -> Icons.Outlined.FitnessCenter
                                    else -> Icons.Filled.Warning
                                },
                                contentDescription = it,
                                tint = if (selected) Color.Blue else Color.Gray,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            }

            Text("Notes")
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            )

            Button(
                onClick = {
                    val dao = (context.applicationContext as MyApplication).database.workoutDao()
                    val workout = Workout(
                        type = trainingType,
                        duration = duration,
                        calories = calories.toIntOrNull() ?: 0,
                        intensity = intensity,
                        notes = notes.text,
                        date = selectedDate,
                        time = selectedTime
                    )
                    CoroutineScope(Dispatchers.IO).launch {
                        dao.insertWorkout(workout)
                    }
                    Toast.makeText(context, "Saved to Room DB!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Record")
            }
        }
    }
}
