package com.example.fitlife.data.dao
import com.example.fitlife.data.model.Workout  // ✅ 正确导入实体类
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {
    @Query("SELECT * FROM workout_table")
    suspend fun getAll(): List<Workout>

    @Insert
    suspend fun insertWorkout(workout: Workout)

    @Update
    suspend fun updateWorkout(workout: Workout)

    @Delete
    suspend fun deleteWorkout(workout: Workout)
}
