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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.SentimentNeutral
import androidx.compose.material.icons.outlined.SentimentSatisfiedAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.fitlife.MyApplication
import com.example.fitlife.data.model.Workout
import com.example.fitlife.ui.components.BottomNavBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import com.example.fitlife.data.repository.FirebaseUserRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordTrainingScreen(
    currentRoute: String = "profile",
    onNavigateToHome: () -> Unit = {},
    onNavigateToCalendar: () -> Unit = {},
    onNavigateToMap: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    planTitle: String = "",
    planDate: String = "",
    onMarkPlanDone: () -> Unit = {}
) {
    val context = LocalContext.current

    var trainingType by remember { mutableStateOf(if (planTitle.isNotBlank()) planTitle else "Strength Training") }
    val trainingTypes = listOf(
        "Strength Training",
        "Cardio",
        "HIIT",
        "Yoga",
        "Pilates",
        "Functional Training",
        "Outdoor Activities",
        "Group Classes",
        "Running",
        "Swimming",
        "Boxing",
        "Dancing"
    )

    var duration by remember { mutableStateOf(30) }
    var calories by remember { mutableStateOf("") }
    var intensity by remember { mutableStateOf("Challenging") }
    var notes by remember { mutableStateOf(TextFieldValue("")) }
    var selectedDate by remember { mutableStateOf(if (planDate.isNotBlank()) planDate else "Select Date") }
    var selectedTime by remember { mutableStateOf("Select Time") }

    var isFromCalendarPlan by remember { mutableStateOf(planTitle.isNotBlank() && planDate.isNotBlank()) }

    val dateFormatter = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    val timeFormatter = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    var showDialog by remember { mutableStateOf(false) }
    var caloriesBurned by remember { mutableStateOf(0) }

    // 创建Firebase用户仓库以获取当前用户ID
    val firebaseUserRepository = remember { FirebaseUserRepository() }
    
    // 获取当前用户ID
    val firebaseUser by firebaseUserRepository.currentUser.collectAsState()
    val firebaseUid = firebaseUser?.uid

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .windowInsetsPadding(WindowInsets.safeDrawing)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF3F4F6))
                        .clickable {
                            onNavigateToProfile()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        modifier = Modifier.size(20.dp),
                        tint = Color(0xFF6B7280)
                    )
                }
                Text(
                    text = if (isFromCalendarPlan) "Complete Plan: $planTitle" else "Let's Burn Calories",
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    textAlign = TextAlign.Center,
                    color = Color(0xFF1F2937)
                )
                Spacer(modifier = Modifier.size(36.dp))
            }
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
            if (isFromCalendarPlan) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFE6F0FF)
                    ),
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Completing plan: $planTitle on $planDate",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF1F2937)
                        )
                    }
                }
            }
            
            Card(
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                ) {
                    var expanded by remember { mutableStateOf(false) }

                    Text(
                        text = "Workout Type",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = trainingType,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                            },
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
                }
            }

            Card(
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Button(
                        onClick = {
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
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = MaterialTheme.shapes.medium,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2563EB),
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = if (selectedDate == "Select Date") "Select Date" else "Date: $selectedDate",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

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
                    }, modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                        shape = MaterialTheme.shapes.medium,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2563EB),
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = if (selectedTime == "Select Time") "Select Time" else "Time: $selectedTime",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }


            Card(
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ){
                Column (
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Workout Duration (minutes)",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        listOf(30, 60, 90, 120, 150).forEach {
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
            }

            Card(
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ){
                Column (
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Workout Intensity",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
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
            }
            Card(
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                )
                {
                    Text(
                        text ="Notes",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Notes") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                    )
                }
            }

            Button(
                onClick = {
                    if (selectedDate == "Select Date" || selectedTime == "Select Time") {
                        Toast.makeText(context, "Please select date and time", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    caloriesBurned = estimateCalories(trainingType, duration, intensity)
                    
                    if (isFromCalendarPlan) {
                        showDialog = true
                    } else {
                        showDialog = true
                    }
                }
                ,modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2563EB),
                    contentColor = Color.White
                )
            )
            {
                Text(if (isFromCalendarPlan) "Complete Plan" else "Save Record")
            }
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text("Confirm Workout") },
                    text = { Text("You will burn $caloriesBurned calories.") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showDialog = false
                                val dao = (context.applicationContext as MyApplication).database.workoutDao()
                                
                                // 获取当前用户ID，如果未登录则使用空字符串（应该不会发生这种情况）
                                val currentUid = firebaseUid ?: ""
                                
                                val workout = Workout(
                                    type = trainingType,
                                    duration = duration,
                                    calories = caloriesBurned,
                                    intensity = intensity,
                                    notes = notes.text,
                                    date = selectedDate,
                                    time = selectedTime,
                                    firebaseUid = currentUid // 添加用户ID到训练记录
                                )

                                CoroutineScope(Dispatchers.IO).launch {
                                    try {
                                        dao.insertWorkout(workout)
                                        withContext(Dispatchers.Main) {
                                            Toast.makeText(context, "Workout saved", Toast.LENGTH_SHORT).show()
                                            
                                            if (isFromCalendarPlan) {
                                                onMarkPlanDone()
                                                Toast.makeText(context, "Plan marked as completed!", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    } catch (e: Exception) {
                                        withContext(Dispatchers.Main) {
                                            Toast.makeText(context, "Failed to insert: ${e.message}", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            }
                        ) {
                            Text("Confirm")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    }
}
fun estimateCalories(type: String, duration: Int, intensity: String): Int {
    val baseRate = when (type) {
        "Strength Training" -> 6.0
        "Cardio" -> 8.0
        "Yoga" -> 3.0
        "Swimming" -> 9.0
        "HIIT" -> 10.0
        "Pilates" -> 4.5
        "Functional Training" -> 7.0
        "Outdoor Activities" -> 5.5
        "Group Classes" -> 6.5
        "Running" -> 11.0
        "Boxing" -> 10.0
        "Dancing" -> 6.5
        else -> 5.0
    }

    val intensityFactor = when (intensity) {
        "Easy" -> 0.8
        "Moderate" -> 1.0
        "Challenging" -> 1.2
        "Hard" -> 1.5
        else -> 1.0
    }

    return (baseRate * duration * intensityFactor).toInt()
}

