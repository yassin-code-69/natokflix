package com.example.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class YouTubeSearchResponse(
    @Json(name = "nextPageToken") val nextPageToken: String? = null,
    @Json(name = "prevPageToken") val prevPageToken: String? = null,
    @Json(name = "items") val items: List<YouTubeSearchItem> = emptyList()
)

@JsonClass(generateAdapter = true)
data class YouTubeSearchItem(
    @Json(name = "id") val id: YouTubeIdInfo? = null,
    @Json(name = "snippet") val snippet: YouTubeSnippet? = null
)

@JsonClass(generateAdapter = true)
data class YouTubeIdInfo(
    @Json(name = "kind") val kind: String? = null,
    @Json(name = "videoId") val videoId: String? = null
)

@JsonClass(generateAdapter = true)
data class YouTubeSnippet(
    @Json(name = "publishedAt") val publishedAt: String? = null,
    @Json(name = "channelId") val channelId: String? = null,
    @Json(name = "title") val title: String? = null,
    @Json(name = "description") val description: String? = null,
    @Json(name = "thumbnails") val thumbnails: YouTubeThumbnails? = null,
    @Json(name = "channelTitle") val channelTitle: String? = null
)

@JsonClass(generateAdapter = true)
data class YouTubeThumbnails(
    @Json(name = "high") val high: YouTubeThumbnailDetails? = null,
    @Json(name = "medium") val medium: YouTubeThumbnailDetails? = null,
    @Json(name = "default") val default: YouTubeThumbnailDetails? = null
)

@JsonClass(generateAdapter = true)
data class YouTubeThumbnailDetails(
    @Json(name = "url") val url: String? = null,
    @Json(name = "width") val width: Int? = null,
    @Json(name = "height") val height: Int? = null
)
