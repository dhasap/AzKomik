#!/bin/bash

# Script untuk membuat keystore AzKomik
# Jalankan di terminal dengan Java terinstall

echo "==================================="
echo "  AzKomik Keystore Generator"
echo "==================================="
echo ""

# Warna
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Check Java
if ! command -v keytool &> /dev/null; then
    echo -e "${RED}Error: keytool tidak ditemukan!${NC}"
    echo "Pastikan Java JDK sudah terinstall."
    echo "Download: https://www.oracle.com/java/technologies/downloads/"
    exit 1
fi

# Konfigurasi
KEYSTORE_PATH="app/release.keystore"
ALIAS="azkomik_release"
STOREPASS="AzKomik2024Secure!"
KEYPASS="AzKomik2024Secure!"
VALIDITY=9125  # 25 tahun

# Certificate info
DNAME="CN=Developer, OU=Mobile Development, O=AzKomik, L=Jakarta, ST=DKI Jakarta, C=ID"

echo "Konfigurasi:"
echo "  Path: $KEYSTORE_PATH"
echo "  Alias: $ALIAS"
echo "  Password: $STOREPASS"
echo "  Validity: $VALIDITY days (25 years)"
echo ""

# Buat folder app jika belum ada
mkdir -p app

# Hapus keystore lama jika ada
if [ -f "$KEYSTORE_PATH" ]; then
    echo -e "${YELLOW}Keystore lama ditemukan, menghapus...${NC}"
    rm "$KEYSTORE_PATH"
fi

# Generate keystore
echo "Generating keystore..."
keytool -genkey -v \
    -keystore "$KEYSTORE_PATH" \
    -alias "$ALIAS" \
    -keyalg RSA \
    -keysize 2048 \
    -validity "$VALIDITY" \
    -storepass "$STOREPASS" \
    -keypass "$KEYPASS" \
    -dname "$DNAME"

if [ $? -eq 0 ]; then
    echo ""
    echo -e "${GREEN}✅ Keystore berhasil dibuat!${NC}"
    echo ""
    
    # Convert ke base64
    echo "Converting to base64..."
    BASE64=$(base64 -i "$KEYSTORE_PATH" | tr -d '\n')
    
    # Simpan ke file
    echo "$BASE64" > keystore_base64.txt
    
    echo ""
    echo "==================================="
    echo "  GITHUB SECRETS - COPY INI:"
    echo "==================================="
    echo ""
    echo -e "${YELLOW}RELEASE_KEYSTORE:${NC}"
    echo "$BASE64"
    echo ""
    echo "==================================="
    echo -e "${YELLOW}RELEASE_KEYSTORE_PASSWORD:${NC}"
    echo "$STOREPASS"
    echo ""
    echo -e "${YELLOW}RELEASE_KEY_ALIAS:${NC}"
    echo "$ALIAS"
    echo ""
    echo -e "${YELLOW}RELEASE_KEY_PASSWORD:${NC}"
    echo "$KEYPASS"
    echo ""
    echo "==================================="
    echo ""
    echo -e "${GREEN}✅ File tersimpan:${NC}"
    echo "  - $KEYSTORE_PATH"
    echo "  - keystore_base64.txt"
    echo ""
    echo "⚠️  PENTING: Backup keystore ini di tempat aman!"
    echo "   Jangan hilangkan file $KEYSTORE_PATH"
    
else
    echo ""
    echo -e "${RED}❌ Gagal membuat keystore!${NC}"
    exit 1
fi
