package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = NatokRed,
    onPrimary = Color.White,
    primaryContainer = NatokRedDark,
    onPrimaryContainer = Color.White,
    secondary = NatokGold,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF3B2F00),
    onSecondaryContainer = NatokGoldLight,
    tertiary = BkashPink,
    onTertiary = Color.White,
    background = BackgroundDark,
    onBackground = TextPrimary,
    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = TextSecondary,
    outline = BorderDark
)

private val LightColorScheme = lightColorScheme(
    primary = NatokRed,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDADA),
    onPrimaryContainer = NatokRedDark,
    secondary = NatokGold,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFFFFF1C1),
    onSecondaryContainer = Color(0xFF261E00),
    tertiary = BkashPink,
    onTertiary = Color.White,
    background = BackgroundLight,
    onBackground = TextPrimaryLight,
    surface = SurfaceLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = TextSecondaryLight,
    outline = BorderLight
)

@Composable
fun VideoEarnTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    VideoEarnTheme(darkTheme = darkTheme, content = content)
}
