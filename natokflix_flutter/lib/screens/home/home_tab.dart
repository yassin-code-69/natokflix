import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:cached_network_image/cached_network_image.dart';
import '../../core/theme/app_colors.dart';
import '../../data/models/video_item.dart';
import '../../providers/natok_provider.dart';
import '../../components/admob_banner_widget.dart';

class HomeTab extends StatelessWidget {
  final bool isSearchActive;

  const HomeTab({super.key, required this.isSearchActive});

  @override
  Widget build(BuildContext context) {
    final provider = context.watch<NatokProvider>();
    final displayedVideos = provider.isSearching ? provider.searchResults : provider.natoks.where((v) {
      if (provider.selectedCategory == 'সকল নাটক') return true;
      return v.category.toLowerCase().contains(provider.selectedCategory.toLowerCase()) ||
          provider.selectedCategory.toLowerCase().contains(v.category.toLowerCase());
    }).toList();

    return RefreshIndicator(
      color: AppColors.natokRed,
      onRefresh: () async {
        await Future.delayed(const Duration(milliseconds: 600));
      },
      child: SingleChildScrollView(
        physics: const AlwaysScrollableScrollPhysics(),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // Search Bar Input (When toggled)
            if (isSearchActive) _buildSearchBar(provider),

            // Announcement Banner
            if (provider.economyConfig.isAnnouncementEnabled)
              _buildAnnouncementBanner(provider.economyConfig.announcementNotice),

            // 3D Aura Featured Promo Card
            _buildFeaturedPromoCard(context, provider),

            const SizedBox(height: 16),

            // Category Filter Chips
            _buildCategoryChips(provider),

            const SizedBox(height: 12),

            // Section Header: "জনপ্রিয় ও ট্রেন্ডিং নাটক"
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 6),
              child: Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Row(
                    children: [
                      Container(
                        width: 4,
                        height: 18,
                        decoration: BoxDecoration(
                          color: AppColors.natokRed,
                          borderRadius: BorderRadius.circular(2),
                        ),
                      ),
                      const SizedBox(width: 8),
                      Text(
                        provider.selectedCategory,
                        style: const TextStyle(
                          fontSize: 16,
                          fontWeight: FontWeight.bold,
                          color: Colors.white,
                        ),
                      ),
                    ],
                  ),
                  Text(
                    '${displayedVideos.length} টি নাটক',
                    style: const TextStyle(
                      fontSize: 12,
                      color: AppColors.textSecondary,
                    ),
                  ),
                ],
              ),
            ),

            // Videos Grid
            if (displayedVideos.isEmpty)
              Padding(
                padding: const EdgeInsets.symmetric(vertical: 48, horizontal: 24),
                child: Center(
                  child: Column(
                    children: const [
                      Icon(Icons.movie_filter_outlined, size: 56, color: AppColors.textMuted),
                      SizedBox(height: 12),
                      Text(
                        'কোনো নাটক খুঁজে পাওয়া যায়নি',
                        style: TextStyle(color: AppColors.textSecondary, fontSize: 14),
                      ),
                    ],
                  ),
                ),
              )
            else
              Padding(
                padding: const EdgeInsets.symmetric(horizontal: 14),
                child: LayoutBuilder(
                  builder: (context, constraints) {
                    final crossAxisCount = constraints.maxWidth > 800 ? 3 : (constraints.maxWidth > 550 ? 2 : 1);
                    return GridView.builder(
                      shrinkWrap: true,
                      physics: const NeverScrollableScrollPhysics(),
                      itemCount: displayedVideos.length,
                      gridDelegate: SliverGridDelegateWithFixedCrossAxisCount(
                        crossAxisCount: crossAxisCount,
                        crossAxisSpacing: 14,
                        mainAxisSpacing: 14,
                        childAspectRatio: crossAxisCount == 1 ? 1.45 : 0.88,
                      ),
                      itemBuilder: (context, index) {
                        return _buildVideoCard(context, displayedVideos[index], provider);
                      },
                    );
                  },
                ),
              ),

            const SizedBox(height: 12),
            const AdMobBannerWidget(),
            const SizedBox(height: 24),
          ],
        ),
      ),
    );
  }

  Widget _buildSearchBar(NatokProvider provider) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
      color: AppColors.surfaceDark,
      child: TextField(
        autofocus: true,
        onChanged: (val) => provider.searchVideos(val),
        decoration: InputDecoration(
          hintText: 'নাটক, অভিনেতা বা ক্যাটাগরি সার্চ করুন...',
          hintStyle: const TextStyle(color: AppColors.textMuted, fontSize: 13),
          filled: true,
          fillColor: AppColors.surfaceVariantDark,
          contentPadding: const EdgeInsets.symmetric(horizontal: 14, vertical: 10),
          prefixIcon: const Icon(Icons.search, color: AppColors.textSecondary, size: 20),
          suffixIcon: provider.searchQuery.isNotEmpty
              ? IconButton(
                  icon: const Icon(Icons.clear, color: AppColors.textSecondary, size: 18),
                  onPressed: () => provider.searchVideos(''),
                )
              : null,
          border: OutlineInputBorder(
            borderRadius: BorderRadius.circular(10),
            borderSide: BorderSide.none,
          ),
        ),
      ),
    );
  }

  Widget _buildAnnouncementBanner(String message) {
    return Container(
      width: double.infinity,
      padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 8),
      decoration: const BoxDecoration(
        color: Color(0xFF281C08),
        border: Border(bottom: BorderSide(color: Color(0xFF4A3408), width: 1)),
      ),
      child: Row(
        children: [
          const Icon(Icons.campaign, color: AppColors.natokGold, size: 20),
          const SizedBox(width: 8),
          Expanded(
            child: Text(
              message,
              maxLines: 1,
              overflow: TextOverflow.ellipsis,
              style: const TextStyle(
                color: AppColors.natokGoldLight,
                fontSize: 12,
                fontWeight: FontWeight.w500,
              ),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildFeaturedPromoCard(BuildContext context, NatokProvider provider) {
    return Container(
      margin: const EdgeInsets.symmetric(horizontal: 14, vertical: 10),
      decoration: BoxDecoration(
        borderRadius: BorderRadius.circular(18),
        gradient: const LinearGradient(
          colors: [
            Color(0xFF38080C),
            Color(0xFF1E1E28),
            Color(0xFF14141E),
          ],
          begin: Alignment.topLeft,
          end: Alignment.bottomRight,
        ),
        border: Border.all(
          color: AppColors.natokRed.withOpacity(0.5),
          width: 1.5,
        ),
        boxShadow: [
          BoxShadow(
            color: AppColors.natokRed.withOpacity(0.2),
            blurRadius: 14,
            offset: const Offset(0, 4),
          ),
        ],
      ),
      padding: const EdgeInsets.all(16),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                decoration: BoxDecoration(
                  color: AppColors.natokGold.withOpacity(0.2),
                  borderRadius: BorderRadius.circular(6),
                  border: Border.all(color: AppColors.natokGold.withOpacity(0.6)),
                ),
                child: Row(
                  children: const [
                    Icon(Icons.star, color: AppColors.natokGold, size: 14),
                    SizedBox(width: 4),
                    Text(
                      'আজকের সেরা ট্রেন্ডিং',
                      style: TextStyle(
                        fontSize: 11,
                        fontWeight: FontWeight.bold,
                        color: AppColors.natokGold,
                      ),
                    ),
                  ],
                ),
              ),
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 3),
                decoration: BoxDecoration(
                  color: AppColors.natokRed,
                  borderRadius: BorderRadius.circular(6),
                ),
                child: const Text(
                  'ডাবল কয়েন',
                  style: TextStyle(
                    fontSize: 10,
                    fontWeight: FontWeight.bold,
                    color: Colors.white,
                  ),
                ),
              ),
            ],
          ),
          const SizedBox(height: 12),
          Text(
            provider.economyConfig.featuredPromoTitle,
            style: const TextStyle(
              fontSize: 19,
              fontWeight: FontWeight.w900,
              color: Colors.white,
            ),
          ),
          const SizedBox(height: 4),
          Text(
            provider.economyConfig.featuredPromoSubtitle,
            style: const TextStyle(
              fontSize: 13,
              color: AppColors.textSecondary,
            ),
          ),
          const SizedBox(height: 16),
          Row(
            children: [
              ElevatedButton.icon(
                onPressed: () {
                  if (provider.natoks.isNotEmpty) {
                    provider.playVideo(provider.natoks.first);
                  }
                },
                icon: const Icon(Icons.play_arrow, color: Colors.white, size: 18),
                label: const Text(
                  'এখনই দেখুন',
                  style: TextStyle(fontSize: 13, fontWeight: FontWeight.bold, color: Colors.white),
                ),
                style: ElevatedButton.styleFrom(
                  backgroundColor: AppColors.natokRed,
                  padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 10),
                  shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
                ),
              ),
              const SizedBox(width: 10),
              OutlinedButton.icon(
                onPressed: () {
                  provider.selectTab(NavigationTab.spin);
                },
                icon: const Icon(Icons.casino, color: AppColors.natokGold, size: 18),
                label: const Text(
                  'লাকি স্পিন 🎰',
                  style: TextStyle(fontSize: 13, fontWeight: FontWeight.bold, color: AppColors.natokGold),
                ),
                style: OutlinedButton.styleFrom(
                  side: const BorderSide(color: AppColors.natokGold),
                  padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 10),
                  shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }

  Widget _buildCategoryChips(NatokProvider provider) {
    return SizedBox(
      height: 40,
      child: ListView.separated(
        padding: const EdgeInsets.symmetric(horizontal: 14),
        scrollDirection: Axis.horizontal,
        itemCount: provider.categories.length,
        separatorBuilder: (_, __) => const SizedBox(width: 8),
        itemBuilder: (context, index) {
          final cat = provider.categories[index];
          final isSelected = provider.selectedCategory == cat;
          return ChoiceChip(
            label: Text(cat),
            selected: isSelected,
            labelStyle: TextStyle(
              fontSize: 12,
              fontWeight: isSelected ? FontWeight.bold : FontWeight.w500,
              color: isSelected ? Colors.white : AppColors.textSecondary,
            ),
            selectedColor: AppColors.natokRed,
            backgroundColor: AppColors.surfaceVariantDark,
            side: BorderSide(
              color: isSelected ? AppColors.natokRed : AppColors.borderDark,
            ),
            shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(20)),
            onSelected: (_) => provider.selectCategory(cat),
          );
        },
      ),
    );
  }

  Widget _buildVideoCard(BuildContext context, VideoItem video, NatokProvider provider) {
    return InkWell(
      onTap: () => provider.playVideo(video),
      borderRadius: BorderRadius.circular(14),
      child: Container(
        decoration: BoxDecoration(
          color: AppColors.surfaceCard,
          borderRadius: BorderRadius.circular(14),
          border: Border.all(color: AppColors.borderDark, width: 1),
          boxShadow: [
            BoxShadow(
              color: Colors.black.withOpacity(0.3),
              blurRadius: 8,
              offset: const Offset(0, 3),
            ),
          ],
        ),
        clipBehavior: Clip.antiAlias,
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // Thumbnail with Duration & Coins badges
            Expanded(
              flex: 5,
              child: Stack(
                fit: StackFit.expand,
                children: [
                  CachedNetworkImage(
                    imageUrl: video.thumbnailUrl,
                    fit: BoxFit.cover,
                    placeholder: (_, __) => Container(
                      color: AppColors.surfaceVariantDark,
                      child: const Center(
                        child: CircularProgressIndicator(color: AppColors.natokRed, strokeWidth: 2),
                      ),
                    ),
                    errorWidget: (_, __, ___) => Container(
                      color: AppColors.surfaceVariantDark,
                      child: const Center(
                        child: Icon(Icons.movie, color: AppColors.textMuted, size: 36),
                      ),
                    ),
                  ),

                  // Black gradient overlay on bottom of thumbnail
                  Positioned.fill(
                    child: Container(
                      decoration: const BoxDecoration(
                        gradient: LinearGradient(
                          begin: Alignment.topCenter,
                          end: Alignment.bottomCenter,
                          colors: [Colors.transparent, Color(0x99000000)],
                        ),
                      ),
                    ),
                  ),

                  // Coins reward pill (top-left)
                  Positioned(
                    top: 8,
                    left: 8,
                    child: Container(
                      padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
                      decoration: BoxDecoration(
                        color: Colors.black.withOpacity(0.75),
                        borderRadius: BorderRadius.circular(6),
                        border: Border.all(color: AppColors.natokGold, width: 1),
                      ),
                      child: Row(
                        children: [
                          const Icon(Icons.monetization_on, color: AppColors.natokGold, size: 12),
                          const SizedBox(width: 3),
                          Text(
                            '+${video.rewardPoints}',
                            style: const TextStyle(
                              color: AppColors.natokGold,
                              fontSize: 10,
                              fontWeight: FontWeight.bold,
                            ),
                          ),
                        ],
                      ),
                    ),
                  ),

                  // Duration pill (bottom-right)
                  Positioned(
                    bottom: 8,
                    right: 8,
                    child: Container(
                      padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
                      decoration: BoxDecoration(
                        color: Colors.black.withOpacity(0.8),
                        borderRadius: BorderRadius.circular(4),
                      ),
                      child: Text(
                        video.duration,
                        style: const TextStyle(
                          color: Colors.white,
                          fontSize: 10,
                          fontWeight: FontWeight.w600,
                        ),
                      ),
                    ),
                  ),

                  // Play Button Hover Icon
                  Center(
                    child: Container(
                      padding: const EdgeInsets.all(8),
                      decoration: BoxDecoration(
                        shape: BoxShape.circle,
                        color: Colors.black.withOpacity(0.5),
                        border: Border.all(color: Colors.white.withOpacity(0.4)),
                      ),
                      child: const Icon(Icons.play_arrow, color: Colors.white, size: 26),
                    ),
                  ),
                ],
              ),
            ),

            // Video Meta Details
            Expanded(
              flex: 4,
              child: Padding(
                padding: const EdgeInsets.all(10.0),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Text(
                      video.title,
                      maxLines: 2,
                      overflow: TextOverflow.ellipsis,
                      style: const TextStyle(
                        fontSize: 13,
                        fontWeight: FontWeight.bold,
                        color: Colors.white,
                        height: 1.25,
                      ),
                    ),
                    Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: [
                        Expanded(
                          child: Text(
                            video.channelTitle,
                            maxLines: 1,
                            overflow: TextOverflow.ellipsis,
                            style: const TextStyle(
                              fontSize: 11,
                              color: AppColors.textSecondary,
                            ),
                          ),
                        ),
                        Text(
                          '${video.views} ভিউ',
                          style: const TextStyle(
                            fontSize: 10,
                            color: AppColors.textMuted,
                          ),
                        ),
                      ],
                    ),
                  ],
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
