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
    
    // The user ID needs to be passed in when obtaining training records
    fun getAllWorkouts(firebaseUid: String, onResult: (List<Workout>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val all = repo.getAllWorkouts(firebaseUid)
            withContext(Dispatchers.Main) {
                onResult(all)
            }
        }
    }
    
    // Add default parameters to make existing code compatible
    suspend fun getAllWorkouts(firebaseUid: String = ""): List<Workout> = repo.getAllWorkouts(firebaseUid)
    
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
