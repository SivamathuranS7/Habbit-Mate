# HabbitMate - Personal Wellness Android App

A clean, minimal Android app focused on personal wellness with a light blue and orange theme. HabbitMate helps users track daily habits, journal their moods, and stay hydrated with smart reminders.

## Features

### 1. Daily Habit Tracker
- Card-style list of habits (Drink Water, Meditate, Walk, etc.)
- Checkbox/progress bar to mark completion
- Floating Action Button (FAB) in red to add new habits
- Persistent storage using SharedPreferences

### 2. Mood Journal
- Emoji selector with large tappable emoji buttons
- Simple date/time auto-logging
- Past moods shown in scrollable list with red accent headers
- Weekly mood trend chart using MPAndroidChart
- Line chart with red data line on white background

### 3. Hydration Reminder
- Settings page to choose reminder intervals (30min, 1hr, 2hr, 3hr)
- Switch/Toggle styled in red & white
- Clean notification preview mockup
- Background service for scheduled notifications

### 4. Navigation
- Bottom navigation bar with 3 icons: Habits, Mood, Settings
- Red selected item indicator, white background
- ViewPager2 for smooth screen transitions

## Design System

### Colors
- **Primary**: Light Blue (#87CEEB)
- **Secondary**: Orange (#FFA500)
- **Accent**: Red (#FF6B6B)
- **Background**: Light Gray (#FAFAFA)
- **Cards**: White (#FFFFFF)

### Typography
- **Font**: Roboto (Material Design)
- **Primary Text**: #212121
- **Secondary Text**: #757575

### Components
- **Buttons**: Rounded corners, flat style, red fill with white text
- **Cards**: White background, light shadow, red accent titles
- **Icons**: Simple, flat, line-style Material Icons

## Project Structure

```
app/src/main/
├── java/com/example/habbitmate/
│   ├── MainActivity.kt                    # Main activity with ViewPager2
│   ├── MainPagerAdapter.kt               # ViewPager2 adapter
│   ├── data/
│   │   ├── Habit.kt                      # Habit data model
│   │   ├── Mood.kt                       # Mood data model
│   │   └── PreferencesHelper.kt          # SharedPreferences helper
│   ├── fragments/
│   │   ├── HabitTrackerFragment.kt       # Habit tracking screen
│   │   ├── MoodJournalFragment.kt        # Mood journal screen
│   │   └── HydrationReminderFragment.kt  # Hydration settings screen
│   ├── adapters/
│   │   ├── HabitAdapter.kt               # RecyclerView adapter for habits
│   │   └── MoodAdapter.kt                # RecyclerView adapter for moods
│   └── services/
│       └── HydrationNotificationService.kt # Notification service
├── res/
│   ├── layout/
│   │   ├── activity_main.xml             # Main activity layout
│   │   ├── fragment_habit_tracker.xml    # Habit tracker layout
│   │   ├── fragment_mood_journal.xml     # Mood journal layout
│   │   ├── fragment_hydration_reminder.xml # Hydration settings layout
│   │   ├── item_habit.xml                # Habit item layout
│   │   └── item_mood.xml                 # Mood item layout
│   ├── drawable/
│   │   ├── card_background.xml           # Card background drawable
│   │   ├── mood_button_background.xml    # Mood button background
│   │   ├── spinner_background.xml        # Spinner background
│   │   ├── ic_habits.xml                 # Habits icon
│   │   ├── ic_mood.xml                   # Mood icon
│   │   ├── ic_settings.xml               # Settings icon
│   │   ├── ic_add.xml                    # Add icon
│   │   └── ic_water_drop.xml             # Water drop icon
│   ├── menu/
│   │   └── bottom_navigation_menu.xml    # Bottom navigation menu
│   ├── values/
│   │   ├── colors.xml                    # App color scheme
│   │   ├── strings.xml                   # String resources
│   │   └── themes.xml                    # App themes and styles
└── AndroidManifest.xml                   # App manifest with permissions
```

## Dependencies

- **AndroidX Core**: Core AndroidX libraries
- **Material Design**: Material Design components
- **ViewPager2**: For screen navigation
- **Navigation**: Android Navigation Component
- **MPAndroidChart**: For mood trend charts
- **RecyclerView**: For lists
- **Fragment**: Fragment support
- **Lifecycle**: ViewModel and LiveData
- **Gson**: JSON serialization

## Key Features Implementation

### Data Persistence
- Uses SharedPreferences with Gson for JSON serialization
- Stores habits, moods, and hydration settings
- Automatic data loading and saving

### Notifications
- Background service for hydration reminders
- Configurable intervals (30min to 3hrs)
- Material Design notification styling
- Proper notification channels for Android 8+

### Charts
- MPAndroidChart integration for mood trends
- Line chart with red data visualization
- Responsive design for different screen sizes

### UI/UX
- Material Design 3 components
- Consistent color scheme throughout
- Smooth animations and transitions
- Responsive layouts for phones and tablets

## Usage

1. **Habit Tracking**: Tap checkboxes to mark habits as complete
2. **Mood Journal**: Tap emoji buttons to log your current mood
3. **Hydration Reminders**: Enable notifications and set your preferred interval
4. **Navigation**: Use bottom navigation to switch between screens

## Building the App

1. Clone the repository
2. Open in Android Studio
3. Sync Gradle files
4. Build and run on device/emulator

## Requirements

- Android API 24+ (Android 7.0)
- Target SDK 36
- Kotlin 1.8+
- Gradle 8.0+

## Permissions

- `POST_NOTIFICATIONS`: For hydration reminder notifications
- `SCHEDULE_EXACT_ALARM`: For precise notification scheduling
- `USE_EXACT_ALARM`: For exact alarm usage

## Future Enhancements

- Habit streak tracking
- Mood analytics and insights
- Custom habit creation
- Data export/import
- Widget support
- Dark theme support
- Habit categories and tags
