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
    private lateinit var preferencesHelper: PreferencesHelper
    
    private val intervals = listOf(30, 60, 120, 180) // minutes
    private val intervalLabels = listOf("30 minutes", "1 hour", "2 hours", "3 hours")
    
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
        
        setupSwitch()
        setupSpinner()
        loadSettings()
        createNotificationChannel()
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
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            intervalLabels
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerInterval.adapter = adapter
        
        spinnerInterval.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedInterval = intervals[position]
                preferencesHelper.setHydrationInterval(selectedInterval)
                updateNotificationPreview()
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
    }
    
    private fun updateNotificationPreview() {
        val isEnabled = switchHydrationEnabled.isChecked
        val interval = intervals[spinnerInterval.selectedItemPosition]
        
        if (isEnabled) {
            textNotificationPreview.text = "Notification will appear every $interval minutes"
            textNotificationPreview.setTextColor(resources.getColor(R.color.light_blue, null))
        } else {
            textNotificationPreview.text = "Notifications are disabled"
            textNotificationPreview.setTextColor(resources.getColor(R.color.text_secondary, null))
        }
    }
    
    private fun startHydrationReminders() {
        val interval = intervals[spinnerInterval.selectedItemPosition]
        HydrationNotificationService.startService(requireContext(), interval)
    }
    
    private fun stopHydrationReminders() {
        HydrationNotificationService.stopService(requireContext())
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
