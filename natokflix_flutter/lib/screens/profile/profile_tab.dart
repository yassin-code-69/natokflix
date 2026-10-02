import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:provider/provider.dart';
import '../../core/theme/app_colors.dart';
import '../../components/gender_avatar.dart';
import '../../providers/natok_provider.dart';

class ProfileTab extends StatelessWidget {
  const ProfileTab({super.key});

  void _showWithdrawalModal(BuildContext context, NatokProvider provider) {
    String selectedMethod = 'bKash';
    String? selectedOperator;
    final accountCtrl = TextEditingController(text: provider.user.phone);
    final amountCtrl = TextEditingController(text: '300');

    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      backgroundColor: AppColors.surfaceDark,
      shape: const RoundedRectangleBorder(
        borderRadius: BorderRadius.vertical(top: Radius.circular(20)),
      ),
      builder: (ctx) => StatefulBuilder(
        builder: (ctx, setModalState) {
          final minBdt = provider.economyConfig.minWithdrawalBdt;
          return Padding(
            padding: EdgeInsets.only(
              left: 20,
              right: 20,
              top: 20,
              bottom: MediaQuery.of(ctx).viewInsets.bottom + 20,
            ),
            child: SingleChildScrollView(
              child: Column(
                mainAxisSize: MainAxisSize.min,
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      Row(
                        children: const [
                          Icon(Icons.account_balance_wallet, color: AppColors.natokGold),
                          SizedBox(width: 8),
                          Text(
                            'টাকা উত্তোলন (Withdraw)',
                            style: TextStyle(fontSize: 17, fontWeight: FontWeight.bold, color: Colors.white),
                          ),
                        ],
                      ),
                      IconButton(
                        icon: const Icon(Icons.close, color: AppColors.textSecondary),
                        onPressed: () => Navigator.pop(ctx),
                      ),
                    ],
                  ),
                  const SizedBox(height: 8),
                  Text(
                    'সর্বনিম্ন উত্তোলন: ৳ ${minBdt.toInt()} | আপনার ব্যালেন্স: ৳ ${provider.user.balanceBdt.toStringAsFixed(2)}',
                    style: const TextStyle(fontSize: 12, color: AppColors.natokGoldLight),
                  ),
                  const SizedBox(height: 16),

                  // Method Selection Chips
                  const Text('পেমেন্ট মেথড নির্বাচন করুন:', style: TextStyle(fontSize: 13, color: AppColors.textSecondary)),
                  const SizedBox(height: 10),
                  Wrap(
                    spacing: 8,
                    runSpacing: 8,
                    children: [
                      _buildMethodChip(
                        name: 'bKash',
                        color: AppColors.bkashPink,
                        isSelected: selectedMethod == 'bKash',
                        onTap: () => setModalState(() => selectedMethod = 'bKash'),
                      ),
                      _buildMethodChip(
                        name: 'Nagad',
                        color: AppColors.nagadOrange,
                        isSelected: selectedMethod == 'Nagad',
                        onTap: () => setModalState(() => selectedMethod = 'Nagad'),
                      ),
                      _buildMethodChip(
                        name: 'Rocket',
                        color: AppColors.rocketPurple,
                        isSelected: selectedMethod == 'Rocket',
                        onTap: () => setModalState(() => selectedMethod = 'Rocket'),
                      ),
                      _buildMethodChip(
                        name: 'Mobile Recharge',
                        color: const Color(0xFF1E88E5),
                        isSelected: selectedMethod == 'Mobile Recharge',
                        onTap: () => setModalState(() => selectedMethod = 'Mobile Recharge'),
                      ),
                    ],
                  ),
                  const SizedBox(height: 16),

                  // Account Number
                  TextField(
                    controller: accountCtrl,
                    keyboardType: TextInputType.phone,
                    decoration: InputDecoration(
                      labelText: selectedMethod == 'Mobile Recharge' ? 'রিচার্জ মোবাইল নম্বর' : '$selectedMethod অ্যাকাউন্ট নম্বর (১১ ডিজিট)',
                      filled: true,
                      fillColor: AppColors.surfaceVariantDark,
                      border: OutlineInputBorder(borderRadius: BorderRadius.circular(10)),
                      prefixIcon: const Icon(Icons.phone_android, color: AppColors.textSecondary),
                    ),
                  ),
                  const SizedBox(height: 12),

                  // Amount
                  TextField(
                    controller: amountCtrl,
                    keyboardType: TextInputType.number,
                    decoration: InputDecoration(
                      labelText: 'উত্তোলনের পরিমাণ (টাকা)',
                      filled: true,
                      fillColor: AppColors.surfaceVariantDark,
                      border: OutlineInputBorder(borderRadius: BorderRadius.circular(10)),
                      prefixIcon: const Icon(Icons.money, color: AppColors.natokGold),
                    ),
                  ),
                  const SizedBox(height: 20),

                  // Submit Button
                  SizedBox(
                    width: double.infinity,
                    height: 48,
                    child: ElevatedButton(
                      onPressed: () {
                        final amt = double.tryParse(amountCtrl.text.trim()) ?? 0.0;
                        final acc = accountCtrl.text.trim();

                        final err = provider.submitWithdrawal(
                          method: selectedMethod,
                          accountNumber: acc,
                          amountBdt: amt,
                          operator: selectedOperator,
                        );

                        if (err != null) {
                          ScaffoldMessenger.of(context).showSnackBar(
                            SnackBar(content: Text(err), backgroundColor: AppColors.statusRejected),
                          );
                        } else {
                          Navigator.pop(ctx);
                          ScaffoldMessenger.of(context).showSnackBar(
                            const SnackBar(
                              content: Text('🎉 উইথড্রয়াল রিকোয়েস্ট সফলভাবে জমা হয়েছে! ২৪ ঘণ্টার মধ্যে পেমেন্ট পৌঁছে যাবে।'),
                              backgroundColor: AppColors.statusSuccess,
                            ),
                          );
                        }
                      },
                      style: ElevatedButton.styleFrom(
                        backgroundColor: AppColors.natokRed,
                        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
                      ),
                      child: const Text(
                        'রিকোয়েস্ট সাবমিট করুন',
                        style: TextStyle(fontSize: 15, fontWeight: FontWeight.bold, color: Colors.white),
                      ),
                    ),
                  ),
                ],
              ),
            ),
          );
        },
      ),
    );
  }

  static Widget _buildMethodChip({
    required String name,
    required Color color,
    required bool isSelected,
    required VoidCallback onTap,
  }) {
    return InkWell(
      onTap: onTap,
      borderRadius: BorderRadius.circular(10),
      child: Container(
        padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 8),
        decoration: BoxDecoration(
          color: isSelected ? color : AppColors.surfaceVariantDark,
          borderRadius: BorderRadius.circular(10),
          border: Border.all(color: isSelected ? Colors.white : AppColors.borderDark, width: 1.2),
        ),
        child: Text(
          name,
          style: TextStyle(
            fontSize: 12,
            fontWeight: isSelected ? FontWeight.bold : FontWeight.w500,
            color: isSelected ? Colors.white : AppColors.textSecondary,
          ),
        ),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    final provider = context.watch<NatokProvider>();
    final user = provider.user;

    return SingleChildScrollView(
      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // Profile Details Card
          Container(
            padding: const EdgeInsets.all(16),
            decoration: BoxDecoration(
              gradient: const LinearGradient(
                colors: [Color(0xFF261014), AppColors.surfaceCard],
              ),
              borderRadius: BorderRadius.circular(16),
              border: Border.all(color: AppColors.borderDark),
            ),
            child: Row(
              children: [
                GenderAvatar(
                  gender: user.gender,
                  size: 64,
                  levelBadge: user.levelBadge,
                  showBadge: true,
                ),
                const SizedBox(width: 16),
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Row(
                        children: [
                          Flexible(
                            child: Text(
                              user.name,
                              style: const TextStyle(fontSize: 18, fontWeight: FontWeight.bold, color: Colors.white),
                            ),
                          ),
                          const SizedBox(width: 6),
                          Container(
                            padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
                            decoration: BoxDecoration(
                              color: AppColors.natokGold.withOpacity(0.2),
                              borderRadius: BorderRadius.circular(4),
                              border: Border.all(color: AppColors.natokGold, width: 0.8),
                            ),
                            child: Text(
                              user.role,
                              style: const TextStyle(fontSize: 9, fontWeight: FontWeight.bold, color: AppColors.natokGold),
                            ),
                          ),
                        ],
                      ),
                      const SizedBox(height: 4),
                      Text(
                        user.phone.isNotEmpty ? user.phone : user.email,
                        style: const TextStyle(fontSize: 12, color: AppColors.textSecondary),
                      ),
                      const SizedBox(height: 2),
                      Text(
                        'সদস্যপদ: ${user.levelName}',
                        style: TextStyle(fontSize: 11, color: user.levelColor, fontWeight: FontWeight.w600),
                      ),
                    ],
                  ),
                ),
              ],
            ),
          ),
          const SizedBox(height: 16),

          // Wallet & Balance Card
          Container(
            padding: const EdgeInsets.all(18),
            decoration: BoxDecoration(
              gradient: const LinearGradient(
                colors: [Color(0xFF382405), Color(0xFF1E1428)],
                begin: Alignment.topLeft,
                end: Alignment.bottomRight,
              ),
              borderRadius: BorderRadius.circular(16),
              border: Border.all(color: AppColors.natokGold.withOpacity(0.6), width: 1.5),
            ),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    const Text(
                      'উপার্জন ওয়ালেট ব্যালেন্স',
                      style: TextStyle(fontSize: 13, color: AppColors.textSecondary, fontWeight: FontWeight.w500),
                    ),
                    Container(
                      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 3),
                      decoration: BoxDecoration(
                        color: AppColors.natokGold,
                        borderRadius: BorderRadius.circular(6),
                      ),
                      child: const Text(
                        'রেট: ১০০ কয়েন = ১০ টাকা',
                        style: TextStyle(fontSize: 10, fontWeight: FontWeight.bold, color: Colors.black),
                      ),
                    ),
                  ],
                ),
                const SizedBox(height: 10),
                Row(
                  children: [
                    Text(
                      '৳ ${user.balanceBdt.toStringAsFixed(2)}',
                      style: const TextStyle(fontSize: 32, fontWeight: FontWeight.w900, color: Colors.white),
                    ),
                    const SizedBox(width: 12),
                    Container(
                      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                      decoration: BoxDecoration(
                        color: Colors.black.withOpacity(0.4),
                        borderRadius: BorderRadius.circular(8),
                      ),
                      child: Row(
                        children: [
                          const Icon(Icons.monetization_on, color: AppColors.natokGold, size: 16),
                          const SizedBox(width: 4),
                          Text(
                            '${user.points} কয়েন',
                            style: const TextStyle(fontSize: 13, fontWeight: FontWeight.bold, color: AppColors.natokGold),
                          ),
                        ],
                      ),
                    ),
                  ],
                ),
                const SizedBox(height: 16),
                SizedBox(
                  width: double.infinity,
                  height: 44,
                  child: ElevatedButton.icon(
                    onPressed: () => _showWithdrawalModal(context, provider),
                    icon: const Icon(Icons.account_balance_wallet, color: Colors.white, size: 18),
                    label: const Text(
                      'টাকা উত্তোলন করুন (Withdraw)',
                      style: TextStyle(fontSize: 14, fontWeight: FontWeight.bold, color: Colors.white),
                    ),
                    style: ElevatedButton.styleFrom(
                      backgroundColor: AppColors.natokRed,
                      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
                    ),
                  ),
                ),
              ],
            ),
          ),
          const SizedBox(height: 16),

          // Referral Code Card
          Container(
            padding: const EdgeInsets.all(16),
            decoration: BoxDecoration(
              color: AppColors.surfaceCard,
              borderRadius: BorderRadius.circular(14),
              border: Border.all(color: AppColors.borderDark),
            ),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: const [
                    Text('আপনার রেফারেল কোড', style: TextStyle(fontSize: 14, fontWeight: FontWeight.bold, color: Colors.white)),
                    Text('+২০০ কয়েন/রেফার', style: TextStyle(fontSize: 11, fontWeight: FontWeight.bold, color: AppColors.natokGold)),
                  ],
                ),
                const SizedBox(height: 8),
                Row(
                  children: [
                    Expanded(
                      child: Container(
                        padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 10),
                        decoration: BoxDecoration(
                          color: AppColors.surfaceVariantDark,
                          borderRadius: BorderRadius.circular(8),
                          border: Border.all(color: AppColors.borderDark),
                        ),
                        child: Text(
                          user.referralCode,
                          style: const TextStyle(fontSize: 16, fontWeight: FontWeight.bold, color: AppColors.natokGold, letterSpacing: 1.5),
                        ),
                      ),
                    ),
                    const SizedBox(width: 8),
                    ElevatedButton.icon(
                      onPressed: () {
                        Clipboard.setData(ClipboardData(text: user.referralCode));
                        ScaffoldMessenger.of(context).showSnackBar(
                          const SnackBar(content: Text('রেফারেল কোড কপি করা হয়েছে!')),
                        );
                      },
                      icon: const Icon(Icons.copy, size: 16, color: Colors.white),
                      label: const Text('কপি', style: TextStyle(color: Colors.white)),
                      style: ElevatedButton.styleFrom(
                        backgroundColor: AppColors.surfaceVariantDark,
                        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
                      ),
                    ),
                  ],
                ),
              ],
            ),
          ),
          const SizedBox(height: 20),

          // Withdrawal History
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              const Text(
                'উত্তোলনের হিস্ট্রি',
                style: TextStyle(fontSize: 15, fontWeight: FontWeight.bold, color: Colors.white),
              ),
              Text(
                '${provider.withdrawals.length} টি লেনদেন',
                style: const TextStyle(fontSize: 11, color: AppColors.textSecondary),
              ),
            ],
          ),
          const SizedBox(height: 10),

          if (provider.withdrawals.isEmpty)
            Padding(
              padding: const EdgeInsets.symmetric(vertical: 24),
              child: Center(
                child: Column(
                  children: const [
                    Icon(Icons.history, color: AppColors.textMuted, size: 36),
                    SizedBox(height: 6),
                    Text('এখনও কোনো উইথড্র করেননি', style: TextStyle(fontSize: 12, color: AppColors.textMuted)),
                  ],
                ),
              ),
            )
          else
            ListView.separated(
              shrinkWrap: true,
              physics: const NeverScrollableScrollPhysics(),
              itemCount: provider.withdrawals.length,
              separatorBuilder: (_, __) => const SizedBox(height: 8),
              itemBuilder: (context, index) {
                final w = provider.withdrawals[index];
                Color statusColor = AppColors.statusPending;
                if (w.status.toLowerCase() == 'approved') statusColor = AppColors.statusSuccess;
                if (w.status.toLowerCase() == 'rejected') statusColor = AppColors.statusRejected;

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
                      Row(
                        children: [
                          Container(
                            padding: const EdgeInsets.all(8),
                            decoration: BoxDecoration(
                              color: AppColors.surfaceVariantDark,
                              shape: BoxShape.circle,
                            ),
                            child: const Icon(Icons.account_balance, color: AppColors.natokGold, size: 18),
                          ),
                          const SizedBox(width: 10),
                          Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              Text(
                                '${w.method} (${w.accountNumber})',
                                style: const TextStyle(fontSize: 13, fontWeight: FontWeight.bold, color: Colors.white),
                              ),
                              Text(w.formattedDate, style: const TextStyle(fontSize: 10, color: AppColors.textMuted)),
                            ],
                          ),
                        ],
                      ),
                      Column(
                        crossAxisAlignment: CrossAxisAlignment.end,
                        children: [
                          Text(
                            '৳ ${w.amountBdt.toStringAsFixed(0)}',
                            style: const TextStyle(fontSize: 14, fontWeight: FontWeight.bold, color: Colors.white),
                          ),
                          Container(
                            padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 1.5),
                            decoration: BoxDecoration(
                              color: statusColor.withOpacity(0.15),
                              borderRadius: BorderRadius.circular(4),
                            ),
                            child: Text(
                              w.status,
                              style: TextStyle(fontSize: 9, fontWeight: FontWeight.bold, color: statusColor),
                            ),
                          ),
                        ],
                      ),
                    ],
                  ),
                );
              },
            ),
          const SizedBox(height: 20),

          // Admin Panel Button (Shortcut)
          SizedBox(
            width: double.infinity,
            height: 44,
            child: OutlinedButton.icon(
              onPressed: () => provider.navigateToScreen(AppScreen.adminPanel),
              icon: const Icon(Icons.admin_panel_settings, color: AppColors.natokGold, size: 20),
              label: const Text(
                'অ্যাডমিন ড্যাশবোর্ড প্যানেল 🛡️',
                style: TextStyle(fontSize: 13, fontWeight: FontWeight.bold, color: AppColors.natokGold),
              ),
              style: OutlinedButton.styleFrom(
                side: const BorderSide(color: AppColors.natokGold),
                shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
              ),
            ),
          ),
          const SizedBox(height: 10),

          // Logout Button
          SizedBox(
            width: double.infinity,
            height: 44,
            child: OutlinedButton.icon(
              onPressed: () => provider.logout(),
              icon: const Icon(Icons.logout, color: AppColors.natokRed, size: 18),
              label: const Text(
                'লগআউট করুন',
                style: TextStyle(fontSize: 13, fontWeight: FontWeight.bold, color: AppColors.natokRed),
              ),
              style: OutlinedButton.styleFrom(
                side: const BorderSide(color: AppColors.natokRed),
                shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
              ),
            ),
          ),
          const SizedBox(height: 24),
        ],
      ),
    );
  }
}
