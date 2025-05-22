package com.example.fitlife.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.fitlife.data.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM user_table WHERE id = :id")
    fun getUser(id: Int): Flow<User?>

    @Query("UPDATE user_table SET avatarUri = :avatarUri WHERE id = :id")
    suspend fun updateAvatar(id: Int, avatarUri: String?)

    // You can add other user-related queries here, e.g., for name, email
} 