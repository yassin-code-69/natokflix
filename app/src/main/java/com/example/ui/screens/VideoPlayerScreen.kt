package com.example.ui.screens

import android.annotation.SuppressLint
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import com.example.data.model.VideoItem
import com.example.ui.components.AdMobBanner
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.BorderDark
import com.example.ui.theme.NatokGold
import com.example.ui.theme.NatokRed
import com.example.ui.theme.NatokRedDark
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun VideoPlayerScreen(
    video: VideoItem,
    allNatoks: List<VideoItem>,
    continuousSeconds: Int,
    watchTimeCountdown: Int,
    sessionCoinsEarned: Int,
    showPreRollAd: Boolean,
    preRollCountdown: Int,
    showPeriodicMidRollAd: Boolean = false,
    periodicAdCountdown: Int = 5,
    timeUntilNextMidRollAd: Int = 180,
    isPlayerPlaying: Boolean,
    onBackClick: () -> Unit,
    onSkipPreRollAd: () -> Unit,
    onDismissPeriodicAd: () -> Unit = {},
    onTogglePlayPause: (Boolean) -> Unit,
    onVideoEnded: () -> Unit = {},
    onPlayCustomUrl: (String) -> Unit,
    onRelatedVideoClick: (VideoItem) -> Unit,
    modifier: Modifier = Modifier
) {
    var isFullscreen by remember { mutableStateOf(false) }
    var isLiked by remember { mutableStateOf(false) }
    var isSaved by remember { mutableStateOf(false) }
    var webViewRef by remember { mutableStateOf<WebView?>(null) }

    var showUrlDialog by remember { mutableStateOf(false) }
    var customUrlInput by remember { mutableStateOf("") }

    BackHandler {
        if (isFullscreen) {
            isFullscreen = false
        } else {
            onBackClick()
        }
    }

    val videoPlayerUrl = remember(video.youtubeVideoId) {
        "https://m.youtube.com/watch?v=${video.youtubeVideoId}"
    }

    val cleanPlayerScript = remember {
        """
        (function() {
            function injectNatokCleanCss() {
                var styleId = 'natokflix-clean-style';
                if (!document.getElementById(styleId)) {
                    var style = document.createElement('style');
                    style.id = styleId;
                    style.textContent = ''
                        + 'ytm-mobile-topbar-renderer, .mobile-topbar-header, .mobile-topbar-header-background, '
                        + '.mobile-topbar-header-content, header.mobile-topbar-header, ytm-app-banner-renderer, '
                        + '#app-banner, .app-banner, ytm-pivot-bar-renderer, .pivot-bar, .watch-below-the-player, '
                        + 'm-watch-next-feed, .watch-bottom-panel, #related, #comments, .comments-section, '
                        + '.ytp-pause-overlay, ytm-search-box { '
                        + '   display: none !important; height: 0 !important; visibility: hidden !important; '
                        + '} '
                        + '#player-container-id, .player-container { '
                        + '   top: 0px !important; margin-top: 0px !important; '
                        + '} '
                        + 'html, body { '
                        + '   background: #000000 !important; '
                        + '}';
                    (document.head || document.documentElement).appendChild(style);
                }
            }

            function cleanDOM() {
                var headers = document.querySelectorAll('ytm-mobile-topbar-renderer, .mobile-topbar-header, ytm-app-banner-renderer, #app-banner');
                for (var i = 0; i < headers.length; i++) {
                    headers[i].style.setProperty('display', 'none', 'important');
                }
                var player = document.getElementById('player-container-id') || document.querySelector('.player-container');
                if (player) {
                    player.style.setProperty('top', '0px', 'important');
                    player.style.setProperty('margin-top', '0px', 'important');
                }
                var skipBtn = document.querySelector('.ytp-ad-skip-button, .ytp-ad-skip-button-modern, .ytp-skip-ad-button');
                if (skipBtn) {
                    try { skipBtn.click(); } catch(e) {}
                }
            }

            function hookVideo() {
                var v = document.querySelector('video');
                if (v && !v._natokHooked) {
                    v._natokHooked = true;
                    v.addEventListener('play', function() {
                        if (window.AndroidBridge && window.AndroidBridge.onPlaybackStateChanged) {
                            window.AndroidBridge.onPlaybackStateChanged(true);
                        }
                    });
                    v.addEventListener('pause', function() {
                        if (window.AndroidBridge && window.AndroidBridge.onPlaybackStateChanged) {
                            window.AndroidBridge.onPlaybackStateChanged(false);
                        }
                    });
                    v.addEventListener('ended', function() {
                        if (window.AndroidBridge && window.AndroidBridge.onVideoEnded) {
                            window.AndroidBridge.onVideoEnded();
                        }
                    });
                }
            }

            function tryAutoStart() {
                var v = document.querySelector('video');
                if (v && v.paused) {
                    var p = v.play();
                    if (p !== undefined) {
                        p.catch(function() {
                            var playBtn = document.querySelector('.ytp-large-play-button, button.player-control-play-pause-icon, .icon-button.player-control-play-pause-icon');
                            if (playBtn) {
                                try { playBtn.click(); } catch(e) {}
                            }
                        });
                    }
                }
            }

            injectNatokCleanCss();
            cleanDOM();
            hookVideo();
            tryAutoStart();

            if (!window._natokObserverSet) {
                window._natokObserverSet = true;
                var observer = new MutationObserver(function() {
                    injectNatokCleanCss();
                    cleanDOM();
                    hookVideo();
                });
                if (document.documentElement || document.body) {
                    observer.observe(document.documentElement || document.body, { childList: true, subtree: true });
                }
            }

            var count = 0;
            var timer = setInterval(function() {
                injectNatokCleanCss();
                cleanDOM();
                hookVideo();
                if (count < 10) {
                    tryAutoStart();
                }
                count++;
                if (count > 25) clearInterval(timer);
            }, 250);
        })();
        """.trimIndent()
    }

    fun sendPlayerCommand(command: String, arg: Int = 0) {
        val js = when (command) {
            "play" -> """
                (function() {
                    var v = document.querySelector('video');
                    if (v) {
                        v.play().catch(function() {
                            var btn = document.querySelector('.ytp-large-play-button, button.player-control-play-pause-icon, .ytp-play-button');
                            if (btn) btn.click();
                        });
                    } else {
                        var btn = document.querySelector('.ytp-large-play-button, button.player-control-play-pause-icon, .ytp-play-button');
                        if (btn) btn.click();
                    }
                })();
            """.trimIndent()
            "pause" -> """
                (function() {
                    var v = document.querySelector('video');
                    if (v) {
                        v.pause();
                    }
                    var btn = document.querySelector('button.player-control-play-pause-icon, .ytp-play-button');
                    if (btn && v && !v.paused) btn.click();
                })();
            """.trimIndent()
            "seek" -> """
                (function() {
                    var v = document.querySelector('video');
                    if (v) {
                        v.currentTime = Math.max(0, Math.min((v.duration || 999999), (v.currentTime || 0) + ($arg)));
                    }
                })();
            """.trimIndent()
            "replay" -> """
                (function() {
                    var v = document.querySelector('video');
                    if (v) {
                        v.currentTime = 0;
                        v.play().catch(function(){});
                    }
                })();
            """.trimIndent()
            else -> ""
        }
        if (js.isNotEmpty()) {
            webViewRef?.evaluateJavascript(js, null)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .testTag("video_player_screen")
    ) {
        if (isFullscreen) {
            // Immersive Fullscreen Mode
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                AndroidView(
                    factory = { ctx ->
                        WebView(ctx).apply {
                            try {
                                setLayerType(android.view.View.LAYER_TYPE_HARDWARE, null)
                            } catch (_: Exception) {}
                            settings.apply {
                                javaScriptEnabled = true
                                domStorageEnabled = true
                                mediaPlaybackRequiresUserGesture = false
                                loadWithOverviewMode = true
                                useWideViewPort = true
                                allowFileAccess = true
                                allowContentAccess = true
                                databaseEnabled = true
                                userAgentString = "Mozilla/5.0 (Linux; Android 14; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Mobile Safari/537.36"
                            }
                            addJavascriptInterface(object {
                                @android.webkit.JavascriptInterface
                                fun onVideoEnded() {
                                    onVideoEnded()
                                }

                                @android.webkit.JavascriptInterface
                                fun onPlaybackStateChanged(isPlaying: Boolean) {
                                    onTogglePlayPause(isPlaying)
                                }
                            }, "AndroidBridge")
                            webChromeClient = WebChromeClient()
                            webViewClient = object : WebViewClient() {
                                override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                                    super.onPageStarted(view, url, favicon)
                                    view?.evaluateJavascript(cleanPlayerScript, null)
                                }

                                override fun onPageFinished(view: WebView?, url: String?) {
                                    super.onPageFinished(view, url)
                                    view?.evaluateJavascript(cleanPlayerScript, null)
                                }
                            }
                            loadUrl(videoPlayerUrl)
                            webViewRef = this
                        }
                    },
                    update = { webView ->
                        webViewRef = webView
                        if (webView.url?.contains(video.youtubeVideoId) != true) {
                            webView.loadUrl(videoPlayerUrl)
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )

                // Floating Top Controls in Fullscreen
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .align(Alignment.TopStart),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { isFullscreen = false },
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                    ) {
                        Icon(Icons.Default.FullscreenExit, contentDescription = "Exit Fullscreen", tint = Color.White)
                    }

                    // Live Points Ticker
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(16.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(text = "🪙", fontSize = 13.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "+১ কয়েন in ${continuousSeconds}s",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = NatokGold
                        )
                    }
                }

                // Floating Bottom Controls Pill in Fullscreen
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 20.dp)
                        .background(Color.Black.copy(alpha = 0.75f), RoundedCornerShape(24.dp))
                        .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(24.dp))
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { sendPlayerCommand("seek", -10) },
                        modifier = Modifier.size(38.dp)
                    ) {
                        Icon(Icons.Default.FastRewind, contentDescription = "Rewind 10s", tint = Color.White, modifier = Modifier.size(22.dp))
                    }

                    IconButton(
                        onClick = {
                            val newPlaying = !isPlayerPlaying
                            onTogglePlayPause(newPlaying)
                            sendPlayerCommand(if (newPlaying) "play" else "pause")
                        },
                        modifier = Modifier
                            .size(44.dp)
                            .background(NatokRed, CircleShape)
                    ) {
                        Icon(
                            imageVector = if (isPlayerPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = "Play/Pause",
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    IconButton(
                        onClick = { sendPlayerCommand("seek", 10) },
                        modifier = Modifier.size(38.dp)
                    ) {
                        Icon(Icons.Default.FastForward, contentDescription = "Forward 10s", tint = Color.White, modifier = Modifier.size(22.dp))
                    }

                    IconButton(
                        onClick = {
                            sendPlayerCommand("replay")
                            onTogglePlayPause(true)
                        },
                        modifier = Modifier.size(38.dp)
                    ) {
                        Icon(Icons.Default.Replay, contentDescription = "Replay", tint = Color(0xFFDDDDDD), modifier = Modifier.size(20.dp))
                    }
                }
            }
        } else {
            // Standard Layout with controls, details, and related videos
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundDark)
            ) {
                // Top Header with Back, Title & URL Input button
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = onBackClick,
                                modifier = Modifier.testTag("back_from_player_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "NatokFlix Stream",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Input custom URL button
                            Button(
                                onClick = { showUrlDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF262638)),
                                shape = RoundedCornerShape(16.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Icon(Icons.Default.Link, contentDescription = null, tint = NatokGold, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("URL পেস্ট", fontSize = 11.sp, color = Color.White)
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            // Total Session Coins Badge
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color(0xFF2B200A))
                                    .border(1.dp, NatokGold.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "🪙 +$sessionCoinsEarned",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NatokGold
                                )
                            }
                        }
                    }
                }

                // In-App Seamless Video Player Box
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(230.dp)
                            .background(Color.Black)
                    ) {
                        AndroidView(
                            factory = { ctx ->
                                WebView(ctx).apply {
                                    try {
                                        setLayerType(android.view.View.LAYER_TYPE_HARDWARE, null)
                                    } catch (_: Exception) {}
                                    settings.apply {
                                        javaScriptEnabled = true
                                        domStorageEnabled = true
                                        mediaPlaybackRequiresUserGesture = false
                                        loadWithOverviewMode = true
                                        useWideViewPort = true
                                        allowFileAccess = true
                                        allowContentAccess = true
                                        databaseEnabled = true
                                        userAgentString = "Mozilla/5.0 (Linux; Android 14; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Mobile Safari/537.36"
                                    }
                                    addJavascriptInterface(object {
                                        @android.webkit.JavascriptInterface
                                        fun onVideoEnded() {
                                            onVideoEnded()
                                        }

                                        @android.webkit.JavascriptInterface
                                        fun onPlaybackStateChanged(isPlaying: Boolean) {
                                            onTogglePlayPause(isPlaying)
                                        }
                                    }, "AndroidBridge")
                                    webChromeClient = WebChromeClient()
                                    webViewClient = object : WebViewClient() {
                                        override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                                            super.onPageStarted(view, url, favicon)
                                            view?.evaluateJavascript(cleanPlayerScript, null)
                                        }

                                        override fun onPageFinished(view: WebView?, url: String?) {
                                            super.onPageFinished(view, url)
                                            view?.evaluateJavascript(cleanPlayerScript, null)
                                        }
                                    }
                                    loadUrl(videoPlayerUrl)
                                    webViewRef = this
                                }
                            },
                            update = { webView ->
                                webViewRef = webView
                                if (webView.url?.contains(video.youtubeVideoId) != true) {
                                    webView.loadUrl(videoPlayerUrl)
                                }
                            },
                            modifier = Modifier.fillMaxSize()
                        )

                        // Automated Pre-Roll Ad Overlay (Shows for 5 seconds before playback with bonus coins)
                        AnimatedVisibility(
                            visible = showPreRollAd,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.92f))
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(CircleShape)
                                            .background(NatokGold.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Videocam,
                                            contentDescription = null,
                                            tint = NatokGold,
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Text(
                                        text = "স্পনসরড বিজ্ঞাপন লোড হচ্ছে...",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )

                                    Text(
                                        text = "পুরো বিজ্ঞাপনটি দেখলে পাবেন +১৫ বোনাস কয়েন!",
                                        fontSize = 12.sp,
                                        color = NatokGold,
                                        textAlign = TextAlign.Center
                                    )

                                    Spacer(modifier = Modifier.height(14.dp))

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(Color(0xFF261D08))
                                                .padding(horizontal = 12.dp, vertical = 6.dp)
                                        ) {
                                            Text(
                                                text = "⏱️ ভিডিও শুরু হতে বাকি: ${preRollCountdown}s",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(10.dp))

                                        Button(
                                            onClick = onSkipPreRollAd,
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF333342)),
                                            shape = RoundedCornerShape(12.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                            modifier = Modifier.height(34.dp)
                                        ) {
                                            Text("স্কিপ করুন", fontSize = 11.sp, color = Color(0xFFDDDDDD))
                                        }
                                    }
                                }
                            }
                        }

                        // Periodic 3-Minute Long Ad Overlay
                        AnimatedVisibility(
                            visible = showPeriodicMidRollAd,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.95f))
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(52.dp)
                                            .clip(CircleShape)
                                            .background(NatokGold.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Videocam,
                                            contentDescription = null,
                                            tint = NatokGold,
                                            modifier = Modifier.size(30.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Text(
                                        text = "বিজ্ঞাপন চলছে (প্রতি ৩ মিনিট ইন্টারভাল)...",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )

                                    Text(
                                        text = "বিজ্ঞাপন শেষে পাবেন +১০ বোনাস কয়েন (৳১.০)!",
                                        fontSize = 12.sp,
                                        color = NatokGold,
                                        textAlign = TextAlign.Center
                                    )

                                    Spacer(modifier = Modifier.height(14.dp))

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(Color(0xFF261D08))
                                                .padding(horizontal = 12.dp, vertical = 6.dp)
                                        ) {
                                            Text(
                                                text = if (periodicAdCountdown > 0) "⏱️ স্কিপ করতে বাকি: ${periodicAdCountdown}s" else "✅ বিজ্ঞাপন সম্পন্ন!",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(10.dp))

                                        Button(
                                            onClick = onDismissPeriodicAd,
                                            enabled = periodicAdCountdown <= 0,
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = if (periodicAdCountdown <= 0) NatokRed else Color(0xFF333342),
                                                contentColor = Color.White
                                            ),
                                            shape = RoundedCornerShape(12.dp),
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                            modifier = Modifier.height(36.dp)
                                        ) {
                                            Text(if (periodicAdCountdown <= 0) "স্কিপ ও নাটক চালু করুন" else "অপেক্ষা করুন", fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Custom In-App Playback Controls Bar
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF13131D))
                            .border(BorderStroke(1.dp, Color(0xFF232334)), RoundedCornerShape(0.dp))
                            .padding(horizontal = 14.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // -10s Rewind
                            IconButton(
                                onClick = {
                                    sendPlayerCommand("seek", -10)
                                },
                                modifier = Modifier.size(38.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FastRewind,
                                    contentDescription = "Rewind 10s",
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(4.dp))

                            // Play / Pause Toggle
                            IconButton(
                                onClick = {
                                    val newPlaying = !isPlayerPlaying
                                    onTogglePlayPause(newPlaying)
                                    sendPlayerCommand(if (newPlaying) "play" else "pause")
                                },
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Color(0xFF261828), CircleShape)
                                    .border(1.dp, NatokRed.copy(alpha = 0.6f), CircleShape)
                            ) {
                                Icon(
                                    imageVector = if (isPlayerPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = "Play/Pause",
                                    tint = NatokRed,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(4.dp))

                            // +10s Forward
                            IconButton(
                                onClick = {
                                    sendPlayerCommand("seek", 10)
                                },
                                modifier = Modifier.size(38.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FastForward,
                                    contentDescription = "Forward 10s",
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(4.dp))

                            // Replay from start
                            IconButton(
                                onClick = {
                                    sendPlayerCommand("replay")
                                    onTogglePlayPause(true)
                                },
                                modifier = Modifier.size(38.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Replay,
                                    contentDescription = "Replay",
                                    tint = Color(0xFFBBBBCC),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        // Fullscreen Toggle
                        IconButton(
                            onClick = { isFullscreen = true },
                            modifier = Modifier.size(38.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Fullscreen,
                                contentDescription = "Fullscreen",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }

                // Floating Natok Watch Reward Banner
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1F142D)),
                        border = BorderStroke(1.dp, NatokGold.copy(alpha = 0.8f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(NatokGold),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("🪙", fontSize = 16.sp)
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "এই নাটকটি দেখলে পাবেন ${video.rewardPoints} কয়েন!",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "সম্পূর্ণ নাটক দেখুন এবং ৳${String.format(java.util.Locale.US, "%.1f", video.rewardPoints / 10.0)} টাকা সরাসরি জিতে নিন 🎉",
                                        fontSize = 11.sp,
                                        color = Color(0xFFDCC8F5)
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF38200A))
                                    .border(1.dp, NatokGold, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "+${video.rewardPoints} 🪙",
                                    color = NatokGold,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }

                // Drama Duration Reward Guide Card
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF1A1426))
                            .border(1.dp, Color(0xFF422E5C), RoundedCornerShape(0.dp))
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "🎬", fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "নাটক ওয়াচ রিওয়ার্ড পলিসি (10 Coins = 1 BDT)",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFE0D0F8)
                                    )
                                    Text(
                                        text = "১০০ কয়েন = ১০ টাকা • ২০০ কয়েন = ২০ টাকা",
                                        fontSize = 11.sp,
                                        color = Color(0xFFC0AECB)
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFF2C1B40))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "১০০ কয়েন = ৳১০",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NatokGold
                                )
                            }
                        }
                    }
                }

                // Continuous Watch & Earn Point Engine Bar (Automatic +2 Points Every 60 Seconds)
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF261E0A))
                            .border(1.dp, NatokGold.copy(alpha = 0.3f), RoundedCornerShape(0.dp))
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "🪙", fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "ওয়াচ & আর্ন ইঞ্জিন সক্রিয় (প্রতি ৬০ সে: +২ কয়েন)",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NatokGold
                                    )
                                    Text(
                                        text = "পরবর্তী ৩ মিনিট অ্যাড: ${timeUntilNextMidRollAd / 60}মি ${timeUntilNextMidRollAd % 60}সে",
                                        fontSize = 10.sp,
                                        color = if (isPlayerPlaying) Color(0xFF00E676) else Color(0xFFFF5252)
                                    )
                                }
                            }

                            Text(
                                text = "${continuousSeconds}s",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        val progress = ((60 - continuousSeconds) / 60f).coerceIn(0f, 1f)
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(5.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = NatokGold,
                            trackColor = Color(0xFF3B2E15)
                        )
                    }
                }

                // Natok Metadata & Details
                item {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = video.title,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "${video.views} • ${video.duration} • ${video.category}",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Action Buttons: Like, Share, Save
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.clickable { isLiked = !isLiked }
                            ) {
                                Icon(
                                    imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = "Like",
                                    tint = if (isLiked) NatokRed else Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                                Text(
                                    text = if (isLiked) "লাইকড" else "লাইক",
                                    fontSize = 11.sp,
                                    color = Color.White,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.clickable { /* Share */ }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Share",
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                                Text(
                                    text = "শেয়ার",
                                    fontSize = 11.sp,
                                    color = Color.White,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.clickable { isSaved = !isSaved }
                            ) {
                                Icon(
                                    imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                    contentDescription = "Save",
                                    tint = if (isSaved) NatokGold else Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                                Text(
                                    text = if (isSaved) "সংরক্ষিত" else "লিস্ট",
                                    fontSize = 11.sp,
                                    color = Color.White,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Channel info
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(NatokRedDark),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = video.channelTitle.firstOrNull()?.toString() ?: "Y",
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = video.channelTitle,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = "Verified",
                                                tint = Color(0xFF64B5F6),
                                                modifier = Modifier.size(13.dp)
                                            )
                                        }
                                        Text(
                                            text = "অফিশিয়াল এইচডি সম্প্রচার",
                                            fontSize = 11.sp,
                                            color = TextSecondary
                                        )
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(NatokRed)
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = "সাবস্ক্রাইব",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "অভিনয়ে: ${video.cast}",
                            fontSize = 12.sp,
                            color = NatokGold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = video.description,
                            fontSize = 12.sp,
                            color = Color(0xFFCCCCCC),
                            lineHeight = 17.sp
                        )
                    }
                }

                // Native Promo Banner
                item {
                    AdMobBanner(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }

                // Related Videos
                item {
                    Text(
                        text = "সম্পর্কিত অন্যান্য নাটক (More Videos)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                    )
                }

                items(allNatoks.filter { it.id != video.id }, key = { it.id }) { related ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                            .clickable { onRelatedVideoClick(related) },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(width = 115.dp, height = 72.dp)
                                .clip(RoundedCornerShape(8.dp))
                        ) {
                            AsyncImage(
                                model = related.thumbnailUrl,
                                contentDescription = related.title,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(4.dp)
                                    .background(Color.Black.copy(alpha = 0.8f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = related.duration,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = related.title,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = related.channelTitle,
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                            Text(
                                text = "🪙 প্রতি ৩০ সে: +১০ কয়েন",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = NatokGold
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }

        // Custom Stream URL Input Dialog
        if (showUrlDialog) {
            AlertDialog(
                onDismissRequest = { showUrlDialog = false },
                title = { Text("ভিডিও লিংক পেস্ট করুন (Stream URL)") },
                text = {
                    Column {
                        Text(
                            text = "যেকোনো ইউটিউব ভিডিও বা নাটকের লিংক পেস্ট করুন। ভিডিও সরাসরি অ্যাপে চলবে এবং কয়েন অর্জিত হবে:",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = customUrlInput,
                            onValueChange = { customUrlInput = it },
                            placeholder = { Text("https://www.youtube.com/watch?v=...") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NatokRed,
                                unfocusedBorderColor = BorderDark
                            )
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (customUrlInput.isNotBlank()) {
                                onPlayCustomUrl(customUrlInput)
                                showUrlDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NatokRed)
                    ) {
                        Text("ভিডিও চালান")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showUrlDialog = false }) {
                        Text("বাতিল")
                    }
                }
            )
        }
    }
}
