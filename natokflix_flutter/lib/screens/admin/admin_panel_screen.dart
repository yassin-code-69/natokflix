import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../core/theme/app_colors.dart';
import '../../data/models/video_item.dart';
import '../../providers/natok_provider.dart';

class AdminPanelScreen extends StatefulWidget {
  const AdminPanelScreen({super.key});

  @override
  State<AdminPanelScreen> createState() => _AdminPanelScreenState();
}

class _AdminPanelScreenState extends State<AdminPanelScreen> with SingleTickerProviderStateMixin {
  late TabController _tabController;

  @override
  void initState() {
    super.initState();
    _tabController = TabController(length: 4, vsync: this);
  }

  @override
  void dispose() {
    _tabController.dispose();
    super.dispose();
  }

  void _showAddDramaDialog(BuildContext context, NatokProvider provider) {
    final titleCtrl = TextEditingController();
    final channelCtrl = TextEditingController(text: 'Dhruba TV');
    final ytIdCtrl = TextEditingController();
    final durationCtrl = TextEditingController(text: '42:00');
    final castCtrl = TextEditingController(text: 'Popular Cast');
    String selectedCategory = provider.categories.isNotEmpty ? provider.categories.first : 'সকল নাটক';

    showDialog(
      context: context,
      builder: (ctx) => AlertDialog(
        title: const Text('নতুন নাটক যুক্ত করুন', style: TextStyle(fontWeight: FontWeight.bold)),
        content: SingleChildScrollView(
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              TextField(
                controller: titleCtrl,
                decoration: const InputDecoration(labelText: 'নাটকের শিরোনাম'),
              ),
              const SizedBox(height: 8),
              TextField(
                controller: ytIdCtrl,
                decoration: const InputDecoration(labelText: 'YouTube Video ID (যেমন: dQw4w9WgXcQ)'),
              ),
              const SizedBox(height: 8),
              TextField(
                controller: channelCtrl,
                decoration: const InputDecoration(labelText: 'চ্যানেলের নাম'),
              ),
              const SizedBox(height: 8),
              TextField(
                controller: castCtrl,
                decoration: const InputDecoration(labelText: 'অভিনয়শিল্পী (Cast)'),
              ),
            ],
          ),
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(ctx),
            child: const Text('বাতিল'),
          ),
          ElevatedButton(
            onPressed: () {
              final id = ytIdCtrl.text.trim();
              final title = titleCtrl.text.trim();
              if (id.isNotEmpty && title.isNotEmpty) {
                final newVideo = VideoItem(
                  id: 'vid_${DateTime.now().millisecondsSinceEpoch}',
                  title: title,
                  channelTitle: channelCtrl.text.trim(),
                  duration: durationCtrl.text.trim(),
                  thumbnailUrl: 'https://img.youtube.com/vi/$id/hqdefault.jpg',
                  youtubeVideoId: id,
                  category: selectedCategory,
                  views: '1K',
                  cast: castCtrl.text.trim(),
                );
                provider.addNatok(newVideo);
                Navigator.pop(ctx);
                ScaffoldMessenger.of(context).showSnackBar(
                  const SnackBar(content: Text('নতুন নাটক সফলভাবে যুক্ত হয়েছে!'), backgroundColor: AppColors.statusSuccess),
                );
              }
            },
            style: ElevatedButton.styleFrom(backgroundColor: AppColors.natokRed),
            child: const Text('যোগ করুন', style: TextStyle(color: Colors.white)),
          ),
        ],
      ),
    );
  }

  void _showBonusDialog(BuildContext context, NatokProvider provider) {
    final coinsCtrl = TextEditingController(text: '100');
    final reasonCtrl = TextEditingController(text: 'ঈদ মেগা বোনাস');

    showDialog(
      context: context,
      builder: (ctx) => AlertDialog(
        title: const Text('সব ইউজারকে বোনাস পাঠান', style: TextStyle(fontWeight: FontWeight.bold)),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            TextField(
              controller: coinsCtrl,
              keyboardType: TextInputType.number,
              decoration: const InputDecoration(labelText: 'কয়েনের পরিমাণ'),
            ),
            const SizedBox(height: 8),
            TextField(
              controller: reasonCtrl,
              decoration: const InputDecoration(labelText: 'কারণ / বার্তা'),
            ),
          ],
        ),
        actions: [
          TextButton(onPressed: () => Navigator.pop(ctx), child: const Text('বাতিল')),
          ElevatedButton(
            onPressed: () {
              final c = int.tryParse(coinsCtrl.text.trim()) ?? 0;
              if (c > 0) {
                provider.broadcastBonus(c, reasonCtrl.text.trim());
                Navigator.pop(ctx);
                ScaffoldMessenger.of(context).showSnackBar(
                  SnackBar(content: Text('🎉 $c কয়েন বোনাস সফলভাবে পাঠানো হয়েছে!'), backgroundColor: AppColors.statusSuccess),
                );
              }
            },
            style: ElevatedButton.styleFrom(backgroundColor: AppColors.natokRed),
            child: const Text('পাঠান', style: TextStyle(color: Colors.white)),
          ),
        ],
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    final provider = context.watch<NatokProvider>();
    final pendingWithdrawals = provider.allWithdrawals.where((w) => w.status.toLowerCase() == 'pending').toList();

    return Scaffold(
      backgroundColor: AppColors.backgroundDark,
      appBar: AppBar(
        backgroundColor: AppColors.surfaceDark,
        elevation: 2,
        title: Row(
          children: const [
            Icon(Icons.admin_panel_settings, color: AppColors.natokGold),
            SizedBox(width: 8),
            Text('অ্যাডমিন কন্ট্রোল সেন্টার 🛡️', style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
          ],
        ),
        actions: [
          TextButton.icon(
            onPressed: () => provider.exitAdminMode(),
            icon: const Icon(Icons.exit_to_app, color: Colors.white70, size: 18),
            label: const Text('প্রস্থান', style: TextStyle(color: Colors.white70)),
          ),
        ],
        bottom: TabBar(
          controller: _tabController,
          indicatorColor: AppColors.natokRed,
          labelColor: AppColors.natokRed,
          unselectedLabelColor: AppColors.textSecondary,
          isScrollable: true,
          tabs: [
            Tab(text: 'উইথড্র (${pendingWithdrawals.length})'),
            const Tab(text: 'নাটক ম্যানেজমেন্ট'),
            const Tab(text: 'ইউজার্স'),
            const Tab(text: 'ইকোনমি সেটিংস'),
          ],
        ),
      ),
      body: TabBarView(
        controller: _tabController,
        children: [
          // 1. Withdrawals Approval Tab
          _buildWithdrawalsTab(pendingWithdrawals, provider),

          // 2. Natok Management Tab
          _buildNatoksTab(context, provider),

          // 3. Users Management Tab
          _buildUsersTab(context, provider),

          // 4. Economy & Settings Tab
          _buildEconomyTab(provider),
        ],
      ),
    );
  }

  Widget _buildWithdrawalsTab(List pendingWithdrawals, NatokProvider provider) {
    if (pendingWithdrawals.isEmpty) {
      return const Center(
        child: Text('কোনো পেন্ডিং উইথড্র রিকোয়েস্ট নেই ✅', style: TextStyle(color: AppColors.textSecondary)),
      );
    }

    return ListView.separated(
      padding: const EdgeInsets.all(16),
      itemCount: pendingWithdrawals.length,
      separatorBuilder: (_, __) => const SizedBox(height: 12),
      itemBuilder: (context, index) {
        final w = pendingWithdrawals[index];
        return Container(
          padding: const EdgeInsets.all(14),
          decoration: BoxDecoration(
            color: AppColors.surfaceCard,
            borderRadius: BorderRadius.circular(12),
            border: Border.all(color: AppColors.borderDark),
          ),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Text(
                    '${w.method} - ${w.accountNumber}',
                    style: const TextStyle(fontSize: 14, fontWeight: FontWeight.bold, color: Colors.white),
                  ),
                  Text(
                    '৳ ${w.amountBdt.toStringAsFixed(0)} (${w.pointsDeducted} কয়েন)',
                    style: const TextStyle(fontSize: 14, fontWeight: FontWeight.bold, color: AppColors.natokGold),
                  ),
                ],
              ),
              const SizedBox(height: 6),
              Text(
                'রিকোয়েস্টের সময়: ${w.formattedDate}',
                style: const TextStyle(fontSize: 11, color: AppColors.textMuted),
              ),
              const SizedBox(height: 12),
              Row(
                mainAxisAlignment: MainAxisAlignment.end,
                children: [
                  OutlinedButton(
                    onPressed: () => provider.rejectWithdrawal(w.id),
                    style: OutlinedButton.styleFrom(
                      side: const BorderSide(color: AppColors.statusRejected),
                      padding: const EdgeInsets.symmetric(horizontal: 14),
                    ),
                    child: const Text('বাতিল করুন', style: TextStyle(color: AppColors.statusRejected, fontSize: 12)),
                  ),
                  const SizedBox(width: 8),
                  ElevatedButton(
                    onPressed: () => provider.approveWithdrawal(w.id),
                    style: ElevatedButton.styleFrom(
                      backgroundColor: AppColors.statusSuccess,
                      padding: const EdgeInsets.symmetric(horizontal: 14),
                    ),
                    child: const Text('অ্যাপ্রুভ করুন ✅', style: TextStyle(color: Colors.white, fontSize: 12)),
                  ),
                ],
              ),
            ],
          ),
        );
      },
    );
  }

  Widget _buildNatoksTab(BuildContext context, NatokProvider provider) {
    return Column(
      children: [
        Padding(
          padding: const EdgeInsets.all(14.0),
          child: SizedBox(
            width: double.infinity,
            height: 44,
            child: ElevatedButton.icon(
              onPressed: () => _showAddDramaDialog(context, provider),
              icon: const Icon(Icons.add, color: Colors.white),
              label: const Text('নতুন নাটক যুক্ত করুন', style: TextStyle(color: Colors.white, fontWeight: FontWeight.bold)),
              style: ElevatedButton.styleFrom(backgroundColor: AppColors.natokRed),
            ),
          ),
        ),
        Expanded(
          child: ListView.separated(
            padding: const EdgeInsets.symmetric(horizontal: 14),
            itemCount: provider.natoks.length,
            separatorBuilder: (_, __) => const SizedBox(height: 8),
            itemBuilder: (context, index) {
              final natok = provider.natoks[index];
              return Container(
                padding: const EdgeInsets.all(10),
                decoration: BoxDecoration(
                  color: AppColors.surfaceCard,
                  borderRadius: BorderRadius.circular(10),
                  border: Border.all(color: AppColors.borderDark),
                ),
                child: Row(
                  children: [
                    ClipRRect(
                      borderRadius: BorderRadius.circular(6),
                      child: Image.network(
                        natok.thumbnailUrl,
                        width: 70,
                        height: 44,
                        fit: BoxFit.cover,
                        errorBuilder: (_, __, ___) => Container(color: Colors.grey, width: 70, height: 44),
                      ),
                    ),
                    const SizedBox(width: 10),
                    Expanded(
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Text(natok.title, maxLines: 1, overflow: TextOverflow.ellipsis, style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 13, color: Colors.white)),
                          Text(natok.channelTitle, style: const TextStyle(fontSize: 11, color: AppColors.textSecondary)),
                        ],
                      ),
                    ),
                    IconButton(
                      icon: const Icon(Icons.delete_outline, color: AppColors.statusRejected, size: 20),
                      onPressed: () => provider.deleteNatok(natok.id),
                    ),
                  ],
                ),
              );
            },
          ),
        ),
      ],
    );
  }

  Widget _buildUsersTab(BuildContext context, NatokProvider provider) {
    return Column(
      children: [
        Padding(
          padding: const EdgeInsets.all(14),
          child: SizedBox(
            width: double.infinity,
            height: 44,
            child: ElevatedButton.icon(
              onPressed: () => _showBonusDialog(context, provider),
              icon: const Icon(Icons.card_giftcard, color: Colors.white),
              label: const Text('সব ইউজারকে বোনাস কয়েন পাঠান', style: TextStyle(color: Colors.white, fontWeight: FontWeight.bold)),
              style: ElevatedButton.styleFrom(backgroundColor: AppColors.natokGold),
            ),
          ),
        ),
        Expanded(
          child: ListView.separated(
            padding: const EdgeInsets.symmetric(horizontal: 14),
            itemCount: provider.allUsers.length,
            separatorBuilder: (_, __) => const SizedBox(height: 8),
            itemBuilder: (context, index) {
              final u = provider.allUsers[index];
              return Container(
                padding: const EdgeInsets.all(12),
                decoration: BoxDecoration(
                  color: AppColors.surfaceCard,
                  borderRadius: BorderRadius.circular(10),
                  border: Border.all(color: AppColors.borderDark),
                ),
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(u.name, style: const TextStyle(fontSize: 14, fontWeight: FontWeight.bold, color: Colors.white)),
                        Text(u.email, style: const TextStyle(fontSize: 11, color: AppColors.textSecondary)),
                        Text('রেফার কোড: ${u.referralCode}', style: const TextStyle(fontSize: 10, color: AppColors.natokGold)),
                      ],
                    ),
                    Column(
                      crossAxisAlignment: CrossAxisAlignment.end,
                      children: [
                        Text('৳ ${u.balanceBdt.toStringAsFixed(0)}', style: const TextStyle(fontWeight: FontWeight.bold, color: Colors.white)),
                        Text('${u.points} কয়েন', style: const TextStyle(fontSize: 11, color: AppColors.natokGold)),
                      ],
                    ),
                  ],
                ),
              );
            },
          ),
        ),
      ],
    );
  }

  Widget _buildEconomyTab(NatokProvider provider) {
    final cfg = provider.economyConfig;
    return SingleChildScrollView(
      padding: const EdgeInsets.all(16),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const Text('ইকোনমি ও পেমেন্ট কন্ট্রোল', style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold, color: Colors.white)),
          const SizedBox(height: 12),
          SwitchListTile(
            title: const Text('মেইনটেন্যান্স মোড (Maintenance Mode)'),
            subtitle: const Text('অন থাকলে সাধারণ ইউজাররা নোটিশ দেখতে পাবে।'),
            value: cfg.isMaintenanceMode,
            activeColor: AppColors.natokRed,
            onChanged: (val) {
              provider.updateEconomy(cfg.copyWith(isMaintenanceMode: val));
            },
          ),
          const Divider(color: AppColors.borderDark),
          SwitchListTile(
            title: const Text('বিকাশ (bKash) পেমেন্ট গেটওয়ে'),
            value: cfg.isBkashEnabled,
            activeColor: AppColors.bkashPink,
            onChanged: (val) {
              provider.updateEconomy(cfg.copyWith(isBkashEnabled: val));
            },
          ),
          SwitchListTile(
            title: const Text('নগদ (Nagad) পেমেন্ট গেটওয়ে'),
            value: cfg.isNagadEnabled,
            activeColor: AppColors.nagadOrange,
            onChanged: (val) {
              provider.updateEconomy(cfg.copyWith(isNagadEnabled: val));
            },
          ),
          SwitchListTile(
            title: const Text('রকেট (Rocket) পেমেন্ট গেটওয়ে'),
            value: cfg.isRocketEnabled,
            activeColor: AppColors.rocketPurple,
            onChanged: (val) {
              provider.updateEconomy(cfg.copyWith(isRocketEnabled: val));
            },
          ),
          const SizedBox(height: 16),
          const Text('অ্যানাউন্সমেন্ট নোটিশ', style: TextStyle(fontWeight: FontWeight.bold, color: Colors.white)),
          const SizedBox(height: 8),
          TextField(
            controller: TextEditingController(text: cfg.announcementNotice),
            maxLines: 2,
            decoration: InputDecoration(
              filled: true,
              fillColor: AppColors.surfaceVariantDark,
              border: OutlineInputBorder(borderRadius: BorderRadius.circular(10)),
            ),
            onSubmitted: (newNotice) {
              provider.updateEconomy(cfg.copyWith(announcementNotice: newNotice));
            },
          ),
        ],
      ),
    );
  }
}
