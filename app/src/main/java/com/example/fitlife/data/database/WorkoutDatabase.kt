package com.example.fitlife.data.database

import com.example.fitlife.data.model.Workout  // ✅ 正确导入实体类
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.fitlife.data.dao.WorkoutDao
import com.example.fitlife.data.dao.UserDao // 导入 UserDao
import com.example.fitlife.data.model.User // 导入 User 实体

@Database(entities = [Workout::class, User::class], version = 1, exportSchema = false) // 添加 User 实体
abstract class WorkoutDatabase : RoomDatabase() {
    abstract fun workoutDao(): WorkoutDao
    abstract fun userDao(): UserDao // 添加 userDao 方法

    companion object {
        @Volatile
        private var INSTANCE: WorkoutDatabase? = null

        fun getDatabase(context: Context): WorkoutDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WorkoutDatabase::class.java,
                    "workout_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
