# 📝 Panduan Signing untuk Release Build

Panduan ini menjelaskan cara setup signing configuration untuk build release APK yang ditandatangani di GitHub Actions.

---

## 🔑 Langkah 1: Membuat Keystore

### Menggunakan Android Studio

1. Buka **Build** → **Generate Signed Bundle / APK**
2. Pilih **APK** atau **Android App Bundle**
3. Klik **Create new...** untuk membuat keystore baru
4. Isi informasi keystore:
   - **Key store path**: Pilih lokasi penyimpanan
   - **Password**: Password untuk keystore
   - **Alias**: Nama alias key
   - **Password**: Password untuk key
   - **Validity**: 25 tahun (default)
   - Isi informasi certificate (opsional)

5. Simpan file keystore dengan aman (jangan commit ke repository!)

### Menggunakan Command Line (keytool)

```bash
keytool -genkey -v -keystore azkomik-release.keystore \
  -alias azkomik \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000
```

---

## 🔐 Langkah 2: Encode Keystore ke Base64

GitHub Secrets tidak bisa upload file langsung, jadi kita perlu encode ke Base64.

### Linux/macOS

```bash
base64 -w 0 azkomik-release.keystore > keystore-base64.txt
```

### Windows (PowerShell)

```powershell
[Convert]::ToBase64String([IO.File]::ReadAllBytes("azkomik-release.keystore")) | Out-File -Encoding ASCII keystore-base64.txt
```

### Online Tool

Gunakan tool online seperti [base64encode.org](https://www.base64encode.org/) untuk upload file keystore.

---

## 🔑 Langkah 3: Setup GitHub Secrets

1. Buka repository GitHub Anda
2. Pergi ke **Settings** → **Secrets and variables** → **Actions**
3. Klik **New repository secret**
4. Tambahkan secrets berikut:

| Secret Name | Value |
|-------------|-------|
| `RELEASE_KEYSTORE` | Isi file `keystore-base64.txt` (string Base64) |
| `RELEASE_KEYSTORE_PASSWORD` | Password keystore |
| `RELEASE_KEY_ALIAS` | Alias key (contoh: `azkomik`) |
| `RELEASE_KEY_PASSWORD` | Password key |

---

## 📝 Langkah 4: Update build.gradle.kts (Opsional)

Jika Anda ingin build release dengan signing dari command line lokal, tambahkan konfigurasi ini:

### File: `app/build.gradle.kts`

```kotlin
android {
    // ... konfigurasi lainnya
    
    signingConfigs {
        create("release") {
            // Load dari keystore.properties atau environment variables
            storeFile = file(System.getenv("KEYSTORE_PATH") ?: "release-keystore.jks")
            storePassword = System.getenv("RELEASE_KEYSTORE_PASSWORD")
            keyAlias = System.getenv("RELEASE_KEY_ALIAS")
            keyPassword = System.getenv("RELEASE_KEY_PASSWORD")
        }
    }
    
    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }
}
```

### File: `keystore.properties` (Jangan commit ke repository!)

```properties
storeFile=release-keystore.jks
storePassword=your_keystore_password
keyAlias=your_key_alias
keyPassword=your_key_password
```

Tambahkan ke `.gitignore`:

```
# Keystore
*.jks
*.keystore
keystore.properties
release-keystore.jks
```

---

## 🚀 Langkah 5: Trigger Release Build

### Cara 1: Create Tag Release

```bash
git tag v1.0.0
git push origin v1.0.0
```

Workflow `android-release-build.yml` akan otomatis trigger dan:
- Build signed APK
- Build Android App Bundle (AAB)
- Upload artifacts
- Create GitHub Release dengan attachments

### Cara 2: Manual Trigger

1. Buka **Actions** → **Android Release Build**
2. Klik **Run workflow**
3. Pilih branch
4. Klik **Run workflow**

---

## 📥 Download Release Build

Setelah workflow selesai:

1. **Dari Artifacts**:
   - Buka workflow run di Actions tab
   - Scroll ke bagian **Artifacts**
   - Download APK atau AAB

2. **Dari GitHub Releases**:
   - Buka **Releases** di repository
   - Download dari release terbaru

---

## ⚠️ Keamanan

- **JANGAN** commit file keystore ke repository
- **JANGAN** share password keystore
- Simpan backup keystore di tempat aman (Google Drive, password manager)
- Jika keystore hilang, Anda **tidak bisa** update aplikasi di Play Store

---

## 🔧 Troubleshooting

### Build gagal dengan error "Keystore file not found"

Pastikan secrets sudah di-setup dengan benar di GitHub.

### Build gagal dengan error "Invalid keystore password"

Cek `RELEASE_KEYSTORE_PASSWORD` dan `RELEASE_KEY_PASSWORD` di secrets.

### APK tidak bisa install di Android

Untuk testing, enable "Install from Unknown Sources" di settings Android.

### Release build lebih besar dari debug

Ini normal karena ProGuard/R8 belum enabled. Enable `isMinifyEnabled = true` untuk mengurangi ukuran.

---

## 📚 Referensi

- [Android Developer: Sign your app](https://developer.android.com/studio/publish/app-signing)
- [GitHub Actions: Storing secrets](https://docs.github.com/en/actions/security-guides/encrypted-secrets)
- [GitHub Actions: Android workflow](https://github.com/android-actions/setup-android)
