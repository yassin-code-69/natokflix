import 'package:intl/intl.dart';

class WithdrawalModel {
  final String id;
  final String userId;
  final String method; // "Mobile Recharge", "bKash", "Nagad", "Rocket"
  final String? operator; // "Grameenphone (GP)", "Robi", etc.
  final String accountNumber;
  final double amountBdt;
  final int pointsDeducted;
  final String status; // "Pending", "Approved", "Rejected"
  final int timestamp;

  const WithdrawalModel({
    required this.id,
    this.userId = 'user_default',
    required this.method,
    this.operator,
    required this.accountNumber,
    required this.amountBdt,
    required this.pointsDeducted,
    this.status = 'Pending',
    required this.timestamp,
  });

  String get formattedDate {
    final date = DateTime.fromMillisecondsSinceEpoch(timestamp);
    return DateFormat('dd MMM yyyy, hh:mm a').format(date);
  }

  WithdrawalModel copyWith({
    String? id,
    String? userId,
    String? method,
    String? operator,
    String? accountNumber,
    double? amountBdt,
    int? pointsDeducted,
    String? status,
    int? timestamp,
  }) {
    return WithdrawalModel(
      id: id ?? this.id,
      userId: userId ?? this.userId,
      method: method ?? this.method,
      operator: operator ?? this.operator,
      accountNumber: accountNumber ?? this.accountNumber,
      amountBdt: amountBdt ?? this.amountBdt,
      pointsDeducted: pointsDeducted ?? this.pointsDeducted,
      status: status ?? this.status,
      timestamp: timestamp ?? this.timestamp,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'userId': userId,
      'method': method,
      'operator': operator,
      'accountNumber': accountNumber,
      'amountBdt': amountBdt,
      'pointsDeducted': pointsDeducted,
      'status': status,
      'timestamp': timestamp,
    };
  }

  factory WithdrawalModel.fromJson(Map<String, dynamic> json) {
    return WithdrawalModel(
      id: json['id'] ?? '',
      userId: json['userId'] ?? 'user_default',
      method: json['method'] ?? 'bKash',
      operator: json['operator'],
      accountNumber: json['accountNumber'] ?? '',
      amountBdt: (json['amountBdt'] as num?)?.toDouble() ?? 0.0,
      pointsDeducted: json['pointsDeducted'] ?? 0,
      status: json['status'] ?? 'Pending',
      timestamp: json['timestamp'] ?? DateTime.now().millisecondsSinceEpoch,
    );
  }
}
