# NutriSnap - DevOps CI/CD Configuration

## Quick Start Scripts

### Build Docker Image
```bash
docker build -t nutrisnap-builder:latest .
```

### Run Build Locally with Docker
```bash
# Debug build
docker run --rm -v $(pwd):/workspace nutrisnap-builder:latest ./gradlew assembleDebug

# Release build
docker run --rm -v $(pwd):/workspace nutrisnap-builder:latest ./gradlew assembleRelease
```

### Run with Docker Compose
```bash
# Build and run
docker-compose up --build

# Run tests
docker-compose run android-builder ./gradlew test

# Clean build
docker-compose run android-builder ./gradlew clean assembleDebug
```

### Local Gradle Commands
```bash
# Clean build
./gradlew clean

# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease

# Run tests
./gradlew test

# Run lint
./gradlew lint

# Build AAB (App Bundle)
./gradlew bundleRelease

# Run all checks
./gradlew check
```

## Jenkins Setup

### Prerequisites
1. Jenkins server with Docker support
2. Android keystore file
3. Jenkins credentials configured

### Required Jenkins Plugins
- Docker Pipeline
- Git
- JUnit
- HTML Publisher
- Credentials Binding

### Jenkins Credentials Setup
Configure the following credentials in Jenkins:

1. **android-keystore** (Secret file)
   - Upload your .jks or .keystore file

2. **keystore-password** (Secret text)
   - Your keystore password

3. **key-alias** (Secret text)
   - Your key alias name

4. **key-password** (Secret text)
   - Your key password

### Creating a Jenkins Pipeline Job
1. New Item → Pipeline
2. Configure Git repository URL
3. Set branch to build (e.g., `main` or `*/main`)
4. Pipeline script from SCM
5. Script Path: `Jenkinsfile`

## Signing Configuration

### Generate a New Keystore (if needed)
```bash
keytool -genkey -v -keystore nutrisnap-release.jks \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias nutrisnap-key
```

### Verify APK Signature
```bash
jarsigner -verify -verbose -certs app/build/outputs/apk/release/nutrisnap-release-signed.apk
```

## Build Variants

### Debug
- Debuggable: Yes
- Minification: No
- Signing: Debug keystore (auto-generated)

### Release
- Debuggable: No
- Minification: Disabled (can be enabled in build.gradle.kts)
- Signing: Release keystore (via Jenkins credentials)

## Environment Variables

### Docker
- `ANDROID_HOME`: /opt/android-sdk
- `ANDROID_SDK_ROOT`: /opt/android-sdk
- `JAVA_HOME`: /usr/lib/jvm/java-17-openjdk-amd64

### Jenkins
- `VERSION_CODE`: Build number
- `VERSION_NAME`: 1.0
- `KEYSTORE_FILE`: Path to keystore
- `KEYSTORE_PASSWORD`: Keystore password
- `KEY_ALIAS`: Key alias
- `KEY_PASSWORD`: Key password

## Troubleshooting

### Docker build fails
```bash
# Clean Docker cache
docker system prune -a

# Rebuild without cache
docker build --no-cache -t nutrisnap-builder:latest .
```

### Gradle daemon issues
```bash
# Stop Gradle daemon
./gradlew --stop

# Clean and rebuild
./gradlew clean assembleDebug
```

### Permission denied on gradlew
```bash
chmod +x gradlew
```

### Android SDK licenses not accepted
```bash
# In Docker container
yes | sdkmanager --licenses
```

## CI/CD Pipeline Stages

1. **Checkout**: Clone repository
2. **Environment Setup**: Verify tools and versions
3. **Dependencies**: Download project dependencies
4. **Lint Analysis**: Static code analysis
5. **Unit Tests**: Run JUnit tests
6. **Build Debug APK**: Create debug build
7. **Build Release APK**: Create release build
8. **Sign APK**: Sign release APK (main branch only)
9. **Build AAB**: Create App Bundle (main branch only)
10. **Archive Artifacts**: Store build outputs

## Artifacts

### Generated Files
- Debug APK: `app/build/outputs/apk/debug/app-debug.apk`
- Release APK: `app/build/outputs/apk/release/app-release.apk`
- Signed APK: `app/build/outputs/apk/release/nutrisnap-release-signed.apk`
- AAB: `app/build/outputs/bundle/release/app-release.aab`

### Reports
- Lint Report: `app/build/reports/lint-results-debug.html`
- Test Report: `app/build/reports/tests/testDebugUnitTest/index.html`
- Coverage Report: `app/build/reports/coverage/`

## Next Steps

1. Push code to Git repository
2. Set up Jenkins server
3. Configure Jenkins credentials
4. Create Jenkins pipeline job
5. Run first build
6. Monitor build results and reports
