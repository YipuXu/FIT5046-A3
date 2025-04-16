package com.example.fitlife.ui.policy

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.fitlife.R
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding

@Composable
fun PrivacyPolicyScreen(
    onBackClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9FAFB))
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        // Fixed top bar
        TopBar(
            onBackClick = onBackClick,
            modifier = Modifier
                .fillMaxWidth()
                .zIndex(1f) // Ensure top bar stays above scrolling content
                .background(Color(0xFFF9FAFB))
        )

        // Scrollable content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 64.dp) // Add padding for top bar
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            // Privacy Policy Content Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
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
                    PolicySection(
                        title = "Introduction",
                        content = "Welcome to FitLife! We are committed to protecting your privacy. This Privacy Policy explains how we collect, use, disclose, and safeguard your information when you use our mobile application."
                    )

                    PolicySection(
                        title = "Information We Collect",
                        content = "We may collect information about you in a variety of ways. The information we may collect via the Application includes:\n\n- Personal Data: Personally identifiable information, such as your name, email address, and demographic information (like your age, gender, hometown, and interests), that you voluntarily give to us when you register with the Application.\n- Fitness Data: Information related to your fitness activities, goals, workout logs, height, weight, and other health-related information you provide."
                    )

                    PolicySection(
                        title = "Use of Your Information",
                        content = "Having accurate information permits us to provide you with a smooth, efficient, and customized experience. Specifically, we may use information collected about you via the Application to:\n\n- Create and manage your account.\n- Personalize and improve your experience.\n- Monitor and analyze usage and trends to improve functionality."
                    )

                    PolicySection(
                        title = "Security of Your Information",
                        content = "We use administrative, technical, and physical security measures to help protect your personal information. While we have taken reasonable steps to secure the personal information you provide to us, please be aware that despite our efforts, no security measures are perfect or impenetrable, and no method of data transmission can be guaranteed against any interception or other type of misuse."
                    )
                    
                    PolicySection(
                        title = "Contact Us",
                        content = "If you have questions or comments about this Privacy Policy, please contact us at: privacy@fitlife.com"
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun TopBar(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFFF9FAFB))
    ) {
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
                text = "Privacy Policy",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                textAlign = TextAlign.Center,
                color = Color(0xFF1F2937)
            )
            
            // Placeholder for symmetry
            Spacer(modifier = Modifier.size(32.dp))
        }
    }
}

@Composable
private fun PolicySection(
    title: String,
    content: String
) {
    Column(modifier = Modifier.padding(bottom = 24.dp)) {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1F2937),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = content,
            fontSize = 14.sp,
            color = Color(0xFF4B5563),
            lineHeight = 22.sp
        )
    }
} 