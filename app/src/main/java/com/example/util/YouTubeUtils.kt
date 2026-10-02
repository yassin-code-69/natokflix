package com.example.util

object YouTubeUtils {
    fun extractVideoId(url: String): String? {
        val pattern = "(?<=watch\\?v=|/videos/|embed/|youtu.be/)[^#&?]*".toRegex()
        return pattern.find(url)?.value
    }

    fun getThumbnailUrl(videoId: String): String {
        return "https://img.youtube.com/vi/$videoId/maxresdefault.jpg"
    }
}
