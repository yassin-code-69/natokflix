package com.example.data.model

data class EconomyConfig(
    val coinsPerBdtRate: Int = 10,
    val minWithdrawalBdt: Double = 300.0,
    val referrerBonusCoins: Int = 200,
    val refereeBonusCoins: Int = 100,
    val dailyCheckInBonus: Int = 20,
    val freeDailySpinsLimit: Int = 1,
    val shortsRewardCoins: Int = 5,
    val rewardedVideoAdCoins: Int = 10,
    val interstitialAdCoins: Int = 5,
    val reelWatchCoins: Int = 2,
    val isBkashEnabled: Boolean = true,
    val isNagadEnabled: Boolean = true,
    val isRocketEnabled: Boolean = true,
    val isMaintenanceMode: Boolean = false,
    val maintenanceMessage: String = "অ্যাপ সার্ভার আপগ্রেড চলছে। সাময়িক অসুবিধার জন্য আমরা আন্তরিকভাবে দুঃখিত।",
    val announcementNotice: String = "📢 স্বাগতম NatokFlix-এ! আজ ভিডিও দেখে আর্ন করুন দ্বিগুণ পয়েন্ট। বিকাশ ও নগদে উত্তোলন চালু রয়েছে।",
    val isAnnouncementEnabled: Boolean = true,
    val featuredPromoTitle: String = "ঈদের বিশেষ নাটক ও ধামাকা শর্টস",
    val featuredPromoSubtitle: String = "এখনই দেখুন এবং প্রতিদিন ৩০০+ টাকা আয় করুন",
    val telegramChannelUrl: String = "https://t.me/natokflix_official",
    val whatsappSupportNumber: String = "+8801700000000",
    val withdrawalRulesText: String = "উত্তোলনের নিয়মাবলি:\n১. সর্বনিম্ন উত্তোলন ৩০০ টাকা।\n২. উইথড্র সময়সূচি: প্রতিদিন সকাল ১০:০০ টা থেকে রাত ০৮:০০ টা পর্যন্ত উইথড্র সিস্টেম সক্রিয় থাকে। রাত ৮:০১ থেকে পরদিন সকাল ৯:৫৯ পর্যন্ত বন্ধ থাকে।\n৩. ২৪ ঘণ্টার মধ্যে পেমেন্ট বিকাশ, নগদ বা রকেটে পৌঁছে যাবে।\n৪. সঠিক ১১ ডিজিটের মোবাইল নম্বর প্রদান করতে হবে।\n৫. কোনো ভিপিএন ব্যবহার করা যাবে না। ভুল তথ্য দিলে পেমেন্ট বাতিল হতে পারে।",
    val updatedAt: Long = System.currentTimeMillis()
)
