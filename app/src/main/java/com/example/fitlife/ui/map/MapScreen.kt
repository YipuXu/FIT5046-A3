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
    
    // Default sample data (displayed when there are no search results)
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
            // Add more sample data points to enable the map to display a broader area
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

    // Store the fitness places found through search
    var searchResults by remember { mutableStateOf<List<FitnessPlace>>(emptyList()) }
    
    // Is it searching
    var isSearching by remember { mutableStateOf(false) }
    
    // error message
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    // The current location of the user
    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    
    // Has the initial search been performed
    var initialSearchDone by remember { mutableStateOf(false) }
    
    // Coordinates of the Clayton campus
    val claytonCampus = remember { LatLng(-37.9105, 145.1363) }
    
    // FusedLocationProviderClient Used to obtain the location of the device
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    
    // The function for obtaining the user's location
    fun fetchUserLocation() {
        try {
            // Authority Check
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // Obtain the last known position
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        location?.let {
                            userLocation = LatLng(it.latitude, it.longitude)
                        } ?: run {
                            if (!initialSearchDone) {
                                userLocation = claytonCampus
                            }
                        }
                    }
            } else {
                // If you don't have location permission, use the Clayton campus
                if (!initialSearchDone) {
                    userLocation = claytonCampus
                }
            }
        } catch (e: Exception) {
            Log.e("MapScreen", "Error getting location", e)
            // Use the Clayton campus when errors occur
            if (!initialSearchDone) {
                userLocation = claytonCampus
            }
        }
    }
    
    // Execute the function for searching nearby fitness places
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
    
    // Request location permissions and obtain the user's location
    DisposableEffect(Unit) {
        fetchUserLocation()
        onDispose { }
    }
    
    // Automatically search for nearby fitness venues when the user's location changes
    LaunchedEffect(userLocation) {
        userLocation?.let {
            if (!initialSearchDone) {
                searchNearbyPlaces(it, selectedFilter.takeIf { filter -> filter != "All" })
            }
        }
    }
    
    // Listen for changes in the search text. If it is empty, reset the state
    LaunchedEffect(searchText) {
        if (searchText.isBlank() && initialSearchDone) {
            userLocation?.let {
                searchNearbyPlaces(it, selectedFilter.takeIf { filter -> filter != "All" })
            }
        }
    }
    
    // Handle the click of the search button
    val handleSearch = {
        if (searchText.isNotBlank()) {
            // Reset the error message
            errorMessage = null
            // Set the status in search
            isSearching = true
            
            // Start the coroutine to perform the search
            coroutineScope.launch {
                try {
                    // 1. Search for the location and obtain the longitude and latitude
                    val location = placesSearchService.searchPlaceByName(searchText)
                    
                    if (location != null) {
                        // 2. Search for fitness places around
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
                        
                        // 3. Update the search results
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
                        // If the location cannot be found, display an error message
                        searchResults = emptyList()
                        searchLocation = null
                        errorMessage = "Location not found: '${searchText}'"
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    // An error occurs and the error message is displayed
                    searchResults = emptyList()
                    searchLocation = null
                    errorMessage = "Error during search: ${e.localizedMessage ?: "Unknown error"}"
                } finally {
                    // End the search status
                    isSearching = false
                }
            }
        }
    }

    // The actual displayed list of locations
    val placesToShow = searchResults
    
    // Use derivedStateOf to ensure that the filtering logic is calculated only when the dependent state changes
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
            // Search bar and title
            SearchBarWithTitle(
                searchText = searchText,
                onSearchTextChanged = { searchText = it },
                onSearch = handleSearch,
                isSearching = isSearching
            )
            
            // Map view (input the search location and the current location of the user)
            MapView(
                filteredPlaces = filteredPlaces,
                searchLocation = searchLocation,
                userLocation = userLocation
            )
            
            // Filter label
            FilterTabs(
                filters = filters,
                selectedFilter = selectedFilter,
                onFilterSelected = { 
                    selectedFilter = it
                    // A new search is triggered only when the search result is empty
                    if (searchResults.isEmpty() && userLocation != null) {
                        searchNearbyPlaces(userLocation!!, selectedFilter.takeIf { filter -> filter != "All" })
                    }
                }
            )
            
            // List
            if (isSearching) {
                // The list of loading indicator venues is displayed in the battle search
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
                // Display error message
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
                // No search results
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
                // Display the list of locations
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
        
        // Bottom Tabbar
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
        // Title and menu button lines
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // Title
            Text(
                text = "Nearby Fitness Places",
                fontSize = 18.sp, 
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = Color(0xFF1F2937)
            )
        }
        
        // Add spacing between the title line and the search bar
        Spacer(modifier = Modifier.height(16.dp))
        
        // search box
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
    val initialPosition = userLocation ?: searchLocation ?: defaultCameraPosition
    
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialPosition, 13f)
    }

    LaunchedEffect(filteredPlaces, searchLocation, userLocation) {
        if (searchLocation != null) {
            if (filteredPlaces.isEmpty()) {
                // If there is no filtered location, only the search location will be displayed
                cameraPositionState.animate(
                    CameraUpdateFactory.newLatLngZoom(searchLocation, 14f)
                )
            } else if (filteredPlaces.size == 1) {
                // If there is only one location, display that location
                val place = filteredPlaces.first()
                cameraPositionState.animate(
                    CameraUpdateFactory.newLatLngZoom(LatLng(place.latitude, place.longitude), 15f)
                )
            } else {
                // There are multiple locations. Display the boundaries of all locations
                val boundsBuilder = LatLngBounds.builder()
                // Add the search location
                boundsBuilder.include(searchLocation)
                // Add all the filtered locations
                for (place in filteredPlaces) {
                    boundsBuilder.include(LatLng(place.latitude, place.longitude))
                }
                val padding = 250
                cameraPositionState.animate(
                    CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), padding),
                    durationMs = 1000
                )
            }
        } else if (userLocation != null && filteredPlaces.isEmpty()) {
            // If there is a user's location but no search results, display the user's location
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(userLocation, 14f)
            )
        } else if (filteredPlaces.isNotEmpty()) {
            // There is no clear search location. Use the previous logic
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
            // There is no specific location or search result that shows the user's location or the default location
            val position = userLocation ?: defaultCameraPosition
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(position, 13f)
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
        // Display all filtered location markers
        filteredPlaces.forEach { place ->
            // Choose different marking colors according to the type of location
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
        
        // If there is a search location, display a blue mark to indicate the search location
        searchLocation?.let {
            Marker(
                state = MarkerState(position = it),
                title = "Search Location",
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
            )
        }
        
        // If there is a user location, display a cyan mark to indicate the current location of the user
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
                    Text(
                        text = place.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF1F2937),
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(8.dp))
                    
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
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    place.tags.take(2).forEach { tag ->
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
 * Obtain the default image resources based on the location type
 */
private fun getDefaultImageResourceByType(type: PlaceType): Int {
    return when (type) {
        PlaceType.GYM -> R.drawable.gym_image
        PlaceType.YOGA -> R.drawable.yoga_image
        PlaceType.SWIMMING -> R.drawable.swim_image 
        PlaceType.PARK -> R.drawable.outdoor_gym
    }
}