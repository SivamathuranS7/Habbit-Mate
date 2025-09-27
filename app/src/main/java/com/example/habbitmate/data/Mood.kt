package com.example.habbitmate.data

import java.util.Date

data class Mood(
    val id: String,
    val moodType: MoodType,
    val date: Date,
    val note: String? = null
)

enum class MoodType(val emoji: String, val value: Int) {
    HAPPY("😊", 5),
    SAD("😢", 1),
    ANGRY("😠", 2),
    CALM("😌", 4),
    EXCITED("🤩", 5),
    TIRED("😴", 2),
    STRESSED("😰", 1),
    GRATEFUL("🙏", 4),
    CONFUSED("😕", 2),
    SURPRISED("😮", 3),
    LOVED("🥰", 5),
    PROUD("😎", 4)
}

data class MoodChartData(
    val date: String,
    val moodValue: Int
)
