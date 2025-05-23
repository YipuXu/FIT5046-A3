package com.example.fitlife.ui.train

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitlife.R
import com.example.fitlife.data.model.Workout
import com.example.fitlife.MyApplication
import kotlinx.coroutines.launch
import com.example.fitlife.data.repository.FirebaseUserRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllRecentRecordsScreen(
    onBack: () -> Unit = {},
    onAddRecord: () -> Unit = {}
) {
    val context = LocalContext.current
    val workoutDao = (context.applicationContext as MyApplication).database.workoutDao()
    
    // 创建Firebase用户仓库
    val firebaseUserRepository = remember { FirebaseUserRepository() }
    
    // 获取Firebase当前用户信息
    val firebaseUser by firebaseUserRepository.currentUser.collectAsState()
    val firebaseUid = firebaseUser?.uid
    
    // 使用用户ID获取该用户的训练记录
    val allWorkouts by remember(firebaseUid) {
        if (firebaseUid != null) {
            workoutDao.getAllOrderByDateDesc(firebaseUid)
        } else {
            workoutDao.getAllOrderByDateDesc()
        }
    }.collectAsState(initial = emptyList())
    
    val coroutineScope = rememberCoroutineScope()

    // State for the workout detail dialog
    var showDialog by remember { mutableStateOf(false) }
    var selectedWorkout by remember { mutableStateOf<Workout?>(null) }
    
    // 删除确认对话框状态
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var workoutToDelete by remember { mutableStateOf<Workout?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("All Recent Records", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddRecord,
                containerColor = Color(0xFF3B82F6),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Record")
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF9FAFB))
                .padding(padding)
        ) {
            if (allWorkouts.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No records found.", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(allWorkouts) { workout ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { // 点击整个项目查看详情
                                        selectedWorkout = workout
                                        showDialog = true
                                    }
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // 图标
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(Color(0xFFE6F0FF), RoundedCornerShape(8.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_workout),
                                        contentDescription = null,
                                        tint = Color(0xFF3B82F6),
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                // 文字信息
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(start = 12.dp)
                                ) {
                                    Text(
                                        text = workout.type,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = "${workout.date} · ${workout.duration} min · ${workout.calories} kcal",
                                        fontSize = 14.sp,
                                        color = Color(0xFF6B7280),
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                                
                                // 删除按钮
                                IconButton(
                                    onClick = {
                                        // 设置要删除的记录并显示确认对话框
                                        workoutToDelete = workout
                                        showDeleteConfirmDialog = true
                                    },
                                    modifier = Modifier.padding(start = 8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete record",
                                        tint = Color(0xFFE53935) // 红色
                                    )
                                }
                                
                                // 查看详情箭头
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowRight,
                                    contentDescription = "View details",
                                    tint = Color(0xFF9CA3AF)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // 删除确认对话框
    if (showDeleteConfirmDialog && workoutToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("删除记录") },
            text = { 
                Text(
                    "确定要删除这条${workoutToDelete!!.type}记录吗？\n" +
                    "日期: ${workoutToDelete!!.date}\n" +
                    "时长: ${workoutToDelete!!.duration}分钟"
                ) 
            },
            confirmButton = {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            // 从Room数据库删除记录
                            workoutToDelete?.let { workout ->
                                workoutDao.deleteWorkout(workout)
                            }
                        }
                        showDeleteConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)) // 红色确认按钮
                ) {
                    Text("删除")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDeleteConfirmDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9E9E9E)) // 灰色取消按钮
                ) {
                    Text("取消")
                }
            }
        )
    }

    // Workout detail dialog
    if (showDialog && selectedWorkout != null) {
        val workout = selectedWorkout // Create a local non-nullable variable
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(workout!!.type) },
            text = {
                Column {
                    Text("Date: ${workout!!.date}")
                    Text("Time: ${workout!!.time}")
                    Text("Duration: ${workout!!.duration} min")
                    Text("Calories: ${workout!!.calories} kcal")
                    if (workout!!.notes.isNotBlank()) {
                        Text("Notes: ${workout!!.notes}")
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6))
                ) {
                    Text("Close")
                }
            }
        )
    }
}