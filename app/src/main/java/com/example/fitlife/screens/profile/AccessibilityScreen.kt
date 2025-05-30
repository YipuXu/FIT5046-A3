package com.example.fitlife.ui.profile

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.view.accessibility.AccessibilityManager
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
    
    // Get settings from DataStore
    val highContrastEnabled by accessibilityPreferences.highContrastMode.collectAsState(initial = false)
    val colorBlindModeEnabled by accessibilityPreferences.colorBlindMode.collectAsState(initial = false)
    val zoomEnabled by accessibilityPreferences.zoomFunction.collectAsState(initial = false)
    val screenReaderEnabled by accessibilityPreferences.screenReader.collectAsState(initial = false)
    val keyboardControlEnabled by accessibilityPreferences.keyboardControl.collectAsState(initial = false)
    
    // Dialog State
    var showScreenReaderDialog by remember { mutableStateOf(false) }
    var showKeyboardControlDialog by remember { mutableStateOf(false) }

    // Check if TalkBack service is enabled
    val isTalkBackEnabled = remember(context) {
        isTalkBackEnabled(context)
    }
    
    // Check if the keyboard control service is enabled
    val isKeyboardAccessibilityEnabled = remember(context) {
        isKeyboardAccessibilityEnabled(context)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                AccessibilityUtils.getFullyAccessibleColor(
                    normalColor = Color(0xFFF9FAFB),
                    highContrastColor = Color.White
                )
            )
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        // Fixed top bar
        TopBar(
            onBackClick = onBackClick,
            modifier = Modifier
                .fillMaxWidth()
                .zIndex(1f) // Ensure top bar stays above scrolling content
                .background(
                    AccessibilityUtils.getFullyAccessibleColor(
                        normalColor = Color(0xFFF9FAFB),
                        highContrastColor = Color.White
                    )
                )
        )

        // Scrollable content area
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 64.dp) // Padding for the top bar
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Using components in AccessibilityUtils
            AccessibilityUtils.AccessibleSettingsItem(
                title = "High Contrast Mode",
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
                title = "Zoom Function",
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
                title = "Color Blind Mode",
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
                title = "Screen Reader",
                trailingContent = {
                    AccessibilityUtils.AccessibleSwitch(
                checked = screenReaderEnabled,
                        onCheckedChange = { newValue -> 
                            if (newValue && !isTalkBackEnabled) {
                                // If user tries to enable screen reader but TalkBack is not on, show dialog
                                showScreenReaderDialog = true
                            } else {
                                // Otherwise save settings normally
                                coroutineScope.launch {
                                    accessibilityPreferences.saveScreenReader(newValue)
                                }
                            }
                        }
                    )
                }
            )

            AccessibilityUtils.AccessibleSettingsItem(
                title = "Keyboard Control",
                trailingContent = {
                    AccessibilityUtils.AccessibleSwitch(
                checked = keyboardControlEnabled,
                        onCheckedChange = { newValue -> 
                            if (newValue && !isKeyboardAccessibilityEnabled) {
                                // If user tries to enable keyboard control but system keyboard accessibility is not on, show dialog
                                showKeyboardControlDialog = true
                            } else {
                                // Otherwise save settings normally
                                coroutineScope.launch {
                                    accessibilityPreferences.saveKeyboardControl(newValue)
                                }
                            }
                        }
                    )
                }
            )

            Spacer(modifier = Modifier.height(80.dp))
        }
        
        // Screen reader settings dialog
        if (showScreenReaderDialog) {
            AlertDialog(
                onDismissRequest = { showScreenReaderDialog = false },
                icon = {},  // Empty icon
                title = { 
                    Text(
                        text = "Enable Screen Reader",
                        fontWeight = if (isHighContrastMode) FontWeight.ExtraBold else FontWeight.Bold
                    ) 
                },
                text = { 
                    Text(
                        text = "TalkBack is an Android system accessibility service that needs to be enabled in system settings. Click \"Go to Settings\" to open the system accessibility settings page where you can enable TalkBack service."
                    ) 
                },
                confirmButton = {
                    Button(
                        onClick = {
                            // Open system accessibility settings
                            openAccessibilitySettings(context)
                            showScreenReaderDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2196F3) // Blue button
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Go to Settings")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { showScreenReaderDialog = false },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE0E0E0) // Gray button
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Cancel", color = Color.Black)
                    }
                },
                containerColor = AccessibilityUtils.getFullyAccessibleColor(
                    normalColor = MaterialTheme.colorScheme.surface,
                    highContrastColor = Color.White
                ),
                titleContentColor = AccessibilityUtils.getFullyAccessibleColor(
                    normalColor = MaterialTheme.colorScheme.onSurface,
                    highContrastColor = Color.Black
                ),
                textContentColor = AccessibilityUtils.getFullyAccessibleColor(
                    normalColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    highContrastColor = Color.Black
                )
            )
        }
        
        // Keyboard control settings dialog
        if (showKeyboardControlDialog) {
            AlertDialog(
                onDismissRequest = { showKeyboardControlDialog = false },
                icon = {},  // Empty icon
                title = { 
                    Text(
                        text = "Enable Keyboard Control",
                        fontWeight = if (isHighContrastMode) FontWeight.ExtraBold else FontWeight.Bold
                    ) 
                },
                text = { 
                    Text(
                        text = "Keyboard control requires Android's accessibility services like Switch Access or Voice Access to be enabled. Click \"Go to Settings\" to open the system accessibility settings page where you can enable these services."
                    ) 
                },
                confirmButton = {
                    Button(
                        onClick = {
                            // Open system accessibility settings
                            openAccessibilitySettings(context)
                            showKeyboardControlDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2196F3) // Blue button
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Go to Settings")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { showKeyboardControlDialog = false },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE0E0E0) // Gray button
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Cancel", color = Color.Black)
                    }
                },
                containerColor = AccessibilityUtils.getFullyAccessibleColor(
                    normalColor = MaterialTheme.colorScheme.surface,
                    highContrastColor = Color.White
                ),
                titleContentColor = AccessibilityUtils.getFullyAccessibleColor(
                    normalColor = MaterialTheme.colorScheme.onSurface,
                    highContrastColor = Color.Black
                ),
                textContentColor = AccessibilityUtils.getFullyAccessibleColor(
                    normalColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    highContrastColor = Color.Black
                )
            )
        }
    }
}

/**
 * Check if TalkBack service is enabled
 */
private fun isTalkBackEnabled(context: Context): Boolean {
    val accessibilityManager = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
    val enabledServices = accessibilityManager.getEnabledAccessibilityServiceList(
        AccessibilityServiceInfo.FEEDBACK_SPOKEN
    )
    
    return enabledServices.any { 
        it.resolveInfo.serviceInfo.packageName.contains("talkback", ignoreCase = true) ||
        it.resolveInfo.serviceInfo.name.contains("talkback", ignoreCase = true)
    }
}

/**
 * Check if keyboard accessibility service is enabled (like Switch Access or Voice Access)
 */
private fun isKeyboardAccessibilityEnabled(context: Context): Boolean {
    val accessibilityManager = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
    val enabledServices = accessibilityManager.getEnabledAccessibilityServiceList(
        AccessibilityServiceInfo.FEEDBACK_GENERIC
    )
    
    return enabledServices.any { 
        it.resolveInfo.serviceInfo.packageName.contains("switchaccess", ignoreCase = true) ||
        it.resolveInfo.serviceInfo.packageName.contains("voiceaccess", ignoreCase = true) ||
        it.resolveInfo.serviceInfo.name.contains("switchaccess", ignoreCase = true) ||
        it.resolveInfo.serviceInfo.name.contains("voiceaccess", ignoreCase = true)
    }
}

/**
 * Open system accessibility settings page
 */
private fun openAccessibilitySettings(context: Context) {
    val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    context.startActivity(intent)
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
            .background(
                AccessibilityUtils.getFullyAccessibleColor(
                    normalColor = Color(0xFFF9FAFB),
                    highContrastColor = Color.White
                )
            )
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
                    .background(
                        // Use getFullyAccessibleColor to support both high contrast and color blind modes
                        AccessibilityUtils.getFullyAccessibleColor(
                            normalColor = Color(0xFFF3F4F6),
                            highContrastColor = Color.Black
                        )
                    )
                    .clickable { onBackClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier.size(20.dp),
                    tint = AccessibilityUtils.getFullyAccessibleColor(
                        normalColor = Color(0xFF6B7280),
                        highContrastColor = Color.White
                    )
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
                color = AccessibilityUtils.getFullyAccessibleColor(
                    normalColor = Color(0xFF1F2937),
                    highContrastColor = Color.Black
                )
            )

            // Placeholder for symmetry
            Spacer(modifier = Modifier.size(32.dp))
        }
    }
}
