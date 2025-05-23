package com.example.fitlife.ui.help

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Date

@Composable
fun HelpFeedbackScreen(
    onBackClick: () -> Unit = {}
) {
    var feedbackText by remember { mutableStateOf("") }
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9FAFB))
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        // Fixed top bar
        TopBar(
            onBackClick = onBackClick,
            modifier = Modifier
                .fillMaxWidth()
                .zIndex(1f)
                .background(Color(0xFFF9FAFB))
        )

        // Scrollable content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 64.dp) // Padding for top bar
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            // FAQ Section
            FaqSection()
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Feedback Section
            FeedbackSection(feedbackText = feedbackText, onValueChange = { feedbackText = it })
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Submit Button
            Button(
                onClick = { 
                    if (feedbackText.isNotBlank()) {
                        isLoading = true
                        
                        coroutineScope.launch {
                            saveFeedbackToFirestore(
                                context = context,
                                feedback = feedbackText,
                                onSuccess = {
                                    // 显示成功提示
                                    Toast.makeText(
                                        context,
                                        "Feedback submitted successfully", 
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    
                                    // 清空输入框
                                    feedbackText = ""
                                    isLoading = false
                                },
                                onError = { errorMessage ->
                                    // 显示错误提示
                                    Toast.makeText(
                                        context,
                                        errorMessage, 
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    isLoading = false
                                }
                            )
                        }
                    } else {
                        // 如果反馈为空，提示用户
                        Toast.makeText(
                            context,
                            "Please enter your feedback",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF3B82F6)
                ),
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Submit Feedback", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

/**
 * 将反馈保存到Firestore数据库
 */
suspend fun saveFeedbackToFirestore(
    context: Context,
    feedback: String,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    try {
        val db = FirebaseFirestore.getInstance()
        
        // 创建反馈数据
        val feedbackData = hashMapOf(
            "content" to feedback,
            "timestamp" to Date(),
            "status" to "new"
        )
        
        // 保存到Firestore
        db.collection("feedback")
            .add(feedbackData)
            .await()
            
        onSuccess()
        
    } catch (e: Exception) {
        onError("Submission failed: ${e.message}")
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
            .background(Color(0xFFF9FAFB))
    ) {
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
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier.size(20.dp),
                    tint = Color(0xFF6B7280)
                )
            }

            // Title
            Text(
                text = "Help & Feedback",
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
private fun FaqSection() {
    Column {
        Text(
            text = "Frequently Asked Questions",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1F2937),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Example FAQ Items (add more as needed)
        FaqItem(question = "How do I track my workouts?", answer = "Go to the Home screen and tap the '+' button to start logging your activity.")
        FaqItem(question = "How can I change my fitness goals?", answer = "Navigate to Profile > Edit Profile > Fitness Goal to update your objectives.")
        FaqItem(question = "Where can I see my progress?", answer = "Your progress statistics are available on the Profile screen and detailed history can be viewed in the Calendar section.")
    }
}

@Composable
private fun FaqItem(question: String, answer: String) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = question,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF1F2937),
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    tint = Color(0xFF6B7280)
                )
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = answer,
                    fontSize = 14.sp,
                    color = Color(0xFF4B5563),
                    lineHeight = 20.sp
                )
            }
        }
    }
}

@Composable
private fun FeedbackSection(feedbackText: String, onValueChange: (String) -> Unit) {
    Column {
        Text(
            text = "Submit Feedback",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1F2937),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        OutlinedTextField(
            value = feedbackText,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            placeholder = { Text("Describe your issue or suggestion...") },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF3B82F6),
                unfocusedBorderColor = Color(0xFFD1D5DB)
            )
        )
    }
} 