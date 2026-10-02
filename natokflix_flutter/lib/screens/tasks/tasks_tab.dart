import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../core/theme/app_colors.dart';
import '../../data/models/task_item.dart';
import '../../providers/natok_provider.dart';

class TasksTab extends StatelessWidget {
  const TasksTab({super.key});

  @override
  Widget build(BuildContext context) {
    final provider = context.watch<NatokProvider>();
    final tasks = provider.tasks;

    return SingleChildScrollView(
      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // Daily Check-in Banner Card
          Container(
            padding: const EdgeInsets.all(16),
            decoration: BoxDecoration(
              gradient: const LinearGradient(
                colors: [Color(0xFF382005), Color(0xFF1E1428)],
                begin: Alignment.topLeft,
                end: Alignment.bottomRight,
              ),
              borderRadius: BorderRadius.circular(16),
              border: Border.all(color: AppColors.natokGold.withOpacity(0.5)),
            ),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Row(
                      children: const [
                        Icon(Icons.calendar_today, color: AppColors.natokGold, size: 20),
                        SizedBox(width: 8),
                        Text(
                          'ডেইলি চেক-ইন রিওয়ার্ড',
                          style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold, color: Colors.white),
                        ),
                      ],
                    ),
                    Container(
                      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 3),
                      decoration: BoxDecoration(
                        color: AppColors.natokGold,
                        borderRadius: BorderRadius.circular(4),
                      ),
                      child: const Text(
                        '+৫০ কয়েন',
                        style: TextStyle(fontSize: 10, fontWeight: FontWeight.bold, color: Colors.black),
                      ),
                    ),
                  ],
                ),
                const SizedBox(height: 6),
                const Text(
                  'প্রতিদিন অন্তত একবার অ্যাপ ওপেন করে আপনার ফ্রি কয়েন সংগ্রহ করুন।',
                  style: TextStyle(fontSize: 12, color: AppColors.textSecondary),
                ),
                const SizedBox(height: 14),

                // 7 Days Streak Row
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: List.generate(7, (i) {
                    final dayNum = i + 1;
                    final isToday = dayNum == 1; // simple simulated visual
                    return Container(
                      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 6),
                      decoration: BoxDecoration(
                        color: isToday ? AppColors.natokGold : AppColors.surfaceVariantDark,
                        borderRadius: BorderRadius.circular(8),
                        border: Border.all(
                          color: isToday ? AppColors.natokGold : AppColors.borderDark,
                        ),
                      ),
                      child: Column(
                        children: [
                          Text(
                            'দিন $dayNum',
                            style: TextStyle(
                              fontSize: 9,
                              fontWeight: FontWeight.bold,
                              color: isToday ? Colors.black : AppColors.textSecondary,
                            ),
                          ),
                          const SizedBox(height: 2),
                          Icon(
                            Icons.monetization_on,
                            size: 14,
                            color: isToday ? Colors.black : AppColors.natokGold,
                          ),
                          Text(
                            '+${20 + i * 10}',
                            style: TextStyle(
                              fontSize: 8,
                              fontWeight: FontWeight.bold,
                              color: isToday ? Colors.black : Colors.white70,
                            ),
                          ),
                        ],
                      ),
                    );
                  }),
                ),
                const SizedBox(height: 16),

                SizedBox(
                  width: double.infinity,
                  height: 42,
                  child: ElevatedButton(
                    onPressed: provider.dailyCheckInClaimed
                        ? null
                        : () {
                            final success = provider.claimDailyCheckIn();
                            if (success) {
                              ScaffoldMessenger.of(context).showSnackBar(
                                const SnackBar(
                                  content: Text('🎉 ডেইলি চেক-ইন বোনাস +৫০ কয়েন যোগ হয়েছে!'),
                                  backgroundColor: AppColors.statusSuccess,
                                ),
                              );
                            }
                          },
                    style: ElevatedButton.styleFrom(
                      backgroundColor: AppColors.natokGold,
                      disabledBackgroundColor: AppColors.surfaceVariantDark,
                      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
                    ),
                    child: Text(
                      provider.dailyCheckInClaimed ? 'আজকের বোনাস নেওয়া হয়েছে ✅' : 'চেক-ইন করুন ও ৫০ কয়েন নিন 🎁',
                      style: TextStyle(
                        fontSize: 13,
                        fontWeight: FontWeight.bold,
                        color: provider.dailyCheckInClaimed ? AppColors.textMuted : Colors.black,
                      ),
                    ),
                  ),
                ),
              ],
            ),
          ),
          const SizedBox(height: 20),

          // Daily Tasks List Header
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: const [
              Text(
                'আজকের আর্নিং টাস্কসমূহ',
                style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold, color: Colors.white),
              ),
              Text(
                'রিসেট: রাত ১২:০০',
                style: TextStyle(fontSize: 11, color: AppColors.textMuted),
              ),
            ],
          ),
          const SizedBox(height: 12),

          // Task Cards
          ListView.separated(
            shrinkWrap: true,
            physics: const NeverScrollableScrollPhysics(),
            itemCount: tasks.length,
            separatorBuilder: (_, __) => const SizedBox(height: 12),
            itemBuilder: (context, index) {
              final task = tasks[index];
              return _buildTaskCard(context, task, provider);
            },
          ),
          const SizedBox(height: 24),
        ],
      ),
    );
  }

  Widget _buildTaskCard(BuildContext context, TaskItem task, NatokProvider provider) {
    return Container(
      padding: const EdgeInsets.all(14),
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
            children: [
              Expanded(
                child: Text(
                  task.title,
                  style: const TextStyle(fontSize: 14, fontWeight: FontWeight.bold, color: Colors.white),
                ),
              ),
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 7, vertical: 3),
                decoration: BoxDecoration(
                  color: AppColors.natokGold.withOpacity(0.15),
                  borderRadius: BorderRadius.circular(6),
                  border: Border.all(color: AppColors.natokGold.withOpacity(0.5)),
                ),
                child: Row(
                  children: [
                    const Icon(Icons.monetization_on, color: AppColors.natokGold, size: 12),
                    const SizedBox(width: 4),
                    Text(
                      '+${task.rewardPoints}',
                      style: const TextStyle(fontSize: 11, fontWeight: FontWeight.bold, color: AppColors.natokGold),
                    ),
                  ],
                ),
              ),
            ],
          ),
          const SizedBox(height: 4),
          Text(
            task.description,
            style: const TextStyle(fontSize: 12, color: AppColors.textSecondary),
          ),
          const SizedBox(height: 12),

          // Progress Bar & Info
          Row(
            children: [
              Expanded(
                child: ClipRRect(
                  borderRadius: BorderRadius.circular(4),
                  child: LinearProgressIndicator(
                    value: task.progressFraction,
                    minHeight: 6,
                    backgroundColor: AppColors.surfaceVariantDark,
                    color: task.isCompleted ? AppColors.statusSuccess : AppColors.natokRed,
                  ),
                ),
              ),
              const SizedBox(width: 12),
              Text(
                '${task.currentProgress}/${task.targetProgress} ${task.unit}',
                style: const TextStyle(fontSize: 11, color: AppColors.textSecondary, fontWeight: FontWeight.w600),
              ),
            ],
          ),
          const SizedBox(height: 10),

          // Action Button
          Align(
            alignment: Alignment.centerRight,
            child: SizedBox(
              height: 34,
              child: task.isClaimed
                  ? Container(
                      padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
                      decoration: BoxDecoration(
                        color: AppColors.surfaceVariantDark,
                        borderRadius: BorderRadius.circular(8),
                      ),
                      child: Row(
                        mainAxisSize: MainAxisSize.min,
                        children: const [
                          Icon(Icons.check_circle, color: AppColors.statusSuccess, size: 14),
                          SizedBox(width: 4),
                          Text('সংগৃহীত', style: TextStyle(color: AppColors.statusSuccess, fontSize: 11, fontWeight: FontWeight.bold)),
                        ],
                      ),
                    )
                  : ElevatedButton(
                      onPressed: task.isCompleted
                          ? () {
                              final claimed = provider.claimTaskReward(task.id);
                              if (claimed) {
                                ScaffoldMessenger.of(context).showSnackBar(
                                  SnackBar(
                                    content: Text('🎉 টাস্ক পুরষ্কার +${task.rewardPoints} কয়েন যোগ হয়েছে!'),
                                    backgroundColor: AppColors.statusSuccess,
                                  ),
                                );
                              }
                            }
                          : null,
                      style: ElevatedButton.styleFrom(
                        backgroundColor: AppColors.natokRed,
                        disabledBackgroundColor: AppColors.surfaceVariantDark,
                        padding: const EdgeInsets.symmetric(horizontal: 14),
                        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
                      ),
                      child: Text(
                        task.isCompleted ? 'রিওয়ার্ড নিন 🎁' : 'চলমান',
                        style: TextStyle(
                          fontSize: 12,
                          fontWeight: FontWeight.bold,
                          color: task.isCompleted ? Colors.white : AppColors.textMuted,
                        ),
                      ),
                    ),
            ),
          ),
        ],
      ),
    );
  }
}
