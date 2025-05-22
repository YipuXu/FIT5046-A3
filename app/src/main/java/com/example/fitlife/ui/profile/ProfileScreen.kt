package com.example.fitlife.ui.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitlife.R
import com.example.fitlife.ui.components.BottomNavBar
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.FlowRow
import com.example.fitlife.utils.ResourceUtils
import com.example.fitlife.MyApplication
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import com.example.fitlife.data.model.Workout
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import android.net.Uri
import coil.compose.AsyncImage
import com.example.fitlife.data.model.User
import java.io.File
import com.example.fitlife.ui.theme.AccessibilityUtils
import com.example.fitlife.ui.components.AccessibleHeading
import androidx.compose.ui.text.TextStyle
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBackClick: () -> Unit,
    onViewAllHistory: () -> Unit,
    onEditProfileClick: (List<String>) -> Unit,
    onSettingsClick: () -> Unit,
    onAICoachClick: () -> Unit,
    onNavigateToMap: () -> Unit,
    selectedFitnessTags: List<String>,
    onFitnessTagsUpdated: (List<String>) -> Unit,
    plansDoneCount: Int = 8
) {
    val context = LocalContext.current
    val workoutDao = remember { (context.applicationContext as MyApplication).database.workoutDao() }
    val userDao = remember { (context.applicationContext as MyApplication).database.userDao() }
    val isHighContrastMode = AccessibilityUtils.isHighContrastModeEnabled()
    
    // 从数据库获取用户信息
    val userFlow = remember { userDao.getUserById(0) }
    val user by userFlow.collectAsState(initial = null)
    
    // 从数据库获取最近的锻炼记录
    val latestWorkouts by workoutDao.getLatestTwoWorkouts().collectAsState(initial = emptyList())
    
    // 获取不同日期的健身记录数量
    val workoutDaysCount by workoutDao.getUniqueWorkoutDaysCount().collectAsState(initial = 0)
    
    // 获取所有训练日期并计算连续天数
    val allWorkoutDates by workoutDao.getAllWorkoutDatesDesc().collectAsState(initial = emptyList())
    val streakDays = remember(allWorkoutDates) {
        calculateStreakDays(allWorkoutDates)
    }

    // 计算健身标签列表（从逗号分隔的字符串转换为列表）
    val fitnessTags = remember(user) {
        user?.fitnessTags?.split(",") ?: listOf("Strength Training", "Cardio")
    }

    // State for the workout detail dialog
    var showDialog by remember { mutableStateOf(false) }
    var selectedWorkout by remember { mutableStateOf<Workout?>(null) }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isHighContrastMode) Color.White else Color(0xFFF9FAFB))
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 个人资料内容
            ProfileContent(
                onViewAllHistory = onViewAllHistory,
                onEditProfileClick = { onEditProfileClick(fitnessTags) },
                onSettingsClick = onSettingsClick,
                onAICoachClick = onAICoachClick,
                selectedFitnessTags = fitnessTags,
                user = user,
                latestWorkouts = latestWorkouts,
                onWorkoutClick = { workout ->
                    selectedWorkout = workout
                    showDialog = true
                },
                plansDoneCount = plansDoneCount,
                workoutDaysCount = workoutDaysCount, // 传递不同日期的健身记录数量
                streakDays = streakDays // 传递连续训练天数
            )
        }
        
        // 底部导航
        BottomNavBar(
            currentRoute = "profile",
            onNavigateToHome = { /* Empty for now */ },
            onNavigateToCalendar = { /* Empty for now */ },
            onNavigateToMap = onNavigateToMap,
            onNavigateToProfile = {},
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }

    // Workout detail dialog
    if (showDialog && selectedWorkout != null) {
        val workout = selectedWorkout // Create a local non-nullable variable
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { 
                AccessibilityUtils.AccessibleText(
                    text = workout!!.type,
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                Column {
                    // 使用AccessibleText组件
                    val baseStyle = MaterialTheme.typography.bodyMedium
                    
                    AccessibilityUtils.AccessibleText(
                        text = "Date: ${workout!!.date}",
                        style = baseStyle
                    )
                    AccessibilityUtils.AccessibleText(
                        text = "Time: ${workout!!.time}",
                        style = baseStyle
                    )
                    AccessibilityUtils.AccessibleText(
                        text = "Duration: ${workout!!.duration} min",
                        style = baseStyle
                    )
                    AccessibilityUtils.AccessibleText(
                        text = "Calories: ${workout!!.calories} kcal",
                        style = baseStyle
                    )
                    if (workout!!.notes.isNotBlank()) {
                        AccessibilityUtils.AccessibleText(
                            text = "Notes: ${workout!!.notes}",
                            style = baseStyle
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showDialog = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccessibilityUtils.getAccessibleColor(
                            normalColor = Color(0xFF3B82F6),
                            highContrastColor = Color.Black
                        )
                    ),
                    modifier = Modifier.let { 
                        if (AccessibilityUtils.isHighContrastModeEnabled()) {
                            it.border(2.dp, Color.White, RoundedCornerShape(4.dp))
                        } else {
                            it
                        }
                    }
                ) {
                    Text(
                        "Close",
                        fontWeight = if (AccessibilityUtils.isHighContrastModeEnabled()) 
                                     FontWeight.Bold else FontWeight.Normal,
                        color = Color.White
                    )
                }
            },
            containerColor = AccessibilityUtils.getAccessibleColor(
                normalColor = MaterialTheme.colorScheme.surface,
                highContrastColor = Color.White
            ),
            titleContentColor = AccessibilityUtils.getAccessibleColor(
                normalColor = MaterialTheme.colorScheme.onSurface,
                highContrastColor = Color.Black
            ),
            textContentColor = AccessibilityUtils.getAccessibleColor(
                normalColor = MaterialTheme.colorScheme.onSurfaceVariant,
                highContrastColor = Color.Black
            )
        )
    }
}

// 计算连续训练天数的函数
private fun calculateStreakDays(dates: List<String>): Int {
    if (dates.isEmpty()) return 0
    
    try {
        // 尝试解析日期，忽略格式不正确的日期
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val validDates = mutableListOf<LocalDate>()
        
        for (dateStr in dates) {
            try {
                if (dateStr != "Select Date") {
                    validDates.add(LocalDate.parse(dateStr, formatter))
                }
            } catch (e: Exception) {
                // 忽略无法解析的日期
                continue
            }
        }
        
        if (validDates.isEmpty()) return 0
        
        // 按日期降序排序（最新日期在前）
        val sortedDates = validDates.sortedDescending()
        
        // 从最新的日期开始检查连续天数
        var currentStreak = 1
        var previousDate = sortedDates[0]
        
        for (i in 1 until sortedDates.size) {
            val currentDate = sortedDates[i]
            val daysBetween = ChronoUnit.DAYS.between(currentDate, previousDate)
            
            if (daysBetween == 1L) {
                // 两个日期相差1天，连续
                currentStreak++
                previousDate = currentDate
            } else {
                // 连续中断
                break
            }
        }
        
        return currentStreak
    } catch (e: Exception) {
        e.printStackTrace()
        return 0
    }
}

@Composable
private fun ProfileContent(
    onViewAllHistory: () -> Unit,
    onEditProfileClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onAICoachClick: () -> Unit,
    selectedFitnessTags: List<String>,
    user: User?,
    latestWorkouts: List<Workout>,
    onWorkoutClick: (Workout) -> Unit,
    plansDoneCount: Int = 8,
    workoutDaysCount: Int = 0,
    streakDays: Int = 0
) {
    val isHighContrastMode = AccessibilityUtils.isHighContrastModeEnabled()
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 80.dp) // 为底部导航栏留出空间
    ) {
        // 顶部栏 (标题和设置按钮)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 添加左侧Spacer以平衡右侧设置按钮
            Spacer(modifier = Modifier.width(32.dp))

            // 标题
            Text(
                text = "Profile",
                fontSize = 18.sp, 
                fontWeight = if (isHighContrastMode) FontWeight.ExtraBold else FontWeight.Bold,
                modifier = Modifier
                    .weight(1f), // 占据中间可用空间
                textAlign = TextAlign.Center, // 文本在其空间内居中
                color = if (isHighContrastMode) Color.Black else Color(0xFF1F2937)
            )

            // 设置按钮
            Box(
                modifier = Modifier
                    .size(32.dp) // 保持尺寸与左侧Spacer一致
                    .clip(CircleShape)
                    .background(if (isHighContrastMode) Color.Black else Color(0xFFF3F4F6))
                    .clickable { onSettingsClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    modifier = Modifier.size(20.dp),
                    tint = if (isHighContrastMode) Color.White else Color(0xFF6B7280)
                )
            }
        }
        
        // 用户信息卡片
        UserInfoCard(
            onEditClick = onEditProfileClick,
            fitnessTags = selectedFitnessTags,
            user = user,
            plansDoneCount = plansDoneCount,
            workoutDaysCount = workoutDaysCount,
            streakDays = streakDays, // 传递实际的连续训练天数
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        // 最近记录部分
        RecentHistorySection(
            onViewAll = onViewAllHistory,
            latestWorkouts = latestWorkouts,
            onWorkoutClick = onWorkoutClick
        )

        // AI教练部分
        AICoachSection(onStartChat = onAICoachClick)

        // 占位符 - 修复weight()调用问题
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = true)
        )
    }
}

@Composable
private fun UserInfoCard(
    onEditClick: () -> Unit,
    fitnessTags: List<String>,
    user: User?,
    plansDoneCount: Int = 8,
    workoutDaysCount: Int = 0,
    streakDays: Int = 0,
    modifier: Modifier = Modifier
) {
    // 使用用户数据或默认值
    val username = user?.name ?: "Xiao Ming"
    val context = LocalContext.current
    
    // 修改头像URI的处理方式
    val avatarUri = remember(user) {
        user?.avatarUri?.let { uriString ->
            try {
                // 尝试解析URI
                val uri = Uri.parse(uriString)
                
                // 检查URI是否指向内部存储
                if (uri.scheme == "file" && uri.path?.contains(context.filesDir.path) == true) {
                    // 检查文件是否存在
                    val file = File(uri.path!!)
                    if (file.exists()) {
                        uri
                    } else {
                        null
                    }
                } else {
                    // 对于content://或其他URI，直接返回
                    uri
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
    
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = AccessibilityUtils.getAccessibleColor(
                normalColor = Color(0xFF3B82F6),
                highContrastColor = Color.White
        )
        ),
        border = if (AccessibilityUtils.isHighContrastModeEnabled()) {
            BorderStroke(2.dp, Color.Black)
        } else {
            null
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // User avatar and basic info
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar - 使用AsyncImage加载从数据库获取的头像
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE5E7EB))
                        .let {
                            if (AccessibilityUtils.isHighContrastModeEnabled()) {
                                it.border(2.dp, Color.Black, CircleShape)
                            } else {
                                it
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (avatarUri != null) {
                        AsyncImage(
                            model = avatarUri,
                            contentDescription = "Profile Photo",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                            error = painterResource(id = R.drawable.profile_photo),
                            placeholder = painterResource(id = R.drawable.profile_photo)
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.profile_photo),
                            contentDescription = "Profile Photo",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                // Username and edit button in one row
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 16.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = username,
                            color = AccessibilityUtils.getAccessibleColor(
                                normalColor = Color.White,
                                highContrastColor = Color.Black
                            ),
                            fontSize = 18.sp,
                            fontWeight = if (AccessibilityUtils.isHighContrastModeEnabled()) 
                                        FontWeight.ExtraBold else FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )

                        // Smaller edit button
                        OutlinedButton(
                            onClick = onEditClick,
                            modifier = Modifier
                                .height(28.dp)
                                .width(32.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = AccessibilityUtils.getAccessibleColor(
                                    normalColor = Color.White,
                                    highContrastColor = Color.Black
                                )
                            ),
                            border = BorderStroke(
                                width = if (AccessibilityUtils.isHighContrastModeEnabled()) 2.dp else 1.dp, 
                                color = if (AccessibilityUtils.isHighContrastModeEnabled()) 
                                       Color.Black else Color.White.copy(alpha = 0.5f)
                            ),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)
                        ) {
                            Text(
                                text = "Edit",
                                fontSize = 10.sp,
                                fontWeight = if (AccessibilityUtils.isHighContrastModeEnabled()) 
                                           FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }

                    // Fitness tags below username and edit button
                    if (fitnessTags.isNotEmpty()) {
                        LazyRow(
                            modifier = Modifier.padding(top = 4.dp),
                            horizontalArrangement = Arrangement.Start,
                            contentPadding = PaddingValues(end = 8.dp)
                        ) {
                            items(fitnessTags) { tag ->
                                FitnessTag(
                                    text = tag,
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                            }
                        }
                    }
                }
            }
            
            // Statistics
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, start = 0.dp, end = 0.dp)
            ) {
                // Training days
                StatItem(
                    count = workoutDaysCount.toString(), 
                    label = "Workout Days",
                    modifier = Modifier.weight(1f),
                    alignment = Alignment.CenterHorizontally
                )
                
                // Vertical line
                Box(
                    modifier = Modifier
                        .height(30.dp)
                        .width(1.dp)
                        .background(
                            AccessibilityUtils.getAccessibleColor(
                                normalColor = Color.White.copy(alpha = 0.3f),
                                highContrastColor = Color.Black
                            )
                        )
                )
                
                // 连续天数 - 使用计算的值
                StatItem(
                    count = streakDays.toString(), 
                    label = "Streak Days",
                    modifier = Modifier.weight(1f),
                    alignment = Alignment.CenterHorizontally
                )
                
                // Vertical line
                Box(
                    modifier = Modifier
                        .height(30.dp)
                        .width(1.dp)
                        .background(
                            AccessibilityUtils.getAccessibleColor(
                                normalColor = Color.White.copy(alpha = 0.3f),
                                highContrastColor = Color.Black
                            )
                        )
                )
                
                // Completed plans
                StatItem(
                    count = plansDoneCount.toString(), 
                    label = "Plans Done",
                    modifier = Modifier.weight(1f),
                    alignment = Alignment.CenterHorizontally
                )
            }
        }
    }
}

@Composable
private fun FitnessTag(
    text: String,
    isMore: Boolean = false,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = if (AccessibilityUtils.isHighContrastModeEnabled()) {
            Color.Black
        } else if (isMore) {
            Color.White.copy(alpha = 0.2f)
        } else {
            Color.White.copy(alpha = 0.3f)
        }
    ) {
        Text(
            text = text,
            fontSize = 10.sp,
            color = if (AccessibilityUtils.isHighContrastModeEnabled()) Color.White else Color.White,
            fontWeight = if (isMore || AccessibilityUtils.isHighContrastModeEnabled()) 
                        FontWeight.Medium else FontWeight.Normal,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun StatItem(
    count: String, 
    label: String, 
    modifier: Modifier = Modifier,
    alignment: Alignment.Horizontal = Alignment.CenterHorizontally
) {
    Column(
        horizontalAlignment = alignment,
        modifier = modifier.padding(horizontal = 8.dp)
    ) {
        Text(
            text = count,
            color = AccessibilityUtils.getAccessibleColor(
                normalColor = Color.White,
                highContrastColor = Color.Black
            ),
            fontSize = 24.sp,
            fontWeight = if (AccessibilityUtils.isHighContrastModeEnabled()) 
                        FontWeight.ExtraBold else FontWeight.Bold
        )

        Text(
            text = label,
            color = AccessibilityUtils.getAccessibleColor(
                normalColor = Color.White.copy(alpha = 0.8f),
                highContrastColor = Color.Black.copy(alpha = 0.8f)
            ),
            fontSize = 12.sp,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
private fun RecentHistorySection(onViewAll: () -> Unit, latestWorkouts: List<Workout>, onWorkoutClick: (Workout) -> Unit) {
    val isHighContrastMode = AccessibilityUtils.isHighContrastModeEnabled()
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp, start = 16.dp, end = 16.dp, bottom = 8.dp)
    ) {
        // Title and view all
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AccessibilityUtils.AccessibleText(
                text = "Recent Records",
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = "View All",
                fontSize = 14.sp,
                color = AccessibilityUtils.getAccessibleColor(
                    normalColor = Color(0xFF3B82F6), 
                    highContrastColor = Color.Black
                ),
                fontWeight = if (isHighContrastMode) FontWeight.Bold else FontWeight.Normal,
                modifier = Modifier.clickable { onViewAll() }
            )
        }

        // History records list - each record as a separate card
        latestWorkouts.forEach { workout ->
            // 使用自定义卡片而不是AccessibleCard，以确保背景始终为白色
        Card(
            modifier = Modifier
                .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .let {
                        if (isHighContrastMode) {
                            it.border(2.dp, Color.Black, RoundedCornerShape(16.dp))
                        } else {
                            it
                        }
                    },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                    containerColor = Color.White // 确保背景始终为白色
            ),
            elevation = CardDefaults.cardElevation(
                    defaultElevation = if (isHighContrastMode) 4.dp else 0.dp
                ),
                onClick = { onWorkoutClick(workout) }
        ) {
            HistoryItem(
                    icon = R.drawable.ic_workout, // 使用默认图标
                    title = workout.type,
                    time = "${workout.date} · ${workout.duration} min",
                    calories = "${workout.calories} kcal",
                    iconTint = AccessibilityUtils.getAccessibleColor(
                        normalColor = Color(0xFF3B82F6),
                        highContrastColor = Color.White
                    ),
                    iconBackground = AccessibilityUtils.getAccessibleColor(
                        normalColor = Color(0xFFE6F0FF),
                        highContrastColor = Color.Black
                    ),
                    onItemClick = { onWorkoutClick(workout) }
                )
            }
        }
    }
}

@Composable
private fun AICoachSection(
    onStartChat: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
    ) {
        AccessibilityUtils.AccessibleText(
            text = "AI Fitness Coach",
            style = MaterialTheme.typography.titleMedium
        )

        AccessibilityUtils.AccessibleCard(
            modifier = Modifier.fillMaxWidth(),
            onClick = onStartChat
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // AI Coach Icon
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            AccessibilityUtils.getAccessibleColor(
                                normalColor = Color(0xFFE6F0FF),
                                highContrastColor = Color.Black
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_ai_coach),
                        contentDescription = null,
                        tint = AccessibilityUtils.getAccessibleColor(
                            normalColor = Color(0xFF3B82F6),
                            highContrastColor = Color.White
                        ),
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Text content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 12.dp)
                ) {
                    AccessibilityUtils.AccessibleText(
                        text = "Your Personal AI Coach",
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    AccessibilityUtils.AccessibleText(
                        text = "Get personalized workout advice",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                // Arrow icon
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "Start chat",
                    tint = AccessibilityUtils.getAccessibleColor(
                        normalColor = Color(0xFF9CA3AF),
                        highContrastColor = Color.Black
                    )
                )
            }
        }
    }
}

@Composable
private fun HistoryItem(
    icon: Int,
    title: String,
    time: String,
    calories: String,
    iconTint: Color = Color(0xFF3B82F6),
    iconBackground: Color = Color(0xFFE6F0FF),
    onItemClick: () -> Unit = {}
) {
    val isHighContrastMode = AccessibilityUtils.isHighContrastModeEnabled()
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick() }
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
                fontWeight = if (isHighContrastMode) FontWeight.Bold else FontWeight.Medium,
                color = Color.Black // 确保始终使用黑色文字以获得最大对比度
            )

            Text(
                text = "$time · $calories",
                fontSize = 14.sp,
                color = Color.Black, // 确保始终使用黑色文字以获得最大对比度
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // Right arrow
        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = "View details",
            tint = AccessibilityUtils.getAccessibleColor(
                normalColor = Color(0xFF9CA3AF),
                highContrastColor = Color.Black
            )
        )
    }
} 