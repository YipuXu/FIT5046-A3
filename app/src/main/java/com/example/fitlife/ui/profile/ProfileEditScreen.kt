package com.example.fitlife.ui.profile

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.*
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
import com.example.fitlife.R
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding

@Composable
fun ProfileEditScreen(
    onBackClick: () -> Unit,
    onMenuClick: () -> Unit,
    // Add navigation parameters if needed
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9FAFB))
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        TopBar(
            onBackClick = onBackClick,
            onMenuClick = onMenuClick,
            title = "Personal Information"
        )
        
        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            // Personal data section
            item {
                SectionHeader(title = "Personal Data")
                
                ProfileDataItem(
                    icon = R.drawable.ic_body,
                    title = "Height & Weight",
                    subtitle = "180cm, 75kg",
                    onClick = { /* Navigate to height/weight edit */ }
                )
                
                ProfileDataItem(
                    icon = R.drawable.ic_target,
                    title = "Build Muscle",
                    subtitle = "Goal",
                    onClick = { /* Navigate to goal edit */ }
                )
                
                ProfileDataItem(
                    icon = R.drawable.ic_calendar,
                    title = "Workout Frequency",
                    subtitle = "4-5 times per week",
                    onClick = { /* Navigate to frequency edit */ }
                )
                
                Spacer(modifier = Modifier.height(24.dp))
            }
            
            // App settings section
            item {
                SectionHeader(title = "App Settings")
                
                SettingsItem(
                    icon = R.drawable.ic_notification,
                    title = "Notification Settings",
                    isSwitch = true,
                    switchValue = true,
                    onSwitchChange = { /* Update notification settings */ }
                )
                
                SettingsItem(
                    icon = R.drawable.ic_privacy,
                    title = "Privacy Settings",
                    onClick = { /* Navigate to privacy settings */ }
                )
                
                SettingsItem(
                    icon = R.drawable.ic_help,
                    title = "Help & Feedback",
                    onClick = { /* Navigate to help & feedback */ }
                )
                
                Spacer(modifier = Modifier.height(24.dp))
            }
            
            // Logout button
            item {
                Button(
                    onClick = { /* Logout logic */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF3B82F6)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Logout",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun TopBar(
    onBackClick: () -> Unit,
    onMenuClick: () -> Unit,
    title: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
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
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            textAlign = TextAlign.Center,
            color = Color(0xFF1F2937)
        )

        // Menu button
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(Color(0xFFF3F4F6))
                .clickable { onMenuClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Menu",
                modifier = Modifier.size(20.dp),
                tint = Color(0xFF6B7280)
            )
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold,
        color = Color(0xFF1F2937),
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
    )
}

@Composable
fun ProfileDataItem(
    icon: Int,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = Color(0xFF3B82F6)
        )
        
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                color = Color(0xFF1F2937)
            )
            
            Text(
                text = subtitle,
                fontSize = 14.sp,
                color = Color(0xFF6B7280),
                modifier = Modifier.padding(top = 2.dp)
            )
        }
        
        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = "Open",
            tint = Color(0xFF9CA3AF)
        )
    }
}

@Composable
fun SettingsItem(
    icon: Int,
    title: String,
    isSwitch: Boolean = false,
    switchValue: Boolean = false,
    onSwitchChange: ((Boolean) -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .run {
                if (onClick != null) {
                    clickable { onClick() }
                } else {
                    this
                }
            }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = Color(0xFF3B82F6)
        )
        
        Text(
            text = title,
            fontSize = 16.sp,
            color = Color(0xFF1F2937),
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
        )
        
        if (isSwitch) {
            Switch(
                checked = switchValue,
                onCheckedChange = { onSwitchChange?.invoke(it) },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color(0xFF3B82F6),
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color(0xFFD1D5DB)
                )
            )
        } else {
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "Open",
                tint = Color(0xFF9CA3AF)
            )
        }
    }
} 