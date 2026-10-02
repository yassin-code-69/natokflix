package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Male
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.User
import com.example.data.model.Withdrawal
import com.example.ui.components.AdMobBanner
import com.example.ui.components.GenderAvatar
import com.example.ui.components.GlowingNatokFlixLogo
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.BkashPink
import com.example.ui.theme.BorderDark
import com.example.ui.theme.NagadOrange
import com.example.ui.theme.NatokGold
import com.example.ui.theme.NatokRed
import com.example.ui.theme.RocketPurple
import com.example.ui.theme.StatusApproved
import com.example.ui.theme.StatusPending
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    user: User,
    withdrawals: List<Withdrawal>,
    onRequestWithdrawal: (method: String, operator: String?, phone: String, amount: Double) -> Unit,
    onLogout: () -> Unit,
    onAdminPinSubmit: (String) -> Boolean,
    onUpdateProfile: (name: String, gender: String, birthday: String, phone: String) -> Unit = { _, _, _, _ -> },
    minWithdrawalBdt: Double = 300.0,
    coinsPerBdtRate: Int = 10,
    referrerBonusCoins: Int = 200,
    refereeBonusCoins: Int = 100,
    isBkashEnabled: Boolean = true,
    isNagadEnabled: Boolean = true,
    isRocketEnabled: Boolean = true,
    telegramChannelUrl: String = "https://t.me/natokflix_official",
    whatsappSupportNumber: String = "+8801700000000",
    withdrawalRulesText: String = "উত্তোলনের নিয়মাবলি:\n১. সর্বনিম্ন উত্তোলন ৩০০ টাকা (১০০ কয়েন = ১০ টাকা, ৩,০০০ কয়েন প্রয়োজন)।\n২. উইথড্র সময়সূচি: প্রতিদিন সকাল ১০:০০ টা থেকে রাত ০৮:০০ টা পর্যন্ত উইথড্র সিস্টেম সক্রিয় থাকে। রাত ৮:০১ থেকে সকাল ৯:৫৯ পর্যন্ত বন্ধ থাকে।\n৩. সঠিক ১১ ডিজিটের মোবাইল নম্বর প্রদান করতে হবে।\n৪. ১ থেকে ২৪ ঘণ্টার মধ্যে পেমেন্ট পৌঁছে যাবে।",
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Available Withdrawal Gateways
    var selectedMethod by remember { mutableStateOf("Mobile Recharge") }

    // BD Mobile Operators for Mobile Recharge
    val bdOperators = listOf(
        "Grameenphone (GP)",
        "Robi",
        "Banglalink",
        "Airtel",
        "Skitto",
        "Teletalk"
    )
    var selectedOperator by remember { mutableStateOf(bdOperators.first()) }
    var isOperatorDropdownExpanded by remember { mutableStateOf(false) }

    var accountNumber by remember { mutableStateOf(user.phone) }
    var withdrawAmount by remember { mutableDoubleStateOf(300.0) }
    var customAmountText by remember { mutableStateOf("300") }

    val availableMethods = remember(isBkashEnabled, isNagadEnabled, isRocketEnabled) {
        buildList {
            add("Mobile Recharge")
            if (isBkashEnabled) add("bKash")
            if (isNagadEnabled) add("Nagad")
            if (isRocketEnabled) add("Rocket")
        }
    }

    // Phone validation
    val cleanPhone = accountNumber.trim().replace("-", "").replace(" ", "")
    val isPhoneValid = cleanPhone.length == 11 && cleanPhone.startsWith("01")

    // Edit Profile Dialog State
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var editNameInput by remember(user.name) { mutableStateOf(user.name) }
    var editGenderInput by remember(user.gender) { mutableStateOf(user.gender) }
    var editBirthdayInput by remember(user.birthday) { mutableStateOf(user.birthday) }
    var editPhoneInput by remember(user.phone) { mutableStateOf(user.phone) }

    // Secret Admin Pin Dialog
    var showSecretAdminDialog by remember { mutableStateOf(false) }
    var secretPinInput by remember { mutableStateOf("") }
    var secretPinError by remember { mutableStateOf(false) }

    val presetAmounts = listOf(300.0, 500.0, 1000.0, 2000.0, 5000.0, 10000.0, 20000.0)

    // Daily Active Operating Window (10:00 AM - 8:00 PM Dhaka Time)
    val currentCal = remember { java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("Asia/Dhaka")) }
    val currentHour = currentCal.get(java.util.Calendar.HOUR_OF_DAY)
    val isWithdrawalWindowOpen = currentHour in 10 until 20 // 10:00:00 to 19:59:59
    val isDemoUser = user.email.contains("demo") || user.name.contains("ডেমো")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .testTag("profile_screen_container"),
        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 90.dp)
    ) {
        // Profile Branding Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                GlowingNatokFlixLogo(size = 32.dp, showText = true, fontSize = 20.sp, enablePulse = false)
                Text(
                    text = "প্রোফাইল ও ওয়ালেট",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = NatokGold
                )
            }
        }

        // User Profile Header Card with Standard Gender Default Avatar (No upload needed)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Standard Gender-Based Default Avatar (Boy / Girl)
                        GenderAvatar(
                            gender = user.gender,
                            name = user.name,
                            size = 60.dp,
                            showOnlineDot = true,
                            isOnline = true
                        )

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = user.name,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Verified",
                                        tint = Color(0xFF4CAF50),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                // Edit Profile Button
                                IconButton(
                                    onClick = {
                                        editNameInput = user.name
                                        editGenderInput = user.gender
                                        editBirthdayInput = user.birthday
                                        editPhoneInput = user.phone
                                        showEditProfileDialog = true
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit Profile",
                                        tint = NatokGold,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            Text(
                                text = user.email,
                                fontSize = 11.sp,
                                color = TextSecondary
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            // Level Badge System
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.padding(bottom = 6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .background(user.levelColor.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
                                        .border(0.5.dp, user.levelColor, RoundedCornerShape(8.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = user.levelBadge,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = user.levelColor
                                    )
                                }
                                Text(
                                    text = user.levelName,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            // Gender & Birthday Badges
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                val isBoy = user.gender.equals("Boy", ignoreCase = true)
                                Box(
                                    modifier = Modifier
                                        .background(if (isBoy) Color(0xFF0D47A1) else Color(0xFF880E4F), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = if (isBoy) "👦 ছেলে (Boy)" else "👧 মেয়ে (Girl)",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFF261D08), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "🎂 ${user.birthday}",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = NatokGold
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Profile Analytics Grid (Friends Count, Referral, Phone)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF16121E), RoundedCornerShape(12.dp))
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Friends Count
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Group, contentDescription = null, tint = NatokGold, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${user.friendsCount}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            Text(text = "মোট ফ্রেন্ডস", fontSize = 10.sp, color = TextSecondary)
                        }

                        Box(modifier = Modifier.width(1.dp).height(24.dp).background(BorderDark))

                        // Total Referrals Count
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = Color(0xFFFF9800), modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${user.totalReferrals}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            Text(text = "সফল রেফারেল", fontSize = 10.sp, color = TextSecondary)
                        }

                        Box(modifier = Modifier.width(1.dp).height(24.dp).background(BorderDark))

                        // Phone
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Phone, contentDescription = null, tint = Color(0xFF64B5F6), modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = user.phone.take(11),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            Text(text = "মোবাইল নম্বর 🔒", fontSize = 10.sp, color = NatokGold)
                        }
                    }
                }
            }
        }

        // Referral Milestone Mega Bonus Tracker Card (50 Referrals -> 100,000 Coins)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF241A0E))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, NatokGold.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("👑", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "রেফারেল মেগা বোনাস মাইলস্টোন",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = NatokGold
                            )
                        }
                        Text(
                            text = "${user.totalReferrals}/50 সম্পন্ন",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val progress = (user.totalReferrals.toFloat() / 50f).coerceIn(0f, 1f)
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = NatokGold,
                        trackColor = Color(0xFF3E2D18)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    val safeRate = if (coinsPerBdtRate > 0) coinsPerBdtRate.toDouble() else 10.0
                    val referrerBdtStr = String.format(java.util.Locale.US, "%.1f", referrerBonusCoins / safeRate)
                    val refereeBdtStr = String.format(java.util.Locale.US, "%.1f", refereeBonusCoins / safeRate)

                    Text(
                        text = "🎁 প্রতি রেফারে $referrerBonusCoins কয়েন (৳$referrerBdtStr) এবং রেফার কোড ব্যবহারকারী নতুন বন্ধু পাবেন $refereeBonusCoins কয়েন (৳$refereeBdtStr) ওয়েলকাম বোনাস!",
                        fontSize = 11.sp,
                        color = Color(0xFFE0D8C8),
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF161009), RoundedCornerShape(10.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "আপনার রেফার কোড:", fontSize = 10.sp, color = TextSecondary)
                            Text(
                                text = user.referralCode,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = NatokGold
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            // Copy Code Button
                            Button(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("Referral Code", user.referralCode)
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(context, "রেফার কোড কপি হয়েছে: ${user.referralCode}", Toast.LENGTH_SHORT).show()
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3D2C15)),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp), tint = NatokGold)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("কপি", fontSize = 11.sp, color = NatokGold)
                            }

                            // Share Button
                            Button(
                                onClick = {
                                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(
                                            Intent.EXTRA_TEXT,
                                            "NatokFlix অ্যাপ ডাউনলোড করে বাংলা নাটক ও শর্টস দেখে ইনকাম করুন! সাইন-আপ করার সময় আমার রেফার কোড '${user.referralCode}' ব্যবহার করে সাথে সাথে $refereeBonusCoins ফ্রি কয়েন (৳$refereeBdtStr টাকা) বোনাস নিন! 🎬💰"
                                        )
                                    }
                                    context.startActivity(Intent.createChooser(shareIntent, "বন্ধুদের সাথে শেয়ার করুন"))
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = NatokRed),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.White)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("শেয়ার", fontSize = 11.sp, color = Color.White)
                            }
                        }
                    }
                }
            }
        }

        // Total Balance & Automated Cashout Progress Bar
        item {
            val formattedPoints = java.text.NumberFormat.getNumberInstance(java.util.Locale.US).format(user.points)
            val bdtBalanceStr = String.format(java.util.Locale.US, "%.2f", user.balanceBdt)
            val targetCoins = (minWithdrawalBdt * coinsPerBdtRate).toInt().coerceAtLeast(3000)
            val progressRatio = (user.points.toFloat() / targetCoins.toFloat()).coerceIn(0f, 1f)
            val isGoalReached = user.points >= targetCoins

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1822))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, NatokGold.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                        .padding(18.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "লাইভ ওয়ালেট ব্যালেন্স (Live Wallet Balance)",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                                Text(
                                    text = "$formattedPoints Coins (৳$bdtBalanceStr BDT)",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "কনভার্সন রেট",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                                Text(
                                    text = "100 Coins = ৳10 BDT",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NatokGold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Real-time progress bar towards 300 BDT (3,000 Coins) goal
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "🎯 ", fontSize = 12.sp)
                                Text(
                                    text = "৳${minWithdrawalBdt.toInt()} BDT (${java.text.NumberFormat.getNumberInstance(java.util.Locale.US).format(targetCoins)} Coins) ক্যাশআউট গোল",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NatokGold
                                )
                            }
                            Text(
                                text = "${(progressRatio * 100).toInt()}%",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isGoalReached) Color(0xFF4CAF50) else Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        LinearProgressIndicator(
                            progress = { progressRatio },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = if (isGoalReached) Color(0xFF4CAF50) else NatokGold,
                            trackColor = Color(0xFF332A1C)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        if (isGoalReached) {
                            Text(
                                text = "✅ অভিনন্দন! আপনার ব্যালেন্স ৩০০ BDT সর্বনিম্ন উত্তোলন সীমার উপরে রয়েছে। উইথড্র অপশন আনলকড!",
                                fontSize = 11.sp,
                                color = Color(0xFF81C784),
                                fontWeight = FontWeight.SemiBold
                            )
                        } else {
                            val remainingCoins = targetCoins - user.points
                            val remainingBdt = minWithdrawalBdt - user.balanceBdt
                            Text(
                                text = "🔒 ক্যাশআউট আনলক করতে আরও $remainingCoins Coins (৳${String.format(java.util.Locale.US, "%.2f", remainingBdt)} BDT) প্রয়োজন",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }
        }

        // Withdrawal Request Section Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "টাকা উত্তোলন ও মোবাইল রিচার্জ",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Icon(
                            imageVector = Icons.Default.AccountBalanceWallet,
                            contentDescription = null,
                            tint = NatokRed
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Daily Active Operating Window (10:00 AM - 8:00 PM) Indicator Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isWithdrawalWindowOpen) Color(0xFF14241B) else Color(0xFF2C1619)
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (isWithdrawalWindowOpen) Color(0xFF00E676).copy(alpha = 0.6f) else Color(0xFFFF5252).copy(alpha = 0.6f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .background(
                                        if (isWithdrawalWindowOpen) Color(0xFF00E676) else Color(0xFFFF5252),
                                        CircleShape
                                    )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = if (isWithdrawalWindowOpen) "উইথড্র গেটওয়ে এখন সক্রিয় (সকাল ১০:০০ টা - রাত ০৮:০০ টা)"
                                           else "উইথড্র গেটওয়ে এখন সাময়িকভাবে বন্ধ রয়েছে",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isWithdrawalWindowOpen) Color(0xFF00E676) else Color(0xFFFF5252)
                                )
                                Text(
                                    text = if (isWithdrawalWindowOpen) "আজ রাত ৮:০০ টা পর্যন্ত উইথড্র রিকোয়েস্ট পাঠাতে পারবেন।"
                                           else "উইথড্র সার্ভিস প্রতিদিন সকাল ১০:০০ টা থেকে রাত ৮:০০ টা পর্যন্ত সক্রিয় থাকে।",
                                    fontSize = 10.sp,
                                    color = if (isWithdrawalWindowOpen) Color(0xFFB9F6CA) else Color(0xFFFFCDD2)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "পেমেন্ট মাধ্যম নির্বাচন করুন:",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Multi-Payment Gateway Chips (Mobile Recharge, bKash, Nagad, Rocket)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        availableMethods.forEach { method ->
                            val isSelected = selectedMethod == method
                            val (badgeBg, bengali) = when (method) {
                                "Mobile Recharge" -> Color(0xFF00897B) to "রিচার্জ"
                                "bKash" -> BkashPink to "বিকাশ"
                                "Nagad" -> NagadOrange to "নগদ"
                                else -> RocketPurple to "রকেট"
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) badgeBg else Color(0xFF1F1E28))
                                    .border(
                                        1.dp,
                                        if (isSelected) Color.White else BorderDark,
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable { selectedMethod = method }
                                    .testTag("withdraw_method_$method"),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = if (method == "Mobile Recharge") "Recharge" else method,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = Color.White,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = bengali,
                                        fontSize = 10.sp,
                                        color = if (isSelected) Color.White else Color(0xFFAAAAAA)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Mobile Recharge Operator Dropdown Menu (GP, Robi, Banglalink, Airtel, Skitto, Teletalk)
                    if (selectedMethod == "Mobile Recharge") {
                        Text(
                            text = "মোবাইল অপারেটর নির্বাচন করুন:",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        ExposedDropdownMenuBox(
                            expanded = isOperatorDropdownExpanded,
                            onExpandedChange = { isOperatorDropdownExpanded = it }
                        ) {
                            OutlinedTextField(
                                value = selectedOperator,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = isOperatorDropdownExpanded)
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = NatokGold)
                                },
                                modifier = Modifier
                                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                    .fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = NatokRed,
                                    unfocusedBorderColor = BorderDark,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                )
                            )

                            ExposedDropdownMenu(
                                expanded = isOperatorDropdownExpanded,
                                onDismissRequest = { isOperatorDropdownExpanded = false }
                            ) {
                                bdOperators.forEach { op ->
                                    DropdownMenuItem(
                                        text = { Text(op, fontSize = 13.sp) },
                                        onClick = {
                                            selectedOperator = op
                                            isOperatorDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    // Account / Phone Number Input with 11-digit Bangladesh phone validation
                    OutlinedTextField(
                        value = accountNumber,
                        onValueChange = { accountNumber = it },
                        label = {
                            Text(
                                if (selectedMethod == "Mobile Recharge") "১১ ডিজিটের রিচার্জ নম্বর (017xxxxxxxx)"
                                else "$selectedMethod একাউন্ট নম্বর (১১ ডিজিট)"
                            )
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Phone, contentDescription = null, tint = if (isPhoneValid) Color(0xFF4CAF50) else TextSecondary)
                        },
                        trailingIcon = {
                            if (isPhoneValid) {
                                Icon(Icons.Default.CheckCircle, contentDescription = "Valid", tint = Color(0xFF4CAF50))
                            }
                        },
                        isError = accountNumber.isNotBlank() && !isPhoneValid,
                        supportingText = {
                            if (accountNumber.isNotBlank() && !isPhoneValid) {
                                Text("সঠিক ১১ ডিজিটের মোবাইল নম্বর দিন (যেমন: 01788123456)", color = Color(0xFFFF5252), fontSize = 11.sp)
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("withdraw_phone_input"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NatokRed,
                            unfocusedBorderColor = BorderDark,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "উত্তোলনের পরিমাণ নির্বাচন করুন (BDT):",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Preset Amount Chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        presetAmounts.forEach { amount ->
                            val isSelected = withdrawAmount == amount
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(38.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) NatokRed else Color(0xFF1F1E28))
                                    .border(
                                        1.dp,
                                        if (isSelected) NatokGold else BorderDark,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable {
                                        withdrawAmount = amount
                                        customAmountText = amount.toInt().toString()
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "৳${amount.toInt()}",
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Submit Withdrawal Button with Operating Window and Threshold Validation
                    val canWithdraw = (isWithdrawalWindowOpen || isDemoUser) && user.balanceBdt >= withdrawAmount && withdrawAmount >= minWithdrawalBdt && isPhoneValid
                    Button(
                        onClick = {
                            if (canWithdraw) {
                                val operatorParam = if (selectedMethod == "Mobile Recharge") selectedOperator else null
                                onRequestWithdrawal(selectedMethod, operatorParam, cleanPhone, withdrawAmount)
                            }
                        },
                        enabled = canWithdraw,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("submit_withdrawal_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NatokRed,
                            disabledContainerColor = Color(0xFF3B2328)
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = if (!isWithdrawalWindowOpen && !isDemoUser) "উইথড্র সার্ভিস বন্ধ (সকাল ১০:০০ - রাত ৮:০০)"
                                   else if (!isPhoneValid) "সঠিক ১১ ডিজিট নম্বর লিখুন"
                                   else if (user.balanceBdt < withdrawAmount) "পর্যাপ্ত কয়েন ব্যালেন্স নেই"
                                   else if (withdrawAmount < minWithdrawalBdt) "সর্বনিম্ন ৳${minWithdrawalBdt.toInt()} টাকা উত্তোলন করুন"
                                   else if (selectedMethod == "Mobile Recharge") "রিচার্জ অনুরোধ পাঠান (৳${withdrawAmount.toInt()})"
                                   else "উত্তোলন অনুরোধ পাঠান (৳${withdrawAmount.toInt()})",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (canWithdraw) Color.White else Color(0xFF888888)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "💡 সর্বনিম্ন উত্তোলন ৳${minWithdrawalBdt.toInt()} টাকা। উইথড্র সময়সূচি: সকাল ১০:০০ টা থেকে রাত ০৮:০০ টা পর্যন্ত।",
                        fontSize = 11.sp,
                        color = Color(0xFFAAAAAA),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Rules and Support Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = NatokGold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "উত্তোলন নিয়মাবলি ও হেল্পলাইন",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = withdrawalRulesText,
                        fontSize = 12.sp,
                        color = Color(0xFFCCCCCC),
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF1A1828), RoundedCornerShape(10.dp))
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "হোয়াটসঅ্যাপ সাপোর্ট:",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                            Text(
                                text = whatsappSupportNumber,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = NatokGold
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "টেলিগ্রাম গ্রুপ:",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                            Text(
                                text = telegramChannelUrl.replace("https://t.me/", "@"),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF64B5F6)
                            )
                        }
                    }
                }
            }
        }

        // AdMob Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                AdMobBanner()
            }
        }

        // Withdrawal History Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = NatokGold,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "উত্তোলন হিস্টোরি (History)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Text(
                    text = "${withdrawals.size} টি লেনদেন",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }
        }

        // Withdrawal History Items
        if (withdrawals.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "এখনো কোনো উত্তোলনের রেকর্ড নেই",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        } else {
            items(withdrawals) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (item.operator != null) "${item.method} (${item.operator})" else item.method,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "(${item.accountNumber})",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "আইডি: ${item.id} • ${item.formattedDate}",
                                fontSize = 10.sp,
                                color = TextSecondary
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "৳${String.format("%.2f", item.amountBdt)}",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            val (statusColor, statusBengali) = when (item.status) {
                                "Approved", "Success", "অনুমোদিত" -> StatusApproved to "সফল"
                                "Rejected", "বাতিল" -> NatokRed to "বাতিল"
                                else -> StatusPending to "পেন্ডিং"
                            }
                            Text(
                                text = statusBengali,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = statusColor
                            )
                        }
                    }
                }
            }
        }

        // Logout and Admin Access Buttons
        item {
            Spacer(modifier = Modifier.height(20.dp))

            // Logout Button
            OutlinedButton(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .testTag("logout_button"),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF5252)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF4A1A1E)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = "লগআউট (Sign Out)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Hidden Admin Access Trigger
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showSecretAdminDialog = true }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Admin Access",
                        tint = Color(0xFF444455),
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "NatokFlix v2.6 • সিস্টেম অ্যাডমিন এক্সেস",
                        fontSize = 10.sp,
                        color = Color(0xFF555566)
                    )
                }
            }
        }
    }

    // Edit Profile Dialog (Gender, Birthday, Name - Phone and Email locked)
    if (showEditProfileDialog) {
        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = {
                Text(
                    text = "প্রোফাইল তথ্য পরিবর্তন (Edit Profile)",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Security Notice Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF261A10))
                            .border(1.dp, NatokGold.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = NatokGold, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "নিরাপত্তা সংক্রান্ত নির্দেশিকা:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NatokGold
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "🔒 অ্যাকাউন্ট নিরাপত্তার জন্য ইমেইল ও মোবাইল নম্বর লক করা হয়েছে। নম্বর বা ইমেইল পরিবর্তন করতে কাস্টমার কেয়ার হেল্পলাইনে কথা বলুন।",
                                fontSize = 10.sp,
                                color = Color.White,
                                lineHeight = 14.sp
                            )
                        }
                    }

                    // Editable Name Input
                    OutlinedTextField(
                        value = editNameInput,
                        onValueChange = { editNameInput = it },
                        label = { Text("পুরো নাম") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = TextSecondary) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "জেন্ডার নির্বাচন:",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val isBoy = editGenderInput.equals("Boy", ignoreCase = true)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isBoy) Color(0xFF1565C0) else SurfaceCard)
                                .border(1.dp, if (isBoy) Color(0xFF64B5F6) else BorderDark, RoundedCornerShape(8.dp))
                                .clickable { editGenderInput = "Boy" },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "👦 ছেলে (Boy)",
                                fontSize = 12.sp,
                                fontWeight = if (isBoy) FontWeight.Bold else FontWeight.Normal,
                                color = Color.White
                            )
                        }

                        val isGirl = editGenderInput.equals("Girl", ignoreCase = true)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isGirl) Color(0xFFC2185B) else SurfaceCard)
                                .border(1.dp, if (isGirl) Color(0xFFF48FB1) else BorderDark, RoundedCornerShape(8.dp))
                                .clickable { editGenderInput = "Girl" },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "👧 মেয়ে (Girl)",
                                fontSize = 12.sp,
                                fontWeight = if (isGirl) FontWeight.Bold else FontWeight.Normal,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Editable Birthday Input
                    OutlinedTextField(
                        value = editBirthdayInput,
                        onValueChange = { editBirthdayInput = it },
                        label = { Text("জন্মতারিখ (e.g. 15-08-2002)") },
                        leadingIcon = { Icon(Icons.Default.Cake, contentDescription = null, tint = NatokGold) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Locked Phone Number Input (Read-Only)
                    OutlinedTextField(
                        value = user.phone,
                        onValueChange = {},
                        enabled = false,
                        readOnly = true,
                        label = { Text("মোবাইল নম্বর 🔒 (Locked)") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = Color.Gray) },
                        trailingIcon = { Icon(Icons.Default.Lock, contentDescription = "Locked", tint = NatokGold, modifier = Modifier.size(16.dp)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledBorderColor = BorderDark,
                            disabledTextColor = Color(0xFFAAAAAA),
                            disabledLabelColor = NatokGold
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Locked Email Input (Read-Only)
                    OutlinedTextField(
                        value = user.email,
                        onValueChange = {},
                        enabled = false,
                        readOnly = true,
                        label = { Text("ইমেইল এড্রেস 🔒 (Locked)") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = Color.Gray) },
                        trailingIcon = { Icon(Icons.Default.Lock, contentDescription = "Locked", tint = NatokGold, modifier = Modifier.size(16.dp)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledBorderColor = BorderDark,
                            disabledTextColor = Color(0xFFAAAAAA),
                            disabledLabelColor = NatokGold
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateProfile(editNameInput, editGenderInput, editBirthdayInput, user.phone)
                        showEditProfileDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NatokRed)
                ) {
                    Text("সেভ করুন")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) {
                    Text("বাতিল")
                }
            }
        )
    }

    // Secret Admin Passcode Dialog
    if (showSecretAdminDialog) {
        AlertDialog(
            onDismissRequest = {
                showSecretAdminDialog = false
                secretPinError = false
            },
            title = {
                Text("গোপন অ্যাডমিন এক্সেস (Secret Admin Access)")
            },
            text = {
                Column {
                    Text(
                        text = "অ্যাডমিন প্যানেলে প্রবেশ করতে গোপন পাসকোড প্রদান করুন:",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = secretPinInput,
                        onValueChange = {
                            secretPinInput = it
                            secretPinError = false
                        },
                        placeholder = { Text("গোপন পাসকোড (e.g. ADMIN123)") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        isError = secretPinError,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (secretPinError) {
                        Text(
                            text = "ভুল পাসকোড! সাধারণ ব্যবহারকারীদের জন্য এই অপশন নয়।",
                            fontSize = 11.sp,
                            color = Color(0xFFFF5252),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val success = onAdminPinSubmit(secretPinInput)
                        if (success) {
                            showSecretAdminDialog = false
                        } else {
                            secretPinError = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NatokRed)
                ) {
                    Text("যাচাই করুন")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSecretAdminDialog = false }) {
                    Text("বাতিল")
                }
            }
        )
    }
}
