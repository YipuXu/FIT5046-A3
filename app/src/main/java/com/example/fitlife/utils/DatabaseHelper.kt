package com.example.fitlife.utils

import android.content.Context
import com.example.fitlife.data.model.User
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 数据库辅助工具类，用于处理数据库相关操作
 */
class DatabaseHelper {
    companion object {
        /**
         * 初始化用户数据
         * 如果用户不存在，则创建默认用户
         */
        suspend fun initializeUserData(context: Context) {
            withContext(Dispatchers.IO) {
                try {
                    val application = context.applicationContext as com.example.fitlife.MyApplication
                    val userDao = application.database.userDao()
                    
                    // 获取当前Firebase用户ID
                    val firebaseUser = FirebaseAuth.getInstance().currentUser
                    val firebaseUid = firebaseUser?.uid
                    
                    if (firebaseUid != null) {
                        // 检查是否存在用户
                        val existingUser = userDao.getUserByFirebaseUidSync(firebaseUid)
                        
                        if (existingUser == null) {
                            // 如果不存在用户，创建默认用户
                            val defaultUser = User(
                                firebaseUid = firebaseUid,
                                name = firebaseUser.displayName ?: "User",
                                email = firebaseUser.email ?: "user@example.com",
                                height = "178",
                                weight = "70",
                                fitnessGoal = "Muscle Gain & Fat Loss",
                                workoutFrequency = "4-5 Times Weekly",
                                fitnessTags = "Strength Training,Cardio"
                            )
                            userDao.insertUser(defaultUser)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
} 