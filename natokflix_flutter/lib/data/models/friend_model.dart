enum FriendStatus {
  none,
  requestSent,
  requestReceived,
  accepted,
}

class FriendProfile {
  final String id;
  final String name;
  final String gender; // "Boy" or "Girl"
  final String location;
  final int age;
  final String bio;
  final bool isBot;
  final bool isOnline;
  final bool isBlocked;
  final FriendStatus status;
  final String lastMessage;
  final String lastMessageTime;
  final int unreadCount;

  const FriendProfile({
    required this.id,
    required this.name,
    this.gender = 'Girl',
    this.location = 'Dhaka',
    this.age = 21,
    this.bio = '',
    this.isBot = false,
    this.isOnline = true,
    this.isBlocked = false,
    this.status = FriendStatus.none,
    this.lastMessage = '',
    this.lastMessageTime = '',
    this.unreadCount = 0,
  });

  FriendProfile copyWith({
    String? id,
    String? name,
    String? gender,
    String? location,
    int? age,
    String? bio,
    bool? isBot,
    bool? isOnline,
    bool? isBlocked,
    FriendStatus? status,
    String? lastMessage,
    String? lastMessageTime,
    int? unreadCount,
  }) {
    return FriendProfile(
      id: id ?? this.id,
      name: name ?? this.name,
      gender: gender ?? this.gender,
      location: location ?? this.location,
      age: age ?? this.age,
      bio: bio ?? this.bio,
      isBot: isBot ?? this.isBot,
      isOnline: isOnline ?? this.isOnline,
      isBlocked: isBlocked ?? this.isBlocked,
      status: status ?? this.status,
      lastMessage: lastMessage ?? this.lastMessage,
      lastMessageTime: lastMessageTime ?? this.lastMessageTime,
      unreadCount: unreadCount ?? this.unreadCount,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'name': name,
      'gender': gender,
      'location': location,
      'age': age,
      'bio': bio,
      'isBot': isBot,
      'isOnline': isOnline,
      'isBlocked': isBlocked,
      'status': status.name,
      'lastMessage': lastMessage,
      'lastMessageTime': lastMessageTime,
      'unreadCount': unreadCount,
    };
  }

  factory FriendProfile.fromJson(Map<String, dynamic> json) {
    return FriendProfile(
      id: json['id'] ?? '',
      name: json['name'] ?? '',
      gender: json['gender'] ?? 'Girl',
      location: json['location'] ?? 'Dhaka',
      age: json['age'] ?? 21,
      bio: json['bio'] ?? '',
      isBot: json['isBot'] ?? false,
      isOnline: json['isOnline'] ?? true,
      isBlocked: json['isBlocked'] ?? false,
      status: FriendStatus.values.firstWhere(
        (e) => e.name == json['status'],
        orElse: () => FriendStatus.none,
      ),
      lastMessage: json['lastMessage'] ?? '',
      lastMessageTime: json['lastMessageTime'] ?? '',
      unreadCount: json['unreadCount'] ?? 0,
    );
  }
}

class ChatMessage {
  final String id;
  final String conversationUserId;
  final String senderName;
  final String text;
  final int timestamp;
  final bool isFromMe;
  final bool isRead;

  const ChatMessage({
    required this.id,
    required this.conversationUserId,
    required this.senderName,
    required this.text,
    required this.timestamp,
    this.isFromMe = true,
    this.isRead = true,
  });

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'conversationUserId': conversationUserId,
      'senderName': senderName,
      'text': text,
      'timestamp': timestamp,
      'isFromMe': isFromMe,
      'isRead': isRead,
    };
  }

  factory ChatMessage.fromJson(Map<String, dynamic> json) {
    return ChatMessage(
      id: json['id'] ?? '',
      conversationUserId: json['conversationUserId'] ?? '',
      senderName: json['senderName'] ?? '',
      text: json['text'] ?? '',
      timestamp: json['timestamp'] ?? DateTime.now().millisecondsSinceEpoch,
      isFromMe: json['isFromMe'] ?? true,
      isRead: json['isRead'] ?? true,
    );
  }
}
