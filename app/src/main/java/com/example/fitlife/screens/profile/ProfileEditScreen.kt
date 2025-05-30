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
import com.example.fitlife.ui.components.BottomNavBar

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.platform.LocalContext
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitlife.MyApplication
import com.example.fitlife.data.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.util.UUID
import android.provider.MediaStore
import com.example.fitlife.data.repository.FirebaseUserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.tasks.await
import android.util.Log

@Composable
fun ProfileEditScreen(
    onBackClick: () -> Unit = {},
    initialFitnessTags: List<String> = listOf("Strength Training", "Cardio"),
    onFitnessTagsSelected: (List<String>) -> Unit = {},

    onNavigateToHome: () -> Unit,
    onNavigateToCalendar: () -> Unit,
    onNavigateToMap: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val density = LocalDensity.current
    val context = LocalContext.current
    var showProfilePhotoDialog by remember { mutableStateOf(false) }
    var showBasicInfoDialog by remember { mutableStateOf(false) }
    var showHeightWeightDialog by remember { mutableStateOf(false) }
    var showFitnessGoalDialog by remember { mutableStateOf(false) }
    var showWorkoutFrequencyDialog by remember { mutableStateOf(false) }
    var showFitnessTagsDialog by remember { mutableStateOf(false) }
    
    // Get database instance
    val userDao = remember { (context.applicationContext as MyApplication).database.userDao() }
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Create Firebase user repository
    val firebaseUserRepository = remember { FirebaseUserRepository() }
    
    // Get Firebase current user information
    val firebaseUser by firebaseUserRepository.currentUser.collectAsState()
    val firebaseDisplayName = firebaseUser?.displayName
    val firebaseEmail = firebaseUser?.email
    val firebaseUid = firebaseUser?.uid
    
    // Get user information from database
    var user by remember { mutableStateOf<User?>(null) }
    
    // Dynamic states based on user object
    var nameValue by remember { mutableStateOf("") } 
    var emailValue by remember { mutableStateOf("") }
    var heightValue by remember { mutableStateOf("178") }
    var weightValue by remember { mutableStateOf("70") }
    var fitnessGoal by remember { mutableStateOf("Muscle Gain & Fat Loss") }
    var workoutFrequency by remember { mutableStateOf("4-5 Times Weekly") }
    var selectedFitnessTags by remember { mutableStateOf(initialFitnessTags) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    
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
                        email = firebaseEmail ?: "user@example.com"
                    )
                    userDao.insertUser(newUser)
                    user = newUser
                    Log.d("ProfileEditScreen", "Created new user record for UID: $firebaseUid")
                } else {
                    // User exists, use existing data
                    user = existingUser
                    Log.d("ProfileEditScreen", "Found existing user record for UID: $firebaseUid")
                }
                
                // Update state values
                user?.let { loadedUser ->
                    nameValue = firebaseDisplayName ?: loadedUser.name
                    emailValue = firebaseEmail ?: loadedUser.email
                    fitnessGoal = loadedUser.fitnessGoal
                    workoutFrequency = loadedUser.workoutFrequency
                    selectedFitnessTags = loadedUser.fitnessTags.split(",").filter { it.isNotEmpty() }
                    selectedImageUri = loadedUser.avatarUri?.let { Uri.parse(it) }
                }
                
                // Get height and weight data from Firestore
                val heightWeight = firebaseUserRepository.getHeightWeight()
                if (heightWeight != null) {
                    heightValue = heightWeight.first
                    weightValue = heightWeight.second
                    Log.d("ProfileEditScreen", "Height/weight loaded from Firestore: $heightValue/$weightValue")
                } else {
                    // If Firestore has no data, use Room default values or write to Firestore
                    user?.let { loadedUser ->
                        heightValue = loadedUser.height
                        weightValue = loadedUser.weight
                        // Sync Room data to Firestore
                        firebaseUserRepository.updateHeightWeight(heightValue, weightValue)
                        Log.d("ProfileEditScreen", "Synced height/weight to Firestore: $heightValue/$weightValue")
                    }
                }
            } catch (e: Exception) {
                Log.e("ProfileEditScreen", "Error loading user data: ${e.message}")
            }
        }
    }
    
    // Activity result launcher for picking images from gallery
    val pickImageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        uri: Uri? ->
        if (uri != null && firebaseUid != null) {
            coroutineScope.launch {
                // Copy image to app internal storage
                val savedUri = saveImageToInternalStorage(context, uri)
                
                // Update UI and database
                selectedImageUri = savedUri
                showProfilePhotoDialog = false // Close dialog after selecting
                
                // Save URI to Room database
                userDao.updateAvatar(firebaseUid, savedUri.toString())
                user = userDao.getUserByFirebaseUidSync(firebaseUid)
                
                snackbarHostState.showSnackbar("Upload successful!")
            }
        }
    }
    
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
                .padding(bottom = 56.dp) // Leave space for bottom navigation bar
        ) {
            // Top bar
            TopBar(onBackClick = onBackClick)
            
            // Content with scroll
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f) // Allow content to scroll
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                // User profile card
                UserProfileCard(
                    name = nameValue,
                    email = emailValue,
                    imageUri = selectedImageUri, // Pass the selected image URI
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

        BottomNavBar(
            currentRoute = "profile",
            onNavigateToHome = onNavigateToHome,
            onNavigateToCalendar = onNavigateToCalendar,
            onNavigateToMap = onNavigateToMap,
            onNavigateToProfile = onNavigateToProfile, // Click the Profile icon to return to the ProfileScreen
            modifier = Modifier.align(Alignment.BottomCenter)
        )
        // SnackbarHost to display snackbar messages
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
    
    // Profile Photo Dialog
    if (showProfilePhotoDialog) {
        ProfilePhotoDialog(
            selectedImageUri = selectedImageUri, // Pass selected image URI
            onDismiss = { showProfilePhotoDialog = false },
            onUploadPhoto = { // Modified to launch gallery picker
                pickImageLauncher.launch("image/*") // Launch gallery to select images
            }
        )
    }
    
    // Basic Info Edit Dialog
    if (showBasicInfoDialog) {
        NameEditDialog(
            initialName = nameValue,
            onDismiss = { showBasicInfoDialog = false },
            onSave = { name ->
                nameValue = name

                // Saving to two data sources
                coroutineScope.launch {
                    // Update local database - only update the name, keep the original email address
                    firebaseUid?.let { uid ->
                        // Update the local database
                        userDao.updateBasicInfo(uid, name, emailValue)
                        user = userDao.getUserByFirebaseUidSync(uid)
                        
                        // Update Firebase user information - Update only the name
                        try {
                            val firebaseUser = FirebaseAuth.getInstance().currentUser
                            if (firebaseUser != null) {
                                // Create a user profile update request object
                                val profileUpdates = UserProfileChangeRequest.Builder()
                                    .setDisplayName(name)
                                    .build()
                                    
                                // Performing Updates
                                withContext(Dispatchers.IO) {
                                    firebaseUser.updateProfile(profileUpdates).await()
                                }
                                
                                snackbarHostState.showSnackbar("Profile updated successfully!")
                            }
                        } catch (e: Exception) {
                            snackbarHostState.showSnackbar("Failed to update profile: ${e.message}")
                        }
                    }
                    
                    showBasicInfoDialog = false
                }
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
                // Saving to Firestore and local database
                coroutineScope.launch {
                    try {
                        // First save to Firestore
                        val success = firebaseUserRepository.updateHeightWeight(height, weight)
                        
                        if (success) {
                            // If Firestore saves successfully, also update the local database to maintain consistency
                            firebaseUid?.let { uid ->
                                userDao.updateHeightWeight(uid, height, weight)
                                user = userDao.getUserByFirebaseUidSync(uid)
                            }

                            snackbarHostState.showSnackbar("Height and weight updated successfully")
                        } else {
                            snackbarHostState.showSnackbar("Failed to update height and weight")
                        }
                    } catch (e: Exception) {
                        Log.e("ProfileEditScreen", "Error updating height/weight: ${e.message}")
                        snackbarHostState.showSnackbar("Error updating data: ${e.message}")
                    } finally {
                        showHeightWeightDialog = false
                    }
                }
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
                // Save to database
                coroutineScope.launch {
                    firebaseUid?.let { uid ->
                        userDao.updateFitnessGoal(uid, option)
                        user = userDao.getUserByFirebaseUidSync(uid)
                        showFitnessGoalDialog = false
                    }
                }
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
                // Save to database
                coroutineScope.launch {
                    firebaseUid?.let { uid ->
                        userDao.updateWorkoutFrequency(uid, option)
                        user = userDao.getUserByFirebaseUidSync(uid)
                        showWorkoutFrequencyDialog = false
                    }
                }
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
                selectedFitnessTags = selected
                // Save to database, convert list to comma separated string
                coroutineScope.launch {
                    firebaseUid?.let { uid ->
                        val tagsString = selected.joinToString(",")
                        userDao.updateFitnessTags(uid, tagsString)
                        user = userDao.getUserByFirebaseUidSync(uid)
                        showFitnessTagsDialog = false
                    }
                }
            },
            onDismiss = { 
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
            text = "My Profile",
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
    name: String,
    email: String,
    imageUri: Uri? = null, // Add imageUri parameter
    onAvatarClick: () -> Unit,
    onProfileEditClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile Avatar
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE5E7EB))
                    .clickable(onClick = onAvatarClick), // Make avatar clickable
                contentAlignment = Alignment.Center
            ) {
                if (imageUri != null) {
                    AsyncImage(
                        model = imageUri,
                        contentDescription = "Selected Profile Photo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        error = painterResource(id = R.drawable.profile_photo), // Fallback in case of error
                        placeholder = painterResource(id = R.drawable.profile_photo) // Placeholder while loading
                    )
                } else {
                Image(
                    painter = painterResource(id = R.drawable.profile_photo),
                        contentDescription = "Profile Avatar",
                        modifier = Modifier.fillMaxSize(), // Changed from size(72.dp)
                    contentScale = ContentScale.Crop
                )
                }
                // Small "add" icon overlay
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = (-8).dp, y = (-8).dp) // Changed from (-4).dp
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF3B82F6)) // Blue background
                        .border(2.dp, Color.White, CircleShape) // White border
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add Photo",
                        tint = Color.White,
                        modifier = Modifier
                            .size(16.dp) // Smaller icon
                            .align(Alignment.Center)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))

            // User Info
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            ) {
                Text(
                    text = name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF1F2937)
                )
                
                // 保留邮箱显示，但不允许编辑
                Text(
                    text = email,
                    fontSize = 14.sp,
                    color = Color(0xFF6B7280),
                    modifier = Modifier.padding(top = 2.dp)
                )
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
    selectedImageUri: Uri?, // Pass selectedImageUri to display in dialog
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
                // Large profile photo or selected image
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .padding(16.dp)
                        .fillMaxWidth()
                        .height(200.dp) // Fixed height for the image preview area
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF3F4F6)), // Light gray background for preview area
                    contentAlignment = Alignment.Center
                ) {
                    if (selectedImageUri != null) {
                        AsyncImage(
                            model = selectedImageUri,
                            contentDescription = "Selected Profile Photo",
                            modifier = Modifier.fillMaxSize(), // Fill the preview area
                            contentScale = ContentScale.Fit, // Fit the image within bounds
                            error = painterResource(id = R.drawable.profile_photo), // Fallback image
                            placeholder = painterResource(id = R.drawable.profile_photo) // Placeholder
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.profile_photo), // Use profile_photo as placeholder
                            contentDescription = "Placeholder for new photo",
                            modifier = Modifier.size(100.dp), // Placeholder size
                            contentScale = ContentScale.Fit
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Buttons
                Button(
                    onClick = onUploadPhoto, // This will now trigger the gallery picker
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)) // Blue background
                ) {
                    Text("Upload New Photo", color = Color.White)
                    }
                Spacer(modifier = Modifier.height(8.dp))
                // Removed OutlinedButton for Take Photo
                Spacer(modifier = Modifier.height(8.dp))
                // Removed OutlinedButton for Choose from Gallery

                Spacer(modifier = Modifier.height(24.dp))

                TextButton(onClick = onDismiss) {
                    Text("Close")
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
private fun NameEditDialog(
    initialName: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var name by remember { mutableStateOf(initialName) }

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
                    text = "Edit Name",
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
                        onClick = { onSave(name) },
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
    // Use mutableStateListOf to create a mutable state list to support UI updates
    val tempSelected = remember { mutableStateListOf<String>() }
    // The maximum number of selections is 2
    val maxSelections = 2
    // A message to display the maximum selection limit
    var showMaxSelectionsMessage by remember { mutableStateOf(false) }
    
    // Initialize selected items
    LaunchedEffect(selectedOptions) {
        tempSelected.clear()
        // Make sure the initial selection does not exceed the maximum limit
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
                
                // Show maximum selection limit hint
                Text(
                    text = "Select up to ${maxSelections} tags",
                    fontSize = 14.sp,
                    color = Color(0xFF6B7280)
                )
                
                // If a selection restriction message is displayed, show a hint
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
                                            // Display maximum selection limit message
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

// New function: Save image to app internal storage
private suspend fun saveImageToInternalStorage(context: Context, sourceUri: Uri): Uri = withContext(Dispatchers.IO) {
    try {
        // Create a unique filename
        val fileName = "profile_photo_${UUID.randomUUID()}.jpg"
        
        // Create target file
        val directory = File(context.filesDir, "profile_photos")
        if (!directory.exists()) {
            directory.mkdirs()
        }
        val destinationFile = File(directory, fileName)
        
        // Get input stream from URI and create output stream
        val inputStream = context.contentResolver.openInputStream(sourceUri)
        val outputStream = FileOutputStream(destinationFile)
        
        // Copy file
        inputStream?.use { input ->
            outputStream.use { output ->
                val buffer = ByteArray(4 * 1024) // 4KB buffer
                var read: Int
                while (input.read(buffer).also { read = it } != -1) {
                    output.write(buffer, 0, read)
                }
                output.flush()
            }
        }
        
        // Create and return URI pointing to internal storage file
        Uri.fromFile(destinationFile)
    } catch (e: Exception) {
        e.printStackTrace()
        // If error occurs, return original URI
        sourceUri
    }
} 