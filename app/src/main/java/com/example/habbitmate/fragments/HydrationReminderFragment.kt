package com.example.habbitmate.fragments

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Switch
import android.widget.TextView
import androidx.core.app.NotificationCompat
import androidx.fragment.app.Fragment
import com.example.habbitmate.R
import com.example.habbitmate.data.PreferencesHelper
import com.example.habbitmate.services.HydrationNotificationService

class HydrationReminderFragment : Fragment() {
    
    private lateinit var switchHydrationEnabled: Switch
    private lateinit var spinnerInterval: Spinner
    private lateinit var textNotificationPreview: TextView
    private lateinit var textHydrationStatus: TextView
    private lateinit var textHydrationTips: TextView
    private lateinit var btnHydrationBack: Button
    private lateinit var preferencesHelper: PreferencesHelper
    
    private val intervals = listOf(30, 60, 120, 180) // minutes
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_hydration_reminder, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        preferencesHelper = PreferencesHelper(requireContext())
        
        switchHydrationEnabled = view.findViewById(R.id.switchHydrationEnabled)
        spinnerInterval = view.findViewById(R.id.spinnerInterval)
        textNotificationPreview = view.findViewById(R.id.textNotificationPreview)
    textHydrationStatus = view.findViewById(R.id.textHydrationStatus)
    textHydrationTips = view.findViewById(R.id.textHydrationTips)
    btnHydrationBack = view.findViewById(R.id.btnHydrationBack)
        
        setupSwitch()
        setupSpinner()
        loadSettings()
        loadHydrationTips()
        createNotificationChannel()

        btnHydrationBack.setOnClickListener {
            // If hosted inside an activity (HydrationActivity), finish it; otherwise, pop back stack
            activity?.finish()
        }
    }
    
    private fun setupSwitch() {
        switchHydrationEnabled.setOnCheckedChangeListener { _, isChecked ->
            preferencesHelper.setHydrationEnabled(isChecked)
            updateNotificationPreview()
            
            if (isChecked) {
                startHydrationReminders()
            } else {
                stopHydrationReminders()
            }
        }
    }
    
    private fun setupSpinner() {
        val labels = intervals.map { formatInterval(it) }
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            labels
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerInterval.adapter = adapter
        
        spinnerInterval.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedInterval = intervals[position]
                preferencesHelper.setHydrationInterval(selectedInterval)
                updateNotificationPreview()
                updateStatusText()
            }
            
            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        }
    }
    
    private fun loadSettings() {
        switchHydrationEnabled.isChecked = preferencesHelper.isHydrationEnabled()
        
        val currentInterval = preferencesHelper.getHydrationInterval()
        val intervalIndex = intervals.indexOf(currentInterval)
        if (intervalIndex != -1) {
            spinnerInterval.setSelection(intervalIndex)
        }
        
        updateNotificationPreview()
        updateStatusText()
    }
    
    private fun updateNotificationPreview() {
        val isEnabled = switchHydrationEnabled.isChecked
        val interval = intervals[spinnerInterval.selectedItemPosition]

        if (isEnabled) {
            textNotificationPreview.text = "Notification will appear every ${formatInterval(interval)}"
            textNotificationPreview.setTextColor(androidx.core.content.ContextCompat.getColor(requireContext(), R.color.light_blue))
        } else {
            textNotificationPreview.text = "Notifications are disabled"
            textNotificationPreview.setTextColor(androidx.core.content.ContextCompat.getColor(requireContext(), R.color.text_secondary))
        }
    }
    
    private fun startHydrationReminders() {
        val interval = intervals[spinnerInterval.selectedItemPosition]
        HydrationNotificationService.startService(requireContext(), interval)
        updateStatusText()
    }
    
    private fun stopHydrationReminders() {
        HydrationNotificationService.stopService(requireContext())
        updateStatusText()
    }

    private fun updateStatusText() {
        val enabled = preferencesHelper.isHydrationEnabled()
        if (enabled) {
            val interval = preferencesHelper.getHydrationInterval()
            textHydrationStatus.text = "Active — every ${formatInterval(interval)}"
            textHydrationStatus.setTextColor(androidx.core.content.ContextCompat.getColor(requireContext(), R.color.light_blue))
        } else {
            textHydrationStatus.text = "Inactive"
            textHydrationStatus.setTextColor(androidx.core.content.ContextCompat.getColor(requireContext(), R.color.text_secondary))
        }
    }

    private fun loadHydrationTips() {
        val tips = resources.getStringArray(R.array.hydration_tips)
        // Show up to 4 unique tips (random order) each time the fragment is opened
        if (tips.isNotEmpty()) {
            val shuffled = tips.toList().shuffled()
            val selected = shuffled.take(4)
            val bullets = selected.joinToString(separator = "\n") { "• $it" }
            textHydrationTips.text = bullets
        }
    }

    private fun formatInterval(minutes: Int): String {
        if (minutes <= 0) return "0 minutes"
        val hours = minutes / 60
        val mins = minutes % 60
        return when {
            hours > 0 && mins == 0 -> if (hours == 1) "$hours hour" else "$hours hours"
            hours > 0 && mins > 0 -> if (hours == 1) "$hours hour $mins minutes" else "$hours hours $mins minutes"
            else -> "$minutes minutes"
        }
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                HydrationNotificationService.CHANNEL_ID,
                "Hydration Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Reminds you to stay hydrated"
            }
            
            val notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
