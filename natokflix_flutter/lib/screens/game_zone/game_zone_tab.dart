import 'dart:async';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../core/theme/app_colors.dart';
import '../../providers/natok_provider.dart';

class GameZoneTab extends StatefulWidget {
  const GameZoneTab({super.key});

  @override
  State<GameZoneTab> createState() => _GameZoneTabState();
}

class _GameZoneTabState extends State<GameZoneTab> {
  // Speed Tapper Mini Game state
  bool _isPlayingGame = false;
  int _gameTimeLeft = 10;
  int _tapCount = 0;
  Timer? _gameTimer;

  void _startGame() {
    setState(() {
      _isPlayingGame = true;
      _gameTimeLeft = 10;
      _tapCount = 0;
    });

    _gameTimer?.cancel();
    _gameTimer = Timer.periodic(const Duration(seconds: 1), (timer) {
      if (_gameTimeLeft > 0) {
        setState(() {
          _gameTimeLeft--;
        });
      } else {
        _endGame();
      }
    });
  }

  void _onTapCoin() {
    if (_isPlayingGame && _gameTimeLeft > 0) {
      setState(() {
        _tapCount++;
      });
    }
  }

  void _endGame() {
    _gameTimer?.cancel();
    setState(() {
      _isPlayingGame = false;
    });

    final coinsEarned = (_tapCount * 0.8).round();
    if (coinsEarned > 0) {
      context.read<NatokProvider>().addCoins(coinsEarned, 'স্পিড ট্যাপার গেম পুরষ্কার');
    }

    showDialog(
      context: context,
      builder: (ctx) => AlertDialog(
        title: Row(
          children: const [
            Icon(Icons.emoji_events, color: AppColors.natokGold),
            SizedBox(width: 8),
            Text('অভিনন্দন!', style: TextStyle(fontWeight: FontWeight.bold)),
          ],
        ),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Text(
              'আপনি ১০ সেকেন্ডে $_tapCount বার ট্যাপ করেছেন!',
              style: const TextStyle(fontSize: 14),
            ),
            const SizedBox(height: 12),
            Container(
              padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
              decoration: BoxDecoration(
                color: AppColors.natokGold.withOpacity(0.2),
                borderRadius: BorderRadius.circular(10),
                border: Border.all(color: AppColors.natokGold),
              ),
              child: Text(
                '🪙 +$coinsEarned কয়েন ওয়ালেটে জমা হয়েছে!',
                style: const TextStyle(fontSize: 15, fontWeight: FontWeight.bold, color: AppColors.natokGold),
              ),
            ),
          ],
        ),
        actions: [
          ElevatedButton(
            onPressed: () => Navigator.pop(ctx),
            style: ElevatedButton.styleFrom(backgroundColor: AppColors.natokRed),
            child: const Text('সংগ্রহ করুন', style: TextStyle(color: Colors.white)),
          ),
        ],
      ),
    );
  }

  @override
  void dispose() {
    _gameTimer?.cancel();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return SingleChildScrollView(
      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // Banner
          Container(
            padding: const EdgeInsets.all(18),
            decoration: BoxDecoration(
              gradient: const LinearGradient(
                colors: [Color(0xFF381005), Color(0xFF1E1428)],
                begin: Alignment.topLeft,
                end: Alignment.bottomRight,
              ),
              borderRadius: BorderRadius.circular(16),
              border: Border.all(color: AppColors.natokGold.withOpacity(0.6), width: 1.5),
            ),
            child: Row(
              children: [
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Container(
                        padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 3),
                        decoration: BoxDecoration(
                          color: AppColors.natokGold,
                          borderRadius: BorderRadius.circular(4),
                        ),
                        child: const Text(
                          'গেম খেলে কয়েন জিতুন',
                          style: TextStyle(fontSize: 10, fontWeight: FontWeight.bold, color: Colors.black),
                        ),
                      ),
                      const SizedBox(height: 8),
                      const Text(
                        'গেমিং জোন এরিনা 🎮',
                        style: TextStyle(fontSize: 20, fontWeight: FontWeight.w900, color: Colors.white),
                      ),
                      const SizedBox(height: 4),
                      const Text(
                        'মজার মিনি-গেম খেলুন, বন্ধুদের সাথে চ্যালেঞ্জ করুন আর জিতে নিন আনলিমিটেড পয়েন্ট।',
                        style: TextStyle(fontSize: 12, color: AppColors.textSecondary),
                      ),
                    ],
                  ),
                ),
                const Icon(Icons.sports_esports, color: AppColors.natokGold, size: 54),
              ],
            ),
          ),
          const SizedBox(height: 20),

          // Speed Tapper Live Game Arena
          Container(
            padding: const EdgeInsets.all(16),
            decoration: BoxDecoration(
              color: AppColors.surfaceCard,
              borderRadius: BorderRadius.circular(16),
              border: Border.all(color: AppColors.borderDark),
            ),
            child: Column(
              children: [
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Row(
                      children: const [
                        Icon(Icons.bolt, color: AppColors.natokGold, size: 22),
                        SizedBox(width: 6),
                        Text(
                          'স্পিড ট্যাপার - কয়েন রাশ ⚡',
                          style: TextStyle(fontSize: 15, fontWeight: FontWeight.bold, color: Colors.white),
                        ),
                      ],
                    ),
                    if (_isPlayingGame)
                      Container(
                        padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 4),
                        decoration: BoxDecoration(
                          color: AppColors.natokRed,
                          borderRadius: BorderRadius.circular(8),
                        ),
                        child: Text(
                          'সময়: $_gameTimeLeft সে.',
                          style: const TextStyle(fontSize: 12, fontWeight: FontWeight.bold, color: Colors.white),
                        ),
                      ),
                  ],
                ),
                const SizedBox(height: 12),
                const Text(
                  '১০ সেকেন্ড সময়! যত বেশি ট্যাপ করবেন, তত বেশি ফ্রি কয়েন আপনার ওয়ালেটে জমা হবে!',
                  textAlign: TextAlign.center,
                  style: TextStyle(fontSize: 12, color: AppColors.textSecondary),
                ),
                const SizedBox(height: 20),

                // Interactive Tap Button / Coin
                GestureDetector(
                  onTap: _onTapCoin,
                  child: Container(
                    width: 140,
                    height: 140,
                    decoration: BoxDecoration(
                      shape: BoxShape.circle,
                      gradient: RadialGradient(
                        colors: _isPlayingGame
                            ? [AppColors.natokGoldLight, AppColors.natokGold, const Color(0xFFCC8400)]
                            : [AppColors.surfaceVariantDark, AppColors.surfaceDark],
                      ),
                      boxShadow: _isPlayingGame
                          ? [
                              BoxShadow(
                                color: AppColors.natokGold.withOpacity(0.5),
                                blurRadius: 20,
                                spreadRadius: 4,
                              )
                            ]
                          : [],
                      border: Border.all(
                        color: _isPlayingGame ? Colors.white : AppColors.borderDark,
                        width: 3,
                      ),
                    ),
                    child: Center(
                      child: Column(
                        mainAxisAlignment: MainAxisAlignment.center,
                        children: [
                          Icon(
                            Icons.monetization_on,
                            size: 48,
                            color: _isPlayingGame ? Colors.black : AppColors.natokGold,
                          ),
                          const SizedBox(height: 4),
                          Text(
                            _isPlayingGame ? '$_tapCount' : 'ট্যাপ করুন',
                            style: TextStyle(
                              fontSize: _isPlayingGame ? 26 : 14,
                              fontWeight: FontWeight.w900,
                              color: _isPlayingGame ? Colors.black : Colors.white,
                            ),
                          ),
                        ],
                      ),
                    ),
                  ),
                ),
                const SizedBox(height: 20),

                if (!_isPlayingGame)
                  ElevatedButton.icon(
                    onPressed: _startGame,
                    icon: const Icon(Icons.play_arrow, color: Colors.white),
                    label: const Text('খেলা শুরু করুন 🚀', style: TextStyle(fontSize: 14, fontWeight: FontWeight.bold, color: Colors.white)),
                    style: ElevatedButton.styleFrom(
                      backgroundColor: AppColors.natokRed,
                      padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 12),
                      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
                    ),
                  )
                else
                  const Text(
                    'দ্রুত ট্যাপ করুন! 👆⚡',
                    style: TextStyle(fontSize: 14, fontWeight: FontWeight.bold, color: AppColors.natokGold),
                  ),
              ],
            ),
          ),
          const SizedBox(height: 20),

          // Other Games Cards
          const Text(
            'অন্যান্য আকর্ষণীয় গেম',
            style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold, color: Colors.white),
          ),
          const SizedBox(height: 12),

          _buildGameCard(
            title: 'নাটক কুইজ ও ব্রেইন চ্যালেঞ্জ 🧠',
            desc: 'বাংলা জনপ্রিয় নাটকের মজার প্রশ্নের উত্তর দিয়ে জিতে নিন প্রতি কুইজে ৫০ কয়েন।',
            reward: '+৫০ কয়েন',
            icon: Icons.quiz,
            color: const Color(0xFF1E88E5),
          ),
          const SizedBox(height: 10),
          _buildGameCard(
            title: 'লুডু ও কার্ড এরিনা (৪ প্লেয়ার AI) 🎲',
            desc: '৪ জন এআই বন্ধুর সাথে দ্রুত লুডু ম্যাচ খেলে চ্যাম্পিয়ন হয়ে মেগা রিওয়ার্ড পান।',
            reward: '+১০০ কয়েন',
            icon: Icons.casino,
            color: AppColors.rocketPurple,
          ),
          const SizedBox(height: 24),
        ],
      ),
    );
  }

  Widget _buildGameCard({
    required String title,
    required String desc,
    required String reward,
    required IconData icon,
    required Color color,
  }) {
    return Container(
      padding: const EdgeInsets.all(14),
      decoration: BoxDecoration(
        color: AppColors.surfaceCard,
        borderRadius: BorderRadius.circular(12),
        border: Border.all(color: AppColors.borderDark),
      ),
      child: Row(
        children: [
          Container(
            padding: const EdgeInsets.all(12),
            decoration: BoxDecoration(
              color: color.withOpacity(0.2),
              borderRadius: BorderRadius.circular(10),
            ),
            child: Icon(icon, color: color, size: 28),
          ),
          const SizedBox(width: 14),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Expanded(
                      child: Text(
                        title,
                        maxLines: 1,
                        overflow: TextOverflow.ellipsis,
                        style: const TextStyle(fontSize: 14, fontWeight: FontWeight.bold, color: Colors.white),
                      ),
                    ),
                    Container(
                      padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
                      decoration: BoxDecoration(
                        color: AppColors.natokGold.withOpacity(0.2),
                        borderRadius: BorderRadius.circular(4),
                      ),
                      child: Text(
                        reward,
                        style: const TextStyle(fontSize: 10, fontWeight: FontWeight.bold, color: AppColors.natokGold),
                      ),
                    ),
                  ],
                ),
                const SizedBox(height: 4),
                Text(
                  desc,
                  maxLines: 2,
                  overflow: TextOverflow.ellipsis,
                  style: const TextStyle(fontSize: 12, color: AppColors.textSecondary),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}
