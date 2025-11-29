#!/bin/bash

# NutriSnap - Generate Release Keystore Script
# This script generates a new keystore for signing release builds

echo "🔐 NutriSnap - Keystore Generation Script"
echo "=========================================="
echo ""

# Configuration
KEYSTORE_FILE="nutrisnap-release.jks"
KEY_ALIAS="nutrisnap-key"
VALIDITY_DAYS=10000

# Check if keystore already exists
if [ -f "$KEYSTORE_FILE" ]; then
    echo "⚠️  Warning: Keystore file '$KEYSTORE_FILE' already exists!"
    read -p "Do you want to overwrite it? (yes/no): " confirm
    if [ "$confirm" != "yes" ]; then
        echo "❌ Aborted. Existing keystore preserved."
        exit 1
    fi
    rm "$KEYSTORE_FILE"
fi

echo "📝 Please provide the following information:"
echo ""

# Prompt for passwords
read -sp "Enter keystore password: " KEYSTORE_PASSWORD
echo ""
read -sp "Confirm keystore password: " KEYSTORE_PASSWORD_CONFIRM
echo ""

if [ "$KEYSTORE_PASSWORD" != "$KEYSTORE_PASSWORD_CONFIRM" ]; then
    echo "❌ Error: Passwords do not match!"
    exit 1
fi

read -sp "Enter key password (press Enter to use same as keystore): " KEY_PASSWORD
echo ""

if [ -z "$KEY_PASSWORD" ]; then
    KEY_PASSWORD="$KEYSTORE_PASSWORD"
fi

# Prompt for certificate information
echo ""
echo "📋 Certificate Information:"
read -p "First and Last Name (e.g., John Doe): " CN
read -p "Organizational Unit (e.g., Development): " OU
read -p "Organization (e.g., NutriSnap Inc): " O
read -p "City or Locality (e.g., San Francisco): " L
read -p "State or Province (e.g., California): " ST
read -p "Country Code (e.g., US): " C

# Generate the keystore
echo ""
echo "🔨 Generating keystore..."

keytool -genkeypair \
    -v \
    -keystore "$KEYSTORE_FILE" \
    -alias "$KEY_ALIAS" \
    -keyalg RSA \
    -keysize 2048 \
    -validity "$VALIDITY_DAYS" \
    -storepass "$KEYSTORE_PASSWORD" \
    -keypass "$KEY_PASSWORD" \
    -dname "CN=$CN, OU=$OU, O=$O, L=$L, ST=$ST, C=$C"

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Keystore generated successfully!"
    echo ""
    echo "📁 Keystore file: $KEYSTORE_FILE"
    echo "🔑 Key alias: $KEY_ALIAS"
    echo "⏰ Validity: $VALIDITY_DAYS days (~27 years)"
    echo ""
    echo "⚠️  IMPORTANT: Keep this keystore file and passwords secure!"
    echo "⚠️  You will need them to sign future releases."
    echo ""
    echo "📝 Jenkins Credentials to configure:"
    echo "   - android-keystore: Upload $KEYSTORE_FILE"
    echo "   - keystore-password: $KEYSTORE_PASSWORD"
    echo "   - key-alias: $KEY_ALIAS"
    echo "   - key-password: [your key password]"
    echo ""
    
    # Verify the keystore
    echo "🔍 Verifying keystore..."
    keytool -list -v -keystore "$KEYSTORE_FILE" -storepass "$KEYSTORE_PASSWORD"
else
    echo "❌ Error: Failed to generate keystore!"
    exit 1
fi
