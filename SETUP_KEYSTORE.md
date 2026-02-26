# 🔐 Setup Keystore untuk Release Build

## Opsi 1: Via Android Studio (Recommended)

### Step 1: Generate Keystore
1. Buka project AzKomik di **Android Studio**
2. Klik menu **Build** → **Generate Signed Bundle / APK...**
3. Pilih **"APK"** → Klik **"Next"**
4. Klik **"Create new..."**

### Step 2: Isi Form
Isi dengan data berikut:

| Field | Nilai |
|-------|-------|
| **Key store path** | `app/release.keystore` |
| **Password** | `AzKomik2024Secure!` |
| **Confirm password** | `AzKomik2024Secure!` |
| **Key alias** | `azkomik_release` |
| **Key password** | `AzKomik2024Secure!` |
| **Confirm key password** | `AzKomik2024Secure!` |
| **Validity** | `25` years |
| **First and Last Name** | `Developer` |
| **Organization** | `AzKomik` |
| **City** | `Jakarta` |
| **State** | `DKI Jakarta` |
| **Country Code** | `ID` |

5. Klik **"OK"**
6. Klik **"Cancel"** (tidak perlu build sekarang)

### Step 3: Convert ke Base64

Buka terminal di Android Studio atau folder project:

```bash
# Windows (PowerShell)
[Convert]::ToBase64String([IO.File]::ReadAllBytes("app/release.keystore")) | Out-File -Encoding ASCII keystore_base64.txt

# Mac/Linux
base64 -i app/release.keystore | tr -d '\n' > keystore_base64.txt
```

### Step 4: Copy ke GitHub Secrets

Buka file `keystore_base64.txt`, copy isinya, lalu paste ke GitHub secrets.

---

## Opsi 2: Via Terminal (Java Required)

Jalankan script yang sudah dibuat:

```bash
# Beri permission
chmod +x create-keystore.sh

# Jalankan
./create-keystore.sh
```

Script akan:
1. Membuat keystore di `app/release.keystore`
2. Convert ke base64 → `keystore_base64.txt`
3. Menampilkan semua secrets yang perlu di-copy

---

## GitHub Secrets Checklist

Copy 4 nilai ini ke GitHub → Settings → Secrets → Actions:

```
RELEASE_KEYSTORE              = (isi dari keystore_base64.txt)
RELEASE_KEYSTORE_PASSWORD     = AzKomik2024Secure!
RELEASE_KEY_ALIAS             = azkomik_release
RELEASE_KEY_PASSWORD          = AzKomik2024Secure!
```

---

## ⚠️ Penting!

1. **Backup keystore** - Simpan `app/release.keystore` di tempat aman (Google Drive, USB, dll)
2. **Jangan commit keystore** - Sudah di-ignore di `.gitignore`
3. **Jangan share password** - Hanya Anda yang boleh tahu

---

## Test Release Build

Setelah secrets diatur, test dengan:

```bash
git tag v1.0.0
git push origin v1.0.0
```

GitHub Actions akan otomatis build signed APK!
