package com.example.fitlife.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitlife.data.model.Workout
import com.example.fitlife.data.repository.WorkoutRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WorkoutViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = WorkoutRepository(application)
    fun getAllWorkouts(onResult: (List<Workout>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val all = repo.getAllWorkouts()
            withContext(Dispatchers.Main) {
                onResult(all)
            }
        }
    }
    suspend fun getAllWorkouts(): List<Workout> = repo.getAllWorkouts()
    fun insertWorkout(workout: Workout) = viewModelScope.launch(Dispatchers.IO) {
        repo.insert(workout)
    }

    fun updateWorkout(workout: Workout) = viewModelScope.launch(Dispatchers.IO) {
        repo.update(workout)
    }

    fun deleteWorkout(workout: Workout) = viewModelScope.launch(Dispatchers.IO) {
        repo.delete(workout)
    }
}
