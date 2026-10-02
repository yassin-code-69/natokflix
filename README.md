# NatokFlix - Watch Drama & Earn Rewards (Android)

NatokFlix is a high-performance, full-stack native Android application built using Jetpack Compose, Kotlin Coroutines, Room Database, and Google Firebase Firestore. It features live drama streaming, YouTube Data API hybrid search, 3D aura banner carousels, 4-player multiplayer & AI gaming zone, AdMob monetization, real-time admin-to-user live sync, automated referral systems, and wallet withdrawal gateways (bKash, Nagad, Rocket).

---

## 📱 Technology Stack

- **Platform:** Android (API 24+ / Android 7.0 to Android 15+)
- **Language:** Kotlin 2.0+
- **UI Framework:** Jetpack Compose (Material 3)
- **Architecture:** MVVM (Model-View-ViewModel) + Clean Architecture Flow
- **Local Persistence:** Room Database (SQLite with KSP Symbol Processing)
- **Cloud Backend:** Google Firebase Firestore & Firebase Auth
- **Networking:** Retrofit 2 + OkHttp 3 + Moshi Converter
- **Image Loading:** Coil Compose
- **Video Engine:** AndroidYouTubePlayer + Web View Player fallback
- **Monetization:** Google Mobile Ads SDK (AdMob: App Open, Banner, Interstitial, Rewarded Video)
- **CI/CD:** GitHub Actions Automated Workflow (JDK 17 + Gradle Wrapper)

---

## 📋 Prerequisites

Before running or building the project locally, ensure you have installed:
1. **Android Studio Ladybug (or Hedgehog/Iguana/Koala/newer)**
2. **Java Development Kit (JDK 17)** (Temurin or OpenJDK recommended)
3. **Android SDK** (API Level 34 / 35 / 36)

---

## 🚀 Getting Started & Installation

### 1. Clone or Extract the Project
```bash
git clone https://github.com/your-username/natokflix-android.git
cd natokflix-android
```

### 2. Environment Variables Configuration
Copy the `.env.example` file to create your `.env` file:
```bash
cp .env.example .env
```
Populate your configuration values in `.env`:
```env
YOUTUBE_API_KEY=your_youtube_data_api_v3_key
ADMOB_APP_ID=ca-app-pub-2897070588148846~7538724951
ADMOB_BANNER_ID=ca-app-pub-2897070588148846/7383560718
ADMOB_INTERSTITIAL_ID=ca-app-pub-2897070588148846/1015501900
ADMOB_REWARDED_ID=ca-app-pub-2897070588148846/1308358523
ADMOB_APP_OPEN_ID=ca-app-pub-2897070588148846/7538724951
```

### 3. Firebase Configuration
- Place your `google-services.json` file inside the `/app/` directory (already configured in the source package).
- Security rules are defined in `firestore.rules`.

---

## 🛠️ Build & Run Commands

### Run on Connected Device / Emulator:
```bash
./gradlew installDebug
```

### Build Debug APK:
```bash
./gradlew assembleDebug
```
Output APK location:
`app/build/outputs/apk/debug/app-debug.apk`

### Build Release APK:
```bash
./gradlew assembleRelease
```
Output APK location:
`app/build/outputs/apk/release/app-release.apk`

### Run Unit & Local Tests:
```bash
./gradlew testDebugUnitTest
```

---

## 📂 Project Architecture & Directory Structure

```
├── .github/workflows/          # GitHub Actions CI/CD pipeline (build_apk.yml)
├── app/
│   ├── google-services.json    # Firebase configuration
│   ├── proguard-rules.pro      # ProGuard & R8 release keep rules
│   ├── build.gradle.kts        # App-level dependencies & build script
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml # Permissions & activity declarations
│           ├── java/com/example/
│           │   ├── MainActivity.kt         # Single-activity Compose entry
│           │   ├── MyApplication.kt        # Global Application & AdMob init
│           │   ├── data/
│           │   │   ├── local/              # Room DB entities, DAOs, Database
│           │   │   ├── model/              # User, VideoItem, Withdrawal, Economy
│           │   │   ├── remote/             # YouTubeApiEngine & Retrofit service
│           │   │   └── repository/         # NatokRepository (Room + Firestore)
│           │   ├── ui/
│           │   │   ├── NatokViewModel.kt   # Core state management
│           │   │   ├── VideoEarnApp.kt     # App navigation & screen routing
│           │   │   ├── components/         # Reusable UI components & AdMob banners
│           │   │   ├── screens/            # Home, Shorts, GameZone, Admin, Profile
│           │   │   └── theme/              # Color, Type, Theme definitions
│           │   └── util/                   # Bots, notifications, device helpers
│           └── res/                        # Drawables, mipmaps, strings, layouts
├── gradle/
│   └── libs.versions.toml      # Dependency Version Catalog
├── build.gradle.kts            # Root Gradle configuration
├── settings.gradle.kts         # Root project settings & plugin repositories
├── firestore.rules             # Production Firestore security rules
└── README.md                   # Project documentation
```

---

## 🔑 Admin Panel Access

- **Master Credentials:**
  - Username: `ADMIN_NATOKFLIX`
  - Password: `ADMIN9900` or `9900`
- **Features in Admin Panel:**
  - 15-Banner Carousel Manager with real-time drama link binding
  - Live Economy Controls (Coins-to-BDT conversion rate, minimum withdrawal, daily spin limits)
  - Payment Gateways toggle (bKash, Nagad, Rocket)
  - Push Notification Broadcaster & In-App Popups
  - User management & Balance Adjustments
  - Video and Shorts catalog publisher

---

## 📄 License & Distribution

Developed for NatokFlix. Built for full offline portability and cloud deployment.
