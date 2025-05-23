package com.example.fitlife.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val firebaseUid: String,
    var name: String = "Xiao Ming",
    var email: String = "xiaoming@example.com",
    var avatarUri: String? = null,
    var height: String = "178",
    var weight: String = "70",
    var fitnessGoal: String = "Muscle Gain & Fat Loss",
    var workoutFrequency: String = "4-5 Times Weekly",
    var fitnessTags: String = "Strength Training,Cardio"
) 