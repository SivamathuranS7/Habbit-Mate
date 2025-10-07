package com.example.habbitmate.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.habbitmate.R
import com.example.habbitmate.data.Activity
import java.text.SimpleDateFormat
import java.util.*

class ActivityAdapter(
    private val onEditActivity: (Activity) -> Unit,
    private val onDeleteActivity: (Activity) -> Unit
) : RecyclerView.Adapter<ActivityAdapter.ActivityViewHolder>() {
    // Adapter for user activities (walking, running, etc.) displayed in RecyclerView
    private var activities = listOf<Activity>()
    private val dateFormat = SimpleDateFormat("MMM dd, h:mm a", Locale.getDefault())
    
    fun updateActivities(newActivities: List<Activity>) {
        activities = newActivities.sortedByDescending { it.date }
        notifyDataSetChanged()
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_activity, parent, false)
        return ActivityViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: ActivityViewHolder, position: Int) {
        holder.bind(activities[position])
    }
    
    override fun getItemCount(): Int = activities.size
    
    inner class ActivityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val activityEmoji: TextView = itemView.findViewById(R.id.textActivityEmoji)
        private val activityName: TextView = itemView.findViewById(R.id.textActivityName)
        private val activityDetails: TextView = itemView.findViewById(R.id.textActivityDetails)
        private val activityDate: TextView = itemView.findViewById(R.id.textActivityDate)
        private val buttonEdit: ImageButton = itemView.findViewById(R.id.buttonEdit)
        private val buttonDelete: ImageButton = itemView.findViewById(R.id.buttonDelete)
        
        fun bind(activity: Activity) {
            activityEmoji.text = activity.type.emoji
            activityName.text = activity.name
            activityDate.text = dateFormat.format(activity.date)
            
            // Build details string
            val details = buildString {
                if (activity.duration > 0) {
                    append("${activity.duration} min")
                }
                if (activity.distance > 0) {
                    if (isNotEmpty()) append(" • ")
                    append("${String.format("%.1f", activity.distance)} km")
                }
                if (activity.calories > 0) {
                    if (isNotEmpty()) append(" • ")
                    append("${activity.calories} cal")
                }
            }
            
            activityDetails.text = details.ifEmpty { "No details" }
            
            // Setup button listeners
            buttonEdit.setOnClickListener {
                onEditActivity(activity)
            }
            
            buttonDelete.setOnClickListener {
                onDeleteActivity(activity)
            }
        }
    }
}
