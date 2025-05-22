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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBackClick: () -> Unit,
    onViewAllHistory: () -> Unit,
    onEditProfileClick: (List<String>) -> Unit,
    onSettingsClick: () -> Unit,
    onAICoachClick: () -> Unit,
    onNavigateToMap: () -> Unit,
    getUserData: () -> Map<String, Any> = {
        mapOf(
            "username" to "Xiao Ming",
            "level" to "Fitness Enthusiast · Beginner",
            "rating" to 3,
            "workoutDays" to 42,
            "streakDays" to 12,
            "plansDone" to 8,
            "fitnessTags" to listOf("Strength Training", "Cardio", "HIIT")
        )
    },
    selectedFitnessTags: List<String>,
    onFitnessTagsUpdated: (List<String>) -> Unit
) {
    val userData = getUserData()
    
    val context = LocalContext.current
    val workoutDao = remember { (context.applicationContext as MyApplication).database.workoutDao() }
    val latestWorkouts by workoutDao.getLatestTwoWorkouts().collectAsState(initial = emptyList())

    // State for the workout detail dialog
    var showDialog by remember { mutableStateOf(false) }
    var selectedWorkout by remember { mutableStateOf<Workout?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9FAFB))
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 个人资料内容
            ProfileContent(
                onViewAllHistory = onViewAllHistory,
                onEditProfileClick = onEditProfileClick,
                onSettingsClick = onSettingsClick,
                onAICoachClick = onAICoachClick,
                selectedFitnessTags = selectedFitnessTags,
                onFitnessTagsUpdated = onFitnessTagsUpdated,
                userData = userData,
                latestWorkouts = latestWorkouts,
                onWorkoutClick = { workout ->
                    selectedWorkout = workout
                    showDialog = true
                }
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
            title = { Text(workout!!.type) }, // Use non-null assertion
            text = {
                Column {
                    Text("Date: ${workout!!.date}") // Use non-null assertion
                    Text("Time: ${workout!!.time}") // Use non-null assertion
                    Text("Duration: ${workout!!.duration} min") // Use non-null assertion
                    Text("Calories: ${workout!!.calories} kcal") // Use non-null assertion
                    if (workout!!.notes.isNotBlank()) { // Use non-null assertion
                        Text("Notes: ${workout!!.notes}") // Use non-null assertion
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6))
                ) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
private fun ProfileContent(
    onViewAllHistory: () -> Unit,
    onEditProfileClick: (List<String>) -> Unit,
    onSettingsClick: () -> Unit,
    onAICoachClick: () -> Unit,
    selectedFitnessTags: List<String>,
    onFitnessTagsUpdated: (List<String>) -> Unit,
    userData: Map<String, Any>,
    latestWorkouts: List<Workout>,
    onWorkoutClick: (Workout) -> Unit
) {
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
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .weight(1f), // 占据中间可用空间
                textAlign = TextAlign.Center, // 文本在其空间内居中
                color = Color(0xFF1F2937)
            )

            // 设置按钮
            Box(
                modifier = Modifier
                    .size(32.dp) // 保持尺寸与左侧Spacer一致
                    .clip(CircleShape)
                    .background(Color(0xFFF3F4F6))
                    .clickable { onSettingsClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    modifier = Modifier.size(20.dp),
                    tint = Color(0xFF6B7280)
                )
            }
        }
        
        // 用户信息卡片
        UserInfoCard(
            onEditClick = { onEditProfileClick(selectedFitnessTags) },
            fitnessTags = selectedFitnessTags,
            username = userData["username"] as? String ?: "",
            workoutDays = userData["workoutDays"] as? Int ?: 0,
            streakDays = userData["streakDays"] as? Int ?: 0,
            plansDone = userData["plansDone"] as? Int ?: 0,
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
    username: String = "Xiao Ming",
    workoutDays: Int = 42,
    streakDays: Int = 12,
    plansDone: Int = 8,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF3B82F6)
        )
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
                // Avatar
                Image(
                    painter = painterResource(id = R.drawable.profile_photo),
                    contentDescription = "Profile Photo",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                )

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
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )

                        // Smaller edit button
                        OutlinedButton(
                            onClick = onEditClick,
                            modifier = Modifier
                                .height(28.dp)
                                .width(32.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color.White
                            ),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)
                        ) {
                            Text(
                                text = "Edit",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Normal
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
                    count = workoutDays.toString(), 
                    label = "Workout Days",
                    modifier = Modifier.weight(1f),
                    alignment = Alignment.CenterHorizontally
                )
                
                // Vertical line
                Box(
                    modifier = Modifier
                        .height(30.dp)
                        .width(1.dp)
                        .background(Color.White.copy(alpha = 0.3f))
                )
                
                // Consecutive days
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
                        .background(Color.White.copy(alpha = 0.3f))
                )
                
                // Completed plans
                StatItem(
                    count = plansDone.toString(), 
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
        color = if (isMore) Color.White.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.3f)
    ) {
        Text(
            text = text,
            fontSize = 10.sp,
            color = Color.White,
            fontWeight = if (isMore) FontWeight.Medium else FontWeight.Normal,
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
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = label,
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 12.sp,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
private fun RecentHistorySection(onViewAll: () -> Unit, latestWorkouts: List<Workout>, onWorkoutClick: (Workout) -> Unit) {
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
            Text(
                text = "Recent Records",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = "View All",
                fontSize = 14.sp,
                color = Color(0xFF3B82F6),
                modifier = Modifier.clickable { onViewAll() }
            )
        }

        // History records list - each record as a separate card
        latestWorkouts.forEach { workout ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 0.dp
                )
            ) {
                HistoryItem(
                    icon = R.drawable.ic_workout, // 使用默认图标
                    title = workout.type,
                    time = "${workout.date} · ${workout.duration} min",
                    calories = "${workout.calories} kcal",
                    iconTint = Color(0xFF3B82F6), // 使用默认颜色
                    background = Color.White,
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
        Text(
            text = "AI Fitness Coach",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onStartChat() },
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
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // AI Coach Icon
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFE6F0FF)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_ai_coach),
                        contentDescription = null,
                        tint = Color(0xFF3B82F6),
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Text content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 12.dp)
                ) {
                    Text(
                        text = "Your Personal AI Coach",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF1F2937)
                    )
                    Text(
                        text = "Get personalized workout advice",
                        fontSize = 14.sp,
                        color = Color(0xFF6B7280),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                // Arrow icon
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "Start chat",
                    tint = Color(0xFF9CA3AF)
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
    background: Color = Color.White,
    onItemClick: () -> Unit = {}
) {
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
                .background(if (iconTint == Color(0xFF3B82F6)) Color(0xFFE6F0FF) else Color(0xFFDCFCE7)),
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
                fontWeight = FontWeight.Medium
            )

            Text(
                text = "$time · $calories",
                fontSize = 14.sp,
                color = Color(0xFF6B7280),
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // Right arrow
        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = "View details",
            tint = Color(0xFF9CA3AF)
        )
    }
} 