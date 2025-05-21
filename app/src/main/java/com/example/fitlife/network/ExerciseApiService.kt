package com.example.fitlife.network

import com.example.fitlife.model.ExerciseResponse
import retrofit2.http.GET

interface ExerciseApiService {
    @GET("api/v1/exercises")
    suspend fun getExercises(): ExerciseResponse
}

