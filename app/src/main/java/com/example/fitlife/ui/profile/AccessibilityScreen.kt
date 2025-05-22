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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import com.example.fitlife.data.preferences.AccessibilityPreferences
import com.example.fitlife.ui.theme.AccessibilityUtils
import kotlinx.coroutines.launch

@Composable
fun AccessibilityScreen(
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val accessibilityPreferences = remember { AccessibilityPreferences(context) }
    val coroutineScope = rememberCoroutineScope()
    val isHighContrastMode = AccessibilityUtils.isHighContrastModeEnabled()

    // 从DataStore获取设置
    val highContrastEnabled by accessibilityPreferences.highContrastMode.collectAsState(initial = false)
    val colorBlindModeEnabled by accessibilityPreferences.colorBlindMode.collectAsState(initial = false)
    val zoomEnabled by accessibilityPreferences.zoomFunction.collectAsState(initial = false)
    val screenReaderEnabled by accessibilityPreferences.screenReader.collectAsState(initial = false)
    val keyboardControlEnabled by accessibilityPreferences.keyboardControl.collectAsState(initial = false)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isHighContrastMode) Color.White else Color(0xFFF9FAFB))
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        // Fixed top bar
        TopBar(
            onBackClick = onBackClick,
            modifier = Modifier
                .fillMaxWidth()
                .zIndex(1f) // Ensure top bar stays above scrolling content
                .background(if (isHighContrastMode) Color.White else Color(0xFFF9FAFB))
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

            // 使用AccessibilityUtils中的组件
            AccessibilityUtils.AccessibleSettingsItem(
                title = "High Contrast Mode",
                description = "Enhance the contrast between text and background for better visibility",
                trailingContent = {
                    AccessibilityUtils.AccessibleSwitch(
                        checked = highContrastEnabled,
                        onCheckedChange = { 
                            coroutineScope.launch {
                                accessibilityPreferences.saveHighContrastMode(it)
                            }
                        }
                    )
                }
            )

            AccessibilityUtils.AccessibleSettingsItem(
                title = "Color Blind Mode",
                description = "Adjust colors for color blind users to improve recognition",
                trailingContent = {
                    AccessibilityUtils.AccessibleSwitch(
                        checked = colorBlindModeEnabled,
                        onCheckedChange = { 
                            coroutineScope.launch {
                                accessibilityPreferences.saveColorBlindMode(it)
                            }
                        }
                    )
                }
            )

            AccessibilityUtils.AccessibleSettingsItem(
                title = "Zoom Function",
                description = "Allow zooming interface elements to make content easier to read",
                trailingContent = {
                    AccessibilityUtils.AccessibleSwitch(
                        checked = zoomEnabled,
                        onCheckedChange = { 
                            coroutineScope.launch {
                                accessibilityPreferences.saveZoomFunction(it)
                            }
                        }
                    )
                }
            )

            AccessibilityUtils.AccessibleSettingsItem(
                title = "Screen Reader",
                description = "Enable screen reading functionality for visually impaired users",
                trailingContent = {
                    AccessibilityUtils.AccessibleSwitch(
                        checked = screenReaderEnabled,
                        onCheckedChange = { 
                            coroutineScope.launch {
                                accessibilityPreferences.saveScreenReader(it)
                            }
                        }
                    )
                }
            )

            AccessibilityUtils.AccessibleSettingsItem(
                title = "Keyboard Control",
                description = "Allow keyboard navigation and control of the application",
                trailingContent = {
                    AccessibilityUtils.AccessibleSwitch(
                        checked = keyboardControlEnabled,
                        onCheckedChange = { 
                            coroutineScope.launch {
                                accessibilityPreferences.saveKeyboardControl(it)
                            }
                        }
                    )
                }
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
    val isHighContrastMode = AccessibilityUtils.isHighContrastModeEnabled()
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(if (isHighContrastMode) Color.White else Color(0xFFF9FAFB))
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
                    .background(if (isHighContrastMode) Color.Black else Color(0xFFF3F4F6))
                    .clickable { onBackClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier.size(20.dp),
                    tint = if (isHighContrastMode) Color.White else Color(0xFF6B7280)
                )
            }

            // Title
            Text(
                text = "Accessibility",
                fontSize = 18.sp,
                fontWeight = if (isHighContrastMode) FontWeight.ExtraBold else FontWeight.Bold,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                textAlign = TextAlign.Center,
                color = if (isHighContrastMode) Color.Black else Color(0xFF1F2937)
            )

            // Placeholder for symmetry
            Spacer(modifier = Modifier.size(32.dp))
        }
    }
}
