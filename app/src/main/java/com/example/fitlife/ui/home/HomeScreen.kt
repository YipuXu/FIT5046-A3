package com.example.fitlife.ui.home

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    currentRoute: String,
    onNavigateToHome: () -> Unit,
    onNavigateToCalendar: () -> Unit,
    onNavigateToMap: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
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
            WelcomeSection()
//            WeeklySummaryCard()
//            TodayPlanCard()
//            ArticleRecommendation()
//            MusicPlayerSection()
        }

    }
}
@Composable
fun WelcomeSection(userName: String = "User") {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // 左侧欢迎文本
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

        // 右侧头像区域（暂时留空白 + 边框）
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .border(1.dp, Color.LightGray, CircleShape)
        )
    }
}
//@Composable
//fun WeeklySummaryCard() {
//    var trainingCount by remember { mutableStateOf("0") }
//    var totalHours by remember { mutableStateOf("0") }
//    var calories by remember { mutableStateOf("0") }
//
//    var showConfirmDialog by remember { mutableStateOf(false) }
//
//    Card(
//        modifier = Modifier.fillMaxWidth(),
//        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
//        elevation = CardDefaults.cardElevation(4.dp),
//        shape = RoundedCornerShape(16.dp)
//    ) {
//        Column(modifier = Modifier.padding(16.dp)) {
//            // 顶部标题 + 刷新按钮
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Text(
//                    text = "This week's fitness overview",
//                    style = MaterialTheme.typography.titleMedium
//                )
//
//                IconButton(onClick = { showConfirmDialog = true }) {
//                    Icon(
//                        imageVector = Icons.Default.Refresh,
//                        contentDescription = "Refresh"
//                    )
//                }
//            }
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween
//            ) {
//                SummaryItem(
//                    title = "Number of training",
//                    number = trainingCount,
//                    unit = "",
//                    onEdit = { trainingCount = it }
//                )
//                SummaryItem(
//                    title = "Total duration",
//                    number = totalHours,
//                    unit = "hours",
//                    onEdit = { totalHours = it }
//                )
//                SummaryItem(
//                    title = "Burn calories",
//                    number = calories,
//                    unit = "kcal",
//                    onEdit = { calories = it }
//                )
//            }
//        }
//    }
//
//    // 确认弹窗
//    if (showConfirmDialog) {
//        AlertDialog(
//            onDismissRequest = { showConfirmDialog = false },
//            title = { Text("Confirm Refresh") },
//            text = { Text("Are you sure you want to refresh all data?") },
//            confirmButton = {
//                TextButton(onClick = {
//                    trainingCount = "0"
//                    totalHours = "0"
//                    calories = "0"
//                    showConfirmDialog = false
//                }) {
//                    Text("Yes")
//                }
//            },
//            dismissButton = {
//                TextButton(onClick = { showConfirmDialog = false }) {
//                    Text("Cancel")
//                }
//            }
//        )
//    }
//}
//
//
//@Composable
//fun SummaryItem(
//    title: String,
//    number: String,              // 显示用的数字
//    unit: String,                // 单位，例如 "kcal"
//    onEdit: (String) -> Unit     // 编辑后返回的纯数字
//) {
//    var showDialog by remember { mutableStateOf(false) }
//    var inputText by remember { mutableStateOf(number) }
//
//    Column(
//        modifier = Modifier.clickable { showDialog = true },
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Text(text = title, style = MaterialTheme.typography.labelMedium, color = Color.Gray)
//        Spacer(modifier = Modifier.height(4.dp))
//        Text(text = "$number $unit", style = MaterialTheme.typography.titleSmall)
//    }
//
//    if (showDialog) {
//        AlertDialog(
//            onDismissRequest = { showDialog = false },
//            title = { Text("Edit $title") },
//            text = {
//                TextField(
//                    value = inputText,
//                    onValueChange = { inputText = it.filter { c -> c.isDigit() || c == '.' } },
//                    label = { Text("Please enter a number") },
//                    singleLine = true
//                )
//            },
//            confirmButton = {
//                TextButton(onClick = {
//                    if (inputText.isNotBlank()) {
//                        onEdit(inputText)
//                    }
//                    showDialog = false
//                }) {
//                    Text("Yes")
//                }
//            },
//            dismissButton = {
//                TextButton(onClick = { showDialog = false }) {
//                    Text("Cancel")
//                }
//            }
//        )
//    }
//}



