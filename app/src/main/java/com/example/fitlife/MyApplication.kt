package com.example.fitlife

import android.app.Application
import androidx.room.Room
import com.example.fitlife.data.database.WorkoutDatabase
import com.example.fitlife.data.preferences.AccessibilityPreferences

class MyApplication : Application() {
    lateinit var database: WorkoutDatabase
        private set
    
    // 添加辅助功能偏好设置实例
    lateinit var accessibilityPreferences: AccessibilityPreferences
        private set

    override fun onCreate() {
        super.onCreate()
        // 初始化数据库
        database = Room.databaseBuilder(
            applicationContext,
            WorkoutDatabase::class.java,
            "workout_database"
        )
        .fallbackToDestructiveMigration()
        .build()
        
        // 初始化辅助功能偏好设置
        accessibilityPreferences = AccessibilityPreferences(applicationContext)
    }
}
