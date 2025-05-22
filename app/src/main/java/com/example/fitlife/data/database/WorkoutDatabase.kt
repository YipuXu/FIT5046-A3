package com.example.fitlife.data.database

import com.example.fitlife.data.model.Workout  // ✅ 正确导入实体类
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.fitlife.data.dao.WorkoutDao
import com.example.fitlife.data.dao.UserDao
import com.example.fitlife.data.model.User
// import com.example.fitlife.data.model.User // 移除导入 User 实体

@Database(entities = [Workout::class, User::class], version = 2, exportSchema = false)
abstract class WorkoutDatabase : RoomDatabase() {
    abstract fun workoutDao(): WorkoutDao
    abstract fun userDao(): UserDao
    // abstract fun userDao(): UserDao // 移除 userDao 方法

    companion object {
        @Volatile
        private var INSTANCE: WorkoutDatabase? = null

        fun getDatabase(context: Context): WorkoutDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WorkoutDatabase::class.java,
                    "workout_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
