package com.example.fitlife.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// 扩展Context类添加DataStore
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "accessibility_settings")

/**
 * 辅助功能设置管理类
 * 用于存储和检索用户的辅助功能偏好
 */
class AccessibilityPreferences(private val context: Context) {

    // 定义设置键
    companion object {
        val HIGH_CONTRAST_MODE = booleanPreferencesKey("high_contrast_mode")
        val COLOR_BLIND_MODE = booleanPreferencesKey("color_blind_mode")
        val ZOOM_FUNCTION = booleanPreferencesKey("zoom_function")
        val SCREEN_READER = booleanPreferencesKey("screen_reader")
        val KEYBOARD_CONTROL = booleanPreferencesKey("keyboard_control")
    }

    // 高对比度模式
    val highContrastMode: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[HIGH_CONTRAST_MODE] ?: false
        }

    // 色盲模式
    val colorBlindMode: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[COLOR_BLIND_MODE] ?: false
        }

    // 缩放功能
    val zoomFunction: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[ZOOM_FUNCTION] ?: false
        }

    // 屏幕阅读器
    val screenReader: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[SCREEN_READER] ?: false
        }

    // 键盘控制
    val keyboardControl: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[KEYBOARD_CONTROL] ?: false
        }

    // 保存高对比度模式设置
    suspend fun saveHighContrastMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[HIGH_CONTRAST_MODE] = enabled
        }
    }

    // 保存色盲模式设置
    suspend fun saveColorBlindMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[COLOR_BLIND_MODE] = enabled
        }
    }

    // 保存缩放功能设置
    suspend fun saveZoomFunction(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[ZOOM_FUNCTION] = enabled
        }
    }

    // 保存屏幕阅读器设置
    suspend fun saveScreenReader(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[SCREEN_READER] = enabled
        }
    }

    // 保存键盘控制设置
    suspend fun saveKeyboardControl(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEYBOARD_CONTROL] = enabled
        }
    }
} 