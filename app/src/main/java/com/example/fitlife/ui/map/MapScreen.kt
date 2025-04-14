package com.example.fitlife.ui.map

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitlife.R
import com.example.fitlife.ui.components.BottomNavBar
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding

data class FitnessPlace(
    val id: Int,
    val name: String,
    val distance: String,
    val rating: Float,
    val image: Int,
    val tags: List<String>,
    val type: PlaceType
)

enum class PlaceType {
    GYM, YOGA, SWIMMING, PARK
}

@Composable
fun MapScreen(
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToCalendar: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val places = remember {
        listOf(
            FitnessPlace(
                1,
                "Super Fitness Center",
                "0.8 km",
                4.8f,
                R.drawable.gym_image,
                listOf("24/7", "Trainer Available"),
                PlaceType.GYM
            ),
            FitnessPlace(
                2,
                "Peace Yoga Studio",
                "1.2 km",
                4.6f,
                R.drawable.yoga_image,
                listOf("Beginner Friendly", "Hot Yoga"),
                PlaceType.YOGA
            ),
            FitnessPlace(
                3,
                "Central Park Fitness Area",
                "0.5 km",
                4.5f,
                R.drawable.outdoor_gym,
                listOf("Free", "Outdoor"),
                PlaceType.PARK
            )
        )
    }

    var selectedFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "Gym", "Yoga", "Swimming", "Park")

    val filteredPlaces = when (selectedFilter) {
        "Gym" -> places.filter { it.type == PlaceType.GYM }
        "Yoga" -> places.filter { it.type == PlaceType.YOGA }
        "Swimming" -> places.filter { it.type == PlaceType.SWIMMING }
        "Park" -> places.filter { it.type == PlaceType.PARK }
        else -> places
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9FAFB))
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 顶部栏
            TopAppBar(
                onNavigateBack = onNavigateBack
            )
            
            // 地图视图
            MapView()
            
            // 筛选标签
            FilterTabs(
                filters = filters,
                selectedFilter = selectedFilter,
                onFilterSelected = { selectedFilter = it }
            )
            
            // 场所列表
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                items(filteredPlaces) { place ->
                    PlaceItem(place = place)
                }
            }
        }
        
        // 底部导航
        BottomNavBar(
            currentRoute = "map",
            onNavigateToHome = onNavigateToHome,
            onNavigateToCalendar = onNavigateToCalendar,
            onNavigateToMap = {},
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
            text = "Nearby Fitness Places",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            color = Color(0xFF1F2937)
        )
        
        // 搜索按钮
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(Color(0xFFF3F4F6))
                .clickable { /* Search function */ },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                modifier = Modifier.size(20.dp),
                tint = Color(0xFF6B7280)
            )
        }
    }
}

@Composable
fun MapView() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.LightGray)
    ) {
        // 地图背景
        Image(
            painter = painterResource(id = R.drawable.map_placeholder),
            contentDescription = "Map",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        
        // 添加半透明蒙版
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .background(Color.Black.copy(alpha = 0.1f))
//        )

        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(Color(0xFF3B82F6))
                .align(Alignment.Center)
                .border(2.dp, Color.White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_location),
                contentDescription = "Current Location",
                tint = Color.White,
                modifier = Modifier.size(12.dp)
            )
        }
        
        // 地图标记 - 绿色
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(Color(0xFF10B981))
                .align(Alignment.TopEnd)
                .border(2.dp, Color.White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_location),
                contentDescription = "Location",
                tint = Color.White,
                modifier = Modifier.size(12.dp)
            )
        }
        
        // 地图标记 - 红色
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(Color(0xFFEF4444))
                .align(Alignment.BottomCenter)
                .border(2.dp, Color.White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_location),
                contentDescription = "Location",
                tint = Color.White,
                modifier = Modifier.size(12.dp)
            )
        }
    }
}

@Composable
fun FilterTabs(
    filters: List<String>,
    selectedFilter: String,
    onFilterSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        filters.forEach { filter ->
            FilterTab(
                text = filter,
                isSelected = filter == selectedFilter,
                onClick = { onFilterSelected(filter) }
            )
        }
    }
}

@Composable
fun FilterTab(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (isSelected) Color(0xFF3B82F6) 
                else Color.White
            )
            .border(
                width = 1.dp,
                color = if (isSelected) Color(0xFF3B82F6) else Color(0xFFE5E7EB),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isSelected) Color.White else Color(0xFF6B7280),
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
        )
    }
}

@Composable
fun PlaceItem(place: FitnessPlace) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Place image
            Image(
                painter = painterResource(id = place.image),
                contentDescription = place.name,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Place details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Name and rating
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = place.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF1F2937)
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_star),
                            contentDescription = "Rating",
                            tint = Color(0xFFFACC15),
                            modifier = Modifier.size(16.dp)
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(
                            text = place.rating.toString(),
                            fontSize = 14.sp,
                            color = Color(0xFF6B7280)
                        )
                    }
                }
                
                // Distance
                Text(
                    text = "Distance: ${place.distance}",
                    fontSize = 14.sp,
                    color = Color(0xFF6B7280),
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                
                // Tags
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    place.tags.forEach { tag ->
                        val (bgColor, textColor) = when (tag) {
                            "24/7" -> Pair(Color(0xFFE0F2FE), Color(0xFF0369A1))
                            "Trainer Available" -> Pair(Color(0xFFDCFCE7), Color(0xFF047857))
                            "Beginner Friendly" -> Pair(Color(0xFFF3E8FF), Color(0xFF7E22CE))
                            "Hot Yoga" -> Pair(Color(0xFFFFE4E6), Color(0xFFBE123C))
                            "Free" -> Pair(Color(0xFFDCFCE7), Color(0xFF047857))
                            "Outdoor" -> Pair(Color(0xFFE0F2FE), Color(0xFF0369A1))
                            else -> Pair(Color(0xFFF3F4F6), Color(0xFF4B5563))
                        }
                        
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(bgColor)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = tag,
                                fontSize = 12.sp,
                                color = textColor
                            )
                        }
                    }
                }
            }
        }
    }
}