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
    onBackClick: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9FAFB))
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
                // User profile card
                UserProfileCard()
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Personal data section
                Text(
                    text = "个人数据",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1F2937),
                    modifier = Modifier.padding(vertical = 12.dp)
                )
                
                // Height and weight
                PersonalDataItem(
                    icon = R.drawable.ic_weight_scale,
                    title = "身高体重",
                    description = "178cm / 70kg",
                    iconTint = Color(0xFF3B82F6),
                    iconBackground = Color(0xFFE6F0FF)
                )
                
                // Fitness goal
                PersonalDataItem(
                    icon = R.drawable.ic_bar_chart,
                    title = "健身目标",
                    description = "增肌减脂",
                    iconTint = Color(0xFF10B981),
                    iconBackground = Color(0xFFDCFCE7)
                )
                
                // Workout frequency
                PersonalDataItem(
                    icon = R.drawable.ic_clock,
                    title = "健身频率",
                    description = "每周4-5次",
                    iconTint = Color(0xFF9061F9),
                    iconBackground = Color(0xFFF3E8FF)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // App settings section
                Text(
                    text = "应用设置",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1F2937),
                    modifier = Modifier.padding(vertical = 12.dp)
                )
                
                // Notification settings
                SettingsItem(
                    icon = R.drawable.ic_notification,
                    title = "通知设置",
                    isEnabled = true
                )
                
                // Privacy settings
                SettingsItem(
                    icon = R.drawable.ic_privacy,
                    title = "隐私设置"
                )
                
                // Help and feedback
                SettingsItem(
                    icon = R.drawable.ic_help,
                    title = "帮助与反馈"
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
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFEF4444)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "退出登录",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun TopBar(onBackClick: () -> Unit) {
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
                contentDescription = "返回",
                modifier = Modifier.size(20.dp),
                tint = Color(0xFF6B7280)
            )
        }

        // Title
        Text(
            text = "个人信息",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            textAlign = TextAlign.Center,
            color = Color(0xFF1F2937)
        )

        // Settings button
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(Color(0xFFF3F4F6))
                .clickable { /* 打开设置 */ },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "菜单",
                modifier = Modifier.size(20.dp),
                tint = Color(0xFF6B7280)
            )
        }
    }
}

@Composable
private fun UserProfileCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clickable { /* Open profile edit */ },
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile photo
            Image(
                painter = painterResource(id = R.drawable.profile_photo),
                contentDescription = "头像",
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
            )
            
            // Name and email
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            ) {
                Text(
                    text = "小明",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF1F2937)
                )
                
                Text(
                    text = "xiaoming@example.com",
                    fontSize = 14.sp,
                    color = Color(0xFF6B7280)
                )
            }
            
            // Arrow right
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "编辑",
                tint = Color(0xFF9CA3AF)
            )
        }
    }
}

@Composable
private fun PersonalDataItem(
    icon: Int,
    title: String,
    description: String,
    iconTint: Color,
    iconBackground: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { /* Open edit page */ }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(iconBackground),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            // Text information
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp)
            ) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF1F2937)
                )
                
                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = Color(0xFF6B7280),
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
            
            // Arrow right
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "编辑",
                tint = Color(0xFF9CA3AF)
            )
        }
    }
}

@Composable
private fun SettingsItem(
    icon: Int,
    title: String,
    isEnabled: Boolean = false
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { /* Open settings page */ }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF3F4F6)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    tint = Color(0xFF6B7280),
                    modifier = Modifier.size(24.dp)
                )
            }
            
            // Text
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1F2937),
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp)
            )
            
            // Status or arrow
            if (isEnabled) {
                Text(
                    text = "开启",
                    fontSize = 14.sp,
                    color = Color(0xFF10B981),
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
            
            // Arrow right
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "打开",
                tint = Color(0xFF9CA3AF)
            )
        }
    }
} 