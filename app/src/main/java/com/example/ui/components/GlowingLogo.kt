package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NatokGold
import com.example.ui.theme.NatokGoldLight
import com.example.ui.theme.NatokRed
import com.example.ui.theme.NatokRedDark

/**
 * 3D-styled Glowing NatokFlix Logo
 * Features golden play button with film reel style perforations and radial ambient glow.
 */
@Composable
fun GlowingNatokFlixLogo(
    modifier: Modifier = Modifier,
    size: Dp = 80.dp,
    showText: Boolean = false,
    fontSize: TextUnit = 24.sp,
    enablePulse: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "logo_glow")
    val pulseScale by if (enablePulse) {
        infiniteTransition.animateFloat(
            initialValue = 0.96f,
            targetValue = 1.04f,
            animationSpec = infiniteRepeatable(
                animation = tween(1200, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulse"
        )
    } else {
        rememberInfiniteTransition(label = "static").animateFloat(
            initialValue = 1f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(tween(1000)),
            label = "static"
        )
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .scale(pulseScale)
                .shadow(
                    elevation = 16.dp,
                    shape = RoundedCornerShape(size * 0.28f),
                    ambientColor = NatokRed,
                    spotColor = NatokGold
                )
                .clip(RoundedCornerShape(size * 0.28f))
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF38080C),
                            Color(0xFF1F0407),
                            Color(0xFF100204)
                        )
                    )
                )
                .border(
                    width = 2.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(NatokGold, NatokRed, NatokGoldLight, NatokRedDark)
                    ),
                    shape = RoundedCornerShape(size * 0.28f)
                ),
            contentAlignment = Alignment.Center
        ) {
            // Film Reel Style Perforations Canvas
            Canvas(modifier = Modifier.size(size)) {
                val w = this.size.width
                val h = this.size.height
                val notchRadius = w * 0.045f

                // Film reel notch holes (top and bottom edges)
                val notchCount = 5
                val step = w / (notchCount + 1)
                for (i in 1..notchCount) {
                    val x = step * i
                    // Top notches
                    drawCircle(
                        color = Color(0xFFFFD54F).copy(alpha = 0.6f),
                        radius = notchRadius,
                        center = Offset(x, h * 0.12f)
                    )
                    // Bottom notches
                    drawCircle(
                        color = Color(0xFFFFD54F).copy(alpha = 0.6f),
                        radius = notchRadius,
                        center = Offset(x, h * 0.88f)
                    )
                }

                // 3D Play Button Triangle with gradient depth
                val path = Path().apply {
                    val startX = w * 0.38f
                    val endX = w * 0.72f
                    val topY = h * 0.28f
                    val bottomY = h * 0.72f
                    val midY = h * 0.50f

                    moveTo(startX, topY)
                    lineTo(endX, midY)
                    lineTo(startX, bottomY)
                    close()
                }

                // Drop shadow for play button
                drawPath(
                    path = path,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            NatokGoldLight,
                            NatokGold,
                            Color(0xFFCC8400)
                        )
                    )
                )
            }

            // Central 3D Highlighting Play Icon
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "NatokFlix Play",
                tint = Color(0xFFFFF6D6),
                modifier = Modifier
                    .size(size * 0.44f)
                    .padding(start = size * 0.06f)
            )
        }

        if (showText) {
            Spacer(modifier = Modifier.width(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Natok",
                    fontSize = fontSize,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    letterSpacing = (-0.5).sp
                )
                Text(
                    text = "Flix",
                    fontSize = fontSize,
                    fontWeight = FontWeight.ExtraBold,
                    color = NatokRed,
                    letterSpacing = (-0.5).sp
                )
            }
        }
    }
}
