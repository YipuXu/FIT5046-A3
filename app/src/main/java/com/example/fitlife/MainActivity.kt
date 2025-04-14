package com.example.fitlife

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.fitlife.ui.auth.LoginScreen
import com.example.fitlife.ui.auth.RegisterScreen
import com.example.fitlife.ui.map.MapScreen
import com.example.fitlife.ui.profile.ProfileScreen
import com.example.fitlife.ui.theme.FitLifeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FitLifeTheme {
                // 使用状态来控制是否已登录和当前页面
                val isLoggedIn = remember { mutableStateOf(false) }
                val currentScreen = remember { mutableStateOf("login") } // login, register, map, profile
                
                when {
                    isLoggedIn.value -> {
                        when (currentScreen.value) {
                            "map" -> {
                                // 显示地图页面
                                MapScreen(
                                    onNavigateBack = { isLoggedIn.value = false },
                                    onNavigateToHome = { /* 暂时为空 */ },
                                    onNavigateToCalendar = { /* 暂时为空 */ },
                                    onNavigateToProfile = { currentScreen.value = "profile" }
                                )
                            }
                            "profile" -> {
                                // 显示个人资料页面
                                ProfileScreen(
                                    onBackClick = { currentScreen.value = "map" },
                                    onViewAllAchievements = { /* 暂时为空 */ },
                                    onViewAllHistory = { /* 暂时为空 */ }
                                )
                            }
                            else -> {
                                // 默认显示地图页面
                                currentScreen.value = "map"
                            }
                        }
                    }
                    currentScreen.value == "login" -> {
                        // 未登录，显示登录页面
                        LoginScreen(
                            onLoginSuccess = {
                                isLoggedIn.value = true
                                currentScreen.value = "map"
                            },
                            onRegisterClick = { currentScreen.value = "register" }
                        )
                    }
                    currentScreen.value == "register" -> {
                        // 显示注册页面
                        RegisterScreen(
                            onRegisterSuccess = {
                                isLoggedIn.value = true
                                currentScreen.value = "map"
                            },
                            onNavigateToLogin = { currentScreen.value = "login" }
                        )
                    }
                }
            }
        }
    }
}
