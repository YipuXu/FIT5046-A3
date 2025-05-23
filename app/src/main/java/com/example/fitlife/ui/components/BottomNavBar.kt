package com.example.fitlife.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
// Replace the problematic imports with available icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.compose.foundation.background

@Composable
fun BottomNavBar(
    currentRoute: String,
    onNavigateToHome: () -> Unit,
    onNavigateToCalendar: () -> Unit,
    onNavigateToMap: () -> Unit,
    onNavigateToProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Container for the top border
    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        // Top border line
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color(0xFFE5E7EB))
                .align(Alignment.TopCenter)
                .zIndex(1f)
        )
        
        // Navigation bar
        NavigationBar(
            modifier = Modifier.fillMaxWidth(),
            containerColor = Color.White,
            tonalElevation = 0.dp // Remove shadow, use border line instead
        ) {
            BottomNavItem(
                icon = Icons.Default.Home,
                label = "Home",
                selected = currentRoute == "home",
                onClick = onNavigateToHome,
                selectedColor = Color(0xFF3B82F6)
            )
            
            BottomNavItem(
                icon = Icons.Default.DateRange,
                label = "Calendar",
                selected = currentRoute == "calendar",
                onClick = onNavigateToCalendar,
                selectedColor = Color(0xFF3B82F6)
            )
            
            BottomNavItem(
                icon = Icons.Default.Place,
                label = "Map",
                selected = currentRoute == "map",
                onClick = onNavigateToMap,
                selectedColor = Color(0xFF3B82F6)
            )
            
            BottomNavItem(
                icon = Icons.Default.Person,
                label = "Profile",
                selected = currentRoute == "profile",
                onClick = onNavigateToProfile,
                selectedColor = Color(0xFF3B82F6)
            )
        }
    }
}

@Composable
fun RowScope.BottomNavItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    selectedColor: Color = Color(0xFF3B82F6)
) {
    NavigationBarItem(
        icon = { Icon(icon, contentDescription = label) },
        label = { Text(label) },
        selected = selected,
        onClick = onClick,
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = selectedColor,
            selectedTextColor = selectedColor,
            indicatorColor = Color.White,
            unselectedIconColor = Color(0xFF6B7280),
            unselectedTextColor = Color(0xFF6B7280)
        )
    )
}