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
 * 用于处理Firebase用户相关操作的仓库
 */
class FirebaseUserRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    
    // 用户数据StateFlow
    private val _currentUser = MutableStateFlow<FirebaseUser?>(auth.currentUser)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()
    
    // 用户基本信息
    private val _userProfile = MutableStateFlow<Map<String, Any>?>(null)
    val userProfile: StateFlow<Map<String, Any>?> = _userProfile.asStateFlow()
    
    init {
        // 监听用户登录状态变化
        auth.addAuthStateListener { firebaseAuth ->
            _currentUser.value = firebaseAuth.currentUser
            Log.d("FirebaseUserRepository", "Auth state changed: user=${firebaseAuth.currentUser?.uid}")
            
            // 当用户状态变化时，重置资料流
            if (firebaseAuth.currentUser == null) {
                _userProfile.value = null
            } else {
                // 加载用户资料
                firebaseAuth.currentUser?.uid?.let { uid ->
                    loadUserProfile(uid)
                }
            }
        }
    }
    
    /**
     * 获取当前登录用户的显示名称
     * @return 用户名，如果未登录或无显示名则返回null
     */
    fun getCurrentUserDisplayName(): String? {
        return auth.currentUser?.displayName
    }
    
    /**
     * 获取当前登录用户的邮箱
     * @return 用户邮箱，如果未登录则返回null
     */
    fun getCurrentUserEmail(): String? {
        return auth.currentUser?.email
    }
    
    /**
     * 获取当前登录用户的UID
     * @return 用户UID，如果未登录则返回null
     */
    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }
    
    /**
     * 检查用户是否已登录
     * @return 是否已登录
     */
    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }
    
    /**
     * 刷新当前用户信息
     * 确保获取最新的Firebase用户数据
     */
    suspend fun refreshCurrentUser() {
        try {
            auth.currentUser?.reload()?.await()
            _currentUser.value = auth.currentUser
            
            // 刷新用户资料
            auth.currentUser?.uid?.let { uid ->
                loadUserProfile(uid)
            }
            
            Log.d("FirebaseUserRepository", "User refreshed: ${auth.currentUser?.uid}")
        } catch (e: Exception) {
            Log.e("FirebaseUserRepository", "Error refreshing user: ${e.message}")
        }
    }
    
    /**
     * 加载用户资料
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
     * 更新用户身高体重
     * @param height 身高(cm)
     * @param weight 体重(kg)
     * @return 是否更新成功
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
                // 使用merge操作，只更新指定字段
                firestore.collection("users")
                    .document(userId)
                    .set(userData, SetOptions.merge())
                    .await()
                
                // 刷新本地用户资料
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
     * 获取用户身高体重
     * @return Pair<String, String>? 包含身高和体重的Pair，如果不存在则返回null
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
     * 完全退出登录
     * 清除Firebase认证状态和缓存
     */
    fun signOut(context: Context) {
        try {
            // 清除Firebase认证
            auth.signOut()
            
            // 重置用户流
            _currentUser.value = null
            _userProfile.value = null
            
            // 清除WebView缓存以防止认证令牌持久化
            android.webkit.CookieManager.getInstance().removeAllCookies(null)
            android.webkit.CookieManager.getInstance().flush()
            
            Log.d("FirebaseUserRepository", "User signed out and cache cleared")
        } catch (e: Exception) {
            Log.e("FirebaseUserRepository", "Error signing out: ${e.message}")
        }
    }
} 