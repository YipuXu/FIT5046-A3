package com.example.fitlife.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.fitlife.R
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.zIndex
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.FlowRow
import com.example.fitlife.utils.ResourceUtils
import com.example.fitlife.ui.components.BottomNavBar // 添加导入

@Composable
fun ProfileEditScreen(
    onBackClick: () -> Unit = {},
    initialFitnessTags: List<String> = listOf("Strength Training", "Cardio"),
    onFitnessTagsSelected: (List<String>) -> Unit = {},
    // 添加导航回调
    onNavigateToHome: () -> Unit,
    onNavigateToCalendar: () -> Unit,
    onNavigateToMap: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val density = LocalDensity.current
    var showProfilePhotoDialog by remember { mutableStateOf(false) }
    var showBasicInfoDialog by remember { mutableStateOf(false) }
    var showHeightWeightDialog by remember { mutableStateOf(false) }
    var showFitnessGoalDialog by remember { mutableStateOf(false) }
    var showWorkoutFrequencyDialog by remember { mutableStateOf(false) }
    var showFitnessTagsDialog by remember { mutableStateOf(false) }
    
    // State data
    var heightValue by remember { mutableStateOf("178") }
    var weightValue by remember { mutableStateOf("70") }
    var fitnessGoal by remember { mutableStateOf("Muscle Gain & Fat Loss") }
    var workoutFrequency by remember { mutableStateOf("4-5 Times Weekly") }
    var selectedFitnessTags by remember { mutableStateOf(initialFitnessTags) }
    
    // Fitness goal options
    val fitnessGoalOptions = listOf(
        "Muscle Gain & Fat Loss",
        "Weight & Muscle Gain",
        "Weight & Fat Loss",
        "Maintain Physique",
        "Improve Endurance",
        "Increase Strength"
    )
    
    // Workout frequency options
    val workoutFrequencyOptions = listOf(
        "1-2 Times Weekly",
        "3-4 Times Weekly",
        "4-5 Times Weekly",
        "5-6 Times Weekly",
        "Daily Workout",
        "Adjust Based on Condition"
    )
    
    // Fitness tag options
    val fitnessTagOptions = listOf(
        "Strength Training",
        "Cardio",
        "HIIT",
        "Yoga",
        "Pilates",
        "Functional Training",
        "Outdoor Activities",
        "Group Classes",
        "Running",
        "Swimming",
        "Boxing",
        "Dancing"
    )
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9FAFB))
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 56.dp) // 为底部导航栏留出空间
        ) {
            // Top bar
            TopBar(onBackClick = onBackClick)
            
            // Content with scroll
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f) // 允许内容滚动
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                // User profile card
                UserProfileCard(
                    onAvatarClick = { showProfilePhotoDialog = true },
                    onProfileEditClick = { showBasicInfoDialog = true }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Personal data section
                Text(
                    text = "Personal Data",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1F2937),
                    modifier = Modifier.padding(vertical = 12.dp)
                )
                
                // Height and weight
                DataItemCard(
                    icon = ResourceUtils.getResourceId("ic_scale", R.drawable.profile_photo),
                    title = "Height and Weight",
                    value = "${heightValue}cm / ${weightValue}kg",
                    iconTint = Color(0xFF3B82F6),
                    iconBackground = Color(0xFFE6F0FF),
                    onClick = { showHeightWeightDialog = true }
                )
                
                // Fitness goal
                DataItemCard(
                    icon = ResourceUtils.getResourceId("ic_bar_chart", R.drawable.profile_photo),
                    title = "Fitness Goal",
                    value = fitnessGoal,
                    iconTint = Color(0xFF10B981),
                    iconBackground = Color(0xFFDCFCE7),
                    showDropdownIcon = false,
                    onClick = { showFitnessGoalDialog = true }
                )
                
                // Workout frequency
                DataItemCard(
                    icon = ResourceUtils.getResourceId("ic_clock", R.drawable.profile_photo),
                    title = "Workout Frequency",
                    value = workoutFrequency,
                    iconTint = Color(0xFF9061F9),
                    iconBackground = Color(0xFFF3E8FF),
                    showDropdownIcon = false,
                    onClick = { showWorkoutFrequencyDialog = true }
                )
                
                // Fitness tags
                DataItemCard(
                    icon = ResourceUtils.getResourceId("ic_tag", R.drawable.profile_photo),
                    title = "Fitness Tags",
                    value = selectedFitnessTags.joinToString(", ").ifEmpty { "Not set" }, // Handle empty tags
                    iconTint = Color(0xFFFF6B00),
                    iconBackground = Color(0xFFFFEEE0),
                    showDropdownIcon = false,
                    onClick = { showFitnessTagsDialog = true }
                )
                
                // Add bottom spacing
                Spacer(modifier = Modifier.height(16.dp)) // Bottom padding for scrollable content
            }
        }

        // 底部导航栏放在Box的底部，覆盖在Column之上
        BottomNavBar(
            currentRoute = "profile", // 编辑页高亮Profile图标
            onNavigateToHome = onNavigateToHome,
            onNavigateToCalendar = onNavigateToCalendar,
            onNavigateToMap = onNavigateToMap,
            onNavigateToProfile = onNavigateToProfile, // 点击Profile图标返回ProfileScreen
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
    
    // Profile Photo Dialog
    if (showProfilePhotoDialog) {
        ProfilePhotoDialog(
            onDismiss = { showProfilePhotoDialog = false },
            onUploadPhoto = { 
                // Mock function for photo upload UI
                showProfilePhotoDialog = false 
            }
        )
    }
    
    // Basic Info Edit Dialog
    if (showBasicInfoDialog) {
        BasicInfoEditDialog(
            onDismiss = { showBasicInfoDialog = false },
            onSave = { name, email ->
                // Mock function for saving basic info
                showBasicInfoDialog = false
            }
        )
    }
    
    // Height and Weight Dialog
    if (showHeightWeightDialog) {
        HeightWeightDialog(
            initialHeight = heightValue,
            initialWeight = weightValue,
            onDismiss = { showHeightWeightDialog = false },
            onSave = { height, weight ->
                heightValue = height
                weightValue = weight
                showHeightWeightDialog = false
            }
        )
    }
    
    // Fitness Goal Dialog
    if (showFitnessGoalDialog) {
        OptionsDialog(
            title = "Choose Fitness Goal",
            options = fitnessGoalOptions,
            selectedOption = fitnessGoal,
            iconTint = Color(0xFF10B981),
            onOptionSelected = { option ->
                fitnessGoal = option
                showFitnessGoalDialog = false
            },
            onDismiss = { showFitnessGoalDialog = false }
        )
    }
    
    // Workout Frequency Dialog
    if (showWorkoutFrequencyDialog) {
        OptionsDialog(
            title = "Select Workout Frequency",
            options = workoutFrequencyOptions,
            selectedOption = workoutFrequency,
            iconTint = Color(0xFF9061F9),
            onOptionSelected = { option ->
                workoutFrequency = option
                showWorkoutFrequencyDialog = false
            },
            onDismiss = { showWorkoutFrequencyDialog = false }
        )
    }
    
    // Fitness tags selection dialog
    if (showFitnessTagsDialog) {
        MultiSelectDialog(
            title = "Select Fitness Tags",
            options = fitnessTagOptions,
            selectedOptions = selectedFitnessTags,
            iconTint = Color(0xFFFF6B00),
            onConfirm = { selected ->
                // Print logs for debugging
                println("Fitness tags updated, selected: ${selected.joinToString()}")
                // Update local state
                selectedFitnessTags = selected
                // Call callback to notify parent component when tags are updated
                onFitnessTagsSelected(selected)
                showFitnessTagsDialog = false
            },
            onDismiss = { 
                println("Cancelled fitness tag selection")
                showFitnessTagsDialog = false 
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
            text = "Profile",
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
private fun UserProfileCard(
    onAvatarClick: () -> Unit,
    onProfileEditClick: () -> Unit
) {
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
                .clickable { onProfileEditClick() }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 外层布局包含头像和加号
            Box(
                modifier = Modifier
                    .width(65.dp)
                    .height(65.dp)
            ) {
                // 头像部分
                Image(
                    painter = painterResource(id = R.drawable.profile_photo),
                    contentDescription = "Profile Photo",
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .clickable { onAvatarClick() },
                    contentScale = ContentScale.Crop
                )
                
                // 加号按钮 - 使用绝对定位
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .offset(x = 42.dp, y = 42.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF3B82F6))
                        .clickable { onAvatarClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Upload photo",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
            
            // Name and email section
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            ) {
                Text(
                    text = "Xiao Ming",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF1F2937)
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "xiaoming@example.com",
                        fontSize = 14.sp,
                        color = Color(0xFF6B7280)
                    )
                }
            }
            
            // Arrow right
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "Edit",
                tint = Color(0xFF9CA3AF),
                modifier = Modifier.clickable { onProfileEditClick() }
            )
        }
    }
}

@Composable
private fun NumberStepper(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    unit: String,
    iconTint: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color(0xFF6B7280),
            modifier = Modifier.width(40.dp)
        )
        
        Box(
            modifier = Modifier.weight(1f)
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = { newValue ->
                    if (newValue.isEmpty() || newValue.matches(Regex("^\\d{0,3}$"))) {
                        onValueChange(newValue)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = iconTint,
                    cursorColor = iconTint
                ),
                singleLine = true,
                trailingIcon = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(
                            text = unit,
                            fontSize = 14.sp,
                            color = Color(0xFF6B7280),
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFFF3F4F6))
                                    .clickable {
                                        val currentValue = value.toIntOrNull() ?: 0
                                        if (currentValue < 999) {
                                            onValueChange((currentValue + 1).toString())
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowRight,
                                    contentDescription = "Increase",
                                    modifier = Modifier
                                        .size(16.dp)
                                        .rotate(270f),
                                    tint = Color(0xFF6B7280)
                                )
                            }
                            
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFFF3F4F6))
                                    .clickable {
                                        val currentValue = value.toIntOrNull() ?: 0
                                        if (currentValue > 0) {
                                            onValueChange((currentValue - 1).toString())
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowRight,
                                    contentDescription = "Decrease",
                                    modifier = Modifier
                                        .size(16.dp)
                                        .rotate(90f),
                                    tint = Color(0xFF6B7280)
                                )
                            }
                        }
                    }
                }
            )
        }
    }
}

@Composable
private fun DataItemCard(
    icon: Int,
    title: String,
    value: String,
    iconTint: Color,
    iconBackground: Color,
    showDropdownIcon: Boolean = false,
    onClick: () -> Unit
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
                    text = value,
                    fontSize = 14.sp,
                    color = Color(0xFF6B7280),
                    modifier = Modifier.padding(top = 2.dp),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            // Right icon
            if (showDropdownIcon) {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Change",
                    tint = Color(0xFF9CA3AF)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "Edit",
                    tint = Color(0xFF9CA3AF)
                )
            }
        }
    }
}

@Composable
private fun PrivacySettingItem(
    icon: Int,
    title: String,
    value: String,
    options: List<String>,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
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
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = true }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icon
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFE6F0FF)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = icon),
                        contentDescription = null,
                        tint = Color(0xFF3B82F6),
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                // Title and current value
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
                        text = value,
                        fontSize = 14.sp,
                        color = Color(0xFF6B7280),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
                
                // Dropdown indicator
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Change visibility",
                    tint = Color(0xFF9CA3AF)
                )
            }
            
            // Dropdown menu
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .width(200.dp)
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onOptionSelected(option)
                            expanded = false
                        },
                        leadingIcon = {
                            if (option == value) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color(0xFF3B82F6)
                                )
                            }
                        }
                    )
                }
            }
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
                    text = "Enabled",
                    fontSize = 14.sp,
                    color = Color(0xFF10B981),
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
            
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
private fun ProfilePhotoDialog(
    onDismiss: () -> Unit,
    onUploadPhoto: () -> Unit
) {
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
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Large profile photo
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFE6F0FF)),
                    contentAlignment = Alignment.Center
                ) {
                    val hasProfilePhoto = true // 此处可替换为实际逻辑以检查是否有照片
                    
                    if (hasProfilePhoto) {
                        Image(
                            painter = painterResource(id = R.drawable.profile_photo),
                            contentDescription = "Profile Photo",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        // Fallback if image cannot be loaded
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Profile Photo",
                            modifier = Modifier.size(64.dp),
                            tint = Color(0xFF3B82F6)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Upload button
                Button(
                    onClick = onUploadPhoto,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF3B82F6)
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add, // 使用Add图标替代Upload
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Upload New Photo")
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Close button
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Close", color = Color(0xFF6B7280))
                }
            }
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

@Composable
private fun BasicInfoEditDialog(
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var name by remember { mutableStateOf("Xiao Ming") }
    var email by remember { mutableStateOf("xiaoming@example.com") }

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
                    text = "Edit Basic Information",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Name field
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Email field
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
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
                        onClick = { onSave(name, email) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF3B82F6)
                        )
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}

@Composable
private fun HeightWeightDialog(
    initialHeight: String,
    initialWeight: String,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var height by remember { mutableStateOf(initialHeight) }
    var weight by remember { mutableStateOf(initialWeight) }
    
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
                    text = "Height and Weight",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Height input with stepper
                NumberStepperField(
                    value = height,
                    onValueChange = { height = it },
                    label = "Height",
                    unit = "cm",
                    iconTint = Color(0xFF3B82F6)
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Weight input with stepper
                NumberStepperField(
                    value = weight,
                    onValueChange = { weight = it },
                    label = "Weight",
                    unit = "kg",
                    iconTint = Color(0xFF3B82F6)
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
                        onClick = { onSave(height, weight) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF3B82F6)
                        )
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}

@Composable
private fun NumberStepperField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    unit: String,
    iconTint: Color
) {
    OutlinedTextField(
        value = value,
        onValueChange = { newValue ->
            if (newValue.isEmpty() || newValue.matches(Regex("^\\d{0,3}$"))) {
                onValueChange(newValue)
            }
        },
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = iconTint,
            cursorColor = iconTint
        ),
        singleLine = true,
        trailingIcon = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Text(
                    text = unit,
                    fontSize = 14.sp,
                    color = Color(0xFF6B7280),
                    modifier = Modifier.padding(end = 8.dp)
                )
                
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFFF3F4F6))
                            .clickable {
                                val currentValue = value.toIntOrNull() ?: 0
                                if (currentValue < 999) {
                                    onValueChange((currentValue + 1).toString())
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = "Increase",
                            modifier = Modifier
                                .size(16.dp)
                                .rotate(270f),
                            tint = Color(0xFF6B7280)
                        )
                    }
                    
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFFF3F4F6))
                            .clickable {
                                val currentValue = value.toIntOrNull() ?: 0
                                if (currentValue > 0) {
                                    onValueChange((currentValue - 1).toString())
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = "Decrease",
                            modifier = Modifier
                                .size(16.dp)
                                .rotate(90f),
                            tint = Color(0xFF6B7280)
                        )
                    }
                }
            }
        }
    )
}

@Composable
private fun OptionsDialog(
    title: String,
    options: List<String>,
    selectedOption: String,
    iconTint: Color,
    onOptionSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
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
                    .padding(top = 24.dp, start = 24.dp, end = 24.dp, bottom = 16.dp)
            ) {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    options.forEach { option ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onOptionSelected(option) }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (option == selectedOption) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = iconTint,
                                    modifier = Modifier
                                        .size(24.dp)
                                        .padding(end = 4.dp)
                                )
                            } else {
                                Spacer(modifier = Modifier.width(28.dp))
                            }
                            
                            Text(
                                text = option,
                                fontSize = 16.sp,
                                color = if (option == selectedOption) iconTint else Color(0xFF1F2937)
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Cancel")
                }
            }
        }
    }
}

@Composable
private fun MultiSelectDialog(
    title: String,
    options: List<String>,
    selectedOptions: List<String>,
    iconTint: Color,
    onConfirm: (List<String>) -> Unit,
    onDismiss: () -> Unit
) {
    // 使用mutableStateListOf来创建可变状态列表以支持UI更新
    val tempSelected = remember { mutableStateListOf<String>() }
    // 最大选择数量限制为2个
    val maxSelections = 2
    // 用于显示最大选择限制的消息
    var showMaxSelectionsMessage by remember { mutableStateOf(false) }
    
    // 初始化选中项
    LaunchedEffect(selectedOptions) {
        tempSelected.clear()
        // 确保初始选择不超过最大限制
        tempSelected.addAll(selectedOptions.take(maxSelections))
    }
    
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
                    .padding(top = 24.dp, start = 24.dp, end = 24.dp, bottom = 16.dp)
            ) {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // 显示最大选择限制提示
                Text(
                    text = "Select up to ${maxSelections} tags",
                    fontSize = 14.sp,
                    color = Color(0xFF6B7280)
                )
                
                // 如果显示选择限制消息，则显示提示
                if (showMaxSelectionsMessage) {
                    Text(
                        text = "You can select maximum ${maxSelections} tags",
                        fontSize = 14.sp,
                        color = Color(0xFFEF4444),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState())
                ) {
                    options.forEach { option ->
                        val isSelected = tempSelected.contains(option)
                        val isDisabled = !isSelected && tempSelected.size >= maxSelections
                        
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(
                                    enabled = !isDisabled || isSelected,
                                    onClick = {
                                        if (isSelected) {
                                            tempSelected.remove(option)
                                            showMaxSelectionsMessage = false
                                        } else if (tempSelected.size < maxSelections) {
                                            tempSelected.add(option)
                                            showMaxSelectionsMessage = false
                                        } else {
                                            // 显示最大选择限制消息
                                            showMaxSelectionsMessage = true
                                        }
                                    }
                                )
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(
                                        when {
                                            isSelected -> iconTint
                                            isDisabled -> Color(0xFFE5E7EB).copy(alpha = 0.5f)
                                            else -> Color(0xFFE5E7EB)
                                        }
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = when {
                                            isSelected -> iconTint
                                            isDisabled -> Color(0xFFD1D5DB).copy(alpha = 0.5f)
                                            else -> Color(0xFFD1D5DB)
                                        },
                                        shape = RoundedCornerShape(4.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.width(12.dp))
                            
                            Text(
                                text = option,
                                fontSize = 16.sp,
                                color = when {
                                    isSelected -> Color(0xFF1F2937)
                                    isDisabled -> Color(0xFF6B7280).copy(alpha = 0.5f)
                                    else -> Color(0xFF1F2937)
                                }
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
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
                        onClick = { onConfirm(tempSelected.toList()) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = iconTint
                        )
                    ) {
                        Text("Confirm")
                    }
                }
            }
        }
    }
} 