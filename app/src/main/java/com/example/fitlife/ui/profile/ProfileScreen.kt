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

@Composable
fun ProfileScreen(
    onBackClick: () -> Unit = {},
    onViewAllHistory: () -> Unit = {},
    onEditProfileClick: (List<String>) -> Unit = { _ -> },
    onSettingsClick: () -> Unit = {},
    // Add user data retrieval function parameter, in actual application should get from ViewModel
    getUserData: () -> Map<String, Any> = {
        // Return mock user data, including fitness tags
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
    selectedFitnessTags: List<String> = listOf("Strength Training", "Cardio"),
    onFitnessTagsUpdated: (List<String>) -> Unit = {}
) {
    // Use passed selectedFitnessTags, not from userData
    val userData = getUserData()
    
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
            TopBar(onBackClick = onBackClick, onMenuClick = onSettingsClick)

            // User info card
            UserInfoCard(
                onEditClick = { onEditProfileClick(selectedFitnessTags) },
                fitnessTags = selectedFitnessTags,
                username = userData["username"] as? String ?: "",
                workoutDays = userData["workoutDays"] as? Int ?: 0,
                streakDays = userData["streakDays"] as? Int ?: 0,
                plansDone = userData["plansDone"] as? Int ?: 0
            )

            // Recent history section
            RecentHistorySection(onViewAll = onViewAllHistory)

            // Spacer
            Spacer(modifier = Modifier.weight(1f))
        }

        // Bottom navigation bar
        BottomNavBar(
            currentRoute = "profile",
            onNavigateToHome = { /* Navigate to home */ },
            onNavigateToCalendar = { /* Navigate to calendar */ },
            onNavigateToMap = { onBackClick() },
            onNavigateToProfile = { /* Already on profile page */ },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun TopBar(onBackClick: () -> Unit, onMenuClick: () -> Unit) {
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
            text = "My Profile",
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
                .clickable { onMenuClick() },
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
}

@Composable
private fun UserInfoCard(
    onEditClick: () -> Unit,
    fitnessTags: List<String>,
    username: String = "Xiao Ming",
    workoutDays: Int = 42,
    streakDays: Int = 12,
    plansDone: Int = 8
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
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
private fun RecentHistorySection(onViewAll: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
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
                icon = R.drawable.ic_workout,
                title = "Upper Body Training",
                time = "Yesterday · 45 min",
                calories = "320 kcal",
                iconTint = Color(0xFF3B82F6),
                background = Color.White
            )
        }

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
            HistoryItem(
                icon = R.drawable.ic_workout,
                title = "HIIT Training",
                time = "3 days ago · 30 min",
                calories = "280 kcal",
                iconTint = Color(0xFF10B981),
                background = Color.White
            )
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
    background: Color = Color.White
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* View details */ }
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