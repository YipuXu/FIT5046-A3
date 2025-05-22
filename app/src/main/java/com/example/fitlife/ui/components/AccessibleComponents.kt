package com.example.fitlife.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitlife.ui.theme.AccessibilityUtils
import com.example.fitlife.ui.theme.AccessibilityUtils.highContrastBorder

/**
 * 支持辅助功能的卡片组件
 * 在高对比度模式下会有明显边框和更高的对比度
 */
@Composable
fun AccessibleCard(
    title: String,
    content: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val isHighContrastMode = AccessibilityUtils.isHighContrastModeEnabled()
    
    // 在高对比度模式下使用不同的颜色
    val backgroundColor = if (isHighContrastMode) Color.White else MaterialTheme.colorScheme.surface
    val contentColor = if (isHighContrastMode) Color.Black else MaterialTheme.colorScheme.onSurface
    val titleColor = if (isHighContrastMode) Color.Black else MaterialTheme.colorScheme.primary
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .highContrastBorder(RoundedCornerShape(8.dp)), // 在高对比度模式下添加边框
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isHighContrastMode) 8.dp else 4.dp // 在高对比度模式下增加阴影
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = if (isHighContrastMode) FontWeight.ExtraBold else FontWeight.Bold,
                color = titleColor
            )
            
            Text(
                text = content,
                fontSize = 14.sp,
                color = contentColor,
                modifier = Modifier.padding(top = 8.dp)
            )
            
            AccessibleButton(
                text = "了解更多",
                onClick = onClick,
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(top = 8.dp)
            )
        }
    }
}

/**
 * 支持辅助功能的按钮组件
 * 在高对比度模式下有更明显的视觉效果
 */
@Composable
fun AccessibleButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isHighContrastMode = AccessibilityUtils.isHighContrastModeEnabled()
    
    // 在高对比度模式下使用不同的颜色
    val containerColor = if (isHighContrastMode) Color(0xFF0000FF) else MaterialTheme.colorScheme.primary
    val contentColor = if (isHighContrastMode) Color.White else MaterialTheme.colorScheme.onPrimary
    
    Button(
        onClick = onClick,
        modifier = modifier
            .highContrastBorder(RoundedCornerShape(4.dp)), // 在高对比度模式下添加边框
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        )
    ) {
        Text(
            text = text,
            fontWeight = if (isHighContrastMode) FontWeight.ExtraBold else FontWeight.Normal
        )
    }
}

/**
 * 支持辅助功能的标题组件
 * 在高对比度模式下有更高的可见度
 */
@Composable
fun AccessibleHeading(
    text: String,
    modifier: Modifier = Modifier
) {
    val isHighContrastMode = AccessibilityUtils.isHighContrastModeEnabled()
    
    Text(
        text = text,
        fontSize = 22.sp,
        fontWeight = if (isHighContrastMode) FontWeight.ExtraBold else FontWeight.Bold,
        color = if (isHighContrastMode) Color.Black else MaterialTheme.colorScheme.onBackground,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .let {
                if (isHighContrastMode) {
                    it.background(Color.White)
                        .padding(8.dp)
                } else {
                    it
                }
            }
    )
} 