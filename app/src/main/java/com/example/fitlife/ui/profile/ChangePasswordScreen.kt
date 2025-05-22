package com.example.fitlife.ui.profile

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.fitlife.R
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import androidx.compose.runtime.saveable.rememberSaveable
import kotlinx.coroutines.CoroutineScope

// 密码验证状态数据类，与RegisterScreen相同
data class PasswordValidationState(
    val hasMinLength: Boolean = false,
    val hasUppercase: Boolean = false,
    val hasLowercase: Boolean = false,
    val hasNumber: Boolean = false,
    val hasSpecialChar: Boolean = false
) {
    val isValid: Boolean
        get() = hasMinLength && hasUppercase && hasLowercase && hasNumber && hasSpecialChar
}

// 密码验证函数，与RegisterScreen相同
fun validatePassword(password: String): PasswordValidationState {
    return PasswordValidationState(
        hasMinLength = password.length >= 12,
        hasUppercase = password.any { it.isUpperCase() },
        hasLowercase = password.any { it.isLowerCase() },
        hasNumber = password.any { it.isDigit() },
        hasSpecialChar = password.any { !it.isLetterOrDigit() }
    )
}

sealed class PasswordChangeState {
    object Initial : PasswordChangeState()
    object Loading : PasswordChangeState()
    object Success : PasswordChangeState()
    data class Error(val message: String) : PasswordChangeState()
}

@Composable
fun ChangePasswordScreen(
    onBackClick: () -> Unit = {},
    onChangePassword: (current: String, new: String) -> Unit = { _, _ -> },
    onLogout: () -> Unit = {}  // 添加登出回调函数
) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var passwordChangeState by remember { mutableStateOf<PasswordChangeState>(PasswordChangeState.Initial) }
    
    // 密码可视性控制
    var currentPasswordVisible by rememberSaveable { mutableStateOf(false) }
    var newPasswordVisible by rememberSaveable { mutableStateOf(false) }
    var confirmPasswordVisible by rememberSaveable { mutableStateOf(false) }
    
    // 密码验证状态
    var newPasswordValidationState by remember { mutableStateOf(PasswordValidationState()) }
    var showPasswordCriteria by remember { mutableStateOf(false) }
    
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    
    // 监视密码更改状态，当成功时显示Toast并延迟登出
    LaunchedEffect(passwordChangeState) {
        if (passwordChangeState is PasswordChangeState.Success) {
            // 显示Toast消息
            Toast.makeText(
                context,
                "Password changed successfully!",
                Toast.LENGTH_SHORT
            ).show()
            
            // 延迟2秒后登出
            delay(2000)
            
            // 执行登出操作
            try {
                FirebaseAuth.getInstance().signOut()
                Log.d("ChangePasswordScreen", "User logged out successfully")
                
                // 调用登出回调函数，处理UI导航
                onLogout()
            } catch (e: Exception) {
                Log.e("ChangePasswordScreen", "Error during logout: ${e.message}")
                
                // 即使发生错误也尝试执行登出回调
                onLogout()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        // Fixed top bar
        TopBar(
            onBackClick = onBackClick,
            modifier = Modifier
                .fillMaxWidth()
                .zIndex(1f)
                .background(Color.White)
        )

        // Scrollable content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 64.dp) // Padding for top bar
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            // Current Password
            Column(modifier = Modifier.padding(bottom = 16.dp)) {
                Text(
                    text = "Current Password",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF374151),
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                
                OutlinedTextField(
                    value = currentPassword,
                    onValueChange = { 
                        currentPassword = it
                        passwordError = null 
                        passwordChangeState = PasswordChangeState.Initial
                    },
                    placeholder = { Text("••••••••") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password") },
                    visualTransformation = if (currentPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    trailingIcon = {
                        IconButton(onClick = { currentPasswordVisible = !currentPasswordVisible }) {
                            Icon(
                                imageVector = if (currentPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (currentPasswordVisible) "Hide password" else "Show password"
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF3B82F6),
                        unfocusedBorderColor = Color(0xFFD1D5DB)
                    )
                )
            }

            // New Password
            Column(modifier = Modifier.padding(bottom = 16.dp)) {
                Text(
                    text = "New Password",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF374151),
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { 
                        newPassword = it
                        newPasswordValidationState = validatePassword(it)
                        passwordError = null 
                        passwordChangeState = PasswordChangeState.Initial
                    },
                    placeholder = { Text("••••••••") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password") },
                    visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { focusState ->
                            if (focusState.isFocused) {
                                showPasswordCriteria = true
                            }
                        },
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    trailingIcon = {
                        IconButton(onClick = { newPasswordVisible = !newPasswordVisible }) {
                            Icon(
                                imageVector = if (newPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (newPasswordVisible) "Hide password" else "Show password"
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF3B82F6),
                        unfocusedBorderColor = Color(0xFFD1D5DB)
                    )
                )
                
                // 显示密码验证标准
                if (showPasswordCriteria) {
                    PasswordCriteriaView(newPasswordValidationState)
                }
            }

            // Confirm New Password
            Column(modifier = Modifier.padding(bottom = 16.dp)) {
                Text(
                    text = "Confirm New Password",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF374151),
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { 
                        confirmPassword = it
                        passwordError = null 
                        passwordChangeState = PasswordChangeState.Initial
                    },
                    placeholder = { Text("••••••••") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Confirm Password") },
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    trailingIcon = {
                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Icon(
                                imageVector = if (confirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password"
                            )
                        }
                    },
                    isError = passwordError != null,
                    supportingText = {
                        if (passwordError != null) {
                            Text(text = passwordError!!, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF3B82F6),
                        unfocusedBorderColor = Color(0xFFD1D5DB),
                        errorBorderColor = MaterialTheme.colorScheme.error
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 显示密码修改状态 - 只保留错误消息，成功消息改为Toast
            if (passwordChangeState is PasswordChangeState.Error) {
                Text(
                    text = (passwordChangeState as PasswordChangeState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    textAlign = TextAlign.Center
                )
            }

            // Save Button
            Button(
                onClick = {
                    if (!newPasswordValidationState.isValid) {
                        passwordError = "Password does not meet all requirements"
                    } else if (newPassword != confirmPassword) {
                        passwordError = "New passwords do not match"
                    } else {
                        passwordError = null
                        // 使用Firebase Auth更新密码
                        updatePasswordWithFirebase(
                            coroutineScope = coroutineScope,
                            currentPassword = currentPassword,
                            newPassword = newPassword,
                            onStateChange = { state -> passwordChangeState = state }
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2563EB),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp),
                enabled = currentPassword.isNotEmpty() && 
                         newPassword.isNotEmpty() && 
                         confirmPassword.isNotEmpty() && 
                         passwordChangeState != PasswordChangeState.Loading
            ) {
                if (passwordChangeState == PasswordChangeState.Loading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text("Save Changes", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// 使用Firebase Auth更新密码
private fun updatePasswordWithFirebase(
    coroutineScope: CoroutineScope,
    currentPassword: String,
    newPassword: String,
    onStateChange: (PasswordChangeState) -> Unit
) {
    coroutineScope.launch {
        try {
            onStateChange(PasswordChangeState.Loading)
            val user = FirebaseAuth.getInstance().currentUser
            
            if (user != null && user.email != null) {
                // 重新验证用户身份
                val credential = EmailAuthProvider.getCredential(user.email!!, currentPassword)
                
                try {
                    // 重新验证用户
                    user.reauthenticate(credential).await()
                    
                    // 更新密码
                    user.updatePassword(newPassword).await()
                    
                    onStateChange(PasswordChangeState.Success)
                    Log.d("ChangePasswordScreen", "Password updated successfully")
                } catch (e: Exception) {
                    val errorMsg = when {
                        e.message?.contains("password is invalid") == true -> "Current password is incorrect"
                        else -> e.message ?: "An error occurred while updating password"
                    }
                    onStateChange(PasswordChangeState.Error(errorMsg))
                    Log.e("ChangePasswordScreen", "Error updating password", e)
                }
            } else {
                onStateChange(PasswordChangeState.Error("User not logged in or email not available"))
                Log.e("ChangePasswordScreen", "User not logged in or email not available")
            }
        } catch (e: Exception) {
            onStateChange(PasswordChangeState.Error(e.message ?: "An unexpected error occurred"))
            Log.e("ChangePasswordScreen", "Error in password update process", e)
        }
    }
}

@Composable
private fun TopBar(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 12.dp),
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
                text = "Change Password",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                textAlign = TextAlign.Center,
                color = Color(0xFF1F2937)
            )
            
            // Placeholder for symmetry
            Spacer(modifier = Modifier.size(32.dp))
        }
    }
}

@Composable
fun PasswordCriteriaView(validationState: PasswordValidationState) {
    Column(modifier = Modifier.padding(top = 8.dp, start = 4.dp, end = 4.dp)) {
        PasswordCriteriaItem(text = "At least 12 characters", isValid = validationState.hasMinLength)
        PasswordCriteriaItem(text = "At least one uppercase letter", isValid = validationState.hasUppercase)
        PasswordCriteriaItem(text = "At least one lowercase letter", isValid = validationState.hasLowercase)
        PasswordCriteriaItem(text = "At least one number", isValid = validationState.hasNumber)
        PasswordCriteriaItem(text = "At least one special character", isValid = validationState.hasSpecialChar)
    }
}

@Composable
fun PasswordCriteriaItem(text: String, isValid: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 4.dp)) {
        Icon(
            imageVector = if (isValid) Icons.Default.Check else Icons.Default.Close,
            contentDescription = if (isValid) "Valid" else "Invalid",
            tint = if (isValid) Color(0xFF10B981) else Color(0xFFEF4444), // Green for valid, Red for invalid
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            fontSize = 13.sp,
            color = if (isValid) Color(0xFF374151) else Color(0xFF4B5563)
        )
    }
} 