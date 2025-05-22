package com.example.fitlife.ui.map

import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.android.gms.maps.CameraUpdateFactory
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
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.text.input.ImeAction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import androidx.compose.runtime.DisposableEffect
import android.util.Log
import coil.compose.AsyncImage
import coil.request.ImageRequest

data class FitnessPlace(
    val id: Int,
    val name: String,
    val distance: String,
    val rating: Float,
    val imageUrl: String?,
    val tags: List<String>,
    val type: PlaceType,
    val latitude: Double,
    val longitude: Double
)

enum class PlaceType {
    GYM, YOGA, SWIMMING, PARK
}

@Composable
fun MapScreen(
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToCalendar: () -> Unit,
    onNavigateToMap: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val context = LocalContext.current
    val placesSearchService = remember { PlacesSearchService(context) }
    val coroutineScope = rememberCoroutineScope()
    
    // 默认示例数据（当没有搜索结果时显示）
    val defaultPlaces = remember {
        listOf(
            FitnessPlace(
                1,
                "Super Fitness Center",
                "0.8 km",
                4.8f,
                null,
                listOf("24/7", "Trainer Available"),
                PlaceType.GYM,
                -37.8768, 145.0458
            ),
            FitnessPlace(
                2,
                "Peace Yoga Studio",
                "1.2 km",
                4.6f,
                null,
                listOf("Beginner Friendly", "Hot Yoga"),
                PlaceType.YOGA,
                -37.8780, 145.0440
            ),
            FitnessPlace(
                3,
                "Central Park Fitness Area",
                "0.5 km",
                4.5f,
                null,
                listOf("Free", "Outdoor"),
                PlaceType.PARK,
                -37.8750, 145.0460
            ),
            // 添加更多示例数据点，以便地图显示更广阔的区域
            FitnessPlace(
                4,
                "Elite Swimming Club",
                "3.2 km",
                4.7f,
                null,
                listOf("Olympic Pool", "Coaching"),
                PlaceType.SWIMMING,
                -37.8690, 145.0380
            ),
            FitnessPlace(
                5,
                "Zen Yoga Center",
                "4.5 km",
                4.4f,
                null,
                listOf("Meditation", "All Levels"),
                PlaceType.YOGA,
                -37.8830, 145.0520
            ),
            FitnessPlace(
                6,
                "Community Fitness Park",
                "2.8 km",
                4.3f,
                null,
                listOf("Free", "Group Activities"),
                PlaceType.PARK,
                -37.8710, 145.0570
            ),
            FitnessPlace(
                7,
                "PowerLift Gym",
                "5.1 km",
                4.9f,
                null,
                listOf("Weight Training", "Personal Training"),
                PlaceType.GYM,
                -37.8650, 145.0350
            )
        )
    }

    var selectedFilter by remember { mutableStateOf("All") }
    var searchText by remember { mutableStateOf("") }
    var searchLocation by remember { mutableStateOf<LatLng?>(null) }
    val filters = listOf("All", "Gym", "Yoga", "Swimming", "Park")

    // 存储搜索到的健身场所
    var searchResults by remember { mutableStateOf<List<FitnessPlace>>(emptyList()) }
    
    // 是否正在搜索
    var isSearching by remember { mutableStateOf(false) }
    
    // 错误信息
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    // 用户当前位置
    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    
    // 是否已执行过初始搜索
    var initialSearchDone by remember { mutableStateOf(false) }
    
    // Clayton 校区坐标
    val claytonCampus = remember { LatLng(-37.9105, 145.1363) }
    
    // FusedLocationProviderClient 用于获取设备位置
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    
    // 获取用户位置的函数
    fun fetchUserLocation() {
        try {
            // 检查权限
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // 获取最后已知位置
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        location?.let {
                            userLocation = LatLng(it.latitude, it.longitude)
                        } ?: run {
                            // 如果无法获取位置，使用Clayton校区
                            if (!initialSearchDone) {
                                userLocation = claytonCampus
                            }
                        }
                    }
            } else {
                // 如果没有位置权限，使用Clayton校区
                if (!initialSearchDone) {
                    userLocation = claytonCampus
                }
            }
        } catch (e: Exception) {
            Log.e("MapScreen", "Error getting location", e)
            // 发生错误时也使用Clayton校区
            if (!initialSearchDone) {
                userLocation = claytonCampus
            }
        }
    }
    
    // 执行搜索附近健身场所的函数
    val searchNearbyPlaces = { location: LatLng, typeFilter: String? ->
        coroutineScope.launch {
            isSearching = true
            errorMessage = null
            
            try {
                val type = when (typeFilter) {
                    "Gym" -> PlaceType.GYM
                    "Yoga" -> PlaceType.YOGA
                    "Swimming" -> PlaceType.SWIMMING
                    "Park" -> PlaceType.PARK
                    else -> null
                }
                
                val places = placesSearchService.searchNearbyFitnessPlaces(
                    location,
                    type = type
                )
                
                if (places.isNotEmpty()) {
                    searchResults = places
                    searchLocation = location
                    initialSearchDone = true
                } else {
                    searchResults = emptyList()
                    errorMessage = "No fitness places found in this area"
                }
            } catch (e: Exception) {
                Log.e("MapScreen", "Error searching nearby places", e)
                searchResults = emptyList()
                errorMessage = "Error searching nearby places: ${e.localizedMessage ?: "Unknown error"}"
            } finally {
                isSearching = false
            }
        }
    }
    
    // 请求位置权限并获取用户位置
    DisposableEffect(Unit) {
        fetchUserLocation()
        onDispose { }
    }
    
    // 当用户位置变化时自动搜索附近的健身场所
    LaunchedEffect(userLocation) {
        userLocation?.let {
            if (!initialSearchDone) {
                searchNearbyPlaces(it, selectedFilter.takeIf { filter -> filter != "All" })
            }
        }
    }
    
    // 监听搜索文本变化，如果为空则重置状态
    LaunchedEffect(searchText) {
        if (searchText.isBlank() && initialSearchDone) {
            // 当搜索栏清空时，回到初始搜索状态（显示当前位置或Clayton校区的结果）
            userLocation?.let {
                searchNearbyPlaces(it, selectedFilter.takeIf { filter -> filter != "All" })
            }
        }
    }
    
    // 处理搜索按钮点击
    val handleSearch = {
        if (searchText.isNotBlank()) {
            // 重置错误信息
            errorMessage = null
            // 设置搜索中状态
            isSearching = true
            
            // 启动协程执行搜索
            coroutineScope.launch {
                try {
                    // 1. 搜索地点并获取经纬度
                    val location = placesSearchService.searchPlaceByName(searchText)
                    
                    if (location != null) {
                        // 2. 搜索周围的健身场所
                        val selectedType = when (selectedFilter) {
                            "Gym" -> PlaceType.GYM
                            "Yoga" -> PlaceType.YOGA
                            "Swimming" -> PlaceType.SWIMMING
                            "Park" -> PlaceType.PARK
                            else -> null
                        }
                        
                        val places = placesSearchService.searchNearbyFitnessPlaces(
                            location,
                            type = selectedType
                        )
                        
                        // 3. 更新搜索结果
                        if (places.isNotEmpty()) {
                            searchResults = places
                            searchLocation = location
                            initialSearchDone = true
                        } else {
                            searchResults = emptyList()
                            searchLocation = location
                            errorMessage = "No fitness places found near '${searchText}'"
                        }
                    } else {
                        // 如果找不到地点，显示错误信息
                        searchResults = emptyList()
                        searchLocation = null
                        errorMessage = "Location not found: '${searchText}'"
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    // 发生错误，显示错误信息
                    searchResults = emptyList()
                    searchLocation = null
                    errorMessage = "Error during search: ${e.localizedMessage ?: "Unknown error"}"
                } finally {
                    // 结束搜索中状态
                    isSearching = false
                }
            }
        }
    }

    // 实际显示的地点列表（由于我们现在自动搜索，总是显示真实搜索结果）
    val placesToShow = searchResults
    
    // 使用derivedStateOf来确保过滤逻辑只会在依赖的状态改变时计算
    val filteredPlaces by remember(selectedFilter, placesToShow) {
        derivedStateOf {
            placesToShow.filter { place ->
                when (selectedFilter) {
                    "Gym" -> place.type == PlaceType.GYM
                    "Yoga" -> place.type == PlaceType.YOGA
                    "Swimming" -> place.type == PlaceType.SWIMMING
                    "Park" -> place.type == PlaceType.PARK
                    else -> true
                }
            }
        }
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
            // 搜索栏和标题
            SearchBarWithTitle(
                searchText = searchText,
                onSearchTextChanged = { searchText = it },
                onSearch = handleSearch,
                isSearching = isSearching
            )
            
            // 地图视图（传入搜索位置和用户当前位置）
            MapView(
                filteredPlaces = filteredPlaces,
                searchLocation = searchLocation,
                userLocation = userLocation
            )
            
            // 筛选标签
            FilterTabs(
                filters = filters,
                selectedFilter = selectedFilter,
                onFilterSelected = { 
                    selectedFilter = it
                    // 修改筛选逻辑 - 不再触发新的搜索，只对现有结果进行筛选
                    // 只有当搜索结果为空时才触发新搜索
                    if (searchResults.isEmpty() && userLocation != null) {
                        // 如果没有搜索文本，使用当前位置重新搜索
                        searchNearbyPlaces(userLocation!!, selectedFilter.takeIf { filter -> filter != "All" })
                    }
                }
            )
            
            // 场所列表
            if (isSearching) {
                // 搜索中显示加载指示器
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = Color(0xFF3B82F6))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Searching for nearby fitness places...",
                            color = Color(0xFF6B7280),
                            fontSize = 14.sp
                        )
                    }
                }
            } else if (errorMessage != null) {
                // 显示错误消息
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = Color(0xFFE5E7EB),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = errorMessage!!,
                            color = Color(0xFF6B7280),
                            textAlign = TextAlign.Center,
                            fontSize = 14.sp
                        )
                    }
                }
            } else if (filteredPlaces.isEmpty() && searchText.isNotBlank()) {
                // 无搜索结果
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = Color(0xFFE5E7EB),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No fitness places found matching your criteria",
                            color = Color(0xFF6B7280),
                            textAlign = TextAlign.Center,
                            fontSize = 14.sp
                        )
                        if (selectedFilter != "All") {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Try changing filters or searching another location",
                                color = Color(0xFF9CA3AF),
                                textAlign = TextAlign.Center,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            } else {
                // 显示地点列表
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(filteredPlaces) { place ->
                    PlaceItem(place = place)
                    }
                }
            }
        }
        
        // 底部导航
        BottomNavBar(
            currentRoute = "map",
            onNavigateToHome = onNavigateToHome,
            onNavigateToCalendar = onNavigateToCalendar,
            onNavigateToMap = onNavigateToMap,
            onNavigateToProfile = onNavigateToProfile,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun SearchBarWithTitle(
    searchText: String,
    onSearchTextChanged: (String) -> Unit,
    onSearch: () -> Unit,
    isSearching: Boolean = false
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        // 标题和菜单按钮行
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 左侧留白，保持对称
            Spacer(modifier = Modifier.width(32.dp))

            // 标题
            Text(
                text = "Nearby Fitness Places",
                fontSize = 18.sp, 
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .weight(1f), // 占据中间空间
                textAlign = TextAlign.Center, // 文本居中
                color = Color(0xFF1F2937)
            )

            // 菜单图标按钮
            IconButton(
                onClick = { /* TODO: Add menu action */ },
                modifier = Modifier.size(32.dp) // 保持尺寸对称
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu",
                    tint = Color(0xFF6B7280)
                )
            }
        }
        
        // 在标题行和搜索栏之间添加间距
        Spacer(modifier = Modifier.height(16.dp))
        
        // 搜索栏
        OutlinedTextField(
            value = searchText,
            onValueChange = { onSearchTextChanged(it) },
            placeholder = { 
                Text(
                    text = "Enter location to search for fitness places",
                    fontSize = 14.sp,
                    color = Color(0xFF9CA3AF)
                ) 
            },
            leadingIcon = { 
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Color(0xFF6B7280),
                    modifier = Modifier.size(20.dp)
                )
            },
            trailingIcon = {
                if (isSearching) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color(0xFF3B82F6),
                        strokeWidth = 2.dp
                    )
                } else if (searchText.isNotBlank()) {
                    IconButton(
                        onClick = onSearch,
                        modifier = Modifier.size(20.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Execute Search",
                            tint = Color(0xFF3B82F6)
                        )
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF3B82F6),
                unfocusedBorderColor = Color(0xFFE5E7EB),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                cursorColor = Color(0xFF3B82F6),
                focusedTextColor = Color(0xFF1F2937),
                unfocusedTextColor = Color(0xFF1F2937)
            ),
            textStyle = androidx.compose.ui.text.TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = { onSearch() }
            )
        )
    }
}

@Composable
fun MapView(
    filteredPlaces: List<FitnessPlace>,
    searchLocation: LatLng? = null,
    userLocation: LatLng? = null
) {
    val context = LocalContext.current
    // Default camera position (Monash Clayton campus)
    val defaultCameraPosition = LatLng(-37.9105, 145.1363) // Changed from Melbourne to Monash Clayton
    // 如果有用户位置，优先使用；否则如果有搜索位置，使用搜索位置；最后使用默认位置
    val initialPosition = userLocation ?: searchLocation ?: defaultCameraPosition
    
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialPosition, 13f) // 从9f增加到13f以适当放大视图
    }

    LaunchedEffect(filteredPlaces, searchLocation, userLocation) {
        if (searchLocation != null) {
            // 如果有明确的搜索位置，优先显示该位置
            if (filteredPlaces.isEmpty()) {
                // 如果没有过滤后的地点，只显示搜索位置
                cameraPositionState.animate(
                    CameraUpdateFactory.newLatLngZoom(searchLocation, 14f) // 适当放大视图 (从12f到14f)
                )
            } else if (filteredPlaces.size == 1) {
                // 如果只有一个地点，显示该地点
                val place = filteredPlaces.first()
                cameraPositionState.animate(
                    CameraUpdateFactory.newLatLngZoom(LatLng(place.latitude, place.longitude), 15f) // 更进一步放大 (从14f到15f)
                )
            } else {
                // 有多个地点，显示所有地点的边界
                val boundsBuilder = LatLngBounds.builder()
                // 添加搜索位置
                boundsBuilder.include(searchLocation)
                // 添加所有过滤后的地点
                for (place in filteredPlaces) {
                    boundsBuilder.include(LatLng(place.latitude, place.longitude))
                }
                val padding = 250 // 从200增加到250，提供更多边距
                cameraPositionState.animate(
                    CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), padding),
                    durationMs = 1000
                )
            }
        } else if (userLocation != null && filteredPlaces.isEmpty()) {
            // 如果有用户位置但没有搜索结果，显示用户位置
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(userLocation, 14f) // 适当放大用户位置视图
            )
        } else if (filteredPlaces.isNotEmpty()) {
            // 没有明确的搜索位置，使用以前的逻辑
            if (filteredPlaces.size == 1) {
                val place = filteredPlaces.first()
                cameraPositionState.animate(
                    CameraUpdateFactory.newLatLngZoom(LatLng(place.latitude, place.longitude), 15f)
                )
            } else {
                val boundsBuilder = LatLngBounds.builder()
                for (place in filteredPlaces) {
                    boundsBuilder.include(LatLng(place.latitude, place.longitude))
                }
                val padding = 250
                cameraPositionState.animate(
                    CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), padding),
                    durationMs = 1000
                )
            }
        } else {
            // 没有任何特定位置或搜索结果，显示用户位置或默认位置
            val position = userLocation ?: defaultCameraPosition
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(position, 13f) // 从9f增加到13f
            )
        }
    }

    GoogleMap(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(16.dp)),
        cameraPositionState = cameraPositionState
    ) {
        // 显示所有过滤后的地点标记
        filteredPlaces.forEach { place ->
            // 根据地点类型选择不同的标记颜色
            val markerColor = when (place.type) {
                PlaceType.GYM -> BitmapDescriptorFactory.HUE_RED
                PlaceType.YOGA -> BitmapDescriptorFactory.HUE_VIOLET
                PlaceType.SWIMMING -> BitmapDescriptorFactory.HUE_AZURE
                PlaceType.PARK -> BitmapDescriptorFactory.HUE_GREEN
            }
            
            Marker(
                state = MarkerState(position = LatLng(place.latitude, place.longitude)),
                title = place.name,
                snippet = "Rating: ${place.rating} - ${place.type}",
                icon = BitmapDescriptorFactory.defaultMarker(markerColor)
            )
        }
        
        // 如果有搜索位置，显示一个蓝色标记表示搜索位置
        searchLocation?.let {
            Marker(
                state = MarkerState(position = it),
                title = "Search Location",
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
            )
        }
        
        // 如果有用户位置，显示一个青色标记表示用户当前位置
        userLocation?.let {
            Marker(
                state = MarkerState(position = it),
                title = "My Location",
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)
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
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(place.imageUrl ?: getDefaultImageResourceByType(place.type))
                    .crossfade(true)
                    .build(),
                contentDescription = place.name,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
                error = painterResource(id = getDefaultImageResourceByType(place.type)),
                placeholder = painterResource(id = getDefaultImageResourceByType(place.type))
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Place details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Name and rating
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // 名称使用weight并设置最多占用空间，允许末尾省略
                    Text(
                        text = place.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF1F2937),
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )

                    // 评分部分不再使用weight，而是固定宽度
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    // 评分组件设为固定布局
                    Box(
                        modifier = Modifier.widthIn(min = 45.dp),
                        contentAlignment = Alignment.CenterEnd
                    ) {
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
                }
                
                // Distance
                Text(
                    text = "Distance: ${place.distance}",
                    fontSize = 14.sp,
                    color = Color(0xFF6B7280),
                    modifier = Modifier.padding(vertical = 1.dp)
                )
                
                // Tags 包括场所类型和其他标签
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    // 显示所有标签（最多2个）
                    place.tags.take(2).forEach { tag ->
                        // 根据标签内容设置不同样式
                        val (bgColor, textColor) = when (tag) {
                            "24/7" -> Pair(Color(0xFFE0F2FE), Color(0xFF0369A1))
                            "Gym" -> Pair(Color(0xFFFFE4E6), Color(0xFFBE123C))
                            "Yoga" -> Pair(Color(0xFFF3E8FF), Color(0xFF7E22CE))
                            "Swimming" -> Pair(Color(0xFFE0F2FE), Color(0xFF0369A1))
                            "Park" -> Pair(Color(0xFFDCFCE7), Color(0xFF047857))
                            else -> Pair(Color(0xFFF3F4F6), Color(0xFF4B5563))
                        }
                        
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(bgColor)
                                .padding(horizontal = 8.dp, vertical = 1.dp)
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

/**
 * 根据地点类型获取默认图片资源
 */
private fun getDefaultImageResourceByType(type: PlaceType): Int {
    return when (type) {
        PlaceType.GYM -> R.drawable.gym_image
        PlaceType.YOGA -> R.drawable.yoga_image
        PlaceType.SWIMMING -> R.drawable.gym_image // 假设我们没有游泳池图片，使用健身房图片
        PlaceType.PARK -> R.drawable.outdoor_gym
    }
}