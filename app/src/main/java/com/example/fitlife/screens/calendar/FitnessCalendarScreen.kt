package com.example.fitlife.ui.calendar

import android.Manifest
import android.app.TimePickerDialog
import android.content.pm.PackageManager
import android.widget.CalendarView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitlife.R
import com.example.fitlife.data.model.FitnessEvent
import com.example.fitlife.ui.components.BottomNavBar
import com.example.fitlife.MainActivity

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FitnessCalendarScreen(
    currentRoute: String,
    onNavigateToHome: () -> Unit,
    onNavigateToCalendar: () -> Unit,
    onNavigateToMap: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToRecordTraining: (String, String, Long) -> Unit = { _, _, _ -> },
    viewModel: CalendarViewModel = viewModel()
) {
    val context = LocalContext.current

    val selectedDateMillis by viewModel.selectedDateMillis.collectAsState()
    val eventsOnSelectedDate by viewModel.eventsForSelectedDate.collectAsState()

    // add
    var showAddEventDialog by remember { mutableStateOf(false) }
    // delete
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var eventToDelete by remember { mutableStateOf<FitnessEvent?>(null) }

    // Permission Request Launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.WRITE_CALENDAR] == true &&
                permissions[Manifest.permission.READ_CALENDAR] == true
        if (granted) {
            println("Calendar permissions granted")
        } else {
            println("Calendar permission denied")
        }
    }

    // Request permissions when a Composable first enters the composition
    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionLauncher.launch(
                arrayOf(Manifest.permission.WRITE_CALENDAR, Manifest.permission.READ_CALENDAR)
            )
        }
    }
    
    // Check if there is a plan ID that needs to be deleted
    val mainActivity = context as? MainActivity
    val eventIdToDelete by mainActivity?.planEventToDeleteId ?: remember { mutableStateOf<Long?>(null) }
    
    // When the event ID changes, perform the delete operation
    LaunchedEffect(eventIdToDelete) {
        if (eventIdToDelete != null) {
            // Find the corresponding event and delete it
            val allEvents = viewModel.eventsForSelectedDate.value
            val eventToDelete = allEvents.find { it.id == eventIdToDelete }
            eventToDelete?.let {
                viewModel.deleteEvent(it)
                // Reset ID
                mainActivity?.planEventToDeleteId?.value = null
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9FAFB))
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        Scaffold(
            topBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.width(32.dp))

                    Text(
                        text = "Fitness Calendar",
                        fontSize = 18.sp, 
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        color = Color(0xFF1F2937)
                    )

                    Box(
                        modifier = Modifier.size(32.dp)
                    ) {
                        // Empty Box, only used to maintain visual balance
                    }
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
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showAddEventDialog = true },
                    containerColor = Color(0xFF3B82F6),
                    shape = CircleShape
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Add a fitness event", tint = Color.White)
                }
            },
            containerColor = Color(0xFFF9FAFB)
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(Modifier.height(8.dp))

                // Calendar view wrapped with card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 0.dp
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        CalendarViewComposable(
                            selectedDateMillis = selectedDateMillis,
                            onDateSelected = { year, month, dayOfMonth ->
                                viewModel.setSelectedDate(year, month, dayOfMonth)
                            }
                        )
                    }
                }

                val dateFormatter = remember { SimpleDateFormat("yyyy-MM-dd (EEEE)", Locale.getDefault()) }
                Text(
                    "Selected date: ${dateFormatter.format(Date(selectedDateMillis))}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = Color(0xFF1F2937)
                )

                Spacer(Modifier.height(12.dp))

                // Event List Title
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Today's Fitness Plans",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1F2937)
                    )
                }

                if (eventsOnSelectedDate.isEmpty()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 0.dp
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "No fitness plan today.",
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                color = Color(0xFF6B7280)
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(eventsOnSelectedDate) { event ->
                            EventCard(
                                event = event,
                                onClick = {},
                                onDeleteClick = {
                                    eventToDelete = event
                                    showDeleteConfirmDialog = true
                                },
                                onMarkAsDone = {
                                    val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(event.startTime))
                                    onNavigateToRecordTraining(event.title, dateStr, event.id)
                                }
                            )
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }

    // Add conditional rendering of event dialog
    if (showAddEventDialog) {
        AddFitnessEventDialog(
            initialDateMillis = selectedDateMillis,
            onDismiss = { showAddEventDialog = false },
            onSave = { title, description, startTime, endTime ->
                viewModel.addEvent(title, startTime, endTime, description)
                showAddEventDialog = false
            }
        )
    }

    // Conditional rendering of delete confirmation dialog
    if (showDeleteConfirmDialog && eventToDelete != null) {
        AlertDialog(
            onDismissRequest = {
                showDeleteConfirmDialog = false
                eventToDelete = null
            },
            title = { Text("Confirm Delete") },
            text = { Text("Are you sure you want to delete '${eventToDelete!!.title}' Is this the plan? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteEvent(eventToDelete!!)
                        showDeleteConfirmDialog = false
                        eventToDelete = null
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmDialog = false
                        eventToDelete = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}


@Composable
fun CalendarViewComposable(
    selectedDateMillis: Long,
    onDateSelected: (year: Int, month: Int, dayOfMonth: Int) -> Unit
) {
    AndroidView(
        factory = { ctx ->
            CalendarView(ctx).apply {
                setOnDateChangeListener { _, year, month, dayOfMonth ->
                    onDateSelected(year, month, dayOfMonth)
                }
            }
        },
        update = { view ->
            val calendarViewDate = Calendar.getInstance().apply { timeInMillis = view.date }
            val selectedCalendarDate = Calendar.getInstance().apply { timeInMillis = selectedDateMillis }
            if (calendarViewDate.get(Calendar.YEAR) != selectedCalendarDate.get(Calendar.YEAR) ||
                calendarViewDate.get(Calendar.DAY_OF_YEAR) != selectedCalendarDate.get(Calendar.DAY_OF_YEAR)) {
                view.date = selectedDateMillis
            }
        },
        modifier = Modifier.fillMaxWidth().wrapContentHeight()
    )
}

@Composable
fun EventCard(
    event: FitnessEvent,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onMarkAsDone: () -> Unit
) {
    val timeFormatter = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFE6F0FF)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = com.example.fitlife.R.drawable.ic_workout),
                    contentDescription = null,
                    tint = Color(0xFF3B82F6),
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Column(
                Modifier
                    .weight(1f)
                    .padding(start = 12.dp)
            ) {
                Text(
                    event.title, 
                    style = MaterialTheme.typography.titleSmall,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF1F2937)
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Time: ${timeFormatter.format(Date(event.startTime))} - ${timeFormatter.format(Date(event.endTime))}",
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 14.sp,
                    color = Color(0xFF6B7280)
                )
                if (!event.description.isNullOrBlank()) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Remark: ${event.description}", 
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 14.sp,
                        color = Color(0xFF6B7280)
                    )
                }
            }
            IconButton(onClick = onMarkAsDone) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = "Mark as done",
                    tint = Color(0xFF3B82F6)
                )
            }
            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Delete a plan",
                    tint = Color(0xFFEF4444)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFitnessEventDialog(
    initialDateMillis: Long,
    onDismiss: () -> Unit,
    onSave: (title: String, description: String?, startTimeMillis: Long, endTimeMillis: Long) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    val context = LocalContext.current
    val initialCalendar = remember { Calendar.getInstance().apply { timeInMillis = initialDateMillis } }

    var startHour by remember { mutableStateOf(initialCalendar.get(Calendar.HOUR_OF_DAY)) }
    var startMinute by remember { mutableStateOf(initialCalendar.get(Calendar.MINUTE)) }
    var endHour by remember { mutableStateOf((initialCalendar.get(Calendar.HOUR_OF_DAY) + 1) % 24) }
    var endMinute by remember { mutableStateOf(initialCalendar.get(Calendar.MINUTE)) }
    val sdfDate = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    "Add a fitness plan", 
                    style = MaterialTheme.typography.titleLarge,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937)
                )
                Text(
                    "Date: ${sdfDate.format(Date(initialDateMillis))}", 
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF6B7280)
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Program Name (eg: Chest)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0xFF3B82F6),
                        unfocusedBorderColor = Color(0xFFD1D5DB)
                    )
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Notes (optional)") },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 80.dp, max=120.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0xFF3B82F6),
                        unfocusedBorderColor = Color(0xFFD1D5DB)
                    )
                )

                // start time
                Text(
                    "Start time: ${String.format(Locale.getDefault(), "%02d:%02d", startHour, startMinute)}",
                    fontSize = 14.sp,
                    color = Color(0xFF6B7280)
                )
                Button(
                    onClick = {
                        TimePickerDialog(
                            context,
                            { _, selectedHour, selectedMinute ->
                                startHour = selectedHour
                                startMinute = selectedMinute
                            },
                            startHour,
                            startMinute,
                            true // 24-hour system
                        ).show()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF3B82F6)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) { Text("Select start time") }

                // End time
                Text(
                    "End time: ${String.format(Locale.getDefault(), "%02d:%02d", endHour, endMinute)}",
                    fontSize = 14.sp,
                    color = Color(0xFF6B7280)
                )
                Button(
                    onClick = {
                        TimePickerDialog(
                            context,
                            { _, selectedHour, selectedMinute ->
                                endHour = selectedHour
                                endMinute = selectedMinute
                            },
                            endHour,
                            endMinute,
                            true
                        ).show()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF3B82F6)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) { Text("Select end time") }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onDismiss,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color(0xFF6B7280)
                        )
                    ) { Text("Cancel") }
                    
                    Spacer(Modifier.width(8.dp))
                    
                    Button(
                        onClick = {
                            val startCal = Calendar.getInstance().apply {
                                timeInMillis = initialDateMillis
                                set(Calendar.HOUR_OF_DAY, startHour)
                                set(Calendar.MINUTE, startMinute)
                                set(Calendar.SECOND, 0)
                                set(Calendar.MILLISECOND, 0)
                            }
                            val endCal = Calendar.getInstance().apply {
                                timeInMillis = initialDateMillis
                                set(Calendar.HOUR_OF_DAY, endHour)
                                set(Calendar.MINUTE, endMinute)
                                set(Calendar.SECOND, 0)
                                set(Calendar.MILLISECOND, 0)
                            }
                            // If the end time is not later than the start time, set the end time to 1 hour after the start time
                            if (endCal.timeInMillis <= startCal.timeInMillis) {
                                endCal.timeInMillis = startCal.timeInMillis + (60 * 60 * 1000)
                            }
                            onSave(
                                title.trim(),
                                description.trim().ifBlank { null },
                                startCal.timeInMillis,
                                endCal.timeInMillis
                            )
                        },
                        enabled = title.isNotBlank(), // Can only be saved if the title is not empty
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF3B82F6),
                            disabledContainerColor = Color(0xFFBFDBFE)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) { Text("Save") }
                }
            }
        }
    }
}