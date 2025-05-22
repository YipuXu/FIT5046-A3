package com.example.fitlife.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.fitlife.data.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Query("SELECT * FROM users WHERE id = :userId")
    fun getUserById(userId: Int): Flow<User?>

    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserByIdSync(userId: Int): User?

    @Query("UPDATE users SET avatarUri = :uri WHERE id = :userId")
    suspend fun updateAvatar(userId: Int, uri: String?)

    @Query("UPDATE users SET name = :name, email = :email WHERE id = :userId")
    suspend fun updateBasicInfo(userId: Int, name: String, email: String)

    @Query("UPDATE users SET height = :height, weight = :weight WHERE id = :userId")
    suspend fun updateHeightWeight(userId: Int, height: String, weight: String)

    @Query("UPDATE users SET fitnessGoal = :fitnessGoal WHERE id = :userId")
    suspend fun updateFitnessGoal(userId: Int, fitnessGoal: String)

    @Query("UPDATE users SET workoutFrequency = :workoutFrequency WHERE id = :userId")
    suspend fun updateWorkoutFrequency(userId: Int, workoutFrequency: String)

    @Query("UPDATE users SET fitnessTags = :fitnessTags WHERE id = :userId")
    suspend fun updateFitnessTags(userId: Int, fitnessTags: String)
} 