package com.example.fitlife.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitlife.model.Exercise
import com.example.fitlife.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ExerciseViewModel : ViewModel() {
    private val _exercises = MutableStateFlow<List<Exercise>>(emptyList())
    val exercises: StateFlow<List<Exercise>> = _exercises

    init {
        fetchExercises()
    }

    private fun fetchExercises() {
        viewModelScope.launch {
            try {
                // 发起网络请求
                val response = RetrofitClient.api.getExercises()

                // 打印总条数
                Log.d("EXVM", "Exercises count: ${response.data.exercises.size}")

                // 打印第一条的 gifUrl
                response.data.exercises.firstOrNull()?.let { first ->
                    Log.d("EXVM", "First image URL: ${first.gifUrl}")
                }

                // 更新 UI 数据
                _exercises.value = response.data.exercises
            } catch (e: Exception) {
                Log.e("EXVM", "请求失败", e)
            }
        }
    }
}
