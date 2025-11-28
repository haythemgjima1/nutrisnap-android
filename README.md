# NutriSnap - AI-Powered Nutrition Tracking App

An intelligent Android nutrition tracking application that leverages AI to help users monitor their diet, calculate macros, and achieve their health goals.

## 🎯 Features

### AI-Powered Features
- **AI Bro Chatbot**: Interactive AI assistant powered by Gemini API for nutrition advice and guidance
- **Smart Meal Logging**: AI-assisted meal entry and nutritional analysis
- **Personalized Recommendations**: Get customized nutrition suggestions based on your profile and goals

### Nutrition Tracking
- **Meal Journal**: Log daily meals and track nutritional intake
- **Macro Calculator**: Calculate and track macronutrients (proteins, carbs, fats)
- **Calorie Tracking**: Monitor daily caloric intake
- **Meal Approval System**: Review and approve meal suggestions

### User Profile & Onboarding
- **Comprehensive Onboarding**: Step-by-step wizard to set up your profile
  - Age and gender selection
  - Height and weight input
  - Goal setting (weight loss, muscle gain, maintenance)
  - Activity level assessment
  - Desired weight target
  - Personal obstacles identification
- **Profile Management**: Update personal information and preferences
- **Progress Tracking**: Monitor your journey towards health goals

### Exercise Integration
- **Exercise Tracking**: Log and track physical activities
- **Workout Recommendations**: AI-powered exercise suggestions
- **Exercise Library**: Browse and select from various exercises

## 🛠️ Technology Stack

- **Platform**: Android (Java/Kotlin)
- **AI Integration**: Google Gemini API
- **Backend**: Supabase
  - Authentication
  - Database (PostgreSQL)
  - Real-time updates
- **Architecture**: MVVM (Model-View-ViewModel)
- **UI Components**: Material Design, Custom fragments

## 📁 Project Structure

```
nutrisnap/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/nutrisnap/
│   │   │   │   ├── activities/
│   │   │   │   │   ├── DashboardActivity.java
│   │   │   │   │   ├── LoginActivity.java
│   │   │   │   │   ├── MacroCalculationActivity.java
│   │   │   │   │   ├── MealApprovalActivity.java
│   │   │   │   │   └── SignupActivity.java
│   │   │   │   ├── adapters/
│   │   │   │   │   ├── ChatAdapter.java
│   │   │   │   │   └── ExerciseAdapter.java
│   │   │   │   ├── fragments/
│   │   │   │   │   ├── AiBroFragment.java
│   │   │   │   │   ├── ExercisesFragment.java
│   │   │   │   │   ├── HomeFragment.java
│   │   │   │   │   ├── JournalFragment.java
│   │   │   │   │   ├── ProfileFragment.java
│   │   │   │   │   └── ScanFragment.java
│   │   │   │   ├── models/
│   │   │   │   │   ├── ChatMessage.java
│   │   │   │   │   ├── DailySummary.java
│   │   │   │   │   ├── Exercise.java
│   │   │   │   │   ├── JournalEntry.java
│   │   │   │   │   └── UserProfile.java
│   │   │   │   ├── network/
│   │   │   │   │   ├── GeminiApiClient.java
│   │   │   │   │   └── SupabaseService.java
│   │   │   │   ├── onboarding/
│   │   │   │   │   ├── AgeGenderFragment.java
│   │   │   │   │   ├── DesiredWeightFragment.java
│   │   │   │   │   ├── GoalActivityFragment.java
│   │   │   │   │   └── HeightWeightFragment.java
│   │   │   │   └── utils/
│   │   │   ├── res/
│   │   │   │   ├── layout/
│   │   │   │   ├── drawable/
│   │   │   │   └── values/
│   │   │   └── AndroidManifest.xml
│   │   └── test/
│   └── build.gradle
└── gradle/
```

## 🚀 Installation

### Prerequisites
- Android Studio Arctic Fox or later
- Android SDK (API level 24 or higher)
- Google Gemini API key
- Supabase account and project

### Setup Steps

1. **Clone the repository**
   ```bash
   git clone https://github.com/haythemgjima1/nutrisnap-android.git
   cd nutrisnap-android
   ```

2. **Open in Android Studio**
   - Open Android Studio
   - Select "Open an Existing Project"
   - Navigate to the cloned directory

3. **Configure API Keys**
   
   Create a `local.properties` file in the root directory (if not exists) and add:
   ```properties
   GEMINI_API_KEY=your_gemini_api_key_here
   SUPABASE_URL=your_supabase_url
   SUPABASE_ANON_KEY=your_supabase_anon_key
   ```

4. **Sync Gradle**
   - Click "Sync Project with Gradle Files"
   - Wait for dependencies to download

5. **Run the App**
   - Connect an Android device or start an emulator
   - Click "Run" or press Shift+F10

## 💻 Usage

### Getting Started

1. **Sign Up / Login**
   - Create a new account or login with existing credentials
   - Authentication is handled securely via Supabase

2. **Complete Onboarding**
   - Follow the onboarding wizard
   - Provide your personal information
   - Set your health and fitness goals
   - Define your target weight and activity level

3. **Dashboard**
   - View your daily nutrition summary
   - Track calories and macros
   - Access quick actions

### Main Features

#### AI Bro Chat
- Navigate to the AI Bro tab
- Ask questions about nutrition, diet, and fitness
- Get personalized recommendations
- Receive meal and exercise suggestions

#### Meal Journal
- Log your daily meals
- View nutritional breakdown
- Track your eating patterns
- Review meal history

#### Exercise Tracking
- Browse exercise library
- Log completed workouts
- Track exercise frequency
- Get AI-powered workout suggestions

#### Profile Management
- Update personal information
- Modify health goals
- Adjust target metrics
- View progress statistics

## 🔧 Configuration

### Gemini API Integration
The app uses Google's Gemini API for AI-powered features. Ensure you have:
- Valid API key
- Proper API quota
- Network connectivity

### Supabase Configuration
Backend services are powered by Supabase:
- **Authentication**: User sign-up and login
- **Database**: Store user profiles, meals, exercises
- **Real-time**: Live updates for journal entries

## 📱 Screenshots

*Add screenshots of your app here*

## 🏗️ Architecture

The app follows **MVVM (Model-View-ViewModel)** architecture:
- **Models**: Data classes representing app entities
- **Views**: Activities and Fragments for UI
- **ViewModels**: Business logic and state management
- **Repository**: Data layer abstraction

## 📝 License

This project is available for educational purposes.

## 👨‍💻 Author

**Haythem Gjima**
- GitHub: [@haythemgjima1](https://github.com/haythemgjima1)
- Email: haythemgjima5@gmail.com

## 🤝 Contributing

Contributions, issues, and feature requests are welcome!

## 🙏 Acknowledgments

- Google Gemini API for AI capabilities
- Supabase for backend infrastructure
- Material Design for UI components

---

**Note**: This app requires active internet connection for AI features and data synchronization.
