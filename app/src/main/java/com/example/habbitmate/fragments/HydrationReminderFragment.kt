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
import android.widget.CheckBox
import android.widget.TextView
import android.widget.EditText
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
    private lateinit var cbMon: CheckBox
    private lateinit var cbTue: CheckBox
    private lateinit var cbWed: CheckBox
    private lateinit var cbThu: CheckBox
    private lateinit var cbFri: CheckBox
    private lateinit var editGoalMin: EditText
    private lateinit var editGoalMax: EditText
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
    cbMon = view.findViewById(R.id.cbMon)
    cbTue = view.findViewById(R.id.cbTue)
    cbWed = view.findViewById(R.id.cbWed)
    cbThu = view.findViewById(R.id.cbThu)
    cbFri = view.findViewById(R.id.cbFri)
    editGoalMin = view.findViewById(R.id.editGoalMin)
    editGoalMax = view.findViewById(R.id.editGoalMax)
        
        setupSwitch()
        setupSpinner()
    setupWeekdaySelectors()
    setupGoalInputs()
        loadSettings()
        loadHydrationTips()
        createNotificationChannel()

        // Back button removed; navigation handled by hosting activity/toolbar
    }

    private fun setupWeekdaySelectors() {
    val checkboxes = listOf(cbMon, cbTue, cbWed, cbThu, cbFri)

        fun updateMaskFromUI() {
            var mask = 0
            for (i in checkboxes.indices) {
                if (checkboxes[i].isChecked) mask = mask or (1 shl i)
            }
            preferencesHelper.setHydrationDays(mask)
            updateNotificationPreview()
            updateStatusText()
        }

        checkboxes.forEach { cb ->
            cb.setOnCheckedChangeListener { _, _ -> updateMaskFromUI() }
        }
    }
    
    private fun setupSwitch() {
        switchHydrationEnabled.setOnCheckedChangeListener { _, isChecked ->
            preferencesHelper.setHydrationEnabled(isChecked)
            updateNotificationPreview()
            
            if (isChecked) {
                startHydrationReminders()
            } else {
                // Turn off Mon-Fri when reminders are disabled
                try {
                    // Clear Monday..Friday bits (bits 0..4)
                    val mask = preferencesHelper.getHydrationDays()
                    val all = (1 shl 7) - 1 // 7 bits
                    val weekdaysBits = (1 shl 5) - 1 // bits for Mon..Fri
                    val newMask = mask and (all xor weekdaysBits)

                    // Update UI checkboxes
                    cbMon.isChecked = false
                    cbTue.isChecked = false
                    cbWed.isChecked = false
                    cbThu.isChecked = false
                    cbFri.isChecked = false

                    // Persist the change
                    preferencesHelper.setHydrationDays(newMask)
                } catch (e: Exception) {
                    // In case checkboxes not yet initialized, just stop reminders
                }
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

    // Load weekday mask and apply to checkboxes (Mon..Sat)
    val mask = preferencesHelper.getHydrationDays()
    cbMon.isChecked = (mask shr 0) and 1 == 1
    cbTue.isChecked = (mask shr 1) and 1 == 1
    cbWed.isChecked = (mask shr 2) and 1 == 1
    cbThu.isChecked = (mask shr 3) and 1 == 1
    cbFri.isChecked = (mask shr 4) and 1 == 1
                    // Saturday and Sunday bits are ignored by the UI; default is Mon-Fri
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
        // Persist goals on start as a safety
        saveGoalsFromInputs()
    }
    
    private fun stopHydrationReminders() {
        HydrationNotificationService.stopService(requireContext())
        updateStatusText()
    }

    private fun updateStatusText() {
        val enabled = preferencesHelper.isHydrationEnabled()
        if (enabled) {
            val interval = preferencesHelper.getHydrationInterval()
            // Append days summary
            val mask = preferencesHelper.getHydrationDays()
            val days = buildString {
                val names = listOf("Mon","Tue","Wed","Thu","Fri","Sat","Sun")
                for (i in 0..6) if ((mask shr i) and 1 == 1) {
                    if (isNotEmpty()) append(",")
                    append(names[i])
                }
            }
            textHydrationStatus.text = "Active — every ${formatInterval(interval)} on $days"
            textHydrationStatus.setTextColor(androidx.core.content.ContextCompat.getColor(requireContext(), R.color.light_blue))
        } else {
            textHydrationStatus.text = "Inactive"
            textHydrationStatus.setTextColor(androidx.core.content.ContextCompat.getColor(requireContext(), R.color.text_secondary))
        }
    }

    private fun loadHydrationTips() {
        val tips = resources.getStringArray(R.array.hydration_tips)
        // Show up to 5 tips, each on its own line (randomized)
        if (tips.isNotEmpty()) {
            val shuffled = tips.toList().shuffled()
            val selected = shuffled.take(5)
            val lines = selected.joinToString(separator = "\n") { "• $it" }
            textHydrationTips.text = lines
        }
    }

    private fun setupGoalInputs() {
        // Load current saved goals
        val min = preferencesHelper.getHydrationGoalMin()
        val max = preferencesHelper.getHydrationGoalMax()
        editGoalMin.setText(min.toString())
        editGoalMax.setText(max.toString())

        // Save when focus leaves an input
        editGoalMin.setOnFocusChangeListener { _, hasFocus -> if (!hasFocus) saveGoalsFromInputs() }
        editGoalMax.setOnFocusChangeListener { _, hasFocus -> if (!hasFocus) saveGoalsFromInputs() }
    }

    private fun saveGoalsFromInputs() {
        val minStr = editGoalMin.text.toString().trim()
        val maxStr = editGoalMax.text.toString().trim()
        val min = minStr.toIntOrNull() ?: preferencesHelper.getHydrationGoalMin()
        val max = maxStr.toIntOrNull() ?: preferencesHelper.getHydrationGoalMax()
        preferencesHelper.setHydrationGoalMin(min)
        preferencesHelper.setHydrationGoalMax(max)
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
