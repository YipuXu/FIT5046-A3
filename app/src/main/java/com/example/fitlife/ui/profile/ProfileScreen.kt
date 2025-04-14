package com.example.fitlife.ui.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Search
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

@Composable
fun ProfileScreen(
    onBackClick: () -> Unit = {},
    onViewAllAchievements: () -> Unit = {},
    onViewAllHistory: () -> Unit = {},
    onEditProfileClick: () -> Unit = {}
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
            TopBar(onBackClick = onBackClick, onMenuClick = onEditProfileClick)

            // User info card
            UserInfoCard(onEditClick = {})

            // Achievements section
            AchievementsSection(onViewAll = onViewAllAchievements)

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
private fun UserInfoCard(onEditClick: () -> Unit) {
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

                // Username and level
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 16.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "Xiao Ming",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Fitness Enthusiast · Beginner",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp
                    )
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color.Yellow,
                            modifier = Modifier.size(16.dp)
                        )

                        Text(
                            text = "4.8",
                            color = Color.White,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }

                // Edit button (now optional/subtle)
                OutlinedButton(
                    onClick = onEditClick,
                    modifier = Modifier
                        .height(32.dp)
                        .width(48.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White
                    ),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                ) {
                    Text(
                        text = "Edit",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
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
                    count = "42", 
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
                    count = "12", 
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
                    count = "8", 
                    label = "Plans Done",
                    modifier = Modifier.weight(1f),
                    alignment = Alignment.CenterHorizontally
                )
            }
        }
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
private fun AchievementsSection(onViewAll: () -> Unit) {
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
                text = "My Achievements",
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

        // Achievement icons list - each item as a separate card
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Card(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
                    .height(110.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 0.dp
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AchievementItem(
                        icon = R.drawable.ic_achievement,
                        label = "Streak",
                        iconTint = Color(0xFFFFB800),
                        backgroundColor = Color(0xFFFFF8E1)
                    )
                }
            }

            Card(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp)
                    .height(110.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 0.dp
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AchievementItem(
                        icon = R.drawable.ic_bolt,
                        label = "Burner",
                        iconTint = Color(0xFF3B82F6),
                        backgroundColor = Color(0xFFE6F0FF)
                    )
                }
            }

            Card(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp)
                    .height(110.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 0.dp
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AchievementItem(
                        icon = R.drawable.ic_heart,
                        label = "Goal",
                        iconTint = Color(0xFF9061F9),
                        backgroundColor = Color(0xFFF3E8FF)
                    )
                }
            }

            Card(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
                    .height(110.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 0.dp
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AchievementItem(
                        icon = R.drawable.ic_plus,
                        label = "More",
                        iconTint = Color(0xFF9CA3AF),
                        backgroundColor = Color(0xFFF3F4F6)
                    )
                }
            }
        }
    }
}

@Composable
private fun AchievementItem(
    icon: Int, 
    label: String,
    iconTint: Color,
    backgroundColor: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(backgroundColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(24.dp)
            )
        }

        Text(
            text = label,
            fontSize = 12.sp,
            color = Color(0xFF4B5563),
            modifier = Modifier.padding(top = 8.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
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