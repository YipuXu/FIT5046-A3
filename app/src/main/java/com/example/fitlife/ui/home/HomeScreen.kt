package com.example.fitlife.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fitlife.ui.components.BottomNavBar
import androidx.compose.material3.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.example.fitlife.R
import com.example.fitlife.MyApplication
import com.example.fitlife.data.model.User
import com.example.fitlife.data.model.Workout
import com.example.fitlife.data.repository.FirebaseUserRepository
import com.example.fitlife.ui.theme.AccessibilityUtils
import android.net.Uri
import coil.compose.AsyncImage
import android.util.Log
import java.io.File
import kotlin.math.roundToInt
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    currentRoute: String,
    onNavigateToHome: () -> Unit,
    onNavigateToCalendar: () -> Unit,
    onNavigateToMap: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToRecord: () -> Unit,
    onNavigateToProfileEdit: () -> Unit
) {
    val context = LocalContext.current
    val userDao = remember { (context.applicationContext as MyApplication).database.userDao() }
    val workoutDao = remember { (context.applicationContext as MyApplication).database.workoutDao() }
    val firebaseUserRepository = remember { FirebaseUserRepository() }
    val firebaseUser by firebaseUserRepository.currentUser.collectAsState()
    val firebaseDisplayName = firebaseUser?.displayName
    val firebaseUid = firebaseUser?.uid

    var user by remember { mutableStateOf<User?>(null) }

    val workouts by remember(firebaseUid) {
        if (firebaseUid != null) {
            workoutDao.getAllOrderByDateDesc(firebaseUid)
        } else {
            workoutDao.getAllOrderByDateDesc()
        }
    }.collectAsState(initial = emptyList())

    LaunchedEffect(firebaseUid) {
        if (firebaseUid != null) {
            try {
                val existingUser = userDao.getUserByFirebaseUidSync(firebaseUid)
                if (existingUser != null) {
                    user = existingUser
                    Log.d("HomeScreen", "Loaded user data for UID: $firebaseUid")
                }
            } catch (e: Exception) {
                Log.e("HomeScreen", "Error loading user data: ${e.message}")
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "My Fitness",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            )
        },
        bottomBar = {
            BottomNavBar(
                currentRoute = currentRoute,
                onNavigateToHome = onNavigateToHome,
                onNavigateToCalendar = onNavigateToCalendar,
                onNavigateToMap = onNavigateToMap,
                onNavigateToProfile = onNavigateToProfile
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            WelcomeSection(
                userName = firebaseDisplayName ?: user?.name ?: "User",
                avatarUri = user?.avatarUri?.let { Uri.parse(it) },
                onAvatarClick = { onNavigateToProfileEdit() }
            )
            
            FitnessDashboard(workouts = workouts)
            
            Button(
                onClick = onNavigateToRecord,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2563EB),
                    contentColor = Color.White
                )
            ) {
                Text("Record Training")
            }
            MusicPlayerScreen()
            ExerciseRecommendation()
        }

    }
}

@Composable
fun FitnessDashboard(workouts: List<Workout>) {
    val isHighContrastMode = AccessibilityUtils.isHighContrastModeEnabled()
    
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val currentDate = dateFormat.format(calendar.time)
    calendar.add(Calendar.DAY_OF_YEAR, -7)
    val oneWeekAgo = dateFormat.format(calendar.time)
    
    val recentWorkouts = workouts.filter { workout ->
        try {
            val workoutDate = workout.date
            workoutDate >= oneWeekAgo && workoutDate <= currentDate
        } catch (e: Exception) {
            false
        }
    }
    
    val totalWorkouts = recentWorkouts.size
    
    val totalDurationMinutes = recentWorkouts.sumOf { it.duration }
    val totalDurationHours = (totalDurationMinutes / 60.0 * 10).roundToInt() / 10.0
    
    val totalCalories = recentWorkouts.sumOf { it.calories }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = AccessibilityUtils.getAccessibleColor(
                normalColor = Color(0xFF2563EB),
                highContrastColor = Color.White
            )
        ),
        border = if (isHighContrastMode) BorderStroke(2.dp, Color.Black) else null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "This week's fitness overview",
                color = AccessibilityUtils.getAccessibleColor(
                    normalColor = Color.White,
                    highContrastColor = Color.Black
                ),
                fontWeight = if (isHighContrastMode) FontWeight.Bold else FontWeight.Medium,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Start
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                DashboardItem(
                    label = "Trainings done",
                    value = "$totalWorkouts",
                    modifier = Modifier.weight(1f)
                )
                
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
                
                DashboardItem(
                    label = "Total duration",
                    value = "$totalDurationHours h",
                    modifier = Modifier.weight(1f)
                )

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
                
                DashboardItem(
                    label = "Burn calories",
                    value = "$totalCalories kcal",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun DashboardItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    val isHighContrastMode = AccessibilityUtils.isHighContrastModeEnabled()
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(horizontal = 8.dp)
    ) {
        Text(
            text = value,
            color = AccessibilityUtils.getAccessibleColor(
                normalColor = Color.White,
                highContrastColor = Color.Black
            ),
            fontWeight = if (isHighContrastMode) FontWeight.ExtraBold else FontWeight.Bold,
            fontSize = MaterialTheme.typography.bodyLarge.fontSize,
            textAlign = TextAlign.Center
        )
        
        Text(
            text = label,
            color = AccessibilityUtils.getAccessibleColor(
                normalColor = Color.White.copy(alpha = 0.8f),
                highContrastColor = Color.Black.copy(alpha = 0.8f)
            ),
            fontSize = MaterialTheme.typography.bodySmall.fontSize,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun WelcomeSection(
    userName: String = "User",
    avatarUri: Uri? = null,
    onAvatarClick: () -> Unit = {}
) {
    val context = LocalContext.current
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Welcome text on the left
        Column {
            Text(
                text = "Hello, $userName",
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = "Today is a great day for fitness!",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }

        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color(0xFFE5E7EB))
                .border(1.dp, Color.LightGray, CircleShape)
                .clickable { onAvatarClick() },  
            contentAlignment = Alignment.Center
        ) {
            if (avatarUri != null) {
                val isFileUri = avatarUri.scheme == "file" && 
                    avatarUri.path?.contains(context.filesDir.path) == true
                
                val fileExists = if (isFileUri) {
                    try {
                        val file = File(avatarUri.path!!)
                        file.exists()
                    } catch (e: Exception) {
                        false
                    }
                } else {
                    true 
                }
                
                if (fileExists) {
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
                        contentDescription = "Default Profile Photo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            } else {
                Image(
                    painter = painterResource(id = R.drawable.profile_photo),
                    contentDescription = "Default Profile Photo",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}



