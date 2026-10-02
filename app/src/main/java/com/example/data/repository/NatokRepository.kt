package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.ChatDao
import com.example.data.local.ChatMessageEntity
import com.example.data.local.FriendDao
import com.example.data.local.FriendEntity
import com.example.data.local.UserDao
import com.example.data.local.UserEntity
import com.example.data.local.WithdrawalDao
import com.example.data.local.WithdrawalEntity
import com.example.data.model.ChatMessage
import com.example.data.model.EconomyConfig
import com.example.data.model.FriendProfile
import com.example.data.model.FriendStatus
import com.example.data.model.User
import com.example.data.model.VideoItem
import com.example.data.model.Withdrawal
import com.example.util.BangladeshiBotEngine
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.UUID

class NatokRepository(private val database: AppDatabase) {
    private val userDao: UserDao = database.userDao()
    private val withdrawalDao: WithdrawalDao = database.withdrawalDao()
    private val chatDao: ChatDao = database.chatDao()
    private val friendDao: FriendDao = database.friendDao()
    private val db by lazy { com.google.firebase.firestore.FirebaseFirestore.getInstance() }

    fun listenToEconomyConfig(): Flow<EconomyConfig> = callbackFlow {
        val docRef = db.collection("app_config").document("economy_settings")
        val listener = docRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(EconomyConfig())
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                val config = snapshot.toObject(EconomyConfig::class.java) ?: EconomyConfig()
                trySend(config)
            } else {
                trySend(EconomyConfig())
            }
        }
        awaitClose { listener.remove() }
    }

    suspend fun saveEconomyConfig(config: EconomyConfig) {
        try {
            db.collection("app_config").document("economy_settings")
                .set(config, com.google.firebase.firestore.SetOptions.merge()).await()
        } catch (_: Exception) {}
    }

    suspend fun syncUserToFirestore(user: User) {
        try {
            val userMap = mapOf(
                "id" to user.id,
                "name" to user.name,
                "email" to user.email,
                "phone" to user.phone,
                "points" to user.points,
                "balanceBdt" to user.balanceBdt,
                "totalReferrals" to user.totalReferrals,
                "referralCode" to user.referralCode,
                "gender" to user.gender,
                "birthday" to user.birthday,
                "role" to user.role,
                "lastActive" to System.currentTimeMillis()
            )
            db.collection("users").document(user.id).set(userMap, com.google.firebase.firestore.SetOptions.merge()).await()
            if (user.referralCode.isNotBlank()) {
                db.collection("referral_codes").document(user.referralCode.uppercase())
                    .set(
                        mapOf(
                            "userId" to user.id,
                            "userName" to user.name,
                            "phone" to user.phone,
                            "email" to user.email
                        ),
                        com.google.firebase.firestore.SetOptions.merge()
                    ).await()
            }
        } catch (_: Exception) {}
    }

    private fun generateUniqueReferralCode(name: String, phone: String, email: String, id: String): String {
        val cleanPhoneDigits = phone.filter { it.isDigit() }
        val last4 = if (cleanPhoneDigits.length >= 4) cleanPhoneDigits.takeLast(4) else (Math.abs(id.hashCode()) % 9000 + 1000).toString()
        val prefix = if (email.isNotBlank() && email.contains("@")) {
            email.substringBefore("@").take(4).uppercase().filter { it.isLetter() }.ifBlank { "NTK" }
        } else if (name.isNotBlank()) {
            name.take(4).uppercase().filter { it.isLetter() }.ifBlank { "NTK" }
        } else {
            "NTK"
        }
        return "$prefix$last4"
    }

    suspend fun isDeviceRegistered(deviceId: String): Boolean {
        return try {
            val doc = db.collection("registered_devices").document(deviceId).get().await()
            doc.exists()
        } catch (e: Exception) {
            false
        }
    }

    suspend fun registerDevice(deviceId: String) {
        db.collection("registered_devices").document(deviceId).set(mapOf("lastBonusClaimed" to System.currentTimeMillis())).await()
    }
    
    suspend fun addVideo(video: VideoItem) {
        db.collection("videos").document(video.id).set(video).await()
    }

    suspend fun updateVideo(video: VideoItem) {
        db.collection("videos").document(video.id).set(video).await()
    }

    suspend fun deleteVideo(videoId: String) {
        db.collection("videos").document(videoId).delete().await()
    }

    suspend fun incrementViewCount(videoId: String) {
        val ref = db.collection("videos").document(videoId)
        ref.update("viewCount", com.google.firebase.firestore.FieldValue.increment(1)).await()
    }

    fun listenToVideos(): Flow<List<VideoItem>> = callbackFlow {
        val query = db.collection("videos").whereEqualTo("status", "approved")
        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(emptyList())
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val videos = snapshot.toObjects(VideoItem::class.java)
                trySend(videos)
            } else {
                trySend(emptyList())
            }
        }
        awaitClose { listener.remove() }
    }

    suspend fun getDailyAdCount(userId: String): Int {
        return try {
            val doc = db.collection("daily_stats").document(userId).get().await()
            (doc.getLong("adsWatched") ?: 0).toInt()
        } catch (e: Exception) { 0 }
    }

    suspend fun incrementDailyAdCount(userId: String) {
        val ref = db.collection("daily_stats").document(userId)
        ref.set(mapOf("adsWatched" to com.google.firebase.firestore.FieldValue.increment(1)), com.google.firebase.firestore.SetOptions.merge()).await()
    }

    suspend fun fetchVideos(): List<VideoItem> {
        val snapshot = db.collection("videos").orderBy("viewCount", com.google.firebase.firestore.Query.Direction.DESCENDING).get().await()
        return snapshot.toObjects(VideoItem::class.java)
    }

    val userFlow: Flow<User> = userDao.getUserFlow().map { entity ->
        if (entity != null) {
            val refCode = generateUniqueReferralCode(entity.name, entity.phone, entity.email, entity.id)
            User(
                id = entity.id,
                name = entity.name,
                email = entity.email,
                photoUrl = null,
                gender = entity.gender,
                birthday = entity.birthday,
                points = entity.points,
                balanceBdt = entity.balanceBdt,
                role = entity.role,
                isLoggedIn = entity.isLoggedIn,
                joinedDate = entity.joinedDate,
                referralCode = refCode,
                phone = entity.phone,
                friendsCount = entity.friendsCount,
                totalReferrals = entity.totalReferrals
            )
        } else {
            // Default initial user
            User()
        }
    }

    val withdrawalsFlow: Flow<List<Withdrawal>> = withdrawalDao.getUserWithdrawalsFlow().map { list ->
        list.map { entity ->
            Withdrawal(
                id = entity.id,
                userId = entity.userId,
                method = entity.method,
                operator = entity.operator,
                accountNumber = entity.accountNumber,
                amountBdt = entity.amountBdt,
                pointsDeducted = entity.pointsDeducted,
                status = entity.status,
                timestamp = entity.timestamp
            )
        }
    }

    val allWithdrawalsFlow: Flow<List<Withdrawal>> = withdrawalDao.getAllWithdrawalsFlow().map { list ->
        list.map { entity ->
            Withdrawal(
                id = entity.id,
                userId = entity.userId,
                method = entity.method,
                operator = entity.operator,
                accountNumber = entity.accountNumber,
                amountBdt = entity.amountBdt,
                pointsDeducted = entity.pointsDeducted,
                status = entity.status,
                timestamp = entity.timestamp
            )
        }
    }

    val allFriendsFlow: Flow<List<FriendProfile>> = friendDao.getAllFriendsFlow().map { list ->
        if (list.isEmpty()) {
            BangladeshiBotEngine.defaultBotProfiles
        } else {
            list.map { entity ->
                FriendProfile(
                    id = entity.id,
                    name = entity.name,
                    gender = entity.gender,
                    location = entity.location,
                    age = entity.age,
                    bio = entity.bio,
                    isBot = entity.isBot,
                    isOnline = entity.isOnline,
                    isBlocked = entity.isBlocked,
                    status = try {
                        FriendStatus.valueOf(entity.status)
                    } catch (_: Exception) {
                        FriendStatus.NONE
                    },
                    lastMessage = entity.lastMessage,
                    lastMessageTime = entity.lastMessageTime,
                    unreadCount = entity.unreadCount
                )
            }
        }
    }

    val allMessagesFlow: Flow<List<ChatMessage>> = chatDao.getAllMessagesFlow().map { list ->
        list.map { entity ->
            ChatMessage(
                id = entity.id,
                conversationUserId = entity.conversationUserId,
                senderName = entity.senderName,
                text = entity.text,
                timestamp = entity.timestamp,
                isFromMe = entity.isFromMe,
                isRead = entity.isRead
            )
        }
    }

    suspend fun getUserAccount(usernameOrEmail: String): com.example.data.local.UserAccountEntity? {
        return userDao.getUserAccount(usernameOrEmail)
    }

    suspend fun createUserAccount(
        usernameOrEmail: String,
        pass: String,
        name: String,
        phone: String,
        gender: String,
        birthday: String
    ) {
        userDao.insertUserAccount(
            com.example.data.local.UserAccountEntity(
                usernameOrEmail = usernameOrEmail,
                password = pass,
                name = name,
                phone = phone,
                gender = gender,
                birthday = birthday
            )
        )
    }

    suspend fun ensureInitialUser() {
        val existing = userDao.getUser()
        if (existing == null) {
            userDao.insertUser(
                UserEntity(
                    id = "user_default",
                    name = "NatokFlix User",
                    email = "",
                    photoUrl = null,
                    gender = "Boy",
                    birthday = "15-08-2002",
                    points = 0,
                    balanceBdt = 0.0, // 100 points = 1 BDT
                    role = "USER",
                    isLoggedIn = false, // Clean production launch starts logged out
                    joinedDate = "September 2026",
                    phone = "01788-123456",
                    friendsCount = 0,
                    totalReferrals = 0
                )
            )
        }
        // Zero-initial friends, chats, accounts, and referrals are maintained by not prepopulating anything for production!
    }

    suspend fun clearAllTablesAndReset() {
        // Full database flush / Zero-out all test data
        userDao.clearAllAccounts()
        userDao.clearAllUsers()
        chatDao.clearAllMessages()
        friendDao.clearAllFriends()
        withdrawalDao.clearAll()

        // Insert fresh clean default production state user (logged out, 0 balance/referrals/friends)
        userDao.insertUser(
            UserEntity(
                id = "user_default",
                name = "AR Akhirul Islam",
                email = "arakhirulislam81@gmail.com",
                photoUrl = null,
                gender = "Boy",
                birthday = "15-08-2002",
                points = 0,
                balanceBdt = 0.0,
                role = "USER",
                isLoggedIn = false,
                joinedDate = "September 2026",
                phone = "01788-123456",
                friendsCount = 0,
                totalReferrals = 0
            )
        )
    }

    suspend fun loginUser(name: String, email: String, gender: String = "Boy", birthday: String = "15-08-2002", phone: String = "01788-123456", rememberMe: Boolean = true) {
        val account = userDao.getUserAccount(email)
        val currentPoints = account?.points ?: 0
        val currentBalance = account?.balanceBdt ?: 0.0
        val friendsCount = 0
        val totalReferrals = account?.totalReferrals ?: 0
        userDao.insertUser(
            UserEntity(
                id = "user_default",
                name = name.ifBlank { "NatokFlix User" },
                email = email.ifBlank { "user@natokflix.com" },
                photoUrl = null,
                gender = gender.ifBlank { "Boy" },
                birthday = birthday.ifBlank { "15-08-2002" },
                points = currentPoints,
                balanceBdt = currentBalance,
                role = "USER",
                isLoggedIn = rememberMe,
                joinedDate = "September 2026",
                phone = phone.ifBlank { "01788-123456" },
                friendsCount = friendsCount,
                totalReferrals = totalReferrals
            )
        )
    }

    /**
     * Trigger Gender-Based Welcoming Bot Greetings
     */
    suspend fun triggerGenderBasedBotGreeting(userName: String, gender: String) {
        val relevantBots = BangladeshiBotEngine.getRelevantBotsForGender(gender)
        val selectedBots = relevantBots.take(2)
        selectedBots.forEachIndexed { index, bot ->
            val greeting = BangladeshiBotEngine.getWelcomeGreeting(bot.id, userName, gender)
            chatDao.insertMessage(
                ChatMessageEntity(
                    id = "msg_greet_" + UUID.randomUUID().toString(),
                    conversationUserId = bot.id,
                    senderName = bot.name,
                    text = greeting,
                    timestamp = System.currentTimeMillis() - ((2 - index) * 10000L),
                    isFromMe = false
                )
            )
            friendDao.updateLastMessage(bot.id, greeting, "এখনই")
        }
    }

    suspend fun updateProfileDetails(name: String, gender: String, birthday: String, phone: String) {
        userDao.updateProfileDetails("user_default", name, gender, birthday, phone)
    }

    suspend fun updateAccountPassword(usernameOrEmail: String, newPassword: String): Boolean {
        val account = userDao.getUserAccount(usernameOrEmail)
        return if (account != null) {
            userDao.updateAccountPassword(usernameOrEmail, newPassword)
            true
        } else {
            false
        }
    }

    suspend fun updateFriendStatus(friendId: String, status: FriendStatus) {
        friendDao.updateFriendStatus(friendId, status.name)
        val allAccepted = friendDao.getAllFriendsFlow().firstOrNull()?.count { it.status == FriendStatus.ACCEPTED.name } ?: 0
        userDao.updateFriendsCount("user_default", allAccepted)
    }

    suspend fun updateFriendBlockStatus(friendId: String, isBlocked: Boolean) {
        friendDao.updateFriendBlockStatus(friendId, isBlocked)
    }

    suspend fun isFriendBlocked(friendId: String): Boolean {
        return friendDao.getFriendById(friendId)?.isBlocked ?: false
    }

    /**
     * Referral System: Link Referrer as Friend and award bonuses + milestone tracking
     */
    suspend fun processReferralSignUp(
        refCode: String,
        newUserName: String,
        newUserGender: String,
        referrerBonus: Int = 200,
        refereeBonus: Int = 100,
        coinsPerBdtRate: Int = 10
    ): String {
        val trimmedCode = refCode.trim().uppercase()
        if (trimmedCode.isBlank()) return ""

        val user = userDao.getUser() ?: return ""
        val rate = if (coinsPerBdtRate > 0) coinsPerBdtRate else 10

        // 1. Give Referee bonus
        val newPoints = user.points + refereeBonus
        val newBalance = newPoints / rate.toDouble()
        val newReferralCount = user.totalReferrals + 1

        userDao.updateBalance("user_default", newPoints, newBalance)
        userDao.updateTotalReferrals("user_default", newReferralCount)

        // 2. Automatically link referrer as a Friend (status: ACCEPTED)
        val referrerId = "referrer_" + trimmedCode.lowercase()
        val referrerName = "রেফারার ($trimmedCode)"
        val referrerFriend = FriendEntity(
            id = referrerId,
            name = referrerName,
            gender = if (newUserGender == "Boy") "Girl" else "Boy",
            location = "ঢাকা",
            age = 22,
            bio = "আপনার রেফারেল পার্টনার ও শুভাকাঙ্ক্ষী ✨",
            isBot = false,
            isOnline = true,
            isBlocked = false,
            status = FriendStatus.ACCEPTED.name,
            lastMessage = "রেফার কোড ব্যবহারের জন্য ধন্যবাদ! আমরা এখন কানেক্টেড ফ্রেন্ড 🎉",
            lastMessageTime = "এখনই",
            unreadCount = 1
        )
        friendDao.insertOrUpdateFriend(referrerFriend)

        // Insert referral greeting message
        val refereeBdt = refereeBonus / rate.toDouble()
        val referrerBdt = referrerBonus / rate.toDouble()
        chatDao.insertMessage(
            ChatMessageEntity(
                id = "msg_ref_" + UUID.randomUUID().toString(),
                conversationUserId = referrerId,
                senderName = referrerName,
                text = "অভিনন্দন! আপনি '$trimmedCode' কোড দিয়ে যুক্ত হয়েছেন। আপনি পেয়েছেন $refereeBonus ফ্রি কয়েন (৳${String.format(java.util.Locale.US, "%.1f", refereeBdt)}) এবং রেফারার পেয়েছেন $referrerBonus কয়েন (৳${String.format(java.util.Locale.US, "%.1f", referrerBdt)})! 🎉",
                timestamp = System.currentTimeMillis(),
                isFromMe = false
            )
        )

        // Update friends count
        val allAccepted = friendDao.getAllFriendsFlow().firstOrNull()?.count { it.status == FriendStatus.ACCEPTED.name } ?: 0
        userDao.updateFriendsCount("user_default", allAccepted)

        // 3. Credit Referrer in Firestore (Remote Wallet Credit)
        try {
            val refDoc = db.collection("referral_codes").document(trimmedCode).get().await()
            if (refDoc.exists()) {
                val refUserId = refDoc.getString("userId")
                if (!refUserId.isNullOrBlank()) {
                    db.collection("users").document(refUserId).update(
                        "points", com.google.firebase.firestore.FieldValue.increment(referrerBonus.toLong()),
                        "totalReferrals", com.google.firebase.firestore.FieldValue.increment(1)
                    ).await()
                }
            }
        } catch (_: Exception) {}

        // 4. Milestone Check: 50 Referrals -> 5,000 Coins Mega Bonus!
        if (newReferralCount >= 50) {
            val megaBonus = 5000
            val milestonePoints = newPoints + megaBonus
            userDao.updateBalance("user_default", milestonePoints, milestonePoints / rate.toDouble())
            return "🎉 মেগা জ্যাকপট আনলক! ৫০ জন সফল রেফারেল সম্পন্ন করায় ৫,০০০ কয়েন বোনাস দেওয়া হয়েছে!"
        }

        return "🎉 রেফারেল সফল! আপনি পেয়েছেন $refereeBonus কয়েন (৳${String.format(java.util.Locale.US, "%.1f", refereeBdt)}) এবং রেফারার পেয়েছেন $referrerBonus কয়েন (৳${String.format(java.util.Locale.US, "%.1f", referrerBdt)})। আপনার ফ্রেন্ডলিস্টে রেফারার যুক্ত হয়েছে!"
    }

    suspend fun saveChatMessage(conversationUserId: String, senderName: String, text: String, isFromMe: Boolean) {
        val msg = ChatMessageEntity(
            id = "msg_" + UUID.randomUUID().toString(),
            conversationUserId = conversationUserId,
            senderName = senderName,
            text = text,
            timestamp = System.currentTimeMillis(),
            isFromMe = isFromMe
        )
        chatDao.insertMessage(msg)
        friendDao.updateLastMessage(conversationUserId, text, "এখনই")
    }

    suspend fun logoutUser() {
        userDao.setLoginState("user_default", false)
    }

    suspend fun addPoints(amount: Int, coinsPerBdtRate: Int = 10) {
        val user = userDao.getUser() ?: return
        val newPoints = user.points + amount
        val rate = if (coinsPerBdtRate > 0) coinsPerBdtRate else 10
        val newBalance = newPoints / rate.toDouble()
        userDao.updateBalance("user_default", newPoints, newBalance)
    }

    /**
     * Submit Withdrawal with strict 11-digit phone number, operating hours (10 AM - 8 PM), and dynamic balance validation
     */
    suspend fun submitWithdrawal(
        method: String,
        operator: String?,
        accountNumber: String,
        amountBdt: Double,
        minWithdrawalBdt: Double = 300.0,
        coinsPerBdtRate: Int = 10
    ): Pair<Boolean, String> {
        val user = userDao.getUser() ?: return Pair(false, "ইউজার তথ্য পাওয়া যায়নি!")
        val rate = if (coinsPerBdtRate > 0) coinsPerBdtRate else 10

        // 0. Withdrawal Operating Hours Check: 10:00 AM to 8:00 PM (10:00 to 20:00)
        val dhakaCal = java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("Asia/Dhaka"))
        val currentHour = dhakaCal.get(java.util.Calendar.HOUR_OF_DAY)
        val isWindowOpen = currentHour in 10 until 20
        if (!isWindowOpen) {
            return Pair(false, "উইথড্র সিস্টেম এখন বন্ধ রয়েছে! প্রতিদিন সকাল ১০:০০ টা থেকে রাত ০৮:০০ টা পর্যন্ত উইথড্র সার্ভিস সক্রিয় থাকে।")
        }

        // 1. Phone number validation (11 digits required, e.g. 017xxxxxxxx)
        val cleanPhone = accountNumber.trim().replace("-", "").replace(" ", "")
        val phoneRegex = Regex("^01[3-9]\\d{8}$")
        if (!cleanPhone.matches(phoneRegex) && cleanPhone.length != 11) {
            return Pair(false, "ভুল নম্বর! অনুগ্রহ করে সঠিক ১১ ডিজিটের মোবাইল নম্বর দিন (যেমন: 017xxxxxxxx)।")
        }

        // 2. Minimum amount & Balance validation (Dynamic: amountBdt * rate)
        val requiredPoints = (amountBdt * rate).toInt()
        if (user.points < requiredPoints) {
            return Pair(false, "পর্যাপ্ত কয়েন ব্যালেন্স নেই! প্রয়োজন $requiredPoints কয়েন (৳${amountBdt.toInt()} BDT)।")
        }
        if (amountBdt < minWithdrawalBdt) {
            return Pair(false, "সর্বনিম্ন উত্তোলনের পরিমাণ ${minWithdrawalBdt.toInt()} টাকা (${(minWithdrawalBdt * rate).toInt()} কয়েন)।")
        }

        // 3. Deduct points and update balance
        val newPoints = user.points - requiredPoints
        val newBalance = newPoints / rate.toDouble()
        userDao.updateBalance("user_default", newPoints, newBalance)

        // 4. Insert transaction record
        val withdrawal = WithdrawalEntity(
            id = "WD-" + UUID.randomUUID().toString().take(8).uppercase(),
            userId = user.id,
            method = method,
            operator = if (method.contains("Recharge", ignoreCase = true) || method.contains("রিচার্জ")) operator else null,
            accountNumber = cleanPhone,
            amountBdt = amountBdt,
            pointsDeducted = requiredPoints,
            status = "Pending",
            timestamp = System.currentTimeMillis()
        )
        withdrawalDao.insertWithdrawal(withdrawal)

        val successMsg = if (operator != null && method.contains("Recharge", ignoreCase = true)) {
            "৳${amountBdt.toInt()} টাকা $operator (${cleanPhone}) নম্বরে রিচার্জ অনুরোধ সফলভাবে পাঠানো হয়েছে!"
        } else {
            "৳${amountBdt.toInt()} টাকা $method (${cleanPhone}) নম্বরে উত্তোলনের অনুরোধ সফলভাবে পাঠানো হয়েছে!"
        }
        return Pair(true, successMsg)
    }

    suspend fun updateWithdrawalStatus(id: String, status: String) {
        withdrawalDao.updateWithdrawalStatus(id, status)
    }

    fun getSampleNatoks(): List<VideoItem> {
        return listOf(
            VideoItem(
                id = "natok_1",
                title = "হাবুর স্কলারশিপ | Habur Scholarship | Full Eid Natok",
                channelTitle = "Cinemawala Entertainment",
                duration = "44:12",
                thumbnailUrl = "https://images.unsplash.com/photo-1594909122845-11baa439b7bf?w=800&auto=format&fit=crop&q=80",
                youtubeVideoId = "dQw4w9WgXcQ",
                category = "Eid Special",
                views = "2.4M views",
                rewardPoints = 30,
                cast = "Mosharraf Karim, Tanjin Tisha",
                description = "এক সাধারণ গ্রামের ছেলের স্কলারশিপ পাওয়ার মজার ও অনুপ্রেরণামূলক নাটক।"
            ),
            VideoItem(
                id = "natok_2",
                title = "ব্যাচেলর পয়েন্ট সিজন ৫ - স্পেশাল পর্ব",
                channelTitle = "Dhruba TV Natok",
                duration = "36:45",
                thumbnailUrl = "https://images.unsplash.com/photo-1518173946687-a4c8892bbd9f?w=800&auto=format&fit=crop&q=80",
                youtubeVideoId = "k1BneeJTDcU",
                category = "Trending",
                views = "5.8M views",
                rewardPoints = 25,
                cast = "Mishu Sabbir, Polash, Chashi Alam",
                description = "ব্যাচেলরদের জীবনের নিত্যদিনের হাস্যকৌতুক ও আবেগের টানাপোড়েন।"
            ),
            VideoItem(
                id = "natok_3",
                title = "বুকের বাঁ পাশে | Buker Ba Pashe | Romantic Natok",
                channelTitle = "Laser Vision Drama",
                duration = "42:10",
                thumbnailUrl = "https://images.unsplash.com/photo-1516589178581-6cd7833ae3b2?w=800&auto=format&fit=crop&q=80",
                youtubeVideoId = "9bZkp7q19f0",
                category = "Romantic Drama",
                views = "1.9M views",
                rewardPoints = 20,
                cast = "Afran Nisho, Mehazabien Chowdhury",
                description = "ভালোবাসার চিরন্তন অনুভূতি ও বিচ্ছেদের এক অনন্য রোমান্টিক গল্প।"
            ),
            VideoItem(
                id = "natok_4",
                title = "ফিমেল ৪ - পাড়ার ক্যাচাল | Female 4 | Comedy",
                channelTitle = "Club 11 Entertainment",
                duration = "38:20",
                thumbnailUrl = "https://images.unsplash.com/photo-1485846234645-a62644f84728?w=800&auto=format&fit=crop&q=80",
                youtubeVideoId = "kJQP7kiw5Fk",
                category = "Comedy Drama",
                views = "3.2M views",
                rewardPoints = 25,
                cast = "Mishu Sabbir, Ziaul Hoque Polash",
                description = "মহল্লার মজার সব ঘটনা ও রম্য কাণ্ডকারখানা নিয়ে তুমুল জনপ্রিয় কমেডি।"
            ),
            VideoItem(
                id = "natok_5",
                title = "বড় ছেলে | Boro Chele | Emotional Masterpiece",
                channelTitle = "CD Choice Natok",
                duration = "52:14",
                thumbnailUrl = "https://images.unsplash.com/photo-1536440136628-849c177e76a1?w=800&auto=format&fit=crop&q=80",
                youtubeVideoId = "3JZ_D3ELwOQ",
                category = "Emotional Drama",
                views = "12M views",
                rewardPoints = 35,
                cast = "Ziaul Faruq Apurba, Mehazabien Chowdhury",
                description = "মধ্যবিত্ত পরিবারের বড় ছেলের ত্যাগ ও ভালোবাসার অমর ট্র্যাজেডি।"
            )
        )
    }

    fun getSampleShorts(): List<VideoItem> {
        return listOf(
            VideoItem(
                id = "short_1",
                title = "কাবিলা যখন ডায়ালগ দেয় 😂 | Bachelor Point Fun Scene #shorts",
                channelTitle = "Dhruba TV Shorts",
                duration = "0:45",
                thumbnailUrl = "https://images.unsplash.com/photo-1598899134739-24c46f58b8c0?w=800&auto=format&fit=crop&q=80",
                youtubeVideoId = "shorts_1",
                category = "Funny Shorts",
                views = "890K views",
                rewardPoints = 15,
                cast = "Polash (Kabila)",
                description = "কাবিলার মজার নোয়াখালীয় ভাষার সেরা ডায়ালগ।"
            ),
            VideoItem(
                id = "short_2",
                title = "হাবুর মজার কাণ্ড! 🤣 | Habur Scholarship Short #shorts",
                channelTitle = "Cinemawala Shorts",
                duration = "0:55",
                thumbnailUrl = "https://images.unsplash.com/photo-1518173946687-a4c8892bbd9f?w=800&auto=format&fit=crop&q=80",
                youtubeVideoId = "shorts_2",
                category = "Comedy",
                views = "1.2M views",
                rewardPoints = 15,
                cast = "Mosharraf Karim",
                description = "মোশাররফ করিমের সেরা কমেডি সিন।"
            ),
            VideoItem(
                id = "short_3",
                title = "বুকের বাঁ পাশে - ইমোশনাল ডায়ালগ 💔 #shorts",
                channelTitle = "Laser Vision Shorts",
                duration = "0:40",
                thumbnailUrl = "https://images.unsplash.com/photo-1516589178581-6cd7833ae3b2?w=800&auto=format&fit=crop&q=80",
                youtubeVideoId = "shorts_3",
                category = "Emotional",
                views = "650K views",
                rewardPoints = 15,
                cast = "Afran Nisho",
                description = "আফরান নিশোর অসাধারণ ইমোশনাল সংলাপ।"
            )
        )
    }
}
