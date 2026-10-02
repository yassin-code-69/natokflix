package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TaskItem
import com.example.data.model.TaskType
import com.example.data.model.User
import com.example.ui.components.AdMobBanner
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.BorderDark
import com.example.ui.theme.NatokGold
import com.example.ui.theme.NatokRed
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun TaskScreen(
    user: User,
    tasks: List<TaskItem>,
    onClaimReward: (String) -> Unit,
    onPerformAction: (TaskItem) -> Unit,
    telegramUrl: String = "https://t.me/natokflix_official",
    youtubeUrl: String = "https://youtube.com/@natokflix_official",
    facebookUrl: String = "https://facebook.com/natokflix.official",
    socialReward: Int = 500,
    isTelegramClaimed: Boolean = false,
    isYoutubeClaimed: Boolean = false,
    isFacebookClaimed: Boolean = false,
    onCompleteSocialTask: (platform: String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf("সকল টাস্ক") }
    val filters = listOf("সকল টাস্ক", "সোশ্যাল টাস্ক", "অ্যাড টাস্ক", "ভিডিও নাটক", "শর্টস", "ডেইলি")

    val completedTasksCount = tasks.count { it.isCompleted }
    val claimedTasksCount = tasks.count { it.isClaimed }

    val filteredTasks = remember(selectedFilter, tasks) {
        when (selectedFilter) {
            "অ্যাড টাস্ক" -> tasks.filter { it.type == TaskType.WATCH_AD }
            "ভিডিও নাটক" -> tasks.filter { it.type == TaskType.WATCH_VIDEO_MINUTES || it.type == TaskType.CATEGORY_DRAMA }
            "শর্টস" -> tasks.filter { it.type == TaskType.WATCH_SHORTS }
            "ডেইলি" -> tasks.filter { it.type == TaskType.DAILY_CHECK_IN || it.type == TaskType.SPIN_WHEEL || it.type == TaskType.SHARE_APP }
            else -> tasks
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .testTag("task_screen_container"),
        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 90.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "দৈনিক টাস্ক সেন্টার",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "টাস্ক সম্পন্ন করুন এবং প্রতিদিন হাজার হাজার কয়েন আয় করুন",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }

                // Balance summary
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF261D08))
                        .border(1.dp, NatokGold.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(text = "🪙", fontSize = 13.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${user.points}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = NatokGold
                    )
                }
            }
        }

        // Daily Reset & Overall Progress Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.HourglassTop,
                                contentDescription = null,
                                tint = NatokGold,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "টাস্ক রিনিউ হতে বাকি: ১৪ ঘণ্টা ২৫ মি.",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }

                        Text(
                            text = "$completedTasksCount/${tasks.size} সম্পন্ন",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = NatokGold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val overallProgress = if (tasks.isNotEmpty()) completedTasksCount.toFloat() / tasks.size.toFloat() else 0f
                    LinearProgressIndicator(
                        progress = { overallProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = NatokGold,
                        trackColor = Color(0xFF2E2E3E)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "💡 প্রতিদিন রাত ১২টায় টাস্ক স্বয়ংক্রিয়ভাবে রিভিউ হয়ে নতুন টাস্ক যোগ হবে।",
                        fontSize = 11.sp,
                        color = Color(0xFFAAAAAA)
                    )
                }
            }
        }

        // Dedicated Social Reward Tasks Section (Facebook, YouTube, Telegram)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1528)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFAB47BC).copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("⭐", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "সোশ্যাল আর্নিং টাস্ক (Social Rewards)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(NatokGold)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("ইনস্ট্যান্ট +১৫০০ কয়েন", fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // 1. Telegram Task
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF231C30))
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier.size(36.dp).clip(CircleShape).background(Color(0xFF0088CC)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("📢", fontSize = 16.sp)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("টেলিগ্রাম চ্যানেলে জয়েন করুন", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Text("অফিসিয়াল চ্যানেল • +$socialReward Coins", fontSize = 10.sp, color = NatokGold)
                            }
                        }

                        if (isTelegramClaimed) {
                            Box(modifier = Modifier.background(Color(0xFF2E7D32), RoundedCornerShape(6.dp)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                                Text("✓ গৃহীত", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Button(
                                onClick = { onCompleteSocialTask("telegram") },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0088CC)),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Text("জয়েন (+$socialReward)", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // 2. YouTube Task
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF231C30))
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier.size(36.dp).clip(CircleShape).background(Color(0xFFFF0000)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("📺", fontSize = 16.sp)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("ইউটিউব চ্যানেল সাবস্ক্রাইব করুন", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Text("NatokFlix HD • +$socialReward Coins", fontSize = 10.sp, color = NatokGold)
                            }
                        }

                        if (isYoutubeClaimed) {
                            Box(modifier = Modifier.background(Color(0xFF2E7D32), RoundedCornerShape(6.dp)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                                Text("✓ গৃহীত", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Button(
                                onClick = { onCompleteSocialTask("youtube") },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF0000)),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Text("সাবস্ক্রাইব (+$socialReward)", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // 3. Facebook Task
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF231C30))
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier.size(36.dp).clip(CircleShape).background(Color(0xFF1877F2)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("👍", fontSize = 16.sp)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("ফেসবুক পেজে ফলো দিন", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Text("Official Community • +$socialReward Coins", fontSize = 10.sp, color = NatokGold)
                            }
                        }

                        if (isFacebookClaimed) {
                            Box(modifier = Modifier.background(Color(0xFF2E7D32), RoundedCornerShape(6.dp)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                                Text("✓ গৃহীত", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Button(
                                onClick = { onCompleteSocialTask("facebook") },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1877F2)),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Text("ফলো (+$socialReward)", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Filter chips
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(bottom = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filters.forEach { filter ->
                    val isSelected = selectedFilter == filter
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) NatokRed else SurfaceCard)
                            .border(1.dp, if (isSelected) NatokRed else BorderDark, RoundedCornerShape(20.dp))
                            .clickable { selectedFilter = filter }
                            .padding(horizontal = 14.dp, vertical = 7.dp)
                    ) {
                        Text(
                            text = filter,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color.White else TextSecondary
                        )
                    }
                }
            }
        }

        // List of 15 to 20 Tasks
        items(filteredTasks, key = { it.id }) { task ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
                    .testTag("task_item_${task.id}"),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (task.isCompleted && !task.isClaimed) Color(0xFF211D12) else SurfaceCard
                )
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        when (task.type) {
                                            TaskType.WATCH_AD -> Color(0xFF3B1518)
                                            TaskType.WATCH_VIDEO_MINUTES -> Color(0xFF1B263B)
                                            TaskType.WATCH_SHORTS -> Color(0xFF3B1F3B)
                                            TaskType.DAILY_CHECK_IN -> Color(0xFF33260A)
                                            TaskType.SPIN_WHEEL -> Color(0xFF26331A)
                                            TaskType.MEGA_BONUS -> Color(0xFF4A154B)
                                            else -> Color(0xFF262638)
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when (task.type) {
                                        TaskType.WATCH_AD -> Icons.Default.Videocam
                                        TaskType.WATCH_VIDEO_MINUTES -> Icons.Default.PlayCircle
                                        TaskType.WATCH_SHORTS -> Icons.Default.PlayArrow
                                        TaskType.DAILY_CHECK_IN -> Icons.Default.CardGiftcard
                                        TaskType.SPIN_WHEEL -> Icons.Default.Star
                                        TaskType.SHARE_APP -> Icons.Default.Share
                                        else -> Icons.Default.CheckCircle
                                    },
                                    contentDescription = null,
                                    tint = if (task.type == TaskType.MEGA_BONUS) NatokGold else Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column {
                                Text(
                                    text = task.title,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = task.description,
                                    fontSize = 11.sp,
                                    color = TextSecondary,
                                    maxLines = 1
                                )
                            }
                        }

                        // Reward Points Badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF2B200A))
                                .border(1.dp, NatokGold.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "+${task.rewardPoints} 🪙",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = NatokGold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Progress Bar & Counter
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "প্রগ্রেস: ${task.currentProgress} / ${task.targetProgress} ${task.unit}",
                            fontSize = 11.sp,
                            color = Color(0xFFCCCCCC)
                        )

                        Text(
                            text = "${(task.progressFraction * 100).toInt()}%",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (task.isCompleted) Color(0xFF69F0AE) else TextSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    LinearProgressIndicator(
                        progress = { task.progressFraction },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = if (task.isCompleted) Color(0xFF69F0AE) else NatokRed,
                        trackColor = Color(0xFF282838)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Action Button or Claim State
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        if (task.isClaimed) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFF143B1D))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color(0xFF69F0AE),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "সম্পূর্ণ হয়েছে",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF69F0AE)
                                )
                            }
                        } else if (task.isCompleted) {
                            Button(
                                onClick = { onClaimReward(task.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = NatokGold),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Text(
                                    text = "ক্লেইম করুন (+${task.rewardPoints})",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            }
                        } else {
                            Button(
                                onClick = { onPerformAction(task) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = when (task.type) {
                                        TaskType.WATCH_AD -> NatokRed
                                        TaskType.WATCH_VIDEO_MINUTES -> Color(0xFF2563EB)
                                        TaskType.WATCH_SHORTS -> Color(0xFF7C3AED)
                                        TaskType.SPIN_WHEEL -> Color(0xFFD97706)
                                        else -> Color(0xFF374151)
                                    }
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Text(
                                    text = when (task.type) {
                                        TaskType.WATCH_AD -> "অ্যাড দেখুন"
                                        TaskType.WATCH_VIDEO_MINUTES -> "নাটক দেখুন"
                                        TaskType.WATCH_SHORTS -> "শর্টস দেখুন"
                                        TaskType.SPIN_WHEEL -> "স্পিন ঘুরাও"
                                        TaskType.SHARE_APP -> "শেয়ার করুন"
                                        TaskType.DAILY_CHECK_IN -> "চেক-ইন করুন"
                                        else -> "শুরু করুন"
                                    },
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }

        // Native promo card
        item {
            Spacer(modifier = Modifier.height(14.dp))
            AdMobBanner()
        }
    }
}
