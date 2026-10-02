import 'dart:async';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:cached_network_image/cached_network_image.dart';
import '../../core/theme/app_colors.dart';
import '../../providers/natok_provider.dart';

class ShortsTab extends StatefulWidget {
  const ShortsTab({super.key});

  @override
  State<ShortsTab> createState() => _ShortsTabState();
}

class _ShortsTabState extends State<ShortsTab> {
  final PageController _pageController = PageController();
  int _currentIndex = 0;
  final Map<int, bool> _likedVideos = {};
  double _watchProgress = 0.0;
  Timer? _countdownTimer;

  @override
  void initState() {
    super.initState();
    _startShortWatchCountdown();
  }

  void _startShortWatchCountdown() {
    _countdownTimer?.cancel();
    _watchProgress = 0.0;
    _countdownTimer = Timer.periodic(const Duration(milliseconds: 100), (timer) {
      if (!mounted) return;
      setState(() {
        _watchProgress += 0.02; // completes in 5 seconds
        if (_watchProgress >= 1.0) {
          _countdownTimer?.cancel();
          final provider = context.read<NatokProvider>();
          if (provider.shorts.isNotEmpty && _currentIndex < provider.shorts.length) {
            provider.rewardShortsWatch(provider.shorts[_currentIndex]);
            ScaffoldMessenger.of(context).showSnackBar(
              SnackBar(
                content: Row(
                  children: const [
                    Icon(Icons.monetization_on, color: AppColors.natokGold, size: 18),
                    SizedBox(width: 8),
                    Text('+৫ কয়েন শর্টস দেখার রিওয়ার্ড যোগ হয়েছে!'),
                  ],
                ),
                backgroundColor: const Color(0xFF2A1C0A),
                duration: const Duration(seconds: 2),
              ),
            );
          }
        }
      });
    });
  }

  @override
  void dispose() {
    _countdownTimer?.cancel();
    _pageController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final provider = context.watch<NatokProvider>();
    final shorts = provider.shorts;

    if (shorts.isEmpty) {
      return const Center(
        child: Text('কোনো শর্টস পাওয়া যায়নি', style: TextStyle(color: AppColors.textSecondary)),
      );
    }

    return Scaffold(
      backgroundColor: Colors.black,
      body: PageView.builder(
        controller: _pageController,
        scrollDirection: Axis.vertical,
        itemCount: shorts.length,
        onPageChanged: (idx) {
          setState(() {
            _currentIndex = idx;
          });
          _startShortWatchCountdown();
        },
        itemBuilder: (context, index) {
          final short = shorts[index];
          final isLiked = _likedVideos[index] ?? false;

          return Stack(
            fit: StackFit.expand,
            children: [
              // Video Background Poster / Preview
              CachedNetworkImage(
                imageUrl: short.thumbnailUrl,
                fit: BoxFit.cover,
                placeholder: (_, __) => Container(color: Colors.black),
                errorWidget: (_, __, ___) => Container(
                  color: Colors.black,
                  child: const Center(
                    child: Icon(Icons.play_circle_outline, color: Colors.white38, size: 64),
                  ),
                ),
              ),

              // Gradient Darkening overlays
              Positioned.fill(
                child: Container(
                  decoration: const BoxDecoration(
                    gradient: LinearGradient(
                      begin: Alignment.topCenter,
                      end: Alignment.bottomCenter,
                      colors: [
                        Color(0x66000000),
                        Colors.transparent,
                        Color(0xCC000000),
                      ],
                      stops: [0.0, 0.4, 1.0],
                    ),
                  ),
                ),
              ),

              // Center Play/Action Overlay
              Center(
                child: InkWell(
                  onTap: () {
                    provider.playVideo(short);
                  },
                  child: Container(
                    padding: const EdgeInsets.all(16),
                    decoration: BoxDecoration(
                      shape: BoxShape.circle,
                      color: Colors.black.withOpacity(0.4),
                      border: Border.all(color: Colors.white24, width: 2),
                    ),
                    child: const Icon(Icons.play_arrow, color: Colors.white, size: 44),
                  ),
                ),
              ),

              // Floating Actions on Right
              Positioned(
                right: 16,
                bottom: 80,
                child: Column(
                  children: [
                    // Coin Countdown Progress Indicator
                    Stack(
                      alignment: Alignment.center,
                      children: [
                        SizedBox(
                          width: 44,
                          height: 44,
                          child: CircularProgressIndicator(
                            value: _watchProgress.clamp(0.0, 1.0),
                            backgroundColor: Colors.white24,
                            color: AppColors.natokGold,
                            strokeWidth: 3,
                          ),
                        ),
                        const Icon(Icons.monetization_on, color: AppColors.natokGold, size: 22),
                      ],
                    ),
                    const SizedBox(height: 4),
                    const Text(
                      '+৫ 🪙',
                      style: TextStyle(
                        color: AppColors.natokGold,
                        fontSize: 10,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                    const SizedBox(height: 20),

                    // Like Button
                    IconButton(
                      icon: Icon(
                        isLiked ? Icons.favorite : Icons.favorite_border,
                        color: isLiked ? AppColors.natokRed : Colors.white,
                        size: 32,
                      ),
                      onPressed: () {
                        setState(() {
                          _likedVideos[index] = !isLiked;
                        });
                      },
                    ),
                    const Text(
                      '১২.৪K',
                      style: TextStyle(color: Colors.white, fontSize: 11, fontWeight: FontWeight.w600),
                    ),
                    const SizedBox(height: 16),

                    // Comments Button
                    IconButton(
                      icon: const Icon(Icons.comment, color: Colors.white, size: 28),
                      onPressed: () {
                        ScaffoldMessenger.of(context).showSnackBar(
                          const SnackBar(content: Text('কমেন্ট ফিচার শিঘ্রই চালু হচ্ছে')),
                        );
                      },
                    ),
                    const Text(
                      '৪৫০',
                      style: TextStyle(color: Colors.white, fontSize: 11, fontWeight: FontWeight.w600),
                    ),
                    const SizedBox(height: 16),

                    // Share Button
                    IconButton(
                      icon: const Icon(Icons.share, color: Colors.white, size: 28),
                      onPressed: () {
                        ScaffoldMessenger.of(context).showSnackBar(
                          const SnackBar(content: Text('শর্টস লিংক কপি করা হয়েছে!')),
                        );
                      },
                    ),
                    const Text(
                      'শেয়ার',
                      style: TextStyle(color: Colors.white, fontSize: 11, fontWeight: FontWeight.w600),
                    ),
                  ],
                ),
              ),

              // Bottom Caption & Creator Details
              Positioned(
                left: 16,
                right: 80,
                bottom: 30,
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Row(
                      children: [
                        Container(
                          padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 3),
                          decoration: BoxDecoration(
                            color: AppColors.natokRed,
                            borderRadius: BorderRadius.circular(4),
                          ),
                          child: const Text(
                            'নাটক শর্টস',
                            style: TextStyle(
                              fontSize: 10,
                              fontWeight: FontWeight.bold,
                              color: Colors.white,
                            ),
                          ),
                        ),
                        const SizedBox(width: 8),
                        Text(
                          '@${short.channelTitle}',
                          style: const TextStyle(
                            fontSize: 13,
                            fontWeight: FontWeight.bold,
                            color: Colors.white,
                          ),
                        ),
                      ],
                    ),
                    const SizedBox(height: 8),
                    Text(
                      short.title,
                      maxLines: 2,
                      overflow: TextOverflow.ellipsis,
                      style: const TextStyle(
                        fontSize: 14,
                        fontWeight: FontWeight.w600,
                        color: Colors.white,
                        height: 1.3,
                      ),
                    ),
                    const SizedBox(height: 6),
                    Row(
                      children: [
                        const Icon(Icons.person, color: AppColors.textSecondary, size: 14),
                        const SizedBox(width: 4),
                        Text(
                          short.cast,
                          style: const TextStyle(
                            fontSize: 12,
                            color: AppColors.natokGoldLight,
                          ),
                        ),
                      ],
                    ),
                  ],
                ),
              ),
            ],
          );
        },
      ),
    );
  }
}
