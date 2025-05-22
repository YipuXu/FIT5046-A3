package com.example.fitlife.network

import com.example.fitlife.model.ExerciseResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ExerciseApiService {

    @GET("api/v1/exercises")
    suspend fun getExercises(
        @Query("limit") limit: Int = 10,
        @Query("offset") offset: Int = 0
    ): ExerciseResponse
}
