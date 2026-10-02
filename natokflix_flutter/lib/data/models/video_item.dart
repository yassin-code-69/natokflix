class VideoItem {
  final String id;
  final String title;
  final String channelTitle;
  final String duration;
  final String thumbnailUrl;
  final String youtubeVideoId;
  final String category;
  final String views;
  final int viewCount;
  final int rewardPoints;
  final bool isShort;
  final String cast;
  final String description;
  final String status;

  const VideoItem({
    this.id = '',
    this.title = '',
    this.channelTitle = '',
    this.duration = '',
    this.thumbnailUrl = '',
    this.youtubeVideoId = '',
    this.category = '',
    this.views = '',
    this.viewCount = 0,
    this.rewardPoints = 20,
    this.isShort = false,
    this.cast = 'Bangla Popular Cast',
    this.description = 'Watch full drama and earn reward points directly to your NatokFlix account.',
    this.status = 'pending',
  });

  VideoItem copyWith({
    String? id,
    String? title,
    String? channelTitle,
    String? duration,
    String? thumbnailUrl,
    String? youtubeVideoId,
    String? category,
    String? views,
    int? viewCount,
    int? rewardPoints,
    bool? isShort,
    String? cast,
    String? description,
    String? status,
  }) {
    return VideoItem(
      id: id ?? this.id,
      title: title ?? this.title,
      channelTitle: channelTitle ?? this.channelTitle,
      duration: duration ?? this.duration,
      thumbnailUrl: thumbnailUrl ?? this.thumbnailUrl,
      youtubeVideoId: youtubeVideoId ?? this.youtubeVideoId,
      category: category ?? this.category,
      views: views ?? this.views,
      viewCount: viewCount ?? this.viewCount,
      rewardPoints: rewardPoints ?? this.rewardPoints,
      isShort: isShort ?? this.isShort,
      cast: cast ?? this.cast,
      description: description ?? this.description,
      status: status ?? this.status,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'title': title,
      'channelTitle': channelTitle,
      'duration': duration,
      'thumbnailUrl': thumbnailUrl,
      'youtubeVideoId': youtubeVideoId,
      'category': category,
      'views': views,
      'viewCount': viewCount,
      'rewardPoints': rewardPoints,
      'isShort': isShort,
      'cast': cast,
      'description': description,
      'status': status,
    };
  }

  factory VideoItem.fromJson(Map<String, dynamic> json) {
    return VideoItem(
      id: json['id'] ?? '',
      title: json['title'] ?? '',
      channelTitle: json['channelTitle'] ?? '',
      duration: json['duration'] ?? '',
      thumbnailUrl: json['thumbnailUrl'] ?? '',
      youtubeVideoId: json['youtubeVideoId'] ?? '',
      category: json['category'] ?? '',
      views: json['views'] ?? '',
      viewCount: json['viewCount'] ?? 0,
      rewardPoints: json['rewardPoints'] ?? 20,
      isShort: json['isShort'] ?? false,
      cast: json['cast'] ?? 'Bangla Popular Cast',
      description: json['description'] ?? 'Watch full drama and earn reward points directly to your NatokFlix account.',
      status: json['status'] ?? 'pending',
    );
  }
}
