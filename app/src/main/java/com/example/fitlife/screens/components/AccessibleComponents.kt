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
 * Accessible card component
 * Has a prominent border and higher contrast in high contrast mode
 */
@Composable
fun AccessibleCard(
    title: String,
    content: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val isHighContrastMode = AccessibilityUtils.isHighContrastModeEnabled()
    
    // Use different colors in high contrast mode
    val backgroundColor = if (isHighContrastMode) Color.White else MaterialTheme.colorScheme.surface
    val contentColor = if (isHighContrastMode) Color.Black else MaterialTheme.colorScheme.onSurface
    val titleColor = if (isHighContrastMode) Color.Black else MaterialTheme.colorScheme.primary
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .highContrastBorder(RoundedCornerShape(8.dp)), // Add border in high contrast mode
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isHighContrastMode) 8.dp else 4.dp // Increase elevation in high contrast mode
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
                text = "learn more",
                onClick = onClick,
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(top = 8.dp)
            )
        }
    }
}

/**
 * Accessible button component
 * Has more prominent visual effects in high contrast mode
 */
@Composable
fun AccessibleButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isHighContrastMode = AccessibilityUtils.isHighContrastModeEnabled()
    
    // Use different colors in high contrast mode
    val containerColor = if (isHighContrastMode) Color(0xFF0000FF) else MaterialTheme.colorScheme.primary
    val contentColor = if (isHighContrastMode) Color.White else MaterialTheme.colorScheme.onPrimary
    
    Button(
        onClick = onClick,
        modifier = modifier
            .highContrastBorder(RoundedCornerShape(4.dp)), // Add border in high contrast mode
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
 * Accessible heading component
 * Has higher visibility in high contrast mode
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