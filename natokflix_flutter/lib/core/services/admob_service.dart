import 'package:flutter/foundation.dart';
import 'package:google_mobile_ads/google_mobile_ads.dart';

class AdmobService {
  // Production Real AdMob IDs from .env
  static const String appId = 'ca-app-pub-2897070588148846~7538724951';
  static const String bannerId = 'ca-app-pub-2897070588148846/7383560718';
  static const String interstitialId = 'ca-app-pub-2897070588148846/1015501900';
  static const String rewardedId = 'ca-app-pub-2897070588148846/1308358523';
  static const String appOpenId = 'ca-app-pub-2897070588148846/7538724951';

  static bool get isMobile =>
      !kIsWeb &&
      (defaultTargetPlatform == TargetPlatform.android ||
          defaultTargetPlatform == TargetPlatform.iOS);

  static InterstitialAd? _interstitialAd;
  static RewardedAd? _rewardedAd;
  static AppOpenAd? _appOpenAd;

  static Future<void> initialize() async {
    if (isMobile) {
      try {
        await MobileAds.instance.initialize();
        loadInterstitialAd();
        loadRewardedAd();
        loadAppOpenAd();
      } catch (e) {
        debugPrint('AdMob initialization error: $e');
      }
    }
  }

  // Interstitial Ad
  static void loadInterstitialAd() {
    if (!isMobile) return;
    InterstitialAd.load(
      adUnitId: interstitialId,
      request: const AdRequest(),
      adLoadCallback: InterstitialAdLoadCallback(
        onAdLoaded: (ad) {
          _interstitialAd = ad;
        },
        onAdFailedToLoad: (error) {
          _interstitialAd = null;
        },
      ),
    );
  }

  static void showInterstitialAd({VoidCallback? onDismissed}) {
    if (!isMobile || _interstitialAd == null) {
      onDismissed?.call();
      return;
    }

    _interstitialAd!.fullScreenContentCallback = FullScreenContentCallback(
      onAdDismissedFullScreenContent: (ad) {
        ad.dispose();
        _interstitialAd = null;
        loadInterstitialAd();
        onDismissed?.call();
      },
      onAdFailedToShowFullScreenContent: (ad, error) {
        ad.dispose();
        _interstitialAd = null;
        loadInterstitialAd();
        onDismissed?.call();
      },
    );

    _interstitialAd!.show();
  }

  // Rewarded Video Ad
  static void loadRewardedAd() {
    if (!isMobile) return;
    RewardedAd.load(
      adUnitId: rewardedId,
      request: const AdRequest(),
      rewardedAdLoadCallback: RewardedAdLoadCallback(
        onAdLoaded: (ad) {
          _rewardedAd = ad;
        },
        onAdFailedToLoad: (error) {
          _rewardedAd = null;
        },
      ),
    );
  }

  static void showRewardedAd({
    required Function(RewardItem reward) onUserEarnedReward,
    VoidCallback? onDismissed,
  }) {
    if (!isMobile || _rewardedAd == null) {
      onUserEarnedReward(RewardItem(10, 'coins'));
      onDismissed?.call();
      return;
    }

    _rewardedAd!.fullScreenContentCallback = FullScreenContentCallback(
      onAdDismissedFullScreenContent: (ad) {
        ad.dispose();
        _rewardedAd = null;
        loadRewardedAd();
        onDismissed?.call();
      },
      onAdFailedToShowFullScreenContent: (ad, error) {
        ad.dispose();
        _rewardedAd = null;
        loadRewardedAd();
        onDismissed?.call();
      },
    );

    _rewardedAd!.show(
      onUserEarnedReward: (_, reward) {
        onUserEarnedReward(reward);
      },
    );
  }

  // App Open Ad
  static void loadAppOpenAd() {
    if (!isMobile) return;
    AppOpenAd.load(
      adUnitId: appOpenId,
      request: const AdRequest(),
      adLoadCallback: AppOpenAdLoadCallback(
        onAdLoaded: (ad) {
          _appOpenAd = ad;
        },
        onAdFailedToLoad: (error) {
          _appOpenAd = null;
        },
      ),
    );
  }

  static void showAppOpenAd({VoidCallback? onDismissed}) {
    if (!isMobile || _appOpenAd == null) {
      onDismissed?.call();
      return;
    }

    _appOpenAd!.fullScreenContentCallback = FullScreenContentCallback(
      onAdDismissedFullScreenContent: (ad) {
        ad.dispose();
        _appOpenAd = null;
        loadAppOpenAd();
        onDismissed?.call();
      },
      onAdFailedToShowFullScreenContent: (ad, error) {
        ad.dispose();
        _appOpenAd = null;
        loadAppOpenAd();
        onDismissed?.call();
      },
    );

    _appOpenAd!.show();
  }
}
