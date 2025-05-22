package com.example.fitlife.ui.train

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitlife.R
import com.example.fitlife.data.model.Workout
import com.example.fitlife.MyApplication

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllRecentRecordsScreen(
    onBack: () -> Unit = {},
    onAddRecord: () -> Unit = {}
) {
    val context = LocalContext.current
    val workoutDao = (context.applicationContext as MyApplication).database.workoutDao()
    val allWorkouts by workoutDao.getAll().collectAsState(initial = emptyList())

    // State for the workout detail dialog
    var showDialog by remember { mutableStateOf(false) }
    var selectedWorkout by remember { mutableStateOf<Workout?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("All Recent Records", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddRecord,
                containerColor = Color(0xFF3B82F6),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Record")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF9FAFB))
                .padding(padding)
                .padding(16.dp)
        ) {
            if (allWorkouts.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No records found.", color = Color.Gray)
                }
            } else {
                allWorkouts.forEach { workout ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { // Make the item clickable
                                    selectedWorkout = workout
                                    showDialog = true
                                }
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // 图标
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Color(0xFFE6F0FF), RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_workout),
                                    contentDescription = null,
                                    tint = Color(0xFF3B82F6),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            // 文字信息
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 12.dp)
                            ) {
                                Text(
                                    text = workout.type,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "${workout.date} · ${workout.duration} min · ${workout.calories} kcal",
                                    fontSize = 14.sp,
                                    color = Color(0xFF6B7280),
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                            // Right arrow
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowRight,
                                contentDescription = "View details",
                                tint = Color(0xFF9CA3AF)
                            )
                        }
                    }
                }
            }
        }
    }

    // Workout detail dialog
    if (showDialog && selectedWorkout != null) {
        val workout = selectedWorkout // Create a local non-nullable variable
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(workout!!.type) },
            text = {
                Column {
                    Text("Date: ${workout!!.date}")
                    Text("Time: ${workout!!.time}")
                    Text("Duration: ${workout!!.duration} min")
                    Text("Calories: ${workout!!.calories} kcal")
                    if (workout!!.notes.isNotBlank()) {
                        Text("Notes: ${workout!!.notes}")
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6))
                ) {
                    Text("Close")
                }
            }
        )
    }
}