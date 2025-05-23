package com.example.fitlife.data.repository

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.google.firebase.firestore.SetOptions

/**
 * A repository for handling Firebase user-related operations
 */
class FirebaseUserRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    
    // User Data StateFlow
    private val _currentUser = MutableStateFlow<FirebaseUser?>(auth.currentUser)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()
    
    // User basic information
    private val _userProfile = MutableStateFlow<Map<String, Any>?>(null)
    val userProfile: StateFlow<Map<String, Any>?> = _userProfile.asStateFlow()
    
    init {
        // Monitor user login status changes
        auth.addAuthStateListener { firebaseAuth ->
            _currentUser.value = firebaseAuth.currentUser
            Log.d("FirebaseUserRepository", "Auth state changed: user=${firebaseAuth.currentUser?.uid}")
            
            // When the user status changes, reset the data flow
            if (firebaseAuth.currentUser == null) {
                _userProfile.value = null
            } else {
                // Loading user profile
                firebaseAuth.currentUser?.uid?.let { uid ->
                    loadUserProfile(uid)
                }
            }
        }
    }
    
    /**
     * Get the display name of the currently logged in user
     * @return Username, or null if not logged in or no display name
     */
    fun getCurrentUserDisplayName(): String? {
        return auth.currentUser?.displayName
    }
    
    /**
     * Get the email address of the currently logged in user
     * @return the user's email address, or null if not logged in
     */
    fun getCurrentUserEmail(): String? {
        return auth.currentUser?.email
    }
    
    /**
     * Get the UID of the currently logged in user
     * @return User UID, or null if not logged in
     */
    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }
    
    /**
     * Check if the user is logged in
     * @return whether the user is logged in
     */
    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }
    
    /**
     * Refresh current user information
     * Ensure to obtain the latest Firebase user data
     */
    suspend fun refreshCurrentUser() {
        try {
            auth.currentUser?.reload()?.await()
            _currentUser.value = auth.currentUser
            
            // Refresh User Profile
            auth.currentUser?.uid?.let { uid ->
                loadUserProfile(uid)
            }
            
            Log.d("FirebaseUserRepository", "User refreshed: ${auth.currentUser?.uid}")
        } catch (e: Exception) {
            Log.e("FirebaseUserRepository", "Error refreshing user: ${e.message}")
        }
    }
    
    /**
     * Loading user profile
     */
    private fun loadUserProfile(uid: String) {
        firestore.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    _userProfile.value = document.data
                    Log.d("FirebaseUserRepository", "User profile loaded: ${document.data}")
                } else {
                    Log.d("FirebaseUserRepository", "User profile not found, creating empty profile")
                    _userProfile.value = mapOf()
                }
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseUserRepository", "Error loading user profile", e)
                _userProfile.value = null
            }
    }
    
    /**
     * Update user height and weight
     * @param height height (cm)
     * @param weight weight (kg)
     * @return whether the update was successful
     */
    suspend fun updateHeightWeight(height: String, weight: String): Boolean {
        val userId = getCurrentUserId() ?: return false
        
        return try {
            val userData = hashMapOf(
                "height" to height,
                "weight" to weight,
                "lastUpdated" to com.google.firebase.Timestamp.now()
            )
            
            withContext(Dispatchers.IO) {
                // Use the merge operation to update only the specified fields
                firestore.collection("users")
                    .document(userId)
                    .set(userData, SetOptions.merge())
                    .await()
                
                // Refresh local user profile
                loadUserProfile(userId)
            }
            
            Log.d("FirebaseUserRepository", "Height/weight updated for user $userId")
            true
        } catch (e: Exception) {
            Log.e("FirebaseUserRepository", "Error updating height/weight", e)
            false
        }
    }
    
    /**
     * Get the user's height and weight
     * @return Pair<String, String>? A Pair containing height and weight, or null if it does not exist
     */
    suspend fun getHeightWeight(): Pair<String, String>? {
        val userId = getCurrentUserId() ?: return null
        
        return try {
            val document = withContext(Dispatchers.IO) {
                firestore.collection("users")
                    .document(userId)
                    .get()
                    .await()
            }
            
            if (document != null && document.exists()) {
                val height = document.getString("height") ?: "178"
                val weight = document.getString("weight") ?: "70"
                Pair(height, weight)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("FirebaseUserRepository", "Error getting height/weight", e)
            null
        }
    }
    
    /**
     * Completely log out
     * Clear Firebase authentication status and cache
     */
    fun signOut(context: Context) {
        try {
            // Clear Firebase Authentication
            auth.signOut()
            
            // Reset User Flow
            _currentUser.value = null
            _userProfile.value = null
            
            // Clear WebView cache to prevent authentication token persistence
            android.webkit.CookieManager.getInstance().removeAllCookies(null)
            android.webkit.CookieManager.getInstance().flush()
            
            Log.d("FirebaseUserRepository", "User signed out and cache cleared")
        } catch (e: Exception) {
            Log.e("FirebaseUserRepository", "Error signing out: ${e.message}")
        }
    }
} 