package com.example.fitlife.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout_table")
data class Workout(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val firebaseUid: String,
    val type: String,
    val duration: Int,
    val calories: Int,
    val intensity: String,
    val notes: String,
    val date: String,
    val time: String
)
