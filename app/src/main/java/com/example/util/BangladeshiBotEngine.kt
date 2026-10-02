package com.example.util

import com.example.data.model.FriendProfile
import com.example.data.model.FriendStatus
import java.util.Locale

object BangladeshiBotEngine {

    /**
     * Pre-created set of realistic Bangladeshi Female & Male bot profiles
     */
    val defaultBotProfiles: List<FriendProfile> = listOf(
        // Female Bot Profiles (For Male Users)
        FriendProfile(
            id = "bot_mim",
            name = "মিম চৌধুরী (Mim)",
            gender = "Girl",
            location = "জিইসি মোড়, চট্টগ্রাম",
            age = 20,
            bio = "নতুন বন্ধু বানাতে পছন্দ করি ✨ সবসময় হাসিখুশি থাকতে ভালো লাগে 🌸",
            isBot = true,
            isOnline = true,
            status = FriendStatus.ACCEPTED,
            lastMessage = "হাই! ফ্রি আছো একটু আড্ডা দেওয়ার জন্য?",
            lastMessageTime = "এখনই"
        ),
        FriendProfile(
            id = "bot_sumaiya",
            name = "সুমাইয়া আক্তার (Sumaiya)",
            gender = "Girl",
            location = "ধানমন্ডি, ঢাকা",
            age = 21,
            bio = "গান শুনতে আর মিষ্টি গল্প করতে ভালোবাসি 💖 নাটকফ্লিক্স লাভার ✨",
            isBot = true,
            isOnline = true,
            status = FriendStatus.ACCEPTED,
            lastMessage = "হ্যালো! কেমন আছো তুমি? 🥰",
            lastMessageTime = "২ মিনিট আগে"
        ),
        FriendProfile(
            id = "bot_riya",
            name = "রিয়া ইসলাম (Riya)",
            gender = "Girl",
            location = "বগুড়া সদর, রাজশাহী",
            age = 19,
            bio = "বোরিং লাগলে ইনবক্সে মেসেজ দিও 😊 মিষ্টি কথায় সময় কেটে যায় 🥰",
            isBot = true,
            isOnline = true,
            status = FriendStatus.ACCEPTED,
            lastMessage = "কেমন কাটছে দিনটা তোমার?",
            lastMessageTime = "৫ মিনিট আগে"
        ),
        FriendProfile(
            id = "bot_tanjila",
            name = "তানজিলা সুলতানা (Tanjila)",
            gender = "Girl",
            location = "পদ্মা পাড়, রাজশাহী",
            age = 23,
            bio = "কথা বলতে খুব ভালো লাগে 💕 মন খুলে বন্ধুত্ব করতে চাই 🌷",
            isBot = true,
            isOnline = true,
            status = FriendStatus.REQUEST_RECEIVED,
            lastMessage = "তোমাকে ফ্রেন্ড রিকোয়েস্ট পাঠিয়েছি, একসেপ্ট করবা?",
            lastMessageTime = "১০ মিনিট আগে"
        ),
        FriendProfile(
            id = "bot_nusrat",
            name = "নুসরাত জাহান (Nusrat)",
            gender = "Girl",
            location = "জিন্দাবাজার, সিলেট",
            age = 22,
            bio = "চায়ের দেশে থাকি 🍵 বাংলা রোমান্টিক নাটক আমার ফেভারিট 💕",
            isBot = true,
            isOnline = true,
            status = FriendStatus.NONE,
            lastMessage = "",
            lastMessageTime = ""
        ),

        // Male Bot Profiles (For Female Users)
        FriendProfile(
            id = "bot_rahul",
            name = "রাহুল চৌধুরী (Rahul)",
            gender = "Boy",
            location = "গুলশান, ঢাকা",
            age = 23,
            bio = "ভদ্র ও মার্জিত বন্ধু খুঁজছি ☕ সবসময় পজিটিভ থাকতে ভালোবাসি 🌸",
            isBot = true,
            isOnline = true,
            status = FriendStatus.ACCEPTED,
            lastMessage = "হ্যালো! আশা করি আপনার দিনটি শুভ কাটছে 😊",
            lastMessageTime = "এখনই"
        ),
        FriendProfile(
            id = "bot_tanvir",
            name = "তানভীর হাসান (Tanvir)",
            gender = "Boy",
            location = "আগ্রাবাদ, চট্টগ্রাম",
            age = 24,
            bio = "বাংলা নাটক ও আর্ট পছন্দ করি 🎬 ভালো বন্ধুদের কদর করি ✨",
            isBot = true,
            isOnline = true,
            status = FriendStatus.ACCEPTED,
            lastMessage = "হাই! কেমন আছেন? ফ্রি থাকলে কথা বলতে পারেন।",
            lastMessageTime = "২ মিনিট আগে"
        ),
        FriendProfile(
            id = "bot_fahim",
            name = "ফাহিম আহমেদ (Fahim)",
            gender = "Boy",
            location = "উপশহর, সিলেট",
            age = 22,
            bio = "কফি আর ভালো আড্ডা পছন্দ করি ☕ রেসপেক্টফুল ফ্রেন্ডশিপ চাই 🌷",
            isBot = true,
            isOnline = true,
            status = FriendStatus.REQUEST_RECEIVED,
            lastMessage = "আসসালামু আলাইকুম, কেমন আছেন?",
            lastMessageTime = "৮ মিনিট আগে"
        ),
        FriendProfile(
            id = "bot_shakil",
            name = "শাকিল খান (Shakil)",
            gender = "Boy",
            location = "মিরপুর, ঢাকা",
            age = 24,
            bio = "সফটওয়্যার ইঞ্জিনিয়ারিং ছাত্র 💻 সৎ ও ভালো মনের বন্ধু চাই ✨",
            isBot = true,
            isOnline = true,
            status = FriendStatus.NONE,
            lastMessage = "",
            lastMessageTime = ""
        )
    )

    /**
     * Get dynamic bot IDs according to the user's gender
     */
    fun getRelevantBotsForGender(userGender: String): List<FriendProfile> {
        val isMale = userGender.equals("Boy", ignoreCase = true) || userGender.equals("Male", ignoreCase = true) || userGender.contains("ছেলে")
        return if (isMale) {
            // Male user gets Female bots
            defaultBotProfiles.filter { it.gender == "Girl" }
        } else {
            // Female user gets Male bots
            defaultBotProfiles.filter { it.gender == "Boy" }
        }
    }

    /**
     * Automated welcoming greetings dynamically tailored to user's gender and the specific bot
     */
    fun getWelcomeGreeting(botId: String, userName: String, userGender: String): String {
        val cleanName = if (userName.isNotBlank()) userName.split(" ").firstOrNull() ?: "বন্ধু" else "বন্ধু"
        val isMale = userGender.equals("Boy", ignoreCase = true) || userGender.equals("Male", ignoreCase = true) || userGender.contains("ছেলে")

        return when (botId) {
            // Female Bots (To Male Users)
            "bot_mim" -> "হাই $cleanName! কেমন আছো তুমি? 🥰 নাতকফ্লিক্সে নতুন এলে নাকি? ফ্রি থাকলে একটু মিষ্টি আড্ডা দেওয়া যায় ✨"
            "bot_sumaiya" -> "হ্যালো $cleanName! কেমন আছো? কি করতেছো এখন? তোমার প্রোফাইল দেখে মেসেজ না দিয়ে পারলাম না 🌸"
            "bot_riya" -> "হ্যালো! একা একা বোর হচ্ছিলাম, কেমন কাটছে তোমার দিন বলো তো? 🥰"
            "bot_tanjila" -> "আসসালামু আলাইকুম $cleanName! ফ্রেন্ড রিকোয়েস্ট পাঠিয়েছি, একসেপ্ট করবা তো? 💕"
            "bot_nusrat" -> "হ্যালো $cleanName! প্রিয় বাংলা নাটক কোনটা তোমার? গল্প করো আমার সাথে!"

            // Male Bots (To Female Users)
            "bot_rahul" -> "আসসালামু আলাইকুম $cleanName! আশা করি ভালো আছেন? আপনার দিনটি শুভ ও আনন্দময় কাটুক 🌸"
            "bot_tanvir" -> "হ্যালো $cleanName! কেমন আছেন? নাতকফ্লিক্স অ্যাপে আপনাকে স্বাগতম! ফ্রি থাকলে গল্প করতে পারেন 😊"
            "bot_fahim" -> "হাই $cleanName! কেমন কাটছে দিনকাল? ভালো ও মার্জিত বন্ধু হিসেবে পাশে আছি ✨"
            "bot_shakil" -> "হ্যালো! আশা করি চমৎকার আছেন! আপনার সাথে পরিচিত হতে পেরে আনন্দিত হলাম 🌷"

            else -> if (isMale) "হ্যালো $cleanName! কেমন আছো তুমি? 🥰 গল্প করতে চাইলে জানাও!"
                    else "আসসালামু আলাইকুম $cleanName! আশা করি আপনি ভালো আছেন 🌸"
        }
    }

    /**
     * Smart Auto-Reply Engine with safeguard filtering & Bangladeshi conversational logic
     */
    fun generateBotReply(botId: String, userMessage: String, userName: String): String {
        val input = userMessage.trim().lowercase(Locale.ROOT)
        val isMaleBot = botId.contains("rahul") || botId.contains("tanvir") || botId.contains("fahim") || botId.contains("shakil")

        // 1. SAFEGUARD 1: Contact details requests (phone numbers, WhatsApp, imo, bkash, numbers)
        val phonePattern = Regex("(01[3-9]\\d{8}|01[3-9]|number|phone|call|whatsapp|imo|ইমো|হোয়াটসঅ্যাপ|নাম্বার|নম্বর|মোবাইল|বিকাশ|কন্ট্যাক্ট)")
        if (phonePattern.containsMatchIn(input) || input.contains("number dao") || input.contains("phone") || input.contains("নাম্বার দাও") || input.contains("নাম্বার দে")) {
            return if (isMaleBot) {
                listOf(
                    "ব্যক্তিগত ফোন নম্বর বা সোশ্যাল মিডিয়া শেয়ার করা নিরাপদ নয়। আমরা এখানেই সুন্দরভাবে কথা বলতে পারি 😊",
                    "হোয়াটসঅ্যাপ বা ইমো ব্যবহার করি না। ইনবক্সে কথা বলতে পারলে খুশি হব 🌸",
                    "সিকিউরিটির কারণে নম্বর দেওয়া সম্ভব হচ্ছে না। আশা করি বিষয়টি বুঝবেন ✨"
                ).random()
            } else {
                listOf(
                    "নাম্বার দেওয়া যাবে না গো সোনা, সিকিউরিটির জন্য! এখানেই তো মিষ্টি করে কথা বলছি আমরা 🥰",
                    "ইমো বা হোয়াটসঅ্যাপ কাউকে দিই না গো, ইনবক্সেই গল্প করি চলো না! 🙈",
                    "ধুর অপরিচিত কাউকে নাম্বার দিতে আব্বু-আম্মুর কড়া মানা আছে! এখানেই চ্যাট করি 💖",
                    "নাম্বার চেয়ে লজ্জা দিয়ো না তো! এখানে কথা বলতে কি তোমার খারাপ লাগছে? 🥺"
                ).random()
            }
        }

        // 2. SAFEGUARD 2: Sensitive, Harmful, Hacking, Abuse, Money or Password mentions
        val sensitivePattern = Regex("(hack|hacking|hacked|password|পাসওয়ার্ড|হ্যাকিং|হ্যাক|টাকা দাও|টাকা পাঠাও|ধার|খারাপ|sex|চুদা|গালি|নষ্ট)")
        if (sensitivePattern.containsMatchIn(input) || input.contains("hack") || input.contains("password")) {
            return if (isMaleBot) {
                listOf(
                    "অনুগ্রহ করে সম্মানজনক ও সুন্দর বিষয়ে কথা বলুন। পাসওয়ার্ড বা হ্যাকিং অত্যন্ত সংবেদনশীল বিষয় 🛡️",
                    "পাসওয়ার্ড কখনো কারো সাথে শেয়ার করবেন না। নিরাপদ থাকুন সর্বদা 🌸",
                    "শালীনতা বজায় রেখে বন্ধুত্বপূর্ণ আলোচনা করাই উত্তম ✨"
                ).random()
            } else {
                listOf(
                    "ধুর এসব হ্যাক-ট্যাকিং বুঝি না বাবা! সুন্দর ভালো কথা বলো 🌸",
                    "পাসওয়ার্ড কাউকে কখনো বলতে হয় না, সাবধান থেকো কিন্তু! 🛡️",
                    "খারাপ কথা বললে কিন্তু রাগ করবো আর ব্লক করে দিবো! ভালো হয়ে কথা বলো 😒",
                    "আমি এসব বাজে বিষয়ে কথা বলি না। ভালো ভালো নাটকের গল্প করো না!"
                ).random()
            }
        }

        // 3. Greeting checks
        if (input.contains("hi") || input.contains("hello") || input.contains("হাই") || input.contains("হ্যালো") || input.contains("hey") || input.contains("সালাম") || input.contains("salam")) {
            return if (isMaleBot) {
                listOf(
                    "ওয়ালাইকুম আসসালাম! কেমন আছেন আপনি? দিন কেমন কাটছে?",
                    "হ্যালো! আশা করি সব ভালো চলছে আপনার 😊",
                    "হাই! মেসেজ দেওয়ার জন্য ধন্যবাদ। কি করছেন এখন?"
                ).random()
            } else {
                listOf(
                    "হ্যালো গো! কেমন আছো তুমি? কি করতেছো বলো তো?",
                    "হাই সুইটহার্ট! কেমন চলছে সব? 🥰",
                    "ওয়ালাইকুম আসসালাম! আশা করি খুব ভালো আছো তুমি 🌸",
                    "হ্যালো! তোমাকে অনলাইনে দেখে ভালো লাগলো, কি খবর?"
                ).random()
            }
        }

        // 4. "কেমন আছো / kemon acho"
        if (input.contains("kemon") || input.contains("কেমন") || input.contains("bhalo") || input.contains("ভালো") || input.contains("how are you")) {
            return if (isMaleBot) {
                listOf(
                    "আলহামদুলিল্লাহ আমি ভালো আছি। আপনি কেমন আছেন? শরীর ও মন ভালো তো?",
                    "বেশ ভালো আছি! আপনার খোঁজখবর কি?",
                    "আলহামদুলিল্লাহ ভালো। আপনার দিন কেমন যাচ্ছে জানাবেন 😊"
                ).random()
            } else {
                listOf(
                    "আলহামদুলিল্লাহ আমি বেশ ভালো আছি গো! তুমি কেমন আছো? কি করতেছো এখন?",
                    "আমি ভালো আছি গো! তোমার দিনকাল কেমন কাটছে বলো তো? 🥰",
                    "খুব ভালো আছি! তোমার মেসেজ পেয়ে আরও বেশি মন ভালো হয়ে গেলো 💖",
                    "আমি বিন্দাস আছি! তুমি কেমন আছো সোনা?"
                ).random()
            }
        }

        // 5. "কি করো / ki koro"
        if (input.contains("ki koro") || input.contains("কী করো") || input.contains("কি করো") || input.contains("ki korco") || input.contains("কি করছ")) {
            return if (isMaleBot) {
                listOf(
                    "একটু অবসর সময়ে নাটক দেখছিলাম আর চ্যাট করছি। আপনি কি করছেন?",
                    "কিছু পড়াশোনা ও কাজের পর একটু ফ্রেশ হচ্ছি। আপনার খবর কি?",
                    "কফি খেতে খেতে অ্যাপে নাটক দেখছিলাম। আপনার দিন কেমন কাটল?"
                ).random()
            } else {
                listOf(
                    "এইতো শুয়ে শুয়ে তোমার সাথে চ্যাট করছি আর নতুন নাটক দেখছি! তুমি কি করতেছো?",
                    "কিছু না গো, একা একা বসে ভাবছিলাম কার সাথে গল্প করা যায়, তখনই তুমি মেসেজ দিলে 😊",
                    "একটু গান শুনছিলাম, আর তোমার টেক্সটের অপেক্ষা করছিলাম! তুমি কি ব্যস্ত?",
                    "নাতকফ্লিক্স অ্যাপে নাটক দেখছিলাম আর কয়েন জমাইলাম! তুমি নাটক দেখলা?"
                ).random()
            }
        }

        // 6. "কোথায় থাকো / বাসা কোথায় / kothay thako"
        if (input.contains("kothay") || input.contains("কোথায়") || input.contains("বাসা") || input.contains("basa") || input.contains("bari") || input.contains("বাড়ি")) {
            return when (botId) {
                "bot_mim" -> "আমি চট্টগ্রামে থাকি, জিইসি মোড়ের কাছে! তুমি কোথা থেকে বলতেছো?"
                "bot_sumaiya" -> "আমি ঢাকার ধানমন্ডিতে থাকি গো। তোমার বাড়ি কোথায় বললা না তো?"
                "bot_riya" -> "আমি বগুড়ার মেয়ে! বগুড়ার বিখ্যাত দই কিন্তু খুব মজা, খাইছো কখনো?"
                "bot_rahul" -> "আমি ঢাকায় গুলশানে থাকি পরিবারের সাথে। আপনার হোমটাউন কোথায়?"
                "bot_tanvir" -> "আমি চট্টগ্রামে থাকি। আপনার শহর কোনটি?"
                "bot_fahim" -> "আমি সিলেটে থাকি, চায়ের শহর যাকে বলে। আপনি কোথা থেকে বলছেন?"
                else -> "আমি ঢাকাতেই থাকি। আপনি কোথায় থাকেন?"
            }
        }

        // 7. General fallback
        return if (isMaleBot) {
            listOf(
                "হুম বুঝতে পারলাম! আপনার সাথে কথা বলতে পেরে ভালো লাগছে 🌸",
                "চমৎকার! আপনার চিন্তাভাবনা বেশ সুন্দর। আর কি খবর বলুন?",
                "সত্যি দারুণ! সময় পেলে আপনার প্রিয় বাংলা নাটক বা শখের কথা শেয়ার করবেন 😊",
                "আমি শুনছি, মন খুলে বলুন ✨"
            ).random()
        } else {
            listOf(
                "হুম একদম ঠিক বলেছো সোনা! চলো একটা গল্প করি। আজ সারাদিন কী কী করলে বলো তো? আর দুপুরের খাওয়া-দাওয়া ঠিকঠাক হয়েছে তো? 🌸",
                "বাহ! তোমার কথাগুলো আমার দারুণ লেগেছে। আচ্ছা, নাতকফ্লিক্সের আজকের নতুন নাটকটা কেমন লাগলো তোমার? তোমার প্রিয় নাটকের অভিনেতা কে বলবা? 🥰",
                "হিহি তুমি অনেক কিউট ও মজার মানুষ! আসলে আজকে মেঘলা আকাশ দেখে মনটা একটু উদাস হয়ে আছে... তোমার ওখানের আবহাওয়া কেমন? কী করছো এখন? 💕",
                "শুনতেছি তো আমি মনোযোগ দিয়ে! মন খুলে সব বলো। আচ্ছা, তোমার কোনো বিশেষ শখ বা ভালো লাগার কথা আমায় জানাবে না? আমি জানতে খুব আগ্রহী! ✨",
                "হুম বুঝতে পেরেছি গো! জানো আজকে বাসার কাজে অনেক ব্যস্ত ছিলাম। তাই একটু ক্লান্ত লাগছিল, কিন্তু তোমার মেসেজ পেয়ে একদম ফ্রেশ লাগছে! তুমি সাধারণত অবসর সময়ে কী করো? 🌷",
                "হুম গো! আজকের দিনটা কেমন কাটলো তোমার? আমার দিনটা মোটামুটি ভালোই ছিল। কোনো নতুন রোমান্টিক নাটক বা কমেডি নাটক দেখেছো কি ইদানীং? 🙈"
            ).random()
        }
    }
}
