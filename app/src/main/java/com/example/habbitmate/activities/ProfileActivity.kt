package com.example.habbitmate.activities

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.habbitmate.R
import com.example.habbitmate.data.PreferencesHelper
import java.text.SimpleDateFormat
import java.util.*

class ProfileActivity : AppCompatActivity() {
    
    private lateinit var preferencesHelper: PreferencesHelper
    
    private lateinit var btnBack: ImageButton
    private lateinit var textUserName: TextView
    private lateinit var textUserEmail: TextView
    private lateinit var textJoinDate: TextView
    private lateinit var textTotalHabits: TextView
    private lateinit var textTotalMoods: TextView
    private lateinit var textTotalActivities: TextView
    private lateinit var textStreakDays: TextView
    private lateinit var textAchievements: TextView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        
        preferencesHelper = PreferencesHelper(this)
        
        setupViews()
        setupBackButton()
        loadProfileData()
    }
    
    private fun setupViews() {
        btnBack = findViewById(R.id.btnBack)
        textUserName = findViewById(R.id.textUserName)
        textUserEmail = findViewById(R.id.textUserEmail)
        textJoinDate = findViewById(R.id.textJoinDate)
        textTotalHabits = findViewById(R.id.textTotalHabits)
        textTotalMoods = findViewById(R.id.textTotalMoods)
        textTotalActivities = findViewById(R.id.textTotalActivities)
        textStreakDays = findViewById(R.id.textStreakDays)
        textAchievements = findViewById(R.id.textAchievements)
    }
    
    private fun setupBackButton() {
        btnBack.setOnClickListener {
            finish()
        }
    }
    
    private fun loadProfileData() {
        // Load user data from preferences
        val habits = preferencesHelper.getHabits()
        val moods = preferencesHelper.getMoods()
        val activities = preferencesHelper.getActivities()
        
        // Set user information (you can customize these)
        textUserName.text = "Sivamathuran" // Default user name
        textUserEmail.text = "john.doe@example.com" // Default email
        textJoinDate.text = "Joined on ${SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date())}"
        
        // Note: Build Date is static in layout (Build Date: September 2025)
        textTotalHabits.text = habits.size.toString()
        textTotalMoods.text = moods.size.toString()
        textTotalActivities.text = activities.size.toString()
        
        // Calculate streak (simplified - you can implement more complex logic)
        val completedHabits = habits.count { it.isCompleted }
        val streakDays = if (completedHabits > 0) {
            (completedHabits * 1.5).toInt() // Simple calculation
        } else {
            0
        }
        textStreakDays.text = streakDays.toString()
        
        // Calculate achievements
        val achievements = calculateAchievements(habits.size, moods.size, activities.size, streakDays)
        textAchievements.text = achievements.toString()
    }
    
    private fun calculateAchievements(habitCount: Int, moodCount: Int, activityCount: Int, streak: Int): Int {
        var achievements = 0
        
        // Habit achievements
        if (habitCount >= 1) achievements++
        if (habitCount >= 5) achievements++
        if (habitCount >= 10) achievements++
        
        // Mood achievements
        if (moodCount >= 1) achievements++
        if (moodCount >= 10) achievements++
        if (moodCount >= 50) achievements++
        
        // Activity achievements
        if (activityCount >= 1) achievements++
        if (activityCount >= 5) achievements++
        if (activityCount >= 20) achievements++
        
        // Streak achievements
        if (streak >= 1) achievements++
        if (streak >= 7) achievements++
        if (streak >= 30) achievements++
        
        return achievements
    }
}
