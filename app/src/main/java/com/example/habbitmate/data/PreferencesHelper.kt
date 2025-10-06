package com.example.habbitmate.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Date

class PreferencesHelper(context: Context) {
    private val context: Context = context.applicationContext
    private val prefs: SharedPreferences = this.context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val PREFS_NAME = "habbitmate_prefs"
        private const val KEY_HABITS = "habits"
        private const val KEY_MOODS = "moods"
        private const val KEY_ACTIVITIES = "activities"
        private const val KEY_HYDRATION_ENABLED = "hydration_enabled"
        private const val KEY_HYDRATION_INTERVAL = "hydration_interval"
        
        // Settings keys
        private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
        private const val KEY_DARK_MODE_ENABLED = "dark_mode_enabled"
        private const val KEY_DATA_SYNC_ENABLED = "data_sync_enabled"
        private const val KEY_HABIT_REMINDERS_ENABLED = "habit_reminders_enabled"
        private const val KEY_MOOD_REMINDERS_ENABLED = "mood_reminders_enabled"
        private const val KEY_ACTIVITY_TRACKING_ENABLED = "activity_tracking_enabled"
        private const val KEY_WEEKLY_REPORTS_ENABLED = "weekly_reports_enabled"
        private const val KEY_SOUND_EFFECTS_ENABLED = "sound_effects_enabled"
        private const val KEY_VIBRATION_ENABLED = "vibration_enabled"
        private const val KEY_NOTIFICATION_MESSAGE = "notification_message"
        const val ACTION_ACTIVITIES_UPDATED = "com.example.habbitmate.ACTION_ACTIVITIES_UPDATED"
    }

    // Habit management
    fun saveHabits(habits: List<Habit>) {
        val habitsJson = gson.toJson(habits)
        prefs.edit().putString(KEY_HABITS, habitsJson).apply()
    }

    fun getHabits(): List<Habit> {
        val habitsJson = prefs.getString(KEY_HABITS, null)
        return if (habitsJson != null) {
            try {
                val type = object : TypeToken<List<Habit>>() {}.type
                val habits: List<Habit> = gson.fromJson(habitsJson, type) ?: emptyList()
                // Ensure all habits have proper default values
                migrateHabitsIfNeeded(habits)
            } catch (e: Exception) {
                // If parsing fails, return default habits
                getDefaultHabits()
            }
        } else {
            getDefaultHabits()
        }
    }
    
    private fun migrateHabitsIfNeeded(habits: List<Habit>): List<Habit> {
        val migratedHabits = habits.map { habit ->
            // Create a new habit with all fields properly set
            Habit(
                id = habit.id,
                name = habit.name,
                description = habit.description,
                category = habit.category ?: HabitCategory.HEALTH,
                type = habit.type ?: HabitType.SINGLE,
                targetCount = if (habit.targetCount <= 0) 1 else habit.targetCount,
                currentCount = habit.currentCount.coerceAtLeast(0),
                isCompleted = habit.isCompleted,
                completedDate = habit.completedDate,
                streak = habit.streak,
                targetFrequency = habit.targetFrequency
            )
        }
        
        // Save migrated habits back to preferences
        if (migratedHabits != habits) {
            saveHabits(migratedHabits)
        }
        
        return migratedHabits
    }

    fun updateHabitCompletion(habitId: String, isCompleted: Boolean) {
        val habits = getHabits().toMutableList()
        val habitIndex = habits.indexOfFirst { it.id == habitId }
        if (habitIndex != -1) {
            val currentHabit = habits[habitIndex]
            // Only update if the completion state actually changed
            if (currentHabit.isCompleted != isCompleted) {
                habits[habitIndex] = currentHabit.copy(
                    isCompleted = isCompleted,
                    completedDate = if (isCompleted) Date() else null
                )
                saveHabits(habits)
            }
        }
    }

    fun updateHabitCount(habitId: String, increment: Boolean = true) {
        val habits = getHabits().toMutableList()
        val habitIndex = habits.indexOfFirst { it.id == habitId }
        if (habitIndex != -1) {
            val currentHabit = habits[habitIndex]
            val targetCount = currentHabit.targetCount.takeIf { it > 0 } ?: 1
            val newCount = if (increment) {
                // increment but do not exceed targetCount
                (currentHabit.currentCount + 1).coerceAtMost(targetCount)
            } else {
                (currentHabit.currentCount - 1).coerceAtLeast(0)
            }

            val isCompleted = newCount >= targetCount

            habits[habitIndex] = currentHabit.copy(
                currentCount = newCount,
                isCompleted = isCompleted,
                completedDate = if (isCompleted && !currentHabit.isCompleted) Date() else if (!isCompleted) null else currentHabit.completedDate
            )
            saveHabits(habits)
        }
    }

    fun updateHabit(habitId: String, updatedHabit: Habit) {
        val habits = getHabits().toMutableList()
        val index = habits.indexOfFirst { it.id == habitId }
        if (index != -1) {
            habits[index] = updatedHabit
            saveHabits(habits)
        }
    }

    // Mood management
    fun saveMoods(moods: List<Mood>) {
        val moodsJson = gson.toJson(moods)
        prefs.edit().putString(KEY_MOODS, moodsJson).apply()
    }

    fun getMoods(): List<Mood> {
        val moodsJson = prefs.getString(KEY_MOODS, null)
        return if (moodsJson != null) {
            val type = object : TypeToken<List<Mood>>() {}.type
            gson.fromJson(moodsJson, type) ?: emptyList()
        } else {
            emptyList()
        }
    }

    fun addMood(mood: Mood) {
        val moods = getMoods().toMutableList()
        moods.add(mood)
        saveMoods(moods)
    }

    // Activity management
    fun saveActivities(activities: List<Activity>) {
        val activitiesJson = gson.toJson(activities)
        prefs.edit().putString(KEY_ACTIVITIES, activitiesJson).apply()
        // Notify listeners that activities changed
        try {
            val intent = android.content.Intent(ACTION_ACTIVITIES_UPDATED)
            this.context.sendBroadcast(intent)
        } catch (e: Exception) {
            // ignore broadcast errors
        }
    }

    fun getActivities(): List<Activity> {
        val activitiesJson = prefs.getString(KEY_ACTIVITIES, null)
        return if (activitiesJson != null) {
            try {
                val type = object : TypeToken<List<Activity>>() {}.type
                gson.fromJson(activitiesJson, type) ?: emptyList()
            } catch (e: Exception) {
                emptyList()
            }
        } else {
            emptyList()
        }
    }

    fun addActivity(activity: Activity) {
        val activities = getActivities().toMutableList()
        activities.add(activity)
        saveActivities(activities)
    }

    fun updateActivity(activityId: String, updatedActivity: Activity) {
        val activities = getActivities().toMutableList()
        val index = activities.indexOfFirst { it.id == activityId }
        if (index != -1) {
            activities[index] = updatedActivity
            saveActivities(activities)
        }
    }

    fun deleteActivity(activityId: String) {
        val activities = getActivities().toMutableList()
        activities.removeAll { it.id == activityId }
        saveActivities(activities)
    }

    // Hydration settings
    fun setHydrationEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_HYDRATION_ENABLED, enabled).apply()
    }

    fun isHydrationEnabled(): Boolean {
        return prefs.getBoolean(KEY_HYDRATION_ENABLED, false)
    }

    fun setHydrationInterval(intervalMinutes: Int) {
        prefs.edit().putInt(KEY_HYDRATION_INTERVAL, intervalMinutes).apply()
    }

    fun getHydrationInterval(): Int {
        return prefs.getInt(KEY_HYDRATION_INTERVAL, 60) // Default 1 hour
    }
    
    // Settings management
    fun setNotificationsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_NOTIFICATIONS_ENABLED, enabled).apply()
    }
    
    fun isNotificationsEnabled(): Boolean {
        return prefs.getBoolean(KEY_NOTIFICATIONS_ENABLED, true)
    }
    
    fun setDarkModeEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_DARK_MODE_ENABLED, enabled).apply()
    }
    
    fun isDarkModeEnabled(): Boolean {
        return prefs.getBoolean(KEY_DARK_MODE_ENABLED, false)
    }
    
    fun setDataSyncEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_DATA_SYNC_ENABLED, enabled).apply()
    }
    
    fun isDataSyncEnabled(): Boolean {
        return prefs.getBoolean(KEY_DATA_SYNC_ENABLED, true)
    }
    
    fun setHabitRemindersEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_HABIT_REMINDERS_ENABLED, enabled).apply()
    }
    
    fun isHabitRemindersEnabled(): Boolean {
        return prefs.getBoolean(KEY_HABIT_REMINDERS_ENABLED, true)
    }
    
    fun setMoodRemindersEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_MOOD_REMINDERS_ENABLED, enabled).apply()
    }
    
    fun isMoodRemindersEnabled(): Boolean {
        return prefs.getBoolean(KEY_MOOD_REMINDERS_ENABLED, true)
    }
    
    fun setActivityTrackingEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_ACTIVITY_TRACKING_ENABLED, enabled).apply()
    }
    
    fun isActivityTrackingEnabled(): Boolean {
        return prefs.getBoolean(KEY_ACTIVITY_TRACKING_ENABLED, true)
    }
    
    fun setWeeklyReportsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_WEEKLY_REPORTS_ENABLED, enabled).apply()
    }
    
    fun isWeeklyReportsEnabled(): Boolean {
        return prefs.getBoolean(KEY_WEEKLY_REPORTS_ENABLED, true)
    }
    
    fun setSoundEffectsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_SOUND_EFFECTS_ENABLED, enabled).apply()
    }
    
    fun isSoundEffectsEnabled(): Boolean {
        return prefs.getBoolean(KEY_SOUND_EFFECTS_ENABLED, true)
    }
    
    fun setVibrationEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_VIBRATION_ENABLED, enabled).apply()
    }
    
    fun isVibrationEnabled(): Boolean {
        return prefs.getBoolean(KEY_VIBRATION_ENABLED, true)
    }
    
    // Notification message (customizable by user)
    fun setNotificationMessage(message: String) {
        prefs.edit().putString(KEY_NOTIFICATION_MESSAGE, message).apply()
    }

    fun getNotificationMessage(): String? {
        return prefs.getString(KEY_NOTIFICATION_MESSAGE, null)
    }
    
    // Clear all data (useful for debugging or fresh start)
    fun clearAllData() {
        prefs.edit().clear().apply()
    }

    private fun getDefaultHabits(): List<Habit> {
        return listOf(
            Habit("1", "Drink Water", "Stay hydrated throughout the day", HabitCategory.HEALTH, HabitType.COUNTABLE, 8),
            Habit("2", "Meditate", "Take 10 minutes to clear your mind", HabitCategory.PERSONAL, HabitType.SINGLE),
            Habit("3", "Walk", "Take a 30-minute walk", HabitCategory.FITNESS, HabitType.SINGLE),
            Habit("4", "Exercise", "Get your body moving", HabitCategory.FITNESS, HabitType.SINGLE),
            Habit("5", "Read", "Read for at least 20 minutes", HabitCategory.EDUCATION, HabitType.SINGLE),
            Habit("6", "Sleep Early", "Go to bed before 11 PM", HabitCategory.HEALTH, HabitType.SINGLE)
        )
    }
}
