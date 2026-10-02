package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Task
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.User
import com.example.data.model.VideoItem
import com.example.data.model.Withdrawal
import com.example.ui.components.GlowingNatokFlixLogo
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.BkashPink
import com.example.ui.theme.BorderDark
import com.example.ui.theme.NagadOrange
import com.example.ui.theme.NatokGold
import com.example.ui.theme.NatokRed
import com.example.ui.theme.NatokRedDark
import com.example.ui.theme.RocketPurple
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun AdminPanelScreen(
    users: List<User>,
    natoks: List<VideoItem>,
    withdrawals: List<Withdrawal>,
    adsMasterEnabled: Boolean,
    preRollAdEnabled: Boolean,
    pointsPer30s: Int,
    preRollBonusCoins: Int,
    bannerAdId: String,
    rewardedAdId: String,
    // 20+ Extended Controls State
    announcementNotice: String,
    isAnnouncementEnabled: Boolean,
    categoriesList: List<String>,
    featuredPromoTitle: String,
    featuredPromoSubtitle: String,
    minWithdrawalBdt: Double,
    coinsPerBdtRate: Int,
    rewardedVideoAdCoins: Int = 60,
    interstitialAdCoins: Int = 30,
    reelWatchCoins: Int = 10,
    dailyCheckInBonus: Int,
    referralBonusCoins: Int,
    refereeBonusCoins: Int = 100,
    shortsRewardCoins: Int,
    isBkashEnabled: Boolean,
    isNagadEnabled: Boolean,
    isRocketEnabled: Boolean,
    telegramChannelUrl: String,
    whatsappSupportNumber: String,
    withdrawalRulesText: String,
    isMaintenanceMode: Boolean,
    maintenanceMessage: String,
    freeDailySpinsLimit: Int,
    adminCustomUsername: String = "ADMIN_NATOKFLIX",
    adminCustomPhone: String = "01788123456",
    adminCustomPassword: String = "ADMIN9900",
    // Social & Popup States
    telegramTaskUrl: String = "https://t.me/natokflix_official",
    youtubeTaskUrl: String = "https://youtube.com/@natokflix_official",
    facebookTaskUrl: String = "https://facebook.com/natokflix.official",
    socialTaskRewardCoins: Int = 500,
    isPopupNoticeEnabled: Boolean = true,
    popupNoticeTitle: String = "🔥 বিশেষ অফার ও ধামাকা নোটিশ!",
    popupNoticeMessage: String = "📢 NatokFlix-এ স্বাগতম! আজ প্রতিটি নাটক দেখলে ডাবল কয়েন রিওয়ার্ড পাবেন এবং যেকোনো সময় বিকাশ/নগদে ইনস্ট্যান্ট ক্যাশআউট করতে পারবেন। সোশ্যাল টাস্ক সম্পন্ন করে এখনই নিন ১৫০০ ফ্রি কয়েন!",
    popupNoticeBtnText: String = "বুঝেছি / শুরু করুন (Got It)",
    // Callbacks
    onAdjustUserBalance: (userId: String, coinsDelta: Int, bdtDelta: Double) -> Unit,
    onAddNatok: (VideoItem) -> Unit,
    onDeleteNatok: (String) -> Unit,
    onApproveWithdrawal: (String) -> Unit,
    onRejectWithdrawal: (String) -> Unit,
    onUpdateMonetizationSettings: (Boolean, Boolean, Int, Int, String, String) -> Unit,
    onUpdateAnnouncement: (notice: String, enabled: Boolean) -> Unit,
    onUpdateEconomySettings: (minWithdraw: Double, rate: Int, checkIn: Int, referrerBonus: Int, refereeBonus: Int, shortsReward: Int, freeSpins: Int, rewardedVideoCoins: Int, interstitialCoins: Int, reelCoins: Int) -> Unit,
    onUpdateGateways: (bkash: Boolean, nagad: Boolean, rocket: Boolean) -> Unit,
    onUpdateSupportAndRules: (telegram: String, whatsapp: String, rules: String) -> Unit,
    onUpdatePromoBanner: (title: String, subtitle: String) -> Unit,
    onToggleMaintenance: (enabled: Boolean, message: String) -> Unit,
    onAddCategory: (String) -> Unit,
    onDeleteCategory: (String) -> Unit,
    onAddShort: (VideoItem) -> Unit,
    onAddNewTask: (title: String, reward: Int, target: Int) -> Unit,
    onBroadcastBonus: (coins: Int, reason: String) -> Unit,
    onToggleUserBan: (userId: String) -> Unit,
    onUpdateAdminCredentials: (newUsername: String, newPhone: String, newPassword: String) -> Unit = { _, _, _ -> },
    onFullSystemRefresh: () -> Unit = {},
    onSendPushNotification: (title: String, message: String) -> Unit = { _, _ -> },
    onUpdateSocialTaskSettings: (telegram: String, youtube: String, facebook: String, reward: Int) -> Unit = { _, _, _, _ -> },
    onUpdatePopupNoticeSettings: (enabled: Boolean, title: String, message: String, btnText: String) -> Unit = { _, _, _, _ -> },
    onExitAdmin: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler {
        onExitAdmin()
    }

    var selectedAdminTab by remember { mutableIntStateOf(0) }
    val adminTabs = listOf(
        "ড্যাশবোর্ড",
        "ইউজার কন্ট্রোল (${users.size})",
        "কন্টেন্ট ও ক্যাটাগরি (${natoks.size})",
        "উত্তোলন ও গেটওয়ে",
        "রিওয়ার্ড ও ইকোনমি",
        "নোটিশ, পুশ ও সোশ্যাল",
        "বিজ্ঞাপন সেটিংস",
        "অ্যাডমিন এক্সেস ও রিফ্রেশ"
    )

    // Push Notification Form State
    var pushTitleText by remember { mutableStateOf("🎬 আজকের নতুন বিশেষ নাটক রিলিজ!") }
    var pushMessageText by remember { mutableStateOf("এখনই দেখুন এবং উপভোগ করুন ডাবল কয়েন ইনকাম অফার!") }

    // Popup Notice Form State
    var localPopupEnabled by remember(isPopupNoticeEnabled) { mutableStateOf(isPopupNoticeEnabled) }
    var localPopupTitle by remember(popupNoticeTitle) { mutableStateOf(popupNoticeTitle) }
    var localPopupMessage by remember(popupNoticeMessage) { mutableStateOf(popupNoticeMessage) }
    var localPopupBtnText by remember(popupNoticeBtnText) { mutableStateOf(popupNoticeBtnText) }

    // Social Task Form State
    var localSocialTg by remember(telegramTaskUrl) { mutableStateOf(telegramTaskUrl) }
    var localSocialYt by remember(youtubeTaskUrl) { mutableStateOf(youtubeTaskUrl) }
    var localSocialFb by remember(facebookTaskUrl) { mutableStateOf(facebookTaskUrl) }
    var localSocialReward by remember(socialTaskRewardCoins) { mutableStateOf(socialTaskRewardCoins.toString()) }

    // Admin Credentials State
    var localAdminUsername by remember(adminCustomUsername) { mutableStateOf(adminCustomUsername) }
    var localAdminPhone by remember(adminCustomPhone) { mutableStateOf(adminCustomPhone) }
    var localAdminPass by remember(adminCustomPassword) { mutableStateOf(adminCustomPassword) }
    var showFullResetConfirmDialog by remember { mutableStateOf(false) }

    // User balance adjustment dialog state
    var selectedUserForBalance by remember { mutableStateOf<User?>(null) }
    var adjustCoinsInput by remember { mutableStateOf("500") }
    var adjustBdtInput by remember { mutableStateOf("5.0") }
    var isAddingBalance by remember { mutableStateOf(true) }

    // Broadcast Bonus Dialog
    var showBroadcastDialog by remember { mutableStateOf(false) }
    var broadcastCoinsText by remember { mutableStateOf("100") }
    var broadcastReasonText by remember { mutableStateOf("ঈদের ধামাকা বোনাস") }

    // Maintenance Dialog
    var showMaintenanceDialog by remember { mutableStateOf(false) }
    var maintenanceMsgText by remember(maintenanceMessage) { mutableStateOf(maintenanceMessage) }
    var maintenanceActiveState by remember(isMaintenanceMode) { mutableStateOf(isMaintenanceMode) }

    // Content: Add Natok Dialog
    var showAddNatokDialog by remember { mutableStateOf(false) }
    var newNatokTitle by remember { mutableStateOf("") }
    var newNatokUrlOrId by remember { mutableStateOf("") }
    var newNatokChannel by remember { mutableStateOf("NatokFlix Originals") }
    var newNatokCategory by remember { mutableStateOf("Trending") }
    var newNatokDuration by remember { mutableStateOf("40:00") }
    var newNatokCast by remember { mutableStateOf("Bangla All-Stars") }
    var newNatokReward by remember { mutableStateOf("200") }

    // Content: Add Short Dialog
    var showAddShortDialog by remember { mutableStateOf(false) }
    var newShortTitle by remember { mutableStateOf("") }
    var newShortUrl by remember { mutableStateOf("") }
    var newShortCreator by remember { mutableStateOf("NatokFlix Shorts") }

    // Category Dialog
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var newCategoryNameText by remember { mutableStateOf("") }

    // Task Dialog
    var showAddTaskDialog by remember { mutableStateOf(false) }
    var newTaskTitle by remember { mutableStateOf("") }
    var newTaskReward by remember { mutableStateOf("100") }
    var newTaskTarget by remember { mutableStateOf("3") }

    // Local form states for Notice & Support
    var localNoticeText by remember(announcementNotice) { mutableStateOf(announcementNotice) }
    var localNoticeEnabled by remember(isAnnouncementEnabled) { mutableStateOf(isAnnouncementEnabled) }
    var localTelegramUrl by remember(telegramChannelUrl) { mutableStateOf(telegramChannelUrl) }
    var localWhatsappNum by remember(whatsappSupportNumber) { mutableStateOf(whatsappSupportNumber) }
    var localRulesText by remember(withdrawalRulesText) { mutableStateOf(withdrawalRulesText) }

    // Local form states for Economy
    var localMinWithdraw by remember(minWithdrawalBdt) { mutableStateOf(minWithdrawalBdt.toString()) }
    var localRate by remember(coinsPerBdtRate) { mutableStateOf(coinsPerBdtRate.toString()) }
    var localCheckIn by remember(dailyCheckInBonus) { mutableStateOf(dailyCheckInBonus.toString()) }
    var localReferral by remember(referralBonusCoins) { mutableStateOf(referralBonusCoins.toString()) }
    var localRefereeReferral by remember(refereeBonusCoins) { mutableStateOf(refereeBonusCoins.toString()) }
    var localShortsReward by remember(shortsRewardCoins) { mutableStateOf(shortsRewardCoins.toString()) }
    var localFreeSpins by remember(freeDailySpinsLimit) { mutableStateOf(freeDailySpinsLimit.toString()) }
    var localRewardedVideoCoins by remember(rewardedVideoAdCoins) { mutableStateOf(rewardedVideoAdCoins.toString()) }
    var localInterstitialCoins by remember(interstitialAdCoins) { mutableStateOf(interstitialAdCoins.toString()) }
    var localReelCoins by remember(reelWatchCoins) { mutableStateOf(reelWatchCoins.toString()) }

    // Local form states for Gateways
    var localBkash by remember(isBkashEnabled) { mutableStateOf(isBkashEnabled) }
    var localNagad by remember(isNagadEnabled) { mutableStateOf(isNagadEnabled) }
    var localRocket by remember(isRocketEnabled) { mutableStateOf(isRocketEnabled) }

    // Local Promo Banner
    var localPromoTitle by remember(featuredPromoTitle) { mutableStateOf(featuredPromoTitle) }
    var localPromoSub by remember(featuredPromoSubtitle) { mutableStateOf(featuredPromoSubtitle) }

    // Local Ads Settings
    var localAdsEnabled by remember(adsMasterEnabled) { mutableStateOf(adsMasterEnabled) }
    var localPreRollEnabled by remember(preRollAdEnabled) { mutableStateOf(preRollAdEnabled) }
    var localPointsPer30s by remember(pointsPer30s) { mutableStateOf(pointsPer30s.toString()) }
    var localPreRollBonus by remember(preRollBonusCoins) { mutableStateOf(preRollBonusCoins.toString()) }
    var localBannerId by remember(bannerAdId) { mutableStateOf(bannerAdId) }
    var localRewardedId by remember(rewardedAdId) { mutableStateOf(rewardedAdId) }

    val pendingCount = withdrawals.count { it.status == "Pending" }
    val totalPaidOut = withdrawals.filter { it.status == "Approved" }.sumOf { it.amountBdt }
    val totalCoinsInSystem = users.sumOf { it.points }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .testTag("admin_panel_screen")
    ) {
        // Master Admin Header Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1E0E12))
                .border(1.dp, NatokRed.copy(alpha = 0.4f))
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                GlowingNatokFlixLogo(size = 36.dp, showText = false, enablePulse = false)
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "NatokFlix Master Control",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(NatokGold)
                                .padding(horizontal = 5.dp, vertical = 1.dp)
                        ) {
                            Text("ROOT 20+", fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
                        }
                    }
                    Text(
                        text = "ADMIN_NATOKFLIX • ২০+ পূর্ণাঙ্গ কন্ট্রোল প্যানেল সক্রিয়",
                        fontSize = 11.sp,
                        color = Color(0xFFDDDDAA)
                    )
                }
            }

            // Exit Admin Mode Button
            Button(
                onClick = onExitAdmin,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF333342)),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                modifier = Modifier.height(32.dp).testTag("exit_admin_btn")
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(13.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("ইউজার অ্যাপ", fontSize = 11.sp)
            }
        }

        // Admin Navigation Scrollable Tabs
        ScrollableTabRow(
            selectedTabIndex = selectedAdminTab,
            containerColor = SurfaceCard,
            contentColor = Color.White,
            edgePadding = 12.dp,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedAdminTab]),
                    color = NatokRed,
                    height = 3.dp
                )
            }
        ) {
            adminTabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedAdminTab == index,
                    onClick = { selectedAdminTab = index },
                    text = {
                        Text(
                            text = title,
                            fontSize = 12.sp,
                            fontWeight = if (selectedAdminTab == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedAdminTab == index) Color.White else TextSecondary
                        )
                    }
                )
            }
        }

        // Tab Body Content
        AnimatedContent(
            targetState = selectedAdminTab,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "admin_tab_content",
            modifier = Modifier.weight(1f)
        ) { tabIndex ->
            when (tabIndex) {
                // ==========================================
                // 1. DASHBOARD OVERVIEW & QUICK ACTIONS
                // ==========================================
                0 -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            Text(
                                text = "মাস্টার ড্যাশবোর্ড ও কুইক কন্ট্রোলস (Overview)",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        // Stats Grid (Row 1)
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Card(
                                    modifier = Modifier.weight(1f),
                                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Icon(Icons.Default.People, contentDescription = null, tint = Color(0xFF64B5F6), modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(text = "নিবন্ধিত ইউজার", fontSize = 11.sp, color = TextSecondary)
                                        Text(text = "${users.size} জন", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                        Text(text = "কয়েন: $totalCoinsInSystem", fontSize = 10.sp, color = NatokGold)
                                    }
                                }

                                Card(
                                    modifier = Modifier.weight(1f),
                                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Icon(Icons.Default.Movie, contentDescription = null, tint = NatokRed, modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(text = "লাইভ নাটক", fontSize = 11.sp, color = TextSecondary)
                                        Text(text = "${natoks.size} টি", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                        Text(text = "ক্যাটাগরি: ${categoriesList.size}টি", fontSize = 10.sp, color = Color(0xFF81C784))
                                    }
                                }
                            }
                        }

                        // Stats Grid (Row 2)
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Card(
                                    modifier = Modifier.weight(1f),
                                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Icon(Icons.Default.Paid, contentDescription = null, tint = NatokGold, modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(text = "পেন্ডিং উত্তোলন", fontSize = 11.sp, color = TextSecondary)
                                        Text(text = "$pendingCount টি", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = NatokGold)
                                        Text(text = "পরিশোধের অপেক্ষায়", fontSize = 10.sp, color = Color(0xFFDDDDAA))
                                    }
                                }

                                Card(
                                    modifier = Modifier.weight(1f),
                                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Icon(Icons.Default.AttachMoney, contentDescription = null, tint = Color(0xFF69F0AE), modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(text = "মোট পরিশোধিত", fontSize = 11.sp, color = TextSecondary)
                                        Text(text = "৳ ${totalPaidOut.toInt()}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF69F0AE))
                                        Text(text = "বিকাশ ও নগদ", fontSize = 10.sp, color = Color(0xFF888899))
                                    }
                                }
                            }
                        }

                        // Quick Actions & Emergency Broadcasts
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(text = "জরুরি সিস্টেম অ্যাকশন (Emergency Controls)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    Spacer(modifier = Modifier.height(10.dp))

                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                        Button(
                                            onClick = { showBroadcastDialog = true },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Icon(Icons.Default.NotificationsActive, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("সবাইকে বোনাস পুশ", fontSize = 11.sp)
                                        }

                                        Button(
                                            onClick = { showMaintenanceDialog = true },
                                            colors = ButtonDefaults.buttonColors(containerColor = if (isMaintenanceMode) Color(0xFFC62828) else Color(0xFFE65100)),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Icon(Icons.Default.Warning, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(if (isMaintenanceMode) "রক্ষণাবেক্ষণ চালু" else "রক্ষণাবেক্ষণ মোড", fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // ==========================================
                // 2. USER MANAGEMENT & BAN/UNBAN
                // ==========================================
                1 -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "ইউজার তালিকা (${users.size})", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Button(
                                    onClick = { showBroadcastDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier.height(32.dp)
                                ) {
                                    Text("এক ক্লিকে সবাইকে কয়েন দিন", fontSize = 11.sp)
                                }
                            }
                        }

                        items(users, key = { it.id }) { userItem ->
                            val isBanned = userItem.role == "BANNED"
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = if (isBanned) Color(0xFF2B1214) else SurfaceCard),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier.size(40.dp).clip(CircleShape).background(if (isBanned) Color.Red else NatokRedDark),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(userItem.name.take(1), fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
                                            }
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text(userItem.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                                    if (isBanned) {
                                                        Spacer(modifier = Modifier.width(6.dp))
                                                        Box(modifier = Modifier.background(Color.Red, RoundedCornerShape(4.dp)).padding(horizontal = 4.dp, vertical = 1.dp)) {
                                                            Text("BANNED", fontSize = 9.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                                        }
                                                    }
                                                }
                                                Text(userItem.email, fontSize = 11.sp, color = TextSecondary)
                                                Text("ফোন: ${userItem.phone}", fontSize = 11.sp, color = Color(0xFFB0B0C4))
                                            }
                                        }

                                        Column(horizontalAlignment = Alignment.End) {
                                            Text("🪙 ${userItem.points} pts", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = NatokGold)
                                            Text("৳ ${String.format("%.2f", userItem.balanceBdt)}", fontSize = 13.sp, color = Color.White)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Button(
                                            onClick = {
                                                selectedUserForBalance = userItem
                                                isAddingBalance = true
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20)),
                                            shape = RoundedCornerShape(6.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                            modifier = Modifier.height(30.dp)
                                        ) {
                                            Text("+ কয়েন", fontSize = 11.sp)
                                        }

                                        Spacer(modifier = Modifier.width(6.dp))

                                        Button(
                                            onClick = {
                                                selectedUserForBalance = userItem
                                                isAddingBalance = false
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C)),
                                            shape = RoundedCornerShape(6.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                            modifier = Modifier.height(30.dp)
                                        ) {
                                            Text("- কর্তন", fontSize = 11.sp)
                                        }

                                        Spacer(modifier = Modifier.width(6.dp))

                                        Button(
                                            onClick = { onToggleUserBan(userItem.id) },
                                            colors = ButtonDefaults.buttonColors(containerColor = if (isBanned) Color(0xFF2E7D32) else Color(0xFF424242)),
                                            shape = RoundedCornerShape(6.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                            modifier = Modifier.height(30.dp)
                                        ) {
                                            Text(if (isBanned) "আনব্যান" else "ব্যান", fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // ==========================================
                // 3. CONTENT, SHORTS & CATEGORY MANAGEMENT
                // ==========================================
                2 -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Quick Action Buttons
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { showAddNatokDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = NatokRed),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f).height(38.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("নাটক যুক্ত", fontSize = 11.sp)
                                }

                                Button(
                                    onClick = { showAddShortDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A1B9A)),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f).height(38.dp)
                                ) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("শর্টস যুক্ত", fontSize = 11.sp)
                                }

                                Button(
                                    onClick = { showAddCategoryDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00695C)),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f).height(38.dp)
                                ) {
                                    Text("ক্যাটাগরি", fontSize = 11.sp)
                                }
                            }
                        }

                        // Promo Banner Text Editor
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text("হোম ফিড প্রোমো ব্যানার এডিটর", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    Spacer(modifier = Modifier.height(10.dp))

                                    OutlinedTextField(
                                        value = localPromoTitle,
                                        onValueChange = { localPromoTitle = it },
                                        label = { Text("ব্যানার টাইটেল") },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    OutlinedTextField(
                                        value = localPromoSub,
                                        onValueChange = { localPromoSub = it },
                                        label = { Text("ব্যানার সাব-টাইটেল") },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Button(
                                        onClick = { onUpdatePromoBanner(localPromoTitle, localPromoSub) },
                                        colors = ButtonDefaults.buttonColors(containerColor = NatokRed),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth().height(36.dp)
                                    ) {
                                        Text("ব্যানার আপডেট করুন", fontSize = 12.sp)
                                    }
                                }
                            }
                        }

                        // Categories List Chips
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text("হোম ফিড ক্যাটাগরি ফিল্টার্স (${categoriesList.size}টি)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        categoriesList.forEach { cat ->
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(16.dp))
                                                    .background(Color(0xFF262634))
                                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text(cat, fontSize = 11.sp, color = Color.White)
                                                    if (cat != "সকল নাটক") {
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Icon(
                                                            imageVector = Icons.Default.Close,
                                                            contentDescription = "Delete",
                                                            tint = Color.Red,
                                                            modifier = Modifier.size(12.dp).clickable { onDeleteCategory(cat) }
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Natok List
                        item {
                            Text("লাইভ নাটক তালিকা (${natoks.size})", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        items(natoks, key = { it.id }) { natokItem ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = natokItem.title,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = "ID: ${natokItem.youtubeVideoId} • ${natokItem.category} • 🪙 ${natokItem.rewardPoints} Coins (৳${String.format(java.util.Locale.US, "%.1f", natokItem.rewardPoints / 10.0)})",
                                            fontSize = 11.sp,
                                            color = NatokGold
                                        )
                                    }

                                    IconButton(
                                        onClick = { onDeleteNatok(natokItem.id) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFFF5252), modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                // ==========================================
                // 4. WITHDRAWAL & GATEWAYS TAB
                // ==========================================
                3 -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Payment Gateways Master Toggles
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text("পেমেন্ট গেটওয়ে কন্ট্রোল (Payment Methods)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    Spacer(modifier = Modifier.height(10.dp))

                                    // bKash Toggle
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                        Text("বিকাশ (bKash) উত্তোলন", fontSize = 13.sp, color = BkashPink, fontWeight = FontWeight.Bold)
                                        Switch(checked = localBkash, onCheckedChange = {
                                            localBkash = it
                                            onUpdateGateways(localBkash, localNagad, localRocket)
                                        })
                                    }

                                    // Nagad Toggle
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                        Text("নগদ (Nagad) উত্তোলন", fontSize = 13.sp, color = NagadOrange, fontWeight = FontWeight.Bold)
                                        Switch(checked = localNagad, onCheckedChange = {
                                            localNagad = it
                                            onUpdateGateways(localBkash, localNagad, localRocket)
                                        })
                                    }

                                    // Rocket Toggle
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                        Text("রকেট (Rocket) উত্তোলন", fontSize = 13.sp, color = RocketPurple, fontWeight = FontWeight.Bold)
                                        Switch(checked = localRocket, onCheckedChange = {
                                            localRocket = it
                                            onUpdateGateways(localBkash, localNagad, localRocket)
                                        })
                                    }
                                }
                            }
                        }

                        // Withdrawal Requests List
                        item {
                            Text("উত্তোলন অনুরোধসমূহ (${withdrawals.size})", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        if (withdrawals.isEmpty()) {
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Box(modifier = Modifier.fillMaxWidth().padding(20.dp), contentAlignment = Alignment.Center) {
                                        Text("কোনো উত্তোলন অনুরোধ এখনো নেই।", color = TextSecondary, fontSize = 13.sp)
                                    }
                                }
                            }
                        } else {
                            items(withdrawals, key = { it.id }) { item ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(
                                                    modifier = Modifier.size(32.dp).clip(RoundedCornerShape(6.dp)).background(
                                                        when (item.method) {
                                                            "bKash" -> BkashPink
                                                            "Nagad" -> NagadOrange
                                                            else -> RocketPurple
                                                        }
                                                    ),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(item.method.take(1), fontWeight = FontWeight.Bold, color = Color.White)
                                                }
                                                Spacer(modifier = Modifier.width(10.dp))
                                                Column {
                                                    Text("${item.method}: ${item.accountNumber}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                                    Text(item.formattedDate, fontSize = 11.sp, color = TextSecondary)
                                                }
                                            }

                                            Column(horizontalAlignment = Alignment.End) {
                                                Text("৳ ${String.format("%.2f", item.amountBdt)}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = NatokGold)
                                                Text("-${item.pointsDeducted} pts", fontSize = 10.sp, color = TextSecondary)
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(10.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "স্ট্যাটাস: ${item.status}",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (item.status == "Approved") Color(0xFF69F0AE) else NatokGold
                                            )

                                            if (item.status == "Pending") {
                                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                    Button(
                                                        onClick = { onApproveWithdrawal(item.id) },
                                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20)),
                                                        shape = RoundedCornerShape(6.dp),
                                                        modifier = Modifier.height(30.dp)
                                                    ) {
                                                        Text("অনুমোদন", fontSize = 11.sp)
                                                    }

                                                    Button(
                                                        onClick = { onRejectWithdrawal(item.id) },
                                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C)),
                                                        shape = RoundedCornerShape(6.dp),
                                                        modifier = Modifier.height(30.dp)
                                                    ) {
                                                        Text("বাতিল", fontSize = 11.sp)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // ==========================================
                // 5. ECONOMY & REWARDS CONFIGURATION
                // ==========================================
                4 -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Text("অর্থনীতি ও পয়েন্ট কনভার্সন রেট (Economy Engine)", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    OutlinedTextField(
                                        value = localMinWithdraw,
                                        onValueChange = { localMinWithdraw = it },
                                        label = { Text("সর্বনিম্ন উত্তোলন লিমিট (BDT ৳) [যেমন: 300]") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    OutlinedTextField(
                                        value = localRate,
                                        onValueChange = { localRate = it },
                                        label = { Text("কয়েন টু টাকা রূপান্তর হার (Coins Per 1 BDT Rate) [যেমন: 10]") },
                                        supportingText = {
                                            val r = localRate.toIntOrNull()?.takeIf { it > 0 } ?: 10
                                            Text("বর্তমানে $r কয়েনে ১ টাকা ধার্য করা আছে।", color = NatokGold)
                                        },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )

                                    Spacer(modifier = Modifier.height(6.dp))

                                    // Real-Time Dynamic Conversion Calculator Preview Card
                                    val parsedRate = localRate.toIntOrNull()?.takeIf { it > 0 } ?: 10
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Color(0xFF1E182A), RoundedCornerShape(8.dp))
                                            .border(1.dp, Color(0xFF4A386D), RoundedCornerShape(8.dp))
                                            .padding(10.dp)
                                    ) {
                                        Column {
                                            Text("📊 লাইভ কনভার্সন প্রিভিউ (ইউজাররা এই রেট দেখতে পাবে):", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text("• 🪙 $parsedRate কয়েন = ৳ ১.০০ টাকা", fontSize = 11.sp, color = NatokGold)
                                            Text("• 🪙 ১০০ কয়েন = ৳ ${String.format(java.util.Locale.US, "%.2f", 100.0 / parsedRate)} টাকা", fontSize = 11.sp, color = Color(0xFFDDDDAA))
                                            Text("• 🪙 ১,০০০ কয়েন = ৳ ${String.format(java.util.Locale.US, "%.2f", 1000.0 / parsedRate)} টাকা", fontSize = 11.sp, color = Color(0xFFDDDDAA))
                                            Text("• 🪙 ${(parsedRate * (localMinWithdraw.toDoubleOrNull() ?: 300.0)).toInt()} কয়েন = ৳ ${(localMinWithdraw.toDoubleOrNull() ?: 300.0).toInt()} টাকা (সর্বনিম্ন উইথড্র)", fontSize = 11.sp, color = Color(0xFF69F0AE))
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    OutlinedTextField(
                                        value = localCheckIn,
                                        onValueChange = { localCheckIn = it },
                                        label = { Text("ডেইলি চেক-ইন বোনাস কয়েন") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    OutlinedTextField(
                                        value = localReferral,
                                        onValueChange = { localReferral = it },
                                        label = { Text("রেফারার বোনাস কয়েন (যে রেফার কোড শেয়ার করবে)") },
                                        supportingText = {
                                            val refCoins = localReferral.toIntOrNull() ?: 200
                                            val refBdt = refCoins / parsedRate.toDouble()
                                            Text("রেফারার পাবে: 🪙 $refCoins Coins (৳${String.format(java.util.Locale.US, "%.1f", refBdt)} টাকা)", color = NatokGold)
                                        },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    OutlinedTextField(
                                        value = localRefereeReferral,
                                        onValueChange = { localRefereeReferral = it },
                                        label = { Text("রেফারেল গ্রহণকারী বোনাস কয়েন (নতুন জয়েন করা বন্ধু)") },
                                        supportingText = {
                                            val refCoins = localRefereeReferral.toIntOrNull() ?: 100
                                            val refBdt = refCoins / parsedRate.toDouble()
                                            Text("নতুন বন্ধু পাবে: 🪙 $refCoins Coins (৳${String.format(java.util.Locale.US, "%.1f", refBdt)} টাকা)", color = Color(0xFF69F0AE))
                                        },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    OutlinedTextField(
                                        value = localShortsReward,
                                        onValueChange = { localShortsReward = it },
                                        label = { Text("শর্টস ভিডিও দেখার কয়েন") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    OutlinedTextField(
                                        value = localFreeSpins,
                                        onValueChange = { localFreeSpins = it },
                                        label = { Text("দৈনিক ফ্রি স্পিন সংখ্যা (Free Spins/Day)") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )

                                    Spacer(modifier = Modifier.height(14.dp))
                                    Text(
                                        text = "⚡ AdMob eCPM 50/50 রেভিনিউ শেয়ার রিওয়ার্ড রেট:",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NatokGold
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))

                                    OutlinedTextField(
                                        value = localRewardedVideoCoins,
                                        onValueChange = { localRewardedVideoCoins = it },
                                        label = { Text("রিওয়ার্ডেড ভিডিও অ্যাড (৩০ সে) কয়েন") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    OutlinedTextField(
                                        value = localInterstitialCoins,
                                        onValueChange = { localInterstitialCoins = it },
                                        label = { Text("ইন্টারস্টিশিয়াল অ্যাড কয়েন") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    OutlinedTextField(
                                        value = localReelCoins,
                                        onValueChange = { localReelCoins = it },
                                        label = { Text("রিল ওয়াচ টাইমার (প্রতি ১৫ সে) কয়েন") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )

                                    Spacer(modifier = Modifier.height(14.dp))

                                    Button(
                                        onClick = {
                                            val minW = localMinWithdraw.toDoubleOrNull() ?: 300.0
                                            val r = localRate.toIntOrNull() ?: 10
                                            val c = localCheckIn.toIntOrNull() ?: 20
                                            val ref = localReferral.toIntOrNull() ?: 200
                                            val refReferee = localRefereeReferral.toIntOrNull() ?: 100
                                            val sh = localShortsReward.toIntOrNull() ?: 5
                                            val sp = localFreeSpins.toIntOrNull() ?: 1
                                            val rew = localRewardedVideoCoins.toIntOrNull() ?: 10
                                            val inter = localInterstitialCoins.toIntOrNull() ?: 5
                                            val reel = localReelCoins.toIntOrNull() ?: 2
                                            onUpdateEconomySettings(minW, r, c, ref, refReferee, sh, sp, rew, inter, reel)
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = NatokRed),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth().height(44.dp)
                                    ) {
                                        Text("অর্থনীতি সেটিংস সেভ ও ফায়ারবেজ লাইভ সিঙ্ক", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        // Add Custom Task Trigger
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text("ইউজার টাস্ক সেন্টার কন্ট্রোল", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Button(
                                        onClick = { showAddTaskDialog = true },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0)),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth().height(40.dp)
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = null)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("নতুন আর্নিং টাস্ক তৈরি করুন", fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }

                // ==========================================
                // 6. NOTICE, PUSH NOTIFICATIONS & SOCIAL TASKS
                // ==========================================
                5 -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            Text("নোটিশ, পুশ নোটিফিকেশন ও সোশ্যাল টাস্ক সেটিংস", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        // 1. Send External Push Notification Directly to Phone Bar
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2638)),
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF42A5F5).copy(alpha = 0.5f))
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("🔔", fontSize = 18.sp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "মোবাইল বারে পুশ নোটিফিকেশন পাঠান (Push Notification)",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                    Text(
                                        text = "ইউজারদের ফোনের নোটিফিকেশন ট্রে-তে তাৎক্ষণিক রিয়েল পুশ নোটিফিকেশন পৌঁছাবে।",
                                        fontSize = 11.sp,
                                        color = Color(0xFFB0BEC5)
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))

                                    OutlinedTextField(
                                        value = pushTitleText,
                                        onValueChange = { pushTitleText = it },
                                        label = { Text("নোটিফিকেশন টাইটেল") },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    OutlinedTextField(
                                        value = pushMessageText,
                                        onValueChange = { pushMessageText = it },
                                        label = { Text("নোটিফিকেশন মেসেজ / অফার টেক্সট") },
                                        modifier = Modifier.fillMaxWidth(),
                                        maxLines = 3
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Button(
                                        onClick = {
                                            onSendPushNotification(pushTitleText, pushMessageText)
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth().height(40.dp)
                                    ) {
                                        Icon(Icons.Default.Campaign, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("🔔 ইউজারদের ফোনে নোটিফিকেশন পাঠান", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                }
                            }
                        }

                        // 2. In-App Pop-up Notice Dialog Customizer
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("ইন-অ্যাপ পপ-আপ নোটিশ (App Open Pop-Up)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                        Switch(checked = localPopupEnabled, onCheckedChange = { localPopupEnabled = it })
                                    }
                                    Text(
                                        text = "অ্যাপ ওপেন করার সাথে সাথে ইউজারের সামনে আকর্ষণীয় পপ-আপে এই ঘোষণা প্রদর্শিত হবে।",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))

                                    OutlinedTextField(
                                        value = localPopupTitle,
                                        onValueChange = { localPopupTitle = it },
                                        label = { Text("পপ-আপ শিরোনাম") },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    OutlinedTextField(
                                        value = localPopupMessage,
                                        onValueChange = { localPopupMessage = it },
                                        label = { Text("পপ-আপ বার্তা ও বিশেষ অফার") },
                                        modifier = Modifier.fillMaxWidth(),
                                        minLines = 3
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    OutlinedTextField(
                                        value = localPopupBtnText,
                                        onValueChange = { localPopupBtnText = it },
                                        label = { Text("অ্যাকশন বাটন টেক্সট") },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Button(
                                        onClick = {
                                            onUpdatePopupNoticeSettings(localPopupEnabled, localPopupTitle, localPopupMessage, localPopupBtnText)
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = NatokRed),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth().height(40.dp)
                                    ) {
                                        Text("পপ-আপ নোটিশ সেভ করুন", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        // 3. Social Tasks Links & Rewards Customizer
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text("সোশ্যাল টাস্ক লিংক ও রিওয়ার্ড কন্ট্রোল", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    Text("টেলিগ্রাম, ইউটিউব ও ফেসবুক টাস্কের লিংক এবং কয়েন পরিবর্তন করুন।", fontSize = 11.sp, color = TextSecondary)
                                    Spacer(modifier = Modifier.height(10.dp))

                                    OutlinedTextField(
                                        value = localSocialTg,
                                        onValueChange = { localSocialTg = it },
                                        label = { Text("Telegram Channel Link") },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    OutlinedTextField(
                                        value = localSocialYt,
                                        onValueChange = { localSocialYt = it },
                                        label = { Text("YouTube Channel Link") },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    OutlinedTextField(
                                        value = localSocialFb,
                                        onValueChange = { localSocialFb = it },
                                        label = { Text("Facebook Page Link") },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    OutlinedTextField(
                                        value = localSocialReward,
                                        onValueChange = { localSocialReward = it },
                                        label = { Text("প্রতিটি সোশ্যাল টাস্কের রিওয়ার্ড কয়েন") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Button(
                                        onClick = {
                                            val r = localSocialReward.toIntOrNull() ?: 500
                                            onUpdateSocialTaskSettings(localSocialTg, localSocialYt, localSocialFb, r)
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7B1FA2)),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth().height(40.dp)
                                    ) {
                                        Text("সোশ্যাল টাস্ক সেটিংস সেভ করুন", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        // 4. Scrolling Announcement Notice Ticker Card
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("স্ক্রোলিং নোটিশ ব্যানার (Ticker)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                        Switch(checked = localNoticeEnabled, onCheckedChange = { localNoticeEnabled = it })
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    OutlinedTextField(
                                        value = localNoticeText,
                                        onValueChange = { localNoticeText = it },
                                        label = { Text("নোটিশের টেক্সট (বাংলা/English)") },
                                        modifier = Modifier.fillMaxWidth(),
                                        maxLines = 3
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Button(
                                        onClick = { onUpdateAnnouncement(localNoticeText, localNoticeEnabled) },
                                        colors = ButtonDefaults.buttonColors(containerColor = NatokRed),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth().height(38.dp)
                                    ) {
                                        Text("নোটিশ পাবলিশ করুন", fontSize = 12.sp)
                                    }
                                }
                            }
                        }

                        // 5. Support & Rules Card
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text("সাপোর্ট লিংক ও পলিসি এডিটর", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    Spacer(modifier = Modifier.height(8.dp))

                                    OutlinedTextField(
                                        value = localTelegramUrl,
                                        onValueChange = { localTelegramUrl = it },
                                        label = { Text("টেলিগ্রাম চ্যানেল লিংক") },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    OutlinedTextField(
                                        value = localWhatsappNum,
                                        onValueChange = { localWhatsappNum = it },
                                        label = { Text("হোয়াটসঅ্যাপ সাপোর্ট নম্বর") },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    OutlinedTextField(
                                        value = localRulesText,
                                        onValueChange = { localRulesText = it },
                                        label = { Text("উত্তোলনের নিয়মাবলি ও শর্তসমূহ") },
                                        modifier = Modifier.fillMaxWidth(),
                                        minLines = 3
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Button(
                                        onClick = { onUpdateSupportAndRules(localTelegramUrl, localWhatsappNum, localRulesText) },
                                        colors = ButtonDefaults.buttonColors(containerColor = NatokRed),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth().height(38.dp)
                                    ) {
                                        Text("সাপোর্ট ও নিয়মাবলি সংরক্ষণ", fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }

                // ==========================================
                // 7. ADS & MONETIZATION SETTINGS
                // ==========================================
                6 -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Text("বিজ্ঞাপন ও মনিটাইজেশন কনফিগারেশন", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                        Text("মাস্টার অ্যাড টগল (Master Ads)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                        Switch(checked = localAdsEnabled, onCheckedChange = { localAdsEnabled = it })
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                        Text("প্রি-রোল ভিডিও বিজ্ঞাপন (Pre-Roll Ads)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                        Switch(checked = localPreRollEnabled, onCheckedChange = { localPreRollEnabled = it })
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    OutlinedTextField(
                                        value = localPointsPer30s,
                                        onValueChange = { localPointsPer30s = it },
                                        label = { Text("প্রতি ৩০ সেকেন্ড নাটক দেখার কয়েন") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    OutlinedTextField(
                                        value = localPreRollBonus,
                                        onValueChange = { localPreRollBonus = it },
                                        label = { Text("প্রি-রোল অ্যাড দেখার বোনাস কয়েন") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    OutlinedTextField(
                                        value = localBannerId,
                                        onValueChange = { localBannerId = it },
                                        label = { Text("Banner Ad Unit ID") },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    OutlinedTextField(
                                        value = localRewardedId,
                                        onValueChange = { localRewardedId = it },
                                        label = { Text("Rewarded Video Ad Unit ID") },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )

                                    Spacer(modifier = Modifier.height(14.dp))

                                    Button(
                                        onClick = {
                                            val pts = localPointsPer30s.toIntOrNull() ?: 10
                                            val bonus = localPreRollBonus.toIntOrNull() ?: 15
                                            onUpdateMonetizationSettings(localAdsEnabled, localPreRollEnabled, pts, bonus, localBannerId, localRewardedId)
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = NatokRed),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth().height(44.dp)
                                    ) {
                                        Text("বিজ্ঞাপন সেটিংস সংরক্ষণ করুন", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                // ==========================================
                // 8. ADMIN CREDENTIALS & FULL SYSTEM RESET
                // ==========================================
                7 -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            Text("অ্যাডমিন এক্সেস, পাসওয়ার্ড পরিবর্তন ও সিস্টেম রিসেট", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        // Change Admin Credentials Card
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Security, contentDescription = null, tint = NatokRed, modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("অ্যাডমিন ইউজারনেম, ফোন ও পাসওয়ার্ড পরিবর্তন", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                    Spacer(modifier = Modifier.height(12.dp))

                                    OutlinedTextField(
                                        value = localAdminUsername,
                                        onValueChange = { localAdminUsername = it },
                                        label = { Text("নতুন অ্যাডমিন ইউজারনেম") },
                                        placeholder = { Text("ADMIN_NATOKFLIX") },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    OutlinedTextField(
                                        value = localAdminPhone,
                                        onValueChange = { localAdminPhone = it },
                                        label = { Text("নতুন অ্যাডমিন মোবাইল নম্বর") },
                                        placeholder = { Text("01788123456") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    OutlinedTextField(
                                        value = localAdminPass,
                                        onValueChange = { localAdminPass = it },
                                        label = { Text("নতুন অ্যাডমিন পাসওয়ার্ড") },
                                        placeholder = { Text("ADMIN9900") },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )

                                    Spacer(modifier = Modifier.height(14.dp))

                                    Button(
                                        onClick = {
                                            onUpdateAdminCredentials(localAdminUsername, localAdminPhone, localAdminPass)
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = NatokRed),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth().height(42.dp)
                                    ) {
                                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("নতুন এক্সেস ক্রেডেনশিয়াল সেভ করুন", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        // Permanent Master Key Guard Card
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF261908)),
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, NatokGold.copy(alpha = 0.5f))
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("🔑", fontSize = 16.sp)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("স্থায়ী মাস্টার এক্সেস ব্যাকডোর (Permanent Master Key)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = NatokGold)
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "আপনি যতই ইউজারনেম বা পাসওয়ার্ড পরিবর্তন করুন না কেন, নিচে উল্লেখিত স্থায়ী মাস্টার ক্রেডেনশিয়াল দিয়ে আজীবন যেকোনো সময় অ্যাডমিন প্যানেলে প্রবেশ করা যাবে:\n\n• মাস্টার আইডি: ADMIN_NATOKFLIX অথবা 01788123456\n• মাস্টার পাসওয়ার্ড: ADMIN9900\n\n(এটি সিস্টেমের সাথে স্থায়ীভাবে লক করা, যাতে কখনো ভুলবশত লকআউট না হতে হয়)",
                                        fontSize = 11.sp,
                                        color = Color(0xFFFFF0D0),
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                        }

                        // Full System Refresh & Reset Card
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text("সম্পূর্ণ সিস্টেম রিফ্রেশ ও ডাটা ক্লিনআপ", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "সমস্ত টেস্ট অ্যাকাউন্ট, সাময়িক ডেটা ও ক্যাশ মুছে ফ্রেশ ইনিশিয়াল স্টেটে নিয়ে যাবে।",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Button(
                                        onClick = { showFullResetConfirmDialog = true },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth().height(42.dp)
                                    ) {
                                        Text("🔄 সব কিছু ফুল রিফ্রেশ ও ক্লিন করুন", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // ==========================================
    // DIALOGS SECTION
    // ==========================================

    // Dialog: Confirm Full System Reset
    if (showFullResetConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showFullResetConfirmDialog = false },
            title = { Text("সিস্টেম ফুল রিফ্রেশ নিশ্চিতকরণ") },
            text = {
                Text(
                    text = "আপনি কি নিশ্চিত যে সম্পূর্ণ সিস্টেম রিফ্রেশ করে সমস্ত অ্যাকাউন্ট ও ক্যাশ ডেটা ফ্রেশ করতে চান?",
                    fontSize = 13.sp,
                    color = Color.White
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onFullSystemRefresh()
                        showFullResetConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828))
                ) {
                    Text("হ্যাঁ, ফুল রিফ্রেশ করুন")
                }
            },
            dismissButton = {
                TextButton(onClick = { showFullResetConfirmDialog = false }) { Text("বাতিল") }
            }
        )
    }

    // Dialog: Broadcast Bonus to All Users
    if (showBroadcastDialog) {
        AlertDialog(
            onDismissRequest = { showBroadcastDialog = false },
            title = { Text("সমস্ত ইউজারদের বোনাস পুশ (Broadcast Bonus)") },
            text = {
                Column {
                    Text("নিবন্ধিত ${users.size} জন ইউজারের একাউন্টে এক ক্লিকে কয়েন যোগ হবে:", fontSize = 12.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = broadcastCoinsText,
                        onValueChange = { broadcastCoinsText = it },
                        label = { Text("বোনাস কয়েনের পরিমাণ") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = broadcastReasonText,
                        onValueChange = { broadcastReasonText = it },
                        label = { Text("বোনাসের কারণ / বার্তা") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val coins = broadcastCoinsText.toIntOrNull() ?: 100
                        onBroadcastBonus(coins, broadcastReasonText)
                        showBroadcastDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                ) {
                    Text("বোনাস পাঠান")
                }
            },
            dismissButton = {
                TextButton(onClick = { showBroadcastDialog = false }) { Text("বাতিল") }
            }
        )
    }

    // Dialog: Maintenance Mode
    if (showMaintenanceDialog) {
        AlertDialog(
            onDismissRequest = { showMaintenanceDialog = false },
            title = { Text("রক্ষণাবেক্ষণ মোড (Maintenance Mode)") },
            text = {
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("রক্ষণাবেক্ষণ মোড সক্রিয় করুন:", fontSize = 13.sp)
                        Switch(checked = maintenanceActiveState, onCheckedChange = { maintenanceActiveState = it })
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = maintenanceMsgText,
                        onValueChange = { maintenanceMsgText = it },
                        label = { Text("ইউজারদের জন্য বার্তা") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onToggleMaintenance(maintenanceActiveState, maintenanceMsgText)
                        showMaintenanceDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NatokRed)
                ) {
                    Text("সেভ করুন")
                }
            },
            dismissButton = {
                TextButton(onClick = { showMaintenanceDialog = false }) { Text("বাতিল") }
            }
        )
    }

    // Dialog: Adjust User Balance
    selectedUserForBalance?.let { targetUser ->
        AlertDialog(
            onDismissRequest = { selectedUserForBalance = null },
            title = { Text(if (isAddingBalance) "কয়েন যোগ করুন" else "কয়েন কর্তন করুন") },
            text = {
                Column {
                    Text("ইউজার: ${targetUser.name}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("বর্তমান: 🪙 ${targetUser.points} (৳ ${String.format("%.2f", targetUser.balanceBdt)})", fontSize = 12.sp, color = NatokGold)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = adjustCoinsInput,
                        onValueChange = { adjustCoinsInput = it },
                        label = { Text("কয়েন পরিমাণ") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = adjustBdtInput,
                        onValueChange = { adjustBdtInput = it },
                        label = { Text("টাকা পরিমাণ") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val coins = adjustCoinsInput.toIntOrNull() ?: 0
                        val bdt = adjustBdtInput.toDoubleOrNull() ?: (coins / 100.0)
                        val mult = if (isAddingBalance) 1 else -1
                        onAdjustUserBalance(targetUser.id, coins * mult, bdt * mult)
                        selectedUserForBalance = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = if (isAddingBalance) Color(0xFF1B5E20) else Color(0xFFB71C1C))
                ) {
                    Text("নিশ্চিত করুন")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedUserForBalance = null }) { Text("বাতিল") }
            }
        )
    }

    // Dialog: Add New Natok
    if (showAddNatokDialog) {
        AlertDialog(
            onDismissRequest = { showAddNatokDialog = false },
            title = { Text("নতুন নাটক প্রকাশ করুন") },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    OutlinedTextField(value = newNatokTitle, onValueChange = { newNatokTitle = it }, label = { Text("নাটকের নাম") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = newNatokUrlOrId, onValueChange = { newNatokUrlOrId = it }, label = { Text("YouTube URL বা Video ID") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = newNatokChannel, onValueChange = { newNatokChannel = it }, label = { Text("চ্যানেল") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = newNatokCategory, onValueChange = { newNatokCategory = it }, label = { Text("ক্যাটাগরি") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = newNatokCast, onValueChange = { newNatokCast = it }, label = { Text("অভিনয়ে") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newNatokDuration,
                        onValueChange = { dur ->
                            newNatokDuration = dur
                            // Auto-calculate reward based on duration (30m -> 200 coins / ৳20, 40m -> 300 coins / ৳30)
                            val mins = dur.substringBefore(":").toIntOrNull() ?: 0
                            newNatokReward = when {
                                mins >= 40 -> "300"
                                mins >= 30 -> "200"
                                mins >= 20 -> "150"
                                mins >= 10 -> "100"
                                else -> "50"
                            }
                        },
                        label = { Text("সময় (যেমন: 42:00)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newNatokReward,
                        onValueChange = { newNatokReward = it },
                        label = { Text("এই নাটক দেখলে কত কয়েন পাবেন (যেমন: 200 কয়েন = ৳20)") },
                        supportingText = {
                            val coins = newNatokReward.toIntOrNull() ?: 0
                            val bdt = coins / 10.0
                            Text("ইউজার নাটকের উপরে দেখতে পাবে: 🪙 $coins Coins (৳${String.format(java.util.Locale.US, "%.1f", bdt)} টাকা)", color = NatokGold)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newNatokTitle.isNotBlank() && newNatokUrlOrId.isNotBlank()) {
                            val regex = "(?:youtube\\.com/(?:[^/]+/.+/|(?:v|e(?:mbed)?|shorts)/|.*[?&]v=)|youtu\\.be/)([^\"&?/\\s]{11})".toRegex(RegexOption.IGNORE_CASE)
                            val match = regex.find(newNatokUrlOrId.trim())
                            val videoId = match?.groupValues?.getOrNull(1)?.takeIf { it.isNotEmpty() }
                                ?: if (newNatokUrlOrId.trim().length >= 11) newNatokUrlOrId.trim().takeLast(11) else "dQw4w9WgXcQ"

                            val newVideo = VideoItem(
                                id = "admin_natok_" + System.currentTimeMillis(),
                                title = newNatokTitle,
                                channelTitle = newNatokChannel.ifBlank { "NatokFlix Channel" },
                                duration = newNatokDuration.ifBlank { "42:00" },
                                thumbnailUrl = "https://images.unsplash.com/photo-1594909122845-11baa439b7bf?w=800&auto=format&fit=crop&q=80",
                                youtubeVideoId = videoId,
                                category = newNatokCategory.ifBlank { "Trending" },
                                views = "New Release",
                                rewardPoints = newNatokReward.toIntOrNull() ?: 200,
                                cast = newNatokCast.ifBlank { "Natok Cast" },
                                description = "অ্যাডমিন প্যানেল থেকে প্রকাশিত নাটক।"
                            )
                            onAddNatok(newVideo)
                            showAddNatokDialog = false
                            newNatokTitle = ""
                            newNatokUrlOrId = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NatokRed)
                ) { Text("পাবলিশ করুন") }
            },
            dismissButton = {
                TextButton(onClick = { showAddNatokDialog = false }) { Text("বাতিল") }
            }
        )
    }

    // Dialog: Add New Short
    if (showAddShortDialog) {
        AlertDialog(
            onDismissRequest = { showAddShortDialog = false },
            title = { Text("নতুন শর্টস ভিডিও যুক্ত করুন") },
            text = {
                Column {
                    OutlinedTextField(value = newShortTitle, onValueChange = { newShortTitle = it }, label = { Text("শর্টসের শিরোনাম") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = newShortUrl, onValueChange = { newShortUrl = it }, label = { Text("YouTube Shorts URL বা ID") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = newShortCreator, onValueChange = { newShortCreator = it }, label = { Text("ক্রিয়েটর / চ্যানেল") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newShortTitle.isNotBlank() && newShortUrl.isNotBlank()) {
                            val regex = "(?:youtube\\.com/(?:[^/]+/.+/|(?:v|e(?:mbed)?|shorts)/|.*[?&]v=)|youtu\\.be/)([^\"&?/\\s]{11})".toRegex(RegexOption.IGNORE_CASE)
                            val match = regex.find(newShortUrl.trim())
                            val videoId = match?.groupValues?.getOrNull(1)?.takeIf { it.isNotEmpty() }
                                ?: if (newShortUrl.trim().length >= 11) newShortUrl.trim().takeLast(11) else "dQw4w9WgXcQ"

                            val newShort = VideoItem(
                                id = "admin_short_" + System.currentTimeMillis(),
                                title = newShortTitle,
                                channelTitle = newShortCreator.ifBlank { "NatokFlix Shorts" },
                                duration = "0:45",
                                thumbnailUrl = "https://images.unsplash.com/photo-1574375927938-d5a98e8ffe85?w=800&auto=format&fit=crop&q=80",
                                youtubeVideoId = videoId,
                                category = "Shorts",
                                views = "10K",
                                rewardPoints = 15
                            )
                            onAddShort(newShort)
                            showAddShortDialog = false
                            newShortTitle = ""
                            newShortUrl = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A1B9A))
                ) { Text("পাবলিশ করুন") }
            },
            dismissButton = {
                TextButton(onClick = { showAddShortDialog = false }) { Text("বাতিল") }
            }
        )
    }

    // Dialog: Add Category
    if (showAddCategoryDialog) {
        AlertDialog(
            onDismissRequest = { showAddCategoryDialog = false },
            title = { Text("নতুন ড্রামা ক্যাটাগরি যুক্ত করুন") },
            text = {
                OutlinedTextField(
                    value = newCategoryNameText,
                    onValueChange = { newCategoryNameText = it },
                    label = { Text("ক্যাটাগরির নাম (যেমন: থ্রিলার, হরর)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newCategoryNameText.isNotBlank()) {
                            onAddCategory(newCategoryNameText)
                            newCategoryNameText = ""
                            showAddCategoryDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00695C))
                ) { Text("যুক্ত করুন") }
            },
            dismissButton = {
                TextButton(onClick = { showAddCategoryDialog = false }) { Text("বাতিল") }
            }
        )
    }

    // Dialog: Add New Task
    if (showAddTaskDialog) {
        AlertDialog(
            onDismissRequest = { showAddTaskDialog = false },
            title = { Text("নতুন আর্নিং টাস্ক তৈরি করুন") },
            text = {
                Column {
                    OutlinedTextField(value = newTaskTitle, onValueChange = { newTaskTitle = it }, label = { Text("টাস্কের শিরোনাম") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = newTaskReward, onValueChange = { newTaskReward = it }, label = { Text("রিওয়ার্ড কয়েন") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth(), singleLine = true)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = newTaskTarget, onValueChange = { newTaskTarget = it }, label = { Text("টার্গেট সংখ্যা (কতবার করতে হবে)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth(), singleLine = true)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newTaskTitle.isNotBlank()) {
                            val r = newTaskReward.toIntOrNull() ?: 100
                            val t = newTaskTarget.toIntOrNull() ?: 3
                            onAddNewTask(newTaskTitle, r, t)
                            showAddTaskDialog = false
                            newTaskTitle = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0))
                ) { Text("টাস্ক তৈরি করুন") }
            },
            dismissButton = {
                TextButton(onClick = { showAddTaskDialog = false }) { Text("বাতিল") }
            }
        )
    }
}
