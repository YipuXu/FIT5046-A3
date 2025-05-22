package com.example.fitlife.ui.calendar

import android.Manifest
import android.app.TimePickerDialog
import android.content.pm.PackageManager
import android.widget.CalendarView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitlife.data.model.FitnessEvent
import com.example.fitlife.ui.components.BottomNavBar

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FitnessCalendarScreen(
    currentRoute: String,
    onNavigateToHome: () -> Unit,
    onNavigateToCalendar: () -> Unit,
    onNavigateToMap: () -> Unit,
    onNavigateToProfile: () -> Unit,
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

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Fitness Calendar") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
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
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddEventDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add a fitness event", tint = MaterialTheme.colorScheme.onPrimary)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(16.dp))

            CalendarViewComposable(
                selectedDateMillis = selectedDateMillis,
                onDateSelected = { year, month, dayOfMonth ->
                    viewModel.setSelectedDate(year, month, dayOfMonth)
                }
            )

            Spacer(Modifier.height(16.dp))

            val dateFormatter = remember { SimpleDateFormat("yyyy-MM-dd (EEEE)", Locale.getDefault()) }
            Text(
                "Selected date: ${dateFormatter.format(Date(selectedDateMillis))}",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(8.dp))

            if (eventsOnSelectedDate.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No fitness plan today.！", style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
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
                            }
                        )
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
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
    onDeleteClick: () -> Unit
) {
    val timeFormatter = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 16.dp, end = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(event.title, style = MaterialTheme.typography.titleSmall)
                Spacer(Modifier.height(4.dp))
                Text(
                    "Time: ${timeFormatter.format(Date(event.startTime))} - ${timeFormatter.format(Date(event.endTime))}",
                    style = MaterialTheme.typography.bodySmall
                )
                if (!event.description.isNullOrBlank()) {
                    Spacer(Modifier.height(4.dp))
                    Text("Remark: ${event.description}", style = MaterialTheme.typography.bodySmall)
                }
            }
            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Delete a plan",
                    tint = MaterialTheme.colorScheme.error
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
            shape = MaterialTheme.shapes.large
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Add a fitness plan", style = MaterialTheme.typography.titleLarge)
                Text("Date: ${sdfDate.format(Date(initialDateMillis))}", style = MaterialTheme.typography.bodyMedium)

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Program Name (eg: Chest)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Notes (optional)") },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 80.dp, max=120.dp)
                )

                // start time
                Text("Start time: ${String.format(Locale.getDefault(), "%02d:%02d", startHour, startMinute)}")
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
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Select start time") }

                // End time
                Text("End time: ${String.format(Locale.getDefault(), "%02d:%02d", endHour, endMinute)}")
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
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Select end time") }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
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
                        enabled = title.isNotBlank() // Can only be saved if the title is not empty
                    ) { Text("save") }
                }
            }
        }
    }
}