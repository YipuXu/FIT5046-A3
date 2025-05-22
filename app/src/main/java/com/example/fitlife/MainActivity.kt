package com.example.fitlife

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.fitlife.ui.about.AboutUsScreen
import com.example.fitlife.ui.auth.LoginScreen
import com.example.fitlife.ui.auth.RegisterScreen
import com.example.fitlife.ui.calendar.FitnessCalendarScreen
import com.example.fitlife.ui.map.MapScreen
import com.example.fitlife.ui.profile.ProfileEditScreen
import com.example.fitlife.ui.profile.ProfileScreen
import com.example.fitlife.ui.profile.SettingsScreen
import com.example.fitlife.ui.theme.FitLifeTheme
import com.example.fitlife.ui.help.HelpFeedbackScreen
import com.example.fitlife.ui.profile.ChangePasswordScreen
import com.example.fitlife.ui.coach.AICoachScreen
import com.example.fitlife.ui.home.HomeScreen
import com.example.fitlife.ui.policy.PrivacyPolicyScreen
import com.example.fitlife.ui.policy.TermsOfServiceScreen
import com.example.fitlife.ui.profile.AccessibilityScreen
import com.example.fitlife.ui.train.RecordTrainingScreen



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FitLifeTheme {
                // Use state to control login status and current page
                val isLoggedIn = remember { mutableStateOf(false) }
                val currentScreen = remember { mutableStateOf("login") } // login, register, map, profile, profileEdit, settings, about, helpFeedback, changePassword, aiCoach, privacyPolicy, termsOfService, accessibility
                
                // Add a state to store selected fitness tags
                val selectedFitnessTags = remember { mutableStateOf(listOf("Strength Training", "Cardio")) }
                
                when {
                    isLoggedIn.value -> {
                        when (currentScreen.value) {
                            "home" -> {
                                HomeScreen(
                                    currentRoute = currentScreen.value,
                                    onNavigateToHome = { currentScreen.value = "home" },
                                    onNavigateToCalendar = { currentScreen.value = "calendar" },
                                    onNavigateToMap = { currentScreen.value = "map" },
                                    onNavigateToProfile = { currentScreen.value = "profile" }
                                )
                            }
                            "calendar" -> {
                                FitnessCalendarScreen(
                                    currentRoute = currentScreen.value,
                                    onNavigateToHome = { currentScreen.value = "home" },
                                    onNavigateToCalendar = { currentScreen.value = "calendar" },
                                    onNavigateToMap = { currentScreen.value = "map" },
                                    onNavigateToProfile = { currentScreen.value = "profile" }
                                )
                            }
                            "map" -> {
                                // Display map page
                                MapScreen(
                                    onNavigateBack = { /* 移除,返回按钮功能 */ },
                                    onNavigateToHome = { currentScreen.value = "home" },
                                    onNavigateToCalendar = { currentScreen.value = "calendar" },
                                    onNavigateToMap = { currentScreen.value = "map" },
                                    onNavigateToProfile = { currentScreen.value = "profile" }
                                )
                            }
                            "profile" -> {
                                // Display profile page
                                ProfileScreen(
                                    onBackClick = { /* 移除返回按钮功能 */ },
                                    onViewAllHistory = { currentScreen.value = "record" },
                                    onEditProfileClick = { tags -> 
                                        Log.d("MainActivity", "Navigate to edit page, tags: ${selectedFitnessTags.value.joinToString()}")
                                        currentScreen.value = "profileEdit"
                                    },
                                    onSettingsClick = { currentScreen.value = "settings" },
                                    onAICoachClick = { currentScreen.value = "aiCoach" },
                                    onNavigateToMap = { currentScreen.value = "map" },
                                    selectedFitnessTags = selectedFitnessTags.value,
                                    onFitnessTagsUpdated = { tags ->
                                        Log.d("MainActivity", "Update tags: ${tags.joinToString()}")
                                        selectedFitnessTags.value = tags
                                    }
                                )
                            }
                            "profileEdit" -> {
                                // Display profile edit page
                                ProfileEditScreen(
                                    onBackClick = { 
                                        Log.d("MainActivity", "Return from edit page, tags: ${selectedFitnessTags.value.joinToString()}")
                                        currentScreen.value = "profile" 
                                    },
                                    initialFitnessTags = selectedFitnessTags.value,
                                    onFitnessTagsSelected = { tags ->
                                        Log.d("MainActivity", "Tag selection updated: ${tags.joinToString()}")
                                        selectedFitnessTags.value = tags
                                    },
                                    onNavigateToHome = { currentScreen.value = "home" },
                                    onNavigateToCalendar = { currentScreen.value = "calendar" },
                                    onNavigateToMap = { currentScreen.value = "map" },
                                    onNavigateToProfile = { currentScreen.value = "profile" }
                                )
                            }
                            "settings" -> {
                                // Display settings page
                                SettingsScreen(
                                    onBackClick = { currentScreen.value = "profile" },
                                    onLogout = { 
                                        isLoggedIn.value = false
                                        currentScreen.value = "login"
                                    },
                                    onProfileClick = { currentScreen.value = "profileEdit" },
                                    onAboutUsClick = { currentScreen.value = "about" },
                                    onHelpFeedbackClick = { currentScreen.value = "helpFeedback" },
                                    onChangePasswordClick = { currentScreen.value = "changePassword" },
                                    onPrivacyPolicyClick = { currentScreen.value = "privacyPolicy" },
                                    onTermsOfServiceClick = { currentScreen.value = "termsOfService" },
                                    onAccessibilityClick = { currentScreen.value = "accessibility" }
                                )
                            }
                            "about" -> {
                                // Display about us page
                                AboutUsScreen(
                                    onBackClick = { currentScreen.value = "settings" }
                                )
                            }
                            "helpFeedback" -> {
                                HelpFeedbackScreen(
                                    onBackClick = { currentScreen.value = "settings" }
                                )
                            }
                            "changePassword" -> {
                                ChangePasswordScreen(
                                    onBackClick = { currentScreen.value = "settings" },
                                    onChangePassword = { current, new ->
                                        // TODO: Implement actual password change logic
                                        println("Changing password from $current to $new")
                                        currentScreen.value = "settings" // Navigate back after attempting change
                                    }
                                )
                            }
                            "record" -> {
                                RecordTrainingScreen(
                                    currentRoute = "record",
                                    onNavigateToHome = { currentScreen.value = "map" },
                                    onNavigateToCalendar = { /* 可扩展 */ },
                                    onNavigateToMap = { currentScreen.value = "map" },
                                    onNavigateToProfile = { currentScreen.value = "profile" }
                                )
                            }
                            "aiCoach" -> {
                                AICoachScreen(
                                    onNavigateBack = { currentScreen.value = "profile" },
                                    onNavigateToHome = { /* TODO: Navigate */ },
                                    onNavigateToCalendar = { /* TODO: Navigate */ },
                                    onNavigateToMap = { currentScreen.value = "map" },
                                    onNavigateToProfile = { currentScreen.value = "profile" },
                                    currentRoute = currentScreen.value // Pass current route
                                )
                            }
                            "privacyPolicy" -> {
                                PrivacyPolicyScreen(
                                    onBackClick = { currentScreen.value = "settings" }
                                )
                            }
                            "termsOfService" -> {
                                TermsOfServiceScreen(
                                    onBackClick = { currentScreen.value = "settings" }
                                )
                            }
                            "accessibility" -> {
                                AccessibilityScreen(
                                    onBackClick = { currentScreen.value = "settings" }
                                )
                            }
                            else -> {
                                // Default to map page
                                currentScreen.value = "map"
                            }

                        }
                    }
                    currentScreen.value == "login" -> {
                        // Not logged in, display login page
                        LoginScreen(
                            onLoginSuccess = {
                                isLoggedIn.value = true
                                currentScreen.value = "home"
                            },
                            onRegisterClick = { currentScreen.value = "register" }
                        )
                    }
                    currentScreen.value == "register" -> {
                        // Display register page
                        RegisterScreen(
                            onRegisterSuccess = {
                                isLoggedIn.value = true
                                currentScreen.value = "home"
                            },
                            onNavigateToLogin = { currentScreen.value = "login" },
                            onNavigateToTerms = { currentScreen.value = "termsOfService" },
                            onNavigateToPrivacy = { currentScreen.value = "privacyPolicy" }
                        )
                    }
                }
            }
        }
    }
}
