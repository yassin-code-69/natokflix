// Created and Maintained by AR Akhirul Islam
package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.ui.VideoEarnApp
import com.example.ui.theme.VideoEarnTheme
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            if (FirebaseApp.getApps(this).isEmpty()) {
                FirebaseApp.initializeApp(this)
            }
        } catch (_: Exception) {}
        enableEdgeToEdge()
        setContent {
            VideoEarnTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    VideoEarnApp()
                }
            }
        }
    }
}
