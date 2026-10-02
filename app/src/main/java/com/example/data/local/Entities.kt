package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_accounts")
data class UserAccountEntity(
    @PrimaryKey val usernameOrEmail: String,
    val password: String,
    val name: String,
    val phone: String,
    val gender: String,
    val birthday: String,
    val points: Int = 0,
    val balanceBdt: Double = 0.0,
    val totalReferrals: Int = 0
)

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String = "user_default",
    val name: String,
    val email: String,
    val photoUrl: String? = null,
    val gender: String = "Boy", // "Boy" or "Girl"
    val birthday: String = "15-08-2002",
    val points: Int = 0,
    val balanceBdt: Double = 0.0,
    val role: String = "USER",
    val isLoggedIn: Boolean = false,
    val joinedDate: String = "September 2026",
    val phone: String = "01788-123456",
    val friendsCount: Int = 0,
    val totalReferrals: Int = 0
)

@Entity(tableName = "withdrawals")
data class WithdrawalEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val method: String,
    val operator: String? = null,
    val accountNumber: String,
    val amountBdt: Double,
    val pointsDeducted: Int,
    val status: String,
    val timestamp: Long
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey val id: String,
    val conversationUserId: String,
    val senderName: String,
    val text: String,
    val timestamp: Long,
    val isFromMe: Boolean,
    val isRead: Boolean = true
)

@Entity(tableName = "friends")
data class FriendEntity(
    @PrimaryKey val id: String,
    val name: String,
    val gender: String,
    val location: String,
    val age: Int,
    val bio: String,
    val isBot: Boolean,
    val isOnline: Boolean,
    val isBlocked: Boolean = false,
    val status: String, // NONE, REQUEST_SENT, REQUEST_RECEIVED, ACCEPTED
    val lastMessage: String,
    val lastMessageTime: String,
    val unreadCount: Int
)
