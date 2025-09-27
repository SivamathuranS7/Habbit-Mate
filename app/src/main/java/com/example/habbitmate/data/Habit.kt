package com.example.habbitmate.data

import java.util.Date

data class Habit(
    val id: String,
    val name: String,
    val description: String? = null,
    val category: HabitCategory = HabitCategory.HEALTH,
    val type: HabitType = HabitType.SINGLE,
    val targetCount: Int = 1,
    val currentCount: Int = 0,
    val isCompleted: Boolean = false,
    val completedDate: Date? = null,
    val streak: Int = 0,
    val targetFrequency: HabitFrequency = HabitFrequency.DAILY
)

enum class HabitCategory(val displayName: String) {
    HEALTH("Health"),
    EDUCATION("Education"),
    FITNESS("Fitness"),
    WORK("Work"),
    PERSONAL("Personal"),
    SOCIAL("Social"),
    CREATIVE("Creative"),
    FINANCE("Finance")
}

enum class HabitType(val displayName: String) {
    SINGLE("Single"),
    COUNTABLE("Countable")
}

enum class HabitFrequency {
    DAILY, WEEKLY, MONTHLY
}

data class HabitCompletion(
    val habitId: String,
    val date: Date,
    val isCompleted: Boolean,
    val count: Int = 1
)
