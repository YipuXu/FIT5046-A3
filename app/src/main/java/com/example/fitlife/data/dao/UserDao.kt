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

    @Query("SELECT * FROM users WHERE firebaseUid = :firebaseUid")
    fun getUserByFirebaseUid(firebaseUid: String): Flow<User?>

    @Query("SELECT * FROM users WHERE firebaseUid = :firebaseUid")
    suspend fun getUserByFirebaseUidSync(firebaseUid: String): User?

    @Query("UPDATE users SET avatarUri = :uri WHERE firebaseUid = :firebaseUid")
    suspend fun updateAvatar(firebaseUid: String, uri: String?)

    @Query("UPDATE users SET name = :name, email = :email WHERE firebaseUid = :firebaseUid")
    suspend fun updateBasicInfo(firebaseUid: String, name: String, email: String)

    @Query("UPDATE users SET height = :height, weight = :weight WHERE firebaseUid = :firebaseUid")
    suspend fun updateHeightWeight(firebaseUid: String, height: String, weight: String)

    @Query("UPDATE users SET fitnessGoal = :fitnessGoal WHERE firebaseUid = :firebaseUid")
    suspend fun updateFitnessGoal(firebaseUid: String, fitnessGoal: String)

    @Query("UPDATE users SET workoutFrequency = :workoutFrequency WHERE firebaseUid = :firebaseUid")
    suspend fun updateWorkoutFrequency(firebaseUid: String, workoutFrequency: String)

    @Query("UPDATE users SET fitnessTags = :fitnessTags WHERE firebaseUid = :firebaseUid")
    suspend fun updateFitnessTags(firebaseUid: String, fitnessTags: String)
} 