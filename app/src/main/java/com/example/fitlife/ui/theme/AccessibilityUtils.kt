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
     * 检查高对比度模式是否启用
     */
    @Composable
    @ReadOnlyComposable
    fun isHighContrastModeEnabled(): Boolean {
        return LocalAccessibilitySettings.current.highContrastMode
    }
    
    /**
     * 检查色盲模式是否启用
     */
    @Composable
    @ReadOnlyComposable
    fun isColorBlindModeEnabled(): Boolean {
        return LocalAccessibilitySettings.current.colorBlindMode
    }
    
    /**
     * 获取适用于高对比度模式的颜色
     * @param normalColor 正常模式下的颜色
     * @param highContrastColor 高对比度模式下的颜色
     * @return 根据当前模式返回适当的颜色
     */
    @Composable
    @ReadOnlyComposable
    fun getAccessibleColor(normalColor: Color, highContrastColor: Color): Color {
        return if (isHighContrastModeEnabled()) highContrastColor else normalColor
    }
    
    /**
     * 获取适配色盲模式的颜色
     * 主要针对红绿色盲(Deuteranopia)进行优化
     * 
     * @param normalColor 正常模式下的颜色
     * @return 根据当前模式返回适合色盲用户的颜色
     */
    @Composable
    @ReadOnlyComposable
    fun getColorBlindFriendlyColor(normalColor: Color): Color {
        if (!isColorBlindModeEnabled()) {
            return normalColor
        }
        
        // 分解颜色的RGB值
        val red = normalColor.red
        val green = normalColor.green
        val blue = normalColor.blue
        
        // 红绿色盲友好色彩优化
        // 1. 避免同时使用红色和绿色
        // 2. 针对红绿色盲，增强蓝色通道的差异
        // 3. 常见替代组合：蓝/黄, 蓝/橙, 黑/白
        
        return when {
            // 如果原色是红色系 (#FF0000 到 #FF5050)
            red > 0.7f && green < 0.4f && blue < 0.4f -> {
                Color(0xFF0072B2) // 替换为蓝色
            }
            // 如果原色是绿色系 (#00FF00 到 #50FF50)
            green > 0.7f && red < 0.4f && blue < 0.4f -> {
                Color(0xFFE69F00) // 替换为橙/黄色
            }
            // 粉色系转为紫蓝色
            red > 0.7f && blue > 0.7f && green < 0.4f -> {
                Color(0xFF56B4E9) // 蓝色
            }
            // 如果是接近白色或黑色的颜色，不进行转换
            (red > 0.9f && green > 0.9f && blue > 0.9f) || (red < 0.1f && green < 0.1f && blue < 0.1f) -> {
                normalColor
            }
            // 其他颜色略微调整以提高可区分度
            else -> {
                // 增加蓝黄对比度
                val newRed = red * 0.8f
                val newGreen = green * 0.9f
                val newBlue = blue * 1.1f.coerceAtMost(1.0f)
                
                Color(newRed, newGreen, newBlue, normalColor.alpha)
            }
        }
    }
    
    /**
     * 获取同时适配高对比度和色盲模式的颜色
     * @param normalColor 正常模式下的颜色
     * @param highContrastColor 高对比度模式下的颜色
     * @return 根据当前的辅助功能设置返回合适的颜色
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
     * 为文本提供高对比度样式
     * @param normalStyle 正常模式下的文本样式
     * @return 根据当前辅助功能设置调整的文本样式
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
     * Modifier扩展函数，为组件添加高对比度边框
     * 仅在高对比度模式下添加明显的边框
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
     * 获取适用于高对比度模式的文本组件
     * @param text 显示的文本内容
     * @param style 文本样式
     * @param modifier 应用于Text的修饰符
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
     * 高对比度版本的卡片组件
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
     * 高对比度版本的设置项组件
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
     * 高对比度版本的开关组件
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
            isColorBlind -> Color(0xFF0072B2) // 蓝色，适合红绿色盲
            else -> Color(0xFF34C759) // 默认绿色
        }
        
        val uncheckedTrackColor = when {
            isHighContrast -> Color.Gray
            isColorBlind -> Color(0xFFCCCCCC) // 浅灰色，增强与蓝色对比
            else -> Color(0xFFE5E5EA) // iOS默认灰色
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