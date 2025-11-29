# NutriSnap - Complete DevOps CI/CD Documentation

## Table of Contents
1. [Overview](#overview)
2. [Architecture](#architecture)
3. [Docker Environment](#docker-environment)
4. [Jenkins CI/CD Pipeline](#jenkins-cicd-pipeline)
5. [Build Configuration](#build-configuration)
6. [Testing Strategy](#testing-strategy)
7. [Signing & Security](#signing--security)
8. [Deployment Process](#deployment-process)
9. [Monitoring & Reporting](#monitoring--reporting)
10. [Troubleshooting](#troubleshooting)

---

## Overview

### Project Information
- **Application Name**: NutriSnap
- **Package**: com.example.nutrisnap
- **Platform**: Android
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 36 (Android 14+)
- **Build System**: Gradle (Kotlin DSL)
- **Java Version**: 11
- **Version Code**: Dynamic (Jenkins BUILD_NUMBER)
- **Version Name**: 1.0

### DevOps Stack
- **Containerization**: Docker
- **CI/CD**: Jenkins
- **Build Tool**: Gradle 8.x
- **Testing**: JUnit, Espresso
- **Code Quality**: Android Lint
- **Version Control**: Git

---

## Architecture

### CI/CD Pipeline Flow

```
┌─────────────┐
│   Git Push  │
└──────┬──────┘
       │
       ▼
┌─────────────────────────────────────────────────────────┐
│                  Jenkins Pipeline                        │
├─────────────────────────────────────────────────────────┤
│  1. Checkout Code                                        │
│  2. Setup Environment                                    │
│  3. Download Dependencies                                │
│  4. Run Lint Analysis          ──► Lint Report          │
│  5. Run Unit Tests             ──► Test Report          │
│  6. Build Debug APK            ──► Debug APK             │
│  7. Build Release APK          ──► Unsigned APK          │
│  8. Sign APK (main branch)     ──► Signed APK            │
│  9. Build AAB (main branch)    ──► App Bundle            │
│ 10. Archive Artifacts          ──► Jenkins Storage       │
└─────────────────────────────────────────────────────────┘
       │
       ▼
┌─────────────┐
│  Artifacts  │
│   Stored    │
└─────────────┘
```

### Docker Architecture

```
┌──────────────────────────────────────────┐
│         Docker Container                  │
├──────────────────────────────────────────┤
│  Ubuntu 22.04                             │
│  ├─ OpenJDK 17                            │
│  ├─ Android SDK 36                        │
│  ├─ Build Tools 34.0.0                    │
│  ├─ Platform Tools                        │
│  └─ Gradle Wrapper                        │
│                                           │
│  Volumes:                                 │
│  ├─ /workspace (Project files)            │
│  └─ /root/.gradle (Gradle cache)          │
└──────────────────────────────────────────┘
```

---

## Docker Environment

### Dockerfile Breakdown

#### Base Image
```dockerfile
FROM ubuntu:22.04
```
- Uses Ubuntu 22.04 LTS for stability
- Provides consistent build environment

#### Environment Variables
```dockerfile
ENV ANDROID_SDK_ROOT=/opt/android-sdk
ENV ANDROID_HOME=/opt/android-sdk
ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
```
- `ANDROID_SDK_ROOT`: Android SDK location
- `ANDROID_HOME`: Legacy SDK path (for compatibility)
- `JAVA_HOME`: JDK installation path

#### Installed Components
1. **OpenJDK 17**: Java Development Kit
2. **Android SDK Command Line Tools**: Latest version
3. **Platform Tools**: ADB, fastboot
4. **Build Tools 34.0.0**: aapt, zipalign, apksigner
5. **Android Platform 36**: Target SDK platform
6. **Google Play Services**: For Google APIs

#### Build Process
```bash
# Build the Docker image
docker build -t nutrisnap-builder:latest .

# Verify the image
docker images | grep nutrisnap-builder
```

### Docker Compose Configuration

```yaml
services:
  android-builder:
    build: .
    volumes:
      - .:/workspace              # Mount project directory
      - gradle-cache:/root/.gradle # Persist Gradle cache
    working_dir: /workspace
    command: ./gradlew assembleDebug
```

**Benefits**:
- Persistent Gradle cache (faster builds)
- Easy local testing
- Consistent with Jenkins environment

### Usage Examples

#### Build Debug APK
```bash
docker-compose run android-builder ./gradlew assembleDebug
```

#### Run Tests
```bash
docker-compose run android-builder ./gradlew test
```

#### Clean Build
```bash
docker-compose run android-builder ./gradlew clean assembleRelease
```

---

## Jenkins CI/CD Pipeline

### Pipeline Stages Detailed

#### 1. Checkout Stage
```groovy
stage('Checkout') {
    steps {
        checkout scm
        sh 'git log -1 --pretty=format:"%h - %an: %s"'
    }
}
```
**Purpose**: Clone repository and display last commit
**Output**: Commit hash, author, and message

#### 2. Environment Setup
```groovy
stage('Environment Setup') {
    steps {
        sh '''
            echo "Android SDK Root: $ANDROID_SDK_ROOT"
            java -version
            ./gradlew --version
        '''
    }
}
```
**Purpose**: Verify build environment
**Checks**: Java version, Gradle version, SDK path

#### 3. Dependencies
```groovy
stage('Dependencies') {
    steps {
        sh './gradlew dependencies'
    }
}
```
**Purpose**: Download and cache project dependencies
**Benefits**: Faster subsequent builds

#### 4. Lint Analysis
```groovy
stage('Lint Analysis') {
    steps {
        sh './gradlew lint'
    }
    post {
        always {
            publishHTML(target: [...])
        }
    }
}
```
**Purpose**: Static code analysis
**Output**: HTML report with code quality issues
**Report Location**: `app/build/reports/lint-results-debug.html`

**Lint Checks**:
- Unused resources
- Hardcoded strings
- Missing translations
- Performance issues
- Security vulnerabilities
- Accessibility issues

#### 5. Unit Tests
```groovy
stage('Unit Tests') {
    steps {
        sh './gradlew test --continue'
    }
    post {
        always {
            junit '**/build/test-results/test/*.xml'
            publishHTML(target: [...])
        }
    }
}
```
**Purpose**: Run JUnit unit tests
**Output**: Test results XML + HTML report
**Report Location**: `app/build/reports/tests/testDebugUnitTest/index.html`

**Test Coverage**:
- Business logic tests
- ViewModel tests
- Utility function tests
- Data model tests

#### 6. Build Debug APK
```groovy
stage('Build Debug APK') {
    steps {
        sh './gradlew assembleDebug'
    }
    post {
        success {
            archiveArtifacts artifacts: 'app/build/outputs/apk/debug/*.apk'
        }
    }
}
```
**Purpose**: Create debuggable APK for testing
**Output**: `app-debug.apk`
**Features**: Debuggable, not minified

#### 7. Build Release APK
```groovy
stage('Build Release APK') {
    steps {
        sh './gradlew assembleRelease'
    }
}
```
**Purpose**: Create optimized release APK
**Output**: `app-release-unsigned.apk`
**Features**: Not debuggable, optimized

#### 8. Sign APK
```groovy
stage('Sign APK') {
    when {
        branch 'main'
    }
    steps {
        sh '''
            jarsigner -verbose \
                -sigalg SHA256withRSA \
                -digestalg SHA-256 \
                -keystore $KEYSTORE_FILE \
                -storepass $KEYSTORE_PASSWORD \
                -keypass $KEY_PASSWORD \
                $UNSIGNED_APK \
                $KEY_ALIAS
            
            zipalign -v 4 $UNSIGNED_APK $SIGNED_APK
            jarsigner -verify -verbose -certs $SIGNED_APK
        '''
    }
}
```
**Purpose**: Sign APK for distribution
**Condition**: Only on `main` branch
**Output**: `nutrisnap-release-signed.apk`

**Signing Process**:
1. Sign with jarsigner (SHA256withRSA)
2. Align with zipalign (4-byte boundaries)
3. Verify signature

#### 9. Build AAB (Bundle)
```groovy
stage('Build AAB (Bundle)') {
    when {
        branch 'main'
    }
    steps {
        sh './gradlew bundleRelease'
    }
}
```
**Purpose**: Create Android App Bundle for Play Store
**Condition**: Only on `main` branch
**Output**: `app-release.aab`

**AAB Benefits**:
- Smaller download sizes
- Dynamic delivery
- Required for Play Store

#### 10. Archive Artifacts
```groovy
stage('Archive Artifacts') {
    steps {
        archiveArtifacts artifacts: '**/build/outputs/**/*.apk'
        archiveArtifacts artifacts: '**/build/outputs/**/*.aab'
        archiveArtifacts artifacts: '**/build/outputs/mapping/**/*'
    }
}
```
**Purpose**: Store build outputs in Jenkins
**Artifacts**:
- APK files (debug, release, signed)
- AAB files
- ProGuard/R8 mapping files

### Jenkins Configuration

#### Required Plugins
1. **Docker Pipeline**: Run builds in Docker containers
2. **Git**: Source code management
3. **JUnit**: Test result publishing
4. **HTML Publisher**: Report publishing
5. **Credentials Binding**: Secure credential management
6. **Pipeline**: Pipeline as code support

#### Credentials Setup

##### 1. Android Keystore (Secret File)
- **ID**: `android-keystore`
- **Type**: Secret file
- **File**: Upload `.jks` or `.keystore` file

##### 2. Keystore Password (Secret Text)
- **ID**: `keystore-password`
- **Type**: Secret text
- **Value**: Your keystore password

##### 3. Key Alias (Secret Text)
- **ID**: `key-alias`
- **Type**: Secret text
- **Value**: Your key alias name

##### 4. Key Password (Secret Text)
- **ID**: `key-password`
- **Type**: Secret text
- **Value**: Your key password

#### Creating Jenkins Job

1. **New Item** → **Pipeline**
2. **General**:
   - Name: `NutriSnap-Android-CI-CD`
   - Description: `CI/CD pipeline for NutriSnap Android app`
3. **Build Triggers**:
   - ✅ GitHub hook trigger for GITScm polling
   - ✅ Poll SCM: `H/5 * * * *` (every 5 minutes)
4. **Pipeline**:
   - Definition: Pipeline script from SCM
   - SCM: Git
   - Repository URL: `https://github.com/yourusername/nutrisnap.git`
   - Branches to build: `*/main`
   - Script Path: `Jenkinsfile`

---

## Build Configuration

### Gradle Configuration (build.gradle.kts)

#### Application Configuration
```kotlin
android {
    namespace = "com.example.nutrisnap"
    compileSdk = 36
    
    defaultConfig {
        applicationId = "com.example.nutrisnap"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}
```

#### Build Types
```kotlin
buildTypes {
    release {
        isMinifyEnabled = false
        proguardFiles(
            getDefaultProguardFile("proguard-android-optimize.txt"),
            "proguard-rules.pro"
        )
    }
}
```

**Debug Build**:
- Debuggable: Yes
- Minification: No
- Obfuscation: No
- Shrinking: No

**Release Build**:
- Debuggable: No
- Minification: Disabled (can be enabled)
- Obfuscation: Disabled (can be enabled)
- Shrinking: Disabled (can be enabled)

#### Dependencies
- **AndroidX**: Core Android libraries
- **Material Design**: UI components
- **Retrofit**: HTTP client
- **Gson**: JSON parsing
- **Glide**: Image loading
- **CameraX**: Camera functionality
- **Coroutines**: Async operations
- **JUnit**: Unit testing
- **Espresso**: UI testing

### Build Variants

#### Debug
```bash
./gradlew assembleDebug
```
- Output: `app-debug.apk`
- Signing: Debug keystore (auto-generated)
- Use case: Development, testing

#### Release
```bash
./gradlew assembleRelease
```
- Output: `app-release-unsigned.apk`
- Signing: None (unsigned)
- Use case: Pre-signing build

#### Signed Release
```bash
# Via Jenkins pipeline
```
- Output: `nutrisnap-release-signed.apk`
- Signing: Release keystore
- Use case: Distribution

---

## Testing Strategy

### Unit Tests

#### Test Framework
- **JUnit 4**: Core testing framework
- **Mockito**: Mocking framework (if needed)
- **Truth**: Assertion library (if needed)

#### Test Location
```
app/src/test/java/com/example/nutrisnap/
```

#### Running Tests
```bash
# Run all unit tests
./gradlew test

# Run specific test class
./gradlew test --tests com.example.nutrisnap.ExampleUnitTest

# Run with coverage
./gradlew testDebugUnitTest jacocoTestReport
```

#### Test Reports
- **Location**: `app/build/reports/tests/testDebugUnitTest/index.html`
- **Format**: HTML with test results, failures, and execution time

### Instrumentation Tests

#### Test Framework
- **Espresso**: UI testing
- **AndroidJUnitRunner**: Test runner

#### Test Location
```
app/src/androidTest/java/com/example/nutrisnap/
```

#### Running Tests
```bash
# Run all instrumentation tests
./gradlew connectedAndroidTest

# Run on specific device
./gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.example.nutrisnap.ExampleInstrumentedTest
```

### Lint Analysis

#### Running Lint
```bash
# Run lint on debug variant
./gradlew lintDebug

# Run lint on release variant
./gradlew lintRelease

# Run lint on all variants
./gradlew lint
```

#### Lint Report
- **Location**: `app/build/reports/lint-results-debug.html`
- **Checks**: 300+ code quality checks

#### Lint Categories
1. **Correctness**: Code correctness issues
2. **Security**: Security vulnerabilities
3. **Performance**: Performance issues
4. **Usability**: Usability issues
5. **Accessibility**: Accessibility issues
6. **Internationalization**: i18n issues

---

## Signing & Security

### Keystore Generation

#### Using the Script
```bash
cd scripts
chmod +x generate-keystore.sh
./generate-keystore.sh
```

#### Manual Generation
```bash
keytool -genkeypair \
    -v \
    -keystore nutrisnap-release.jks \
    -alias nutrisnap-key \
    -keyalg RSA \
    -keysize 2048 \
    -validity 10000 \
    -storepass YOUR_KEYSTORE_PASSWORD \
    -keypass YOUR_KEY_PASSWORD \
    -dname "CN=Your Name, OU=Development, O=NutriSnap, L=City, ST=State, C=US"
```

### Keystore Information

#### File Details
- **Filename**: `nutrisnap-release.jks`
- **Format**: JKS (Java KeyStore)
- **Algorithm**: RSA
- **Key Size**: 2048 bits
- **Validity**: 10,000 days (~27 years)

#### Certificate Information
- **Common Name (CN)**: Developer name
- **Organizational Unit (OU)**: Development team
- **Organization (O)**: Company name
- **Locality (L)**: City
- **State (ST)**: State/Province
- **Country (C)**: Country code

### Signing Process

#### Automatic (Jenkins)
1. Jenkins retrieves keystore from credentials
2. Signs APK using jarsigner
3. Aligns APK using zipalign
4. Verifies signature
5. Archives signed APK

#### Manual Signing
```bash
# Sign the APK
jarsigner -verbose \
    -sigalg SHA256withRSA \
    -digestalg SHA-256 \
    -keystore nutrisnap-release.jks \
    -storepass YOUR_PASSWORD \
    app/build/outputs/apk/release/app-release-unsigned.apk \
    nutrisnap-key

# Align the APK
zipalign -v 4 \
    app/build/outputs/apk/release/app-release-unsigned.apk \
    app/build/outputs/apk/release/nutrisnap-release-signed.apk

# Verify the signature
jarsigner -verify -verbose -certs \
    app/build/outputs/apk/release/nutrisnap-release-signed.apk
```

### Security Best Practices

#### Keystore Security
1. **Never commit keystore to Git**
   - Add `*.jks` and `*.keystore` to `.gitignore`
2. **Use Jenkins Credentials**
   - Store keystore as secret file
   - Store passwords as secret text
3. **Backup keystore securely**
   - Store in secure location (encrypted drive, password manager)
   - Keep multiple backups
4. **Restrict access**
   - Only authorized personnel
   - Use Jenkins role-based access control

#### Password Management
1. **Strong passwords**
   - Minimum 12 characters
   - Mix of uppercase, lowercase, numbers, symbols
2. **Different passwords**
   - Keystore password ≠ Key password
3. **Secure storage**
   - Use password manager
   - Never hardcode in scripts

---

## Deployment Process

### Build Artifacts

#### Debug APK
- **File**: `app-debug.apk`
- **Purpose**: Development and testing
- **Distribution**: Internal testers, QA team
- **Installation**: Direct install via ADB or file manager

#### Release APK (Signed)
- **File**: `nutrisnap-release-signed.apk`
- **Purpose**: Production distribution
- **Distribution**: Direct download, third-party stores
- **Installation**: User installs from file

#### Android App Bundle (AAB)
- **File**: `app-release.aab`
- **Purpose**: Google Play Store distribution
- **Distribution**: Google Play Console
- **Benefits**: Smaller downloads, dynamic delivery

### Distribution Channels

#### 1. Internal Testing
- **Artifact**: Debug APK
- **Method**: Direct install, Firebase App Distribution
- **Audience**: Development team, QA

#### 2. Beta Testing
- **Artifact**: Signed APK or AAB
- **Method**: Google Play Internal Testing Track
- **Audience**: Beta testers

#### 3. Production
- **Artifact**: AAB
- **Method**: Google Play Production Track
- **Audience**: Public users

### Artifact Storage

#### Jenkins Artifacts
- **Location**: Jenkins build artifacts
- **Retention**: Configurable (e.g., last 10 builds)
- **Access**: Via Jenkins UI

#### External Storage (Optional)
- **Nexus/Artifactory**: Artifact repository
- **AWS S3**: Cloud storage
- **Google Cloud Storage**: Cloud storage

---

## Monitoring & Reporting

### Build Reports

#### Lint Report
- **Location**: `app/build/reports/lint-results-debug.html`
- **Content**:
  - Issue severity (Error, Warning, Information)
  - Issue category
  - File location and line number
  - Issue description and explanation
  - Suggested fixes

#### Test Report
- **Location**: `app/build/reports/tests/testDebugUnitTest/index.html`
- **Content**:
  - Test summary (passed, failed, skipped)
  - Test execution time
  - Test class details
  - Failure stack traces

#### Build Logs
- **Location**: Jenkins build console output
- **Content**:
  - Build steps execution
  - Gradle output
  - Error messages
  - Warnings

### Jenkins Dashboard

#### Build Status
- ✅ **Success**: All stages passed
- ❌ **Failure**: One or more stages failed
- ⚠️ **Unstable**: Build succeeded but tests failed

#### Build Trends
- Build duration over time
- Test pass/fail trends
- Code quality trends

#### Artifacts
- List of archived artifacts
- Download links
- Artifact size and timestamp

### Notifications (Optional)

#### Email Notifications
```groovy
post {
    failure {
        mail to: 'team@nutrisnap.com',
             subject: "Build Failed: ${env.JOB_NAME} - ${env.BUILD_NUMBER}",
             body: "Check console output at ${env.BUILD_URL}"
    }
}
```

#### Slack Notifications
```groovy
post {
    success {
        slackSend channel: '#builds',
                  color: 'good',
                  message: "Build Successful: ${env.JOB_NAME} - ${env.BUILD_NUMBER}"
    }
}
```

---

## Troubleshooting

### Common Issues

#### 1. Docker Build Fails

**Symptom**: Docker image build fails
**Possible Causes**:
- Network issues downloading SDK
- Insufficient disk space
- Permission issues

**Solutions**:
```bash
# Clean Docker cache
docker system prune -a

# Build without cache
docker build --no-cache -t nutrisnap-builder:latest .

# Check disk space
df -h

# Check Docker logs
docker logs <container_id>
```

#### 2. Gradle Build Fails

**Symptom**: `./gradlew assembleDebug` fails
**Possible Causes**:
- Dependency resolution issues
- Gradle daemon issues
- SDK version mismatch

**Solutions**:
```bash
# Stop Gradle daemon
./gradlew --stop

# Clean build
./gradlew clean

# Rebuild
./gradlew assembleDebug

# Update dependencies
./gradlew --refresh-dependencies
```

#### 3. Tests Fail

**Symptom**: Unit tests fail in CI but pass locally
**Possible Causes**:
- Environment differences
- Missing test resources
- Flaky tests

**Solutions**:
```bash
# Run tests with stack trace
./gradlew test --stacktrace

# Run specific test
./gradlew test --tests com.example.nutrisnap.ExampleUnitTest

# Clean test cache
./gradlew cleanTest test
```

#### 4. Signing Fails

**Symptom**: APK signing fails in Jenkins
**Possible Causes**:
- Incorrect credentials
- Keystore file not found
- Wrong key alias

**Solutions**:
1. Verify Jenkins credentials are configured
2. Check keystore file is uploaded
3. Verify key alias matches keystore
4. Test signing locally:
```bash
jarsigner -verify -verbose -certs app-release.apk
```

#### 5. Out of Memory

**Symptom**: Gradle build fails with OutOfMemoryError
**Possible Causes**:
- Insufficient heap size
- Too many parallel builds

**Solutions**:
```bash
# Increase heap size in gradle.properties
org.gradle.jvmargs=-Xmx4096m -XX:MaxPermSize=512m

# Disable parallel builds
org.gradle.parallel=false
```

### Debug Commands

#### Check Environment
```bash
# Java version
java -version

# Gradle version
./gradlew --version

# Android SDK location
echo $ANDROID_HOME

# List installed SDK components
sdkmanager --list
```

#### Clean Build
```bash
# Clean all build outputs
./gradlew clean

# Clean and rebuild
./gradlew clean assembleDebug

# Clean specific variant
./gradlew cleanDebug assembleDebug
```

#### Verbose Output
```bash
# Run with stack trace
./gradlew assembleDebug --stacktrace

# Run with debug output
./gradlew assembleDebug --debug

# Run with info output
./gradlew assembleDebug --info
```

---

## Best Practices

### 1. Version Control
- ✅ Commit `Jenkinsfile`, `Dockerfile`, `docker-compose.yml`
- ✅ Commit `.dockerignore`
- ❌ Never commit keystore files
- ❌ Never commit passwords or secrets
- ✅ Use `.gitignore` for build outputs

### 2. Build Optimization
- Use Gradle build cache
- Enable parallel builds (if memory allows)
- Use Docker layer caching
- Cache dependencies in Docker volumes

### 3. Security
- Store credentials in Jenkins Credentials
- Use environment variables for secrets
- Rotate keystore passwords regularly
- Limit access to Jenkins and keystore

### 4. Testing
- Run tests on every commit
- Maintain high test coverage
- Fix failing tests immediately
- Use test reports to track quality

### 5. Documentation
- Document build process
- Keep README up to date
- Document environment setup
- Maintain troubleshooting guide

---

## Appendix

### File Structure
```
nutrisnap3/
├── .dockerignore
├── Dockerfile
├── docker-compose.yml
├── Jenkinsfile
├── DEVOPS_QUICK_START.md
├── DEVOPS_DOCUMENTATION.md (this file)
├── scripts/
│   └── generate-keystore.sh
├── app/
│   ├── build.gradle.kts
│   ├── src/
│   │   ├── main/
│   │   ├── test/
│   │   └── androidTest/
│   └── proguard-rules.pro
├── build.gradle.kts
├── gradle/
├── gradlew
└── gradlew.bat
```

### Gradle Tasks Reference
```bash
# Build tasks
./gradlew assembleDebug          # Build debug APK
./gradlew assembleRelease        # Build release APK
./gradlew bundleRelease          # Build AAB

# Test tasks
./gradlew test                   # Run unit tests
./gradlew connectedAndroidTest   # Run instrumentation tests
./gradlew lint                   # Run lint analysis

# Clean tasks
./gradlew clean                  # Clean build outputs
./gradlew cleanBuildCache        # Clean build cache

# Dependency tasks
./gradlew dependencies           # Show dependencies
./gradlew dependencyUpdates      # Check for updates

# Help tasks
./gradlew tasks                  # List all tasks
./gradlew help                   # Show help
```

### Environment Variables
```bash
# Android SDK
ANDROID_HOME=/opt/android-sdk
ANDROID_SDK_ROOT=/opt/android-sdk

# Java
JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64

# Gradle
GRADLE_USER_HOME=/root/.gradle

# Build
VERSION_CODE=${BUILD_NUMBER}
VERSION_NAME=1.0
```

### Useful Links
- [Android Developer Documentation](https://developer.android.com/)
- [Gradle Documentation](https://docs.gradle.org/)
- [Jenkins Documentation](https://www.jenkins.io/doc/)
- [Docker Documentation](https://docs.docker.com/)
- [Android Signing Documentation](https://developer.android.com/studio/publish/app-signing)

---

## Summary

This DevOps setup provides:
- ✅ Automated CI/CD pipeline
- ✅ Containerized build environment
- ✅ Comprehensive testing
- ✅ Code quality analysis
- ✅ Secure signing process
- ✅ Artifact management
- ✅ Detailed reporting

The pipeline ensures consistent, reliable builds and enables rapid iteration while maintaining code quality and security.

---

**Document Version**: 1.0  
**Last Updated**: 2025-11-29  
**Maintained By**: DevOps Team
