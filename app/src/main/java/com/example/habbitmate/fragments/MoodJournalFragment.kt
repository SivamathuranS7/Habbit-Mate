package com.example.habbitmate.fragments

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.habbitmate.R
import com.example.habbitmate.adapters.ActivityAdapter
import com.example.habbitmate.adapters.MoodAdapter
import com.example.habbitmate.data.Activity
import com.example.habbitmate.data.ActivityType
import com.example.habbitmate.data.Mood
import com.example.habbitmate.data.MoodType
import com.example.habbitmate.data.PreferencesHelper
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.google.android.material.textfield.TextInputEditText
import java.util.*

class MoodJournalFragment : Fragment() {
    
    private lateinit var moodRecyclerView: RecyclerView
    private lateinit var moodChart: LineChart
    private lateinit var caloriesChart: PieChart
    private lateinit var moodAdapter: MoodAdapter
    private lateinit var preferencesHelper: PreferencesHelper
    
    // Activities
    private lateinit var activityRecyclerView: RecyclerView
    private lateinit var activityAdapter: ActivityAdapter
    private lateinit var btnAddActivity: Button
    
    // Hero section views
    private lateinit var textGreeting: TextView
    private lateinit var textCurrentTime: TextView
    
    // Walking activity views
    private lateinit var textWalkingSteps: TextView
    private lateinit var textWalkingDistance: TextView
    
    // Add mood button
    private lateinit var btnAddMood: Button
    
    private val timeHandler = Handler(Looper.getMainLooper())
    private val timeRunnable = object : Runnable {
        override fun run() {
            updateTime()
            timeHandler.postDelayed(this, 60000) // Update every minute
        }
    }
    
    private var selectedMoodType: MoodType? = null
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_mood_journal, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        preferencesHelper = PreferencesHelper(requireContext())
        
        // Initialize views
        moodRecyclerView = view.findViewById(R.id.recyclerViewMoods)
        moodChart = view.findViewById(R.id.moodChart)
        caloriesChart = view.findViewById(R.id.caloriesChart)
        textGreeting = view.findViewById(R.id.textGreeting)
        textCurrentTime = view.findViewById(R.id.textCurrentTime)
        textWalkingSteps = view.findViewById(R.id.textWalkingSteps)
        textWalkingDistance = view.findViewById(R.id.textWalkingDistance)
        btnAddMood = view.findViewById(R.id.btnAddMood)
        
        // Activities
        activityRecyclerView = view.findViewById(R.id.recyclerViewActivities)
        btnAddActivity = view.findViewById(R.id.btnAddActivity)
        
        setupHeroSection()
        setupMoodSelector(view)
        setupRecyclerView()
        setupChart()
        setupCaloriesChart()
        setupWalkingActivity()
        setupActivities()
        loadMoods()
        loadActivities()
        
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
    
    private fun setupWalkingActivity() {
        // Simulate walking data - in a real app, this would come from a fitness tracker
        val randomSteps = 1000 + (Math.random() * 7000).toInt()
        val randomDistance = 0.5 + Math.random() * 4.5
        textWalkingSteps.text = "Steps Today: $randomSteps"
        textWalkingDistance.text = "Distance: ${String.format("%.1f", randomDistance)} km"
    }
    
    private fun setupMoodSelector(view: View) {
        val moodButtons = listOf(
            view.findViewById<View>(R.id.btnMoodHappy),
            view.findViewById<View>(R.id.btnMoodSad),
            view.findViewById<View>(R.id.btnMoodAngry),
            view.findViewById<View>(R.id.btnMoodCalm),
            view.findViewById<View>(R.id.btnMoodExcited),
            view.findViewById<View>(R.id.btnMoodTired),
            view.findViewById<View>(R.id.btnMoodStressed),
            view.findViewById<View>(R.id.btnMoodGrateful),
            view.findViewById<View>(R.id.btnMoodConfused),
            view.findViewById<View>(R.id.btnMoodSurprised),
            view.findViewById<View>(R.id.btnMoodLoved),
            view.findViewById<View>(R.id.btnMoodProud)
        )
        
        val moodTypes = listOf(
            MoodType.HAPPY,
            MoodType.SAD,
            MoodType.ANGRY,
            MoodType.CALM,
            MoodType.EXCITED,
            MoodType.TIRED,
            MoodType.STRESSED,
            MoodType.GRATEFUL,
            MoodType.CONFUSED,
            MoodType.SURPRISED,
            MoodType.LOVED,
            MoodType.PROUD
        )
        
        moodButtons.forEachIndexed { index, button ->
            button.setOnClickListener {
                selectedMoodType = moodTypes[index]
                showAddMoodDialog()
            }
        }
        
        // Setup add mood button
        btnAddMood.setOnClickListener {
            if (selectedMoodType != null) {
                showAddMoodDialog()
            } else {
                // Show message to select a mood first
                AlertDialog.Builder(requireContext())
                    .setTitle("Select a Mood")
                    .setMessage("Please select a mood emoji first!")
                    .setPositiveButton("OK", null)
                    .show()
            }
        }
    }
    
    private fun showAddMoodDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_mood, null)
        
        val textSelectedMoodEmoji = dialogView.findViewById<TextView>(R.id.textSelectedMoodEmoji)
        val textSelectedMoodName = dialogView.findViewById<TextView>(R.id.textSelectedMoodName)
        val editTextMoodDescription = dialogView.findViewById<TextInputEditText>(R.id.editTextMoodDescription)
        val buttonCancel = dialogView.findViewById<Button>(R.id.buttonCancel)
        val buttonSave = dialogView.findViewById<Button>(R.id.buttonSave)
        
        // Set selected mood info
        selectedMoodType?.let { moodType ->
            textSelectedMoodEmoji.text = moodType.emoji
            textSelectedMoodName.text = moodType.name.lowercase().replaceFirstChar { it.uppercase() }
        }
        
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()
        
        buttonCancel.setOnClickListener {
            dialog.dismiss()
        }
        
        buttonSave.setOnClickListener {
            val description = editTextMoodDescription.text.toString().trim()
            val note = if (description.isNotEmpty()) description else null
            
            selectedMoodType?.let { moodType ->
                val mood = Mood(
                    id = System.currentTimeMillis().toString(),
                    moodType = moodType,
                    date = Date(),
                    note = note
                )
                preferencesHelper.addMood(mood)
                loadMoods()
            }
            dialog.dismiss()
        }
        
        dialog.show()
    }
    
    private fun setupRecyclerView() {
        moodAdapter = MoodAdapter()
        moodRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = moodAdapter
        }
        
        // Setup swipe to delete
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                val moods = preferencesHelper.getMoods().sortedByDescending { it.date }
                
                if (position < moods.size) {
                    val moodToDelete = moods[position]
                    
                    // Show confirmation dialog
                    AlertDialog.Builder(requireContext())
                        .setTitle("Delete Mood")
                        .setMessage("Are you sure you want to delete this mood entry?")
                        .setPositiveButton("Delete") { _, _ ->
                            // Remove mood from preferences
                            val updatedMoods = moods.toMutableList()
                            updatedMoods.removeAt(position)
                            preferencesHelper.saveMoods(updatedMoods)
                            loadMoods()
                        }
                        .setNegativeButton("Cancel") { _, _ ->
                            // Restore the item
                            moodAdapter.notifyItemChanged(position)
                        }
                        .setOnCancelListener {
                            // Restore the item if dialog is cancelled
                            moodAdapter.notifyItemChanged(position)
                        }
                        .show()
                }
            }

            override fun onChildDraw(
                c: android.graphics.Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                val itemView = viewHolder.itemView
                val background = if (dX > 0) {
                    // Swiping right - show delete background
                    android.graphics.drawable.ColorDrawable(Color.RED)
                } else {
                    // Swiping left - show delete background
                    android.graphics.drawable.ColorDrawable(Color.RED)
                }
                
                background.setBounds(
                    itemView.left,
                    itemView.top,
                    itemView.right,
                    itemView.bottom
                )
                background.draw(c)
                
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        })
        
        itemTouchHelper.attachToRecyclerView(moodRecyclerView)
    }
    
    private fun setupActivities() {
        activityAdapter = ActivityAdapter(
            onEditActivity = { activity ->
                showEditActivityDialog(activity)
            },
            onDeleteActivity = { activity ->
                AlertDialog.Builder(requireContext())
                    .setTitle("Delete Activity")
                    .setMessage("Are you sure you want to delete this activity?")
                    .setPositiveButton("Delete") { _, _ ->
                        preferencesHelper.deleteActivity(activity.id)
                        loadActivities()
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        )
        
        activityRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = activityAdapter
            // Ensure RecyclerView measures correctly inside the ScrollView
            setHasFixedSize(false)
            isNestedScrollingEnabled = false
        }
        
        btnAddActivity.setOnClickListener {
            showAddActivityDialog()
        }
    }
    
    private fun showAddActivityDialog() {
        showActivityDialog(null)
    }
    
    private fun showEditActivityDialog(activity: Activity) {
        showActivityDialog(activity)
    }
    
    private fun showActivityDialog(activity: Activity?) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_activity, null)
        
        val editTextActivityName = dialogView.findViewById<TextInputEditText>(R.id.editTextActivityName)
        val spinnerActivityType = dialogView.findViewById<android.widget.Spinner>(R.id.spinnerActivityType)
        val spinnerDuration = dialogView.findViewById<android.widget.Spinner>(R.id.spinnerDuration)
        val spinnerDistance = dialogView.findViewById<android.widget.Spinner>(R.id.spinnerDistance)
        val buttonCancel = dialogView.findViewById<Button>(R.id.buttonCancel)
        val buttonSave = dialogView.findViewById<Button>(R.id.buttonSave)
        
        // Setup activity type spinner
        val activityTypes = ActivityType.values().map { it.displayName }
        val typeAdapter = android.widget.ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, activityTypes)
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerActivityType.adapter = typeAdapter
        
        // Setup duration spinner
        val durations = listOf("5", "10", "15", "20", "30", "45", "60", "90", "120")
        val durationAdapter = android.widget.ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, durations)
        durationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDuration.adapter = durationAdapter
        
        // Setup distance spinner
        val distances = listOf("0.5", "1.0", "1.5", "2.0", "2.5", "3.0", "4.0", "5.0", "6.0", "8.0", "10.0")
        val distanceAdapter = android.widget.ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, distances)
        distanceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDistance.adapter = distanceAdapter
        
        // Pre-fill if editing
        if (activity != null) {
            editTextActivityName.setText(activity.name)
            spinnerActivityType.setSelection(activityTypes.indexOf(activity.type.displayName))
            spinnerDuration.setSelection(durations.indexOf(activity.duration.toString()))
            spinnerDistance.setSelection(distances.indexOf(activity.distance.toString()))
        }
        
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()
        
        buttonCancel.setOnClickListener {
            dialog.dismiss()
        }
        
        buttonSave.setOnClickListener {
            val name = editTextActivityName.text.toString().trim()
            if (name.isEmpty()) {
                editTextActivityName.error = "Please enter activity name"
                return@setOnClickListener
            }
            
            val type = ActivityType.values()[spinnerActivityType.selectedItemPosition]
            val duration = durations[spinnerDuration.selectedItemPosition].toInt()
            val distance = distances[spinnerDistance.selectedItemPosition].toDouble()
            
            if (activity == null) {
                // Add new activity
                val newActivity = Activity(
                    id = System.currentTimeMillis().toString(),
                    name = name,
                    type = type,
                    duration = duration,
                    distance = distance,
                    date = Date()
                )
                preferencesHelper.addActivity(newActivity)
            } else {
                // Update existing activity
                val updatedActivity = activity.copy(
                    name = name,
                    type = type,
                    duration = duration,
                    distance = distance
                )
                preferencesHelper.updateActivity(activity.id, updatedActivity)
            }
            
            loadActivities()
            dialog.dismiss()
        }
        
        dialog.show()
    }
    
    private fun loadActivities() {
        val activities = preferencesHelper.getActivities()
        activityAdapter.updateActivities(activities)
        updateCaloriesChart(activities)

        // Force a re-layout and scroll to top so newly added activities appear immediately
        activityRecyclerView.post {
            try {
                activityRecyclerView.requestLayout()
                if (activities.isNotEmpty()) {
                    (activityRecyclerView.layoutManager as? LinearLayoutManager)
                        ?.scrollToPositionWithOffset(0, 0)
                }
            } catch (e: Exception) {
                // Ignore layout exceptions — best effort attempt to refresh UI
            }
        }
    }
    
    private fun updateCaloriesChart(activities: List<Activity>) {
        val todayActivities = activities.filter { 
            val today = Calendar.getInstance()
            val activityDate = Calendar.getInstance()
            activityDate.time = it.date
            today.get(Calendar.DAY_OF_YEAR) == activityDate.get(Calendar.DAY_OF_YEAR) &&
            today.get(Calendar.YEAR) == activityDate.get(Calendar.YEAR)
        }
        
        if (todayActivities.isNotEmpty()) {
            // Group activities by type and sum calories
            val caloriesByType = todayActivities.groupBy { it.type }
                .mapValues { (_, activities) -> activities.sumOf { it.calories } }
            
            val entries = mutableListOf<PieEntry>()
            val colors = mutableListOf<Int>()
            
            // Define colors for different activity types
            val colorMap = mapOf(
                ActivityType.WALKING to androidx.core.content.ContextCompat.getColor(requireContext(), R.color.light_blue),
                ActivityType.RUNNING to androidx.core.content.ContextCompat.getColor(requireContext(), R.color.red),
                ActivityType.CYCLING to androidx.core.content.ContextCompat.getColor(requireContext(), R.color.orange),
                ActivityType.SWIMMING to android.graphics.Color.CYAN,
                ActivityType.YOGA to android.graphics.Color.MAGENTA,
                ActivityType.GYM to android.graphics.Color.DKGRAY,
                ActivityType.DANCE to android.graphics.Color.YELLOW,
                ActivityType.HIKING to android.graphics.Color.GREEN
            )
            
            caloriesByType.forEach { (type, calories) ->
                entries.add(PieEntry(calories.toFloat(), "${type.emoji} ${type.displayName}"))
                colors.add(colorMap[type] ?: android.graphics.Color.GRAY)
            }
            
            val dataSet = PieDataSet(entries, "")
            dataSet.colors = colors
            dataSet.setDrawValues(true)
            dataSet.valueTextSize = 12f
            dataSet.valueTextColor = android.graphics.Color.BLACK
            
            val pieData = PieData(dataSet)
            pieData.setValueFormatter(PercentFormatter(caloriesChart))
            
            caloriesChart.data = pieData
            caloriesChart.invalidate()
        } else {
            // Clear chart when no activities
            caloriesChart.clear()
            caloriesChart.invalidate()
        }
    }
    
    private fun setupCaloriesChart() {
        // Configure pie chart appearance
        caloriesChart.description.isEnabled = false
        caloriesChart.setTouchEnabled(true)
        caloriesChart.setBackgroundColor(android.graphics.Color.WHITE)
        caloriesChart.setDrawEntryLabels(true)
        caloriesChart.setEntryLabelTextSize(12f)
        caloriesChart.setEntryLabelColor(android.graphics.Color.BLACK)
        caloriesChart.setUsePercentValues(true)
        caloriesChart.setDrawHoleEnabled(true)
        caloriesChart.setHoleColor(android.graphics.Color.WHITE)
        caloriesChart.setTransparentCircleColor(android.graphics.Color.WHITE)
        caloriesChart.setTransparentCircleAlpha(110)
        caloriesChart.setHoleRadius(58f)
        caloriesChart.setTransparentCircleRadius(61f)
        caloriesChart.setDrawCenterText(true)
        caloriesChart.setCenterText("Calories\nBurned")
        caloriesChart.setCenterTextSize(16f)
        caloriesChart.setCenterTextColor(android.graphics.Color.BLACK)
    }
    
    private fun setupChart() {
        // Configure chart appearance
        moodChart.description.isEnabled = false
        moodChart.setTouchEnabled(true)
        moodChart.isDragEnabled = true
        moodChart.setScaleEnabled(true)
        moodChart.setPinchZoom(true)
        moodChart.setBackgroundColor(android.graphics.Color.WHITE)
        
        // Configure axes
        val xAxis = moodChart.xAxis
    xAxis.textColor = androidx.core.content.ContextCompat.getColor(requireContext(), R.color.text_secondary)
        
        val yAxis = moodChart.axisLeft
    yAxis.textColor = androidx.core.content.ContextCompat.getColor(requireContext(), R.color.text_secondary)
        yAxis.axisMinimum = 0f
        yAxis.axisMaximum = 6f
        
        moodChart.axisRight.isEnabled = false
    }
    
    private fun loadMoods() {
        val moods = preferencesHelper.getMoods()
        moodAdapter.updateMoods(moods)
        updateChart(moods)
    }
    
    private fun updateChart(moods: List<Mood>) {
        val entries = mutableListOf<Entry>()
        val last7Days = moods.takeLast(7)
        
        last7Days.forEachIndexed { index, mood ->
            entries.add(Entry(index.toFloat(), mood.moodType.value.toFloat()))
        }
        
        if (entries.isNotEmpty()) {
            val dataSet = LineDataSet(entries, "Mood Level")
            dataSet.color = androidx.core.content.ContextCompat.getColor(requireContext(), R.color.red)
            dataSet.setCircleColor(androidx.core.content.ContextCompat.getColor(requireContext(), R.color.red))
            dataSet.lineWidth = 3f
            dataSet.circleRadius = 5f
            dataSet.setDrawValues(false)
            
            val lineData = LineData(dataSet)
            moodChart.data = lineData
            moodChart.invalidate()
        } else {
            // Clear chart when no data
            moodChart.clear()
            moodChart.invalidate()
        }
    }
}
