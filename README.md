# AzKomik - Android Manga Reader App

![Android Debug Build](https://github.com/user/azkomik-v2/actions/workflows/android-debug-build.yml/badge.svg?branch=main)
![Android Test](https://github.com/user/azkomik-v2/actions/workflows/android-test.yml/badge.svg?branch=main)
![Android Release Build](https://github.com/user/azkomik-v2/actions/workflows/android-release-build.yml/badge.svg)
![License](https://img.shields.io/badge/license-MIT-blue.svg)

Aplikasi pembaca komik/manga Android yang dibangun dengan Kotlin dan Jetpack Compose.

## 📋 Tech Stack

- **Language**: Kotlin 1.9.0
- **UI Framework**: Jetpack Compose (Material 3)
- **Architecture**: MVVM + Clean Architecture
- **Dependency Injection**: Hilt
- **Networking**: Retrofit + OkHttp + Kotlinx Serialization
- **Database**: Room (SQLite)
- **Image Loading**: Coil
- **Async**: Kotlin Coroutines + Flow
- **Navigation**: Jetpack Navigation Compose
- **Preferences**: DataStore

## 🏗️ Architecture

```
app/
├── data/              # Data Layer
│   ├── local/         # Local data source (Room)
│   ├── remote/        # Remote data source (API)
│   └── repository/    # Repository implementations
├── domain/            # Domain Layer
│   ├── model/         # Business entities
│   ├── repository/    # Repository interfaces
│   └── usecase/       # Business use cases
├── presentation/      # Presentation Layer
│   ├── components/    # Reusable UI components
│   ├── navigation/    # Navigation setup
│   ├── screens/       # Screen composables
│   ├── theme/         # Theme, colors, typography
│   └── viewmodel/     # ViewModels
├── di/                # Dependency Injection modules
└── utils/             # Utility classes
```

## 🚀 Features

- **Home Screen**: Featured manga, favorites, and collections
- **Explore Screen**: Search and browse manga by genre
- **Library Screen**: User's manga collection with filters
- **Updates Screen**: Latest chapter updates
- **Manga Detail**: Manga information and chapter list
- **Reader Screen**: Manga chapter reader with zoom controls
- **Profile Screen**: User stats and reading history

## 🎨 Theme

Dark mode dengan accent orange:
- Background: `#000000`
- Surface: `#121212`
- Primary (Orange): `#F97316`
- Secondary (Green): `#22C55E`

## 📦 Setup

### Prerequisites
- Android Studio Hedgehog atau lebih baru
- JDK 17
- Android SDK 34

### Installation

1. Clone repository ini
2. Buka project di Android Studio
3. Sync Gradle files
4. Run pada emulator atau device fisik

### Build Configuration

```kotlin
// app/build.gradle.kts
android {
    namespace = "com.azkomik"
    compileSdk = 34
    
    defaultConfig {
        applicationId = "com.azkomik"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"
    }
}
```

## 🔧 Configuration

### API Endpoint

Update base URL di `di/AppModule.kt`:

```kotlin
.baseUrl("https://api.azkomik.com/v1/")
```

### Database

Database Room disimpan dengan nama `azkomik_database` dengan 3 tabel:
- `manga` - Manga information
- `chapters` - Chapter information
- `reading_history` - User reading history

## 📱 Screens

| Screen | Description |
|--------|-------------|
| Home | Featured manga, favorites, collections |
| Explore | Search and genre browsing |
| Library | User's manga library |
| Updates | Latest chapter updates |
| Manga Detail | Manga info and chapters |
| Reader | Chapter reader |
| Profile | User stats and settings |

## 🛠️ Development

### Running Tests

```bash
./gradlew test
./gradlew connectedAndroidTest
```

### Building Release APK

```bash
./gradlew assembleRelease
```

---

## 🔄 CI/CD with GitHub Actions

Proyek ini menggunakan GitHub Actions untuk Continuous Integration dan Continuous Deployment.

### Workflows yang Tersedia

| Workflow | Trigger | Output |
|----------|---------|--------|
| **Debug Build** | Push ke `main`/`develop` | Debug APK |
| **Release Build** | Tag release (v*) | Signed APK + AAB |
| **Test** | Pull Request | Test Results + Lint Report |

### Download Build Artifacts

1. Buka tab **Actions** di repository
2. Pilih workflow run yang diinginkan
3. Scroll ke bagian **Artifacts**
4. Download APK yang tersedia

### Setup untuk Release Build (Signed APK)

Untuk build release yang ditandatangani, setup GitHub Secrets berikut:

```
RELEASE_KEYSTORE         = Base64 encoded keystore file
RELEASE_KEYSTORE_PASSWORD = Password keystore
RELEASE_KEY_ALIAS        = Alias key
RELEASE_KEY_PASSWORD     = Password key
```

Lihat [SIGNING.md](SIGNING.md) untuk panduan lengkap.

---

## 📦 Manual Build

### Debug APK
```bash
./gradlew assembleDebug
```
Output: `app/build/outputs/apk/debug/app-debug.apk`

### Release APK (tanpa signing)
```bash
./gradlew assembleRelease
```
Output: `app/build/outputs/apk/release/app-release-unsigned.apk`

### Android App Bundle (untuk Play Store)
```bash
./gradlew bundleRelease
```
Output: `app/build/outputs/bundle/release/app-release.aab`

## 📄 License

This project is open source and available under the MIT License.

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## 📞 Contact

For questions or feedback, please open an issue on the repository.
