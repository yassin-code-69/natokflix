package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.ChatMessage
import com.example.data.model.EconomyConfig
import com.example.data.model.FriendProfile
import com.example.data.model.FriendStatus
import com.example.data.model.TaskItem
import com.example.data.model.TaskType
import com.example.data.model.User
import com.example.data.model.VideoItem
import com.example.data.model.Withdrawal
import com.example.data.repository.NatokRepository
import com.example.util.BangladeshiBotEngine
import com.example.util.NotificationHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class WelcomeBonusInfo(
    val coins: Int,
    val bdtAmount: Int
)

enum class AppScreen {
    SPLASH,
    APP_OPEN_AD,
    LOGIN,
    MAIN_DASHBOARD,
    VIDEO_PLAYER,
    ADMIN_PANEL,
    CHAT
}

enum class NavigationTab {
    HOME,
    SHORTS,
    FRIENDS,
    GAME_ZONE,
    TASK,
    SPIN,
    PROFILE
}

class NatokViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getInstance(application)
    private val repository = NatokRepository(database)
    private val deviceId: String

    // YouTube API Auto-Fetch & Category Feed State
    private val _selectedCategory = MutableStateFlow("সকল নাটক")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _categoriesList = MutableStateFlow(
        listOf("সকল নাটক", "Trending Natok", "Korean & Chinese Dubbed", "Romantic Drama", "Comedy Drama", "Bangla Music & Songs")
    )
    val categoriesList: StateFlow<List<String>> = _categoriesList.asStateFlow()

    private val _isLoadingCategoryFeed = MutableStateFlow(false)
    val isLoadingCategoryFeed: StateFlow<Boolean> = _isLoadingCategoryFeed.asStateFlow()

    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore.asStateFlow()

    private val categoryNextPageTokens = mutableMapOf<String, String?>()
    private val adminPinnedNatoks = mutableListOf<VideoItem>()

    // ==========================================
    // AUTOMATED LIVE SEARCH BAR ENGINE
    // ==========================================
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<VideoItem>>(emptyList())
    val searchResults: StateFlow<List<VideoItem>> = _searchResults.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    private var searchJob: kotlinx.coroutines.Job? = null

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        val trimmed = query.trim()
        if (trimmed.isBlank()) {
            searchJob?.cancel()
            _searchResults.value = emptyList()
            _isSearching.value = false
            return
        }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            _isSearching.value = true
            delay(250) // Fast debounce for instant live results
            val results = com.example.data.remote.YouTubeApiEngine.searchVideosLive(trimmed)
            _searchResults.value = results
            _isSearching.value = false
        }
    }

    fun clearSearch() {
        searchJob?.cancel()
        _searchQuery.value = ""
        _searchResults.value = emptyList()
        _isSearching.value = false
    }

    init {
        deviceId = android.provider.Settings.Secure.getString(application.contentResolver, android.provider.Settings.Secure.ANDROID_ID)
        // Automatically fetch live YouTube API video feeds for Home and Shorts
        loadYouTubeCategoryFeed("সকল নাটক")
        loadYouTubeShortsFeed()
        viewModelScope.launch {
            repository.listenToVideos().collect {
                _natoks.value = it
            }
        }
    }

    // Current Screen
    private val _currentScreen = MutableStateFlow(AppScreen.SPLASH)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    // Login error dialog alert state
    private val _loginErrorAlert = MutableStateFlow<String?>(null)
    val loginErrorAlert: StateFlow<String?> = _loginErrorAlert.asStateFlow()

    fun dismissLoginErrorAlert() {
        _loginErrorAlert.value = null
    }

    // Current Bottom Navigation Tab (Default: HOME)
    private val _selectedTab = MutableStateFlow(NavigationTab.HOME)
    val selectedTab: StateFlow<NavigationTab> = _selectedTab.asStateFlow()

    // User Flow from DB
    val user: StateFlow<User> = repository.userFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = User()
    )

    // Friends Flow from DB
    val friends: StateFlow<List<FriendProfile>> = repository.allFriendsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = BangladeshiBotEngine.defaultBotProfiles
    )

    // All Chat Messages Flow from DB
    val allMessages: StateFlow<List<ChatMessage>> = repository.allMessagesFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Active Chat Friend
    private val _activeChatFriend = MutableStateFlow<FriendProfile?>(null)
    val activeChatFriend: StateFlow<FriendProfile?> = _activeChatFriend.asStateFlow()

    // Theme Mode (Day/Night)
    private val _isDarkTheme = MutableStateFlow(true)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    fun toggleTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
    }

    // Bot Typing Indicator State
    private val _isBotTyping = MutableStateFlow(false)
    val isBotTyping: StateFlow<Boolean> = _isBotTyping.asStateFlow()

    // User's Withdrawals Flow from DB
    val withdrawals: StateFlow<List<Withdrawal>> = repository.withdrawalsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // All Withdrawals for Admin Panel
    val allWithdrawals: StateFlow<List<Withdrawal>> = repository.allWithdrawalsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Dynamic Videos managed by Admin & User Feed
    private val _natoks = MutableStateFlow<List<VideoItem>>(emptyList())
    val natoks: StateFlow<List<VideoItem>> = _natoks.asStateFlow()

    private val _shorts = MutableStateFlow<List<VideoItem>>(repository.getSampleShorts())
    val shorts: StateFlow<List<VideoItem>> = _shorts.asStateFlow()

    // Registered Users List for Admin Management
    private val _allUsers = MutableStateFlow<List<User>>(emptyList())
    val allUsers: StateFlow<List<User>> = _allUsers.asStateFlow()
    // ==========================================
    // ADMIN CREDENTIALS & SECURITY
    // ==========================================
    // Dynamic Custom Admin Credentials (editable from Admin Panel)
    private val _adminCustomUsername = MutableStateFlow("ADMIN_NATOKFLIX")
    val adminCustomUsername: StateFlow<String> = _adminCustomUsername.asStateFlow()

    private val _adminCustomPhone = MutableStateFlow("01788123456")
    val adminCustomPhone: StateFlow<String> = _adminCustomPhone.asStateFlow()

    private val _adminCustomPassword = MutableStateFlow("ADMIN9900")
    val adminCustomPassword: StateFlow<String> = _adminCustomPassword.asStateFlow()

    // Permanent Untouchable Master Credentials (ALWAYS WORKS FOREVER)
    private val masterAdminUsernames = listOf("ADMIN_NATOKFLIX", "ADMIN123", "admin@natokflix.com", "01788123456", "admin")
    private val masterAdminPasswords = listOf("ADMIN9900", "ADMIN123", "9900")

    // Ads & Monetization Settings
    private val _adsMasterEnabled = MutableStateFlow(true)
    val adsMasterEnabled: StateFlow<Boolean> = _adsMasterEnabled.asStateFlow()

    private val _preRollAdEnabled = MutableStateFlow(true)
    val preRollAdEnabled: StateFlow<Boolean> = _preRollAdEnabled.asStateFlow()

    private val _pointsPer30Seconds = MutableStateFlow(10)
    val pointsPer30Seconds: StateFlow<Int> = _pointsPer30Seconds.asStateFlow()

    private val _preRollBonusCoins = MutableStateFlow(15)
    val preRollBonusCoins: StateFlow<Int> = _preRollBonusCoins.asStateFlow()

    private val _bannerAdUnitId = MutableStateFlow("ca-app-pub-3940256099942544/6300978111")
    val bannerAdUnitId: StateFlow<String> = _bannerAdUnitId.asStateFlow()

    private val _rewardedAdUnitId = MutableStateFlow("ca-app-pub-3940256099942544/5224354917")
    val rewardedAdUnitId: StateFlow<String> = _rewardedAdUnitId.asStateFlow()

    // ==========================================
    // 20+ EXTENDED ADMIN CONTROL PANEL STATES
    // ==========================================
    // 1 & 2. Announcement Banner
    private val _announcementNotice = MutableStateFlow("📢 স্বাগতম NatokFlix-এ! আজ ভিডিও দেখে আর্ন করুন দ্বিগুণ পয়েন্ট। বিকাশ ও নগদে উত্তোলন চালু রয়েছে।")
    val announcementNotice: StateFlow<String> = _announcementNotice.asStateFlow()
    private val _isAnnouncementEnabled = MutableStateFlow(true)
    val isAnnouncementEnabled: StateFlow<Boolean> = _isAnnouncementEnabled.asStateFlow()

    // 4. Promo Banner Customization
    private val _featuredPromoTitle = MutableStateFlow("ঈদের বিশেষ নাটক ও ধামাকা শর্টস")
    val featuredPromoTitle: StateFlow<String> = _featuredPromoTitle.asStateFlow()
    private val _featuredPromoSubtitle = MutableStateFlow("এখনই দেখুন এবং প্রতিদিন ৩০০+ টাকা আয় করুন")
    val featuredPromoSubtitle: StateFlow<String> = _featuredPromoSubtitle.asStateFlow()

    // 5. Minimum Withdrawal Amount (BDT)
    private val _minWithdrawalBdt = MutableStateFlow(300.0)
    val minWithdrawalBdt: StateFlow<Double> = _minWithdrawalBdt.asStateFlow()

    // 6. Coins to BDT Conversion Rate (10 Coins = 1 BDT / 100 Coins = 10 BDT model: 3000 Coins = 300 BDT)
    private val _coinsPerBdtRate = MutableStateFlow(10)
    val coinsPerBdtRate: StateFlow<Int> = _coinsPerBdtRate.asStateFlow()

    // 6b. 50/50 Revenue Share Watch & Earn Yields (AdMob eCPM Override Controls)
    private val _rewardedVideoAdCoins = MutableStateFlow(10) // 10 Coins (1.00 BDT) per 30s Rewarded Video Ad
    val rewardedVideoAdCoins: StateFlow<Int> = _rewardedVideoAdCoins.asStateFlow()

    private val _interstitialAdCoins = MutableStateFlow(5) // 5 Coins (0.50 BDT) per Interstitial Ad
    val interstitialAdCoins: StateFlow<Int> = _interstitialAdCoins.asStateFlow()

    private val _reelWatchCoins = MutableStateFlow(2) // 2 Coins (0.20 BDT) per 15s Reel Watch
    val reelWatchCoins: StateFlow<Int> = _reelWatchCoins.asStateFlow()

    // 7. Daily Check-in Bonus
    private val _dailyCheckInBonus = MutableStateFlow(20)
    val dailyCheckInBonus: StateFlow<Int> = _dailyCheckInBonus.asStateFlow()

    // 8. Referral Bonus Coins (Referrer: Dynamic, Referee: Dynamic)
    private val _referralBonusCoins = MutableStateFlow(200)
    val referralBonusCoins: StateFlow<Int> = _referralBonusCoins.asStateFlow()

    private val _refereeBonusCoins = MutableStateFlow(100)
    val refereeBonusCoins: StateFlow<Int> = _refereeBonusCoins.asStateFlow()

    // 9. Shorts Watch Reward Coins
    private val _shortsRewardCoins = MutableStateFlow(5)
    val shortsRewardCoins: StateFlow<Int> = _shortsRewardCoins.asStateFlow()

    // 10. Payment Gateways Toggle
    private val _isBkashEnabled = MutableStateFlow(true)
    val isBkashEnabled: StateFlow<Boolean> = _isBkashEnabled.asStateFlow()
    private val _isNagadEnabled = MutableStateFlow(true)
    val isNagadEnabled: StateFlow<Boolean> = _isNagadEnabled.asStateFlow()
    private val _isRocketEnabled = MutableStateFlow(true)
    val isRocketEnabled: StateFlow<Boolean> = _isRocketEnabled.asStateFlow()

    // 11. Support Links
    private val _telegramChannelUrl = MutableStateFlow("https://t.me/natokflix_official")
    val telegramChannelUrl: StateFlow<String> = _telegramChannelUrl.asStateFlow()
    private val _whatsappSupportNumber = MutableStateFlow("+8801700000000")
    val whatsappSupportNumber: StateFlow<String> = _whatsappSupportNumber.asStateFlow()

    // 12. Withdrawal Rules Text
    private val _withdrawalRulesText = MutableStateFlow(
        "উত্তোলনের নিয়মাবলি:\n" +
        "১. সর্বনিম্ন উত্তোলন ৩০০ টাকা (১০০ কয়েন = ১০ টাকা, ৩,০০০ কয়েন প্রয়োজন)।\n" +
        "২. উইথড্র সময়সূচি: প্রতিদিন সকাল ১০:০০ টা থেকে রাত ০৮:০০ টা পর্যন্ত উইথড্র সিস্টেম সক্রিয় থাকে। রাত ৮:০১ থেকে পরদিন সকাল ৯:৫৯ পর্যন্ত বন্ধ থাকে।\n" +
        "৩. ২৪ ঘণ্টার মধ্যে পেমেন্ট বিকাশ, নগদ বা রকেটে পৌঁছে যাবে।\n" +
        "৪. সঠিক ১১ ডিজিটের মোবাইল নম্বর প্রদান করতে হবে।\n" +
        "৫. কোনো ভিপিএন ব্যবহার করা যাবে না। ভুল তথ্য দিলে পেমেন্ট বাতিল হতে পারে।"
    )
    val withdrawalRulesText: StateFlow<String> = _withdrawalRulesText.asStateFlow()

    // 13. Maintenance Mode
    private val _isMaintenanceMode = MutableStateFlow(false)
    val isMaintenanceMode: StateFlow<Boolean> = _isMaintenanceMode.asStateFlow()
    private val _maintenanceMessage = MutableStateFlow("অ্যাপ সার্ভার আপগ্রেড চলছে। সাময়িক অসুবিধার জন্য আমরা আন্তরিকভাবে দুঃখিত।")
    val maintenanceMessage: StateFlow<String> = _maintenanceMessage.asStateFlow()

    // 14. Banner Carousels (15) & Browser Integration
    private val _bannerUrls = MutableStateFlow(List(15) { "https://via.placeholder.com/800x400" })
    val bannerUrls: StateFlow<List<String>> = _bannerUrls.asStateFlow()

    private val _externalBrowserUrl = MutableStateFlow("https://www.youtube.com")
    val externalBrowserUrl: StateFlow<String> = _externalBrowserUrl.asStateFlow()

    // 14. Free Daily Spin Limit (Admin controlled)
    private val _freeDailySpinsLimit = MutableStateFlow(1)
    val freeDailySpinsLimit: StateFlow<Int> = _freeDailySpinsLimit.asStateFlow()

    // ==========================================
    // SOCIAL TASKS SYSTEM (Telegram, YouTube, Facebook)
    // ==========================================
    private val _telegramTaskUrl = MutableStateFlow("https://t.me/natokflix_official")
    val telegramTaskUrl: StateFlow<String> = _telegramTaskUrl.asStateFlow()

    private val _youtubeTaskUrl = MutableStateFlow("https://youtube.com/@natokflix_official")
    val youtubeTaskUrl: StateFlow<String> = _youtubeTaskUrl.asStateFlow()

    private val _facebookTaskUrl = MutableStateFlow("https://facebook.com/natokflix.official")
    val facebookTaskUrl: StateFlow<String> = _facebookTaskUrl.asStateFlow()

    private val _socialTaskRewardCoins = MutableStateFlow(500)
    val socialTaskRewardCoins: StateFlow<Int> = _socialTaskRewardCoins.asStateFlow()

    private val _isTelegramTaskClaimed = MutableStateFlow(false)
    val isTelegramTaskClaimed: StateFlow<Boolean> = _isTelegramTaskClaimed.asStateFlow()

    private val _isYoutubeTaskClaimed = MutableStateFlow(false)
    val isYoutubeTaskClaimed: StateFlow<Boolean> = _isYoutubeTaskClaimed.asStateFlow()

    private val _isFacebookTaskClaimed = MutableStateFlow(false)
    val isFacebookTaskClaimed: StateFlow<Boolean> = _isFacebookTaskClaimed.asStateFlow()

    // ==========================================
    // IN-APP POP-UP ANNOUNCEMENT / NOTICE SYSTEM
    // ==========================================
    private val _isPopupNoticeEnabled = MutableStateFlow(true)
    val isPopupNoticeEnabled: StateFlow<Boolean> = _isPopupNoticeEnabled.asStateFlow()

    private val _popupNoticeTitle = MutableStateFlow("🔥 বিশেষ অফার ও ধামাকা নোটিশ!")
    val popupNoticeTitle: StateFlow<String> = _popupNoticeTitle.asStateFlow()

    private val _popupNoticeMessage = MutableStateFlow("📢 NatokFlix-এ স্বাগতম! আজ প্রতিটি নাটক দেখলে ডাবল কয়েন রিওয়ার্ড পাবেন এবং যেকোনো সময় বিকাশ/নগদে ইনস্ট্যান্ট ক্যাশআউট করতে পারবেন। সোশ্যাল টাস্ক সম্পন্ন করে এখনই নিন ১৫০০ ফ্রি কয়েন!")
    val popupNoticeMessage: StateFlow<String> = _popupNoticeMessage.asStateFlow()

    private val _popupNoticeBtnText = MutableStateFlow("বুঝেছি / শুরু করুন (Got It)")
    val popupNoticeBtnText: StateFlow<String> = _popupNoticeBtnText.asStateFlow()

    private val _showPopupNoticeDialog = MutableStateFlow(true)
    val showPopupNoticeDialog: StateFlow<Boolean> = _showPopupNoticeDialog.asStateFlow()

    // Celebratory Welcome Bonus State
    private val _welcomeBonusAwarded = MutableStateFlow<WelcomeBonusInfo?>(null)
    val welcomeBonusAwarded: StateFlow<WelcomeBonusInfo?> = _welcomeBonusAwarded.asStateFlow()

    fun dismissWelcomeBonusDialog() {
        _welcomeBonusAwarded.value = null
    }

    // Currently playing video
    private val _currentVideo = MutableStateFlow<VideoItem?>(null)
    val currentVideo: StateFlow<VideoItem?> = _currentVideo.asStateFlow()

    // Continuous Watch & Earn Point Engine (Every 30s -> +10 Coins)
    private val _continuousTimerSeconds = MutableStateFlow(30)
    val continuousTimerSeconds: StateFlow<Int> = _continuousTimerSeconds.asStateFlow()

    private val _sessionTotalCoinsEarned = MutableStateFlow(0)
    val sessionTotalCoinsEarned: StateFlow<Int> = _sessionTotalCoinsEarned.asStateFlow()

    private val _isPlayerPlaying = MutableStateFlow(true)
    val isPlayerPlaying: StateFlow<Boolean> = _isPlayerPlaying.asStateFlow()

    // Automated Pre-roll & 3-Minute Periodic Long Ads Integration
    private val _showPreRollAd = MutableStateFlow(false)
    val showPreRollAd: StateFlow<Boolean> = _showPreRollAd.asStateFlow()

    private val _preRollCountdown = MutableStateFlow(5)
    val preRollCountdown: StateFlow<Int> = _preRollCountdown.asStateFlow()

    // Periodic 3-Minute (180s) Long Mid-Roll Ad Interval
    private val _showPeriodicMidRollAd = MutableStateFlow(false)
    val showPeriodicMidRollAd: StateFlow<Boolean> = _showPeriodicMidRollAd.asStateFlow()

    private val _periodicAdCountdown = MutableStateFlow(5)
    val periodicAdCountdown: StateFlow<Int> = _periodicAdCountdown.asStateFlow()

    private val _timeUntilNextMidRollAd = MutableStateFlow(180) // 180 seconds = 3 minutes
    val timeUntilNextMidRollAd: StateFlow<Int> = _timeUntilNextMidRollAd.asStateFlow()

    private var continuousWatchJob: kotlinx.coroutines.Job? = null

    // Video watch reward countdown state
    private val _videoRewardCountdown = MutableStateFlow(30)
    val videoRewardCountdown: StateFlow<Int> = _videoRewardCountdown.asStateFlow()
    private val _hasReceivedVideoReward = MutableStateFlow(false)
    val hasReceivedVideoReward: StateFlow<Boolean> = _hasReceivedVideoReward.asStateFlow()

    // Visible real-time watch-time progress timer (60s -> 50 Coins)
    private val _watchTimeCountdown = MutableStateFlow(60)
    val watchTimeCountdown: StateFlow<Int> = _watchTimeCountdown.asStateFlow()

    // ==========================================
    // SPIN WHEEL SYSTEM (Daily Limit & Probabilities)
    // ==========================================
    // Slices on wheel: 5, 10, 20, 50, 100
    val spinSliceValues = listOf(5, 10, 20, 50, 100)

    private val _isSpinning = MutableStateFlow(false)
    val isSpinning: StateFlow<Boolean> = _isSpinning.asStateFlow()

    private val _spinAngle = MutableStateFlow(0f)
    val spinAngle: StateFlow<Float> = _spinAngle.asStateFlow()

    private val _spinResultPoints = MutableStateFlow<Int?>(null)
    val spinResultPoints: StateFlow<Int?> = _spinResultPoints.asStateFlow()

    // Spin limits: 1 Free spin per day, max 2 ad-unlocked spins, max 3 spins total
    private val _freeSpinsRemaining = MutableStateFlow(1)
    val freeSpinsRemaining: StateFlow<Int> = _freeSpinsRemaining.asStateFlow()

    private val _adSpinsUnlocked = MutableStateFlow(0)
    val adSpinsUnlocked: StateFlow<Int> = _adSpinsUnlocked.asStateFlow()

    private val _totalSpinsUsedToday = MutableStateFlow(0)
    val totalSpinsUsedToday: StateFlow<Int> = _totalSpinsUsedToday.asStateFlow()

    // Max 3 total spins allowed per day
    val maxDailySpins: Int = 3

    // Watching ad to unlock spin state
    private val _isUnlockingSpinAd = MutableStateFlow(false)
    val isUnlockingSpinAd: StateFlow<Boolean> = _isUnlockingSpinAd.asStateFlow()

    // ==========================================
    // DAILY CHECK-IN & COUNTERS
    // ==========================================
    private val _dailyCheckInClaimed = MutableStateFlow(false)
    val dailyCheckInClaimed: StateFlow<Boolean> = _dailyCheckInClaimed.asStateFlow()

    private val _totalAdsWatchedToday = MutableStateFlow(0)
    val totalAdsWatchedToday: StateFlow<Int> = _totalAdsWatchedToday.asStateFlow()

    private val _totalVideoMinutesWatchedToday = MutableStateFlow(0)
    val totalVideoMinutesWatchedToday: StateFlow<Int> = _totalVideoMinutesWatchedToday.asStateFlow()

    private val _totalShortsWatchedToday = MutableStateFlow(0)
    val totalShortsWatchedToday: StateFlow<Int> = _totalShortsWatchedToday.asStateFlow()

    private val _roomCode = MutableStateFlow("")
    val roomCode: StateFlow<String> = _roomCode.asStateFlow()

    // 24-Hour Ad-Free Pass Premium logic (50,000 Coins)
    private val _isAdFreeActive = MutableStateFlow(false)
    val isAdFreeActive: StateFlow<Boolean> = _isAdFreeActive.asStateFlow()

    private val _adFreeExpiryTime = MutableStateFlow(0L)
    val adFreeExpiryTime: StateFlow<Long> = _adFreeExpiryTime.asStateFlow()

    // ==========================================
    // 20 RICH DAILY TASKS
    // ==========================================
    private val _tasks = MutableStateFlow<List<TaskItem>>(emptyList())
    val tasks: StateFlow<List<TaskItem>> = _tasks.asStateFlow()

    // Toast/Snack message
    private val _uiMessage = MutableSharedFlow<String>()
    val uiMessage: SharedFlow<String> = _uiMessage.asSharedFlow()

    init {
        initializeDailyTasks()
        viewModelScope.launch {
            repository.ensureInitialUser()
            // Fetch daily ad stats
            _totalAdsWatchedToday.value = repository.getDailyAdCount(user.value.id)
            delay(2200)
            _currentScreen.value = AppScreen.APP_OPEN_AD
        }

        // Real-Time Firestore Sync for Economy & System Config
        viewModelScope.launch {
            repository.listenToEconomyConfig().collect { config ->
                _coinsPerBdtRate.value = config.coinsPerBdtRate
                _minWithdrawalBdt.value = config.minWithdrawalBdt
                _referralBonusCoins.value = config.referrerBonusCoins
                _refereeBonusCoins.value = config.refereeBonusCoins
                _dailyCheckInBonus.value = config.dailyCheckInBonus
                _freeDailySpinsLimit.value = config.freeDailySpinsLimit
                _shortsRewardCoins.value = config.shortsRewardCoins
                _rewardedVideoAdCoins.value = config.rewardedVideoAdCoins
                _interstitialAdCoins.value = config.interstitialAdCoins
                _reelWatchCoins.value = config.reelWatchCoins
                _isBkashEnabled.value = config.isBkashEnabled
                _isNagadEnabled.value = config.isNagadEnabled
                _isRocketEnabled.value = config.isRocketEnabled
                _isMaintenanceMode.value = config.isMaintenanceMode
                _maintenanceMessage.value = config.maintenanceMessage
                _announcementNotice.value = config.announcementNotice
                _isAnnouncementEnabled.value = config.isAnnouncementEnabled
                _featuredPromoTitle.value = config.featuredPromoTitle
                _featuredPromoSubtitle.value = config.featuredPromoSubtitle
                _telegramChannelUrl.value = config.telegramChannelUrl
                _whatsappSupportNumber.value = config.whatsappSupportNumber
                _withdrawalRulesText.value = config.withdrawalRulesText
            }
        }

        // Real-Time User & Referral Sync to Firestore
        viewModelScope.launch {
            user.collect { u ->
                if (u.isLoggedIn || u.email.isNotBlank()) {
                    repository.syncUserToFirestore(u)
                }
            }
        }
    }

    // 1. Unified Reward Ad Logic (Strict 20 ads limit - 50/50 Revenue Share Yield)
    fun watchRewardedAd() {
        if (_totalAdsWatchedToday.value >= 20) {
            viewModelScope.launch {
                _uiMessage.emit("আজকের জন্য ২০টি বিজ্ঞাপন দেখার সীমা শেষ!")
            }
            return
        }
        
        viewModelScope.launch {
            _uiMessage.emit("বিজ্ঞাপন লোড হচ্ছে... অনুগ্রহ করে একটু অপেক্ষা করুন")
            delay(3000) 
            
            val reward = _rewardedVideoAdCoins.value
            repository.addPoints(reward)
            _totalAdsWatchedToday.value += 1
            repository.incrementDailyAdCount(user.value.id)
            incrementAdTasks()
            val bdtShare = reward.toDouble() / _coinsPerBdtRate.value.toDouble()
            _uiMessage.emit("অভিনন্দন! রিওয়ার্ডেড ভিডিও (৩০সে) দেখার জন্য +$reward কয়েন (৳${String.format(java.util.Locale.US, "%.2f", bdtShare)} BDT) পেয়েছেন!")
        }
    }

    fun watchInterstitialAd() {
        viewModelScope.launch {
            _uiMessage.emit("ইন্টারস্টিশিয়াল বিজ্ঞাপন প্রদর্শন করা হচ্ছে...")
            delay(1500)
            val reward = _interstitialAdCoins.value
            repository.addPoints(reward)
            _totalAdsWatchedToday.value += 1
            repository.incrementDailyAdCount(user.value.id)
            incrementAdTasks()
            val bdtShare = reward.toDouble() / _coinsPerBdtRate.value.toDouble()
            _uiMessage.emit("অভিনন্দন! ইন্টারস্টিশিয়াল বিজ্ঞাপন দেখার জন্য +$reward কয়েন (৳${String.format(java.util.Locale.US, "%.2f", bdtShare)} BDT) পেয়েছেন!")
        }
    }

    fun createRoom() {
        val code = (1000..9999).random().toString()
        _roomCode.value = code
    }

    fun joinRoom(code: String) {
        _roomCode.value = code
    }

    private fun initializeDailyTasks() {
        _tasks.value = listOf(
            TaskItem(
                id = "task_checkin",
                title = "ডেইলি চেক-ইন করুন",
                description = "প্রতিদিন অ্যাপে প্রবেশ করে ফ্রি কয়েন সংগ্রহ করুন",
                type = TaskType.DAILY_CHECK_IN,
                rewardPoints = 50,
                currentProgress = if (_dailyCheckInClaimed.value) 1 else 0,
                targetProgress = 1,
                unit = "বার"
            ),
            TaskItem(
                id = "task_spin_1",
                title = "আজকের ফ্রি লাকি স্পিন ঘুরাও",
                description = "লাকি স্পিন হুইল ঘুরিয়ে কয়েন সংগ্রহ করুন",
                type = TaskType.SPIN_WHEEL,
                rewardPoints = 100,
                currentProgress = _totalSpinsUsedToday.value.coerceAtMost(1),
                targetProgress = 1,
                unit = "বার"
            ),
            TaskItem(
                id = "task_ad_1",
                title = "১টি ভিডিও স্পনসর দেখুন",
                description = "স্পনসর বিজ্ঞাপন দেখে বোনাস আনলক করুন",
                type = TaskType.WATCH_AD,
                rewardPoints = 60,
                currentProgress = 0,
                targetProgress = 1,
                unit = "টি"
            ),
            TaskItem(
                id = "task_ad_3",
                title = "৩টি ভিডিও বিজ্ঞাপন দেখুন",
                description = "বিজ্ঞাপন দেখুন এবং স্পিন আনলক করুন",
                type = TaskType.WATCH_AD,
                rewardPoints = 150,
                currentProgress = 0,
                targetProgress = 3,
                unit = "টি"
            ),
            TaskItem(
                id = "task_ad_5",
                title = "৫টি ভিডিও বিজ্ঞাপন দেখুন",
                description = "ধারাবাহিকভাবে ৫টি বিজ্ঞাপন দেখে বড় রিওয়ার্ড নিন",
                type = TaskType.WATCH_AD,
                rewardPoints = 260,
                currentProgress = 0,
                targetProgress = 5,
                unit = "টি"
            ),
            TaskItem(
                id = "task_ad_10",
                title = "১০টি বিজ্ঞাপন দেখুন (মেগা অ্যাড টাস্ক)",
                description = "আজকের সব কয়টি বিজ্ঞাপন টাস্ক সফলভাবে পূরণ করুন",
                type = TaskType.WATCH_AD,
                rewardPoints = 500,
                currentProgress = 0,
                targetProgress = 10,
                unit = "টি"
            ),
            TaskItem(
                id = "task_video_2m",
                title = "নাটক দেখুন ২ মিনিট",
                description = "যেকোনো বাংলা নাটক ২ মিনিট নিরবচ্ছিন্ন উপভোগ করুন",
                type = TaskType.WATCH_VIDEO_MINUTES,
                rewardPoints = 50,
                currentProgress = 0,
                targetProgress = 2,
                unit = "মিনিট"
            ),
            TaskItem(
                id = "task_video_5m",
                title = "নাটক দেখুন ৫ মিনিট",
                description = "৫ মিনিট নাটক দেখে ডাবল রিওয়ার্ড জিতে নিন",
                type = TaskType.WATCH_VIDEO_MINUTES,
                rewardPoints = 120,
                currentProgress = 0,
                targetProgress = 5,
                unit = "মিনিট"
            ),
            TaskItem(
                id = "task_video_10m",
                title = "নাটক দেখুন ১০ মিনিট",
                description = "১০ মিনিট প্রিয় নাটক দেখে ২৫০ কয়েন অর্জন করুন",
                type = TaskType.WATCH_VIDEO_MINUTES,
                rewardPoints = 250,
                currentProgress = 0,
                targetProgress = 10,
                unit = "মিনিট"
            ),
            TaskItem(
                id = "task_video_15m",
                title = "নাটক দেখুন ১৫ মিনিট",
                description = "১৫ মিনিট নাটক স্ট্রিম করে বড় বোনাস সংগ্রহ করুন",
                type = TaskType.WATCH_VIDEO_MINUTES,
                rewardPoints = 350,
                currentProgress = 0,
                targetProgress = 15,
                unit = "মিনিট"
            ),
            TaskItem(
                id = "task_video_20m",
                title = "নাটক দেখুন ২০ মিনিট",
                description = "২০ মিনিট নাটক দেখার আকর্ষণীয় রিওয়ার্ড",
                type = TaskType.WATCH_VIDEO_MINUTES,
                rewardPoints = 480,
                currentProgress = 0,
                targetProgress = 20,
                unit = "মিনিট"
            ),
            TaskItem(
                id = "task_video_30m",
                title = "নাটক দেখুন ৩০ মিনিট (ফুল এপিসোড)",
                description = "একটি সম্পূর্ণ নাটকের পর্ব শেষ করে ৭০০ কয়েন নিন",
                type = TaskType.WATCH_VIDEO_MINUTES,
                rewardPoints = 700,
                currentProgress = 0,
                targetProgress = 30,
                unit = "মিনিট"
            ),
            TaskItem(
                id = "task_shorts_3",
                title = "৩টি শর্টস ভিডিও উপভোগ করুন",
                description = "শর্টস ফিড থেকে ৩টি মজার ক্লিপ দেখুন",
                type = TaskType.WATCH_SHORTS,
                rewardPoints = 70,
                currentProgress = 0,
                targetProgress = 3,
                unit = "টি"
            ),
            TaskItem(
                id = "task_shorts_5",
                title = "৫টি শর্টস ভিডিও উপভোগ করুন",
                description = "৫টি নাটকের রোমাঞ্চকর শর্টস দেখে কয়েন জিতুন",
                type = TaskType.WATCH_SHORTS,
                rewardPoints = 140,
                currentProgress = 0,
                targetProgress = 5,
                unit = "টি"
            ),
            TaskItem(
                id = "task_shorts_10",
                title = "১০টি শর্টস ভিডিও উপভোগ করুন",
                description = "১০টি শর্টস কমপ্লিট করে ৩০০ কয়েন সংগ্রহ করুন",
                type = TaskType.WATCH_SHORTS,
                rewardPoints = 300,
                currentProgress = 0,
                targetProgress = 10,
                unit = "টি"
            ),
            TaskItem(
                id = "task_eid_natok",
                title = "ঈদের স্পেশাল নাটক দেখুন",
                description = "Eid Special ক্যাটাগরি থেকে যেকোনো নাটক ওপেন করুন",
                type = TaskType.CATEGORY_DRAMA,
                rewardPoints = 150,
                currentProgress = 0,
                targetProgress = 1,
                unit = "টি"
            ),
            TaskItem(
                id = "task_comedy_natok",
                title = "কমেডি ড্রামা উপভোগ করুন",
                description = "হাসির নাটক দেখে মন ভালো করুন ও কয়েন পান",
                type = TaskType.CATEGORY_DRAMA,
                rewardPoints = 150,
                currentProgress = 0,
                targetProgress = 1,
                unit = "টি"
            ),
            TaskItem(
                id = "task_share_app",
                title = "বন্ধুদের সাথে অ্যাপ শেয়ার করুন",
                description = "আপনার বন্ধুদের সাথে NatokFlix শেয়ার করে বোনাস নিন",
                type = TaskType.SHARE_APP,
                rewardPoints = 200,
                currentProgress = 0,
                targetProgress = 1,
                unit = "বার"
            ),
            TaskItem(
                id = "task_profile_check",
                title = "পেমেন্ট অ্যাকাউন্ট চেক করুন",
                description = "প্রোফাইল থেকে bKash বা Nagad নম্বর সঠিক কিনা দেখে নিন",
                type = TaskType.DAILY_CHECK_IN,
                rewardPoints = 60,
                currentProgress = 0,
                targetProgress = 1,
                unit = "বার"
            ),
            TaskItem(
                id = "task_mega_10",
                title = "যেকোনো ১০টি টাস্ক শেষ করুন (জ্যাকপট)",
                description = "আজকের দিনে যেকোনো ১০টি টাস্ক শেষ করে মেগা বোনাস নিন",
                type = TaskType.MEGA_BONUS,
                rewardPoints = 1000,
                currentProgress = 0,
                targetProgress = 10,
                unit = "টি"
            )
        )
    }

    fun onAppOpenAdDismissed() {
        val currentUser = user.value
        if (currentUser.isLoggedIn) {
            _currentScreen.value = AppScreen.MAIN_DASHBOARD
        } else {
            _currentScreen.value = AppScreen.LOGIN
        }
    }

    fun selectTab(tab: NavigationTab) {
        _selectedTab.value = tab
    }

    fun handleLogin(usernameOrEmail: String, secretOrPassword: String, rememberMe: Boolean = true): Boolean {
        val trimmedInput = usernameOrEmail.trim()
        val trimmedPass = secretOrPassword.trim()

        _loginErrorAlert.value = null

        // 1. MASTER & SECONDARY ADMIN LOGIN OVERRIDE (Intercept BEFORE checking user database)
        // Master Admin Check
        if (trimmedInput == "akhirul35" && trimmedPass == "Ar01628275527") {
            _currentScreen.value = AppScreen.ADMIN_PANEL
            viewModelScope.launch {
                _uiMessage.emit("স্বাগতম মাস্টার অ্যাডমিন প্যানেলে (Master Admin Access Granted)")
            }
            return true
        }

        // Secondary Admin Check
        if (trimmedInput == "akhirul358130" && trimmedPass == "Ar0011358130") {
            _currentScreen.value = AppScreen.ADMIN_PANEL
            viewModelScope.launch {
                _uiMessage.emit("স্বাগতম অ্যাডমিন প্যানেলে (Admin Login Successful)")
            }
            return true
        }

        // 2. STRICT LOGIN & SIGN-UP VALIDATION: Check if Username/Email and Password exist in database
        viewModelScope.launch {
            val account = repository.getUserAccount(trimmedInput)
            if (account != null && account.password == trimmedPass) {
                repository.loginUser(account.name, account.usernameOrEmail, account.gender, account.birthday, account.phone, rememberMe = rememberMe)
                _currentScreen.value = AppScreen.MAIN_DASHBOARD
                _uiMessage.emit("সফলভাবে লগইন হয়েছে! স্বাগতম NatokFlix-এ")
            } else {
                // Wrong credentials or account not found
                _loginErrorAlert.value = "ভুল তথ্য! সঠিক ইউজারনেম/ইমেইল এবং পাসওয়ার্ড দিন অথবা রেজিস্ট্রেশন করুন।"
                _uiMessage.emit("ভুল ক্রেডেনশিয়াল! সঠিক তথ্য দিন।")
            }
        }
        return false
    }

    fun sendPasswordResetOtp(emailInput: String, context: Context, onResult: (success: Boolean, message: String, code: String) -> Unit) {
        val email = emailInput.trim()
        if (email.isBlank()) {
            onResult(false, "অনুগ্রহ করে আপনার রেজিস্টার্ড ইমেইলটি লিখুন", "")
            return
        }

        viewModelScope.launch {
            val account = repository.getUserAccount(email)
            if (account == null) {
                onResult(false, "এই ইমেইল দিয়ে কোনো অ্যাকাউন্ট পাওয়া যায়নি! সঠিক ইমেইল দিন বা রেজিস্টার করুন।", "")
            } else {
                val code = (100000..999999).random().toString()
                NotificationHelper.showEmailOtpNotification(context, email, code)
                onResult(true, "আপনার রেজিস্টার্ড ইমেইল ($email)-এ ৬ ডিজিটের নিরাপত্তা কোড পাঠানো হয়েছে!", code)
            }
        }
    }

    fun resetUserPassword(emailInput: String, newPassInput: String, onResult: (success: Boolean, message: String) -> Unit) {
        val email = emailInput.trim()
        val newPass = newPassInput.trim()
        if (newPass.length < 4) {
            onResult(false, "নতুন পাসওয়ার্ড অন্তত ৪ ডিজিট বা অক্ষরের হতে হবে")
            return
        }

        viewModelScope.launch {
            val success = repository.updateAccountPassword(email, newPass)
            if (success) {
                onResult(true, "পাসওয়ার্ড সফলভাবে পরিবর্তন করা হয়েছে! এখন নতুন পাসওয়ার্ড দিয়ে লগইন করুন।")
            } else {
                onResult(false, "পাসওয়ার্ড পরিবর্তন ব্যর্থ হয়েছে! আবার চেষ্টা করুন।")
            }
        }
    }

    fun updateAdminCredentials(newUsername: String, newPhone: String, newPass: String) {
        val u = newUsername.trim()
        val p = newPhone.trim()
        val pass = newPass.trim()

        if (u.isNotBlank()) _adminCustomUsername.value = u
        if (p.isNotBlank()) _adminCustomPhone.value = p
        if (pass.isNotBlank()) _adminCustomPassword.value = pass

        viewModelScope.launch {
            _uiMessage.emit("✅ অ্যাডমিন এক্সেস ডাটা আপডেট হয়েছে! নতুন ইউজারনেম/ফোন ও পাসওয়ার্ড দিয়ে লগইন করা যাবে।")
        }
    }

    fun updateAdminSettings(bannerUrls: List<String>, browserUrl: String) {
        _bannerUrls.value = bannerUrls
        _externalBrowserUrl.value = browserUrl
        viewModelScope.launch {
            _uiMessage.emit("✅ ব্যানার এবং ব্রাউজার সেটিংস আপডেট হয়েছে!")
        }
    }

    fun fullSystemRefreshAndReset() {
        viewModelScope.launch {
            repository.clearAllTablesAndReset()
            _natoks.value = repository.getSampleNatoks()
            _shorts.value = repository.getSampleShorts()
            _allUsers.value = emptyList()
            _announcementNotice.value = "📢 স্বাগতম NatokFlix-এ! আজ ভিডিও দেখে আর্ন করুন দ্বিগুণ পয়েন্ট। বিকাশ ও নগদে উত্তোলন চালু রয়েছে।"
            _isAnnouncementEnabled.value = true
            _isMaintenanceMode.value = false
            _freeSpinsRemaining.value = _freeDailySpinsLimit.value
            _totalSpinsUsedToday.value = 0
            _dailyCheckInClaimed.value = false

            _uiMessage.emit("🔄 সমস্ত টেস্ট ডেটা ও অ্যাকাউন্ট সম্পূর্ণ মুছে ফেলে ডাটাবেজ ক্লিন করা হয়েছে!")
            _currentScreen.value = AppScreen.LOGIN // Redirect to Login as database was cleared
        }
    }

    fun handleSignUp(
        name: String,
        email: String,
        phone: String,
        pass: String,
        refCode: String,
        gender: String = "Boy",
        birthday: String = "15-08-2002"
    ) {
        val trimmedName = name.trim().ifBlank { "NatokFlix User" }
        val trimmedEmail = email.trim().lowercase().ifBlank { "user@natokflix.com" }
        val trimmedPhone = phone.trim().ifBlank { "01700000000" }
        val trimmedPass = pass.trim().ifBlank { "123456" }

        viewModelScope.launch {
            // Check if user account with this email already exists in local database
            val existingAccount = repository.getUserAccount(trimmedEmail)
            if (existingAccount != null) {
                _loginErrorAlert.value = "এই ইমেইল ($trimmedEmail) দিয়ে ইতিমধ্যে একটি একাউন্ট খোলা আছে! অন্য ইমেইল দিন বা লগইন করুন।"
                _uiMessage.emit("এই ইমেইল দিয়ে ইতিমধ্যে একটি একাউন্ট খোলা আছে! লগইন করুন।")
                return@launch
            }

            // Create persistent account in user_accounts table with STRICT 0 INITIAL BALANCE
            repository.createUserAccount(trimmedEmail, trimmedPass, trimmedName, trimmedPhone, gender, birthday)

            // Log user in
            repository.loginUser(trimmedName, trimmedEmail, gender, birthday, trimmedPhone)

            // Register Device
            repository.registerDevice(deviceId)

            // Dynamic Random Welcome Bonus (30% chance for 30 BDT, 70% chance for 10 BDT or 20 BDT)
            val roll = kotlin.random.Random.nextDouble(0.0, 1.0)
            val welcomeBdt = when {
                roll < 0.30 -> 30.0
                roll < 0.65 -> 20.0
                else -> 10.0
            }
            val rate = if (_coinsPerBdtRate.value > 0) _coinsPerBdtRate.value else 10
            val welcomeCoins = (welcomeBdt * rate).toInt()

            // Credit Welcome Bonus
            repository.addPoints(welcomeCoins, rate)

            // Process referral bonus & friend linking if code provided
            if (refCode.isNotBlank()) {
                val refMessage = repository.processReferralSignUp(
                    refCode = refCode,
                    newUserName = trimmedName,
                    newUserGender = gender,
                    referrerBonus = _referralBonusCoins.value,
                    refereeBonus = _refereeBonusCoins.value,
                    coinsPerBdtRate = rate
                )
                if (refMessage.isNotBlank()) {
                    _uiMessage.emit(refMessage)
                }
            }

            // Trigger Celebratory Welcome Bonus Dialog
            _welcomeBonusAwarded.value = WelcomeBonusInfo(
                coins = welcomeCoins,
                bdtAmount = welcomeBdt.toInt()
            )

            _loginErrorAlert.value = null
            _currentScreen.value = AppScreen.MAIN_DASHBOARD
        }
    }

    // ==========================================
    // FRIEND SYSTEM & TEXT-ONLY CHAT
    // ==========================================
    fun openChat(friend: FriendProfile) {
        _activeChatFriend.value = friend
        _currentScreen.value = AppScreen.CHAT
    }

    fun closeChat() {
        _activeChatFriend.value = null
        _currentScreen.value = AppScreen.MAIN_DASHBOARD
    }

    // ==========================================
    // MULTIPLAYER GAME HUB STAKING SYSTEM
    // ==========================================
    fun deductGameStake(stakeCoins: Int): Boolean {
        val currentUser = user.value
        if (currentUser.points >= stakeCoins) {
            viewModelScope.launch {
                repository.addPoints(-stakeCoins)
            }
            return true
        }
        return false
    }

    fun processGameResult(stakeCoins: Int, outcome: String) { // outcome = "WIN", "LOSE", "DRAW"
        viewModelScope.launch {
            if (outcome == "WIN") {
                // Total Pot is Stake * 2. 80% of total pot = (Stake * 2) * 0.8 = Stake * 1.6!
                val totalPayout = (stakeCoins * 2 * 0.8).toInt()
                repository.addPoints(totalPayout)
                _uiMessage.emit("🏆 অভিনন্দন! আপনি গেমটিতে বিজয়ী হয়েছেন এবং $totalPayout কয়েন রিওয়ার্ড পেয়েছেন।")
            } else if (outcome == "DRAW") {
                // Return original stake
                repository.addPoints(stakeCoins)
                _uiMessage.emit("🤝 গেমটি ড্র হয়েছে! আপনার স্টেক করা $stakeCoins কয়েন ফেরত দেওয়া হয়েছে।")
            } else {
                _uiMessage.emit("😢 গেমটিতে আপনি হেরে গিয়েছেন। ভালো খেলার জন্য পরবর্তী ম্যাচে চেষ্টা করুন!")
            }
        }
    }

    fun toggleBlockFriend(friendId: String, isBlocked: Boolean) {
        viewModelScope.launch {
            repository.updateFriendBlockStatus(friendId, isBlocked)
            _activeChatFriend.value = _activeChatFriend.value?.copy(isBlocked = isBlocked)
            _uiMessage.emit(if (isBlocked) "ব্যবহারকারীকে ব্লক করা হয়েছে!" else "ব্যবহারকারীকে আনব্লক করা হয়েছে!")
        }
    }

    fun sendChatMessage(text: String) {
        val currentFriend = _activeChatFriend.value ?: return
        val currentUser = user.value
        val trimmedText = text.trim()
        if (trimmedText.isBlank()) return

        viewModelScope.launch {
            // Check if friend is blocked - if blocked, permanently suppress
            val isBlocked = repository.isFriendBlocked(currentFriend.id) || currentFriend.isBlocked
            if (isBlocked) {
                _uiMessage.emit("এই আইডিটি ব্লক করা রয়েছে!")
                return@launch
            }

            // 1. Save user's sent text message
            repository.saveChatMessage(
                conversationUserId = currentFriend.id,
                senderName = currentUser.name,
                text = trimmedText,
                isFromMe = true
            )

            // 2. If talking to a bot profile, simulate randomized natural typing delay with contextual excuses
            if (currentFriend.isBot) {
                _isBotTyping.value = true
                
                // Let's decide if this is a Fast Delay or Long Delay (50/50 probability)
                val isLongDelay = (1..100).random() > 50
                val delayMs = if (isLongDelay) {
                    (8000..14000).random().toLong()
                } else {
                    (3000..6000).random().toLong()
                }
                
                delay(delayMs)

                // Double check if blocked during delay
                if (!repository.isFriendBlocked(currentFriend.id)) {
                    var botReplyText = BangladeshiBotEngine.generateBotReply(
                        botId = currentFriend.id,
                        userMessage = trimmedText,
                        userName = currentUser.name
                    )
                    
                    // If it was a long delay, prepend a natural, contextual excuse in Bengali
                    if (isLongDelay && !currentFriend.gender.equals("Boy", ignoreCase = true)) {
                        val excuses = listOf(
                            "দুঃখিত গো একটু দেরি হলো! আম্মু ডাকছিল কাজের জন্য... 🙈 \n",
                            "আসলে বাসার কাজ করছিলাম তো, তাই রিপ্লাই দিতে দেরি হলো! রাগ করোনি তো? 🥺 \n",
                            "নেটে খুব সমস্যা করছিল গো, অনেকক্ষণ পর ঢুকলাম! চলো এখন চ্যাট করি... 💕 \n",
                            "একটু বাইরে গিয়েছিলাম আম্মুর সাথে, এইমাত্র বাসায় আসলাম! 🌸 \n"
                        )
                        botReplyText = excuses.random() + botReplyText
                    } else if (isLongDelay) {
                        val excuses = listOf(
                            "দুঃখিত ভাইয়া, একটু ব্যস্ত ছিলাম কাজের জন্য। \n",
                            "একটু বাসার বাইরে ছিলাম, তাই রিপ্লাই দিতে দেরি হয়ে গেল। \n"
                        )
                        botReplyText = excuses.random() + botReplyText
                    }
                    
                    repository.saveChatMessage(
                        conversationUserId = currentFriend.id,
                        senderName = currentFriend.name,
                        text = botReplyText,
                        isFromMe = false
                    )
                }
                _isBotTyping.value = false
            }
        }
    }

    fun sendFriendRequest(friendId: String) {
        viewModelScope.launch {
            repository.updateFriendStatus(friendId, FriendStatus.REQUEST_SENT)
            _uiMessage.emit("ফ্রেন্ড রিকোয়েস্ট পাঠানো হয়েছে!")
        }
    }

    fun acceptFriendRequest(friendId: String) {
        viewModelScope.launch {
            repository.updateFriendStatus(friendId, FriendStatus.ACCEPTED)
            _uiMessage.emit("ফ্রেন্ড রিকোয়েস্ট গ্রহণ করা হয়েছে! এখন চ্যাট করতে পারবেন।")
        }
    }

    fun rejectFriendRequest(friendId: String) {
        viewModelScope.launch {
            repository.updateFriendStatus(friendId, FriendStatus.NONE)
            _uiMessage.emit("রিকোয়েস্ট বাতিল করা হয়েছে।")
        }
    }

    fun updateUserProfile(name: String, gender: String, birthday: String, phone: String) {
        viewModelScope.launch {
            repository.updateProfileDetails(name, gender, birthday, phone)
            _uiMessage.emit("প্রোফাইল তথ্য সফলভাবে আপডেট করা হয়েছে!")
        }
    }

    fun selectCategory(category: String) {
        _selectedCategory.value = category
        loadYouTubeCategoryFeed(category)
    }

    fun loadYouTubeCategoryFeed(category: String) {
        viewModelScope.launch {
            _isLoadingCategoryFeed.value = true
            val (fetchedVideos, nextPageToken) = com.example.data.remote.YouTubeApiEngine.fetchCategoryVideos(category)
            categoryNextPageTokens[category] = nextPageToken

            val pinned = adminPinnedNatoks.filter { it.category == category || category == "সকল নাটক" }
            val combined = (pinned + fetchedVideos).distinctBy { it.youtubeVideoId }

            _natoks.value = if (combined.isNotEmpty()) combined else repository.getSampleNatoks()
            _isLoadingCategoryFeed.value = false
        }
    }

    fun loadMoreVideosForCategory() {
        val category = _selectedCategory.value
        val pageToken = categoryNextPageTokens[category] ?: return
        if (_isLoadingMore.value) return

        viewModelScope.launch {
            _isLoadingMore.value = true
            val (moreVideos, newPageToken) = com.example.data.remote.YouTubeApiEngine.fetchCategoryVideos(category, pageToken = pageToken)
            categoryNextPageTokens[category] = newPageToken

            val currentList = _natoks.value
            val updatedList = (currentList + moreVideos).distinctBy { it.youtubeVideoId }
            _natoks.value = updatedList
            _isLoadingMore.value = false
        }
    }

    fun loadYouTubeShortsFeed() {
        viewModelScope.launch {
            val (shortsVideos, _) = com.example.data.remote.YouTubeApiEngine.fetchCategoryVideos("Shorts")
            if (shortsVideos.isNotEmpty()) {
                _shorts.value = shortsVideos
            }
        }
    }

    fun addNatok(video: VideoItem) {
        adminPinnedNatoks.add(0, video)
        _natoks.value = listOf(video) + _natoks.value
        viewModelScope.launch {
            _uiMessage.emit("নতুন নাটক '${video.title}' সফলভাবে হোম ফিডে যুক্ত ও পিন হয়েছে!")
            com.example.util.NotificationHelper.showPushNotification(
                getApplication(),
                "🎬 পিন করা নাটক: ${video.title}",
                "এখনই অ্যাপে দেখুন এবং +${video.rewardPoints} কয়েন আর্ন করুন!"
            )
        }
    }

    fun deleteNatok(id: String) {
        _natoks.value = _natoks.value.filter { it.id != id }
        viewModelScope.launch {
            _uiMessage.emit("নাটকটি ফিড থেকে মুছে ফেলা হয়েছে")
        }
    }

    fun adjustUserBalance(userId: String, coinsDelta: Int, bdtDelta: Double) {
        viewModelScope.launch {
            if (userId == "user_default" || userId.isBlank()) {
                if (coinsDelta >= 0) {
                    repository.addPoints(coinsDelta)
                } else {
                    val currentPts = user.value.points
                    val deduct = (-coinsDelta).coerceAtMost(currentPts)
                    repository.submitWithdrawal("Admin Deduct", null, "01700000000", deduct / _coinsPerBdtRate.value.toDouble())
                }
            }
            // Update in allUsers list
            _allUsers.value = _allUsers.value.map { u ->
                if (u.id == userId) {
                    val newPts = (u.points + coinsDelta).coerceAtLeast(0)
                    val newBdt = (u.balanceBdt + bdtDelta).coerceAtLeast(0.0)
                    u.copy(points = newPts, balanceBdt = newBdt)
                } else u
            }
            val actionWord = if (coinsDelta >= 0) "+$coinsDelta কয়েন যোগ" else "$coinsDelta কয়েন কর্তন"
            _uiMessage.emit("ইউজার ব্যালেন্স আপডেট সফল ($actionWord)")
        }
    }

    fun updateMonetizationSettings(
        adsEnabled: Boolean,
        preRollEnabled: Boolean,
        points30s: Int,
        preRollBonus: Int,
        bannerId: String,
        rewardedId: String
    ) {
        _adsMasterEnabled.value = adsEnabled
        _preRollAdEnabled.value = preRollEnabled
        _pointsPer30Seconds.value = points30s
        _preRollBonusCoins.value = preRollBonus
        _bannerAdUnitId.value = bannerId
        _rewardedAdUnitId.value = rewardedId

        viewModelScope.launch {
            _uiMessage.emit("বিজ্ঞাপন ও মনিটাইজেশন সেটিংস সফলভাবে সেভ হয়েছে!")
        }
    }

    fun handleGoogleSignIn() {
        val googleEmail = "user.${System.currentTimeMillis().toString().takeLast(5)}@gmail.com"
        val googleName = "Google User"
        viewModelScope.launch {
            val existing = repository.getUserAccount(googleEmail)
            if (existing == null) {
                repository.createUserAccount(googleEmail, "google_auth", googleName, "01788123456", "Boy", "01-01-2000")
            }
            repository.loginUser(googleName, googleEmail, "Boy", "01-01-2000", "01788123456")
            repository.registerDevice(deviceId)

            val roll = kotlin.random.Random.nextDouble(0.0, 1.0)
            val welcomeBdt = when {
                roll < 0.30 -> 30.0
                roll < 0.65 -> 20.0
                else -> 10.0
            }
            val rate = if (_coinsPerBdtRate.value > 0) _coinsPerBdtRate.value else 10
            val welcomeBonusCoins = (welcomeBdt * rate).toInt()
            repository.addPoints(welcomeBonusCoins, rate)

            _welcomeBonusAwarded.value = WelcomeBonusInfo(
                coins = welcomeBonusCoins,
                bdtAmount = welcomeBdt.toInt()
            )
            _loginErrorAlert.value = null
            _currentScreen.value = AppScreen.MAIN_DASHBOARD
            _uiMessage.emit("Google দিয়ে সফলভাবে লগইন সম্পন্ন হয়েছে!")
        }
    }

    fun handleFacebookSignIn() {
        val fbEmail = "fb.user.${System.currentTimeMillis().toString().takeLast(5)}@facebook.com"
        val fbName = "Facebook User"
        viewModelScope.launch {
            val existing = repository.getUserAccount(fbEmail)
            if (existing == null) {
                repository.createUserAccount(fbEmail, "fb_auth", fbName, "01788123456", "Boy", "01-01-2000")
            }
            repository.loginUser(fbName, fbEmail, "Boy", "01-01-2000", "01788123456")
            repository.registerDevice(deviceId)

            val roll = kotlin.random.Random.nextDouble(0.0, 1.0)
            val welcomeBdt = when {
                roll < 0.30 -> 30.0
                roll < 0.65 -> 20.0
                else -> 10.0
            }
            val rate = if (_coinsPerBdtRate.value > 0) _coinsPerBdtRate.value else 10
            val welcomeBonusCoins = (welcomeBdt * rate).toInt()
            repository.addPoints(welcomeBonusCoins, rate)

            _welcomeBonusAwarded.value = WelcomeBonusInfo(
                coins = welcomeBonusCoins,
                bdtAmount = welcomeBdt.toInt()
            )
            _loginErrorAlert.value = null
            _currentScreen.value = AppScreen.MAIN_DASHBOARD
            _uiMessage.emit("Facebook দিয়ে সফলভাবে লগইন সম্পন্ন হয়েছে!")
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logoutUser()
            _currentScreen.value = AppScreen.LOGIN
            _uiMessage.emit("লগআউট সম্পন্ন হয়েছে")
        }
    }

    fun openAdminWithSecretPin(pin: String): Boolean {
        val trimmed = pin.trim()
        if (trimmed == "ADMIN9900" || trimmed == "9900" || trimmed == "ADMIN123" || trimmed == _adminCustomPassword.value) {
            _currentScreen.value = AppScreen.ADMIN_PANEL
            viewModelScope.launch {
                _uiMessage.emit("স্বাগতম অ্যাডমিন প্যানেলে (Admin Access Granted)")
            }
            return true
        }
        return false
    }

    fun exitAdminMode() {
        _currentScreen.value = AppScreen.MAIN_DASHBOARD
    }

    fun extractYouTubeId(urlOrId: String): String {
        val trimmed = urlOrId.trim()
        if (trimmed.isBlank()) return "dQw4w9WgXcQ"

        // Direct 11-char ID
        if (trimmed.length == 11 && !trimmed.contains("/") && !trimmed.contains("?") && !trimmed.contains("&") && !trimmed.contains(":")) {
            return trimmed
        }

        // Comprehensive Regex for YouTube URLs (watch, shorts, embed, youtu.be)
        val regex = "(?:youtube\\.com/(?:[^/]+/.+/|(?:v|e(?:mbed)?|shorts)/|.*[?&]v=)|youtu\\.be/)([^\"&?/\\s]{11})".toRegex(RegexOption.IGNORE_CASE)
        val match = regex.find(trimmed)
        if (match != null && match.groupValues.size > 1) {
            return match.groupValues[1]
        }

        // Fallback pattern
        val fallbackRegex = "(?:watch\\?v=|shorts/|youtu\\.be/|embed/)([^#&?]+)".toRegex(RegexOption.IGNORE_CASE)
        val fallbackMatch = fallbackRegex.find(trimmed)
        val extracted = fallbackMatch?.groupValues?.getOrNull(1)?.takeIf { it.isNotEmpty() }

        return when {
            extracted != null && extracted.length >= 11 -> extracted.take(11)
            extracted != null -> extracted
            trimmed.length >= 11 -> trimmed.takeLast(11)
            else -> "dQw4w9WgXcQ"
        }
    }

    fun playCustomYouTubeUrl(url: String) {
        val id = extractYouTubeId(url)
        val customVideo = VideoItem(
            id = "custom_" + System.currentTimeMillis(),
            title = "ইউটিউব সরাসরি সম্প্রচার (Watch & Earn)",
            channelTitle = "YouTube Streaming",
            duration = "Live",
            thumbnailUrl = "https://images.unsplash.com/photo-1594909122845-11baa439b7bf?w=800&auto=format&fit=crop&q=80",
            youtubeVideoId = id,
            category = "YouTube Live",
            views = "Live",
            rewardPoints = 20,
            cast = "Official Cast",
            description = "কাস্টম লিংক থেকে সরাসরি NatokFlix-এ নাটক উপভোগ করছেন। প্রতি ৩০ সেকেন্ডে পয়েন্ট অর্জিত হচ্ছে।"
        )
        playVideo(customVideo)
    }

    fun playVideo(video: VideoItem) {
        viewModelScope.launch {
            repository.incrementViewCount(video.id)
            _natoks.value = repository.fetchVideos()
        }
        _currentVideo.value = video
        _sessionTotalCoinsEarned.value = 0
        _continuousTimerSeconds.value = 30
        _watchTimeCountdown.value = 60
        _isPlayerPlaying.value = true
        _currentScreen.value = AppScreen.VIDEO_PLAYER

        if (_isAdFreeActive.value) {
            _showPreRollAd.value = false
            startContinuousWatchTimer()
        } else {
            // Launch Pre-roll Ad first (Automated Ads Integration)
            _showPreRollAd.value = true
            _preRollCountdown.value = 5

            viewModelScope.launch {
                for (i in 5 downTo 1) {
                    if (!_showPreRollAd.value || _currentScreen.value != AppScreen.VIDEO_PLAYER) break
                    _preRollCountdown.value = i
                    delay(1000)
                }
                if (_showPreRollAd.value) {
                    _showPreRollAd.value = false
                    repository.addPoints(15) // +15 bonus for watching full pre-roll ad!
                    _sessionTotalCoinsEarned.value += 15
                    _uiMessage.emit("🎁 বিজ্ঞাপন দেখার জন্য +১৫ বোনাস কয়েন পেয়েছেন!")
                }
                startContinuousWatchTimer()
            }
        }

        // Progress Category drama tasks
        if (video.category == "Eid Special") {
            updateTaskProgress("task_eid_natok", 1)
        } else if (video.category == "Comedy Drama") {
            updateTaskProgress("task_comedy_natok", 1)
        }
    }

    fun skipPreRollAd() {
        _showPreRollAd.value = false
        startContinuousWatchTimer()
    }

    fun setPlayerPlaying(playing: Boolean) {
        _isPlayerPlaying.value = playing
    }

    private fun calculateNatokReward(durationStr: String): Int {
        val minutes = try {
            if (durationStr.contains(":")) {
                val parts = durationStr.split(":")
                if (parts.size == 2) {
                    parts[0].trim().toIntOrNull() ?: 30
                } else if (parts.size >= 3) {
                    val h = parts[0].trim().toIntOrNull() ?: 0
                    val m = parts[1].trim().toIntOrNull() ?: 0
                    h * 60 + m
                } else {
                    30
                }
            } else {
                durationStr.filter { it.isDigit() }.toIntOrNull() ?: 30
            }
        } catch (_: Exception) {
            30
        }

        // 30m -> 20 coins, 40m -> 30 coins, every additional 10m -> +10 coins
        return when {
            minutes >= 40 -> 20 + ((minutes - 30) / 10) * 10
            minutes >= 30 -> 20
            minutes >= 20 -> 15
            minutes >= 10 -> 10
            else -> 5
        }
    }

    fun onVideoCompletion() {
        val video = _currentVideo.value ?: return
        viewModelScope.launch {
            val reward = if (video.rewardPoints > 0) video.rewardPoints else calculateNatokReward(video.duration)
            val bdtValue = reward / 10.0 // 10 Coins = 1 BDT (100 Coins = 10 BDT)
            repository.addPoints(reward)
            _sessionTotalCoinsEarned.value += reward
            _uiMessage.emit("🎬 '${video.title}' সম্পূর্ণ করার জন্য +$reward কয়েন (৳${String.format(java.util.Locale.US, "%.1f", bdtValue)} টাকা) সফলভাবে যোগ হয়েছে! 🎉")
        }
    }

    fun closeVideoPlayer() {
        continuousWatchJob?.cancel()
        _currentScreen.value = AppScreen.MAIN_DASHBOARD
        _currentVideo.value = null
        _showPreRollAd.value = false
        _showPeriodicMidRollAd.value = false
    }

    fun triggerPeriodicLongAd() {
        viewModelScope.launch {
            _showPeriodicMidRollAd.value = true
            _periodicAdCountdown.value = 5
            while (_periodicAdCountdown.value > 0 && _showPeriodicMidRollAd.value) {
                delay(1000)
                _periodicAdCountdown.value -= 1
            }
        }
    }

    fun dismissPeriodicLongAd() {
        _showPeriodicMidRollAd.value = false
        _timeUntilNextMidRollAd.value = 180 // Reset 3-minute timer
        viewModelScope.launch {
            val bonus = 10 // 10 coins bonus
            repository.addPoints(bonus)
            _sessionTotalCoinsEarned.value += bonus
            _totalAdsWatchedToday.value += 1
            incrementAdTasks()
            _uiMessage.emit("📺 বিজ্ঞাপনটি সফলভাবে দেখার জন্য +১০ বোনাস কয়েন যোগ হয়েছে!")
        }
    }

    private fun startContinuousWatchTimer() {
        continuousWatchJob?.cancel()
        _timeUntilNextMidRollAd.value = 180
        continuousWatchJob = viewModelScope.launch {
            while (_currentScreen.value == AppScreen.VIDEO_PLAYER) {
                delay(1000)
                if (!_showPreRollAd.value && !_showPeriodicMidRollAd.value && _isPlayerPlaying.value) {
                    _continuousTimerSeconds.value -= 1
                    if (_continuousTimerSeconds.value <= 0) {
                        _continuousTimerSeconds.value = 60
                        // Award user 2 coins per minute of continuous watching
                        repository.addPoints(2)
                        _sessionTotalCoinsEarned.value += 2
                        _uiMessage.emit("🎉 +২ কয়েন ওয়াচ রিওয়ার্ড যোগ হয়েছে! মোট: ${_sessionTotalCoinsEarned.value} 🪙")

                        // Increment watched video minutes & update tasks
                        _totalVideoMinutesWatchedToday.value += 1
                        incrementVideoMinutesTasks(1)
                    }

                    // 3-Minute (180s) Long Mid-Roll Ad Counter
                    _timeUntilNextMidRollAd.value -= 1
                    if (_timeUntilNextMidRollAd.value <= 0) {
                        triggerPeriodicLongAd()
                    }

                    _watchTimeCountdown.value -= 1
                    if (_watchTimeCountdown.value <= 0) {
                        _watchTimeCountdown.value = 60
                    }
                }
            }
        }
    }

    // ==========================================
    // SPIN WHEEL LOGIC (STRICT LIMIT & PROBABILITY)
    // ==========================================
    fun canSpin(): Boolean {
        if (_isSpinning.value) return false
        if (_totalSpinsUsedToday.value >= maxDailySpins) return false
        return _freeSpinsRemaining.value > 0 || _adSpinsUnlocked.value > 0
    }

    fun spinWheel() {
        if (_isSpinning.value) return

        if (_totalSpinsUsedToday.value >= maxDailySpins) {
            viewModelScope.launch {
                _uiMessage.emit("আজকের সর্বোচ্চ ৩টি স্পিন সম্পন্ন হয়েছে! আগামীকাল আবার আসুন।")
            }
            return
        }

        if (_freeSpinsRemaining.value <= 0 && _adSpinsUnlocked.value <= 0) {
            viewModelScope.launch {
                _uiMessage.emit("আজকের ফ্রি স্পিন শেষ! ভিডিও বিজ্ঞাপন দেখে স্পিন আনলক করুন।")
            }
            return
        }

        _isSpinning.value = true
        _spinResultPoints.value = null

        // Deduct 1 spin (free first, then ad spin)
        if (_freeSpinsRemaining.value > 0) {
            _freeSpinsRemaining.value -= 1
        } else {
            _adSpinsUnlocked.value -= 1
        }
        _totalSpinsUsedToday.value += 1

        // Slices: 5, 10, 20, 50, 100
        // User gets max 5 coins strictly as specified
        val wonPoints = 5
        val sliceIndex = 0 // Index 0 is "5"
        
        // Pointer is at top (270°). 5 slices of 72° each.
        val sliceAngle = 360f / 5f
        val targetSliceCenter = sliceIndex * sliceAngle + (sliceAngle / 2f)
        val fullRotations = 360f * 6
        // To make winning slice align with top pointer (270°)
        val targetRotation = fullRotations + (360f - targetSliceCenter + 270f) % 360f

        viewModelScope.launch {
            _spinAngle.value = _spinAngle.value + targetRotation
            delay(3600)
            _isSpinning.value = false
            _spinResultPoints.value = wonPoints
            repository.addPoints(wonPoints)
            _uiMessage.emit("🎉 অভিনন্দন! স্পিন থেকে আপনি জিতেছেন $wonPoints কয়েন (৳$wonPoints BDT)!")

            // Update spin task
            updateTaskProgress("task_spin_1", 1)
        }
    }

    fun unlockSpinViaAd() {
        if (_isUnlockingSpinAd.value) return
        val spinsUsed = _totalSpinsUsedToday.value
        if (spinsUsed >= maxDailySpins) {
            viewModelScope.launch {
                _uiMessage.emit("আজকের সর্বোচ্চ ৩টি স্পিন নেওয়া হয়ে গেছে!")
            }
            return
        }

        val availableToUnlock = maxDailySpins - spinsUsed - _adSpinsUnlocked.value
        if (availableToUnlock <= 0) {
            viewModelScope.launch {
                _uiMessage.emit("আপনার কাছে ইতিমধ্যে ১টি আনলকড স্পিন রয়েছে! আগে স্পিনটি সম্পন্ন করুন।")
            }
            return
        }

        _isUnlockingSpinAd.value = true
        viewModelScope.launch {
            _uiMessage.emit("বিজ্ঞাপন লোড হচ্ছে... অনুগ্রহ করে একটু অপেক্ষা করুন")
            delay(3000)
            _isUnlockingSpinAd.value = false
            _adSpinsUnlocked.value += 1
            _totalAdsWatchedToday.value += 1
            incrementAdTasks()
            _uiMessage.emit("অভিনন্দন! ১টি নতুন স্পিন আনলক হয়েছে। এখনই স্পিন করুন!")
        }
    }

    // ==========================================
    // REWARDS & TASKS INTEGRATION
    // ==========================================
    fun claimDailyCheckIn() {
        if (_dailyCheckInClaimed.value) return
        _dailyCheckInClaimed.value = true
        viewModelScope.launch {
            val checkInBonus = _dailyCheckInBonus.value
            repository.addPoints(checkInBonus)
            _uiMessage.emit("দৈনিক চেক-ইন বোনাস +$checkInBonus কয়েন (৳$checkInBonus BDT) সফলভাবে গ্রহণ করেছেন!")
            updateTaskProgress("task_checkin", 1)
        }
    }

    fun claimShortReward(points: Int = _reelWatchCoins.value) {
        viewModelScope.launch {
            val reward = if (points > 0) points else _reelWatchCoins.value
            repository.addPoints(reward)
            val bdtShare = reward.toDouble() / _coinsPerBdtRate.value.toDouble()
            _uiMessage.emit("১৫ সেকেন্ড রিলস দেখার জন্য +$reward কয়েন (৳${String.format(java.util.Locale.US, "%.2f", bdtShare)} BDT) যোগ হয়েছে!")
            _totalShortsWatchedToday.value += 1
            incrementShortsTasks(1)
        }
    }

    private fun incrementAdTasks() {
        val currentAds = _totalAdsWatchedToday.value
        listOf("task_ad_1", "task_ad_3", "task_ad_5", "task_ad_10").forEach { taskId ->
            updateTaskProgress(taskId, currentAds)
        }
    }

    private fun incrementVideoMinutesTasks(minutes: Int) {
        val currentMinutes = _totalVideoMinutesWatchedToday.value
        listOf(
            "task_video_2m",
            "task_video_5m",
            "task_video_10m",
            "task_video_15m",
            "task_video_20m",
            "task_video_30m"
        ).forEach { taskId ->
            updateTaskProgress(taskId, currentMinutes)
        }
    }

    private fun incrementShortsTasks(amount: Int) {
        val currentShorts = _totalShortsWatchedToday.value
        listOf("task_shorts_3", "task_shorts_5", "task_shorts_10").forEach { taskId ->
            updateTaskProgress(taskId, currentShorts)
        }
    }

    fun updateTaskProgress(taskId: String, newProgress: Int) {
        _tasks.value = _tasks.value.map { task ->
            if (task.id == taskId) {
                task.copy(currentProgress = newProgress.coerceAtMost(task.targetProgress))
            } else {
                task
            }
        }
        checkMegaBonus()
    }

    private fun checkMegaBonus() {
        val completedCount = _tasks.value.count { it.id != "task_mega_10" && it.isCompleted }
        _tasks.value = _tasks.value.map { task ->
            if (task.id == "task_mega_10") {
                task.copy(currentProgress = completedCount.coerceAtMost(10))
            } else {
                task
            }
        }
    }

    fun claimTaskReward(taskId: String) {
        val task = _tasks.value.find { it.id == taskId } ?: return
        if (!task.isCompleted || task.isClaimed) return

        viewModelScope.launch {
            repository.addPoints(task.rewardPoints)
            _tasks.value = _tasks.value.map {
                if (it.id == taskId) it.copy(isClaimed = true) else it
            }
            _uiMessage.emit("অভিনন্দন! '${task.title}' থেকে +${task.rewardPoints} কয়েন গ্রহণ করেছেন!")
            checkMegaBonus()
        }
    }

    fun performTaskAction(task: TaskItem) {
        when (task.type) {
            TaskType.DAILY_CHECK_IN -> {
                if (task.id == "task_checkin") {
                    claimDailyCheckIn()
                } else {
                    updateTaskProgress(task.id, 1)
                    _selectedTab.value = NavigationTab.PROFILE
                }
            }
            TaskType.SPIN_WHEEL -> {
                _selectedTab.value = NavigationTab.SPIN
            }
            TaskType.WATCH_AD -> {
                watchRewardedAd()
            }
            TaskType.WATCH_VIDEO_MINUTES, TaskType.CATEGORY_DRAMA -> {
                _selectedTab.value = NavigationTab.HOME
            }
            TaskType.WATCH_SHORTS -> {
                _selectedTab.value = NavigationTab.SHORTS
            }
            TaskType.SHARE_APP -> {
                viewModelScope.launch {
                    updateTaskProgress(task.id, 1)
                    _uiMessage.emit("রেফারেল লিঙ্ক শেয়ার করা হয়েছে!")
                }
            }
            TaskType.MEGA_BONUS -> {
                viewModelScope.launch {
                    _uiMessage.emit("আরও টাস্ক সম্পন্ন করে জ্যাকপট আনলক করুন!")
                }
            }
        }
    }

    // ==========================================
    // WITHDRAWAL & ADMIN ACTIONS
    // ==========================================
    fun requestWithdrawal(method: String, operator: String? = null, accountNumber: String, amountBdt: Double): Boolean {
        viewModelScope.launch {
            val (success, message) = repository.submitWithdrawal(method, operator, accountNumber, amountBdt)
            _uiMessage.emit(message)
        }
        return true
    }

    fun adminApproveWithdrawal(id: String) {
        viewModelScope.launch {
            repository.updateWithdrawalStatus(id, "Approved")
            _uiMessage.emit("উত্তোলন সফলভাবে অনুমোদিত হয়েছে!")
        }
    }

    fun adminRejectWithdrawal(id: String) {
        viewModelScope.launch {
            repository.updateWithdrawalStatus(id, "Rejected")
            _uiMessage.emit("উত্তোলন বাতিল করা হয়েছে")
        }
    }

    // ==========================================
    // EXTENDED ADMIN CONTROLS METHODS
    // ==========================================
    fun updateAnnouncement(notice: String, enabled: Boolean) {
        _announcementNotice.value = notice
        _isAnnouncementEnabled.value = enabled
        viewModelScope.launch { _uiMessage.emit("ঘোষণা নোটিশ আপডেট হয়েছে") }
    }

    fun updateEconomySettings(
        minWithdraw: Double,
        rate: Int,
        checkIn: Int,
        referrerBonus: Int,
        refereeBonus: Int = 100,
        shortsReward: Int,
        freeSpins: Int,
        rewardedVideoCoins: Int = 10,
        interstitialCoins: Int = 5,
        reelWatchCoins: Int = 2
    ) {
        _minWithdrawalBdt.value = minWithdraw
        _coinsPerBdtRate.value = rate
        _dailyCheckInBonus.value = checkIn
        _referralBonusCoins.value = referrerBonus
        _refereeBonusCoins.value = refereeBonus
        _shortsRewardCoins.value = shortsReward
        _freeDailySpinsLimit.value = freeSpins
        _freeSpinsRemaining.value = freeSpins
        _rewardedVideoAdCoins.value = rewardedVideoCoins
        _interstitialAdCoins.value = interstitialCoins
        _reelWatchCoins.value = reelWatchCoins

        val config = EconomyConfig(
            coinsPerBdtRate = rate,
            minWithdrawalBdt = minWithdraw,
            referrerBonusCoins = referrerBonus,
            refereeBonusCoins = refereeBonus,
            dailyCheckInBonus = checkIn,
            freeDailySpinsLimit = freeSpins,
            shortsRewardCoins = shortsReward,
            rewardedVideoAdCoins = rewardedVideoCoins,
            interstitialAdCoins = interstitialCoins,
            reelWatchCoins = reelWatchCoins,
            isBkashEnabled = _isBkashEnabled.value,
            isNagadEnabled = _isNagadEnabled.value,
            isRocketEnabled = _isRocketEnabled.value,
            isMaintenanceMode = _isMaintenanceMode.value,
            maintenanceMessage = _maintenanceMessage.value,
            announcementNotice = _announcementNotice.value,
            isAnnouncementEnabled = _isAnnouncementEnabled.value,
            featuredPromoTitle = _featuredPromoTitle.value,
            featuredPromoSubtitle = _featuredPromoSubtitle.value,
            telegramChannelUrl = _telegramChannelUrl.value,
            whatsappSupportNumber = _whatsappSupportNumber.value,
            withdrawalRulesText = _withdrawalRulesText.value,
            updatedAt = System.currentTimeMillis()
        )
        viewModelScope.launch {
            repository.saveEconomyConfig(config)
            _uiMessage.emit("⚡ অর্থনীতি সেটিংস ফায়ারবেজে সেভ ও লাইভ সিঙ্ক হয়েছে!")
        }
    }

    fun updateGateways(bkash: Boolean, nagad: Boolean, rocket: Boolean) {
        _isBkashEnabled.value = bkash
        _isNagadEnabled.value = nagad
        _isRocketEnabled.value = rocket
        viewModelScope.launch { _uiMessage.emit("পেমেন্ট গেটওয়ে স্ট্যাটাস আপডেট হয়েছে") }
    }

    fun updateSupportAndRules(telegram: String, whatsapp: String, rules: String) {
        _telegramChannelUrl.value = telegram
        _whatsappSupportNumber.value = whatsapp
        _withdrawalRulesText.value = rules
        viewModelScope.launch { _uiMessage.emit("সাপোর্ট লিংক ও নিয়মাবলি আপডেট হয়েছে") }
    }

    fun updatePromoBanner(title: String, subtitle: String) {
        _featuredPromoTitle.value = title
        _featuredPromoSubtitle.value = subtitle
        viewModelScope.launch { _uiMessage.emit("প্রোমো ব্যানার টেক্সট আপডেট হয়েছে") }
    }

    fun toggleMaintenance(enabled: Boolean, message: String) {
        _isMaintenanceMode.value = enabled
        _maintenanceMessage.value = message
        viewModelScope.launch {
            _uiMessage.emit(if (enabled) "রক্ষণাবেক্ষণ মোড চালু হয়েছে" else "রক্ষণাবেক্ষণ মোড বন্ধ করা হয়েছে")
        }
    }

    fun addCategory(categoryName: String) {
        val trimmed = categoryName.trim()
        if (trimmed.isNotBlank() && !_categoriesList.value.contains(trimmed)) {
            _categoriesList.value = _categoriesList.value + trimmed
            viewModelScope.launch { _uiMessage.emit("নতুন ক্যাটাগরি '$trimmed' যুক্ত হয়েছে") }
        }
    }

    fun deleteCategory(categoryName: String) {
        if (categoryName != "সকল নাটক") {
            _categoriesList.value = _categoriesList.value.filter { it != categoryName }
            viewModelScope.launch { _uiMessage.emit("ক্যাটাগরি '$categoryName' মুছে ফেলা হয়েছে") }
        }
    }

    fun addShort(video: VideoItem) {
        _shorts.value = listOf(video) + _shorts.value
        viewModelScope.launch { _uiMessage.emit("নতুন শর্টস ক্লিপ সফলভাবে যুক্ত হয়েছে!") }
    }

    fun addNewTask(title: String, reward: Int, target: Int) {
        val newTask = TaskItem(
            id = "custom_task_" + System.currentTimeMillis(),
            title = title,
            description = "অ্যাডমিন প্যানেল থেকে তৈরি নতুন টাস্ক সম্পন্ন করে কয়েন অর্জন করুন।",
            type = TaskType.WATCH_VIDEO_MINUTES,
            rewardPoints = reward,
            currentProgress = 0,
            targetProgress = target,
            unit = "বার"
        )
        _tasks.value = _tasks.value + newTask
        viewModelScope.launch { _uiMessage.emit("নতুন টাস্ক '$title' ইউজার প্যানেলে পাবলিশ হয়েছে!") }
    }

    fun broadcastBonusToAllUsers(coins: Int, reason: String) {
        viewModelScope.launch {
            repository.addPoints(coins)
            _allUsers.value = _allUsers.value.map { u ->
                u.copy(
                    points = u.points + coins,
                    balanceBdt = u.balanceBdt + (coins / _coinsPerBdtRate.value.toDouble())
                )
            }
            _uiMessage.emit("🎉 সমস্ত ইউজারদের অ্যাকাউন্টে +$coins কয়েন বোনাস পাঠানো হয়েছে ($reason)!")
        }
    }

    fun toggleUserBan(userId: String) {
        _allUsers.value = _allUsers.value.map { u ->
            if (u.id == userId) {
                val newRole = if (u.role == "BANNED") "USER" else "BANNED"
                u.copy(role = newRole)
            } else u
        }
        viewModelScope.launch { _uiMessage.emit("ইউজার স্ট্যাটাস পরিবর্তন করা হয়েছে") }
    }

    // ==========================================
    // SOCIAL TASKS ACTIONS
    // ==========================================
    fun completeSocialTask(platform: String, context: android.content.Context) {
        val reward = _socialTaskRewardCoins.value
        val url = when (platform.lowercase()) {
            "telegram" -> _telegramTaskUrl.value
            "youtube" -> _youtubeTaskUrl.value
            "facebook" -> _facebookTaskUrl.value
            else -> "https://natokflix.com"
        }

        // Open external intent link
        try {
            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(url)).apply {
                flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            // Fallback
        }

        // Credit reward coins
        viewModelScope.launch {
            when (platform.lowercase()) {
                "telegram" -> _isTelegramTaskClaimed.value = true
                "youtube" -> _isYoutubeTaskClaimed.value = true
                "facebook" -> _isFacebookTaskClaimed.value = true
            }
            repository.addPoints(reward)
            _uiMessage.emit("🎉 অভিনন্দন! সোশ্যাল টাস্ক সম্পন্ন হয়েছে এবং +$reward কয়েন যোগ হয়েছে!")
        }
    }

    fun updateSocialTaskSettings(telegram: String, youtube: String, facebook: String, reward: Int) {
        if (telegram.isNotBlank()) _telegramTaskUrl.value = telegram.trim()
        if (youtube.isNotBlank()) _youtubeTaskUrl.value = youtube.trim()
        if (facebook.isNotBlank()) _facebookTaskUrl.value = facebook.trim()
        if (reward > 0) _socialTaskRewardCoins.value = reward

        viewModelScope.launch {
            _uiMessage.emit("সোশ্যাল টাস্ক লিংক ও রিওয়ার্ড সফলভাবে সেভ হয়েছে")
        }
    }

    // ==========================================
    // POP-UP NOTICE ACTIONS
    // ==========================================
    fun dismissPopupNotice() {
        _showPopupNoticeDialog.value = false
    }

    fun updatePopupNoticeSettings(enabled: Boolean, title: String, message: String, btnText: String) {
        _isPopupNoticeEnabled.value = enabled
        if (title.isNotBlank()) _popupNoticeTitle.value = title.trim()
        if (message.isNotBlank()) _popupNoticeMessage.value = message.trim()
        if (btnText.isNotBlank()) _popupNoticeBtnText.value = btnText.trim()

        viewModelScope.launch {
            _uiMessage.emit("ইন-অ্যাপ পপ-আপ নোটিশ সেটিংস আপডেট হয়েছে!")
        }
    }

    // ==========================================
    // PUSH NOTIFICATIONS ACTIONS
    // ==========================================
    fun sendPushNotification(title: String, message: String) {
        if (title.isBlank() || message.isBlank()) {
            viewModelScope.launch { _uiMessage.emit("টাইটেল ও মেসেজ পূরণ করুন") }
            return
        }

        com.example.util.NotificationHelper.showPushNotification(
            getApplication(),
            title.trim(),
            message.trim()
        )

        viewModelScope.launch {
            _uiMessage.emit("🔔 ফোন নোটিফিকেশন বারে পুশ নোটিফিকেশন সফলভাবে পাঠানো হয়েছে!")
        }
    }
}
