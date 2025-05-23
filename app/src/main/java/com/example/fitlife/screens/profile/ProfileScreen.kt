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
import com.example.fitlife.data.repository.FirebaseUserRepository
import android.util.Log
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBackClick: () -> Unit,
    onViewAllHistory: () -> Unit,
    onEditProfileClick: (List<String>) -> Unit,
    onSettingsClick: () -> Unit,
    onAICoachClick: () -> Unit,
    onNavigateToMap: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToCalendar: () -> Unit,
    selectedFitnessTags: List<String>,
    onFitnessTagsUpdated: (List<String>) -> Unit
) {
    val context = LocalContext.current
    val workoutDao = remember { (context.applicationContext as MyApplication).database.workoutDao() }
    val userDao = remember { (context.applicationContext as MyApplication).database.userDao() }
    val isHighContrastMode = AccessibilityUtils.isHighContrastModeEnabled()
    
    // Create Firebase user repository
    val firebaseUserRepository = remember { FirebaseUserRepository() }
    
    // Get Firebase current user information
    val firebaseUser by firebaseUserRepository.currentUser.collectAsState()
    val firebaseDisplayName = firebaseUser?.displayName
    val firebaseUid = firebaseUser?.uid
    
    // User data state - keep variable as it's used elsewhere
    var user by remember { mutableStateOf<User?>(null) }
    
    // Get or create user data based on Firebase UID
    LaunchedEffect(firebaseUid) {
        if (firebaseUid != null) {
            try {
                // Try to get user data
                val existingUser = userDao.getUserByFirebaseUidSync(firebaseUid)
                
                if (existingUser == null) {
                    // User doesn't exist, create new user
                    val newUser = User(
                        firebaseUid = firebaseUid,
                        name = firebaseDisplayName ?: "User",
                        email = firebaseUser?.email ?: "user@example.com"
                    )
                    userDao.insertUser(newUser)
                    user = newUser
                    Log.d("ProfileScreen", "Created new user record for UID: $firebaseUid")
                } else {
                    // User exists, use existing data
                    user = existingUser
                    Log.d("ProfileScreen", "Found existing user record for UID: $firebaseUid")
                }
            } catch (e: Exception) {
                Log.e("ProfileScreen", "Error getting user data: ${e.message}")
            }
        }
    }
    
    // Get recent workout records from database
    val latestWorkouts by remember(firebaseUid) {
        if (firebaseUid != null) {
            workoutDao.getLatestTwoWorkouts(firebaseUid)
        } else {
            workoutDao.getLatestTwoWorkouts()
        }
    }.collectAsState(initial = emptyList())
    
    // Get count of unique workout dates
    val workoutDaysCount by remember(firebaseUid) {
        if (firebaseUid != null) {
            workoutDao.getUniqueWorkoutDaysCount(firebaseUid)
        } else {
            workoutDao.getUniqueWorkoutDaysCount()
        }
    }.collectAsState(initial = 0)
    
    // Get count of all workout records
    val totalWorkoutsCount by remember(firebaseUid) {
        if (firebaseUid != null) {
            workoutDao.getAllOrderByDateDesc(firebaseUid)
        } else {
            workoutDao.getAllOrderByDateDesc()
        }
    }.collectAsState(initial = emptyList())
    
    // Get all workout dates and calculate streak days
    val allWorkoutDates by remember(firebaseUid) {
        if (firebaseUid != null) {
            workoutDao.getAllWorkoutDatesDesc(firebaseUid)
        } else {
            workoutDao.getAllWorkoutDatesDesc()
        }
    }.collectAsState(initial = emptyList())
    val streakDays = remember(allWorkoutDates) {
        calculateStreakDays(allWorkoutDates)
    }

    // Calculate fitness tags list (convert from comma-separated string to list)
    val fitnessTags = remember(user) {
        user?.fitnessTags?.split(",")?.filter { it.isNotEmpty() } ?: listOf("Strength Training", "Cardio")
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
            // Profile content
            ProfileContent(
                onViewAllHistory = onViewAllHistory,
                onEditProfileClick = { onEditProfileClick(fitnessTags) },
                onSettingsClick = onSettingsClick,
                onAICoachClick = onAICoachClick,
                selectedFitnessTags = fitnessTags,
                user = user,
                firebaseDisplayName = firebaseDisplayName,
                latestWorkouts = latestWorkouts,
                onWorkoutClick = { workout ->
                    selectedWorkout = workout
                    showDialog = true
                },
                totalWorkoutsCount = totalWorkoutsCount.size,
                workoutDaysCount = workoutDaysCount,
                streakDays = streakDays
            )
        }
        
        // Bottom navigation
        BottomNavBar(
            currentRoute = "profile",
            onNavigateToHome = onNavigateToHome,
            onNavigateToCalendar = onNavigateToCalendar,
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
                    // Use AccessibleText component
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

// Calculate consecutive training days
private fun calculateStreakDays(dates: List<String>): Int {
    if (dates.isEmpty()) return 0
    
    try {
        // Use SimpleDateFormat instead of DateTimeFormatter to support API Level 24
        val simpleDateFormat = java.text.SimpleDateFormat("yyyy-MM-dd")
        val validDates = mutableListOf<java.util.Date>()
        
        for (dateStr in dates) {
            try {
                if (dateStr != "Select Date") {
                    validDates.add(simpleDateFormat.parse(dateStr))
                }
            } catch (e: Exception) {
                // Ignore unparseable dates
                continue
            }
        }
        
        if (validDates.isEmpty()) return 0
        
        // Sort dates in descending order (latest date first)
        val sortedDates = validDates.sortedDescending()
        
        // Start checking streak from the latest date
        var currentStreak = 1
        var previousDate = sortedDates[0]
        
        for (i in 1 until sortedDates.size) {
            val currentDate = sortedDates[i]
            
            // Calculate the difference in milliseconds between two dates
            val diffInMillis = previousDate.time - currentDate.time
            // Milliseconds in a day
            val dayInMillis = 24 * 60 * 60 * 1000L
            // Calculate the difference in days
            val daysBetween = diffInMillis / dayInMillis
            
            if (daysBetween == 1L) {
                // Two dates are 1 day apart, consecutive
                currentStreak++
                previousDate = currentDate
            } else {
                // Streak interrupted
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
    firebaseDisplayName: String?,
    latestWorkouts: List<Workout>,
    onWorkoutClick: (Workout) -> Unit,
    totalWorkoutsCount: Int = 0,
    workoutDaysCount: Int = 0,
    streakDays: Int = 0
) {
    val isHighContrastMode = AccessibilityUtils.isHighContrastModeEnabled()
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 80.dp) // Leave space for bottom navigation bar
    ) {
        // Top bar (title and settings button)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Add left Spacer to balance right settings button
            Spacer(modifier = Modifier.width(32.dp))

            // Title
            Text(
                text = "Profile",
                fontSize = 18.sp, 
                fontWeight = if (isHighContrastMode) FontWeight.ExtraBold else FontWeight.Bold,
                modifier = Modifier
                    .weight(1f), // Take up available space
                textAlign = TextAlign.Center, // Text centered within space
                color = if (isHighContrastMode) Color.Black else Color(0xFF1F2937)
            )

            // Settings button
            Box(
                modifier = Modifier
                    .size(32.dp) // Keep size consistent with left Spacer
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
        
        // User info card
        UserInfoCard(
            onEditClick = onEditProfileClick,
            fitnessTags = selectedFitnessTags,
            user = user,
            firebaseDisplayName = firebaseDisplayName,
            totalWorkoutsCount = totalWorkoutsCount,
            workoutDaysCount = workoutDaysCount,
            streakDays = streakDays,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        // Recent records section
        RecentHistorySection(
            onViewAll = onViewAllHistory,
            latestWorkouts = latestWorkouts,
            onWorkoutClick = onWorkoutClick
        )

        // AI coach section
        AICoachSection(onStartChat = onAICoachClick)

        // Placeholder - Fix weight() call issue
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
    firebaseDisplayName: String?,
    totalWorkoutsCount: Int = 0,
    workoutDaysCount: Int = 0,
    streakDays: Int = 0,
    modifier: Modifier = Modifier
) {
    // Prefer Firebase's displayName, if empty use database user name, then default
    val username = firebaseDisplayName ?: user?.name ?: "Xiao Ming"
    val context = LocalContext.current
    
    // Modify avatar URI handling
    val avatarUri = remember(user) {
        user?.avatarUri?.let { uriString ->
            try {
                // Try to parse URI
                val uri = Uri.parse(uriString)
                
                // Check if URI points to internal storage
                if (uri.scheme == "file" && uri.path?.contains(context.filesDir.path) == true) {
                    // Check if file exists
                    val file = File(uri.path!!)
                    if (file.exists()) {
                        uri
                    } else {
                        null
                    }
                } else {
                    // For content:// or other URIs, return directly
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
                // Avatar - Use AsyncImage to load avatar from database
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
                            modifier = Modifier.padding(top = 2.dp),
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
                
                // Streak days - Use calculated value
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
                
                // Count of completed workouts
                StatItem(
                    count = totalWorkoutsCount.toString(), 
                    label = "Trainings Done",
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
            // Use custom card instead of AccessibleCard to ensure background is always white
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
                    containerColor = Color.White // Ensure background is always white
            ),
            elevation = CardDefaults.cardElevation(
                    defaultElevation = if (isHighContrastMode) 4.dp else 0.dp
                ),
                onClick = { onWorkoutClick(workout) }
        ) {
            HistoryItem(
                    icon = R.drawable.ic_workout, // Use default icon
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
                color = Color.Black // Ensure always use black text for maximum contrast
            )

            Text(
                text = "$time · $calories",
                fontSize = 14.sp,
                color = Color.Black, // Ensure always use black text for maximum contrast
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