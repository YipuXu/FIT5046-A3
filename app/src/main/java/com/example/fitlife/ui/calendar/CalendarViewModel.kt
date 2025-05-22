package com.example.fitlife.ui.calendar

import android.Manifest
import android.app.Application
import android.content.ContentValues
import android.content.pm.PackageManager
import android.provider.CalendarContract
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitlife.data.model.FitnessEvent
import com.example.fitlife.data.repository.FitnessEventRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.TimeZone

class CalendarViewModel(application: Application) : AndroidViewModel(application) {

    private val fitnessEventRepository: FitnessEventRepository = FitnessEventRepository(application)
    private val app = application

    private fun getStartOfDayMillis(millis: Long): Long {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = millis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }

    private val _selectedDateMillis = MutableStateFlow(getStartOfDayMillis(System.currentTimeMillis()))
    val selectedDateMillis: StateFlow<Long> = _selectedDateMillis.asStateFlow()

    val eventsForSelectedDate: StateFlow<List<FitnessEvent>> = _selectedDateMillis.flatMapLatest { dateMillis ->
        val dayStartMillis = getStartOfDayMillis(dateMillis)
        val nextDayStartMillis = Calendar.getInstance().apply {
            timeInMillis = dayStartMillis
            add(Calendar.DAY_OF_MONTH, 1)
        }.timeInMillis
        fitnessEventRepository.getEventsForDay(dayStartMillis, nextDayStartMillis)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = emptyList()
    )

    fun setSelectedDate(year: Int, month: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance().apply {
            clear()
            set(year, month, dayOfMonth)
        }
        _selectedDateMillis.value = getStartOfDayMillis(calendar.timeInMillis)
    }

    fun addEvent(title: String, startTime: Long, endTime: Long, description: String?) {
        if (title.isBlank()) {
            println("Error: Event title cannot be empty.")
            // TODO
            return
        }
        if (startTime >= endTime) {
            println("Error: Start time must be before end time.")
            // TODO
            return
        }

        viewModelScope.launch {
            val initialEvent = FitnessEvent(
                title = title,
                startTime = startTime,
                endTime = endTime,
                description = description,
                systemCalendarEventId = null
            )
            val localGeneratedId = fitnessEventRepository.insertEvent(initialEvent)

            if (hasCalendarPermission(Manifest.permission.WRITE_CALENDAR) &&
                hasCalendarPermission(Manifest.permission.READ_CALENDAR)) {

                val systemEventId = addEventToSystemCalendarInternal(title, startTime, endTime, description)

                if (systemEventId != null) {
                    val eventToUpdate = FitnessEvent(
                        id = localGeneratedId,
                        title = title,
                        startTime = startTime,
                        endTime = endTime,
                        description = description,
                        systemCalendarEventId = systemEventId
                    )
                    fitnessEventRepository.updateEvent(eventToUpdate)
                    println("Events have been synchronized to the system calendar, system ID: $systemEventId")
                } else {
                    println("Failed to synchronize events to the system calendar or no writable calendar was found. Events are only saved locally.")
                    // TODO
                }
            } else {
                println("Calendar permissions not granted. Events are saved locally only.")
                // TODO
            }
        }
    }

    // --- System calendar synchronization logic ---

    private fun hasCalendarPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            app.applicationContext,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private suspend fun getWritableCalendarId(): Long? = withContext(Dispatchers.IO) {
        if (!hasCalendarPermission(Manifest.permission.READ_CALENDAR)) {
            println("Without permission to read the calendar, unable to get the calendar ID.")
            return@withContext null
        }

        val projection = arrayOf(
            CalendarContract.Calendars._ID,
            CalendarContract.Calendars.IS_PRIMARY
        )

        var selection = "(${CalendarContract.Calendars.IS_PRIMARY} = ?) AND (${CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL} >= ?)"
        var selectionArgs = arrayOf("1", CalendarContract.Calendars.CAL_ACCESS_CONTRIBUTOR.toString())

        val contentResolver = app.contentResolver
        var calendarId: Long? = null

        try {
            contentResolver.query(
                CalendarContract.Calendars.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null
            )?.use { cursor ->
                if (cursor.moveToFirst()) {
                    calendarId = cursor.getLong(cursor.getColumnIndexOrThrow(CalendarContract.Calendars._ID))
                }
            }

            if (calendarId == null) {
                selection = "(${CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL} >= ?)"
                selectionArgs = arrayOf(CalendarContract.Calendars.CAL_ACCESS_CONTRIBUTOR.toString())
                contentResolver.query(
                    CalendarContract.Calendars.CONTENT_URI,
                    projection,
                    selection,
                    selectionArgs,
                    CalendarContract.Calendars._ID + " ASC LIMIT 1"
                )?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        calendarId = cursor.getLong(cursor.getColumnIndexOrThrow(CalendarContract.Calendars._ID))
                    }
                }
            }
        } catch (e: Exception) {
            println("Error querying calendar ID: ${e.message}")
            e.printStackTrace()
            return@withContext null
        }
        return@withContext calendarId
    }


    /**
     * performs the operation of adding the event to the system calendar.
     */
    private suspend fun addEventToSystemCalendarInternal(
        title: String,
        startTime: Long,
        endTime: Long,
        description: String?
    ): Long? {
        val calendarId = getWritableCalendarId()
        if (calendarId == null) {
            println("No writable calendar ID found.")
            return null
        }

        val contentResolver = app.contentResolver
        val values = ContentValues().apply {
            put(CalendarContract.Events.DTSTART, startTime)
            put(CalendarContract.Events.DTEND, endTime)
            put(CalendarContract.Events.TITLE, title)
            put(CalendarContract.Events.DESCRIPTION, description ?: "")
            put(CalendarContract.Events.CALENDAR_ID, calendarId)
            put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)

        }

        return try {
            val uri = contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)
            val systemEventId = uri?.lastPathSegment?.toLongOrNull()
//            if (systemEventId != null) {
//
//            }
            systemEventId
        } catch (e: SecurityException) {
            println("Permission error writing to system calendar: ${e.message}")
            e.printStackTrace()
            null
        } catch (e: Exception) {
            println("An unknown error occurred while writing to the system calendar: ${e.message}")
            e.printStackTrace()
            null
        }
    }
}