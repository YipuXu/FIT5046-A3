package com.example.fitlife.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding

@Composable
fun AccessibilityScreen(
    onBackClick: () -> Unit = {}
) {
    var highContrastEnabled by remember { mutableStateOf(false) }
    var colorBlindModeEnabled by remember { mutableStateOf(false) }
    var zoomEnabled by remember { mutableStateOf(false) }
    var screenReaderEnabled by remember { mutableStateOf(false) }
    var keyboardControlEnabled by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9FAFB))
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        // Fixed top bar
        TopBar(
            onBackClick = onBackClick,
            modifier = Modifier
                .fillMaxWidth()
                .zIndex(1f) // Ensure top bar stays above scrolling content
                .background(Color(0xFFF9FAFB))
        )

        // Scrollable content area
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 64.dp) // Padding for the top bar
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            AccessibilityItem(
                title = "High Contrast Mode",
                checked = highContrastEnabled,
                onCheckedChange = { highContrastEnabled = it }
            )

            AccessibilityItem(
                title = "Color Blind Mode",
                checked = colorBlindModeEnabled,
                onCheckedChange = { colorBlindModeEnabled = it }
            )

            AccessibilityItem(
                title = "Zoom Function",
                checked = zoomEnabled,
                onCheckedChange = { zoomEnabled = it }
            )

            AccessibilityItem(
                title = "Screen Reader",
                checked = screenReaderEnabled,
                onCheckedChange = { screenReaderEnabled = it }
            )

            AccessibilityItem(
                title = "Keyboard Control",
                checked = keyboardControlEnabled,
                onCheckedChange = { keyboardControlEnabled = it }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun TopBar(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFFF9FAFB))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back button
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF3F4F6))
                    .clickable { onBackClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier.size(20.dp),
                    tint = Color(0xFF6B7280)
                )
            }

            // Title
            Text(
                text = "Accessibility",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                textAlign = TextAlign.Center,
                color = Color(0xFF1F2937)
            )

            // Placeholder for symmetry
            Spacer(modifier = Modifier.size(32.dp))
        }
    }
}

@Composable
private fun AccessibilityItem(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp), // Adjust padding for switch
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1F2937)
            )

            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color(0xFF34C759), // iOS Green
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color(0xFFE5E5EA), // iOS Grey Track
                    uncheckedBorderColor = Color(0xFFE5E5EA).copy(alpha = 0.8f) // iOS Grey Border
                )
            )
        }
    }
}
