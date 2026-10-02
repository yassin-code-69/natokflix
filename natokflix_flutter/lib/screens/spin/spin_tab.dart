import 'dart:math';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../core/services/admob_service.dart';
import '../../core/theme/app_colors.dart';
import '../../providers/natok_provider.dart';

class SpinTab extends StatefulWidget {
  const SpinTab({super.key});

  @override
  State<SpinTab> createState() => _SpinTabState();
}

class _SpinTabState extends State<SpinTab> with SingleTickerProviderStateMixin {
  late AnimationController _animController;
  late Animation<double> _rotationAnimation;
  double _currentAngle = 0.0;
  final List<int> _prizes = [10, 20, 50, 100, 200, 500, 15, 25];

  @override
  void initState() {
    super.initState();
    _animController = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 3500),
    );
  }

  @override
  void dispose() {
    _animController.dispose();
    super.dispose();
  }

  void _onSpinPressed(NatokProvider provider) async {
    if (provider.isSpinning) return;
    if (provider.freeSpinsRemaining <= 0 && provider.adSpinsUnlocked <= 0) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          content: Text('আজকের ফ্রি স্পিন শেষ! নিচে বিজ্ঞাপন দেখে অতিরিক্ত স্পিন আনলক করুন।'),
          backgroundColor: AppColors.statusPending,
        ),
      );
      return;
    }

    final winningPoints = await provider.spinWheel();
    final winningIndex = _prizes.indexOf(winningPoints);

    // Calculate rotation: 5 full turns + target segment offset
    final segmentAngle = (2 * pi) / _prizes.length;
    final targetSegmentAngle = winningIndex >= 0 ? (winningIndex * segmentAngle) : 0.0;
    final totalTarget = (5 * 2 * pi) + targetSegmentAngle;

    _rotationAnimation = Tween<double>(
      begin: _currentAngle,
      end: _currentAngle + totalTarget,
    ).animate(CurvedAnimation(parent: _animController, curve: Curves.easeOutCubic));

    _animController.forward(from: 0.0).then((_) {
      _currentAngle = (_currentAngle + totalTarget) % (2 * pi);
      _showWinnerDialog(winningPoints);
    });
  }

  void _showWinnerDialog(int points) {
    showDialog(
      context: context,
      builder: (ctx) => AlertDialog(
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
        title: Row(
          children: const [
            Icon(Icons.emoji_events, color: AppColors.natokGold, size: 28),
            SizedBox(width: 8),
            Text('বিশাল জয়! 🎉', style: TextStyle(fontWeight: FontWeight.bold)),
          ],
        ),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            const Text(
              'আপনি লাকি স্পিন হুইল থেকে জিতেছেন:',
              style: TextStyle(fontSize: 14, color: AppColors.textSecondary),
            ),
            const SizedBox(height: 16),
            Container(
              padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 12),
              decoration: BoxDecoration(
                gradient: const LinearGradient(
                  colors: [Color(0xFF3B2A00), Color(0xFF1E1400)],
                ),
                borderRadius: BorderRadius.circular(12),
                border: Border.all(color: AppColors.natokGold, width: 1.5),
              ),
              child: Row(
                mainAxisSize: MainAxisSize.min,
                children: [
                  const Icon(Icons.monetization_on, color: AppColors.natokGold, size: 30),
                  const SizedBox(width: 10),
                  Text(
                    '+$points কয়েন',
                    style: const TextStyle(fontSize: 22, fontWeight: FontWeight.w900, color: AppColors.natokGold),
                  ),
                ],
              ),
            ),
          ],
        ),
        actions: [
          ElevatedButton(
            onPressed: () => Navigator.pop(ctx),
            style: ElevatedButton.styleFrom(backgroundColor: AppColors.natokRed),
            child: const Text('ওয়ালেটে নিন', style: TextStyle(color: Colors.white, fontWeight: FontWeight.bold)),
          ),
        ],
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    final provider = context.watch<NatokProvider>();
    final canSpin = (provider.freeSpinsRemaining > 0 || provider.adSpinsUnlocked > 0) && !provider.isSpinning;

    return SingleChildScrollView(
      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
      child: Column(
        children: [
          // Header info
          Container(
            padding: const EdgeInsets.all(16),
            decoration: BoxDecoration(
              gradient: const LinearGradient(
                colors: [Color(0xFF381405), AppColors.surfaceCard],
              ),
              borderRadius: BorderRadius.circular(16),
              border: Border.all(color: AppColors.natokGold.withOpacity(0.4)),
            ),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    const Text(
                      'লাকি স্পিন হুইল 🎰',
                      style: TextStyle(fontSize: 18, fontWeight: FontWeight.w900, color: Colors.white),
                    ),
                    const SizedBox(height: 4),
                    Text(
                      'আজকের ফ্রি স্পিন: ${provider.freeSpinsRemaining} টি বাকি',
                      style: const TextStyle(fontSize: 12, color: AppColors.natokGoldLight),
                    ),
                  ],
                ),
                Container(
                  padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 5),
                  decoration: BoxDecoration(
                    color: AppColors.surfaceVariantDark,
                    borderRadius: BorderRadius.circular(8),
                    border: Border.all(color: AppColors.borderDark),
                  ),
                  child: Row(
                    children: [
                      const Icon(Icons.bolt, color: AppColors.natokGold, size: 16),
                      const SizedBox(width: 4),
                      Text(
                        'বোনাস: ${provider.adSpinsUnlocked}',
                        style: const TextStyle(fontSize: 12, fontWeight: FontWeight.bold, color: Colors.white),
                      ),
                    ],
                  ),
                ),
              ],
            ),
          ),
          const SizedBox(height: 24),

          // The Wheel & Pointer Container
          Center(
            child: SizedBox(
              width: 290,
              height: 290,
              child: Stack(
                alignment: Alignment.center,
                children: [
                  // Outer Glow Ring
                  Container(
                    width: 280,
                    height: 280,
                    decoration: BoxDecoration(
                      shape: BoxShape.circle,
                      boxShadow: [
                        BoxShadow(
                          color: AppColors.natokGold.withOpacity(0.35),
                          blurRadius: 24,
                          spreadRadius: 2,
                        ),
                      ],
                    ),
                  ),

                  // Rotating Wheel Canvas
                  AnimatedBuilder(
                    animation: _animController,
                    builder: (context, child) {
                      final angle = _animController.isAnimating ? _rotationAnimation.value : _currentAngle;
                      return Transform.rotate(
                        angle: angle,
                        child: child,
                      );
                    },
                    child: CustomPaint(
                      size: const Size(270, 270),
                      painter: SpinWheelPainter(prizes: _prizes),
                    ),
                  ),

                  // Central Golden Cap
                  Container(
                    width: 58,
                    height: 58,
                    decoration: BoxDecoration(
                      shape: BoxShape.circle,
                      gradient: const RadialGradient(
                        colors: [AppColors.natokGoldLight, AppColors.natokGold, Color(0xFFCC8400)],
                      ),
                      border: Border.all(color: Colors.white, width: 2),
                      boxShadow: [
                        BoxShadow(
                          color: Colors.black.withOpacity(0.5),
                          blurRadius: 6,
                        ),
                      ],
                    ),
                    child: const Center(
                      child: Icon(Icons.casino, color: Colors.black, size: 28),
                    ),
                  ),

                  // Top Pointer Needle
                  Positioned(
                    top: 0,
                    child: CustomPaint(
                      size: const Size(24, 28),
                      painter: PointerNeedlePainter(),
                    ),
                  ),
                ],
              ),
            ),
          ),
          const SizedBox(height: 28),

          // Spin Action Button
          SizedBox(
            width: double.infinity,
            height: 50,
            child: ElevatedButton.icon(
              onPressed: canSpin ? () => _onSpinPressed(provider) : null,
              icon: const Icon(Icons.rotate_right, color: Colors.white, size: 22),
              label: Text(
                provider.isSpinning
                    ? 'হুইল ঘুরছে...'
                    : (canSpin ? 'এখনই স্পিন করুন 🎰' : 'স্পিন শেষ! অ্যাড দেখুন'),
                style: const TextStyle(fontSize: 16, fontWeight: FontWeight.bold, color: Colors.white),
              ),
              style: ElevatedButton.styleFrom(
                backgroundColor: AppColors.natokRed,
                disabledBackgroundColor: AppColors.surfaceVariantDark,
                shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
              ),
            ),
          ),
          const SizedBox(height: 14),

          // Unlock Ad Spin Button
          SizedBox(
            width: double.infinity,
            height: 44,
            child: OutlinedButton.icon(
              onPressed: () {
                AdmobService.showRewardedAd(
                  onUserEarnedReward: (_) {
                    provider.unlockAdSpin();
                    ScaffoldMessenger.of(context).showSnackBar(
                      const SnackBar(
                        content: Text('🎉 বিজ্ঞাপন দেখে ১টি অতিরিক্ত লাকি স্পিন আনলক হয়েছে!'),
                        backgroundColor: AppColors.statusSuccess,
                      ),
                    );
                  },
                );
              },
              icon: const Icon(Icons.play_circle, color: AppColors.natokGold, size: 18),
              label: const Text(
                'বিজ্ঞাপন দেখে অতিরিক্ত স্পিন পান 🎬',
                style: TextStyle(fontSize: 13, fontWeight: FontWeight.bold, color: AppColors.natokGold),
              ),
              style: OutlinedButton.styleFrom(
                side: const BorderSide(color: AppColors.natokGold),
                shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
              ),
            ),
          ),
          const SizedBox(height: 24),
        ],
      ),
    );
  }
}

class SpinWheelPainter extends CustomPainter {
  final List<int> prizes;

  SpinWheelPainter({required this.prizes});

  @override
  void paint(Canvas canvas, Size size) {
    final center = Offset(size.width / 2, size.height / 2);
    final radius = size.width / 2;
    final sweepAngle = (2 * pi) / prizes.length;

    final segmentColors = [
      const Color(0xFF8B0000),
      const Color(0xFF1F1F2B),
      const Color(0xFFB8860B),
      const Color(0xFF28182E),
      const Color(0xFF8B0000),
      const Color(0xFF1F1F2B),
      const Color(0xFFB8860B),
      const Color(0xFF28182E),
    ];

    for (int i = 0; i < prizes.length; i++) {
      final startAngle = i * sweepAngle;

      final paint = Paint()
        ..color = segmentColors[i % segmentColors.length]
        ..style = PaintingStyle.fill;

      canvas.drawArc(
        Rect.fromCircle(center: center, radius: radius),
        startAngle,
        sweepAngle,
        true,
        paint,
      );

      // Border between segments
      final linePaint = Paint()
        ..color = AppColors.natokGoldLight.withOpacity(0.5)
        ..strokeWidth = 1.5
        ..style = PaintingStyle.stroke;

      canvas.drawLine(
        center,
        Offset(center.dx + radius * cos(startAngle), center.dy + radius * sin(startAngle)),
        linePaint,
      );

      // Text inside segment
      final textAngle = startAngle + sweepAngle / 2;
      final textRadius = radius * 0.65;
      final textOffset = Offset(
        center.dx + textRadius * cos(textAngle),
        center.dy + textRadius * sin(textAngle),
      );

      final textSpan = TextSpan(
        text: '+${prizes[i]}',
        style: const TextStyle(
          color: Colors.white,
          fontSize: 13,
          fontWeight: FontWeight.w900,
        ),
      );

      final textPainter = TextPainter(
        text: textSpan,
        textDirection: TextDirection.ltr,
      )..layout();

      canvas.save();
      canvas.translate(textOffset.dx, textOffset.dy);
      canvas.rotate(textAngle + pi / 2);
      textPainter.paint(canvas, Offset(-textPainter.width / 2, -textPainter.height / 2));
      canvas.restore();
    }

    // Outer rim border
    final rimPaint = Paint()
      ..color = AppColors.natokGold
      ..strokeWidth = 5.0
      ..style = PaintingStyle.stroke;
    canvas.drawCircle(center, radius, rimPaint);
  }

  @override
  bool shouldRepaint(covariant CustomPainter oldDelegate) => false;
}

class PointerNeedlePainter extends CustomPainter {
  @override
  void paint(Canvas canvas, Size size) {
    final path = Path()
      ..moveTo(size.width / 2, size.height)
      ..lineTo(0, 0)
      ..lineTo(size.width, 0)
      ..close();

    final paint = Paint()
      ..color = AppColors.natokRed
      ..style = PaintingStyle.fill;

    canvas.drawPath(path, paint);

    final borderPaint = Paint()
      ..color = Colors.white
      ..strokeWidth = 1.5
      ..style = PaintingStyle.stroke;
    canvas.drawPath(path, borderPaint);
  }

  @override
  bool shouldRepaint(covariant CustomPainter oldDelegate) => false;
}
