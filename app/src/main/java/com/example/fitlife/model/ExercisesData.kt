package com.example.fitlife.model

data class ExercisesData(
    val previousPage: String?,
    val nextPage: String?,
    val totalPages: Int,
    val totalExercises: Int,
    val exercises: List<Exercise>
)