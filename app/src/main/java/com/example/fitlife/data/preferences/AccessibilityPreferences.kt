package com.example.fitlife.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extend the Context class to add DataStore
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "accessibility_settings")

/***
 * Accessibility settings manager class
 * Used to store and retrieve user accessibility preferences
 */
class AccessibilityPreferences(private val context: Context) {

    companion object {
        val HIGH_CONTRAST_MODE = booleanPreferencesKey("high_contrast_mode")
        val COLOR_BLIND_MODE = booleanPreferencesKey("color_blind_mode")
        val ZOOM_FUNCTION = booleanPreferencesKey("zoom_function")
        val SCREEN_READER = booleanPreferencesKey("screen_reader")
        val KEYBOARD_CONTROL = booleanPreferencesKey("keyboard_control")
    }

    // High contrast mode
    val highContrastMode: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[HIGH_CONTRAST_MODE] ?: false
        }

    // Colorblind mode
    val colorBlindMode: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[COLOR_BLIND_MODE] ?: false
        }

    // Zoom function
    val zoomFunction: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[ZOOM_FUNCTION] ?: false
        }

    // Screen readers
    val screenReader: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[SCREEN_READER] ?: false
        }

    // Keyboard Control
    val keyboardControl: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[KEYBOARD_CONTROL] ?: false
        }

    // Save high contrast mode settings
    suspend fun saveHighContrastMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[HIGH_CONTRAST_MODE] = enabled
        }
    }

    // Save colorblind mode settings
    suspend fun saveColorBlindMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[COLOR_BLIND_MODE] = enabled
        }
    }

    // Save zoom settings
    suspend fun saveZoomFunction(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[ZOOM_FUNCTION] = enabled
        }
    }

    // Save screen reader settings
    suspend fun saveScreenReader(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[SCREEN_READER] = enabled
        }
    }

    // Save keyboard control settings
    suspend fun saveKeyboardControl(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEYBOARD_CONTROL] = enabled
        }
    }
} 