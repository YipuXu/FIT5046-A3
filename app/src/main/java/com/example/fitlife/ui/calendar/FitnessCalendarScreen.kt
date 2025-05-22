package com.example.fitlife.ui.calendar // Or your UI package

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
    var showAddEventDialog by remember { mutableStateOf(false) }

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

    LaunchedEffect(Unit) { // Runs once when the composable enters the composition
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
                    Text("No fitness plan today!", style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(eventsOnSelectedDate) { event ->
                        EventCard(event = event, onClick = { /* TODO: Implement Edit/Delete event */ })
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }

    if (showAddEventDialog) {
        // Using the AddFitnessEventDialog that calls android.app.TimePickerDialog
        AddFitnessEventDialog(
            initialDateMillis = selectedDateMillis,
            onDismiss = { showAddEventDialog = false },
            onSave = { title, description, startTime, endTime ->
                viewModel.addEvent(title, startTime, endTime, description)
                showAddEventDialog = false
            }
        )
    }
}

// --- CalendarViewComposable (from previous response) ---
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

// --- EventCard (from previous response) ---
@Composable
fun EventCard(event: FitnessEvent, onClick: () -> Unit) {
    val timeFormatter = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(Modifier.padding(16.dp)) {
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
    }
}

// --- AddFitnessEventDialog (using android.app.TimePickerDialog - simplest version from previous response) ---
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
                Text("date: ${sdfDate.format(Date(initialDateMillis))}", style = MaterialTheme.typography.bodyMedium)
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Program Name (eg: Chest)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Notes (optional)") }, modifier = Modifier.fillMaxWidth().heightIn(min = 80.dp, max=120.dp))
                // Start Time
                Text("Start time: ${String.format(Locale.getDefault(), "%02d:%02d", startHour, startMinute)}")
                Button(onClick = { TimePickerDialog(context, { _, h, m -> startHour = h; startMinute = m }, startHour, startMinute, true).show() }, modifier = Modifier.fillMaxWidth()) { Text("Select start time") }
                // End Time
                Text("End time: ${String.format(Locale.getDefault(), "%02d:%02d", endHour, endMinute)}")
                Button(onClick = { TimePickerDialog(context, { _, h, m -> endHour = h; endMinute = m }, endHour, endMinute, true).show() }, modifier = Modifier.fillMaxWidth()) { Text("Select end time") }
                // Action Buttons
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val startCal = Calendar.getInstance().apply { timeInMillis = initialDateMillis; set(Calendar.HOUR_OF_DAY, startHour); set(Calendar.MINUTE, startMinute); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0) }
                            val endCal = Calendar.getInstance().apply { timeInMillis = initialDateMillis; set(Calendar.HOUR_OF_DAY, endHour); set(Calendar.MINUTE, endMinute); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0) }
                            if (endCal.timeInMillis <= startCal.timeInMillis) endCal.timeInMillis = startCal.timeInMillis + 3600000 // add 1 hour if end time is not after start time
                            onSave(title.trim(), description.trim().ifBlank { null }, startCal.timeInMillis, endCal.timeInMillis)
                        },
                        enabled = title.isNotBlank()
                    ) { Text("save") }
                }
            }
        }
    }
}