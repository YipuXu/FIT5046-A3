package com.example.fitlife.ui.theme

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip

object AccessibilityUtils {
    
    /**
     * Check if high contrast mode is enabled
     */
    @Composable
    @ReadOnlyComposable
    fun isHighContrastModeEnabled(): Boolean {
        return LocalAccessibilitySettings.current.highContrastMode
    }
    
    /**
     * Check if colorblind mode is enabled
     */
    @Composable
    @ReadOnlyComposable
    fun isColorBlindModeEnabled(): Boolean {
        return LocalAccessibilitySettings.current.colorBlindMode
    }
    
    /**
     * Get the color for high contrast mode
     * @param normalColor The color in normal mode
     * @param highContrastColor The color in high contrast mode
     * @return Returns the appropriate color based on the current mode
     */
    @Composable
    @ReadOnlyComposable
    fun getAccessibleColor(normalColor: Color, highContrastColor: Color): Color {
        return if (isHighContrastModeEnabled()) highContrastColor else normalColor
    }
    
    /**
     * Get the color suitable for color blindness mode
     * Mainly optimized for red and green color blindness (Deuteranopia)
     *
     * @param normalColor The color in normal mode
     * @return Returns the color suitable for color blind users according to the current mode
     */
    @Composable
    @ReadOnlyComposable
    fun getColorBlindFriendlyColor(normalColor: Color): Color {
        if (!isColorBlindModeEnabled()) {
            return normalColor
        }
        
        // Decompose the RGB values of a color
        val red = normalColor.red
        val green = normalColor.green
        val blue = normalColor.blue

        // Red-green color blindness friendly color optimization
        // 1. Avoid using red and green at the same time
        // 2. For red-green color blindness, enhance the difference of blue channel
        // 3. Common alternative combinations: blue/yellow, blue/orange, black/white
        
        return when {
            // If the primary color is red (#FF0000 to #FF5050)
            red > 0.7f && green < 0.4f && blue < 0.4f -> {
                Color(0xFF0072B2) // Replace with blue
            }
            // If the primary color is green (#00FF00 to #50FF50)
            green > 0.7f && red < 0.4f && blue < 0.4f -> {
                Color(0xFFE69F00) // Replaced with orange/yellow
            }
            // Pink turns to purple-blue
            red > 0.7f && blue > 0.7f && green < 0.4f -> {
                Color(0xFF56B4E9) // blue
            }
            // If the color is close to white or black, no conversion is performed
            (red > 0.9f && green > 0.9f && blue > 0.9f) || (red < 0.1f && green < 0.1f && blue < 0.1f) -> {
                normalColor
            }
            // Other colors slightly adjusted to improve distinguishability
            else -> {
                // Increase blue-yellow contrast
                val newRed = red * 0.8f
                val newGreen = green * 0.9f
                val newBlue = blue * 1.1f.coerceAtMost(1.0f)
                
                Color(newRed, newGreen, newBlue, normalColor.alpha)
            }
        }
    }
    
    /**
     * Get the color that is suitable for both high contrast and color blindness modes
     * @param normalColor The color in normal mode
     * @param highContrastColor The color in high contrast mode
     * @return Returns the appropriate color based on the current accessibility settings
     */
    @Composable
    @ReadOnlyComposable
    fun getFullyAccessibleColor(normalColor: Color, highContrastColor: Color): Color {
        return when {
            isHighContrastModeEnabled() -> highContrastColor
            isColorBlindModeEnabled() -> getColorBlindFriendlyColor(normalColor)
            else -> normalColor
        }
    }
    
    /**
     * Provides high contrast style for text
     * @param normalStyle Text style in normal mode
     * @return Text style adjusted according to current accessibility settings
     */
    @Composable
    fun getAccessibleTextStyle(normalStyle: TextStyle): TextStyle {
        return if (isHighContrastModeEnabled()) {
            normalStyle.copy(
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                background = Color.White
            )
        } else {
            normalStyle
        }
    }
    
    /**
     * Modifier extension function, add high contrast border to the component
     * Only add obvious border in high contrast mode
     */
    fun Modifier.highContrastBorder(shape: Shape): Modifier = composed {
        if (isHighContrastModeEnabled()) {
            this.border(2.dp, Color.Black, shape)
                .padding(1.dp)
        } else {
            this
        }
    }
    
    /**
     * Get the text component for high contrast mode
     * @param text The text content displayed
     * @param style The text style
     * @param modifier The modifier applied to the Text
     */
    @Composable
    fun AccessibleText(
        text: String,
        style: TextStyle,
        modifier: Modifier = Modifier
    ) {
        val isHighContrast = isHighContrastModeEnabled()
        val accessibleStyle = if (isHighContrast) {
            style.copy(
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
        } else {
            style
        }
        
        Text(
            text = text,
            style = accessibleStyle,
            modifier = modifier
        )
    }
    
    /**
     * High contrast version of the card component
     */
    @Composable
    fun AccessibleCard(
        modifier: Modifier = Modifier,
        onClick: () -> Unit = {},
        content: @Composable () -> Unit
    ) {
        val isHighContrast = isHighContrastModeEnabled()
        val shape = RoundedCornerShape(16.dp)
        
        Card(
            modifier = modifier.let {
                if (isHighContrast) {
                    it.border(2.dp, Color.Black, shape)
                } else {
                    it
                }
            },
            shape = shape,
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = if (isHighContrast) 4.dp else 0.dp
            ),
            onClick = onClick
        ) {
            content()
        }
    }
    
    /**
     * High contrast version of the settings widget
     */
    @Composable
    fun AccessibleSettingsItem(
        title: String,
        description: String = "",
        onClick: () -> Unit = {},
        trailingContent: @Composable () -> Unit = {}
    ) {
        val isHighContrast = isHighContrastModeEnabled()
        
        AccessibleCard(
            modifier = Modifier.padding(vertical = 4.dp),
            onClick = onClick
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                ) {
                    Text(
                        text = title,
                        fontSize = MaterialTheme.typography.titleMedium.fontSize,
                        fontWeight = if (isHighContrast) FontWeight.Bold else FontWeight.Medium,
                        color = if (isHighContrast) Color.Black else MaterialTheme.colorScheme.onSurface
                    )
                    
                    if (description.isNotEmpty()) {
                        Text(
                            text = description,
                            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                            color = if (isHighContrast) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
                
                trailingContent()
            }
        }
    }
    
    /**
     * High contrast version of switch assembly
     */
    @Composable
    fun AccessibleSwitch(
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit,
        modifier: Modifier = Modifier
    ) {
        val isHighContrast = isHighContrastModeEnabled()
        val isColorBlind = isColorBlindModeEnabled()
        
        val checkedTrackColor = when {
            isHighContrast -> Color.Black
            isColorBlind -> Color(0xFF0072B2) // Blue, suitable for red and green color blindness
            else -> Color(0xFF34C759) // Default green
        }
        
        val uncheckedTrackColor = when {
            isHighContrast -> Color.Gray
            isColorBlind -> Color(0xFFCCCCCC) // Light gray, to enhance contrast with blue
            else -> Color(0xFFE5E5EA)
        }
        
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = modifier,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = checkedTrackColor,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = uncheckedTrackColor,
                uncheckedBorderColor = if (isHighContrast) Color.Black else Color(0xFFE5E5EA).copy(alpha = 0.8f)
            )
        )
    }
} 