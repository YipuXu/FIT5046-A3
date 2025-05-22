package com.example.fitlife.data.database // 或者你 WorkoutDatabase 所在的包

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

import com.example.fitlife.data.dao.WorkoutDao
import com.example.fitlife.data.model.Workout
import com.example.fitlife.data.dao.FitnessEventDao
import com.example.fitlife.data.model.FitnessEvent

@Database(
    entities = [
        Workout::class,
        FitnessEvent::class
    ],
    version = 2,
    exportSchema = false
)
abstract class WorkoutDatabase : RoomDatabase() {

    abstract fun workoutDao(): WorkoutDao
    abstract fun fitnessEventDao(): FitnessEventDao

    companion object {
        @Volatile
        private var INSTANCE: WorkoutDatabase? = null

        fun getDatabase(context: Context): WorkoutDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WorkoutDatabase::class.java,
                    "workout_database"
                )
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}