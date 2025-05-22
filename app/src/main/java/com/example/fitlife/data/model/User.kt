package com.example.fitlife.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_table")
data class User(
    @PrimaryKey val id: Int = 0, // Assuming a single user for now, or you can manage IDs
    val name: String? = null,
    val email: String? = null,
    val avatarUri: String? = null
) 