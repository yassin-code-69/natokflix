import 'dart:convert';
import 'package:shared_preferences/shared_preferences.dart';
import '../models/user_model.dart';
import '../models/withdrawal_model.dart';
import '../models/task_item.dart';
import '../models/friend_model.dart';

class LocalStorageService {
  static const String _userKey = 'user_data';
  static const String _withdrawalsKey = 'withdrawals_data';
  static const String _tasksKey = 'tasks_data';
  static const String _chatKey = 'chat_messages_data';
  static const String _dailyCheckInKey = 'daily_check_in_date';
  static const String _freeSpinsUsedKey = 'free_spins_used_date';
  static const String _darkThemeKey = 'is_dark_theme';

  static Future<void> saveUser(UserModel user) async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString(_userKey, jsonEncode(user.toJson()));
  }

  static Future<UserModel?> loadUser() async {
    final prefs = await SharedPreferences.getInstance();
    final jsonString = prefs.getString(_userKey);
    if (jsonString != null) {
      try {
        final map = jsonDecode(jsonString);
        return UserModel.fromJson(map);
      } catch (_) {}
    }
    return null;
  }

  static Future<void> saveWithdrawals(List<WithdrawalModel> list) async {
    final prefs = await SharedPreferences.getInstance();
    final jsonList = list.map((e) => e.toJson()).toList();
    await prefs.setString(_withdrawalsKey, jsonEncode(jsonList));
  }

  static Future<List<WithdrawalModel>> loadWithdrawals() async {
    final prefs = await SharedPreferences.getInstance();
    final jsonString = prefs.getString(_withdrawalsKey);
    if (jsonString != null) {
      try {
        final List list = jsonDecode(jsonString);
        return list.map((e) => WithdrawalModel.fromJson(e)).toList();
      } catch (_) {}
    }
    return [];
  }

  static Future<void> saveTasks(List<TaskItem> list) async {
    final prefs = await SharedPreferences.getInstance();
    final jsonList = list.map((e) => e.toJson()).toList();
    await prefs.setString(_tasksKey, jsonEncode(jsonList));
  }

  static Future<List<TaskItem>> loadTasks() async {
    final prefs = await SharedPreferences.getInstance();
    final jsonString = prefs.getString(_tasksKey);
    if (jsonString != null) {
      try {
        final List list = jsonDecode(jsonString);
        return list.map((e) => TaskItem.fromJson(e)).toList();
      } catch (_) {}
    }
    return [];
  }

  static Future<void> saveChatMessages(List<ChatMessage> list) async {
    final prefs = await SharedPreferences.getInstance();
    final jsonList = list.map((e) => e.toJson()).toList();
    await prefs.setString(_chatKey, jsonEncode(jsonList));
  }

  static Future<List<ChatMessage>> loadChatMessages() async {
    final prefs = await SharedPreferences.getInstance();
    final jsonString = prefs.getString(_chatKey);
    if (jsonString != null) {
      try {
        final List list = jsonDecode(jsonString);
        return list.map((e) => ChatMessage.fromJson(e)).toList();
      } catch (_) {}
    }
    return [];
  }

  static Future<bool> isDailyCheckInDoneToday() async {
    final prefs = await SharedPreferences.getInstance();
    final dateStr = prefs.getString(_dailyCheckInKey);
    final today = DateTime.now().toIso8601String().substring(0, 10);
    return dateStr == today;
  }

  static Future<void> markDailyCheckInDone() async {
    final prefs = await SharedPreferences.getInstance();
    final today = DateTime.now().toIso8601String().substring(0, 10);
    await prefs.setString(_dailyCheckInKey, today);
  }

  static Future<bool> isFreeSpinUsedToday() async {
    final prefs = await SharedPreferences.getInstance();
    final dateStr = prefs.getString(_freeSpinsUsedKey);
    final today = DateTime.now().toIso8601String().substring(0, 10);
    return dateStr == today;
  }

  static Future<void> markFreeSpinUsed() async {
    final prefs = await SharedPreferences.getInstance();
    final today = DateTime.now().toIso8601String().substring(0, 10);
    await prefs.setString(_freeSpinsUsedKey, today);
  }

  static Future<bool> loadThemeMode() async {
    final prefs = await SharedPreferences.getInstance();
    return prefs.getBool(_darkThemeKey) ?? true;
  }

  static Future<void> saveThemeMode(bool isDark) async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setBool(_darkThemeKey, isDark);
  }
}
