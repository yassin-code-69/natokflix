import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'core/services/admob_service.dart';
import 'core/theme/app_theme.dart';
import 'providers/natok_provider.dart';
import 'screens/splash_screen.dart';
import 'screens/app_open_ad_screen.dart';
import 'screens/login_screen.dart';
import 'screens/dashboard_screen.dart';
import 'screens/video_player/video_player_screen.dart';
import 'screens/chat/chat_screen.dart';
import 'screens/admin/admin_panel_screen.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await AdmobService.initialize();
  runApp(
    MultiProvider(
      providers: [
        ChangeNotifierProvider(create: (_) => NatokProvider()),
      ],
      child: const NatokFlixApp(),
    ),
  );
}

class NatokFlixApp extends StatelessWidget {
  const NatokFlixApp({super.key});

  @override
  Widget build(BuildContext context) {
    final provider = context.watch<NatokProvider>();

    return MaterialApp(
      title: 'NatokFlix',
      debugShowCheckedModeBanner: false,
      theme: AppTheme.lightTheme,
      darkTheme: AppTheme.darkTheme,
      themeMode: provider.isDarkTheme ? ThemeMode.dark : ThemeMode.light,
      home: _buildCurrentScreen(provider.currentScreen),
    );
  }

  Widget _buildCurrentScreen(AppScreen screen) {
    switch (screen) {
      case AppScreen.splash:
        return const SplashScreen();
      case AppScreen.appOpenAd:
        return const AppOpenAdScreen();
      case AppScreen.login:
        return const LoginScreen();
      case AppScreen.mainDashboard:
        return const DashboardScreen();
      case AppScreen.videoPlayer:
        return const VideoPlayerScreen();
      case AppScreen.chat:
        return const ChatScreen();
      case AppScreen.adminPanel:
        return const AdminPanelScreen();
    }
  }
}
