import 'dart:async';
import 'dart:math';
import 'package:flutter/material.dart';
import '../core/constants/app_constants.dart';
import '../data/models/user_model.dart';
import '../data/models/video_item.dart';
import '../data/models/withdrawal_model.dart';
import '../data/models/task_item.dart';
import '../data/models/friend_model.dart';
import '../data/models/economy_config.dart';
import '../data/services/local_storage_service.dart';

enum AppScreen {
  splash,
  appOpenAd,
  login,
  mainDashboard,
  videoPlayer,
  chat,
  adminPanel,
}

enum NavigationTab {
  home,
  shorts,
  friends,
  gameZone,
  task,
  spin,
  profile,
}

class NatokProvider extends ChangeNotifier {
  // Navigation & Screens
  AppScreen _currentScreen = AppScreen.splash;
  NavigationTab _selectedTab = NavigationTab.home;

  AppScreen get currentScreen => _currentScreen;
  NavigationTab get selectedTab => _selectedTab;

  // Theme
  bool _isDarkTheme = true;
  bool get isDarkTheme => _isDarkTheme;

  // User State
  UserModel _user = const UserModel(
    id: 'user_default',
    name: 'NatokFlix User',
    email: 'user@natokflix.com',
    gender: 'Boy',
    points: 1250,
    balanceBdt: 125.0,
    role: 'USER',
    isLoggedIn: true,
  );
  UserModel get user => _user;

  String? _loginErrorAlert;
  String? get loginErrorAlert => _loginErrorAlert;

  // Economy & Config
  EconomyConfig _economyConfig = const EconomyConfig();
  EconomyConfig get economyConfig => _economyConfig;

  // Videos & Feeds
  List<VideoItem> _natoks = [];
  List<VideoItem> get natoks => _natoks;

  List<VideoItem> _shorts = [];
  List<VideoItem> get shorts => _shorts;

  List<String> _categories = [];
  List<String> get categories => _categories;

  String _selectedCategory = 'সকল নাটক';
  String get selectedCategory => _selectedCategory;

  String _searchQuery = '';
  String get searchQuery => _searchQuery;

  List<VideoItem> _searchResults = [];
  List<VideoItem> get searchResults => _searchResults;

  bool _isSearching = false;
  bool get isSearching => _isSearching;

  // Video Player State
  VideoItem? _currentVideo;
  VideoItem? get currentVideo => _currentVideo;

  bool _isPlayerPlaying = false;
  bool get isPlayerPlaying => _isPlayerPlaying;

  int _continuousTimerSeconds = 0;
  int get continuousTimerSeconds => _continuousTimerSeconds;

  int _watchTimeCountdown = 30; // 30s countdown to earn points
  int get watchTimeCountdown => _watchTimeCountdown;

  int _sessionCoinsEarned = 0;
  int get sessionCoinsEarned => _sessionCoinsEarned;

  bool _showPreRollAd = false;
  final int _preRollCountdown = 5;
  bool get showPreRollAd => _showPreRollAd;
  int get preRollCountdown => _preRollCountdown;

  bool _showPeriodicMidRollAd = false;
  final int _periodicAdCountdown = 5;
  bool get showPeriodicMidRollAd => _showPeriodicMidRollAd;
  int get periodicAdCountdown => _periodicAdCountdown;

  Timer? _videoWatchTimer;
  Timer? _adCountdownTimer;

  // Spin Wheel State
  bool _isSpinning = false;
  double _spinAngle = 0.0;
  int _spinResultPoints = 0;
  bool _dailyCheckInClaimed = false;
  int _freeSpinsRemaining = 1;
  int _adSpinsUnlocked = 0;
  int _totalSpinsUsedToday = 0;

  bool get isSpinning => _isSpinning;
  double get spinAngle => _spinAngle;
  int get spinResultPoints => _spinResultPoints;
  bool get dailyCheckInClaimed => _dailyCheckInClaimed;
  int get freeSpinsRemaining => _freeSpinsRemaining;
  int get adSpinsUnlocked => _adSpinsUnlocked;
  int get totalSpinsUsedToday => _totalSpinsUsedToday;

  // Tasks
  List<TaskItem> _tasks = [];
  List<TaskItem> get tasks => _tasks;

  // Withdrawals
  List<WithdrawalModel> _withdrawals = [];
  List<WithdrawalModel> get withdrawals => _withdrawals;

  List<WithdrawalModel> _allWithdrawals = [];
  List<WithdrawalModel> get allWithdrawals => _allWithdrawals;

  // Friends & Chat
  List<FriendProfile> _friends = [];
  List<FriendProfile> get friends => _friends;

  FriendProfile? _activeChatFriend;
  FriendProfile? get activeChatFriend => _activeChatFriend;

  List<ChatMessage> _allMessages = [];
  List<ChatMessage> get allMessages => _allMessages;

  bool _isBotTyping = false;
  bool get isBotTyping => _isBotTyping;

  // Admin state
  List<UserModel> _allUsers = [];
  List<UserModel> get allUsers => _allUsers;

  NatokProvider() {
    _initApp();
  }

  Future<void> _initApp() async {
    // 1. Load theme
    _isDarkTheme = await LocalStorageService.loadThemeMode();

    // 2. Load defaults
    _natoks = List.from(AppConstants.initialNatoks);
    _shorts = List.from(AppConstants.initialShorts);
    _categories = List.from(AppConstants.defaultCategories);
    _tasks = List.from(AppConstants.defaultTasks);
    _friends = List.from(AppConstants.defaultBotProfiles);

    // 3. Load saved user & persistence
    final savedUser = await LocalStorageService.loadUser();
    if (savedUser != null) {
      _user = savedUser;
    }

    final savedWithdrawals = await LocalStorageService.loadWithdrawals();
    if (savedWithdrawals.isNotEmpty) {
      _withdrawals = savedWithdrawals;
      _allWithdrawals = savedWithdrawals;
    } else {
      // Sample initial withdrawal history
      _withdrawals = [
        WithdrawalModel(
          id: 'w_101',
          method: 'bKash',
          accountNumber: '01788123456',
          amountBdt: 300.0,
          pointsDeducted: 3000,
          status: 'Approved',
          timestamp: DateTime.now().subtract(const Duration(days: 2)).millisecondsSinceEpoch,
        ),
      ];
      _allWithdrawals = List.from(_withdrawals);
    }

    _dailyCheckInClaimed = await LocalStorageService.isDailyCheckInDoneToday();
    final freeSpinUsed = await LocalStorageService.isFreeSpinUsedToday();
    if (freeSpinUsed) {
      _freeSpinsRemaining = 0;
    }

    final savedTasks = await LocalStorageService.loadTasks();
    if (savedTasks.isNotEmpty) {
      _tasks = savedTasks;
    }

    final savedMessages = await LocalStorageService.loadChatMessages();
    if (savedMessages.isNotEmpty) {
      _allMessages = savedMessages;
    }

    // Mock admin users list
    _allUsers = [
      _user,
      const UserModel(
        id: 'user_sakib',
        name: 'Sakib Ahmed',
        email: 'sakib@gmail.com',
        phone: '01812345678',
        points: 4500,
        balanceBdt: 450.0,
      ),
      const UserModel(
        id: 'user_nusrat',
        name: 'Nusrat Jahan',
        email: 'nusrat@gmail.com',
        phone: '01987654321',
        gender: 'Girl',
        points: 8200,
        balanceBdt: 820.0,
      ),
    ];

    notifyListeners();
  }

  // Navigation handlers
  void navigateToScreen(AppScreen screen) {
    _currentScreen = screen;
    notifyListeners();
  }

  void selectTab(NavigationTab tab) {
    _selectedTab = tab;
    notifyListeners();
  }

  void toggleTheme() {
    _isDarkTheme = !_isDarkTheme;
    LocalStorageService.saveThemeMode(_isDarkTheme);
    notifyListeners();
  }

  void onAppOpenAdDismissed() {
    if (_user.isLoggedIn) {
      _currentScreen = AppScreen.mainDashboard;
    } else {
      _currentScreen = AppScreen.login;
    }
    notifyListeners();
  }

  // Auth Handling
  void handleLogin(String usernameOrEmail, String password, bool rememberMe) {
    _loginErrorAlert = null;
    if (usernameOrEmail.isEmpty || password.isEmpty) {
      _loginErrorAlert = 'ব্যবহারকারীর নাম এবং পাসওয়ার্ড প্রদান করুন।';
      notifyListeners();
      return;
    }

    // Admin login shortcut
    if (usernameOrEmail.toLowerCase() == 'admin' && password == 'admin123') {
      _user = _user.copyWith(
        role: 'ADMIN',
        isLoggedIn: true,
        name: 'Admin Boss',
      );
      _currentScreen = AppScreen.adminPanel;
      LocalStorageService.saveUser(_user);
      notifyListeners();
      return;
    }

    _user = _user.copyWith(
      name: usernameOrEmail.contains('@') ? usernameOrEmail.split('@')[0] : usernameOrEmail,
      email: usernameOrEmail.contains('@') ? usernameOrEmail : '$_user@natokflix.com',
      isLoggedIn: rememberMe,
    );
    _currentScreen = AppScreen.mainDashboard;
    LocalStorageService.saveUser(_user);
    notifyListeners();
  }

  void handleSignUp(
    String name,
    String email,
    String phone,
    String password,
    String referralCode,
    String gender,
    String birthday,
  ) {
    _loginErrorAlert = null;
    int initialBonus = 100; // Registration welcome bonus
    if (referralCode.trim().isNotEmpty) {
      initialBonus += _economyConfig.refereeBonusCoins;
    }

    final double balance = initialBonus / _economyConfig.coinsPerBdtRate;
    _user = UserModel(
      id: 'usr_${DateTime.now().millisecondsSinceEpoch}',
      name: name.isNotEmpty ? name : 'New Watcher',
      email: email,
      phone: phone,
      gender: gender,
      birthday: birthday,
      points: initialBonus,
      balanceBdt: balance,
      referralCode: 'NTK${Random().nextInt(9000) + 1000}',
      isLoggedIn: true,
    );

    _currentScreen = AppScreen.mainDashboard;
    LocalStorageService.saveUser(_user);
    notifyListeners();
  }

  void handleGoogleSignIn() {
    _user = _user.copyWith(
      name: 'Google User',
      email: 'user.google@gmail.com',
      isLoggedIn: true,
    );
    _currentScreen = AppScreen.mainDashboard;
    LocalStorageService.saveUser(_user);
    notifyListeners();
  }

  void handleFacebookSignIn() {
    _user = _user.copyWith(
      name: 'Facebook User',
      email: 'user.fb@gmail.com',
      isLoggedIn: true,
    );
    _currentScreen = AppScreen.mainDashboard;
    LocalStorageService.saveUser(_user);
    notifyListeners();
  }

  void logout() {
    _user = _user.copyWith(isLoggedIn: false);
    _currentScreen = AppScreen.login;
    LocalStorageService.saveUser(_user);
    notifyListeners();
  }

  // Coins Accrual & Balance
  void addCoins(int amount, String reason) {
    if (amount <= 0) return;
    final int newPoints = _user.points + amount;
    final double newBalance = newPoints / _economyConfig.coinsPerBdtRate;
    _user = _user.copyWith(
      points: newPoints,
      balanceBdt: newBalance,
    );
    LocalStorageService.saveUser(_user);
    notifyListeners();
  }

  // Withdrawal
  String? submitWithdrawal({
    required String method,
    required String accountNumber,
    required double amountBdt,
    String? operator,
  }) {
    if (amountBdt < _economyConfig.minWithdrawalBdt) {
      return 'সর্বনিম্ন উত্তোলন ${_economyConfig.minWithdrawalBdt.toInt()} টাকা।';
    }
    if (_user.balanceBdt < amountBdt) {
      return 'আপনার অ্যাকাউন্টে পর্যাপ্ত ব্যালেন্স নেই।';
    }
    if (accountNumber.trim().length < 11) {
      return 'সঠিক মোবাইল নম্বর প্রদান করুন।';
    }

    final int pointsToDeduct = (amountBdt * _economyConfig.coinsPerBdtRate).toInt();
    final newPoints = _user.points - pointsToDeduct;
    final newBalance = newPoints / _economyConfig.coinsPerBdtRate;

    _user = _user.copyWith(
      points: newPoints,
      balanceBdt: newBalance,
    );
    LocalStorageService.saveUser(_user);

    final withdrawal = WithdrawalModel(
      id: 'w_${DateTime.now().millisecondsSinceEpoch}',
      userId: _user.id,
      method: method,
      operator: operator,
      accountNumber: accountNumber,
      amountBdt: amountBdt,
      pointsDeducted: pointsToDeduct,
      status: 'Pending',
      timestamp: DateTime.now().millisecondsSinceEpoch,
    );

    _withdrawals.insert(0, withdrawal);
    _allWithdrawals.insert(0, withdrawal);
    LocalStorageService.saveWithdrawals(_withdrawals);
    notifyListeners();
    return null;
  }

  // Daily Check-In
  bool claimDailyCheckIn() {
    if (_dailyCheckInClaimed) return false;
    _dailyCheckInClaimed = true;
    addCoins(_economyConfig.dailyCheckInBonus, 'ডেইলি চেক-ইন বোনাস');
    LocalStorageService.markDailyCheckInDone();

    // Update check-in task
    updateTaskProgress(TaskType.dailyCheckIn, 1);
    notifyListeners();
    return true;
  }

  // Lucky Spin
  Future<int> spinWheel() async {
    if (_isSpinning) return 0;
    if (_freeSpinsRemaining <= 0 && _adSpinsUnlocked <= 0) return -1;

    _isSpinning = true;
    notifyListeners();

    if (_freeSpinsRemaining > 0) {
      _freeSpinsRemaining--;
      LocalStorageService.markFreeSpinUsed();
    } else if (_adSpinsUnlocked > 0) {
      _adSpinsUnlocked--;
    }
    _totalSpinsUsedToday++;

    // 8-segment prizes: [10, 20, 50, 100, 200, 500, 15, 25]
    final prizes = [10, 20, 50, 100, 200, 500, 15, 25];
    final selectedIndex = Random().nextInt(prizes.length);
    _spinResultPoints = prizes[selectedIndex];

    // Calculate rotation angle
    final fullTurns = 5 + Random().nextInt(3);
    final segmentAngle = (2 * pi) / prizes.length;
    final targetAngle = (fullTurns * 2 * pi) + (selectedIndex * segmentAngle);

    _spinAngle += targetAngle;

    // Simulate spin duration
    await Future.delayed(const Duration(milliseconds: 3500));
    _isSpinning = false;
    addCoins(_spinResultPoints, 'লাকি স্পিন পুরষ্কার');
    updateTaskProgress(TaskType.spinWheel, 1);
    notifyListeners();

    return _spinResultPoints;
  }

  void unlockAdSpin() {
    _adSpinsUnlocked += 1;
    notifyListeners();
  }

  // Video Playback & Timer
  void playVideo(VideoItem video) {
    _currentVideo = video;
    _continuousTimerSeconds = 0;
    _watchTimeCountdown = 30;
    _sessionCoinsEarned = 0;
    _currentScreen = AppScreen.videoPlayer;
    _isPlayerPlaying = true;

    _startVideoTimer();
    notifyListeners();
  }

  void closeVideoPlayer() {
    _stopVideoTimer();
    _currentVideo = null;
    _isPlayerPlaying = false;
    _currentScreen = AppScreen.mainDashboard;
    notifyListeners();
  }

  void playCustomYouTubeUrl(String url) {
    // Extract video ID
    String id = 'dQw4w9WgXcQ';
    if (url.contains('v=')) {
      id = url.split('v=')[1].split('&')[0];
    } else if (url.contains('youtu.be/')) {
      id = url.split('youtu.be/')[1].split('?')[0];
    }

    final customItem = VideoItem(
      id: 'custom_${DateTime.now().millisecondsSinceEpoch}',
      title: 'কাস্টম অনলাইন নাটক / ভিডিও',
      channelTitle: 'YouTube Stream',
      duration: '40:00',
      thumbnailUrl: 'https://img.youtube.com/vi/$id/hqdefault.jpg',
      youtubeVideoId: id,
      category: 'সকল নাটক',
      views: '10K',
      rewardPoints: 25,
      description: 'ব্যবহারকারীর দেওয়া কাস্টম ভিডিও লিংক।',
    );

    playVideo(customItem);
  }

  void _startVideoTimer() {
    _videoWatchTimer?.cancel();
    _videoWatchTimer = Timer.periodic(const Duration(seconds: 1), (timer) {
      if (!_isPlayerPlaying) return;
      _continuousTimerSeconds++;
      _watchTimeCountdown--;

      if (_watchTimeCountdown <= 0) {
        _watchTimeCountdown = 30;
        const reward = 10;
        _sessionCoinsEarned += reward;
        addCoins(reward, 'ভিডিও দেখার রিওয়ার্ড');
        updateTaskProgress(TaskType.watchVideoMinutes, 1);
      }
      notifyListeners();
    });
  }

  void _stopVideoTimer() {
    _videoWatchTimer?.cancel();
    _videoWatchTimer = null;
  }

  void setPlayerPlaying(bool playing) {
    _isPlayerPlaying = playing;
    notifyListeners();
  }

  void skipPreRollAd() {
    _showPreRollAd = false;
    notifyListeners();
  }

  void dismissPeriodicLongAd() {
    _showPeriodicMidRollAd = false;
    notifyListeners();
  }

  // Shorts Reward
  void rewardShortsWatch(VideoItem short) {
    addCoins(_economyConfig.shortsRewardCoins, 'শর্টস ড্রামা রিওয়ার্ড');
    updateTaskProgress(TaskType.watchShorts, 1);
  }

  // Tasks Management
  void updateTaskProgress(TaskType type, int count) {
    for (int i = 0; i < _tasks.length; i++) {
      if (_tasks[i].type == type && !_tasks[i].isClaimed) {
        final newProgress = _tasks[i].currentProgress + count;
        _tasks[i] = _tasks[i].copyWith(
          currentProgress: newProgress.clamp(0, _tasks[i].targetProgress),
        );
      }
    }
    LocalStorageService.saveTasks(_tasks);
    notifyListeners();
  }

  bool claimTaskReward(String taskId) {
    final index = _tasks.indexWhere((t) => t.id == taskId);
    if (index != -1) {
      final task = _tasks[index];
      if (task.isCompleted && !task.isClaimed) {
        _tasks[index] = task.copyWith(isClaimed: true);
        addCoins(task.rewardPoints, 'টাস্ক পুরষ্কার: ${task.title}');
        LocalStorageService.saveTasks(_tasks);
        notifyListeners();
        return true;
      }
    }
    return false;
  }

  // Friends & Chat
  void openChat(FriendProfile friend) {
    _activeChatFriend = friend;
    _currentScreen = AppScreen.chat;
    notifyListeners();
  }

  void closeChat() {
    _activeChatFriend = null;
    _currentScreen = AppScreen.mainDashboard;
    notifyListeners();
  }

  void sendChatMessage(String text) {
    if (text.trim().isEmpty || _activeChatFriend == null) return;

    final myMsg = ChatMessage(
      id: 'msg_${DateTime.now().millisecondsSinceEpoch}',
      conversationUserId: _activeChatFriend!.id,
      senderName: _user.name,
      text: text,
      timestamp: DateTime.now().millisecondsSinceEpoch,
      isFromMe: true,
    );

    _allMessages.add(myMsg);
    LocalStorageService.saveChatMessages(_allMessages);
    notifyListeners();

    // Simulate smart bot response after 1.5 seconds
    if (_activeChatFriend!.isBot) {
      _isBotTyping = true;
      notifyListeners();

      Timer(const Duration(milliseconds: 1800), () {
        _isBotTyping = false;
        final botReply = _generateBotResponse(text, _activeChatFriend!.name);
        final replyMsg = ChatMessage(
          id: 'msg_bot_${DateTime.now().millisecondsSinceEpoch}',
          conversationUserId: _activeChatFriend!.id,
          senderName: _activeChatFriend!.name,
          text: botReply,
          timestamp: DateTime.now().millisecondsSinceEpoch,
          isFromMe: false,
        );
        _allMessages.add(replyMsg);
        LocalStorageService.saveChatMessages(_allMessages);
        notifyListeners();
      });
    }
  }

  String _generateBotResponse(String text, String botName) {
    final lower = text.toLowerCase();
    if (lower.contains('কেমন আছো') || lower.contains('kemon aso')) {
      return 'আলহামদুলিল্লাহ ভালো আছি! আপনি কেমন আছেন? নাটক দেখছেন তো? 😊';
    }
    if (lower.contains('টাকা') || lower.contains('উইথড্র') || lower.contains('কয়েন')) {
      return 'প্রতিদিন নাটক দেখে ও স্পিন ঘুরিয়ে পয়েন্ট জমান, বিকাশ ও নগদে দ্রুত পেমেন্ট পেয়ে যাবেন! 💰';
    }
    if (lower.contains('হ্যালো') || lower.contains('হাই') || lower.contains('hi')) {
      return 'হ্যালো! আপনার সাথে আড্ডা দিতে পেরে ভালো লাগছে 🥰';
    }
    return 'দারুণ লাগলো আপনার কথা শুনে! নতুন কোনো ভালো বাংলা নাটক দেখলে আমাকেও জানাবেন ✨';
  }

  // Categories & Filtering
  void selectCategory(String category) {
    _selectedCategory = category;
    notifyListeners();
  }

  void searchVideos(String query) {
    _searchQuery = query;
    if (query.trim().isEmpty) {
      _isSearching = false;
      _searchResults = [];
    } else {
      _isSearching = true;
      final q = query.toLowerCase();
      _searchResults = _natoks.where((v) {
        return v.title.toLowerCase().contains(q) ||
            v.cast.toLowerCase().contains(q) ||
            v.category.toLowerCase().contains(q);
      }).toList();
    }
    notifyListeners();
  }

  // Admin Actions
  void approveWithdrawal(String id) {
    final idx = _allWithdrawals.indexWhere((w) => w.id == id);
    if (idx != -1) {
      _allWithdrawals[idx] = _allWithdrawals[idx].copyWith(status: 'Approved');
      final userIdx = _withdrawals.indexWhere((w) => w.id == id);
      if (userIdx != -1) {
        _withdrawals[userIdx] = _withdrawals[userIdx].copyWith(status: 'Approved');
      }
      LocalStorageService.saveWithdrawals(_withdrawals);
      notifyListeners();
    }
  }

  void rejectWithdrawal(String id) {
    final idx = _allWithdrawals.indexWhere((w) => w.id == id);
    if (idx != -1) {
      final req = _allWithdrawals[idx];
      _allWithdrawals[idx] = req.copyWith(status: 'Rejected');
      // Refund points to user
      addCoins(req.pointsDeducted, 'উইথড্র বাতিল ও রিফান্ড');
      final userIdx = _withdrawals.indexWhere((w) => w.id == id);
      if (userIdx != -1) {
        _withdrawals[userIdx] = _withdrawals[userIdx].copyWith(status: 'Rejected');
      }
      LocalStorageService.saveWithdrawals(_withdrawals);
      notifyListeners();
    }
  }

  void broadcastBonus(int coins, String reason) {
    addCoins(coins, 'অ্যাডমিন বোনাস: $reason');
  }

  void addNatok(VideoItem video) {
    _natoks.insert(0, video);
    notifyListeners();
  }

  void deleteNatok(String id) {
    _natoks.removeWhere((v) => v.id == id);
    notifyListeners();
  }

  void updateEconomy(EconomyConfig config) {
    _economyConfig = config;
    notifyListeners();
  }

  void exitAdminMode() {
    _currentScreen = AppScreen.mainDashboard;
    notifyListeners();
  }

  @override
  void dispose() {
    _stopVideoTimer();
    _adCountdownTimer?.cancel();
    super.dispose();
  }
}
