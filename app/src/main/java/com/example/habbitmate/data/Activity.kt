package com.example.habbitmate.data

import java.util.Date

data class Activity(
    val id: String,
    val name: String,
    val description: String? = null,
    val type: ActivityType = ActivityType.WALKING,
    val duration: Int = 0, // in minutes
    val distance: Double = 0.0, // in km
    val date: Date = Date(),
    val isCompleted: Boolean = false
) {
    // Calculate calories based on activity type, duration, and distance
    val calories: Int
        get() = when (type) {
            ActivityType.WALKING -> (duration * 3.5 + distance * 50).toInt()
            ActivityType.RUNNING -> (duration * 8.0 + distance * 80).toInt()
            ActivityType.CYCLING -> (duration * 6.0 + distance * 60).toInt()
            ActivityType.SWIMMING -> (duration * 7.0 + distance * 70).toInt()
            ActivityType.YOGA -> (duration * 2.5).toInt()
            ActivityType.GYM -> (duration * 5.0).toInt()
            ActivityType.DANCE -> (duration * 4.5).toInt()
            ActivityType.HIKING -> (duration * 6.5 + distance * 65).toInt()
        }
}

enum class ActivityType(val displayName: String, val emoji: String) {
    WALKING("Walking", "🚶"),
    RUNNING("Running", "🏃"),
    CYCLING("Cycling", "🚴"),
    SWIMMING("Swimming", "🏊"),
    YOGA("Yoga", "🧘"),
    GYM("Gym", "🏋️"),
    DANCE("Dance", "💃"),
    HIKING("Hiking", "🥾")
}
