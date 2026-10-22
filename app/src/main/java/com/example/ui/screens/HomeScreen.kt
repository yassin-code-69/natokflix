package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.User
import com.example.data.model.VideoItem
import com.example.ui.components.AdMobBanner
import com.example.ui.components.GlowingNatokFlixLogo
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.BorderDark
import com.example.ui.theme.NatokGold
import com.example.ui.theme.NatokRed
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun HomeScreen(
    user: User,
    natoks: List<VideoItem>,
    onVideoClick: (VideoItem) -> Unit,
    onCoinsBadgeClick: () -> Unit,
    isDarkTheme: Boolean = true,
    onToggleTheme: () -> Unit = {},
    onPlayCustomUrl: (String) -> Unit = {},
    onSpinTabNavigate: () -> Unit = {},
    announcementNotice: String = "",
    isAnnouncementEnabled: Boolean = false,
    categories: List<String> = listOf("সকল নাটক", "Trending Natok", "Korean & Chinese Dubbed", "Romantic Drama", "Comedy Drama", "Bangla Music & Songs"),
    activeCategory: String = "সকল নাটক",
    onSelectCategory: (String) -> Unit = {},
    isLoadingCategoryFeed: Boolean = false,
    isLoadingMore: Boolean = false,
    onLoadMore: () -> Unit = {},
    featuredPromoTitle: String = "ঈদের বিশেষ নাটক ও ধামাকা শর্টস",
    featuredPromoSubtitle: String = "এখনই দেখুন এবং প্রতিদিন ৩০০+ টাকা আয় করুন",
    searchQuery: String = "",
    searchResults: List<VideoItem> = emptyList(),
    isSearching: Boolean = false,
    onSearchQueryChanged: (String) -> Unit = {},
    onClearSearch: () -> Unit = {},
    bannerUrls: List<String> = emptyList(),
    modifier: Modifier = Modifier
) {
    var customUrlText by remember { mutableStateOf("") }
    var currentBannerIndex by remember { mutableStateOf(0) }

    // Auto-cycle 3D Aura Banners every 3.5 seconds
    val effectiveBanners = remember(bannerUrls, natoks) {
        val list = mutableListOf<VideoItem>()
        if (natoks.isNotEmpty()) {
            list.addAll(natoks.take(15))
        }
        // Fill up to 15 if needed
        while (list.size < 15 && natoks.isNotEmpty()) {
            list.addAll(natoks.take(15 - list.size))
        }
        list
    }

    val filteredNatoks = remember(activeCategory, natoks) {
        if (activeCategory == "সকল নাটক" || activeCategory.isBlank()) {
            natoks
        } else {
            val matches = natoks.filter { it.category.contains(activeCategory, ignoreCase = true) || it.title.contains(activeCategory, ignoreCase = true) }
            if (matches.isNotEmpty()) matches else natoks
        }
    }

    androidx.compose.runtime.LaunchedEffect(effectiveBanners.size) {
        if (effectiveBanners.isNotEmpty()) {
            while (true) {
                kotlinx.coroutines.delay(3500)
                currentBannerIndex = (currentBannerIndex + 1) % effectiveBanners.size
            }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(androidx.compose.material3.MaterialTheme.colorScheme.background)
            .testTag("home_screen_feed"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Top App Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    GlowingNatokFlixLogo(
                        size = 32.dp,
                        showText = true,
                        fontSize = 22.sp,
                        enablePulse = false
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Theme Toggle Button
                    IconButton(
                        onClick = onToggleTheme,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Default.WbSunny else Icons.Default.NightsStay,
                            contentDescription = "Toggle Theme",
                            tint = if (isDarkTheme) NatokGold else Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Balance Badge (clickable to go to Profile)
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFF261E0A))
                            .border(1.dp, NatokGold.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                            .clickable { onCoinsBadgeClick() }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .testTag("coins_header_badge"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "🪙", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${user.points}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = NatokGold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "(৳${String.format("%.1f", user.balanceBdt)})",
                            fontSize = 11.sp,
                            color = Color(0xFFDDDDAA)
                        )
                    }
                }
            }
        }

        // ==========================================
        // 1. AUTOMATED LIVE SEARCH BAR ENGINE
        // ==========================================
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                border = BorderStroke(1.dp, if (searchQuery.isNotBlank()) NatokGold else BorderDark)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Live Search",
                        tint = if (searchQuery.isNotBlank()) NatokGold else TextSecondary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChanged,
                        placeholder = {
                            Text(
                                "যেকোনো নাটক, ড্রামা, শর্টস বা গান লাইভ খুঁজুন...",
                                fontSize = 13.sp,
                                color = TextSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("live_search_bar_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )
                    if (isSearching) {
                        CircularProgressIndicator(
                            color = NatokGold,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(18.dp)
                        )
                    } else if (searchQuery.isNotBlank()) {
                        IconButton(
                            onClick = onClearSearch,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear Search",
                                tint = TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        // ==========================================
        // DYNAMIC CONTENT RENDERING (SEARCH vs HOME)
        // ==========================================
        if (searchQuery.isNotBlank()) {
            // Live Search Results View
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🔎 লাইভ সার্চের ফলাফল: '${searchQuery.trim()}'",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = NatokGold
                    )
                    Text(
                        text = "${searchResults.size} টি কন্টেন্ট",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }

            if (isSearching) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = NatokRed, strokeWidth = 3.dp)
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("ইউটিউব ও গ্লোবাল ড্রামা ফিড থেকে লাইভ সার্চ হচ্ছে...", fontSize = 12.sp, color = NatokGold)
                        }
                    }
                }
            } else if (searchResults.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceCard)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "🎬", fontSize = 36.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "‘${searchQuery.trim()}’ সংক্রান্ত কোনো ভিডিও পাওয়া যায়নি",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "অনুগ্রহ করে নাটকের অন্য কোনো নাম বা কি-ওয়ার্ড লিখে সার্চ করুন।",
                                fontSize = 12.sp,
                                color = TextSecondary,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(searchResults, key = { it.id }) { searchItem ->
                    NatokCardItem(natok = searchItem, onVideoClick = onVideoClick)
                }
            }
        } else {
            // Standard Auto-Populating Trending Feed
            // Global Admin Scrolling Notice Ticker (if enabled)
            if (isAnnouncementEnabled && announcementNotice.isNotBlank()) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(Color(0xFF3E1D06), Color(0xFF261204))
                                )
                            )
                            .border(1.dp, NatokGold.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Campaign,
                            contentDescription = "Notice",
                            tint = NatokGold,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = announcementNotice,
                            fontSize = 11.sp,
                            color = Color(0xFFFFF2D1),
                            fontWeight = FontWeight.Medium,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // Custom YouTube URL Input Bar (Watch & Earn direct streaming)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Link,
                            contentDescription = "Paste Link",
                            tint = NatokGold,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedTextField(
                            value = customUrlText,
                            onValueChange = { customUrlText = it },
                            placeholder = { Text("ইউটিউব লিংক পেস্ট করুন...", fontSize = 12.sp, color = TextSecondary) },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )
                        Button(
                            onClick = {
                                if (customUrlText.isNotBlank()) {
                                    onPlayCustomUrl(customUrlText)
                                    customUrlText = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NatokRed),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text("চালান", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }

            // 3D Aura 15-Banner Animated Carousel
            item {
                if (effectiveBanners.isNotEmpty()) {
                    val currentBanner = effectiveBanners[currentBannerIndex.coerceIn(0, effectiveBanners.size - 1)]

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        // 3D Aura Container Card
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(210.dp)
                                .clickable { onVideoClick(currentBanner) }
                                .testTag("featured_banner_3d_aura"),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF161220)),
                            border = BorderStroke(
                                width = 2.dp,
                                brush = Brush.sweepGradient(
                                    listOf(
                                        NatokGold,
                                        NatokRed,
                                        Color(0xFF00E5FF),
                                        Color(0xFFE040FB),
                                        NatokGold
                                    )
                                )
                            )
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                AsyncImage(
                                    model = currentBanner.thumbnailUrl,
                                    contentDescription = currentBanner.title,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )

                                // Gradient overlay
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            Brush.verticalGradient(
                                                colors = listOf(
                                                    Color.Transparent,
                                                    Color.Black.copy(alpha = 0.4f),
                                                    Color.Black.copy(alpha = 0.92f)
                                                )
                                            )
                                        )
                                )

                                // Overlay Content
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(14.dp),
                                    verticalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .background(NatokRed, RoundedCornerShape(6.dp))
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                                .testTag("special_release_badge")
                                        ) {
                                            Text(
                                                text = "🔥 স্পেশাল ড্রামা #${currentBannerIndex + 1}/15",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        }

                                        Box(
                                            modifier = Modifier
                                                .background(NatokGold, RoundedCornerShape(6.dp))
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                                .testTag("featured_reward_badge")
                                        ) {
                                            Text(
                                                text = "🪙 +${currentBanner.rewardPoints} Coins",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.Black
                                            )
                                        }
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.Bottom,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                                            Text(
                                                text = currentBanner.title,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = Color.White,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = "${currentBanner.channelTitle} • ${currentBanner.views} • ${currentBanner.category}",
                                                fontSize = 11.sp,
                                                color = Color(0xFFDDDDDD),
                                                maxLines = 1
                                            )
                                        }

                                        Box(
                                            modifier = Modifier
                                                .size(42.dp)
                                                .clip(CircleShape)
                                                .background(NatokRed),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.PlayArrow,
                                                contentDescription = "Play Banner Video",
                                                tint = Color.White,
                                                modifier = Modifier.size(26.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // 15-Banner Indicator Dots
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            effectiveBanners.indices.take(15).forEach { idx ->
                                val isSelected = idx == currentBannerIndex
                                Box(
                                    modifier = Modifier
                                        .padding(horizontal = 2.dp)
                                        .size(if (isSelected) 8.dp else 4.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) NatokGold else Color.Gray.copy(alpha = 0.5f))
                                        .clickable { currentBannerIndex = idx }
                                )
                            }
                        }
                    }
                }
            }

            // Category Filter Chips
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { category ->
                        val isSelected = activeCategory == category
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) NatokRed else SurfaceCard)
                                .clickable { onSelectCategory(category) }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                                .testTag("category_chip_$category")
                        ) {
                            Text(
                                text = category,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else TextSecondary
                            )
                        }
                    }
                }
            }

            // Category Loading Spinner Indicator
            if (isLoadingCategoryFeed) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = NatokRed, strokeWidth = 3.dp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("ইউটিউব ডাটা ফেচিং হচ্ছে...", fontSize = 12.sp, color = NatokGold)
                        }
                    }
                }
            }

            // AdMob Banner
            item {
                AdMobBanner(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }

            // Section Title
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "নতুন ও জনপ্রিয় নাটক সমূহ",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "${filteredNatoks.size} টি নাটক",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }

            // Natok Cards List
            items(filteredNatoks, key = { it.id }) { natok ->
                NatokCardItem(natok = natok, onVideoClick = onVideoClick)
            }

            // Pagination / Infinite Scroll Load More Button
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = onLoadMore,
                        enabled = !isLoadingMore && !isLoadingCategoryFeed,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SurfaceCard,
                            contentColor = NatokGold
                        ),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, NatokGold.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("load_more_button")
                    ) {
                        if (isLoadingMore) {
                            CircularProgressIndicator(color = NatokGold, strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("ইউটিউব ডাটা লোড হচ্ছে...", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        } else {
                            Text("আরো নাটক দেখুন (Load More Videos)", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NatokCardItem(
    natok: VideoItem,
    onVideoClick: (VideoItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onVideoClick(natok) }
            .testTag("natok_card_${natok.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard)
    ) {
        Column {
            // Thumbnail with badge
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                AsyncImage(
                    model = natok.thumbnailUrl,
                    contentDescription = natok.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Duration Badge
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .background(Color.Black.copy(alpha = 0.8f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = natok.duration,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // Reward Points Badge
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .background(Color(0xFF261D08), RoundedCornerShape(6.dp))
                        .border(1.dp, NatokGold, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "🪙 +${natok.rewardPoints} Coins (৳${String.format(java.util.Locale.US, "%.1f", natok.rewardPoints / 10.0)})",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = NatokGold
                    )
                }

                // Center Play Button
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.6f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }

            // Details
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = natok.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = natok.channelTitle,
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Verified Channel",
                            tint = Color(0xFF64B5F6),
                            modifier = Modifier.size(13.dp)
                        )
                    }

                    Text(
                        text = natok.views,
                        fontSize = 12.sp,
                        color = Color(0xFFAAAAAA)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(Color(0xFF161622), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "অভিনয়ে: ${natok.cast}",
                        fontSize = 11.sp,
                        color = Color(0xFFB0B0C4)
                    )
                }
            }
        }
    }
}
