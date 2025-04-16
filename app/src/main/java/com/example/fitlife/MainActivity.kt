package com.example.fitlife

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.fitlife.ui.auth.LoginScreen
import com.example.fitlife.ui.auth.RegisterScreen
import com.example.fitlife.ui.map.MapScreen
import com.example.fitlife.ui.profile.ProfileEditScreen
import com.example.fitlife.ui.profile.ProfileScreen
import com.example.fitlife.ui.profile.SettingsScreen
import com.example.fitlife.ui.theme.FitLifeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FitLifeTheme {
                // Use state to control login status and current page
                val isLoggedIn = remember { mutableStateOf(false) }
                val currentScreen = remember { mutableStateOf("login") } // login, register, map, profile, profileEdit, settings
                
                // Add a state to store selected fitness tags
                val selectedFitnessTags = remember { mutableStateOf(listOf("Strength Training", "Cardio")) }
                
                when {
                    isLoggedIn.value -> {
                        when (currentScreen.value) {
                            "map" -> {
                                // Display map page
                                MapScreen(
                                    onNavigateBack = { isLoggedIn.value = false },
                                    onNavigateToHome = { /* Empty for now */ },
                                    onNavigateToCalendar = { /* Empty for now */ },
                                    onNavigateToProfile = { currentScreen.value = "profile" }
                                )
                            }
                            "profile" -> {
                                // Display profile page
                                ProfileScreen(
                                    onBackClick = { currentScreen.value = "map" },
                                    onViewAllHistory = { /* Empty for now */ },
                                    onEditProfileClick = { tags -> 
                                        Log.d("MainActivity", "Navigate to edit page, tags: ${selectedFitnessTags.value.joinToString()}")
                                        currentScreen.value = "profileEdit" 
                                    },
                                    onSettingsClick = { currentScreen.value = "settings" },
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
                                    // Pass current selected tags to ProfileEditScreen
                                    initialFitnessTags = selectedFitnessTags.value,
                                    // Update state when tags are updated
                                    onFitnessTagsSelected = { tags ->
                                        Log.d("MainActivity", "Tag selection updated: ${tags.joinToString()}")
                                        selectedFitnessTags.value = tags
                                    }
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
                                    onProfileClick = { currentScreen.value = "profileEdit" }
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
                                currentScreen.value = "map"
                            },
                            onRegisterClick = { currentScreen.value = "register" }
                        )
                    }
                    currentScreen.value == "register" -> {
                        // Display register page
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
