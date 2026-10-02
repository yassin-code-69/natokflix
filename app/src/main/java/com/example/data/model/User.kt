package com.example.data.model

import androidx.compose.ui.graphics.Color

data class User(
    val id: String = "user_default",
    val name: String = "NatokFlix User",
    val email: String = "",
    val photoUrl: String? = null,
    val gender: String = "Boy", // "Boy" / "Girl" (Male / Female)
    val birthday: String = "15-08-2002",
    val points: Int = 0,
    val balanceBdt: Double = 0.0, // 100 points = 1.0 BDT
    val role: String = "USER", // "USER" or "ADMIN"
    val isLoggedIn: Boolean = false,
    val joinedDate: String = "September 2026",
    val referralCode: String = "ARFLIX77",
    val phone: String = "01788-123456",
    val friendsCount: Int = 0,
    val totalReferrals: Int = 0 // Tracks successful referrals count for 50-referral 100,000 mega bonus
) {
    val levelName: String
        get() = when {
            points >= 50000 -> "VIP Gold Member (Level 4)"
            points >= 15000 -> "Silver Member (Level 3)"
            points >= 5000 -> "Bronze Watcher (Level 2)"
            else -> "Beginner (Level 1)"
        }

    val levelBadge: String
        get() = when {
            points >= 50000 -> "👑 VIP GOLD"
            points >= 15000 -> "🥈 SILVER"
            points >= 5000 -> "🥉 BRONZE"
            else -> "🌱 BEGINNER"
        }

    val levelColor: Color
        get() = when {
            points >= 50000 -> Color(0xFFFFD700)
            points >= 15000 -> Color(0xFFE0E0E0)
            points >= 5000 -> Color(0xFFCD7F32)
            else -> Color(0xFF81C784)
        }
}
