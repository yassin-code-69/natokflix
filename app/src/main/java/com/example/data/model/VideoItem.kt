package com.example.data.model

data class VideoItem(
    val id: String = "",
    val title: String = "",
    val channelTitle: String = "",
    val duration: String = "",
    val thumbnailUrl: String = "",
    val youtubeVideoId: String = "",
    val category: String = "",
    val views: String = "",
    val viewCount: Int = 0,
    val rewardPoints: Int = 20,
    val isShort: Boolean = false,
    val cast: String = "Bangla Popular Cast",
    val description: String = "Watch full drama and earn reward points directly to your NatokFlix account.",
    val status: String = "pending"
)
