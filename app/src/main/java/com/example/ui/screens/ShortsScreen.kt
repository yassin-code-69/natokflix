package com.example.ui.screens

import android.app.WallpaperManager
import android.graphics.Bitmap
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import coil.compose.AsyncImage
import coil.imageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.data.model.VideoItem
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.NatokGold
import com.example.ui.theme.NatokRed
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ShortsScreen(
    shorts: List<VideoItem>,
    onEarnReward: (Int) -> Unit,
    reelWatchCoins: Int = 2,
    coinsPerBdtRate: Int = 10,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { shorts.size })

    val currentShort = shorts.getOrElse(pagerState.currentPage) { shorts.first() }

    var likesCount by remember(pagerState.currentPage) { mutableIntStateOf(1420 + pagerState.currentPage * 123) }
    var isLiked by remember(pagerState.currentPage) { mutableStateOf(false) }

    // Watch & Earn Progress Timer (15 seconds per cycle - 1 Coin = 1 BDT)
    var timerSeconds by remember(pagerState.currentPage) { mutableIntStateOf(15) }
    var hasEarnedCurrentCycle by remember(pagerState.currentPage) { mutableStateOf(false) }
    var showCoinPopup by remember { mutableStateOf(false) }
    var earnedPopupText by remember { mutableStateOf("+$reelWatchCoins Coins Earned!") }

    // Episode watch counter for Mandatory Non-Skippable Ad Trigger
    var episodesWatchedCount by remember { mutableIntStateOf(0) }
    var showMandatoryAd by remember { mutableStateOf(false) }
    var adCountdown by remember { mutableIntStateOf(6) }

    // Timer countdown effect
    LaunchedEffect(pagerState.currentPage) {
        timerSeconds = 15
        hasEarnedCurrentCycle = false
        while (timerSeconds > 0) {
            delay(1000)
            timerSeconds--
        }
        if (!hasEarnedCurrentCycle) {
            hasEarnedCurrentCycle = true
            onEarnReward(reelWatchCoins)
            val bdtShare = reelWatchCoins.toDouble() / coinsPerBdtRate.toDouble()
            earnedPopupText = "+$reelWatchCoins Coins (৳${String.format(java.util.Locale.US, "%.2f", bdtShare)} BDT) Earned!"
            showCoinPopup = true
            episodesWatchedCount++

            // Check if mandatory ad should trigger after every 3 episodes
            if (episodesWatchedCount % 3 == 0) {
                showMandatoryAd = true
                adCountdown = 6
            }
        }
    }

    // Coin Popup Auto-dismiss
    LaunchedEffect(showCoinPopup) {
        if (showCoinPopup) {
            delay(2500)
            showCoinPopup = false
        }
    }

    // Mandatory Ad Countdown effect
    LaunchedEffect(showMandatoryAd) {
        if (showMandatoryAd) {
            while (adCountdown > 0) {
                delay(1000)
                adCountdown--
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .testTag("shorts_screen_container")
    ) {
        // Vertical Pager (TikTok / Reels Style Swipable Feed)
        VerticalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val videoItem = shorts.getOrElse(page) { shorts.first() }

            Box(modifier = Modifier.fillMaxSize()) {
                // Background video frame / thumbnail
                AsyncImage(
                    model = videoItem.thumbnailUrl,
                    contentDescription = videoItem.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Dark gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.5f),
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.9f)
                                )
                            )
                        )
                )
            }
        }

        // ==========================================
        // 1. TOP BAR & WATCH & EARN CIRCULAR TIMER WIDGET
        // ==========================================
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                .align(Alignment.TopCenter)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Reel Brand Pill
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "🎬 NatokFlix Reels",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Floating Circular Progress Timer Widget & Reward Status
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(
                            if (hasEarnedCurrentCycle) Color(0xFF1B4D25) else Color(0xFF2E2211),
                            RoundedCornerShape(24.dp)
                        )
                        .border(1.dp, NatokGold.copy(alpha = 0.6f), RoundedCornerShape(24.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(24.dp)
                    ) {
                        if (!hasEarnedCurrentCycle) {
                            val progress = timerSeconds / 15f
                            CircularProgressIndicator(
                                progress = { progress },
                                modifier = Modifier.size(22.dp),
                                color = NatokGold,
                                trackColor = Color.White.copy(alpha = 0.2f),
                                strokeWidth = 2.5.dp
                            )
                        }
                        Text(
                            text = if (hasEarnedCurrentCycle) "✓" else "${timerSeconds}s",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (hasEarnedCurrentCycle) Color(0xFF76FF03) else NatokGold
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = if (hasEarnedCurrentCycle) "+$reelWatchCoins Coins!" else "Earn in ${timerSeconds}s",
                        color = if (hasEarnedCurrentCycle) Color(0xFF76FF03) else NatokGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Linear Progress Bar
            val progress = (15 - timerSeconds) / 15f
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = NatokGold,
                trackColor = Color.White.copy(alpha = 0.2f),
            )
        }

        // ==========================================
        // 2. FLOATING COIN ANIMATION TOAST ("+🪙 Earned Coins!")
        // ==========================================
        AnimatedVisibility(
            visible = showCoinPopup,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 80.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(Color(0xFF2E1C0A), RoundedCornerShape(30.dp))
                    .border(1.5.dp, NatokGold, RoundedCornerShape(30.dp))
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🪙", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = earnedPopupText,
                        color = NatokGold,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // ==========================================
        // 3. RIGHT ACTION BAR (Like, Wallpaper Set, Share)
        // ==========================================
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Like Button
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = {
                        isLiked = !isLiked
                        if (isLiked) likesCount++ else likesCount--
                    },
                    modifier = Modifier
                        .size(46.dp)
                        .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Like",
                        tint = if (isLiked) NatokRed else Color.White
                    )
                }
                Text(
                    text = "$likesCount",
                    fontSize = 12.sp,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Wallpaper & Theme System Button (Set Wallpaper directly)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = {
                        scope.launch {
                            try {
                                val loader = context.imageLoader
                                val request = ImageRequest.Builder(context)
                                    .data(currentShort.thumbnailUrl)
                                    .allowHardware(false)
                                    .build()
                                val result = loader.execute(request)
                                if (result is SuccessResult) {
                                    val bitmap = result.drawable.toBitmap()
                                    val wallpaperManager = WallpaperManager.getInstance(context)
                                    wallpaperManager.setBitmap(bitmap)
                                    Toast.makeText(context, "🖼️ সফলভাবে ফোন ওয়ালপেপার সেট করা হয়েছে!", Toast.LENGTH_LONG).show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(context, "ওয়ালপেপার সেট করতে সমস্যা: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    modifier = Modifier
                        .size(46.dp)
                        .background(Color(0xFF1565C0), CircleShape)
                        .testTag("set_wallpaper_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Wallpaper,
                        contentDescription = "Set Wallpaper",
                        tint = Color.White
                    )
                }
                Text(
                    text = "Wallpaper",
                    fontSize = 11.sp,
                    color = Color.White
                )
            }

            // Share Button
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = { /* Share */ },
                    modifier = Modifier
                        .size(46.dp)
                        .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = Color.White
                    )
                }
                Text(
                    text = "Share",
                    fontSize = 12.sp,
                    color = Color.White
                )
            }
        }

        // ==========================================
        // 4. BOTTOM DETAILS & EPISODE INFO
        // ==========================================
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth(0.8f)
                .padding(start = 16.dp, bottom = 80.dp)
        ) {
            Text(
                text = currentShort.channelTitle,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = currentShort.title,
                fontSize = 13.sp,
                color = Color(0xFFEEEEEE),
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .background(Color(0xFF261D08), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "🎁 +${currentShort.rewardPoints} Coins Per Episode",
                        color = NatokGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // ==========================================
        // 5. MANDATORY NON-SKIPPABLE AD TRIGGER OVERLAY
        // ==========================================
        if (showMandatoryAd) {
            // Disable back button and touch skip
            BackHandler(enabled = true) {
                // Consume back press during mandatory ad
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.95f))
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        // Consume all touch events to prevent skipping
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .background(NatokRed, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("📺", fontSize = 32.sp)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "ম্যান্ডেটরি স্পনসরড অ্যাড (Mandatory Ad)",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "৩টি শর্টস পর্ব দেখার পর স্পনসরড অ্যাড চলছে। অনুগ্রহ করে অপেক্ষা করুন।",
                        fontSize = 13.sp,
                        color = Color(0xFFB0BEC5),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    if (adCountdown > 0) {
                        Box(
                            modifier = Modifier
                                .background(Color(0xFF1E1E28), RoundedCornerShape(12.dp))
                                .border(1.dp, NatokGold, RoundedCornerShape(12.dp))
                                .padding(horizontal = 24.dp, vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                              text = "বিজ্ঞাপন শেষ হতে বাকি: ${adCountdown}s",
                              fontSize = 15.sp,
                              fontWeight = FontWeight.Bold,
                              color = NatokGold
                            )
                        }
                    } else {
                        Button(
                            onClick = {
                                showMandatoryAd = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NatokRed),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.height(48.dp).fillMaxWidth(0.8f)
                        ) {
                            Text(
                                text = "চালিয়ে যান (Continue Watching)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}
