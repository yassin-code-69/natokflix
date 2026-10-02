package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GlowingNatokFlixLogo
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.NatokGold
import com.example.ui.theme.NatokRed

@Composable
fun SplashScreen(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF2B080B),
                        BackgroundDark,
                        BackgroundDark
                    )
                )
            )
            .testTag("splash_screen_container"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            // Glowing 3D-styled NatokFlix Logo
            GlowingNatokFlixLogo(
                size = 110.dp,
                enablePulse = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            // App Name: NatokFlix
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Natok",
                    fontSize = 38.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                Text(
                    text = "Flix",
                    fontSize = 38.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = NatokRed
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(Color(0xFF2A1C0A), RoundedCornerShape(16.dp))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "🪙 বাংলা নাটক ও শর্টস দেখুন, আয় করুন আনলিমিটেড",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = NatokGold
                )
            }

            Spacer(modifier = Modifier.height(44.dp))

            CircularProgressIndicator(
                color = NatokRed,
                strokeWidth = 3.dp,
                modifier = Modifier.size(28.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Author credit
            Text(
                text = "Created and Maintained by AR Akhirul Islam",
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF888899)
            )
        }
    }
}
