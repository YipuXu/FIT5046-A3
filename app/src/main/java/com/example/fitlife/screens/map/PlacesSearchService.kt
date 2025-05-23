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
        // Make sure that the Places API has been initialized
        if (!Places.isInitialized()) {
            Places.initialize(context.applicationContext, context.getString(context.resources.getIdentifier("google_maps_api_key", "string", context.packageName)))
        }
        placesClient = Places.createClient(context)
    }

    /**
     * Search for and obtain the longitude and latitude of the location by its name
     */
    suspend fun searchPlaceByName(query: String): LatLng? = withContext(Dispatchers.IO) {
        try {
            // The creation location automatically completes the request
            val request = FindAutocompletePredictionsRequest.builder()
                .setQuery(query)
                .build()

            // Execute the search request
            val response = placesClient.findAutocompletePredictions(request).await()
            val predictions = response.autocompletePredictions

            if (predictions.isNotEmpty()) {
                val placeId = predictions[0].placeId

                // Obtain detailed information through placeId
                val placeFields = listOf(Place.Field.LAT_LNG)
                val fetchRequest = FetchPlaceRequest.newInstance(placeId, placeFields)
                val fetchResponse = placesClient.fetchPlace(fetchRequest).await()
                
                // Return to longitude and latitude
                return@withContext fetchResponse.place.latLng
            }
            null
        } catch (e: Exception) {
            Log.e(TAG, "Error searching place by name: $query", e)
            null
        }
    }

    /**
     * Search for fitness venues around the specified location
     * Use the auto-complete interface to search for peripherals
     */
    suspend fun searchNearbyFitnessPlaces(
        location: LatLng,
        radius: Int = 5000,
        type: PlaceType? = null
    ): List<FitnessPlace> = withContext(Dispatchers.IO) {
        val results = mutableListOf<FitnessPlace>()
        
        try {
            // Create position boundaries - Create a rectangle based on the radius of the given position
            val latLngBounds = createBoundsFromLocation(location, radius)
            
            // Build different search queries based on the types of fitness venues
            val placeQueries = when(type) {
                PlaceType.GYM -> listOf("gym", "fitness center")
                PlaceType.YOGA -> listOf("yoga studio", "yoga")
                PlaceType.SWIMMING -> listOf("swimming pool")
                PlaceType.PARK -> listOf("park fitness", "playground", "outdoor fitness")
                else -> listOf("gym", "fitness", "yoga", "swimming pool", "park fitness", "playground")
            }
            
            // Search each query separately
            for (query in placeQueries) {
                try {
                    val request = FindAutocompletePredictionsRequest.builder()
                        .setLocationBias(RectangularBounds.newInstance(
                            latLngBounds.southwest,
                            latLngBounds.northeast
                        ))
                        .setQuery(query)
                        .setTypeFilter(getTypeFilterForQuery(query))
                        .build()
                    
                    val response = placesClient.findAutocompletePredictions(request).await()
                    
                    // Obtain the Place details from the auto-completion results
                    for (prediction in response.autocompletePredictions) {
                        try {
                            // Obtain detailed information about Place, including photo metadata
                            val placeId = prediction.placeId
                            val placeFields = listOf(
                                Place.Field.ID,
                                Place.Field.NAME,
                                Place.Field.TYPES,
                                Place.Field.LAT_LNG,
                                Place.Field.RATING,
                                Place.Field.USER_RATINGS_TOTAL,
                                Place.Field.PHOTO_METADATAS, 
                                Place.Field.OPENING_HOURS,
                                Place.Field.BUSINESS_STATUS
                            )
                            
                            val fetchPlaceRequest = FetchPlaceRequest.newInstance(placeId, placeFields)
                            val fetchPlaceResponse = placesClient.fetchPlace(fetchPlaceRequest).await()
                            val place = fetchPlaceResponse.place
                            
                            // Only handle locations with location information
                            if (place.latLng != null) {
                                val placeName = place.name?.lowercase() ?: ""
                                val currentType = when {
                                    type != null -> type
                                    placeName.contains("yoga") -> PlaceType.YOGA
                                    placeName.contains("gym") || placeName.contains("fitness") -> PlaceType.GYM
                                    placeName.contains("swim") || placeName.contains("pool") -> PlaceType.SWIMMING
                                    placeName.contains("park") -> PlaceType.PARK
                                    else -> getPlaceTypeFromGoogleType(place.types, query)
                                }
                                
                                val distance = calculateDistance(
                                    location.latitude, location.longitude,
                                    place.latLng.latitude, place.latLng.longitude
                                )
                                
                                // Only add locations within the specified range (m -> km).
                                if (distance <= radius / 1000.0) {
                                    var photoUrl: String? = null
                                    place.photoMetadatas?.let { photoMetadatas ->
                                        if (photoMetadatas.isNotEmpty()) {
                                            // Get the first photo
                                            try {
                                                val photoMetadata = photoMetadatas[0]
                                                val photoRequest = FetchPhotoRequest.builder(photoMetadata)
                                                    .setMaxWidth(500)
                                                    .setMaxHeight(500)
                                                    .build()
                                                
                                                // Obtain the photo Bitmap and save it as a temporary file
                                                val photoResponse = placesClient.fetchPhoto(photoRequest).await()
                                                val bitmap = photoResponse.bitmap
                                                
                                                // Create temporary files to store photos
                                                val photoFile = File(context.cacheDir, "place_${place.id}.jpg")
                                                FileOutputStream(photoFile).use { out ->
                                                    bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 90, out)
                                                }
                                                
                                                // Use the file URI as the photo URL
                                                photoUrl = photoFile.toURI().toString()
                                            } catch (e: Exception) {
                                                Log.e(TAG, "Error fetching photo for place: ${place.name}", e)
                                            }
                                        }
                                    }
                                    
                                    // Check whether the premises are open 24 hours
                                    val is24Hours = isOpen24Hours(place)
                                    
                                    // Prepare the label list
                                    val tags = if (is24Hours) {
                                        // Add 24/7 tags and venue type tags
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
                                        imageUrl = photoUrl,
                                        tags = tags,
                                        type = currentType,
                                        latitude = place.latLng.latitude,
                                        longitude = place.latLng.longitude
                                    )
                                    
                                    // Record the debugging information and show whether there is a 24-hour label
                                    Log.d(TAG, "Place information: ${place.name}, label: ${fitnessPlace.tags}, Is it 24 hours: $is24Hours")
                                    
                                    // Avoid adding locations with the same ID repeatedly
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
            
            // Return the available results
            results
        } catch (e: Exception) {
            Log.e(TAG, "Error in searchNearbyFitnessPlaces", e)
            emptyList()
        }
    }
    
    /**
     * Obtain the appropriate TypeFilter based on the query type
     */
    private fun getTypeFilterForQuery(query: String): TypeFilter {
        return when {
            query.contains("gym") || query.contains("fitness") -> TypeFilter.ESTABLISHMENT
            query.contains("yoga") -> TypeFilter.ESTABLISHMENT
            query.contains("swimming") -> TypeFilter.ESTABLISHMENT
            query.contains("park") || query.contains("playground") || query.contains("outdoor fitness") -> TypeFilter.GEOCODE
            else -> TypeFilter.ESTABLISHMENT
        }
    }

    /**
     * Create rectangular boundaries based on position and radius
     */
    private fun createBoundsFromLocation(center: LatLng, radiusInMeters: Int): LatLngBounds {
        // Convert the radius from meters to degrees (roughly calculated: 1 degree is approximately equal to 111 kilometers)
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

    private fun getPlaceTypeFromGoogleType(placeTypes: List<Place.Type>?, query: String): PlaceType {
        val placeName = query.lowercase()
        if (placeName.contains("yoga")) {
            return PlaceType.YOGA
        }
        if (placeName.contains("playground") || placeName.contains("outdoor fitness")) {
            return PlaceType.PARK
        }
        
        if (placeTypes == null || placeTypes.isEmpty()) {
            // Infer the type based on the query
            return when {
                query.contains("gym") || query.contains("fitness") -> PlaceType.GYM
                query.contains("yoga") -> PlaceType.YOGA
                query.contains("swimming") -> PlaceType.SWIMMING
                query.contains("park") || query.contains("playground") -> PlaceType.PARK
                else -> PlaceType.GYM
            }
        }
        
        val typeStrings = placeTypes.map { it.name.lowercase() }
        
        return when {
            typeStrings.any { it.contains("yoga") } || (typeStrings.contains("establishment") && query.contains("yoga")) -> PlaceType.YOGA
            typeStrings.any { it.contains("gym") || it.contains("fitness") } || (typeStrings.contains("establishment") && (query.contains("gym") || query.contains("fitness"))) -> PlaceType.GYM
            typeStrings.any { it.contains("swimming") || it.contains("pool") } || (typeStrings.contains("establishment") && query.contains("swimming")) -> PlaceType.SWIMMING
            typeStrings.any { it.contains("park") || it.contains("playground") || it.contains("amusement_park") } || (typeStrings.contains("establishment") && (query.contains("park") || query.contains("playground"))) -> PlaceType.PARK
            else -> PlaceType.GYM 
        }
    }
    
    /**
     * Check directly whether the premises are open 24 hours
     */
    fun isOpen24Hours(place: Place): Boolean {
        // Common 24-hour gym brands and keywords
        val brands24Hours = listOf(
            "anytime fitness", "24 hour fitness", "jetts fitness", "snap fitness",
            "plus fitness", "fitness 24", "planet fitness", "24/7 fitness", "goodlife"
        )
        
        // Other possible keywords that may represent 24 hours
        val keywords24Hours = listOf(
            "24 hour", "24/7", "open 24", "always open", "all day", "all night"
        )
        
        // Get the name of the place and convert it to lowercase to ignore case comparison
        val name = place.name?.lowercase() ?: ""
        
        // 1. Check if it is a known 24-hour brand
        val isKnown24HourBrand = brands24Hours.any { name.contains(it) }
        
        // 2. Check whether the name contains 24-hour keywords
        val nameContains24HourKeyword = keywords24Hours.any { name.contains(it) }
        
        // 3. Check whether the business hours indicate 24-hour operation
        val is24HoursByHours = place.openingHours?.let { hours ->
            hours.periods?.any { period -> 
                period.open?.day == period.close?.day && 
                period.open?.time?.hours == 0 && period.open?.time?.minutes == 0 && 
                period.close?.time?.hours == 23 && period.close?.time?.minutes == 59
            } ?: false
        } ?: false
        
        // 4. Check whether the business hours text contains words related to "24 hours"
        val is24HoursByText = place.openingHours?.let { hours ->
            hours.weekdayText?.any { 
                it.contains("Open 24 hours", ignoreCase = true) ||
                it.contains("24 hours", ignoreCase = true) ||
                it.contains("24/7", ignoreCase = true) ||
                it.contains("Always open", ignoreCase = true)
            } ?: false
        } ?: false
        
        // Record the debugging information
        Log.d(TAG, "24-hour detection: ${place.name}")
        Log.d(TAG, "- It is known that the 24-hour brand=$isKnown24HourBrand")
        Log.d(TAG, "- The name contains 24-hour keywords=$nameContains24HourKeyword")
        Log.d(TAG, "- The business hours data indicates 24 hours=$is24HoursByHours")
        Log.d(TAG, "- The business hours text indicates 24 hours=$is24HoursByText")
        Log.d(TAG, "- Business hours text: ${place.openingHours?.weekdayText}")
        
        // If it is a known 24-hour brand, or the business hours explicitly indicate 24 hours, or the name contains keywords, return true
        return isKnown24HourBrand || is24HoursByHours || is24HoursByText || nameContains24HourKeyword
    }

    /**
     * Generate tags from the location type
     */
    private fun getTagsFromPlaceTypes(placeTypes: List<Place.Type>?, query: String, openingHours: com.google.android.libraries.places.api.model.OpeningHours? = null): List<String> {
        val tags = mutableListOf<String>()
        
        // Check whether it is open 24 hours
        // Method 1: Check if the time period is from 0:00 to 23:59
        val is24HoursByPeriod = openingHours?.periods?.any { period -> 
            period.open?.day == period.close?.day && 
            period.open?.time?.hours == 0 && period.open?.time?.minutes == 0 && 
            period.close?.time?.hours == 23 && period.close?.time?.minutes == 59
        } ?: false
        
        // Method 2: Check whether the text description of business hours contains "Open 24 hours"
        val is24HoursByText = openingHours?.weekdayText?.any { 
            it.contains("Open 24 hours", ignoreCase = true) ||
            it.contains("24 hours", ignoreCase = true) ||
            it.contains("24/7", ignoreCase = true) ||
            it.contains("Always open", ignoreCase = true)
        } ?: false
        
        // Method 3: Check whether the name contains any words related to "24 hours"
        val nameContains24h = query.lowercase().contains("24") || 
                             query.lowercase().contains("hour") || 
                             query.lowercase().contains("anytime") ||
                             query.lowercase().contains("fitness")
        
        // If any method determines that it is open 24 hours, it is considered to be open 24 hours
        val is24HoursOpen = is24HoursByPeriod || is24HoursByText || (nameContains24h && query.lowercase().contains("fit"))
        
        // Add log output to assist in debugging
        Log.d("PlacesSearchService", "Business hours detection: query=$query")
        Log.d("PlacesSearchService", "Business hours text: ${openingHours?.weekdayText}")
        Log.d("PlacesSearchService", "is24HoursByPeriod=$is24HoursByPeriod, is24HoursByText=$is24HoursByText, nameContains24h=$nameContains24h")
        
        if (is24HoursOpen) {
            tags.add("24/7")
            Log.d("PlacesSearchService", "24-hour operation was detected: $query")
            return tags
        }
        
        // It's not open 24 hours a day. Just choose the most suitable label
        when {
            query.contains("yoga") -> tags.add("Yoga")
            query.contains("gym") || query.contains("fitness") -> tags.add("Gym")
            query.contains("swimming") -> tags.add("Swimming")
            query.contains("park") || query.contains("playground") || query.contains("outdoor fitness") -> tags.add("Outdoor")
        }
        
        // If the label is not obtained from the query, try to obtain it from the location type
        if (tags.isEmpty() && placeTypes != null && placeTypes.isNotEmpty()) {
            val typeStrings = placeTypes.map { it.name.lowercase() }
            
            when {
                typeStrings.any { it.contains("yoga") } -> tags.add("Yoga")
                typeStrings.any { it.contains("gym") || it.contains("fitness") } -> tags.add("Gym")
                typeStrings.any { it.contains("swimming") || it.contains("pool") } -> tags.add("Swimming")
                typeStrings.any { it.contains("park") || it.contains("playground") } -> tags.add("Outdoor")
                else -> tags.add("Fitness")
            }
        }
        
        return tags
    }
    
    /**
     * Obtain appropriate image resources based on the location type
     */
    private fun getImageResourceByType(type: PlaceType): Int {
        return when (type) {
            PlaceType.GYM -> R.drawable.gym_image
            PlaceType.YOGA -> R.drawable.yoga_image
            PlaceType.SWIMMING -> R.drawable.gym_image
            PlaceType.PARK -> R.drawable.outdoor_gym
        }
    }
    
    /**
     * Use Haversine The formula calculates the distance (in kilometers) between two points.
     */
    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0
        
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        
        return r * c
    }
} 