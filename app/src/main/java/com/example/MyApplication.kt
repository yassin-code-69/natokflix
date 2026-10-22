package com.example

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.android.gms.ads.MobileAds

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        try {
            if (FirebaseApp.getApps(this).isEmpty()) {
                FirebaseApp.initializeApp(this)
            }
        } catch (_: Exception) {}
        try {
            MobileAds.initialize(this) {}
        } catch (_: Exception) {}
    }
}
