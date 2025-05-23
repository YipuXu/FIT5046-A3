package com.example.fitlife.utils

import android.content.Context
import com.example.fitlife.data.model.User
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Database auxiliary tool class, used to handle database related operations
 */
class DatabaseHelper {
    companion object {
        /**
         * Initialize user data
         * If the user does not exist, create a default user
         */
        suspend fun initializeUserData(context: Context) {
            withContext(Dispatchers.IO) {
                try {
                    val application = context.applicationContext as com.example.fitlife.MyApplication
                    val userDao = application.database.userDao()
                    
                    // Get the current Firebase user ID
                    val firebaseUser = FirebaseAuth.getInstance().currentUser
                    val firebaseUid = firebaseUser?.uid
                    
                    if (firebaseUid != null) {
                        // Check if user exists
                        val existingUser = userDao.getUserByFirebaseUidSync(firebaseUid)
                        
                        if (existingUser == null) {
                            // If the user does not exist, create a default user
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