import 'package:flutter/material.dart';
import '../core/theme/app_colors.dart';

class GenderAvatar extends StatelessWidget {
  final String gender; // "Boy" or "Girl"
  final double size;
  final String? levelBadge;
  final bool showBadge;

  const GenderAvatar({
    super.key,
    required this.gender,
    this.size = 64.0,
    this.levelBadge,
    this.showBadge = true,
  });

  @override
  Widget build(BuildContext context) {
    final isGirl = gender.toLowerCase() == 'girl' || gender.toLowerCase() == 'female';
    final primaryColor = isGirl ? AppColors.bkashPink : const Color(0xFF1E88E5);
    final secondaryColor = isGirl ? const Color(0xFFFF80AB) : const Color(0xFF64B5F6);

    return SizedBox(
      width: size,
      height: size,
      child: Stack(
        clipBehavior: Clip.none,
        alignment: Alignment.center,
        children: [
          // Outer Glow Circle
          Container(
            width: size,
            height: size,
            decoration: BoxDecoration(
              shape: BoxShape.circle,
              gradient: LinearGradient(
                colors: [primaryColor, secondaryColor, AppColors.natokGold],
                begin: Alignment.topLeft,
                end: Alignment.bottomRight,
              ),
              boxShadow: [
                BoxShadow(
                  color: primaryColor.withOpacity(0.4),
                  blurRadius: 10,
                  spreadRadius: 1,
                ),
              ],
            ),
            padding: const EdgeInsets.all(2.5),
            child: Container(
              decoration: const BoxDecoration(
                shape: BoxShape.circle,
                color: AppColors.surfaceDark,
              ),
              child: Center(
                child: Icon(
                  isGirl ? Icons.face_3 : Icons.face,
                  size: size * 0.58,
                  color: primaryColor,
                ),
              ),
            ),
          ),

          // Level Badge (Optional)
          if (showBadge && levelBadge != null)
            Positioned(
              bottom: -4,
              child: Container(
                padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
                decoration: BoxDecoration(
                  color: AppColors.backgroundDark,
                  borderRadius: BorderRadius.circular(8),
                  border: Border.all(color: AppColors.natokGold, width: 1),
                ),
                child: Text(
                  levelBadge!,
                  style: const TextStyle(
                    fontSize: 8,
                    fontWeight: FontWeight.bold,
                    color: AppColors.natokGold,
                  ),
                ),
              ),
            ),
        ],
      ),
    );
  }
}
