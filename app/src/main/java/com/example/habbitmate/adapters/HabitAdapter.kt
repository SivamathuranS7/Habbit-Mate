package com.example.habbitmate.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.habbitmate.R
import com.example.habbitmate.data.Habit

class HabitAdapter(
    private val onHabitToggle: (Habit, Boolean) -> Unit,
    private val onHabitCountUpdate: (Habit) -> Unit,
    private val onEditHabit: (Habit) -> Unit,
    private val onDeleteHabit: (Habit) -> Unit
) : RecyclerView.Adapter<HabitAdapter.HabitViewHolder>() {
    
    private var habits = listOf<Habit>()
    
    fun updateHabits(newHabits: List<Habit>) {
        val oldHabits = habits
        habits = newHabits
        
        // Use DiffUtil for more efficient updates if lists are similar
        if (oldHabits.size == newHabits.size) {
            // Simple case: just notify that data changed
            notifyItemRangeChanged(0, habits.size)
        } else {
            // Different sizes: use notifyDataSetChanged
            notifyDataSetChanged()
        }
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_habit, parent, false)
        return HabitViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        holder.bind(habits[position])
    }
    
    override fun getItemCount(): Int = habits.size
    
    inner class HabitViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val habitName: TextView = itemView.findViewById(R.id.textHabitName)
        private val habitDescription: TextView = itemView.findViewById(R.id.textHabitDescription)
        private val habitCategory: TextView = itemView.findViewById(R.id.textHabitCategory)
        private val habitProgress: ProgressBar = itemView.findViewById(R.id.progressHabit)
        private val progressPercentage: TextView = itemView.findViewById(R.id.textProgressPercentage)
        private val countInfo: TextView = itemView.findViewById(R.id.textCountInfo)
        private val buttonEdit: ImageButton = itemView.findViewById(R.id.buttonEdit)
        private val buttonDelete: ImageButton = itemView.findViewById(R.id.buttonDelete)
        
        fun bind(habit: Habit) {
            habitName.text = habit.name
            habitDescription.text = habit.description ?: ""
            habitCategory.text = habit.category?.displayName ?: "General"
            
            // Update progress based on habit type and completion
            val progress = when (habit.type ?: com.example.habbitmate.data.HabitType.SINGLE) {
                com.example.habbitmate.data.HabitType.SINGLE -> {
                    if (habit.isCompleted) 100 else 0
                }
                com.example.habbitmate.data.HabitType.COUNTABLE -> {
                    val targetCount = habit.targetCount.takeIf { it > 0 } ?: 1
                    val currentCount = habit.currentCount
                    val percentage = (currentCount * 100) / targetCount
                    percentage.coerceAtMost(100)
                }
            }
            
            habitProgress.progress = progress
            progressPercentage.text = "$progress%"
            
            // Show count info for countable habits
            if ((habit.type ?: com.example.habbitmate.data.HabitType.SINGLE) == com.example.habbitmate.data.HabitType.COUNTABLE) {
                val targetCount = habit.targetCount.takeIf { it > 0 } ?: 1
                countInfo.text = "${habit.currentCount} / ${targetCount}"
                countInfo.visibility = View.VISIBLE
            } else {
                countInfo.visibility = View.GONE
            }
            
            // Setup button listeners
            buttonEdit.setOnClickListener {
                onEditHabit(habit)
            }
            
            buttonDelete.setOnClickListener {
                onDeleteHabit(habit)
            }
            
            // Handle habit completion based on type
            itemView.setOnClickListener {
                when (habit.type ?: com.example.habbitmate.data.HabitType.SINGLE) {
                    com.example.habbitmate.data.HabitType.SINGLE -> {
                        onHabitToggle(habit, !habit.isCompleted)
                    }
                    com.example.habbitmate.data.HabitType.COUNTABLE -> {
                        onHabitCountUpdate(habit)
                    }
                }
            }
        }
    }
}
