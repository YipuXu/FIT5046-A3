package com.example.fitlife.ui.auth

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitlife.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalContext

// Data class to hold password validation state
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

// Helper function to validate password
fun validatePassword(password: String): PasswordValidationState {
    return PasswordValidationState(
        hasMinLength = password.length >= 12,
        hasUppercase = password.any { it.isUpperCase() },
        hasLowercase = password.any { it.isLowerCase() },
        hasNumber = password.any { it.isDigit() },
        hasSpecialChar = password.any { !it.isLetterOrDigit() }
    )
}

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToTerms: () -> Unit,
    onNavigateToPrivacy: () -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var agreeToTerms by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var passwordValidationState by remember { mutableStateOf(PasswordValidationState()) }
    var showPasswordCriteria by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val auth: FirebaseAuth = Firebase.auth
    val context = LocalContext.current

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
            // 顶部背景图和标题
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF4F46E5),
                                Color(0xFF4338CA)
                            )
                        )
                    )
            ) {
                // 背景图片
                Image(
                    painter = painterResource(id = R.drawable.fitness_background),
                    contentDescription = "Fitness Background",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF4F46E5).copy(alpha = 0.4f))
                )
                
                // 标题文本
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
            
            // 注册表单
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                // 创建账户文本
                Text(
                    text = "Create Account",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1F2937),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    textAlign = TextAlign.Start
                )
                
                // 全名输入框
                Column(modifier = Modifier.padding(bottom = 16.dp)) {
                    Text(
                        text = "Full Name",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF374151),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        placeholder = { Text("Your Full Name") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Person") },
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
                
                // 邮箱输入框
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
                
                // 密码输入框
                Column(modifier = Modifier.padding(bottom = 16.dp)) {
                    Text(
                        text = "Password",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF374151),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    
                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            passwordValidationState = validatePassword(it)
                        },
                        placeholder = { Text("••••••••") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { focusState ->
                                if (focusState.isFocused) {
                                    showPasswordCriteria = true
                                }
                                // Optionally, you can set showPasswordCriteria = false when focus is lost
                                // else if (!focusState.isFocused && password.isEmpty()) {
                                // showPasswordCriteria = false
                                // }
                            },
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF3B82F6),
                            unfocusedBorderColor = Color(0xFFD1D5DB)
                        )
                    )

                    if (showPasswordCriteria) {
                        PasswordCriteriaView(passwordValidationState)
                    }
                }
                
                // 确认密码输入框
                Column(modifier = Modifier.padding(bottom = 16.dp)) {
                    Text(
                        text = "Confirm Password",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF374151),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        placeholder = { Text("••••••••") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Confirm Password") },
                        visualTransformation = PasswordVisualTransformation(),
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
                
                // 同意条款选项
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween // 两端对齐
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.offset(x = (-12).dp) // 通过负偏移量去除 Checkbox 左侧的默认空白
                    ) {
                    Checkbox(
                        checked = agreeToTerms,
                        onCheckedChange = { agreeToTerms = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color(0xFF2563EB)
                        )
                    )
                    
                    // 将文本放在一个可以换行的Column中
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "I agree to the ",
                                fontSize = 14.sp,
                                color = Color(0xFF4B5563)
                            )
                            
                            Text(
                                text = "Terms of Service",
                                fontSize = 14.sp,
                                color = Color(0xFF2563EB),
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.clickable { onNavigateToTerms() }
                            )
                            
                            Text(
                                text = " , ",
                                fontSize = 14.sp,
                                color = Color(0xFF4B5563)
                            )
                            
                            Text(
                                text = "Privacy Policy",
                                fontSize = 14.sp,
                                color = Color(0xFF2563EB),
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.clickable { onNavigateToPrivacy() }
                            )
                        }
                    }
                }
                }
                
                // 注册按钮
                Button(
                    onClick = {
                        if (password != confirmPassword) {
                            errorMessage = "Passwords do not match."
                            return@Button
                        }
                        if (!agreeToTerms) {
                            errorMessage = "You must agree to the terms and conditions."
                            return@Button
                        }
                        errorMessage = null // Clear previous errors
                        isLoading = true
                        coroutineScope.launch {
                            try {
                                auth.createUserWithEmailAndPassword(email, password)
                                    .addOnCompleteListener { task ->
                                        isLoading = false
                                        if (task.isSuccessful) {
                                            // Registration success
                                            Log.d("RegisterScreen", "createUserWithEmail:success")
                                            // 显示注册成功的Toast
                                            Toast.makeText(
                                                context, 
                                                "Registered Successfully!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            // 跳转到登录页面
                                            onNavigateToLogin()
                                        } else {
                                            // If sign in fails, display a message to the user.
                                            Log.w("RegisterScreen", "createUserWithEmail:failure", task.exception)
                                            errorMessage = task.exception?.message ?: "Registration failed."
                                        }
                                    }
                            } catch (e: Exception) {
                                isLoading = false
                                errorMessage = e.message ?: "An unexpected error occurred."
                                Log.e("RegisterScreen", "Registration error", e)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2563EB),
                        contentColor = Color.White
                    ),
                    enabled = !isLoading && agreeToTerms && email.isNotBlank() &&
                            password.isNotBlank() && confirmPassword.isNotBlank() && fullName.isNotBlank() &&
                            password == confirmPassword && passwordValidationState.isValid
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Create Account", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                // 显示错误信息
                errorMessage?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 8.dp),
                        textAlign = TextAlign.Center
                    )
                }

                // 登录链接
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Already have an account? ",
                        fontSize = 14.sp,
                        color = Color(0xFF6B7280)
                    )
                    
                    Text(
                        text = "Sign in",
                        fontSize = 14.sp,
                        color = Color(0xFF2563EB),
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.clickable { onNavigateToLogin() }
                    )
                }
            }
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