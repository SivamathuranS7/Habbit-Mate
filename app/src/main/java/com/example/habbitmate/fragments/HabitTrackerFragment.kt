package com.example.habbitmate.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.habbitmate.R
import com.example.habbitmate.adapters.HabitAdapter
import com.example.habbitmate.data.Habit
import com.example.habbitmate.data.HabitCategory
import com.example.habbitmate.data.HabitType
import com.example.habbitmate.data.PreferencesHelper
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*

class HabitTrackerFragment : Fragment() {
    
    private lateinit var recyclerView: RecyclerView
    private lateinit var fab: FloatingActionButton
    private lateinit var habitAdapter: HabitAdapter
    private lateinit var preferencesHelper: PreferencesHelper
    
    // Hero section views
    private lateinit var textGreeting: TextView
    private lateinit var textCurrentTime: TextView
    
    // Progress views
    private lateinit var progressBarOverall: ProgressBar
    private lateinit var textProgressPercentage: TextView
    
    // Calendar
    private lateinit var calendarView: CalendarView
    
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
        return inflater.inflate(R.layout.fragment_habit_tracker, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        preferencesHelper = PreferencesHelper(requireContext())
        
        // Initialize views
        recyclerView = view.findViewById(R.id.recyclerViewHabits)
        fab = view.findViewById(R.id.fabAddHabit)
        textGreeting = view.findViewById(R.id.textGreeting)
        textCurrentTime = view.findViewById(R.id.textCurrentTime)
        progressBarOverall = view.findViewById(R.id.progressBarOverall)
        textProgressPercentage = view.findViewById(R.id.textProgressPercentage)
        calendarView = view.findViewById(R.id.calendarView)
        
        setupHeroSection()
        setupRecyclerView()
        setupFab()
        setupCalendar()
        loadHabits()
        
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
            in 5..11 -> getString(R.string.greeting_morning)
            in 12..17 -> getString(R.string.greeting_afternoon)
            in 18..21 -> getString(R.string.greeting_evening)
            else -> getString(R.string.greeting_night)
        }
        
        textGreeting.text = greeting
    }
    
    private fun updateTime() {
        val currentTime = DateFormat.format("h:mm a", Date()).toString()
        textCurrentTime.text = currentTime
    }
    
    private fun setupRecyclerView() {
        habitAdapter = HabitAdapter(
            onHabitToggle = { habit, isCompleted ->
                preferencesHelper.updateHabitCompletion(habit.id, isCompleted)
                recyclerView.post {
                    loadHabits()
                }
            },
            onHabitCountUpdate = { habit ->
                preferencesHelper.updateHabitCount(habit.id, true)
                recyclerView.post {
                    loadHabits()
                }
            },
            onEditHabit = { habit ->
                showEditHabitDialog(habit)
            },
            onDeleteHabit = { habit ->
                showDeleteConfirmationDialog(habit)
            }
        )
        
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = habitAdapter
        }
    }
    
    private fun setupFab() {
        fab.setOnClickListener {
            showAddHabitDialog()
        }
    }
    
    private fun setupCalendar() {
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            // Handle date selection if needed
        }
    }
    
    private fun loadHabits() {
        val habits = preferencesHelper.getHabits()
        habitAdapter.updateHabits(habits)
        updateOverallProgress(habits)
    }
    
    private fun updateOverallProgress(habits: List<Habit>) {
        if (habits.isEmpty()) {
            progressBarOverall.progress = 0
            textProgressPercentage.text = getString(R.string.progress_percentage, 0)
            return
        }
        
        val completedHabits = habits.count { it.isCompleted }
        val totalHabits = habits.size
        val percentage = (completedHabits * 100) / totalHabits
        
        progressBarOverall.progress = percentage
        textProgressPercentage.text = getString(R.string.progress_percentage, percentage)
    }
    
    private fun showAddHabitDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_habit, null)
        
        val editTextName = dialogView.findViewById<EditText>(R.id.editTextHabitName)
        val editTextDescription = dialogView.findViewById<EditText>(R.id.editTextHabitDescription)
        val spinnerCategory = dialogView.findViewById<Spinner>(R.id.spinnerCategory)
        val spinnerType = dialogView.findViewById<Spinner>(R.id.spinnerType)
        val textTargetCountLabel = dialogView.findViewById<TextView>(R.id.textTargetCountLabel)
        val textInputTargetCount = dialogView.findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.textInputTargetCount)
        val editTextTargetCount = dialogView.findViewById<EditText>(R.id.editTextTargetCount)
        val buttonCancel = dialogView.findViewById<Button>(R.id.buttonCancel)
        val buttonSave = dialogView.findViewById<Button>(R.id.buttonSave)
        
        // Setup category spinner
        val categories = HabitCategory.values().map { it.displayName }
        val categoryAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = categoryAdapter
        
        // Setup type spinner
        val types = HabitType.values().map { it.displayName }
        val typeAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, types)
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerType.adapter = typeAdapter
        
        // Handle type change
        spinnerType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val isCountable = HabitType.values()[position] == HabitType.COUNTABLE
                textTargetCountLabel.visibility = if (isCountable) View.VISIBLE else View.GONE
                textInputTargetCount.visibility = if (isCountable) View.VISIBLE else View.GONE
            }
            
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()
        
        buttonCancel.setOnClickListener {
            dialog.dismiss()
        }
        
        buttonSave.setOnClickListener {
            val name = editTextName.text.toString().trim()
            if (name.isEmpty()) {
                editTextName.error = "Please enter a habit name"
                return@setOnClickListener
            }
            
            val description = editTextDescription.text.toString().trim()
            val category = HabitCategory.values()[spinnerCategory.selectedItemPosition]
            val type = HabitType.values()[spinnerType.selectedItemPosition]
            val targetCount = if (type == HabitType.COUNTABLE) {
                editTextTargetCount.text.toString().toIntOrNull() ?: 1
            } else {
                1
            }
            
            val newHabit = Habit(
                id = System.currentTimeMillis().toString(),
                name = name,
                description = if (description.isEmpty()) null else description,
                category = category,
                type = type,
                targetCount = targetCount
            )
            
            val habits = preferencesHelper.getHabits().toMutableList()
            habits.add(newHabit)
            preferencesHelper.saveHabits(habits)
            loadHabits()
            dialog.dismiss()
        }
        
        dialog.show()
    }
    
    private fun showEditHabitDialog(habit: Habit) {
        // Similar to add dialog but pre-filled with habit data
        showAddHabitDialog() // For now, just show add dialog
    }
    
    private fun showDeleteConfirmationDialog(habit: Habit) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Habit")
            .setMessage(getString(R.string.confirm_delete))
            .setPositiveButton("Delete") { _, _ ->
                val habits = preferencesHelper.getHabits().toMutableList()
                habits.removeAll { it.id == habit.id }
                preferencesHelper.saveHabits(habits)
                loadHabits()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
