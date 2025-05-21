package com.example.fitlife.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitlife.data.model.Workout
import com.example.fitlife.data.repository.WorkoutRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class WorkoutViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = WorkoutRepository(application)
    val allWorkouts: Flow<List<Workout>> = repo.allWorkouts

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
