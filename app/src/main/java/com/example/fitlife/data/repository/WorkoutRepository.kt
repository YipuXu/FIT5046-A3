package com.example.fitlife.data.repository

import android.app.Application
import com.example.fitlife.data.database.WorkoutDatabase
import com.example.fitlife.data.dao.WorkoutDao
import com.example.fitlife.data.model.Workout
import kotlinx.coroutines.flow.Flow

class WorkoutRepository(application: Application) {
    private val dao: WorkoutDao =
        WorkoutDatabase.getDatabase(application).workoutDao()

    suspend fun getAllWorkouts(): List<Workout> = dao.getAll()
    suspend fun insert(workout: Workout) = dao.insertWorkout(workout)
    suspend fun delete(workout: Workout) = dao.deleteWorkout(workout)
    suspend fun update(workout: Workout) = dao.updateWorkout(workout)
}
