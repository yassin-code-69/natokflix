import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../core/theme/app_colors.dart';
import '../providers/natok_provider.dart';
import 'glowing_logo.dart';

class CustomAppBar extends StatelessWidget implements PreferredSizeWidget {
  final VoidCallback onToggleSearch;
  final bool isSearchActive;

  const CustomAppBar({
    super.key,
    required this.onToggleSearch,
    required this.isSearchActive,
  });

  @override
  Size get preferredSize => const Size.fromHeight(60.0);

  void _showCustomUrlDialog(BuildContext context) {
    final urlCtrl = TextEditingController();
    showDialog(
      context: context,
      builder: (ctx) => AlertDialog(
        title: Row(
          children: const [
            Icon(Icons.link, color: AppColors.natokGold),
            SizedBox(width: 8),
            Text('কাস্টম ইউটিউব নাটক লিংক', style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
          ],
        ),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Text(
              'যে কোনো ইউটিউব নাটকের লিংক পেস্ট করুন, সরাসরি অ্যাপে চলবে এবং কয়েন জমা হবে!',
              style: TextStyle(fontSize: 13, color: AppColors.textSecondary),
            ),
            const SizedBox(height: 14),
            TextField(
              controller: urlCtrl,
              decoration: InputDecoration(
                hintText: 'https://youtu.be/...',
                filled: true,
                fillColor: AppColors.surfaceVariantDark,
                border: OutlineInputBorder(borderRadius: BorderRadius.circular(10)),
              ),
            ),
          ],
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(ctx),
            child: const Text('বাতিল', style: TextStyle(color: Colors.white70)),
          ),
          ElevatedButton(
            onPressed: () {
              final url = urlCtrl.text.trim();
              Navigator.pop(ctx);
              if (url.isNotEmpty) {
                context.read<NatokProvider>().playCustomYouTubeUrl(url);
              }
            },
            style: ElevatedButton.styleFrom(backgroundColor: AppColors.natokRed),
            child: const Text('প্লে করুন ▶️', style: TextStyle(color: Colors.white)),
          ),
        ],
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    final provider = context.watch<NatokProvider>();
    final user = provider.user;

    return AppBar(
      backgroundColor: AppColors.surfaceDark,
      elevation: 4,
      automaticallyImplyLeading: false,
      title: Row(
        children: [
          const GlowingNatokFlixLogo(
            size: 36,
            showText: true,
            fontSize: 20,
            enablePulse: false,
          ),
        ],
      ),
      actions: [
        // Custom URL Link Button
        IconButton(
          icon: const Icon(Icons.link, color: AppColors.textSecondary, size: 22),
          tooltip: 'কাস্টম লিংক পেস্ট করুন',
          onPressed: () => _showCustomUrlDialog(context),
        ),

        // Search Button
        IconButton(
          icon: Icon(
            isSearchActive ? Icons.close : Icons.search,
            color: isSearchActive ? AppColors.natokRed : AppColors.textSecondary,
            size: 22,
          ),
          tooltip: 'অনুসন্ধান',
          onPressed: onToggleSearch,
        ),

        // Theme Toggle Button
        IconButton(
          icon: Icon(
            provider.isDarkTheme ? Icons.light_mode : Icons.dark_mode,
            color: AppColors.natokGold,
            size: 20,
          ),
          tooltip: 'থিম পরিবর্তন',
          onPressed: () => provider.toggleTheme(),
        ),

        // Coins Capsule Badge (Clickable to go to Profile/Wallet)
        InkWell(
          onTap: () {
            provider.selectTab(NavigationTab.profile);
          },
          borderRadius: BorderRadius.circular(16),
          child: Container(
            margin: const EdgeInsets.only(right: 12, left: 4),
            padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 5),
            decoration: BoxDecoration(
              gradient: const LinearGradient(
                colors: [Color(0xFF382800), Color(0xFF1F1600)],
              ),
              borderRadius: BorderRadius.circular(16),
              border: Border.all(color: AppColors.natokGold.withOpacity(0.8), width: 1.2),
              boxShadow: [
                BoxShadow(
                  color: AppColors.natokGold.withOpacity(0.2),
                  blurRadius: 6,
                ),
              ],
            ),
            child: Row(
              mainAxisSize: MainAxisSize.min,
              children: [
                const Icon(Icons.monetization_on, color: AppColors.natokGold, size: 16),
                const SizedBox(width: 5),
                Text(
                  '${user.points}',
                  style: const TextStyle(
                    fontSize: 12,
                    fontWeight: FontWeight.bold,
                    color: AppColors.natokGold,
                  ),
                ),
              ],
            ),
          ),
        ),
      ],
    );
  }
}
