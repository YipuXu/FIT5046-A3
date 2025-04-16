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

@Composable
fun SettingsScreen(
    onBackClick: () -> Unit = {},
    onLogout: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    var showPasswordDialog by remember { mutableStateOf(false) }
    
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
                // Profile
                SettingsItem(
                    icon = ResourceUtils.getResourceId("ic_profile", R.drawable.profile_photo),
                    title = "Profile",
                    onClick = onProfileClick
                )
                
                // Privacy settings
                SettingsItem(
                    icon = ResourceUtils.getResourceId("ic_privacy", R.drawable.profile_photo),
                    title = "隐私与条例"
                )
                
                // Change password
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
                            .clickable { showPasswordDialog = true }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFFFECEC)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = Color(0xFFEF4444),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        
                        Text(
                            text = "Change Password",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF1F2937),
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 12.dp)
                        )
                        
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = "Change",
                            tint = Color(0xFF9CA3AF)
                        )
                    }
                }
                
                // Help and feedback
                SettingsItem(
                    icon = ResourceUtils.getResourceId("ic_help", R.drawable.profile_photo),
                    title = "Help & Feedback"
                )
                
                // About us
                SettingsItem(
                    icon = ResourceUtils.getResourceId("ic_help", R.drawable.profile_photo),
                    title = "About Us"
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
                        text = "Logout",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
            }
        }
    }
    
    // Password Change Dialog
    if (showPasswordDialog) {
        PasswordChangeDialog(
            onDismiss = { showPasswordDialog = false },
            onConfirm = { _, _ -> 
                // Mock function for password change UI
                showPasswordDialog = false 
            }
        )
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
                contentDescription = "Back",
                modifier = Modifier.size(20.dp),
                tint = Color(0xFF6B7280)
            )
        }

        // Title
        Text(
            text = "Settings",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            textAlign = TextAlign.Center,
            color = Color(0xFF1F2937)
        )
        
        // Placeholder to maintain symmetry
        Spacer(
            modifier = Modifier
                .size(32.dp)
        )
    }
}

@Composable
private fun SettingsItem(
    icon: Int,
    title: String,
    onClick: () -> Unit = {}
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
                .clickable(onClick = onClick)
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
            
            // Arrow right
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "Open",
                tint = Color(0xFF9CA3AF)
            )
        }
    }
}

@Composable
private fun PasswordChangeDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = "Change Password",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Current password
                OutlinedTextField(
                    value = currentPassword,
                    onValueChange = { currentPassword = it },
                    label = { Text("Current Password") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // New password
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { 
                        newPassword = it
                        // Reset error when typing
                        if (passwordError.isNotEmpty()) passwordError = ""
                    },
                    label = { Text("New Password") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Confirm password
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { 
                        confirmPassword = it 
                        // Reset error when typing
                        if (passwordError.isNotEmpty()) passwordError = ""
                    },
                    label = { Text("Confirm New Password") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = passwordError.isNotEmpty(),
                    supportingText = {
                        if (passwordError.isNotEmpty()) {
                            Text(
                                text = passwordError,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onDismiss
                    ) {
                        Text("Cancel")
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Button(
                        onClick = {
                            if (newPassword.length < 6) {
                                passwordError = "Password must be at least 6 characters"
                            } else if (newPassword != confirmPassword) {
                                passwordError = "Passwords don't match"
                            } else {
                                onConfirm(currentPassword, newPassword)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF3B82F6)
                        )
                    ) {
                        Text("Change Password")
                    }
                }
            }
        }
    }
} 