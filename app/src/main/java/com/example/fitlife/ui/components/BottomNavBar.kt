package com.example.fitlife.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
// Replace the problematic imports with available icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun BottomNavBar(
    currentRoute: String,
    onNavigateToHome: () -> Unit,
    onNavigateToCalendar: () -> Unit,
    onNavigateToMap: () -> Unit,
    onNavigateToProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier.fillMaxWidth(),
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        BottomNavItem(
            icon = Icons.Default.Home,
            label = "Home",
            selected = currentRoute == "home",
            onClick = onNavigateToHome
        )
        
        BottomNavItem(
            // Use DateRange instead of CalendarToday
            icon = Icons.Default.DateRange,
            label = "Calendar",
            selected = currentRoute == "calendar",
            onClick = onNavigateToCalendar
        )
        
        BottomNavItem(
            // Use Place instead of Map
            icon = Icons.Default.Place,
            label = "Map",
            selected = currentRoute == "map",
            onClick = onNavigateToMap
        )
        
        BottomNavItem(
            icon = Icons.Default.Person,
            label = "Profile",
            selected = currentRoute == "profile",
            onClick = onNavigateToProfile
        )
    }
}

@Composable
fun RowScope.BottomNavItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    NavigationBarItem(
        icon = { Icon(icon, contentDescription = label) },
        label = { Text(label) },
        selected = selected,
        onClick = onClick,
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = Color.Blue,
            selectedTextColor = Color.Blue,
            indicatorColor = Color.White
        )
    )
}