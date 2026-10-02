enum TaskType {
  dailyCheckIn,
  spinWheel,
  watchAd,
  watchVideoMinutes,
  watchShorts,
  shareApp,
  categoryDrama,
  megaBonus,
}

class TaskItem {
  final String id;
  final String title;
  final String description;
  final TaskType type;
  final int rewardPoints;
  final int currentProgress;
  final int targetProgress;
  final String unit;
  final bool isClaimed;

  const TaskItem({
    required this.id,
    required this.title,
    required this.description,
    required this.type,
    required this.rewardPoints,
    this.currentProgress = 0,
    this.targetProgress = 1,
    this.unit = 'টি',
    this.isClaimed = false,
  });

  bool get isCompleted => currentProgress >= targetProgress;

  double get progressFraction {
    if (targetProgress > 0) {
      return (currentProgress / targetProgress).clamp(0.0, 1.0);
    }
    return 0.0;
  }

  TaskItem copyWith({
    String? id,
    String? title,
    String? description,
    TaskType? type,
    int? rewardPoints,
    int? currentProgress,
    int? targetProgress,
    String? unit,
    bool? isClaimed,
  }) {
    return TaskItem(
      id: id ?? this.id,
      title: title ?? this.title,
      description: description ?? this.description,
      type: type ?? this.type,
      rewardPoints: rewardPoints ?? this.rewardPoints,
      currentProgress: currentProgress ?? this.currentProgress,
      targetProgress: targetProgress ?? this.targetProgress,
      unit: unit ?? this.unit,
      isClaimed: isClaimed ?? this.isClaimed,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'title': title,
      'description': description,
      'type': type.name,
      'rewardPoints': rewardPoints,
      'currentProgress': currentProgress,
      'targetProgress': targetProgress,
      'unit': unit,
      'isClaimed': isClaimed,
    };
  }

  factory TaskItem.fromJson(Map<String, dynamic> json) {
    return TaskItem(
      id: json['id'] ?? '',
      title: json['title'] ?? '',
      description: json['description'] ?? '',
      type: TaskType.values.firstWhere(
        (e) => e.name == json['type'],
        orElse: () => TaskType.dailyCheckIn,
      ),
      rewardPoints: json['rewardPoints'] ?? 0,
      currentProgress: json['currentProgress'] ?? 0,
      targetProgress: json['targetProgress'] ?? 1,
      unit: json['unit'] ?? 'টি',
      isClaimed: json['isClaimed'] ?? false,
    );
  }
}
