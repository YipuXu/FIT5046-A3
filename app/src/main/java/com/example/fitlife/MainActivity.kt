package com.example.fitlife

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.fitlife.ui.auth.LoginScreen
import com.example.fitlife.ui.map.MapScreen
import com.example.fitlife.ui.theme.FitLifeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FitLifeTheme {
                // 使用状态来控制是否已登录
                val isLoggedIn = remember { mutableStateOf(false) }
                
                if (isLoggedIn.value) {
                    // 已登录，显示地图页面
                    MapScreen(
                        onNavigateBack = { /* 暂时为空 */ },
                        onNavigateToHome = { /* 暂时为空 */ },
                        onNavigateToCalendar = { /* 暂时为空 */ },
                        onNavigateToProfile = { /* 暂时为空 */ }
                    )
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
