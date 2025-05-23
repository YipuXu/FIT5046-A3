# FitLife

FitLife is a mobile fitness management application designed to help users track workouts, find fitness locations, manage fitness plans, and get AI coaching.

## Key Features

FitLife offers the following core features:

- **User Authentication**: Secure registration and login process.
- **Home**: The app's starting page, providing quick access to common features like showing dashboard, editing profile, recording workouts, viewing exercise recommendations, and music playback.
- **Map**: Integrates with Google Maps to show nearby fitness places, distinguished by color markers. Supports filtering by place type and searching based on the user's current location or a specified location (like Monash Clayton campus). Displays real photos and ratings of places.
- **Fitness Calendar**: View and manage fitness plans. Users can add, view, and delete fitness events on specific dates. Supports calendar integration.
- **Record Workout**: Detailed recording of each workout session, including type, duration, date, time, intensity, and notes. Supports quickly creating records from calendar plans and estimating calories burned.
- **Profile**: Shows user basic information, selected fitness tags, recent workout history, total workout days, and continuous workout streak. Users can edit their profile and view detailed workout records.
- **All Recent Records**: View a complete list of workout records, sorted by date. Users can see details for each record.
- **AI Coach**: Provides AI-powered fitness advice and guidance.
- **Settings**: Includes various app settings options such as editing profile, about us, help & feedback, change password, privacy policy, terms of service, and accessibility settings.

## Technology Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose
- **Architecture**: MVVM (ViewModel)
- **Local Data Storage**: Room Persistence Library
- **Backend Service**: Firebase (User Authentication)
- **Map Services**: Google Maps API, Google Places API
- **Asynchronous Operations**: Kotlin Coroutines
- **Image Loading**: Coil

## Page Overview

The FitLife app includes the following main pages:

- LoginScreen
- RegisterScreen
- HomeScreen
- MapScreen
- FitnessCalendarScreen
- RecordTrainingScreen
- ProfileScreen
- ProfileEditScreen
- SettingsScreen
- AboutUsScreen
- HelpFeedbackScreen
- ChangePasswordScreen
- AICoachScreen
- PrivacyPolicyScreen
- TermsOfServiceScreen
- AccessibilityScreen
- AllRecentRecordsScreen

## Getting Started

### System Requirements

- **Android Studio**: Arctic Fox (2020.3.1) or later
- **Android SDK**: API Level 26 (Oreo) or higher
- **JDK**: 11 or higher

### Installation and Configuration

1.  **Clone the repository**:
    ```bash
    git clone https://github.com/YipuXu/FIT5046-A3.git
    ```
2.  **Open the project in Android Studio**: Wait for Gradle sync to complete.

### Google Login for Multiple Developers

To ensure all developers can use the Google login feature (especially during debug builds), you need to add each developer's SHA-1 and SHA-256 certificate fingerprints in the Android app settings on the Firebase project console. Follow these steps to get the fingerprints:
   ```bash
   ./gradlew signingReport
   ```
   Or use the `keytool` command.

### Running the App

Connect your Android device or start an emulator in Android Studio, then click the Run button.

## Data Storage

- **Local Data**: The app uses a Room database to store workout records, user preferences (like fitness tags), calendar events, etc., locally on the user's device.
- **Cloud Data**: Firebase is mainly used for user authentication, personal information, feedback, and other important information. Currently, other user data (like workout details) is primarily stored locally.

## Contribution

Contributions are welcome! If you have any suggestions or find bugs, please submit an Issue or Pull Request.

## License

None 