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

// High contrast mode color scheme - enhanced contrast
private val HighContrastLightColorScheme = lightColorScheme(
    primary = Color(0xFF0000FF), // Dark blue
    onPrimary = Color.White,
    secondary = Color(0xFF000000), // Black
    onSecondary = Color.White,
    background = Color.White,
    onBackground = Color.Black,
    surface = Color.White,
    onSurface = Color.Black,
    error = Color(0xFFFF0000), // Red
    onError = Color.White,
    
    // Add more high contrast color definitions
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
    primary = Color(0xFF00BFFF), // Bright blue
    onPrimary = Color.Black,
    secondary = Color(0xFFFFFFFF), // White
    onSecondary = Color.Black,
    background = Color.Black,
    onBackground = Color.White,
    surface = Color.Black,
    onSurface = Color.White,
    error = Color(0xFFFF6B6B), // Bright red
    onError = Color.Black,
    
    // Add more high contrast color definitions
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

// Color blind mode color scheme (Red-Green color blindness friendly)
private val ColorBlindLightColorScheme = lightColorScheme(
    primary = Color(0xFF0072B2), // Blue - replaces green
    secondary = Color(0xFFE69F00), // Orange - replaces red
    tertiary = Color(0xFF56B4E9), // Sky blue
    background = Color.White,
    onBackground = Color(0xFF000000),
    surface = Color.White,
    onSurface = Color(0xFF000000),
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onTertiary = Color.Black,
    error = Color(0xFF9467BD), // Purple - replaces red for error color
    onError = Color.White,
    primaryContainer = Color(0xFFD0E7F7), // Light blue
    secondaryContainer = Color(0xFFFFF0D0), // Light orange
    tertiaryContainer = Color(0xFFD9F0FB), // Light sky blue
    errorContainer = Color(0xFFE9DBEE), // Light purple
    onErrorContainer = Color(0xFF9467BD)
)

private val ColorBlindDarkColorScheme = darkColorScheme(
    primary = Color(0xFF00ADD8), // Bright blue
    secondary = Color(0xFFFFB74D), // Bright orange
    tertiary = Color(0xFF80DEEA), // Bright sky blue
    background = Color(0xFF121212),
    onBackground = Color(0xFFFFFFFF),
    surface = Color(0xFF121212),
    onSurface = Color(0xFFFFFFFF),
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onTertiary = Color.Black,
    error = Color(0xFFD0A9DF), // Light purple - replaces red
    onError = Color.Black,
    primaryContainer = Color(0xFF004080), // Dark blue
    secondaryContainer = Color(0xFFC87800), // Dark orange
    tertiaryContainer = Color(0xFF0097A7), // Dark teal
    errorContainer = Color(0xFF7E4E9B), // Dark purple
    onErrorContainer = Color(0xFFE9DBEE) // Light purple
)

// Default color scheme
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

// Local composition item for storing accessibility settings
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
    
    // Collect accessibility settings
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
    
    // Determine color scheme based on accessibility settings
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
    
    // Handle zoom function
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