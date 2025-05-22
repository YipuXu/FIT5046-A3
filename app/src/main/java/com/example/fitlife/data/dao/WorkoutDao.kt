package com.example.fitlife.data.dao
import com.example.fitlife.data.model.Workout  // ✅ 正确导入实体类
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {
    @Query("SELECT * FROM workout_table")
    fun getAll(): Flow<List<Workout>>

    @Query("SELECT * FROM workout_table ORDER BY date DESC, time DESC")
    fun getAllOrderByDateDesc(): Flow<List<Workout>>

    @Query("SELECT * FROM workout_table ORDER BY date DESC, time DESC LIMIT 2")
    fun getLatestTwoWorkouts(): Flow<List<Workout>>

    @Query("SELECT COUNT(DISTINCT date) FROM workout_table")
    fun getUniqueWorkoutDaysCount(): Flow<Int>

    @Query("SELECT DISTINCT date FROM workout_table ORDER BY date DESC")
    fun getAllWorkoutDatesDesc(): Flow<List<String>>

    @Insert
    suspend fun insertWorkout(workout: Workout)

    @Update
    suspend fun updateWorkout(workout: Workout)

    @Delete
    suspend fun deleteWorkout(workout: Workout)
}
