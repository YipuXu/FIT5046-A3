package com.example.fitlife.ui.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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

@Composable
fun AboutUsScreen(
    onBackClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9FAFB))
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Top bar
            TopBar(onBackClick = onBackClick)

            // App Logo
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = "App Logo",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(24.dp)),
                    contentScale = ContentScale.Fit
                )
            }

            // App Name and Version
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "FitLife",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937)
                )
                Text(
                    text = "Version 1.0.0",
                    fontSize = 14.sp,
                    color = Color(0xFF6B7280),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // About Content
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Text(
                        text = "About FitLife",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F2937)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "FitLife is a smart fitness application focused on helping users achieve a healthy lifestyle. Our mission is to make professional fitness guidance easily accessible to everyone, enabling personalized health goals.",
                        fontSize = 14.sp,
                        color = Color(0xFF4B5563),
                        lineHeight = 24.sp
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Text(
                        text = "Key Features",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1F2937)
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    FeatureItem(
                        title = "Personalized Training Plans",
                        description = "Customized training plans based on your fitness goals, physical condition, and schedule"
                    )
                    
                    FeatureItem(
                        title = "Real-time Activity Tracking",
                        description = "Precise recording of workout data, including duration, calories burned, and activity routes"
                    )
                    
                    FeatureItem(
                        title = "Professional Guidance",
                        description = "Standard exercise videos and detailed training instructions to ensure proper form"
                    )
                    
                    FeatureItem(
                        title = "Community Interaction",
                        description = "Share experiences with other fitness enthusiasts, motivate each other, and grow together"
                    )
                }
            }

            // Contact Info
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Contact Us",
                    fontSize = 14.sp,
                    color = Color(0xFF6B7280)
                )
                Text(
                    text = "support@fitlife.com",
                    fontSize = 14.sp,
                    color = Color(0xFF3B82F6),
                    modifier = Modifier.padding(top = 4.dp)
                )
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun TopBar(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Back button
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(Color(0xFFF3F4F6))
                .clickable { onBackClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier.size(20.dp),
                tint = Color(0xFF6B7280)
            )
        }

        // Title
        Text(
            text = "About Us",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            textAlign = TextAlign.Center,
            color = Color(0xFF1F2937)
        )
        
        // Placeholder for symmetry
        Spacer(
            modifier = Modifier
                .size(32.dp)
        )
    }
}

@Composable
private fun FeatureItem(
    title: String,
    description: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = "• $title",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF1F2937)
        )
        Text(
            text = description,
            fontSize = 14.sp,
            color = Color(0xFF6B7280),
            modifier = Modifier.padding(start = 16.dp, top = 4.dp),
            lineHeight = 20.sp
        )
    }
} 