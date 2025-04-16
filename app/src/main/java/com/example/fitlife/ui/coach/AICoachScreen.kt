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
    onNavigateToProfile: () -> Unit
) {
    var messageText by remember { mutableStateOf("") }
    
    val messages = remember {
        mutableStateListOf(
            Message(
                content = "您好！我是您的AI健身教练，请问有什么可以帮您的？",
                isFromUser = false,
                isCentered = true
            ),
            Message(
                content = "我想增肌减脂，有什么建议？",
                isFromUser = true
            ),
            Message(
                content = "增肌减脂需要合理安排饮食和训练。建议：",
                isFromUser = false,
                details = listOf(
                    "控制热量摄入，保持适度赤字",
                    "增加蛋白质摄入（每公斤体重1.6-2.2g）",
                    "每周进行3-4次力量训练",
                    "适量有氧运动（每周2-3次，每次20-30分钟）"
                )
            ),
            Message(
                content = "是的，请帮我制定一个详细的计划",
                isFromUser = true
            ),
            Message(
                content = "好的，根据您的需求，我为您制定了一个7天的增肌减脂计划：",
                isFromUser = false,
                sections = mapOf(
                    "饮食建议：" to listOf(
                        "每日热量：基础代谢率减去300-500卡路里",
                        "蛋白质：每公斤体重2g",
                        "碳水化合物：总热量的40-50%",
                        "脂肪：总热量的20-30%"
                    ),
                    "训练计划：" to listOf(
                        "周一：上肢力量训练",
                        "周二：20分钟HIIT",
                        "周三：下肢力量训练",
                        "周四：休息",
                        "周五：全身力量训练",
                        "周六：30分钟中强度有氧",
                        "周日：休息与恢复"
                    )
                )
            )
        )
    }
    
    val quickQuestions = remember {
        listOf(
            QuickQuestion("如何开始健身？"),
            QuickQuestion("推荐的蛋白质来源"),
            QuickQuestion("制定减脂计划"),
            QuickQuestion("增肌训练动作")
        )
    }
    
    val features = remember {
        listOf(
            AIFeature(
                R.drawable.ic_fitness,  // Changed from ic_plan
                "个性化健身计划",
                Color(0xFFEBF5FF),
                Color(0xFF3B82F6)
            ),
            AIFeature(
                R.drawable.ic_restaurant,  // Changed from ic_food
                "饮食配比建议",
                Color(0xFFECFDF5),
                Color(0xFF10B981)
            ),
            AIFeature(
                R.drawable.ic_calendar,
                "训练进度跟踪",
                Color(0xFFF5F3FF),
                Color(0xFF8B5CF6)
            ),
            AIFeature(
                R.drawable.ic_help,  // Changed from ic_question
                "健身问题解答",
                Color(0xFFFEF3C7),
                Color(0xFFD97706)
            )
        )
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9FAFB))
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 顶部栏
            TopAppBar(
                onNavigateBack = onNavigateBack
            )
            
            // AI教练头像和介绍
            AICoachHeader()
            
            // 功能卡片区域
            FeaturesGrid(features)
            
            // 聊天界面
            ChatSection(messages)
            
            Spacer(modifier = Modifier.weight(1f))
            
            // 输入框
            MessageInput(
                value = messageText,
                onValueChange = { messageText = it },
                onSendClick = {
                    if (messageText.isNotEmpty()) {
                        messages.add(Message(messageText, true))
                        messageText = ""
                    }
                }
            )
            
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
            currentRoute = "coach",
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
                contentDescription = "返回",
                modifier = Modifier.size(20.dp),
                tint = Color(0xFF6B7280)
            )
        }
        
        // 标题
        Text(
            text = "AI教练",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            color = Color(0xFF1F2937)
        )
        
        // 更多选项
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(Color(0xFFF3F4F6)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "更多选项",
                modifier = Modifier.size(20.dp),
                tint = Color(0xFF6B7280)
            )
        }
    }
}

@Composable
fun AICoachHeader() {
    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
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
                            painter = painterResource(id = R.drawable.ic_person),  // Changed from ic_ai
                            contentDescription = "AI头像",
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
                        text = "您的专属健身顾问，随时为您提供专业指导",
                        color = Color(0xFFBFDBFE),
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun FeaturesGrid(features: List<AIFeature>) {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = "我能帮您做什么",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF374151),
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            features.take(2).forEach { feature ->
                FeatureCard(
                    feature = feature,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            features.drop(2).forEach { feature ->
                FeatureCard(
                    feature = feature,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

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
fun ChatSection(messages: List<Message>) {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "与AI教练对话",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF374151),
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(messages) { message ->
                    MessageItem(message = message)
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
                    text = "输入您的问题...",
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
                        painter = painterResource(id = R.drawable.ic_send_arrow),  // Changed from ic_send
                        contentDescription = "发送",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
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