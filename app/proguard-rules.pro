# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/runner/work/android-sdk/android-sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/projects/proguard.html

# Add any project specific keep options here:
-keep class com.example.** { *; }

# Firebase
-keep class com.google.firebase.** { *; }

# AdMob
-keep class com.google.android.gms.ads.** { *; }
-keep interface com.google.android.gms.ads.** { *; }

# YouTube Player
-keep class com.pierfrancescosoffritti.androidyoutubeplayer.** { *; }
