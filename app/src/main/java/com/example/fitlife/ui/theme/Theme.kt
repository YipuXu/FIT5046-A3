package com.example.fitlife.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Density
import androidx.core.view.WindowCompat
import com.example.fitlife.data.preferences.AccessibilityPreferences

// 高对比度模式下的颜色方案 - 增强对比度
private val HighContrastLightColorScheme = lightColorScheme(
    primary = Color(0xFF0000FF), // 深蓝色
    onPrimary = Color.White,
    secondary = Color(0xFF000000), // 黑色
    onSecondary = Color.White,
    background = Color.White,
    onBackground = Color.Black,
    surface = Color.White,
    onSurface = Color.Black,
    error = Color(0xFFFF0000), // 红色
    onError = Color.White,
    
    // 增加更多高对比度的颜色定义
    primaryContainer = Color(0xFF0000FF),
    onPrimaryContainer = Color.White,
    secondaryContainer = Color(0xFF000000),
    onSecondaryContainer = Color.White,
    tertiaryContainer = Color(0xFF000000),
    onTertiaryContainer = Color.White,
    surfaceVariant = Color.White,
    onSurfaceVariant = Color.Black,
    outline = Color.Black,
    outlineVariant = Color(0xFF444444)
)

private val HighContrastDarkColorScheme = darkColorScheme(
    primary = Color(0xFF00BFFF), // 亮蓝色
    onPrimary = Color.Black,
    secondary = Color(0xFFFFFFFF), // 白色
    onSecondary = Color.Black,
    background = Color.Black,
    onBackground = Color.White,
    surface = Color.Black,
    onSurface = Color.White,
    error = Color(0xFFFF6B6B), // 亮红色
    onError = Color.Black,
    
    // 增加更多高对比度的颜色定义
    primaryContainer = Color(0xFF00BFFF),
    onPrimaryContainer = Color.Black,
    secondaryContainer = Color(0xFFFFFFFF),
    onSecondaryContainer = Color.Black,
    tertiaryContainer = Color(0xFFFFFFFF),
    onTertiaryContainer = Color.Black,
    surfaceVariant = Color.Black,
    onSurfaceVariant = Color.White,
    outline = Color.White,
    outlineVariant = Color(0xFFCCCCCC)
)

// 色盲模式的颜色方案 (红绿色盲友好)
private val ColorBlindLightColorScheme = lightColorScheme(
    primary = Color(0xFF0072B2), // 蓝色 - 替代绿色
    secondary = Color(0xFFE69F00), // 橙色 - 替代红色
    tertiary = Color(0xFF56B4E9), // 天蓝色
    background = Color.White,
    onBackground = Color(0xFF000000),
    surface = Color.White,
    onSurface = Color(0xFF000000),
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onTertiary = Color.Black,
    error = Color(0xFF9467BD), // 紫色 - 替代红色作为错误色
    onError = Color.White,
    primaryContainer = Color(0xFFD0E7F7), // 浅蓝色
    secondaryContainer = Color(0xFFFFF0D0), // 浅橙色
    tertiaryContainer = Color(0xFFD9F0FB), // 浅天蓝色
    errorContainer = Color(0xFFE9DBEE), // 浅紫色
    onErrorContainer = Color(0xFF9467BD)
)

private val ColorBlindDarkColorScheme = darkColorScheme(
    primary = Color(0xFF00ADD8), // 亮蓝色
    secondary = Color(0xFFFFB74D), // 亮橙色
    tertiary = Color(0xFF80DEEA), // 亮天蓝色
    background = Color(0xFF121212),
    onBackground = Color(0xFFFFFFFF),
    surface = Color(0xFF121212),
    onSurface = Color(0xFFFFFFFF),
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onTertiary = Color.Black,
    error = Color(0xFFD0A9DF), // 浅紫色 - 替代红色
    onError = Color.Black,
    primaryContainer = Color(0xFF004080), // 深蓝色
    secondaryContainer = Color(0xFFC87800), // 深橙色
    tertiaryContainer = Color(0xFF0097A7), // 深蓝绿色
    errorContainer = Color(0xFF7E4E9B), // 深紫色
    onErrorContainer = Color(0xFFE9DBEE) // 浅紫色
)

// 默认颜色方案
private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

// 本地组合项，用于存储辅助功能设置
data class AccessibilitySettings(
    val highContrastMode: Boolean = false,
    val colorBlindMode: Boolean = false,
    val zoomFunction: Boolean = false,
    val screenReader: Boolean = false,
    val keyboardControl: Boolean = false
)

val LocalAccessibilitySettings = staticCompositionLocalOf { 
    AccessibilitySettings() 
}

@Composable
fun FitLifeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val accessibilityPreferences = AccessibilityPreferences(context)
    
    // 收集辅助功能设置
    val highContrastMode by accessibilityPreferences.highContrastMode.collectAsState(initial = false)
    val colorBlindMode by accessibilityPreferences.colorBlindMode.collectAsState(initial = false)
    val zoomFunction by accessibilityPreferences.zoomFunction.collectAsState(initial = false)
    val screenReader by accessibilityPreferences.screenReader.collectAsState(initial = false)
    val keyboardControl by accessibilityPreferences.keyboardControl.collectAsState(initial = false)
    
    val accessibilitySettings = AccessibilitySettings(
        highContrastMode = highContrastMode,
        colorBlindMode = colorBlindMode,
        zoomFunction = zoomFunction,
        screenReader = screenReader,
        keyboardControl = keyboardControl
    )
    
    // 基于辅助功能设置确定颜色方案
    val colorScheme = when {
        highContrastMode -> if (darkTheme) HighContrastDarkColorScheme else HighContrastLightColorScheme
        colorBlindMode -> if (darkTheme) ColorBlindDarkColorScheme else ColorBlindLightColorScheme
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }
    
    // 处理缩放功能
    val density = LocalDensity.current
    val fontScale = if (zoomFunction) 1.3f else 1.0f
    val scaledDensity = Density(
        density = density.density,
        fontScale = density.fontScale * fontScale
    )
    
    CompositionLocalProvider(
        LocalDensity provides scaledDensity,
        LocalAccessibilitySettings provides accessibilitySettings
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}