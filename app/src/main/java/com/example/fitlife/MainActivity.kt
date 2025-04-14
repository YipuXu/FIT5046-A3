package com.example.fitlife

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.fitlife.ui.auth.LoginScreen
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
                val currentScreen = remember { mutableStateOf("map") } // map, profile等
                
                if (isLoggedIn.value) {
                    // 已登录，根据当前页面显示内容
                    when (currentScreen.value) {
                        "map" -> MapScreen(
                            onNavigateBack = { /* 暂时为空 */ },
                            onNavigateToHome = { /* 暂时为空 */ },
                            onNavigateToCalendar = { /* 暂时为空 */ },
                            onNavigateToProfile = { currentScreen.value = "profile" }
                        )
                        "profile" -> ProfileScreen(
                            onBackClick = { currentScreen.value = "map" },
                            onViewAllAchievements = { /* 处理查看所有成就 */ },
                            onViewAllHistory = { /* 处理查看所有历史记录 */ }
                        )
                    }
                } else {
                    // 未登录，显示登录页面
                    LoginScreen(
                        onLoginSuccess = { isLoggedIn.value = true },
                        onRegisterClick = { /* 处理注册点击 */ }
                    )
                }
            }
        }
    }
}
