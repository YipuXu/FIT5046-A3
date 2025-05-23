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
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.fitlife.R
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import com.example.fitlife.utils.ResourceUtils
import com.example.fitlife.ui.theme.AccessibilityUtils
import com.example.fitlife.ui.components.AccessibleHeading
import com.example.fitlife.ui.components.AccessibleButton
import com.example.fitlife.ui.theme.AccessibilityUtils.highContrastBorder

@Composable
fun SettingsScreen(
    onBackClick: () -> Unit = {},
    onLogout: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onAboutUsClick: () -> Unit = {},
    onHelpFeedbackClick: () -> Unit = {},
    onChangePasswordClick: () -> Unit = {},
    onPrivacyPolicyClick: () -> Unit = {},
    onTermsOfServiceClick: () -> Unit = {},
    onAccessibilityClick: () -> Unit = {}
) {
    val isHighContrastMode = AccessibilityUtils.isHighContrastModeEnabled()
    
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
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top bar
            TopBar(onBackClick = onBackClick)
            
            // Content with scroll
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                
                // Profile
                SettingsItemUnified(
                    icon = ResourceUtils.getResourceId("ic_profile", R.drawable.profile_photo),
                    title = "Profile",
                    onClick = onProfileClick,
                    backgroundColor = AccessibilityUtils.getFullyAccessibleColor(
                        normalColor = Color.White,
                        highContrastColor = Color.White
                    )
                )
                
                // Privacy Policy
                SettingsItemUnified(
                    icon = ResourceUtils.getResourceId("ic_privacy", R.drawable.profile_photo),
                    title = "Privacy Policy",
                    onClick = onPrivacyPolicyClick,
                    backgroundColor = AccessibilityUtils.getFullyAccessibleColor(
                        normalColor = Color.White,
                        highContrastColor = Color.White
                    )
                )
                
                // Terms of Service
                SettingsItemUnified(
                    icon = ResourceUtils.getResourceId("ic_privacy", R.drawable.profile_photo),
                    title = "Terms of Service",
                    onClick = onTermsOfServiceClick,
                    backgroundColor = AccessibilityUtils.getFullyAccessibleColor(
                        normalColor = Color.White,
                        highContrastColor = Color.White
                    )
                )
                
                // Change password
                SettingsItemUnified(
                    icon = R.drawable.profile_photo, // 替换为实际的锁图标
                    title = "Change Password",
                    onClick = onChangePasswordClick,
                    useVectorIcon = true,
                    vectorIcon = Icons.Default.Lock,
                    backgroundColor = AccessibilityUtils.getFullyAccessibleColor(
                        normalColor = Color.White,
                        highContrastColor = Color.White
                    )
                        )
                
                // Accessibility
                SettingsItemUnified(
                    icon = R.drawable.profile_photo, // 占位图标
                    title = "Accessibility",
                    onClick = onAccessibilityClick,
                    useVectorIcon = true,
                    vectorIcon = Icons.Default.Settings,
                    backgroundColor = AccessibilityUtils.getFullyAccessibleColor(
                        normalColor = Color.White,
                        highContrastColor = Color.White
                    )
                )
                
                // Help and feedback
                SettingsItemUnified(
                    icon = ResourceUtils.getResourceId("ic_help", R.drawable.profile_photo),
                    title = "Help & Feedback",
                    onClick = onHelpFeedbackClick,
                    backgroundColor = AccessibilityUtils.getFullyAccessibleColor(
                        normalColor = Color.White,
                        highContrastColor = Color.White
                    )
                )
                
                // About us
                SettingsItemUnified(
                    icon = ResourceUtils.getResourceId("ic_help", R.drawable.profile_photo),
                    title = "About Us",
                    onClick = onAboutUsClick,
                    backgroundColor = AccessibilityUtils.getFullyAccessibleColor(
                        normalColor = Color.White,
                        highContrastColor = Color.White
                    )
                )
                
                Spacer(modifier = Modifier.height(80.dp))
            }
            
            // Logout button in fixed position at bottom
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Button(
                    onClick = onLogout,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .let {
                            if (AccessibilityUtils.isHighContrastModeEnabled()) {
                                it.highContrastBorder(RoundedCornerShape(12.dp))
                            } else {
                                it
                            }
                        },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccessibilityUtils.getFullyAccessibleColor(
                            normalColor = Color(0xFFEF4444), 
                            highContrastColor = Color.Black
                        )
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Logout",
                        fontSize = 16.sp,
                        fontWeight = if (AccessibilityUtils.isHighContrastModeEnabled()) 
                                     FontWeight.ExtraBold else FontWeight.Medium,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun TopBar(onBackClick: () -> Unit) {
    val isHighContrastMode = AccessibilityUtils.isHighContrastModeEnabled()
    
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
            text = "Settings",
            fontSize = 18.sp,
            fontWeight = if (isHighContrastMode) FontWeight.ExtraBold else FontWeight.Bold,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            textAlign = TextAlign.Center,
            color = if (isHighContrastMode) Color.Black else Color(0xFF1F2937)
        )
        
        // Placeholder to maintain symmetry
        Spacer(
            modifier = Modifier
                .size(32.dp)
        )
    }
}

@Composable
private fun SettingsItemUnified(
    icon: Int,
    title: String,
    onClick: () -> Unit,
    backgroundColor: Color = Color.White,
    useVectorIcon: Boolean = false,
    vectorIcon: androidx.compose.ui.graphics.vector.ImageVector = Icons.Default.Settings
) {
    val isHighContrastMode = AccessibilityUtils.isHighContrastModeEnabled()
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .let {
                if (isHighContrastMode) {
                    it.highContrastBorder(RoundedCornerShape(12.dp))
                } else {
                    it
                }
            },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isHighContrastMode) 4.dp else 0.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isHighContrastMode) Color.Black else Color(0xFFF3F4F6)),
                contentAlignment = Alignment.Center
            ) {
                if (useVectorIcon) {
                    Icon(
                        imageVector = vectorIcon,
                        contentDescription = null,
                        tint = if (isHighContrastMode) Color.White else Color(0xFF6B7280),
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                        tint = if (isHighContrastMode) Color.White else Color(0xFF6B7280),
                    modifier = Modifier.size(24.dp)
                )
                }
            }
            
            // Text
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = if (isHighContrastMode) FontWeight.Bold else FontWeight.Medium,
                color = if (isHighContrastMode) Color.Black else Color(0xFF1F2937),
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp)
            )
            
            // Arrow right
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "Open",
                tint = if (isHighContrastMode) Color.Black else Color(0xFF9CA3AF)
            )
        }
    }
} 