import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:youtube_player_iframe/youtube_player_iframe.dart';
import 'package:cached_network_image/cached_network_image.dart';
import '../../core/theme/app_colors.dart';
import '../../providers/natok_provider.dart';
import '../../components/admob_banner_widget.dart';

class VideoPlayerScreen extends StatefulWidget {
  const VideoPlayerScreen({super.key});

  @override
  State<VideoPlayerScreen> createState() => _VideoPlayerScreenState();
}

class _VideoPlayerScreenState extends State<VideoPlayerScreen> {
  late YoutubePlayerController _ytController;
  String _currentLoadedId = '';
  bool _isDescriptionExpanded = false;

  @override
  void initState() {
    super.initState();
    final currentVideo = context.read<NatokProvider>().currentVideo;
    final initialId = currentVideo?.youtubeVideoId ?? 'dQw4w9WgXcQ';
    _currentLoadedId = initialId;

    _ytController = YoutubePlayerController.fromVideoId(
      videoId: initialId,
      autoPlay: true,
      params: const YoutubePlayerParams(
        showControls: true,
        showFullscreenButton: true,
        mute: false,
      ),
    );
  }

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();
    final currentVideo = context.watch<NatokProvider>().currentVideo;
    if (currentVideo != null && currentVideo.youtubeVideoId != _currentLoadedId) {
      _currentLoadedId = currentVideo.youtubeVideoId;
      _ytController.loadVideoById(videoId: _currentLoadedId);
    }
  }

  @override
  void dispose() {
    _ytController.close();
    super.dispose();
  }

  String _formatSeconds(int seconds) {
    final m = seconds ~/ 60;
    final s = seconds % 60;
    return '${m.toString().padLeft(2, '0')}:${s.toString().padLeft(2, '0')}';
  }

  @override
  Widget build(BuildContext context) {
    final provider = context.watch<NatokProvider>();
    final video = provider.currentVideo;

    if (video == null) {
      return const Scaffold(
        body: Center(child: Text('কোনো ভিডিও নির্বাচিত নেই')),
      );
    }

    final relatedVideos = provider.natoks.where((v) => v.id != video.id).toList();

    return Scaffold(
      backgroundColor: AppColors.backgroundDark,
      appBar: AppBar(
        backgroundColor: AppColors.surfaceDark,
        elevation: 2,
        leading: IconButton(
          icon: const Icon(Icons.arrow_back, color: Colors.white),
          tooltip: 'ফিরে যান',
          onPressed: () => provider.closeVideoPlayer(),
        ),
        title: const Text(
          'NatokFlix Stream',
          style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold, color: Colors.white),
        ),
        actions: [
          Container(
            margin: const EdgeInsets.only(right: 14),
            padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 4),
            decoration: BoxDecoration(
              color: const Color(0xFF2B200A),
              borderRadius: BorderRadius.circular(16),
              border: Border.all(color: AppColors.natokGold.withOpacity(0.6)),
            ),
            child: Row(
              children: [
                const Icon(Icons.monetization_on, color: AppColors.natokGold, size: 14),
                const SizedBox(width: 4),
                Text(
                  '+${provider.sessionCoinsEarned}',
                  style: const TextStyle(fontSize: 12, fontWeight: FontWeight.bold, color: AppColors.natokGold),
                ),
              ],
            ),
          ),
        ],
      ),
      body: SafeArea(
        child: Column(
          children: [
            // Top Video Player Section (16:9)
            AspectRatio(
              aspectRatio: 16 / 9,
              child: YoutubePlayer(
                controller: _ytController,
                aspectRatio: 16 / 9,
              ),
            ),

            // Live Watch Timer & Earnings Bar
            Container(
              padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
              color: const Color(0xFF1E1810),
              child: Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Row(
                    children: [
                      const Icon(Icons.timer, color: AppColors.natokGold, size: 16),
                      const SizedBox(width: 6),
                      Text(
                        'দেখার সময়: ${_formatSeconds(provider.continuousTimerSeconds)}',
                        style: const TextStyle(fontSize: 12, fontWeight: FontWeight.bold, color: Colors.white),
                      ),
                    ],
                  ),
                  Container(
                    padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 3),
                    decoration: BoxDecoration(
                      color: AppColors.natokGold.withOpacity(0.2),
                      borderRadius: BorderRadius.circular(6),
                      border: Border.all(color: AppColors.natokGold, width: 0.8),
                    ),
                    child: Row(
                      children: [
                        const Icon(Icons.monetization_on, color: AppColors.natokGold, size: 14),
                        const SizedBox(width: 4),
                        Text(
                          'সেশন: +${provider.sessionCoinsEarned} 🪙',
                          style: const TextStyle(fontSize: 11, fontWeight: FontWeight.bold, color: AppColors.natokGold),
                        ),
                      ],
                    ),
                  ),
                  Text(
                    'পরবর্তী রিওয়ার্ড: ${provider.watchTimeCountdown} সে.',
                    style: const TextStyle(fontSize: 11, color: AppColors.natokGoldLight),
                  ),
                ],
              ),
            ),

            // Scrollable Content (Metadata + Related)
            Expanded(
              child: SingleChildScrollView(
                padding: const EdgeInsets.all(16),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    // Drama Title
                    Text(
                      video.title,
                      style: const TextStyle(
                        fontSize: 17,
                        fontWeight: FontWeight.bold,
                        color: Colors.white,
                        height: 1.3,
                      ),
                    ),
                    const SizedBox(height: 8),

                    // Category, Channel & Views Info
                    Row(
                      children: [
                        Container(
                          padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 3),
                          decoration: BoxDecoration(
                            color: AppColors.natokRed.withOpacity(0.2),
                            borderRadius: BorderRadius.circular(4),
                            border: Border.all(color: AppColors.natokRed, width: 0.8),
                          ),
                          child: Text(
                            video.category,
                            style: const TextStyle(fontSize: 11, color: AppColors.natokRed, fontWeight: FontWeight.bold),
                          ),
                        ),
                        const SizedBox(width: 8),
                        Text(
                          '${video.views} ভিউ',
                          style: const TextStyle(fontSize: 12, color: AppColors.textSecondary),
                        ),
                        const SizedBox(width: 8),
                        const Text('•', style: TextStyle(color: AppColors.textMuted)),
                        const SizedBox(width: 8),
                        Expanded(
                          child: Text(
                            video.channelTitle,
                            maxLines: 1,
                            overflow: TextOverflow.ellipsis,
                            style: const TextStyle(fontSize: 12, color: AppColors.textSecondary, fontWeight: FontWeight.w500),
                          ),
                        ),
                      ],
                    ),
                    const SizedBox(height: 12),

                    // Cast & Description Box
                    Container(
                      padding: const EdgeInsets.all(12),
                      decoration: BoxDecoration(
                        color: AppColors.surfaceCard,
                        borderRadius: BorderRadius.circular(10),
                        border: Border.all(color: AppColors.borderDark),
                      ),
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Row(
                            children: [
                              const Icon(Icons.people, color: AppColors.natokGold, size: 16),
                              const SizedBox(width: 6),
                              Text(
                                'অভিনয়ে: ${video.cast}',
                                style: const TextStyle(fontSize: 12, fontWeight: FontWeight.bold, color: Colors.white),
                              ),
                            ],
                          ),
                          const SizedBox(height: 6),
                          Text(
                            video.description,
                            maxLines: _isDescriptionExpanded ? 10 : 2,
                            overflow: TextOverflow.ellipsis,
                            style: const TextStyle(fontSize: 12, color: AppColors.textSecondary, height: 1.4),
                          ),
                          InkWell(
                            onTap: () {
                              setState(() {
                                _isDescriptionExpanded = !_isDescriptionExpanded;
                              });
                            },
                            child: Padding(
                              padding: const EdgeInsets.only(top: 4),
                              child: Text(
                                _isDescriptionExpanded ? 'কম দেখুন' : 'আরও দেখুন...',
                                style: const TextStyle(fontSize: 11, color: AppColors.natokGold, fontWeight: FontWeight.bold),
                              ),
                            ),
                          ),
                        ],
                      ),
                    ),
                    const SizedBox(height: 16),

                    const AdMobBannerWidget(),
                    const SizedBox(height: 16),

                    // Related Natoks Section
                    Row(
                      children: [
                        Container(
                          width: 4,
                          height: 16,
                          decoration: BoxDecoration(
                            color: AppColors.natokRed,
                            borderRadius: BorderRadius.circular(2),
                          ),
                        ),
                        const SizedBox(width: 8),
                        const Text(
                          'সম্পর্কিত নাটকসমূহ',
                          style: TextStyle(fontSize: 15, fontWeight: FontWeight.bold, color: Colors.white),
                        ),
                      ],
                    ),
                    const SizedBox(height: 12),

                    ListView.separated(
                      shrinkWrap: true,
                      physics: const NeverScrollableScrollPhysics(),
                      itemCount: relatedVideos.length,
                      separatorBuilder: (_, __) => const SizedBox(height: 10),
                      itemBuilder: (context, index) {
                        final rel = relatedVideos[index];
                        return InkWell(
                          onTap: () => provider.playVideo(rel),
                          borderRadius: BorderRadius.circular(10),
                          child: Container(
                            padding: const EdgeInsets.all(8),
                            decoration: BoxDecoration(
                              color: AppColors.surfaceCard,
                              borderRadius: BorderRadius.circular(10),
                              border: Border.all(color: AppColors.borderDark),
                            ),
                            child: Row(
                              children: [
                                ClipRRect(
                                  borderRadius: BorderRadius.circular(8),
                                  child: CachedNetworkImage(
                                    imageUrl: rel.thumbnailUrl,
                                    width: 110,
                                    height: 64,
                                    fit: BoxFit.cover,
                                  ),
                                ),
                                const SizedBox(width: 12),
                                Expanded(
                                  child: Column(
                                    crossAxisAlignment: CrossAxisAlignment.start,
                                    children: [
                                      Text(
                                        rel.title,
                                        maxLines: 2,
                                        overflow: TextOverflow.ellipsis,
                                        style: const TextStyle(fontSize: 13, fontWeight: FontWeight.bold, color: Colors.white),
                                      ),
                                      const SizedBox(height: 4),
                                      Text(
                                        '${rel.channelTitle} • ${rel.views} ভিউ',
                                        style: const TextStyle(fontSize: 11, color: AppColors.textMuted),
                                      ),
                                    ],
                                  ),
                                ),
                              ],
                            ),
                          ),
                        );
                      },
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
