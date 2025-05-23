package com.example.fitlife.ui.auth

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitlife.R
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.Divider
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import com.example.fitlife.data.repository.FirebaseUserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onRegisterClick: () -> Unit
) {
    val context = LocalContext.current
    
    // Read the saved mailbox
    val sharedPreferences = remember { context.getSharedPreferences("fitlife_prefs", Context.MODE_PRIVATE) }
    val savedEmail = remember { sharedPreferences.getString("saved_email", "") ?: "" }
    
    var email by remember { mutableStateOf(savedEmail) }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(savedEmail.isNotEmpty()) } // If there is a saved email, "Remember me" is checked by default
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    // Display prompt information
    var showRememberMeInfo by remember { mutableStateOf(false) }
    
    // When the email address changes, if it is different from the saved one, cancel "Remember Me".
    LaunchedEffect(email) {
        if (email != savedEmail && rememberMe) {
            rememberMe = false
        }
    }
    
    // Startup effect: If auto-fill mailbox is detected, a short prompt message will be displayed
    LaunchedEffect(Unit) {
        showRememberMeInfo = savedEmail.isNotEmpty()
        if (showRememberMeInfo) {
            kotlinx.coroutines.delay(2000) // Display the prompt message for 2 seconds
            showRememberMeInfo = false
        }
    }
    
    val coroutineScope = rememberCoroutineScope()
    val auth: FirebaseAuth = Firebase.auth
    val scrollState = rememberScrollState()
    val firebaseUserRepository = remember { FirebaseUserRepository() }

    // Google Sign-In
    val webClientId = "735710334901-1ak9pnf9hqetcsouc5sf2p67st5274oa.apps.googleusercontent.com"
    val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(webClientId)
        .requestEmail()
        .build()
    val googleSignInClient: GoogleSignInClient = GoogleSignIn.getClient(context, googleSignInOptions)

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                Log.d("LoginScreen", "Google sign in successful, got idToken: ${account.idToken}")
                firebaseAuthWithGoogle(account.idToken!!, auth, onLoginSuccess = {
                    Toast.makeText(context, "Google Sign-In Successful!", Toast.LENGTH_SHORT).show()
                    onLoginSuccess()
                }, onError = { errorMsg ->
                    errorMessage = errorMsg
                }, coroutineScope = coroutineScope, onLoadingChange = { isLoading = it })
            } catch (e: ApiException) {
                Log.w("LoginScreen", "Google sign in failed", e)
                errorMessage = "Google sign in failed: ${e.localizedMessage} (Code: ${e.statusCode})"
                isLoading = false
            }
        } else {
             Log.w("LoginScreen", "Google sign in cancelled or failed. Result code: ${result.resultCode}")
            
            if (result.resultCode != Activity.RESULT_CANCELED) {
                 errorMessage = "Google sign in failed. Please try again."
            }
            isLoading = false
        }
    }

    // Save the mailbox function
    fun saveEmail(email: String, remember: Boolean) {
        sharedPreferences.edit().apply {
            if (remember) {
                putString("saved_email", email)
            } else {
                remove("saved_email")
            }
            apply()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top background image and title
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF4F46E5),
                                Color(0xFF4338CA)
                            )
                        )
                    )
            ) {
                // Background Image
                Image(
                    painter = painterResource(id = R.drawable.fitness_background),
                    contentDescription = "Fitness Background",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF4F46E5).copy(alpha = 0.4f))
                )
                
                // Title
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "FitLife",
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    
                    Text(
                        text = "Shape a better you",
                        fontSize = 16.sp,
                        color = Color.White,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
            
            // Login Form
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                // Welcome text
                Text(
                    text = "Welcome Back",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1F2937),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    textAlign = TextAlign.Start
                )
                
                // Email Input
                Column(modifier = Modifier.padding(bottom = 16.dp)) {
                    Text(
                        text = "Email",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF374151),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = { Text("your@email.com") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email") },
                        modifier = Modifier
                            .fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF3B82F6),
                            unfocusedBorderColor = Color(0xFFD1D5DB)
                        )
                    )
                }
                
                // Password input
                Column(modifier = Modifier.padding(bottom = 16.dp)) {
                    Text(
                        text = "Password",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF374151),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    
                    var passwordVisible by remember { mutableStateOf(false) }
                    
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = { Text("••••••••") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password") },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    painter = painterResource(
                                        id = if (passwordVisible) 
                                            R.drawable.ic_visibility_off 
                                        else 
                                            R.drawable.ic_visibility
                                    ),
                                    contentDescription = if (passwordVisible) "Hide Password" else "Show Password",
                                    tint = Color(0xFF6B7280)
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier
                            .fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF3B82F6),
                            unfocusedBorderColor = Color(0xFFD1D5DB)
                        )
                    )
                }
                
                // Remember my options and forget the password
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Left: Remember Me option
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.offset(x = (-12).dp)
                    ) {
                        Checkbox(
                            checked = rememberMe,
                            onCheckedChange = { rememberMe = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = Color(0xFF2563EB)
                            )
                        )
                        
                        Text(
                            text = "Remember me",
                            fontSize = 14.sp,
                            color = Color(0xFF4B5563)
                        )
                    }
                    
                    // Right side: Forgot password
                    Text(
                        text = "Forgot password?",
                        color = Color(0xFF2563EB),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.clickable { 
                            if (email.isBlank()) {
                                errorMessage = "Please enter your email address"
                                return@clickable
                            }
                            
                            isLoading = true
                            errorMessage = null
                            
                            auth.sendPasswordResetEmail(email)
                                .addOnCompleteListener { task ->
                                    isLoading = false
                                    if (task.isSuccessful) {
                                        Toast.makeText(
                                            context,
                                            "Password reset email sent!",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    } else {
                                        errorMessage = task.exception?.message ?: "Failed to send password reset email"
                                    }
                                }
                        }
                    )
                }
                
                // Display error message
                errorMessage?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(bottom = 16.dp),
                        textAlign = TextAlign.Center
                    )
                }
                
                // Login Button
                Button(
                    onClick = {
                        if (email.isBlank() || password.isBlank()) {
                            errorMessage = "Please enter both email and password"
                            return@Button
                        }
                        
                        // Save the email path name
                        saveEmail(email, rememberMe)
                        
                        isLoading = true
                        errorMessage = null
                        
                        coroutineScope.launch {
                            try {
                                auth.signInWithEmailAndPassword(email, password)
                                    .addOnCompleteListener { task ->
                                        isLoading = false
                                        if (task.isSuccessful) {
                                            Log.d("LoginScreen", "signInWithEmail:success")
                                            // Refresh user information after successful login
                                            coroutineScope.launch(Dispatchers.IO) {
                                                try {
                                                    // Refresh Firebase user information
                                                    firebaseUserRepository.refreshCurrentUser()
                                                    
                                                    // Get the latest user UID and record it in the log
                                                    val uid = firebaseUserRepository.getCurrentUserId()
                                                    Log.d("LoginScreen", "User logged in with UID: $uid")
                                                    
                                                    // After successful refresh, the login is successful.
                                                    withContext(Dispatchers.Main) {
                                                        Toast.makeText(context, "Login successful!", Toast.LENGTH_SHORT).show()
                                                        onLoginSuccess()
                                                    }
                                                } catch (e: Exception) {
                                                    Log.e("LoginScreen", "Error refreshing user: ${e.message}")
                                                    withContext(Dispatchers.Main) {
                                                        Toast.makeText(context, "Login successful!", Toast.LENGTH_SHORT).show()
                                                        onLoginSuccess()
                                                    }
                                                }
                                            }
                                        } else {
                                            Log.w("LoginScreen", "signInWithEmail:failure", task.exception)
                                            errorMessage = task.exception?.message ?: "Authentication failed"
                                        }
                                    }
                            } catch (e: Exception) {
                                isLoading = false
                                errorMessage = e.message ?: "An unexpected error occurred"
                                Log.e("LoginScreen", "Login error", e)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2563EB)
                    ),
                    enabled = !isLoading && email.isNotBlank() && password.isNotBlank()
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Sign In",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                
                // Add a separator line and "or" text
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Divider(
                        modifier = Modifier.weight(1f),
                        color = Color(0xFFD1D5DB)
                    )
                    Text(
                        text = "OR",
                        modifier = Modifier.padding(horizontal = 16.dp),
                        fontSize = 14.sp,
                        color = Color(0xFF6B7280)
                    )
                    Divider(
                        modifier = Modifier.weight(1f),
                        color = Color(0xFFD1D5DB)
                    )
                }
                
                // Google login button
                OutlinedButton(
                    onClick = {
                        isLoading = true
                        errorMessage = null
                        Log.d("LoginScreen", "Attempting Google Sign-In")
                        val signInIntent = googleSignInClient.signInIntent
                        googleSignInLauncher.launch(signInIntent)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color(0xFFD1D5DB)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF374151)
                    )
                ) {
                    // Our Logo Project
                    Icon(
                        painter = painterResource(id = R.drawable.ic_google),
                        contentDescription = "Google",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isLoading && errorMessage == null) "Signing in..." else "Sign in with Google",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                // Registration link
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Don't have an account? ",
                        fontSize = 14.sp,
                        color = Color(0xFF6B7280)
                    )
                    
                    Text(
                        text = "Sign up",
                        fontSize = 14.sp,
                        color = Color(0xFF2563EB),
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.clickable { onRegisterClick() }
                    )
                }
            }
        }
    }
}

private fun firebaseAuthWithGoogle(
    idToken: String,
    auth: FirebaseAuth,
    onLoginSuccess: () -> Unit,
    onError: (String) -> Unit,
    coroutineScope: kotlinx.coroutines.CoroutineScope,
    onLoadingChange: (Boolean) -> Unit
) {
    onLoadingChange(true)
    val credential = GoogleAuthProvider.getCredential(idToken, null)
    val firebaseUserRepository = FirebaseUserRepository()
    
    coroutineScope.launch {
        try {
            auth.signInWithCredential(credential).await()
            Log.d("LoginScreen", "Firebase signInWithCredential success")
            
            // Refresh Firebase user information
            try {
                firebaseUserRepository.refreshCurrentUser()
                val uid = firebaseUserRepository.getCurrentUserId()
                Log.d("LoginScreen", "User logged in with UID: $uid")
            } catch (e: Exception) {
                Log.e("LoginScreen", "Error refreshing user after Google login: ${e.message}")
            }
            
            onLoginSuccess()
        } catch (e: Exception) {
            Log.w("LoginScreen", "Firebase signInWithCredential failed", e)
            onError("Firebase authentication failed: ${e.localizedMessage}")
        } finally {
            onLoadingChange(false)
        }
    }
}