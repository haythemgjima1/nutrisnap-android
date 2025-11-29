pipeline {
    agent {
        dockerfile {
            filename 'Dockerfile'
            args '-v $HOME/.gradle:/root/.gradle'
        }
    }
    
    environment {
        // Android SDK paths
        ANDROID_HOME = '/opt/android-sdk'
        ANDROID_SDK_ROOT = '/opt/android-sdk'
        
        // App version info
        VERSION_NAME = '1.0'
        VERSION_CODE = "${env.BUILD_NUMBER}"
        
        // Keystore credentials (stored in Jenkins Credentials)
        KEYSTORE_FILE = credentials('android-keystore')
        KEYSTORE_PASSWORD = credentials('keystore-password')
        KEY_ALIAS = credentials('key-alias')
        KEY_PASSWORD = credentials('key-password')
    }
    
    stages {
        stage('Checkout') {
            steps {
                echo '📥 Checking out code from repository...'
                checkout scm
                sh 'git log -1 --pretty=format:"%h - %an: %s"'
            }
        }
        
        stage('Environment Setup') {
            steps {
                echo '🔧 Setting up build environment...'
                sh '''
                    echo "Android SDK Root: $ANDROID_SDK_ROOT"
                    echo "Java Version:"
                    java -version
                    echo "Gradle Version:"
                    ./gradlew --version
                '''
            }
        }
        
        stage('Dependencies') {
            steps {
                echo '📦 Downloading dependencies...'
                sh './gradlew dependencies'
            }
        }
        
        stage('Lint Analysis') {
            steps {
                echo '🔍 Running lint analysis...'
                sh './gradlew lint'
            }
            post {
                always {
                    // Publish lint results
                    publishHTML(target: [
                        allowMissing: true,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: 'app/build/reports/lint-results-debug.html',
                        reportFiles: 'lint-results-debug.html',
                        reportName: 'Lint Report'
                    ])
                }
            }
        }
        
        stage('Unit Tests') {
            steps {
                echo '🧪 Running unit tests...'
                sh './gradlew test --continue'
            }
            post {
                always {
                    // Publish test results
                    junit '**/build/test-results/test/*.xml'
                    publishHTML(target: [
                        allowMissing: true,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: 'app/build/reports/tests/testDebugUnitTest',
                        reportFiles: 'index.html',
                        reportName: 'Unit Test Report'
                    ])
                }
            }
        }
        
        stage('Build Debug APK') {
            steps {
                echo '🔨 Building debug APK...'
                sh './gradlew assembleDebug'
            }
            post {
                success {
                    echo '✅ Debug APK built successfully'
                    archiveArtifacts artifacts: 'app/build/outputs/apk/debug/*.apk', fingerprint: true
                }
            }
        }
        
        stage('Build Release APK') {
            steps {
                echo '🔨 Building release APK (unsigned)...'
                sh './gradlew assembleRelease'
            }
            post {
                success {
                    echo '✅ Release APK built successfully'
                }
            }
        }
        
        stage('Sign APK') {
            when {
                branch 'main'
            }
            steps {
                echo '🔐 Signing release APK...'
                script {
                    // Sign the APK using jarsigner
                    sh '''
                        # Find the unsigned APK
                        UNSIGNED_APK=$(find app/build/outputs/apk/release -name "*-release-unsigned.apk" | head -1)
                        SIGNED_APK="app/build/outputs/apk/release/nutrisnap-release-signed.apk"
                        
                        if [ -f "$UNSIGNED_APK" ]; then
                            echo "Signing APK: $UNSIGNED_APK"
                            
                            # Sign the APK
                            jarsigner -verbose \
                                -sigalg SHA256withRSA \
                                -digestalg SHA-256 \
                                -keystore $KEYSTORE_FILE \
                                -storepass $KEYSTORE_PASSWORD \
                                -keypass $KEY_PASSWORD \
                                $UNSIGNED_APK \
                                $KEY_ALIAS
                            
                            # Align the APK
                            zipalign -v 4 $UNSIGNED_APK $SIGNED_APK
                            
                            # Verify the signature
                            jarsigner -verify -verbose -certs $SIGNED_APK
                            
                            echo "Signed APK created: $SIGNED_APK"
                        else
                            echo "Error: Unsigned APK not found"
                            exit 1
                        fi
                    '''
                }
            }
            post {
                success {
                    echo '✅ APK signed successfully'
                    archiveArtifacts artifacts: 'app/build/outputs/apk/release/*-signed.apk', fingerprint: true
                }
            }
        }
        
        stage('Build AAB (Bundle)') {
            when {
                branch 'main'
            }
            steps {
                echo '📦 Building Android App Bundle (AAB)...'
                sh './gradlew bundleRelease'
            }
            post {
                success {
                    echo '✅ AAB built successfully'
                    archiveArtifacts artifacts: 'app/build/outputs/bundle/release/*.aab', fingerprint: true
                }
            }
        }
        
        stage('Archive Artifacts') {
            steps {
                echo '📁 Archiving build artifacts...'
                script {
                    // Archive all APKs and AABs
                    archiveArtifacts artifacts: '**/build/outputs/**/*.apk', allowEmptyArchive: true
                    archiveArtifacts artifacts: '**/build/outputs/**/*.aab', allowEmptyArchive: true
                    
                    // Archive mapping files (ProGuard/R8)
                    archiveArtifacts artifacts: '**/build/outputs/mapping/**/*', allowEmptyArchive: true
                }
            }
        }
    }
    
    post {
        always {
            echo '🧹 Cleaning up workspace...'
            cleanWs()
        }
        success {
            echo '✅ Pipeline completed successfully!'
            // You can add notifications here (email, Slack, etc.)
        }
        failure {
            echo '❌ Pipeline failed!'
            // You can add failure notifications here
        }
    }
}
