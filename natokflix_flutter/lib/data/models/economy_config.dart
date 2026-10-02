class EconomyConfig {
  final int coinsPerBdtRate;
  final double minWithdrawalBdt;
  final int referrerBonusCoins;
  final int refereeBonusCoins;
  final int dailyCheckInBonus;
  final int freeDailySpinsLimit;
  final int shortsRewardCoins;
  final int rewardedVideoAdCoins;
  final int interstitialAdCoins;
  final int reelWatchCoins;
  final bool isBkashEnabled;
  final bool isNagadEnabled;
  final bool isRocketEnabled;
  final bool isMaintenanceMode;
  final String maintenanceMessage;
  final String announcementNotice;
  final bool isAnnouncementEnabled;
  final String featuredPromoTitle;
  final String featuredPromoSubtitle;
  final String telegramChannelUrl;
  final String whatsappSupportNumber;
  final String withdrawalRulesText;
  final int updatedAt;

  const EconomyConfig({
    this.coinsPerBdtRate = 10,
    this.minWithdrawalBdt = 300.0,
    this.referrerBonusCoins = 200,
    this.refereeBonusCoins = 100,
    this.dailyCheckInBonus = 20,
    this.freeDailySpinsLimit = 1,
    this.shortsRewardCoins = 5,
    this.rewardedVideoAdCoins = 10,
    this.interstitialAdCoins = 5,
    this.reelWatchCoins = 2,
    this.isBkashEnabled = true,
    this.isNagadEnabled = true,
    this.isRocketEnabled = true,
    this.isMaintenanceMode = false,
    this.maintenanceMessage = 'অ্যাপ সার্ভার আপগ্রেড চলছে। সাময়িক অসুবিধার জন্য আমরা আন্তরিকভাবে দুঃখিত।',
    this.announcementNotice = '📢 স্বাগতম NatokFlix-এ! আজ ভিডিও দেখে আর্ন করুন দ্বিগুণ পয়েন্ট। বিকাশ ও নগদে উত্তোলন চালু রয়েছে।',
    this.isAnnouncementEnabled = true,
    this.featuredPromoTitle = 'ঈদের বিশেষ নাটক ও ধামাকা শর্টস',
    this.featuredPromoSubtitle = 'এখনই দেখুন এবং প্রতিদিন ৩০০+ টাকা আয় করুন',
    this.telegramChannelUrl = 'https://t.me/natokflix_official',
    this.whatsappSupportNumber = '+8801700000000',
    this.withdrawalRulesText = 'উত্তোলনের নিয়মাবলি:\n১. সর্বনিম্ন উত্তোলন ৩০০ টাকা।\n২. উইথড্র সময়সূচি: প্রতিদিন সকাল ১০:০০ টা থেকে রাত ০৮:০০ টা পর্যন্ত উইথড্র সিস্টেম সক্রিয় থাকে। রাত ৮:০১ থেকে পরদিন সকাল ৯:৫৯ পর্যন্ত বন্ধ থাকে।\n৩. ২৪ ঘণ্টার মধ্যে পেমেন্ট বিকাশ, নগদ বা রকেটে পৌঁছে যাবে।\n৪. সঠিক ১১ ডিজিটের মোবাইল নম্বর প্রদান করতে হবে।\n৫. কোনো ভিপিএন ব্যবহার করা যাবে না। ভুল তথ্য দিলে পেমেন্ট বাতিল হতে পারে।',
    this.updatedAt = 0,
  });

  EconomyConfig copyWith({
    int? coinsPerBdtRate,
    double? minWithdrawalBdt,
    int? referrerBonusCoins,
    int? refereeBonusCoins,
    int? dailyCheckInBonus,
    int? freeDailySpinsLimit,
    int? shortsRewardCoins,
    int? rewardedVideoAdCoins,
    int? interstitialAdCoins,
    int? reelWatchCoins,
    bool? isBkashEnabled,
    bool? isNagadEnabled,
    bool? isRocketEnabled,
    bool? isMaintenanceMode,
    String? maintenanceMessage,
    String? announcementNotice,
    bool? isAnnouncementEnabled,
    String? featuredPromoTitle,
    String? featuredPromoSubtitle,
    String? telegramChannelUrl,
    String? whatsappSupportNumber,
    String? withdrawalRulesText,
    int? updatedAt,
  }) {
    return EconomyConfig(
      coinsPerBdtRate: coinsPerBdtRate ?? this.coinsPerBdtRate,
      minWithdrawalBdt: minWithdrawalBdt ?? this.minWithdrawalBdt,
      referrerBonusCoins: referrerBonusCoins ?? this.referrerBonusCoins,
      refereeBonusCoins: refereeBonusCoins ?? this.refereeBonusCoins,
      dailyCheckInBonus: dailyCheckInBonus ?? this.dailyCheckInBonus,
      freeDailySpinsLimit: freeDailySpinsLimit ?? this.freeDailySpinsLimit,
      shortsRewardCoins: shortsRewardCoins ?? this.shortsRewardCoins,
      rewardedVideoAdCoins: rewardedVideoAdCoins ?? this.rewardedVideoAdCoins,
      interstitialAdCoins: interstitialAdCoins ?? this.interstitialAdCoins,
      reelWatchCoins: reelWatchCoins ?? this.reelWatchCoins,
      isBkashEnabled: isBkashEnabled ?? this.isBkashEnabled,
      isNagadEnabled: isNagadEnabled ?? this.isNagadEnabled,
      isRocketEnabled: isRocketEnabled ?? this.isRocketEnabled,
      isMaintenanceMode: isMaintenanceMode ?? this.isMaintenanceMode,
      maintenanceMessage: maintenanceMessage ?? this.maintenanceMessage,
      announcementNotice: announcementNotice ?? this.announcementNotice,
      isAnnouncementEnabled: isAnnouncementEnabled ?? this.isAnnouncementEnabled,
      featuredPromoTitle: featuredPromoTitle ?? this.featuredPromoTitle,
      featuredPromoSubtitle: featuredPromoSubtitle ?? this.featuredPromoSubtitle,
      telegramChannelUrl: telegramChannelUrl ?? this.telegramChannelUrl,
      whatsappSupportNumber: whatsappSupportNumber ?? this.whatsappSupportNumber,
      withdrawalRulesText: withdrawalRulesText ?? this.withdrawalRulesText,
      updatedAt: updatedAt ?? this.updatedAt,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'coinsPerBdtRate': coinsPerBdtRate,
      'minWithdrawalBdt': minWithdrawalBdt,
      'referrerBonusCoins': referrerBonusCoins,
      'refereeBonusCoins': refereeBonusCoins,
      'dailyCheckInBonus': dailyCheckInBonus,
      'freeDailySpinsLimit': freeDailySpinsLimit,
      'shortsRewardCoins': shortsRewardCoins,
      'rewardedVideoAdCoins': rewardedVideoAdCoins,
      'interstitialAdCoins': interstitialAdCoins,
      'reelWatchCoins': reelWatchCoins,
      'isBkashEnabled': isBkashEnabled,
      'isNagadEnabled': isNagadEnabled,
      'isRocketEnabled': isRocketEnabled,
      'isMaintenanceMode': isMaintenanceMode,
      'maintenanceMessage': maintenanceMessage,
      'announcementNotice': announcementNotice,
      'isAnnouncementEnabled': isAnnouncementEnabled,
      'featuredPromoTitle': featuredPromoTitle,
      'featuredPromoSubtitle': featuredPromoSubtitle,
      'telegramChannelUrl': telegramChannelUrl,
      'whatsappSupportNumber': whatsappSupportNumber,
      'withdrawalRulesText': withdrawalRulesText,
      'updatedAt': updatedAt,
    };
  }

  factory EconomyConfig.fromJson(Map<String, dynamic> json) {
    return EconomyConfig(
      coinsPerBdtRate: json['coinsPerBdtRate'] ?? 10,
      minWithdrawalBdt: (json['minWithdrawalBdt'] as num?)?.toDouble() ?? 300.0,
      referrerBonusCoins: json['referrerBonusCoins'] ?? 200,
      refereeBonusCoins: json['refereeBonusCoins'] ?? 100,
      dailyCheckInBonus: json['dailyCheckInBonus'] ?? 20,
      freeDailySpinsLimit: json['freeDailySpinsLimit'] ?? 1,
      shortsRewardCoins: json['shortsRewardCoins'] ?? 5,
      rewardedVideoAdCoins: json['rewardedVideoAdCoins'] ?? 10,
      interstitialAdCoins: json['interstitialAdCoins'] ?? 5,
      reelWatchCoins: json['reelWatchCoins'] ?? 2,
      isBkashEnabled: json['isBkashEnabled'] ?? true,
      isNagadEnabled: json['isNagadEnabled'] ?? true,
      isRocketEnabled: json['isRocketEnabled'] ?? true,
      isMaintenanceMode: json['isMaintenanceMode'] ?? false,
      maintenanceMessage: json['maintenanceMessage'] ?? 'অ্যাপ সার্ভার আপগ্রেড চলছে। সাময়িক অসুবিধার জন্য আমরা আন্তরিকভাবে দুঃখিত।',
      announcementNotice: json['announcementNotice'] ?? '📢 স্বাগতম NatokFlix-এ! আজ ভিডিও দেখে আর্ন করুন দ্বিগুণ পয়েন্ট। বিকাশ ও নগদে উত্তোলন চালু রয়েছে।',
      isAnnouncementEnabled: json['isAnnouncementEnabled'] ?? true,
      featuredPromoTitle: json['featuredPromoTitle'] ?? 'ঈদের বিশেষ নাটক ও ধামাকা শর্টস',
      featuredPromoSubtitle: json['featuredPromoSubtitle'] ?? 'এখনই দেখুন এবং প্রতিদিন ৩০০+ টাকা আয় করুন',
      telegramChannelUrl: json['telegramChannelUrl'] ?? 'https://t.me/natokflix_official',
      whatsappSupportNumber: json['whatsappSupportNumber'] ?? '+8801700000000',
      withdrawalRulesText: json['withdrawalRulesText'] ?? '',
      updatedAt: json['updatedAt'] ?? 0,
    );
  }
}
