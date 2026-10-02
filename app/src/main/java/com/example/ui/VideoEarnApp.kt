package com.example.ui

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.outlined.Gamepad
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.foundation.layout.offset
import com.example.ui.screens.GameZoneScreen
import androidx.compose.material.icons.outlined.Casino
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.AdminPanelScreen
import com.example.ui.screens.AppOpenAdScreen
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.FriendsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.LuckySpinScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ShortsScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.TaskScreen
import com.example.ui.screens.VideoPlayerScreen
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.NatokGold
import com.example.ui.theme.NatokRed
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.VideoEarnTheme

@Composable
fun VideoEarnApp(
    viewModel: NatokViewModel = viewModel()
) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val user by viewModel.user.collectAsStateWithLifecycle()
    val withdrawals by viewModel.withdrawals.collectAsStateWithLifecycle()
    val allWithdrawals by viewModel.allWithdrawals.collectAsStateWithLifecycle()
    val loginErrorAlert by viewModel.loginErrorAlert.collectAsStateWithLifecycle()

    val currentVideo by viewModel.currentVideo.collectAsStateWithLifecycle()
    val continuousSeconds by viewModel.continuousTimerSeconds.collectAsStateWithLifecycle()
    val sessionCoinsEarned by viewModel.sessionTotalCoinsEarned.collectAsStateWithLifecycle()
    val isPlayerPlaying by viewModel.isPlayerPlaying.collectAsStateWithLifecycle()
    val showPreRollAd by viewModel.showPreRollAd.collectAsStateWithLifecycle()
    val preRollCountdown by viewModel.preRollCountdown.collectAsStateWithLifecycle()
    val showPeriodicMidRollAd by viewModel.showPeriodicMidRollAd.collectAsStateWithLifecycle()
    val periodicAdCountdown by viewModel.periodicAdCountdown.collectAsStateWithLifecycle()
    val timeUntilNextMidRollAd by viewModel.timeUntilNextMidRollAd.collectAsStateWithLifecycle()
    val watchTimeCountdown by viewModel.watchTimeCountdown.collectAsStateWithLifecycle()

    val isSpinning by viewModel.isSpinning.collectAsStateWithLifecycle()
    val spinAngle by viewModel.spinAngle.collectAsStateWithLifecycle()
    val spinResultPoints by viewModel.spinResultPoints.collectAsStateWithLifecycle()
    val dailyCheckInClaimed by viewModel.dailyCheckInClaimed.collectAsStateWithLifecycle()
    val freeSpinsRemaining by viewModel.freeSpinsRemaining.collectAsStateWithLifecycle()
    val adSpinsUnlocked by viewModel.adSpinsUnlocked.collectAsStateWithLifecycle()
    val totalSpinsUsedToday by viewModel.totalSpinsUsedToday.collectAsStateWithLifecycle()
    val totalAdsWatchedToday by viewModel.totalAdsWatchedToday.collectAsStateWithLifecycle()
    val roomCode by viewModel.roomCode.collectAsStateWithLifecycle()
    val isUnlockingSpinAd by viewModel.isUnlockingSpinAd.collectAsStateWithLifecycle()

    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    val allUsers by viewModel.allUsers.collectAsStateWithLifecycle()
    val natoks by viewModel.natoks.collectAsStateWithLifecycle()
    val shorts by viewModel.shorts.collectAsStateWithLifecycle()
    val adsMasterEnabled by viewModel.adsMasterEnabled.collectAsStateWithLifecycle()
    val preRollAdEnabled by viewModel.preRollAdEnabled.collectAsStateWithLifecycle()
    val pointsPer30Seconds by viewModel.pointsPer30Seconds.collectAsStateWithLifecycle()
    val preRollBonusCoins by viewModel.preRollBonusCoins.collectAsStateWithLifecycle()
    val bannerAdUnitId by viewModel.bannerAdUnitId.collectAsStateWithLifecycle()
    val rewardedAdUnitId by viewModel.rewardedAdUnitId.collectAsStateWithLifecycle()
    val bannerUrls by viewModel.bannerUrls.collectAsStateWithLifecycle()

    // 20+ Extended Controls State
    val announcementNotice by viewModel.announcementNotice.collectAsStateWithLifecycle()
    val isAnnouncementEnabled by viewModel.isAnnouncementEnabled.collectAsStateWithLifecycle()
    val categoriesList by viewModel.categoriesList.collectAsStateWithLifecycle()
    val featuredPromoTitle by viewModel.featuredPromoTitle.collectAsStateWithLifecycle()
    val featuredPromoSubtitle by viewModel.featuredPromoSubtitle.collectAsStateWithLifecycle()
    val minWithdrawalBdt by viewModel.minWithdrawalBdt.collectAsStateWithLifecycle()
    val coinsPerBdtRate by viewModel.coinsPerBdtRate.collectAsStateWithLifecycle()
    val rewardedVideoAdCoins by viewModel.rewardedVideoAdCoins.collectAsStateWithLifecycle()
    val interstitialAdCoins by viewModel.interstitialAdCoins.collectAsStateWithLifecycle()
    val reelWatchCoins by viewModel.reelWatchCoins.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()
    val isSearching by viewModel.isSearching.collectAsStateWithLifecycle()
    val dailyCheckInBonus by viewModel.dailyCheckInBonus.collectAsStateWithLifecycle()
    val referralBonusCoins by viewModel.referralBonusCoins.collectAsStateWithLifecycle()
    val refereeBonusCoins by viewModel.refereeBonusCoins.collectAsStateWithLifecycle()
    val shortsRewardCoins by viewModel.shortsRewardCoins.collectAsStateWithLifecycle()
    val isBkashEnabled by viewModel.isBkashEnabled.collectAsStateWithLifecycle()
    val isNagadEnabled by viewModel.isNagadEnabled.collectAsStateWithLifecycle()
    val isRocketEnabled by viewModel.isRocketEnabled.collectAsStateWithLifecycle()
    val telegramChannelUrl by viewModel.telegramChannelUrl.collectAsStateWithLifecycle()
    val whatsappSupportNumber by viewModel.whatsappSupportNumber.collectAsStateWithLifecycle()
    val withdrawalRulesText by viewModel.withdrawalRulesText.collectAsStateWithLifecycle()
    val isMaintenanceMode by viewModel.isMaintenanceMode.collectAsStateWithLifecycle()
    val maintenanceMessage by viewModel.maintenanceMessage.collectAsStateWithLifecycle()
    val freeDailySpinsLimit by viewModel.freeDailySpinsLimit.collectAsStateWithLifecycle()

    val adminCustomUsername by viewModel.adminCustomUsername.collectAsStateWithLifecycle()
    val adminCustomPhone by viewModel.adminCustomPhone.collectAsStateWithLifecycle()
    val adminCustomPassword by viewModel.adminCustomPassword.collectAsStateWithLifecycle()

    // Social Tasks & Popup State
    val telegramTaskUrl by viewModel.telegramTaskUrl.collectAsStateWithLifecycle()
    val youtubeTaskUrl by viewModel.youtubeTaskUrl.collectAsStateWithLifecycle()
    val facebookTaskUrl by viewModel.facebookTaskUrl.collectAsStateWithLifecycle()
    val socialTaskRewardCoins by viewModel.socialTaskRewardCoins.collectAsStateWithLifecycle()
    val isTelegramTaskClaimed by viewModel.isTelegramTaskClaimed.collectAsStateWithLifecycle()
    val isYoutubeTaskClaimed by viewModel.isYoutubeTaskClaimed.collectAsStateWithLifecycle()
    val isFacebookTaskClaimed by viewModel.isFacebookTaskClaimed.collectAsStateWithLifecycle()

    val isPopupNoticeEnabled by viewModel.isPopupNoticeEnabled.collectAsStateWithLifecycle()
    val popupNoticeTitle by viewModel.popupNoticeTitle.collectAsStateWithLifecycle()
    val popupNoticeMessage by viewModel.popupNoticeMessage.collectAsStateWithLifecycle()
    val popupNoticeBtnText by viewModel.popupNoticeBtnText.collectAsStateWithLifecycle()
    val showPopupNoticeDialog by viewModel.showPopupNoticeDialog.collectAsStateWithLifecycle()

    // Friends and Chat State
    val friends by viewModel.friends.collectAsStateWithLifecycle()
    val allMessages by viewModel.allMessages.collectAsStateWithLifecycle()
    val activeChatFriend by viewModel.activeChatFriend.collectAsStateWithLifecycle()
    val isBotTyping by viewModel.isBotTyping.collectAsStateWithLifecycle()

    val chatMessagesForActiveFriend = remember(allMessages, activeChatFriend) {
        allMessages.filter { it.conversationUserId == activeChatFriend?.id }
    }

    val isDarkTheme by viewModel.isDarkTheme.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val isLoadingCategoryFeed by viewModel.isLoadingCategoryFeed.collectAsStateWithLifecycle()
    val isLoadingMore by viewModel.isLoadingMore.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    // Automatic Push Notification Permission Request for Android 13+ (POST_NOTIFICATIONS)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val permissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            // Permission result handled
        }
        LaunchedEffect(Unit) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.uiMessage.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    VideoEarnTheme(darkTheme = isDarkTheme) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
        AnimatedContent(
            targetState = currentScreen,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "screen_transition"
        ) { targetScreen ->
            when (targetScreen) {
                AppScreen.SPLASH -> {
                    SplashScreen()
                }

                AppScreen.APP_OPEN_AD -> {
                    AppOpenAdScreen(
                        onDismiss = { viewModel.onAppOpenAdDismissed() }
                    )
                }

                AppScreen.LOGIN -> {
                    LoginScreen(
                        onGoogleSignIn = { viewModel.handleGoogleSignIn() },
                        onFacebookSignIn = { viewModel.handleFacebookSignIn() },
                        onLoginSubmit = { userOrEmail, pass, rememberMe ->
                            viewModel.handleLogin(userOrEmail, pass, rememberMe)
                        },
                        onSignUpSubmit = { name, email, phone, pass, refCode, gender, birthday ->
                            viewModel.handleSignUp(name, email, phone, pass, refCode, gender, birthday)
                        },
                        onSendResetOtp = { email, ctx, callback ->
                            viewModel.sendPasswordResetOtp(email, ctx, callback)
                        },
                        onResetUserPassword = { email, newPass, callback ->
                            viewModel.resetUserPassword(email, newPass, callback)
                        },
                        loginError = loginErrorAlert
                    )
                }

                AppScreen.CHAT -> {
                    activeChatFriend?.let { friend ->
                        ChatScreen(
                            user = user,
                            friend = friend,
                            messages = chatMessagesForActiveFriend,
                            onSendMessage = { msg -> viewModel.sendChatMessage(msg) },
                            onBack = { viewModel.closeChat() },
                            isBotTyping = isBotTyping
                        )
                    }
                }

                AppScreen.VIDEO_PLAYER -> {
                    currentVideo?.let { video ->
                        VideoPlayerScreen(
                            video = video,
                            allNatoks = natoks,
                            continuousSeconds = continuousSeconds,
                            watchTimeCountdown = watchTimeCountdown,
                            sessionCoinsEarned = sessionCoinsEarned,
                            showPreRollAd = showPreRollAd,
                            preRollCountdown = preRollCountdown,
                            showPeriodicMidRollAd = showPeriodicMidRollAd,
                            periodicAdCountdown = periodicAdCountdown,
                            timeUntilNextMidRollAd = timeUntilNextMidRollAd,
                            isPlayerPlaying = isPlayerPlaying,
                            onBackClick = { viewModel.closeVideoPlayer() },
                            onSkipPreRollAd = { viewModel.skipPreRollAd() },
                            onDismissPeriodicAd = { viewModel.dismissPeriodicLongAd() },
                            onTogglePlayPause = { playing -> viewModel.setPlayerPlaying(playing) },
                            onVideoEnded = { viewModel.onVideoCompletion() },
                            onPlayCustomUrl = { url -> viewModel.playCustomYouTubeUrl(url) },
                            onRelatedVideoClick = { related -> viewModel.playVideo(related) }
                        )
                    } ?: run {
                        viewModel.closeVideoPlayer()
                    }
                }

                AppScreen.ADMIN_PANEL -> {
                    AdminPanelScreen(
                        users = allUsers,
                        natoks = natoks,
                        withdrawals = allWithdrawals,
                        adsMasterEnabled = adsMasterEnabled,
                        preRollAdEnabled = preRollAdEnabled,
                        pointsPer30s = pointsPer30Seconds,
                        preRollBonusCoins = preRollBonusCoins,
                        bannerAdId = bannerAdUnitId,
                        rewardedAdId = rewardedAdUnitId,
                        announcementNotice = announcementNotice,
                        isAnnouncementEnabled = isAnnouncementEnabled,
                        categoriesList = categoriesList,
                        featuredPromoTitle = featuredPromoTitle,
                        featuredPromoSubtitle = featuredPromoSubtitle,
                        minWithdrawalBdt = minWithdrawalBdt,
                        coinsPerBdtRate = coinsPerBdtRate,
                        rewardedVideoAdCoins = rewardedVideoAdCoins,
                        interstitialAdCoins = interstitialAdCoins,
                        reelWatchCoins = reelWatchCoins,
                        dailyCheckInBonus = dailyCheckInBonus,
                        referralBonusCoins = referralBonusCoins,
                        refereeBonusCoins = refereeBonusCoins,
                        shortsRewardCoins = shortsRewardCoins,
                        isBkashEnabled = isBkashEnabled,
                        isNagadEnabled = isNagadEnabled,
                        isRocketEnabled = isRocketEnabled,
                        telegramChannelUrl = telegramChannelUrl,
                        whatsappSupportNumber = whatsappSupportNumber,
                        withdrawalRulesText = withdrawalRulesText,
                        isMaintenanceMode = isMaintenanceMode,
                        maintenanceMessage = maintenanceMessage,
                        freeDailySpinsLimit = freeDailySpinsLimit,
                        adminCustomUsername = adminCustomUsername,
                        adminCustomPhone = adminCustomPhone,
                        adminCustomPassword = adminCustomPassword,
                        telegramTaskUrl = telegramTaskUrl,
                        youtubeTaskUrl = youtubeTaskUrl,
                        facebookTaskUrl = facebookTaskUrl,
                        socialTaskRewardCoins = socialTaskRewardCoins,
                        isPopupNoticeEnabled = isPopupNoticeEnabled,
                        popupNoticeTitle = popupNoticeTitle,
                        popupNoticeMessage = popupNoticeMessage,
                        popupNoticeBtnText = popupNoticeBtnText,
                        onAdjustUserBalance = { userId, coinsDelta, bdtDelta ->
                            viewModel.adjustUserBalance(userId, coinsDelta, bdtDelta)
                        },
                        onAddNatok = { newVideo ->
                            viewModel.addNatok(newVideo)
                        },
                        onDeleteNatok = { videoId ->
                            viewModel.deleteNatok(videoId)
                        },
                        onApproveWithdrawal = { id -> viewModel.adminApproveWithdrawal(id) },
                        onRejectWithdrawal = { id -> viewModel.adminRejectWithdrawal(id) },
                        onUpdateMonetizationSettings = { ads, preRoll, pts, bonus, banner, rewarded ->
                            viewModel.updateMonetizationSettings(ads, preRoll, pts, bonus, banner, rewarded)
                        },
                        onUpdateAnnouncement = { notice, enabled ->
                            viewModel.updateAnnouncement(notice, enabled)
                        },
                        onUpdateEconomySettings = { minW, r, c, ref, refReferee, sh, sp, rew, inter, reel ->
                            viewModel.updateEconomySettings(minW, r, c, ref, refReferee, sh, sp, rew, inter, reel)
                        },
                        onUpdateGateways = { bkash, nagad, rocket ->
                            viewModel.updateGateways(bkash, nagad, rocket)
                        },
                        onUpdateSupportAndRules = { t, w, r ->
                            viewModel.updateSupportAndRules(t, w, r)
                        },
                        onUpdatePromoBanner = { title, sub ->
                            viewModel.updatePromoBanner(title, sub)
                        },
                        onToggleMaintenance = { enabled, msg ->
                            viewModel.toggleMaintenance(enabled, msg)
                        },
                        onAddCategory = { cat ->
                            viewModel.addCategory(cat)
                        },
                        onDeleteCategory = { cat ->
                            viewModel.deleteCategory(cat)
                        },
                        onAddShort = { shortVid ->
                            viewModel.addShort(shortVid)
                        },
                        onAddNewTask = { t, reward, target ->
                            viewModel.addNewTask(t, reward, target)
                        },
                        onBroadcastBonus = { coins, reason ->
                            viewModel.broadcastBonusToAllUsers(coins, reason)
                        },
                        onToggleUserBan = { id ->
                            viewModel.toggleUserBan(id)
                        },
                        onUpdateAdminCredentials = { u, p, pass ->
                            viewModel.updateAdminCredentials(u, p, pass)
                        },
                        onFullSystemRefresh = {
                            viewModel.fullSystemRefreshAndReset()
                        },
                        onSendPushNotification = { title, msg ->
                            viewModel.sendPushNotification(title, msg)
                        },
                        onUpdateSocialTaskSettings = { tg, yt, fb, reward ->
                            viewModel.updateSocialTaskSettings(tg, yt, fb, reward)
                        },
                        onUpdatePopupNoticeSettings = { enabled, title, msg, btnText ->
                            viewModel.updatePopupNoticeSettings(enabled, title, msg, btnText)
                        },
                        onExitAdmin = { viewModel.exitAdminMode() }
                    )
                }

                AppScreen.MAIN_DASHBOARD -> {
                    Scaffold(
                        snackbarHost = { SnackbarHost(snackbarHostState) },
                        bottomBar = {
                            NavigationBar(
                                containerColor = SurfaceDark,
                                tonalElevation = 8.dp,
                                modifier = Modifier.testTag("bottom_nav_bar")
                            ) {
                                // 1. Home Feed
                                NavigationBarItem(
                                    selected = selectedTab == NavigationTab.HOME,
                                    onClick = { viewModel.selectTab(NavigationTab.HOME) },
                                    icon = {
                                        Icon(
                                            imageVector = if (selectedTab == NavigationTab.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                                            contentDescription = "Home",
                                            modifier = Modifier.size(22.dp)
                                        )
                                    },
                                    label = { Text("হোম", fontSize = 10.sp) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = NatokRed,
                                        selectedTextColor = NatokRed,
                                        indicatorColor = Color.Transparent,
                                        unselectedIconColor = Color(0xFF888899),
                                        unselectedTextColor = Color(0xFF888899)
                                    ),
                                    modifier = Modifier.testTag("nav_item_home")
                                )

                                // 2. Shorts
                                NavigationBarItem(
                                    selected = selectedTab == NavigationTab.SHORTS,
                                    onClick = { viewModel.selectTab(NavigationTab.SHORTS) },
                                    icon = {
                                        Icon(
                                            imageVector = if (selectedTab == NavigationTab.SHORTS) Icons.Filled.PlayCircle else Icons.Outlined.PlayCircle,
                                            contentDescription = "Shorts",
                                            modifier = Modifier.size(20.dp)
                                        )
                                    },
                                    label = { Text("শর্টস", fontSize = 9.sp) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = NatokRed,
                                        selectedTextColor = NatokRed,
                                        indicatorColor = Color.Transparent,
                                        unselectedIconColor = Color(0xFF888899),
                                        unselectedTextColor = Color(0xFF888899)
                                    ),
                                    modifier = Modifier.testTag("nav_item_shorts")
                                )

                                // 3. Friends & Chat
                                NavigationBarItem(
                                    selected = selectedTab == NavigationTab.FRIENDS,
                                    onClick = { viewModel.selectTab(NavigationTab.FRIENDS) },
                                    icon = {
                                        Icon(
                                            imageVector = if (selectedTab == NavigationTab.FRIENDS) Icons.Filled.Group else Icons.Outlined.Group,
                                            contentDescription = "Friends",
                                            modifier = Modifier.size(20.dp)
                                        )
                                    },
                                    label = { Text("আড্ডা", fontSize = 9.sp) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = NatokRed,
                                        selectedTextColor = NatokRed,
                                        indicatorColor = Color.Transparent,
                                        unselectedIconColor = Color(0xFF888899),
                                        unselectedTextColor = Color(0xFF888899)
                                    ),
                                    modifier = Modifier.testTag("nav_item_friends")
                                )

                                // 3.5 Game Zone Tab with "গেম খেলে কয়েন জিতুন" Badge
                                NavigationBarItem(
                                    selected = selectedTab == NavigationTab.GAME_ZONE,
                                    onClick = { viewModel.selectTab(NavigationTab.GAME_ZONE) },
                                    icon = {
                                        Box {
                                            Icon(
                                                imageVector = if (selectedTab == NavigationTab.GAME_ZONE) Icons.Filled.Gamepad else Icons.Outlined.Gamepad,
                                                contentDescription = "Game Zone",
                                                modifier = Modifier.size(20.dp)
                                            )
                                            // Tiny glowing badge on top of icon
                                            Box(
                                                modifier = Modifier
                                                    .align(Alignment.TopEnd)
                                                    .offset(x = 12.dp, y = (-4).dp)
                                                    .background(NatokGold, RoundedCornerShape(4.dp))
                                                    .padding(horizontal = 3.dp, vertical = 1.dp)
                                            ) {
                                                Text(
                                                    text = "গেম খেলে কয়েন জিতুন",
                                                    fontSize = 5.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.Black
                                                )
                                            }
                                        }
                                    },
                                    label = { Text("গেম জোন", fontSize = 9.sp) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = NatokRed,
                                        selectedTextColor = NatokRed,
                                        indicatorColor = Color.Transparent,
                                        unselectedIconColor = Color(0xFF888899),
                                        unselectedTextColor = Color(0xFF888899)
                                    ),
                                    modifier = Modifier.testTag("nav_item_game_zone")
                                )

                                // 4. Tasks
                                NavigationBarItem(
                                    selected = selectedTab == NavigationTab.TASK,
                                    onClick = { viewModel.selectTab(NavigationTab.TASK) },
                                    icon = {
                                        Icon(
                                            imageVector = if (selectedTab == NavigationTab.TASK) Icons.Filled.Assignment else Icons.Outlined.Assignment,
                                            contentDescription = "Tasks",
                                            modifier = Modifier.size(20.dp)
                                        )
                                    },
                                    label = { Text("টাস্ক", fontSize = 9.sp) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = NatokRed,
                                        selectedTextColor = NatokRed,
                                        indicatorColor = Color.Transparent,
                                        unselectedIconColor = Color(0xFF888899),
                                        unselectedTextColor = Color(0xFF888899)
                                    ),
                                    modifier = Modifier.testTag("nav_item_task")
                                )

                                // 5. Lucky Spin
                                NavigationBarItem(
                                    selected = selectedTab == NavigationTab.SPIN,
                                    onClick = { viewModel.selectTab(NavigationTab.SPIN) },
                                    icon = {
                                        Icon(
                                            imageVector = if (selectedTab == NavigationTab.SPIN) Icons.Filled.Casino else Icons.Outlined.Casino,
                                            contentDescription = "Spin",
                                            modifier = Modifier.size(20.dp)
                                        )
                                    },
                                    label = { Text("স্পিন", fontSize = 9.sp) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = NatokRed,
                                        selectedTextColor = NatokRed,
                                        indicatorColor = Color.Transparent,
                                        unselectedIconColor = Color(0xFF888899),
                                        unselectedTextColor = Color(0xFF888899)
                                    ),
                                    modifier = Modifier.testTag("nav_item_spin")
                                )

                                // 6. Profile
                                NavigationBarItem(
                                    selected = selectedTab == NavigationTab.PROFILE,
                                    onClick = { viewModel.selectTab(NavigationTab.PROFILE) },
                                    icon = {
                                        Icon(
                                            imageVector = if (selectedTab == NavigationTab.PROFILE) Icons.Filled.Person else Icons.Outlined.Person,
                                            contentDescription = "Profile",
                                            modifier = Modifier.size(20.dp)
                                        )
                                    },
                                    label = { Text("প্রোফাইল", fontSize = 9.sp) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = NatokRed,
                                        selectedTextColor = NatokRed,
                                        indicatorColor = Color.Transparent,
                                        unselectedIconColor = Color(0xFF888899),
                                        unselectedTextColor = Color(0xFF888899)
                                    ),
                                    modifier = Modifier.testTag("nav_item_profile")
                                )
                            }
                        }
                    ) { innerPadding ->
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            if (isMaintenanceMode) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFFB71C1C))
                                        .padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = "Maintenance",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "সার্ভার রক্ষণাবেক্ষণ: $maintenanceMessage",
                                        fontSize = 11.sp,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            Box(modifier = Modifier.weight(1f)) {
                                when (selectedTab) {
                                    NavigationTab.HOME -> {
                                        HomeScreen(
                                            user = user,
                                            natoks = natoks,
                                            onVideoClick = { video -> viewModel.playVideo(video) },
                                            onCoinsBadgeClick = { viewModel.selectTab(NavigationTab.PROFILE) },
                                            isDarkTheme = isDarkTheme,
                                            onToggleTheme = { viewModel.toggleTheme() },
                                            onPlayCustomUrl = { url -> viewModel.playCustomYouTubeUrl(url) },
                                            onSpinTabNavigate = { viewModel.selectTab(NavigationTab.SPIN) },
                                            onGameZoneNavigate = { viewModel.selectTab(NavigationTab.GAME_ZONE) },
                                            announcementNotice = announcementNotice,
                                            isAnnouncementEnabled = isAnnouncementEnabled,
                                            categories = categoriesList,
                                            activeCategory = selectedCategory,
                                            onSelectCategory = { cat -> viewModel.selectCategory(cat) },
                                            isLoadingCategoryFeed = isLoadingCategoryFeed,
                                            isLoadingMore = isLoadingMore,
                                            onLoadMore = { viewModel.loadMoreVideosForCategory() },
                                            featuredPromoTitle = featuredPromoTitle,
                                            featuredPromoSubtitle = featuredPromoSubtitle,
                                            searchQuery = searchQuery,
                                            searchResults = searchResults,
                                            isSearching = isSearching,
                                            onSearchQueryChanged = { q -> viewModel.onSearchQueryChanged(q) },
                                            onClearSearch = { viewModel.clearSearch() },
                                            bannerUrls = bannerUrls
                                        )
                                    }

                                    NavigationTab.SHORTS -> {
                                        ShortsScreen(
                                            shorts = shorts,
                                            onEarnReward = { points ->
                                                viewModel.claimShortReward(points)
                                            },
                                            reelWatchCoins = reelWatchCoins,
                                            coinsPerBdtRate = coinsPerBdtRate
                                        )
                                    }

                                    NavigationTab.FRIENDS -> {
                                        FriendsScreen(
                                            user = user,
                                            friends = friends,
                                            onOpenChat = { friend -> viewModel.openChat(friend) },
                                            onSendFriendRequest = { id -> viewModel.sendFriendRequest(id) },
                                            onAcceptFriendRequest = { id -> viewModel.acceptFriendRequest(id) },
                                            onRejectFriendRequest = { id -> viewModel.rejectFriendRequest(id) }
                                        )
                                    }

                NavigationTab.GAME_ZONE -> {
                    GameZoneScreen(
                        user = user,
                        totalAdsWatched = totalAdsWatchedToday,
                        roomCode = roomCode,
                        onWatchAd = { viewModel.watchRewardedAd() },
                        onCreateRoom = { viewModel.createRoom() },
                        onJoinRoom = { code -> viewModel.joinRoom(code) },
                        onDeductStake = { stake -> viewModel.deductGameStake(stake) },
                        onRewardWinner = { stake, outcome -> viewModel.processGameResult(stake, outcome) }
                    )
                }

                                    NavigationTab.TASK -> {
                                        TaskScreen(
                                            user = user,
                                            tasks = tasks,
                                            onClaimReward = { taskId ->
                                                viewModel.claimTaskReward(taskId)
                                            },
                                            onPerformAction = { task ->
                                                viewModel.performTaskAction(task)
                                            },
                                            telegramUrl = telegramTaskUrl,
                                            youtubeUrl = youtubeTaskUrl,
                                            facebookUrl = facebookTaskUrl,
                                            socialReward = socialTaskRewardCoins,
                                            isTelegramClaimed = isTelegramTaskClaimed,
                                            isYoutubeClaimed = isYoutubeTaskClaimed,
                                            isFacebookClaimed = isFacebookTaskClaimed,
                                            onCompleteSocialTask = { platform ->
                                                viewModel.completeSocialTask(platform, context)
                                            }
                                        )
                                    }

                                    NavigationTab.SPIN -> {
                                        LuckySpinScreen(
                                            user = user,
                                            isSpinning = isSpinning,
                                            spinAngle = spinAngle,
                                            spinResultPoints = spinResultPoints,
                                            dailyCheckInClaimed = dailyCheckInClaimed,
                                            freeSpinsRemaining = freeSpinsRemaining,
                                            adSpinsUnlocked = adSpinsUnlocked,
                                            totalSpinsUsedToday = totalSpinsUsedToday,
                                            maxDailySpins = viewModel.maxDailySpins,
                                            isUnlockingSpinAd = isUnlockingSpinAd,
                                            onSpinClick = { viewModel.spinWheel() },
                                            onUnlockSpinViaAd = { viewModel.unlockSpinViaAd() },
                                            onClaimDaily = { viewModel.claimDailyCheckIn() },
                                            onWatchRewardedAd = { viewModel.watchRewardedAd() }
                                        )
                                    }

                                    NavigationTab.PROFILE -> {
                                        ProfileScreen(
                                            user = user,
                                            withdrawals = withdrawals,
                                            onRequestWithdrawal = { method, operator, phone, amt ->
                                                viewModel.requestWithdrawal(method, operator, phone, amt)
                                            },
                                            onLogout = { viewModel.logout() },
                                            onAdminPinSubmit = { pin ->
                                                viewModel.openAdminWithSecretPin(pin)
                                            },
                                            onUpdateProfile = { name, gender, birthday, phone ->
                                                viewModel.updateUserProfile(name, gender, birthday, phone)
                                            },
                                            minWithdrawalBdt = minWithdrawalBdt,
                                            coinsPerBdtRate = coinsPerBdtRate,
                                            referrerBonusCoins = referralBonusCoins,
                                            refereeBonusCoins = refereeBonusCoins,
                                            isBkashEnabled = isBkashEnabled,
                                            isNagadEnabled = isNagadEnabled,
                                            isRocketEnabled = isRocketEnabled,
                                            telegramChannelUrl = telegramChannelUrl,
                                            whatsappSupportNumber = whatsappSupportNumber,
                                            withdrawalRulesText = withdrawalRulesText
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // In-App Pop-Up Announcement Notice Dialog (App Launch Alert)
                    if (showPopupNoticeDialog && isPopupNoticeEnabled) {
                        AlertDialog(
                            onDismissRequest = { viewModel.dismissPopupNotice() },
                            shape = RoundedCornerShape(16.dp),
                            containerColor = Color(0xFF1E1728),
                            title = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Campaign,
                                        contentDescription = null,
                                        tint = NatokGold,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = popupNoticeTitle,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            },
                            text = {
                                Column {
                                    Text(
                                        text = popupNoticeMessage,
                                        fontSize = 13.sp,
                                        color = Color(0xFFEEEEEE),
                                        lineHeight = 19.sp
                                    )
                                    Spacer(modifier = Modifier.height(14.dp))
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFF2E1B38)),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text("🎁", fontSize = 16.sp)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "সোশ্যাল টাস্ক অপশনে ৩টি টাস্ক কমপ্লিট করে ফ্রিতে ১৫০০ কয়েন গ্রহণ করুন!",
                                                fontSize = 11.sp,
                                                color = NatokGold,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }
                                }
                            },
                            confirmButton = {
                                Button(
                                    onClick = { viewModel.dismissPopupNotice() },
                                    colors = ButtonDefaults.buttonColors(containerColor = NatokRed),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = popupNoticeBtnText,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        )
                    }

                    // Celebratory Welcome Bonus Popup Dialog
                    val welcomeBonus by viewModel.welcomeBonusAwarded.collectAsStateWithLifecycle()
                    welcomeBonus?.let { bonus ->
                        AlertDialog(
                            onDismissRequest = { viewModel.dismissWelcomeBonusDialog() },
                            shape = RoundedCornerShape(20.dp),
                            containerColor = Color(0xFF1F162B),
                            tonalElevation = 10.dp,
                            title = {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("🎉 🎁 👑", fontSize = 28.sp)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "অভিনন্দন! স্বাগতম NatokFlix-এ",
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = NatokGold,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            },
                            text = {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "আপনি নতুন অ্যাকাউন্ট তৈরি করে সফলভাবে সাইন-আপ রিওয়ার্ড ক্লেইম করেছেন!",
                                        fontSize = 12.sp,
                                        color = Color(0xFFE0E0E0),
                                        textAlign = TextAlign.Center,
                                        lineHeight = 17.sp
                                    )
                                    Spacer(modifier = Modifier.height(14.dp))
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFF2F1D46)),
                                        shape = RoundedCornerShape(12.dp),
                                        border = BorderStroke(1.5.dp, NatokGold)
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(14.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = "🪙 +${bonus.coins} Coins",
                                                fontSize = 24.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = NatokGold
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "(সমপরিমাণ ৳${bonus.bdtAmount} টাকা)",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF69F0AE)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = "আপনার ওয়ালেটে বোনাস যুক্ত হয়েছে। এখনই নাটক ও শর্টস দেখে ইনকাম বাড়ান!",
                                        fontSize = 11.sp,
                                        color = Color(0xFFB0B0C0),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            },
                            confirmButton = {
                                Button(
                                    onClick = { viewModel.dismissWelcomeBonusDialog() },
                                    colors = ButtonDefaults.buttonColors(containerColor = NatokRed),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth().height(44.dp)
                                ) {
                                    Text("ধন্যবাদ / শুরু করুন (Let's Go)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            }
                        )
                    }

                    // Login Error Alert Dialog
                    if (loginErrorAlert != null) {
                        AlertDialog(
                            onDismissRequest = { viewModel.dismissLoginErrorAlert() },
                            title = {
                                Text(
                                    text = "লগইন ব্যর্থ (Login Failed)",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            },
                            text = {
                                Text(
                                    text = loginErrorAlert ?: "",
                                    fontSize = 13.sp,
                                    color = Color(0xFFEEEEEE),
                                    lineHeight = 19.sp
                                )
                            },
                            confirmButton = {
                                Button(
                                    onClick = { viewModel.dismissLoginErrorAlert() },
                                    colors = ButtonDefaults.buttonColors(containerColor = NatokRed),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "ঠিক আছে (OK)",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                            },
                            shape = RoundedCornerShape(16.dp),
                            containerColor = Color(0xFF1E1728)
                        )
                    }
                }
            }
        }
    }
}
}
