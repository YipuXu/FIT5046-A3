package com.example.fitlife.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitlife.model.Exercise
import com.example.fitlife.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.fitlife.model.ExerciseResponse

class ExerciseViewModel : ViewModel() {

    private val _exercises = MutableStateFlow<List<Exercise>>(emptyList())
    val exercises: StateFlow<List<Exercise>> = _exercises

    private var currentOffset = 0
    private val pageSize = 10
    private var totalExercises = Int.MAX_VALUE

    private val _hasNextPage = MutableStateFlow(false)
    val hasNextPage: StateFlow<Boolean> = _hasNextPage

    init {
        loadPage(0)
    }

    private fun loadPage(offset: Int) {
        viewModelScope.launch {
            try {
                val resp = RetrofitClient.api.getExercises(limit = pageSize, offset = offset)
                _exercises.value = resp.data.exercises
                totalExercises = resp.data.totalExercises
                currentOffset = offset
                _hasNextPage.value = currentOffset + pageSize < totalExercises
            } catch (e: Exception) {
                Log.e("EXVM", "loadPage failed", e)
            }
        }
    }

    /** 只保留下一页 */
    fun loadNextPage() {
        val nextOffset = (currentOffset + pageSize)
            .coerceAtMost((totalExercises - 1) / pageSize * pageSize)
        loadPage(nextOffset)
    }
}



