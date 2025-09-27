# HabbitMate 📱

A comprehensive Android habit tracking application built with Kotlin that helps users build healthy habits, track their mood, and monitor their activities.

## 🚀 Features

### 🎯 Habit Tracking
- **Single & Countable Habits**: Track simple daily habits or count-based habits with progress bars
- **Habit Categories**: Organize habits by Health, Personal, Work, and Fitness categories
- **Progress Visualization**: Visual progress bars and completion tracking
- **Habit Management**: Add, edit, delete, and toggle habits
- **Swipe-to-Delete**: Intuitive swipe gestures for habit management

### 😊 Mood Journal
- **Mood Tracking**: Log daily moods with 8 different emoji options (Happy, Sad, Angry, Excited, Calm, Tired, Confused, Surprised, Loved, Proud)
- **Mood Notes**: Add personal descriptions to mood entries
- **Mood History**: View mood trends over time with interactive charts
- **Swipe-to-Delete**: Remove mood entries with swipe gestures

### 🏃‍♂️ Activity Tracking
- **Multiple Activity Types**: Walking, Running, Cycling, Swimming, Yoga, Gym, Dance, Hiking
- **Activity Details**: Track duration, distance, and automatically calculated calories
- **Interactive Charts**: Pie charts showing calories burned by activity type
- **Activity Management**: Add, edit, and delete activities with dropdown selections

### ⚙️ Settings & Profile
- **Comprehensive Settings**: Notifications, Dark Mode, Data Sync, Reminders, Sound & Vibration
- **User Profile**: View personal statistics, achievements, and progress
- **Data Persistence**: All data saved locally using SharedPreferences
- **Logout Functionality**: Secure logout with data clearing

### 🎨 Onboarding Experience
- **3-Screen Introduction**: Beautiful onboarding screens introducing app features
- **Smooth Navigation**: Next/Skip buttons and Get Started functionality
- **Clean Design**: Modern UI without progress indicators for focused experience

## 🛠️ Technical Stack

- **Language**: Kotlin
- **UI Framework**: Android Views with Material Design
- **Charts**: MPAndroidChart for data visualization
- **Data Storage**: SharedPreferences with Gson serialization
- **Architecture**: Fragment-based navigation with ViewPager2
- **Notifications**: Custom notification service for reminders

## 📱 Screenshots

The app features a modern, intuitive interface with:
- Clean card-based layouts
- Gradient hero sections
- Interactive charts and progress bars
- Material Design components
- Consistent color scheme throughout

## 🏗️ Project Structure

```
app/
├── src/main/java/com/example/habbitmate/
│   ├── activities/
│   │   ├── MainActivity.kt
│   │   ├── OnboardingActivity.kt
│   │   └── ProfileActivity.kt
│   ├── adapters/
│   │   ├── ActivityAdapter.kt
│   │   ├── HabitAdapter.kt
│   │   ├── MoodAdapter.kt
│   │   └── OnboardingPagerAdapter.kt
│   ├── data/
│   │   ├── Activity.kt
│   │   ├── Habit.kt
│   │   ├── Mood.kt
│   │   └── PreferencesHelper.kt
│   ├── fragments/
│   │   ├── HabitTrackerFragment.kt
│   │   ├── MoodJournalFragment.kt
│   │   └── SettingsFragment.kt
│   └── services/
│       └── HydrationNotificationService.kt
└── src/main/res/
    ├── layout/ (All UI layouts)
    ├── drawable/ (Icons and backgrounds)
    ├── values/ (Colors, strings, themes)
    └── menu/ (Navigation menus)
```

## 🚀 Getting Started

### Prerequisites
- Android Studio Arctic Fox or later
- Android SDK 24 or higher
- Kotlin 1.8.0 or later

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/Sachithra-228/Habbit-Mate.git
   ```

2. **Open in Android Studio**
   - Launch Android Studio
   - Select "Open an existing project"
   - Navigate to the cloned repository folder

3. **Build and Run**
   - Sync project with Gradle files
   - Connect an Android device or start an emulator
   - Click the "Run" button or use `Ctrl+R`

### Building the APK

```bash
./gradlew assembleDebug
```

The APK will be generated in `app/build/outputs/apk/debug/`

## 📊 Key Features Implementation

### Habit Tracking System
- **Habit Types**: Single (toggle) and Countable (increment/decrement) habits
- **Progress Calculation**: Automatic progress bar updates based on completion status
- **Data Persistence**: Habits saved with completion status and counts

### Mood Journal System
- **Mood Selection**: Horizontal scrollable emoji selector
- **Mood Logging**: Add mood entries with optional descriptions
- **Chart Visualization**: Line charts showing mood trends over time

### Activity Tracking System
- **Activity Types**: 8 different activity categories with emoji representations
- **Calorie Calculation**: Automatic calorie calculation based on activity type, duration, and distance
- **Data Visualization**: Pie charts showing calorie distribution by activity type

### Settings Management
- **Preference Storage**: All settings persisted using SharedPreferences
- **Real-time Updates**: Settings changes applied immediately
- **User Profile**: Comprehensive profile with statistics and achievements

## 🎨 Design Philosophy

- **Material Design**: Consistent use of Material Design components
- **Color Scheme**: Primary red theme with supporting colors
- **Typography**: Clear, readable fonts with proper hierarchy
- **Spacing**: Consistent margins and padding throughout
- **Accessibility**: Proper content descriptions and touch targets

## 📈 Future Enhancements

- [ ] Cloud synchronization
- [ ] Social features and sharing
- [ ] Advanced analytics and insights
- [ ] Custom habit templates
- [ ] Push notifications for reminders
- [ ] Dark theme implementation
- [ ] Widget support
- [ ] Export/Import functionality

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👨‍💻 Author

**Sachithra-228**
- GitHub: [@Sachithra-228](https://github.com/Sachithra-228)

## 🙏 Acknowledgments

- Material Design for UI components
- MPAndroidChart for data visualization
- Android development community for best practices

---

**HabbitMate** - Building better habits, one day at a time! 🌟