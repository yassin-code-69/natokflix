import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../core/theme/app_colors.dart';
import '../providers/natok_provider.dart';
import '../components/custom_app_bar.dart';
import 'home/home_tab.dart';
import 'shorts/shorts_tab.dart';
import 'friends/friends_tab.dart';
import 'game_zone/game_zone_tab.dart';
import 'tasks/tasks_tab.dart';
import 'spin/spin_tab.dart';
import 'profile/profile_tab.dart';

class DashboardScreen extends StatefulWidget {
  const DashboardScreen({super.key});

  @override
  State<DashboardScreen> createState() => _DashboardScreenState();
}

class _DashboardScreenState extends State<DashboardScreen> {
  bool _isSearchActive = false;

  void _toggleSearch() {
    setState(() {
      _isSearchActive = !_isSearchActive;
      if (!_isSearchActive) {
        context.read<NatokProvider>().searchVideos('');
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    final provider = context.watch<NatokProvider>();
    final currentTab = provider.selectedTab;

    Widget currentBody;
    switch (currentTab) {
      case NavigationTab.home:
        currentBody = HomeTab(isSearchActive: _isSearchActive);
        break;
      case NavigationTab.shorts:
        currentBody = const ShortsTab();
        break;
      case NavigationTab.friends:
        currentBody = const FriendsTab();
        break;
      case NavigationTab.gameZone:
        currentBody = const GameZoneTab();
        break;
      case NavigationTab.task:
        currentBody = const TasksTab();
        break;
      case NavigationTab.spin:
        currentBody = const SpinTab();
        break;
      case NavigationTab.profile:
        currentBody = const ProfileTab();
        break;
    }

    return Scaffold(
      backgroundColor: AppColors.backgroundDark,
      appBar: currentTab == NavigationTab.shorts
          ? null
          : CustomAppBar(
              onToggleSearch: _toggleSearch,
              isSearchActive: _isSearchActive,
            ),
      body: currentBody,
      bottomNavigationBar: BottomNavigationBar(
        currentIndex: currentTab.index,
        onTap: (index) {
          provider.selectTab(NavigationTab.values[index]);
        },
        backgroundColor: AppColors.surfaceDark,
        selectedItemColor: AppColors.natokRed,
        unselectedItemColor: const Color(0xFF888899),
        type: BottomNavigationBarType.fixed,
        selectedFontSize: 10,
        unselectedFontSize: 9,
        items: [
          // 1. Home
          const BottomNavigationBarItem(
            icon: Icon(Icons.home_outlined),
            activeIcon: Icon(Icons.home),
            label: 'হোম',
          ),
          // 2. Shorts
          const BottomNavigationBarItem(
            icon: Icon(Icons.play_circle_outline),
            activeIcon: Icon(Icons.play_circle),
            label: 'শর্টস',
          ),
          // 3. Friends
          const BottomNavigationBarItem(
            icon: Icon(Icons.group_outlined),
            activeIcon: Icon(Icons.group),
            label: 'আড্ডা',
          ),
          // 4. Game Zone with Tiny Badge
          BottomNavigationBarItem(
            icon: Stack(
              clipBehavior: Clip.none,
              children: [
                const Icon(Icons.sports_esports_outlined),
                Positioned(
                  top: -6,
                  right: -14,
                  child: Container(
                    padding: const EdgeInsets.symmetric(horizontal: 3, vertical: 1),
                    decoration: BoxDecoration(
                      color: AppColors.natokGold,
                      borderRadius: BorderRadius.circular(4),
                    ),
                    child: const Text(
                      'জিতুন',
                      style: TextStyle(
                        fontSize: 6,
                        fontWeight: FontWeight.bold,
                        color: Colors.black,
                      ),
                    ),
                  ),
                ),
              ],
            ),
            activeIcon: Stack(
              clipBehavior: Clip.none,
              children: [
                const Icon(Icons.sports_esports),
                Positioned(
                  top: -6,
                  right: -14,
                  child: Container(
                    padding: const EdgeInsets.symmetric(horizontal: 3, vertical: 1),
                    decoration: BoxDecoration(
                      color: AppColors.natokGold,
                      borderRadius: BorderRadius.circular(4),
                    ),
                    child: const Text(
                      'জিতুন',
                      style: TextStyle(
                        fontSize: 6,
                        fontWeight: FontWeight.bold,
                        color: Colors.black,
                      ),
                    ),
                  ),
                ),
              ],
            ),
            label: 'গেম জোন',
          ),
          // 5. Tasks
          const BottomNavigationBarItem(
            icon: Icon(Icons.assignment_outlined),
            activeIcon: Icon(Icons.assignment),
            label: 'টাস্ক',
          ),
          // 6. Lucky Spin
          const BottomNavigationBarItem(
            icon: Icon(Icons.casino_outlined),
            activeIcon: Icon(Icons.casino),
            label: 'স্পিন',
          ),
          // 7. Profile
          const BottomNavigationBarItem(
            icon: Icon(Icons.person_outline),
            activeIcon: Icon(Icons.person),
            label: 'প্রোফাইল',
          ),
        ],
      ),
    );
  }
}
