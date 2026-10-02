package com.example.data.model

enum class FriendStatus {
    NONE,
    REQUEST_SENT,
    REQUEST_RECEIVED,
    ACCEPTED
}

data class FriendProfile(
    val id: String,
    val name: String,
    val gender: String = "Girl", // "Boy" or "Girl"
    val location: String = "Dhaka",
    val age: Int = 21,
    val bio: String = "",
    val isBot: Boolean = false,
    val isOnline: Boolean = true,
    val isBlocked: Boolean = false, // If true, all automated & manual messages are suppressed
    val status: FriendStatus = FriendStatus.NONE,
    val lastMessage: String = "",
    val lastMessageTime: String = "",
    val unreadCount: Int = 0
)

data class ChatMessage(
    val id: String,
    val conversationUserId: String, // ID of the other user in the chat
    val senderName: String,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isFromMe: Boolean = true,
    val isRead: Boolean = true
)
