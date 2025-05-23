package com.example.fitlife.ui.train

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
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
import kotlinx.coroutines.launch
import com.example.fitlife.data.repository.FirebaseUserRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllRecentRecordsScreen(
    onBack: () -> Unit = {},
    onAddRecord: () -> Unit = {}
) {
    val context = LocalContext.current
    val workoutDao = (context.applicationContext as MyApplication).database.workoutDao()
    
    // Create Firebase user repository
    val firebaseUserRepository = remember { FirebaseUserRepository() }
    
    // Get current Firebase user information
    val firebaseUser by firebaseUserRepository.currentUser.collectAsState()
    val firebaseUid = firebaseUser?.uid
    
    // Get workout records for this user using user ID
    val allWorkouts by remember(firebaseUid) {
        if (firebaseUid != null) {
            workoutDao.getAllOrderByDateDesc(firebaseUid)
        } else {
            workoutDao.getAllOrderByDateDesc()
        }
    }.collectAsState(initial = emptyList())
    
    val coroutineScope = rememberCoroutineScope()

    // State for the workout detail dialog
    var showDialog by remember { mutableStateOf(false) }
    var selectedWorkout by remember { mutableStateOf<Workout?>(null) }
    
    // Delete confirmation dialog state
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var workoutToDelete by remember { mutableStateOf<Workout?>(null) }

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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF9FAFB))
                .padding(padding)
        ) {
            if (allWorkouts.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No records found.", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(allWorkouts) { workout ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { // Click entire item to view details
                                        selectedWorkout = workout
                                        showDialog = true
                                    }
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Icon
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
                                // Text information
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
                                
                                // Delete button
                                IconButton(
                                    onClick = {
                                        // Set record to delete and show confirmation dialog
                                        workoutToDelete = workout
                                        showDeleteConfirmDialog = true
                                    },
                                    modifier = Modifier.padding(start = 8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete record",
                                        tint = Color(0xFFE53935) // Red
                                    )
                                }
                                
                                // View details arrow
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
    }

    // Delete confirmation dialog
    if (showDeleteConfirmDialog && workoutToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("Delete Record") },
            text = { 
                Text(
                    "Are you sure you want to delete this ${workoutToDelete!!.type} record?\n" +
                    "Date: ${workoutToDelete!!.date}\n" +
                    "Duration: ${workoutToDelete!!.duration} minutes"
                ) 
            },
            confirmButton = {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            // Delete record from Room database
                            workoutToDelete?.let { workout ->
                                workoutDao.deleteWorkout(workout)
                            }
                        }
                        showDeleteConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)) // Red confirm button
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDeleteConfirmDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9E9E9E)) // Gray cancel button
                ) {
                    Text("Cancel")
                }
            }
        )
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