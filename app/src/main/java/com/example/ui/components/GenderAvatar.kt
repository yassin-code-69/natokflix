package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.Male
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GenderAvatar(
    gender: String, // "Boy" / "Girl" / "Male" / "Female"
    modifier: Modifier = Modifier,
    size: Dp = 50.dp,
    name: String = "",
    showOnlineDot: Boolean = false,
    isOnline: Boolean = true,
    isBot: Boolean = false
) {
    val isBoy = gender.equals("Boy", ignoreCase = true) || gender.equals("Male", ignoreCase = true) || gender.contains("ছেলে")

    val bgGradient = if (isBoy) {
        Brush.linearGradient(
            colors = listOf(
                Color(0xFF1E88E5),
                Color(0xFF0D47A1)
            )
        )
    } else {
        Brush.linearGradient(
            colors = listOf(
                Color(0xFFEC407A),
                Color(0xFF880E4F)
            )
        )
    }

    val borderColor = if (isBot) {
        Color(0xFFFFD700) // Golden border for bot VIP
    } else if (isBoy) {
        Color(0xFF64B5F6)
    } else {
        Color(0xFFF48FB1)
    }

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(bgGradient)
                .border(1.5.dp, borderColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (name.isNotBlank()) {
                Text(
                    text = name.trim().firstOrNull()?.toString()?.uppercase() ?: (if (isBoy) "B" else "G"),
                    fontSize = (size.value * 0.42).sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            } else {
                Icon(
                    imageVector = if (isBoy) Icons.Filled.Male else Icons.Filled.Female,
                    contentDescription = if (isBoy) "Boy Avatar" else "Girl Avatar",
                    tint = Color.White,
                    modifier = Modifier.size(size * 0.55f)
                )
            }
        }

        // Online indicator dot
        if (showOnlineDot) {
            val dotColor = if (isOnline) Color(0xFF00E676) else Color(0xFF9E9E9E)
            Box(
                modifier = Modifier
                    .size(size * 0.28f)
                    .align(Alignment.BottomEnd)
                    .clip(CircleShape)
                    .background(dotColor)
                    .border(2.dp, Color(0xFF16121E), CircleShape)
            )
        }
    }
}
