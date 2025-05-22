package com.example.fitlife.ui.map

import android.content.Context
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.PlaceTypes
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FetchPhotoRequest
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.example.fitlife.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.random.Random
import java.io.File
import java.io.FileOutputStream

class PlacesSearchService(private val context: Context) {
    private val placesClient: PlacesClient
    private val TAG = "PlacesSearchService"

    init {
        // 确保Places API已初始化
        if (!Places.isInitialized()) {
            // Places.initialize()需要在AndroidManifest.xml中添加的相同API密钥
            Places.initialize(context.applicationContext, context.getString(context.resources.getIdentifier("google_maps_api_key", "string", context.packageName)))
        }
        placesClient = Places.createClient(context)
    }

    /**
     * 通过地点名称搜索并获取其经纬度
     */
    suspend fun searchPlaceByName(query: String): LatLng? = withContext(Dispatchers.IO) {
        try {
            // 创建地点自动完成请求
            val request = FindAutocompletePredictionsRequest.builder()
                .setQuery(query)
                .build()

            // 执行搜索请求
            val response = placesClient.findAutocompletePredictions(request).await()
            val predictions = response.autocompletePredictions

            if (predictions.isNotEmpty()) {
                // 获取第一个预测结果的placeId
                val placeId = predictions[0].placeId

                // 通过placeId获取详细信息
                val placeFields = listOf(Place.Field.LAT_LNG)
                val fetchRequest = FetchPlaceRequest.newInstance(placeId, placeFields)
                val fetchResponse = placesClient.fetchPlace(fetchRequest).await()
                
                // 返回经纬度
                return@withContext fetchResponse.place.latLng
            }
            null
        } catch (e: Exception) {
            Log.e(TAG, "Error searching place by name: $query", e)
            null
        }
    }

    /**
     * 搜索指定位置周围的健身场所
     * 使用自动完成接口搜索周边
     */
    suspend fun searchNearbyFitnessPlaces(
        location: LatLng,
        radius: Int = 5000,
        type: PlaceType? = null
    ): List<FitnessPlace> = withContext(Dispatchers.IO) {
        val results = mutableListOf<FitnessPlace>()
        
        try {
            // 创建位置边界 - 基于给定位置的半径创建矩形
            val latLngBounds = createBoundsFromLocation(location, radius)
            
            // 根据健身场所类型构建不同的搜索查询
            val placeQueries = when(type) {
                PlaceType.GYM -> listOf("gym", "fitness center")
                PlaceType.YOGA -> listOf("yoga studio", "yoga")
                PlaceType.SWIMMING -> listOf("swimming pool")
                PlaceType.PARK -> listOf("park fitness", "playground", "outdoor fitness")
                else -> listOf("gym", "fitness", "yoga", "swimming pool", "park fitness", "playground")
            }
            
            // 对每个查询分别进行搜索
            for (query in placeQueries) {
                try {
                    // 使用自动完成API搜索附近的地点
                    val request = FindAutocompletePredictionsRequest.builder()
                        .setLocationBias(RectangularBounds.newInstance(
                            latLngBounds.southwest,
                            latLngBounds.northeast
                        ))
                        .setQuery(query)
                        .setTypeFilter(getTypeFilterForQuery(query))
                        .build()
                    
                    val response = placesClient.findAutocompletePredictions(request).await()
                    
                    // 从自动完成结果中获取Place详情
                    for (prediction in response.autocompletePredictions) {
                        try {
                            // 获取Place的详细信息，包括照片元数据
                            val placeId = prediction.placeId
                            val placeFields = listOf(
                                Place.Field.ID,
                                Place.Field.NAME,
                                Place.Field.TYPES,
                                Place.Field.LAT_LNG,
                                Place.Field.RATING,
                                Place.Field.USER_RATINGS_TOTAL,
                                Place.Field.PHOTO_METADATAS, // 添加照片元数据
                                Place.Field.OPENING_HOURS, // 添加营业时间信息
                                Place.Field.BUSINESS_STATUS // 添加营业状态信息
                            )
                            
                            val fetchPlaceRequest = FetchPlaceRequest.newInstance(placeId, placeFields)
                            val fetchPlaceResponse = placesClient.fetchPlace(fetchPlaceRequest).await()
                            val place = fetchPlaceResponse.place
                            
                            // 只处理有位置信息的地点
                            if (place.latLng != null) {
                                // 智能判断场所类型
                                // 1. 如果是按类型搜索，优先使用搜索类型
                                // 2. 如果场所名称包含类型关键词，使用该类型
                                // 3. 最后使用API返回的类型
                                val placeName = place.name?.lowercase() ?: ""
                                val currentType = when {
                                    // 如果指定了搜索类型，优先使用该类型
                                    type != null -> type
                                    // 如果场所名称包含类型关键词，使用该类型
                                    placeName.contains("yoga") -> PlaceType.YOGA
                                    placeName.contains("gym") || placeName.contains("fitness") -> PlaceType.GYM
                                    placeName.contains("swim") || placeName.contains("pool") -> PlaceType.SWIMMING
                                    placeName.contains("park") -> PlaceType.PARK
                                    // 根据查询关键词和Google返回的类型判断
                                    else -> getPlaceTypeFromGoogleType(place.types, query)
                                }
                                
                                val distance = calculateDistance(
                                    location.latitude, location.longitude,
                                    place.latLng.latitude, place.latLng.longitude
                                )
                                
                                // 只加入距离在指定范围内的地点 (m -> km)
                                if (distance <= radius / 1000.0) {
                                    // 尝试获取地点照片 URL
                                    var photoUrl: String? = null
                                    place.photoMetadatas?.let { photoMetadatas ->
                                        if (photoMetadatas.isNotEmpty()) {
                                            // 获取第一张照片
                                            try {
                                                val photoMetadata = photoMetadatas[0]
                                                val photoRequest = FetchPhotoRequest.builder(photoMetadata)
                                                    .setMaxWidth(500) // 设置最大宽度
                                                    .setMaxHeight(500) // 设置最大高度
                                                    .build()
                                                
                                                // 获取照片 Bitmap 并将其保存为临时文件
                                                val photoResponse = placesClient.fetchPhoto(photoRequest).await()
                                                val bitmap = photoResponse.bitmap
                                                
                                                // 创建临时文件存储照片
                                                val photoFile = File(context.cacheDir, "place_${place.id}.jpg")
                                                FileOutputStream(photoFile).use { out ->
                                                    bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 90, out)
                                                }
                                                
                                                // 使用文件 URI 作为照片 URL
                                                photoUrl = photoFile.toURI().toString()
                                            } catch (e: Exception) {
                                                Log.e(TAG, "Error fetching photo for place: ${place.name}", e)
                                                // 出错时不设置 photoUrl，将使用默认图片
                                            }
                                        }
                                    }
                                    
                                    // 检查场所是否24小时营业
                                    val is24Hours = isOpen24Hours(place)
                                    
                                    // 准备标签列表
                                    val tags = if (is24Hours) {
                                        // 添加24/7标签和场所类型标签
                                        val typeTag = when(currentType) {
                                            PlaceType.GYM -> "Gym"
                                            PlaceType.YOGA -> "Yoga"
                                            PlaceType.SWIMMING -> "Swimming"
                                            PlaceType.PARK -> "Park"
                                        }
                                        listOf("24/7", typeTag)
                                    } else {
                                        getTagsFromPlaceTypes(place.types, query, place.openingHours)
                                    }
                                    
                                    val fitnessPlace = FitnessPlace(
                                        id = place.id?.hashCode() ?: Random.nextInt(),
                                        name = place.name ?: "Unknown Place",
                                        distance = String.format("%.1f km", distance),
                                        rating = place.rating?.toFloat() ?: 0.0f,
                                        imageUrl = photoUrl, // 使用获取的照片 URL
                                        tags = tags,
                                        type = currentType,
                                        latitude = place.latLng.latitude,
                                        longitude = place.latLng.longitude
                                    )
                                    
                                    // 记录调试信息，显示是否有24小时标签
                                    Log.d(TAG, "场所信息: ${place.name}, 标签: ${fitnessPlace.tags}, 是否24小时: $is24Hours")
                                    
                                    // 避免重复添加相同ID的地点
                                    if (results.none { it.id == fitnessPlace.id }) {
                                        results.add(fitnessPlace)
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Error fetching place details: ${prediction.placeId}", e)
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error searching for query: $query", e)
                }
            }
            
            // 如果没有找到结果，可能搜索范围太小或区域内没有健身场所
            // 返回可用的结果
            results
        } catch (e: Exception) {
            Log.e(TAG, "Error in searchNearbyFitnessPlaces", e)
            emptyList()
        }
    }
    
    /**
     * 根据查询类型获取合适的TypeFilter
     */
    private fun getTypeFilterForQuery(query: String): TypeFilter {
        return when {
            query.contains("gym") || query.contains("fitness") -> TypeFilter.ESTABLISHMENT
            query.contains("yoga") -> TypeFilter.ESTABLISHMENT
            query.contains("swimming") -> TypeFilter.ESTABLISHMENT
            query.contains("park") || query.contains("playground") || query.contains("outdoor fitness") -> TypeFilter.GEOCODE  // GEOCODE更适合地理位置类型
            else -> TypeFilter.ESTABLISHMENT
        }
    }

    /**
     * 基于位置和半径创建矩形边界
     */
    private fun createBoundsFromLocation(center: LatLng, radiusInMeters: Int): LatLngBounds {
        // 将半径从米转换为度 (粗略计算: 1度约等于111km)
        val radiusInDegrees = radiusInMeters / 111000.0
        
        val northEast = LatLng(
            center.latitude + radiusInDegrees,
            center.longitude + radiusInDegrees
        )
        
        val southWest = LatLng(
            center.latitude - radiusInDegrees,
            center.longitude - radiusInDegrees
        )
        
        return LatLngBounds(southWest, northEast)
    }

    /**
     * 基于Google Places API返回的类型确定我们的PlaceType
     */
    private fun getPlaceTypeFromGoogleType(placeTypes: List<Place.Type>?, query: String): PlaceType {
        // 首先检查场所名称是否包含关键词，这往往是最可靠的指标
        val placeName = query.lowercase()
        if (placeName.contains("yoga")) {
            return PlaceType.YOGA
        }
        if (placeName.contains("playground") || placeName.contains("outdoor fitness")) {
            return PlaceType.PARK
        }
        
        if (placeTypes == null || placeTypes.isEmpty()) {
            // 根据查询推断类型
            return when {
                query.contains("gym") || query.contains("fitness") -> PlaceType.GYM
                query.contains("yoga") -> PlaceType.YOGA
                query.contains("swimming") -> PlaceType.SWIMMING
                query.contains("park") || query.contains("playground") -> PlaceType.PARK
                else -> PlaceType.GYM
            }
        }
        
        // 将Place.Type转换为String以便于比较
        val typeStrings = placeTypes.map { it.name.lowercase() }
        
        return when {
            typeStrings.any { it.contains("yoga") } || (typeStrings.contains("establishment") && query.contains("yoga")) -> PlaceType.YOGA
            typeStrings.any { it.contains("gym") || it.contains("fitness") } || (typeStrings.contains("establishment") && (query.contains("gym") || query.contains("fitness"))) -> PlaceType.GYM
            typeStrings.any { it.contains("swimming") || it.contains("pool") } || (typeStrings.contains("establishment") && query.contains("swimming")) -> PlaceType.SWIMMING
            typeStrings.any { it.contains("park") || it.contains("playground") || it.contains("amusement_park") } || (typeStrings.contains("establishment") && (query.contains("park") || query.contains("playground"))) -> PlaceType.PARK
            else -> PlaceType.GYM // 默认
        }
    }
    
    /**
     * 直接检查场所是否24小时营业
     * 专门增加一个方法，模拟系统最常见的判断方式
     */
    fun isOpen24Hours(place: Place): Boolean {
        // 常见的24小时健身房品牌和关键词
        val brands24Hours = listOf(
            "anytime fitness", "24 hour fitness", "jetts fitness", "snap fitness",
            "plus fitness", "fitness 24", "planet fitness", "24/7 fitness", "goodlife"
        )
        
        // 其他可能表示24小时的关键词
        val keywords24Hours = listOf(
            "24 hour", "24/7", "open 24", "always open", "all day", "all night"
        )
        
        // 获取场所名称，转为小写以便忽略大小写比较
        val name = place.name?.lowercase() ?: ""
        
        // 1. 检查是否是已知的24小时品牌
        val isKnown24HourBrand = brands24Hours.any { name.contains(it) }
        
        // 2. 检查名称是否包含24小时关键词
        val nameContains24HourKeyword = keywords24Hours.any { name.contains(it) }
        
        // 3. 检查营业时间是否表明24小时营业
        val is24HoursByHours = place.openingHours?.let { hours ->
            // 检查periods是否有任何一个表示全天营业
            hours.periods?.any { period -> 
                period.open?.day == period.close?.day && 
                period.open?.time?.hours == 0 && period.open?.time?.minutes == 0 && 
                period.close?.time?.hours == 23 && period.close?.time?.minutes == 59
            } ?: false
        } ?: false
        
        // 4. 检查营业时间文本是否包含"24小时"相关词语
        val is24HoursByText = place.openingHours?.let { hours ->
            hours.weekdayText?.any { 
                it.contains("Open 24 hours", ignoreCase = true) ||
                it.contains("24 hours", ignoreCase = true) ||
                it.contains("24/7", ignoreCase = true) ||
                it.contains("Always open", ignoreCase = true)
            } ?: false
        } ?: false
        
        // 记录调试信息
        Log.d(TAG, "24小时检测: ${place.name}")
        Log.d(TAG, "- 已知24小时品牌=$isKnown24HourBrand")
        Log.d(TAG, "- 名称包含24小时关键词=$nameContains24HourKeyword")
        Log.d(TAG, "- 营业时间数据表明24小时=$is24HoursByHours")
        Log.d(TAG, "- 营业时间文本表明24小时=$is24HoursByText")
        Log.d(TAG, "- 营业时间文本: ${place.openingHours?.weekdayText}")
        
        // 如果是已知的24小时品牌、或营业时间明确表示24小时，或名称包含关键词则返回true
        return isKnown24HourBrand || is24HoursByHours || is24HoursByText || nameContains24HourKeyword
    }

    /**
     * 从地点类型生成标签
     */
    private fun getTagsFromPlaceTypes(placeTypes: List<Place.Type>?, query: String, openingHours: com.google.android.libraries.places.api.model.OpeningHours? = null): List<String> {
        val tags = mutableListOf<String>()
        
        // 检查是否24小时营业
        // 方法1：检查时间段是否为0:00到23:59
        val is24HoursByPeriod = openingHours?.periods?.any { period -> 
            period.open?.day == period.close?.day && 
            period.open?.time?.hours == 0 && period.open?.time?.minutes == 0 && 
            period.close?.time?.hours == 23 && period.close?.time?.minutes == 59
        } ?: false
        
        // 方法2：检查营业时间文本描述是否包含"Open 24 hours"
        val is24HoursByText = openingHours?.weekdayText?.any { 
            it.contains("Open 24 hours", ignoreCase = true) ||
            it.contains("24 hours", ignoreCase = true) ||
            it.contains("24/7", ignoreCase = true) ||
            it.contains("Always open", ignoreCase = true)
        } ?: false
        
        // 方法3：检查名称中是否包含"24小时"相关词语
        val nameContains24h = query.lowercase().contains("24") || 
                             query.lowercase().contains("hour") || 
                             query.lowercase().contains("anytime") ||
                             query.lowercase().contains("fitness")
        
        // 如果任一方法判断为24小时营业，则认为是24小时营业
        val is24HoursOpen = is24HoursByPeriod || is24HoursByText || (nameContains24h && query.lowercase().contains("fit"))
        
        // 添加日志输出，辅助调试
        Log.d("PlacesSearchService", "营业时间检测: query=$query")
        Log.d("PlacesSearchService", "营业时间文本: ${openingHours?.weekdayText}")
        Log.d("PlacesSearchService", "is24HoursByPeriod=$is24HoursByPeriod, is24HoursByText=$is24HoursByText, nameContains24h=$nameContains24h")
        
        if (is24HoursOpen) {
            tags.add("24/7")
            Log.d("PlacesSearchService", "检测到24小时营业: $query")
            return tags
        }
        
        // 不是24小时营业，只选择一个最合适的标签
        when {
            query.contains("yoga") -> tags.add("Yoga")
            query.contains("gym") || query.contains("fitness") -> tags.add("Gym")
            query.contains("swimming") -> tags.add("Swimming")
            query.contains("park") || query.contains("playground") || query.contains("outdoor fitness") -> tags.add("Outdoor")
        }
        
        // 如果没有从查询中获取到标签，尝试从地点类型获取
        if (tags.isEmpty() && placeTypes != null && placeTypes.isNotEmpty()) {
            // 将Place.Type转换为String
            val typeStrings = placeTypes.map { it.name.lowercase() }
            
            when {
                typeStrings.any { it.contains("yoga") } -> tags.add("Yoga")
                typeStrings.any { it.contains("gym") || it.contains("fitness") } -> tags.add("Gym")
                typeStrings.any { it.contains("swimming") || it.contains("pool") } -> tags.add("Swimming")
                typeStrings.any { it.contains("park") || it.contains("playground") } -> tags.add("Outdoor")
                else -> tags.add("Fitness") // 默认标签
            }
        }
        
        // 确保至少有一个标签
        if (tags.isEmpty()) {
            tags.add("Fitness")
        }
        
        return tags
    }
    
    /**
     * 根据地点类型获取合适的图片资源
     */
    private fun getImageResourceByType(type: PlaceType): Int {
        return when (type) {
            PlaceType.GYM -> R.drawable.gym_image
            PlaceType.YOGA -> R.drawable.yoga_image
            PlaceType.SWIMMING -> R.drawable.gym_image // 假设我们没有游泳池图片，使用健身房图片
            PlaceType.PARK -> R.drawable.outdoor_gym
        }
    }
    
    /**
     * 使用Haversine公式计算两点之间的距离（公里）
     */
    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0 // 地球半径，单位公里
        
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        
        return r * c
    }
} 