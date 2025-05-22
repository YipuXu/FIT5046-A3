package com.example.fitlife.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 用于处理Firebase用户相关操作的仓库
 */
class FirebaseUserRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    
    // 用户数据StateFlow
    private val _currentUser = MutableStateFlow<FirebaseUser?>(auth.currentUser)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()
    
    init {
        // 监听用户登录状态变化
        auth.addAuthStateListener { firebaseAuth ->
            _currentUser.value = firebaseAuth.currentUser
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
} 