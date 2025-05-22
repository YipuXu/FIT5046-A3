package com.example.fitlife.data.model
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fitness_events")
data class FitnessEvent(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val startTime: Long,
    val endTime: Long,
    val description: String? = null,
    val systemCalendarEventId: Long? = null
)