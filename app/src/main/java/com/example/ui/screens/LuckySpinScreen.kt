package com.example.ui.screens

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.User
import com.example.ui.components.AdMobBanner
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.BorderDark
import com.example.ui.theme.NatokGold
import com.example.ui.theme.NatokGoldLight
import com.example.ui.theme.NatokRed
import com.example.ui.theme.NatokRedDark
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

data class SpinPlayerWinner(
    val name: String,
    val coins: Int,
    val timeAgo: String,
    val avatarBg: Color
)

@Composable
fun LuckySpinScreen(
    user: User,
    isSpinning: Boolean,
    spinAngle: Float,
    spinResultPoints: Int?,
    dailyCheckInClaimed: Boolean,
    freeSpinsRemaining: Int,
    adSpinsUnlocked: Int,
    totalSpinsUsedToday: Int,
    maxDailySpins: Int,
    isUnlockingSpinAd: Boolean,
    onSpinClick: () -> Unit,
    onUnlockSpinViaAd: () -> Unit,
    onClaimDaily: () -> Unit,
    onWatchRewardedAd: () -> Unit,
    modifier: Modifier = Modifier
) {
    val animatedRotation by animateFloatAsState(
        targetValue = spinAngle,
        animationSpec = tween(durationMillis = 3600),
        label = "spin_wheel_rotation"
    )

    // Slice configurations: 5, 10, 20, 50, 100
    val sliceData = listOf(
        Pair("5", Color(0xFFE50914)),
        Pair("10", Color(0xFF1E1E2C)),
        Pair("20", Color(0xFFE2136E)),
        Pair("50", Color(0xFF1E1E2C)),
        Pair("100", Color(0xFFF7941D))
    )

    val sampleWinners = remember {
        listOf(
            SpinPlayerWinner("সবুজ", 20, "এইমাত্র", Color(0xFF4CAF50)),
            SpinPlayerWinner("আরিফ", 20, "৩ সেকেন্ড আগে", Color(0xFF2196F3)),
            SpinPlayerWinner("রাহুল", 20, "৮ সেকেন্ড আগে", Color(0xFFFF9800)),
            SpinPlayerWinner("রোকসানা", 50, "১৫ সেকেন্ড আগে", Color(0xFFE91E63)),
            SpinPlayerWinner("তানভীর", 20, "২২ সেকেন্ড আগে", Color(0xFF9C27B0)),
            SpinPlayerWinner("সুমন", 100, "৩০ সেকেন্ড আগে", Color(0xFFFF5722)),
            SpinPlayerWinner("মিষ্টি", 20, "৩৮ সেকেন্ড আগে", Color(0xFF00BCD4)),
            SpinPlayerWinner("ফরহাদ", 10, "৪৫ সেকেন্ড আগে", Color(0xFF8BC34A)),
            SpinPlayerWinner("শারমিন", 20, "৫২ সেকেন্ড আগে", Color(0xFFE040FB)),
            SpinPlayerWinner("ইমরান", 20, "১ মিনিট আগে", Color(0xFF00E676))
        )
    }

    var currentWinnerIndex by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(2400)
            currentWinnerIndex = (currentWinnerIndex + 1) % sampleWinners.size
        }
    }
    val activeWinner = sampleWinners[currentWinnerIndex]

    val canSpinNow = (freeSpinsRemaining > 0 || adSpinsUnlocked > 0) && totalSpinsUsedToday < maxDailySpins && !isSpinning
    val allSpinsExhausted = totalSpinsUsedToday >= maxDailySpins

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .testTag("lucky_spin_screen"),
        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 90.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(
                text = "লাকি স্পিন ও রিওয়ার্ডস",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "প্রতিদিন ফ্রি স্পিন ও বিজ্ঞাপন দেখে কয়েন জিতে নিন",
                fontSize = 13.sp,
                color = TextSecondary,
                modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
            )

            // Spin Limit Status Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceCard)
                    .border(1.dp, BorderDark, RoundedCornerShape(12.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "দৈনিক স্পিন লিমিট:",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = "ব্যবহৃত: $totalSpinsUsedToday / $maxDailySpins টি",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (allSpinsExhausted) Color(0xFFFF5252) else NatokGold
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (freeSpinsRemaining > 0) Color(0xFF143B1D) else Color(0xFF2E2E3E))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (freeSpinsRemaining > 0) "ফ্রি স্পিন: ১" else "ফ্রি: ০",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (freeSpinsRemaining > 0) Color(0xFF69F0AE) else Color(0xFFAAAAAA)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (adSpinsUnlocked > 0) Color(0xFF33260A) else Color(0xFF2E2E3E))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "বোনাস: $adSpinsUnlocked",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (adSpinsUnlocked > 0) NatokGold else Color(0xFFAAAAAA)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
        }

        // Daily Check-in Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF33260A)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CardGiftcard,
                                contentDescription = null,
                                tint = NatokGold,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "ডেইলি চেক-ইন বোনাস",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "+৫০ কয়েন নিশ্চিত বোনাস",
                                fontSize = 12.sp,
                                color = NatokGold
                            )
                        }
                    }

                    Button(
                        onClick = onClaimDaily,
                        enabled = !dailyCheckInClaimed,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NatokGold,
                            contentColor = Color.Black,
                            disabledContainerColor = Color(0xFF333340),
                            disabledContentColor = Color(0xFF777788)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("claim_daily_button")
                    ) {
                        Text(
                            text = if (dailyCheckInClaimed) "গৃহীত ✅" else "ক্লেইম করুন",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Spin Wheel Visualizer with numbers clearly visible!
        item {
            Box(
                modifier = Modifier
                    .size(290.dp)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                // Wheel Outer Rim
                Box(
                    modifier = Modifier
                        .size(270.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1B1B26))
                        .border(6.dp, NatokGold, CircleShape)
                )

                // Slices Canvas with text values clearly rendered
                Canvas(
                    modifier = Modifier
                        .size(250.dp)
                        .rotate(animatedRotation)
                ) {
                    val sweepAngle = 360f / sliceData.size
                    val radius = size.width / 2f
                    val center = Offset(size.width / 2f, size.height / 2f)

                    sliceData.forEachIndexed { index, (valueText, sliceColor) ->
                        val startAngle = index * sweepAngle

                        // Draw Slice Arc
                        drawArc(
                            color = sliceColor,
                            startAngle = startAngle,
                            sweepAngle = sweepAngle,
                            useCenter = true,
                            size = Size(size.width, size.height)
                        )

                        // Draw Text on slice
                        drawContext.canvas.nativeCanvas.apply {
                            val midAngle = Math.toRadians((startAngle + sweepAngle / 2.0))
                            val textDistance = radius * 0.65f
                            val textX = (center.x + textDistance * cos(midAngle)).toFloat()
                            val textY = (center.y + textDistance * sin(midAngle)).toFloat()

                            save()
                            rotate((startAngle + sweepAngle / 2f + 90f), textX, textY)

                            val paint = Paint().apply {
                                color = android.graphics.Color.WHITE
                                textSize = 28f
                                textAlign = Paint.Align.CENTER
                                isFakeBoldText = true
                                typeface = Typeface.DEFAULT_BOLD
                            }
                            drawText(valueText, textX, textY, paint)
                            restore()
                        }
                    }
                }

                // Center Pin Hub
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(NatokGold)
                        .border(3.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "SPIN",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black
                    )
                }

                // Needle Pointer at top pointing down
                Canvas(
                    modifier = Modifier
                        .size(36.dp)
                        .align(Alignment.TopCenter)
                        .padding(top = 2.dp)
                ) {
                    val path = Path().apply {
                        moveTo(size.width / 2f, size.height)
                        lineTo(0f, 0f)
                        lineTo(size.width, 0f)
                        close()
                    }
                    drawPath(path, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Spin CTA Button
            Button(
                onClick = onSpinClick,
                enabled = canSpinNow,
                colors = ButtonDefaults.buttonColors(
                    containerColor = NatokRed,
                    disabledContainerColor = Color(0xFF333342),
                    disabledContentColor = Color(0xFF888899)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("spin_wheel_button")
            ) {
                if (isSpinning) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "স্পিন ঘুরছে...",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                } else if (allSpinsExhausted) {
                    Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "গৃহীত (Claimed Today) • আগামীকাল আসুন",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                } else if (freeSpinsRemaining > 0) {
                    Text(
                        text = "স্পিন করুন (ফ্রি স্পিন বাকি ১টি)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                } else if (adSpinsUnlocked > 0) {
                    Text(
                        text = "স্পিন করুন (বোনাস স্পিন বাকি $adSpinsUnlocked টি)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                } else {
                    Text(
                        text = "আজকের ফ্রি স্পিন শেষ! অ্যাড দেখে আনলক করুন",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Spin result banner
            if (spinResultPoints != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF261D08))
                        .border(1.dp, NatokGold, RoundedCornerShape(8.dp))
                        .padding(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🎉 অভিনন্দন! আপনি জিতেছেন $spinResultPoints কয়েন (৳$spinResultPoints BDT)!",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = NatokGold
                    )
                }
            }

            // Floating Winner Live Popup Stream (ভাসবে আর চলে যাবে)
            AnimatedContent(
                targetState = activeWinner,
                transitionSpec = {
                    (slideInVertically { height -> height } + fadeIn()) togetherWith
                    (slideOutVertically { height -> -height } + fadeOut())
                },
                label = "floating_winner_pill"
            ) { winner ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, bottom = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1728)),
                    border = BorderStroke(1.dp, NatokGold.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(CircleShape)
                                    .background(winner.avatarBg),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = winner.name.take(1),
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 13.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "🎉 ${winner.name} পেয়েছে ${winner.coins} কয়েন!",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = Color.White
                                )
                                Text(
                                    text = "${winner.timeAgo} • স্পিন রিওয়ার্ড",
                                    fontSize = 10.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF2E2210))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "+${winner.coins} 🪙 (৳${winner.coins})",
                                color = NatokGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
        }

        // Recent Winners Live Feed Table
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(Color(0xFF00E676), CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "লাইভ উইনার্স ফিড (Live Spin Winners)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Text(
                            text = "রিয়েল-টাইম",
                            fontSize = 10.sp,
                            color = NatokGold,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    sampleWinners.take(4).forEachIndexed { idx, winner ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(winner.avatarBg),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(winner.name.take(1), fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(winner.name, fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Medium)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "+${winner.coins} কয়েন (৳${winner.coins})",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NatokGold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(winner.timeAgo, fontSize = 10.sp, color = TextSecondary)
                            }
                        }
                        if (idx < 3) {
                            HorizontalDivider(color = BorderDark.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 2.dp))
                        }
                    }
                }
            }
        }

        // Unlock Spin Via Ad Card
        item {
            val remainingToUnlock = maxDailySpins - totalSpinsUsedToday - adSpinsUnlocked
            val canUnlockMore = remainingToUnlock > 0 && totalSpinsUsedToday < maxDailySpins

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF381518)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Videocam,
                                contentDescription = null,
                                tint = NatokRed,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "বিজ্ঞাপন দেখে স্পিন আনলক",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = if (canUnlockMore) "ভিডিও দেখে আরও ১টি স্পিন নিন (বাকি: $remainingToUnlock টি)" else "আজকের বোনাস স্পিন সম্পূর্ণ",
                                fontSize = 11.sp,
                                color = if (canUnlockMore) NatokGold else Color(0xFF888899)
                            )
                        }
                    }

                    Button(
                        onClick = onUnlockSpinViaAd,
                        enabled = canUnlockMore && !isUnlockingSpinAd,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NatokRed,
                            disabledContainerColor = Color(0xFF333340),
                            disabledContentColor = Color(0xFF777788)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("unlock_spin_ad_button")
                    ) {
                        if (isUnlockingSpinAd) {
                            CircularProgressIndicator(
                                color = Color.White,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(16.dp)
                            )
                        } else {
                            Text(
                                text = if (canUnlockMore) "স্পিন আনলক" else "শেষ",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Rewarded Video Ad for Direct Coins
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF251A33)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = NatokGold,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "রিওয়ার্ড ভিডিও দেখুন",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "+৫০ কয়েন সরাসরি যোগ হবে",
                                fontSize = 11.sp,
                                color = NatokGold
                            )
                        }
                    }

                    Button(
                        onClick = onWatchRewardedAd,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF262638)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("watch_rewarded_ad_button")
                    ) {
                        Text(
                            text = "বিজ্ঞাপন দেখুন",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // Native Promo Banner (clean, no AdMob text)
        item {
            Spacer(modifier = Modifier.height(10.dp))
            AdMobBanner()
        }
    }
}
