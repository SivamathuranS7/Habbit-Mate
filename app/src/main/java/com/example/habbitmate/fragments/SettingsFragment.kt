package com.example.habbitmate.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.habbitmate.R
import com.example.habbitmate.activities.OnboardingActivity
import com.example.habbitmate.activities.ProfileActivity
import com.example.habbitmate.data.PreferencesHelper
import java.util.*

class SettingsFragment : Fragment() {
    
    private lateinit var preferencesHelper: PreferencesHelper
    
    // Hero section views
    private lateinit var textGreeting: TextView
    private lateinit var textCurrentTime: TextView
    
    // Settings switches and buttons
    private lateinit var btnViewProfile: Button
    private lateinit var switchNotifications: Switch
    private lateinit var switchDarkMode: Switch
    private lateinit var switchDataSync: Switch
    private lateinit var switchHabitReminders: Switch
    private lateinit var switchMoodReminders: Switch
    private lateinit var switchActivityTracking: Switch
    private lateinit var switchWeeklyReports: Switch
    private lateinit var switchSoundEffects: Switch
    private lateinit var switchVibration: Switch
    private lateinit var btnLogout: Button
    private lateinit var textNotificationPreview: TextView
    private lateinit var btnEditNotificationMessage: Button
    private lateinit var btnOpenHydrationSettings: Button
    
    private val timeHandler = Handler(Looper.getMainLooper())
    private val timeRunnable = object : Runnable {
        override fun run() {
            updateTime()
            timeHandler.postDelayed(this, 60000) // Update every minute
        }
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        preferencesHelper = PreferencesHelper(requireContext())
        
        // Initialize views
        textGreeting = view.findViewById(R.id.textGreeting)
        textCurrentTime = view.findViewById(R.id.textCurrentTime)
        btnViewProfile = view.findViewById(R.id.btnViewProfile)
        switchNotifications = view.findViewById(R.id.switchNotifications)
        switchDarkMode = view.findViewById(R.id.switchDarkMode)
        switchDataSync = view.findViewById(R.id.switchDataSync)
        switchHabitReminders = view.findViewById(R.id.switchHabitReminders)
        switchMoodReminders = view.findViewById(R.id.switchMoodReminders)
        switchActivityTracking = view.findViewById(R.id.switchActivityTracking)
        switchWeeklyReports = view.findViewById(R.id.switchWeeklyReports)
        switchSoundEffects = view.findViewById(R.id.switchSoundEffects)
    switchVibration = view.findViewById(R.id.switchVibration)
    textNotificationPreview = view.findViewById(R.id.textNotificationPreview)
    btnEditNotificationMessage = view.findViewById(R.id.btnEditNotificationMessage)
    btnOpenHydrationSettings = view.findViewById(R.id.btnOpenHydrationSettings)
    btnLogout = view.findViewById(R.id.btnLogout)
        
        setupHeroSection()
        setupProfileButton()
        setupSettings()
        setupLogout()
        
        // Start time updates
        updateTime()
        timeHandler.postDelayed(timeRunnable, 60000)
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        timeHandler.removeCallbacks(timeRunnable)
    }
    
    private fun setupHeroSection() {
        updateGreeting()
    }
    
    private fun updateGreeting() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        
        val greeting = when (hour) {
            in 5..11 -> "Good Morning!"
            in 12..17 -> "Good Afternoon!"
            in 18..21 -> "Good Evening!"
            else -> "Good Night!"
        }
        
        textGreeting.text = greeting
    }
    
    private fun updateTime() {
        val currentTime = DateFormat.format("h:mm a", Date()).toString()
        textCurrentTime.text = currentTime
    }
    
    private fun setupProfileButton() {
        btnViewProfile.setOnClickListener {
            val intent = Intent(requireContext(), ProfileActivity::class.java)
            startActivity(intent)
        }
    }
    
    private fun setupSettings() {
        // Load saved preferences
        switchNotifications.isChecked = preferencesHelper.isNotificationsEnabled()
        switchDarkMode.isChecked = preferencesHelper.isDarkModeEnabled()
        switchDataSync.isChecked = preferencesHelper.isDataSyncEnabled()
        switchHabitReminders.isChecked = preferencesHelper.isHabitRemindersEnabled()
        switchMoodReminders.isChecked = preferencesHelper.isMoodRemindersEnabled()
        switchActivityTracking.isChecked = preferencesHelper.isActivityTrackingEnabled()
        switchWeeklyReports.isChecked = preferencesHelper.isWeeklyReportsEnabled()
        switchSoundEffects.isChecked = preferencesHelper.isSoundEffectsEnabled()
        switchVibration.isChecked = preferencesHelper.isVibrationEnabled()
        
        // Setup listeners
        switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            preferencesHelper.setNotificationsEnabled(isChecked)
            updateNotificationPreviewVisibility()
        }
        
        switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            preferencesHelper.setDarkModeEnabled(isChecked)
        }
        
        switchDataSync.setOnCheckedChangeListener { _, isChecked ->
            preferencesHelper.setDataSyncEnabled(isChecked)
        }
        
        switchHabitReminders.setOnCheckedChangeListener { _, isChecked ->
            preferencesHelper.setHabitRemindersEnabled(isChecked)
            updateNotificationPreviewVisibility()
        }
        
        switchMoodReminders.setOnCheckedChangeListener { _, isChecked ->
            preferencesHelper.setMoodRemindersEnabled(isChecked)
            updateNotificationPreviewVisibility()
        }
        
        switchActivityTracking.setOnCheckedChangeListener { _, isChecked ->
            preferencesHelper.setActivityTrackingEnabled(isChecked)
            updateNotificationPreviewVisibility()
        }
        
        switchWeeklyReports.setOnCheckedChangeListener { _, isChecked ->
            preferencesHelper.setWeeklyReportsEnabled(isChecked)
            updateNotificationPreviewVisibility()
        }
        
        switchSoundEffects.setOnCheckedChangeListener { _, isChecked ->
            preferencesHelper.setSoundEffectsEnabled(isChecked)
        }
        
        switchVibration.setOnCheckedChangeListener { _, isChecked ->
            preferencesHelper.setVibrationEnabled(isChecked)
        }

        btnEditNotificationMessage.setOnClickListener {
            showEditNotificationDialog()
        }

        btnOpenHydrationSettings.setOnClickListener {
            // Open a dedicated activity that hosts the HydrationReminderFragment
            val intent = android.content.Intent(requireContext(), com.example.habbitmate.activities.HydrationActivity::class.java)
            startActivity(intent)
        }

        // Load and initialize notification preview
        loadNotificationPreview()
        updateNotificationPreviewVisibility()
    }

    private fun loadNotificationPreview() {
        val custom = preferencesHelper.getNotificationMessage()
        val message = custom ?: getString(R.string.hydration_notification_text)
        textNotificationPreview.text = message
    }

    private fun updateNotificationPreviewVisibility() {
        val notificationsEnabled = preferencesHelper.isNotificationsEnabled()
        val anyReminderEnabled = preferencesHelper.isHabitRemindersEnabled() ||
                preferencesHelper.isMoodRemindersEnabled() ||
                preferencesHelper.isActivityTrackingEnabled()

        val shouldShow = notificationsEnabled && anyReminderEnabled

        textNotificationPreview.visibility = if (shouldShow) View.VISIBLE else View.GONE
        btnEditNotificationMessage.visibility = if (shouldShow) View.VISIBLE else View.GONE
    }

    private fun showEditNotificationDialog() {
        val current = preferencesHelper.getNotificationMessage() ?: getString(R.string.hydration_notification_text)
        val editText = android.widget.EditText(requireContext()).apply {
            setText(current)
            setSelection(text.length)
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Edit Notification Message")
            .setView(editText)
            .setPositiveButton(getString(R.string.save)) { _, _ ->
                val newMsg = editText.text.toString().trim()
                if (newMsg.isNotEmpty()) {
                    preferencesHelper.setNotificationMessage(newMsg)
                    textNotificationPreview.text = newMsg
                }
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }
    
    private fun setupLogout() {
        btnLogout.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Logout") { _, _ ->
                        // Note: we intentionally do NOT clear all app data on logout to avoid
                        // deleting user-created habits, moods, and activities.
                        // If you need a full factory reset, add a separate clear-data action.

                        // Navigate to onboarding (or login) screen
                        val intent = Intent(requireContext(), OnboardingActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        requireActivity().finish()
                    }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }
}
