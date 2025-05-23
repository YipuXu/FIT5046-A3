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
                                    // Show success message
                                    Toast.makeText(
                                        context,
                                        "Feedback submitted successfully", 
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    
                                    // Clear input field
                                    feedbackText = ""
                                    isLoading = false
                                },
                                onError = { errorMessage ->
                                    // Show error message
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
                        // If feedback is empty, prompt user
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
 * Saves feedback to Firestore database
 */
suspend fun saveFeedbackToFirestore(
    context: Context,
    feedback: String,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    try {
        val db = FirebaseFirestore.getInstance()
        
        // Create feedback data
        val feedbackData = hashMapOf(
            "content" to feedback,
            "timestamp" to Date(),
            "status" to "new"
        )
        
        // Save to Firestore
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
        FaqItem(question = "How do I track my workouts?", answer = "前往个人资料页（Profile），点击"查看全部"（View All）即可查看所有锻炼记录。您也可以在主页通过"+"按钮记录新的锻炼。")
        FaqItem(question = "How can I change my fitness goals?", answer = "进入个人资料页（Profile）> 点击"编辑"（Edit）按钮 > 选择"健身目标"（Fitness Goal）选项，然后从列表中选择您的新目标。系统会自动为您调整推荐计划。")
        FaqItem(question = "Where can I see my progress?", answer = "您可以在个人资料页面（Profile）查看统计数据（锻炼天数、连续锻炼天数和总训练次数）以及主页（Home）的周健身仪表盘（当周锻炼次数，当周锻炼时间，当周消耗卡路里数），详细的锻炼历史记录可以在日历页面（Calendar）或点击"查看全部"（View All）按钮访问。")
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