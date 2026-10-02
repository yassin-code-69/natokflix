package com.example.data.model

enum class TaskType {
    DAILY_CHECK_IN,
    SPIN_WHEEL,
    WATCH_AD,
    WATCH_VIDEO_MINUTES,
    WATCH_SHORTS,
    SHARE_APP,
    CATEGORY_DRAMA,
    MEGA_BONUS
}

data class TaskItem(
    val id: String,
    val title: String,
    val description: String,
    val type: TaskType,
    val rewardPoints: Int,
    val currentProgress: Int = 0,
    val targetProgress: Int = 1,
    val unit: String = "টি",
    val isClaimed: Boolean = false
) {
    val isCompleted: Boolean
        get() = currentProgress >= targetProgress

    val progressFraction: Float
        get() = if (targetProgress > 0) {
            (currentProgress.toFloat() / targetProgress.toFloat()).coerceIn(0f, 1f)
        } else 0f
}
