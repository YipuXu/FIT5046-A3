package com.example.fitlife.ui.coach

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitlife.R
import com.example.fitlife.ui.components.BottomNavBar
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import com.example.fitlife.ui.components.BottomNavBar


data class Message(
    val content: String,
    val isFromUser: Boolean,
    val isCentered: Boolean = false,
    val details: List<String> = emptyList(),
    val sections: Map<String, List<String>> = emptyMap()
)

data class QuickQuestion(val text: String)

data class AIFeature(
    val icon: Int,
    val title: String,
    val backgroundColor: Color,
    val iconColor: Color
)


@Composable
fun AICoachScreen(
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToCalendar: () -> Unit,
    onNavigateToMap: () -> Unit,
    onNavigateToProfile: () -> Unit,
    currentRoute: String
) {
    var messageText by remember { mutableStateOf("") }
    
    val messages = remember {
        mutableStateListOf(
            Message(
                content = "Hello! I'm your AI fitness coach. How can I help you today?",
                isFromUser = false,
                isCentered = true
            ),
            Message(
                content = "I want to build muscle and lose fat. Any suggestions?",
                isFromUser = true
            ),
            Message(
                content = "Building muscle while losing fat requires a balanced approach to diet and training. Recommendations:",
                isFromUser = false,
                details = listOf(
                    "Control calorie intake, maintain a moderate deficit",
                    "Increase protein intake (1.6-2.2g per kg of body weight)",
                    "Perform strength training 3-4 times per week",
                    "Include moderate cardio (2-3 times weekly, 20-30 minutes each session)"
                )
            ),
            Message(
                content = "Yes, please help me create a detailed plan",
                isFromUser = true
            ),
            Message(
                content = "Based on your needs, I've created a 7-day muscle building and fat loss plan:",
                isFromUser = false,
                sections = mapOf(
                    "Dietary Recommendations:" to listOf(
                        "Daily calories: BMR minus 300-500 calories",
                        "Protein: 2g per kg of body weight",
                        "Carbohydrates: 40-50% of total calories",
                        "Fat: 20-30% of total calories"
                    ),
                    "Training Schedule:" to listOf(
                        "Monday: Upper body strength training",
                        "Tuesday: 20 minutes HIIT",
                        "Wednesday: Lower body strength training",
                        "Thursday: Rest",
                        "Friday: Full body strength training",
                        "Saturday: 30 minutes moderate cardio",
                        "Sunday: Rest and recovery"
                    )
                )
            )
        )
    }
    
    val quickQuestions = remember {
        listOf(
            QuickQuestion("How to start fitness training?"),
            QuickQuestion("Recommended protein sources"),
            QuickQuestion("Create a fat loss plan"),
            QuickQuestion("Muscle building exercises")
        )
    }
    
    val features = remember {
        listOf(
            AIFeature(
                R.drawable.ic_fitness,
                "Personalized Fitness Plan",
                Color(0xFFEBF5FF),
                Color(0xFF3B82F6)
            ),
            AIFeature(
                R.drawable.ic_restaurant,
                "Nutrition Recommendations",
                Color(0xFFECFDF5),
                Color(0xFF10B981)
            ),
            AIFeature(
                R.drawable.ic_calendar,
                "Training Progress Tracking",
                Color(0xFFF5F3FF),
                Color(0xFF8B5CF6)
            ),
            AIFeature(
                R.drawable.ic_help,
                "Fitness Q&A",
                Color(0xFFFEF3C7),
                Color(0xFFD97706)
            )
        )
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9FAFB))
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp)
        ) {
            // 顶部栏
            TopAppBar(
                onNavigateBack = onNavigateBack
            )
            
            // AI教练头像和介绍
            AICoachHeader()
            
            // 聊天界面（包含输入框）
            ChatSection(
                messages = messages,
                messageText = messageText,
                onMessageTextChange = { messageText = it },
                onSendClick = {
                    if (messageText.isNotEmpty()) {
                        messages.add(Message(messageText, true))
                        messageText = ""
                    }
                }
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            // 快捷问题
            QuickQuestionsRow(
                questions = quickQuestions,
                onQuestionClick = { question ->
                    messages.add(Message(question.text, true))
                }
            )
        }
        
        // 底部导航
        BottomNavBar(
            currentRoute = "profile",
            onNavigateToHome = onNavigateToHome,
            onNavigateToCalendar = onNavigateToCalendar,
            onNavigateToMap = onNavigateToMap,
            onNavigateToProfile = onNavigateToProfile,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun TopAppBar(
    onNavigateBack: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 返回按钮
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(Color(0xFFF3F4F6))
                .clickable { onNavigateBack() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier.size(20.dp),
                tint = Color(0xFF6B7280)
            )
        }
        
        // 标题
        Text(
            text = "AI Coach",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            textAlign = TextAlign.Center,
            color = Color(0xFF1F2937)
        )
        
        // Placeholder for symmetry (ensures title centers correctly)
        Spacer(modifier = Modifier.size(32.dp))
    }
}

@Composable
fun AICoachHeader() {
    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF3B82F6),
                            Color(0xFF4F46E5)
                        )
                    )
                )
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // AI头像
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(Color(0xFFEBF5FF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_person),
                            contentDescription = "AI Avatar",
                            modifier = Modifier.size(40.dp),
                            tint = Color(0xFF3B82F6)
                        )
                    }
                }
                
                // AI介绍
                Column(
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .weight(1f)
                ) {
                    Text(
                        text = "FitLife AI",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Your personal fitness advisor, providing professional guidance anytime",
                        color = Color(0xFFBFDBFE),
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

//@Composable
//fun FeaturesGrid(features: List<AIFeature>) {
//    Column(
//        modifier = Modifier
//            .padding(horizontal = 16.dp, vertical = 8.dp)
//    ) {
//        Text(
//            text = "我能帮您做什么",
//            fontSize = 16.sp,
//            fontWeight = FontWeight.SemiBold,
//            color = Color(0xFF374151),
//            modifier = Modifier.padding(bottom = 12.dp)
//        )
//
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.spacedBy(8.dp)
//        ) {
//            features.take(2).forEach { feature ->
//                FeatureCard(
//                    feature = feature,
//                    modifier = Modifier.weight(1f)
//                )
//            }
//        }
//
//        Spacer(modifier = Modifier.height(8.dp))
//
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.spacedBy(8.dp)
//        ) {
//            features.drop(2).forEach { feature ->
//                FeatureCard(
//                    feature = feature,
//                    modifier = Modifier.weight(1f)
//                )
//            }
//        }
//    }
//}

@Composable
fun FeatureCard(
    feature: AIFeature,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(100.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(feature.backgroundColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = feature.icon),
                    contentDescription = feature.title,
                    tint = feature.iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = feature.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1F2937),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ChatSection(
    messages: List<Message>,
    messageText: String,
    onMessageTextChange: (String) -> Unit,
    onSendClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Chat with AI Coach",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF374151),
            modifier = Modifier.padding(vertical = 12.dp)
        )
        
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(480.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // 聊天消息列表
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 2.dp)
                ) {
                    items(messages) { message ->
                        MessageItem(message = message)
                    }
                }
                
                // 输入框
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    OutlinedTextField(
                        value = messageText,
                        onValueChange = onMessageTextChange,
                        placeholder = {
                            Text(
                                text = "Type your question...",
                                fontSize = 14.sp,
                                color = Color(0xFF9CA3AF)
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(28.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF3B82F6),
                            unfocusedBorderColor = Color(0xFFE5E7EB),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            cursorColor = Color(0xFF3B82F6),
                            focusedTextColor = Color(0xFF1F2937),
                            unfocusedTextColor = Color(0xFF1F2937)
                        ),
                        trailingIcon = {
                            IconButton(
                                onClick = onSendClick,
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF3B82F6))
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_send_arrow),
                                    contentDescription = "Send",
                                    tint = Color.White,
                                    modifier = Modifier
                                        .size(20.dp)
                                )
                            }
                        },
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
fun MessageItem(message: Message) {
    if (message.isCentered) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF3F4F6)
                )
            ) {
                Text(
                    text = message.content,
                    fontSize = 12.sp,
                    color = Color(0xFF6B7280),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    } else if (message.isFromUser) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterEnd
        ) {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF3B82F6)
                )
            ) {
                Text(
                    text = message.content,
                    fontSize = 12.sp,
                    color = Color.White,
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .widthIn(max = 240.dp)
                )
            }
        }
    } else {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterStart
        ) {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF3F4F6)
                )
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .widthIn(max = 240.dp)
                ) {
                    Text(
                        text = message.content,
                        fontSize = 12.sp,
                        color = Color(0xFF374151)
                    )
                    
                    if (message.details.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        message.details.forEach { detail ->
                            Row(
                                modifier = Modifier.padding(top = 2.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text(
                                    text = "• ",
                                    fontSize = 12.sp,
                                    color = Color(0xFF374151)
                                )
                                Text(
                                    text = detail,
                                    fontSize = 12.sp,
                                    color = Color(0xFF374151)
                                )
                            }
                        }
                    }
                    
                    if (message.sections.isNotEmpty()) {
                        message.sections.forEach { (title, items) ->
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = title,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF374151)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            items.forEach { item ->
                                Row(
                                    modifier = Modifier.padding(top = 2.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Text(
                                        text = "• ",
                                        fontSize = 12.sp,
                                        color = Color(0xFF374151)
                                    )
                                    Text(
                                        text = item,
                                        fontSize = 12.sp,
                                        color = Color(0xFF374151)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MessageInput(
    value: String,
    onValueChange: (String) -> Unit,
    onSendClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = "Type your question...",
                    fontSize = 14.sp,
                    color = Color(0xFF9CA3AF)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF3B82F6),
                unfocusedBorderColor = Color(0xFFE5E7EB),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                cursorColor = Color(0xFF3B82F6),
                focusedTextColor = Color(0xFF1F2937),
                unfocusedTextColor = Color(0xFF1F2937)
            ),
            trailingIcon = {
                IconButton(
                    onClick = onSendClick,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF3B82F6))
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_send_arrow),
                        contentDescription = "Send",
                        tint = Color.White,
                        modifier = Modifier
                            .size(20.dp)
                    )
                }
            },
            maxLines = 1
        )
    }
}

@Composable
fun QuickQuestionsRow(
    questions: List<QuickQuestion>,
    onQuestionClick: (QuickQuestion) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(questions) { question ->
            Card(
                modifier = Modifier
                    .clickable { onQuestionClick(question) },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                border = BorderStroke(1.dp, Color(0xFFE5E7EB))
            ) {
                Text(
                    text = question.text,
                    fontSize = 12.sp,
                    color = Color(0xFF4B5563),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }
        }
    }
}