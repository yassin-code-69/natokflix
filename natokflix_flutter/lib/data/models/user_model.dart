import 'package:flutter/material.dart';

class UserModel {
  final String id;
  final String name;
  final String email;
  final String? photoUrl;
  final String gender; // "Boy" / "Girl"
  final String birthday;
  final int points;
  final double balanceBdt; // 100 points = 1.0 BDT
  final String role; // "USER" or "ADMIN"
  final bool isLoggedIn;
  final String joinedDate;
  final String referralCode;
  final String phone;
  final int friendsCount;
  final int totalReferrals;

  const UserModel({
    this.id = 'user_default',
    this.name = 'NatokFlix User',
    this.email = '',
    this.photoUrl,
    this.gender = 'Boy',
    this.birthday = '15-08-2002',
    this.points = 0,
    this.balanceBdt = 0.0,
    this.role = 'USER',
    this.isLoggedIn = false,
    this.joinedDate = 'September 2026',
    this.referralCode = 'ARFLIX77',
    this.phone = '01788-123456',
    this.friendsCount = 0,
    this.totalReferrals = 0,
  });

  String get levelName {
    if (points >= 50000) return 'VIP Gold Member (Level 4)';
    if (points >= 15000) return 'Silver Member (Level 3)';
    if (points >= 5000) return 'Bronze Watcher (Level 2)';
    return 'Beginner (Level 1)';
  }

  String get levelBadge {
    if (points >= 50000) return '👑 VIP GOLD';
    if (points >= 15000) return '🥈 SILVER';
    if (points >= 5000) return '🥉 BRONZE';
    return '🌱 BEGINNER';
  }

  Color get levelColor {
    if (points >= 50000) return const Color(0xFFFFD700);
    if (points >= 15000) return const Color(0xFFE0E0E0);
    if (points >= 5000) return const Color(0xFFCD7F32);
    return const Color(0xFF81C784);
  }

  UserModel copyWith({
    String? id,
    String? name,
    String? email,
    String? photoUrl,
    String? gender,
    String? birthday,
    int? points,
    double? balanceBdt,
    String? role,
    bool? isLoggedIn,
    String? joinedDate,
    String? referralCode,
    String? phone,
    int? friendsCount,
    int? totalReferrals,
  }) {
    return UserModel(
      id: id ?? this.id,
      name: name ?? this.name,
      email: email ?? this.email,
      photoUrl: photoUrl ?? this.photoUrl,
      gender: gender ?? this.gender,
      birthday: birthday ?? this.birthday,
      points: points ?? this.points,
      balanceBdt: balanceBdt ?? this.balanceBdt,
      role: role ?? this.role,
      isLoggedIn: isLoggedIn ?? this.isLoggedIn,
      joinedDate: joinedDate ?? this.joinedDate,
      referralCode: referralCode ?? this.referralCode,
      phone: phone ?? this.phone,
      friendsCount: friendsCount ?? this.friendsCount,
      totalReferrals: totalReferrals ?? this.totalReferrals,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'name': name,
      'email': email,
      'photoUrl': photoUrl,
      'gender': gender,
      'birthday': birthday,
      'points': points,
      'balanceBdt': balanceBdt,
      'role': role,
      'isLoggedIn': isLoggedIn,
      'joinedDate': joinedDate,
      'referralCode': referralCode,
      'phone': phone,
      'friendsCount': friendsCount,
      'totalReferrals': totalReferrals,
    };
  }

  factory UserModel.fromJson(Map<String, dynamic> json) {
    return UserModel(
      id: json['id'] ?? 'user_default',
      name: json['name'] ?? 'NatokFlix User',
      email: json['email'] ?? '',
      photoUrl: json['photoUrl'],
      gender: json['gender'] ?? 'Boy',
      birthday: json['birthday'] ?? '15-08-2002',
      points: json['points'] ?? 0,
      balanceBdt: (json['balanceBdt'] as num?)?.toDouble() ?? 0.0,
      role: json['role'] ?? 'USER',
      isLoggedIn: json['isLoggedIn'] ?? false,
      joinedDate: json['joinedDate'] ?? 'September 2026',
      referralCode: json['referralCode'] ?? 'ARFLIX77',
      phone: json['phone'] ?? '01788-123456',
      friendsCount: json['friendsCount'] ?? 0,
      totalReferrals: json['totalReferrals'] ?? 0,
    );
  }
}
