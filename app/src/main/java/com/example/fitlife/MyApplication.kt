package com.example.fitlife

import android.app.Application
import androidx.room.Room
import com.example.fitlife.data.database.WorkoutDatabase

class MyApplication : Application() {
    lateinit var database: WorkoutDatabase
        private set

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(
            applicationContext,
            WorkoutDatabase::class.java,
            "workout_database"
        ).fallbackToDestructiveMigration().build()
    }
}
