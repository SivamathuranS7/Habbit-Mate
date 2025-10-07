package com.example.habbitmate.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.habbitmate.R
import com.example.habbitmate.data.Mood
import java.text.SimpleDateFormat
import java.util.*

class MoodAdapter : RecyclerView.Adapter<MoodAdapter.MoodViewHolder>() {
    // Adapter for mood entries shown in the Mood journal RecyclerView
    private var moods = listOf<Mood>()
    private val dateFormat = SimpleDateFormat("MMM dd, h:mm a", Locale.getDefault())
    
    fun updateMoods(newMoods: List<Mood>) {
        moods = newMoods.sortedByDescending { it.date }
        notifyDataSetChanged()
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoodViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_mood, parent, false)
        return MoodViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: MoodViewHolder, position: Int) {
        holder.bind(moods[position])
    }
    
    override fun getItemCount(): Int = moods.size
    
    inner class MoodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val moodEmoji: TextView = itemView.findViewById(R.id.textMoodEmoji)
        private val moodType: TextView = itemView.findViewById(R.id.textMoodType)
        private val moodNote: TextView = itemView.findViewById(R.id.textMoodNote)
        private val moodDate: TextView = itemView.findViewById(R.id.textMoodDate)
        
        fun bind(mood: Mood) {
            moodEmoji.text = mood.moodType.emoji
            moodType.text = mood.moodType.name.lowercase().replaceFirstChar { it.uppercase() }
            moodDate.text = dateFormat.format(mood.date)
            
            // Show note if available
            if (!mood.note.isNullOrBlank()) {
                moodNote.text = mood.note
                moodNote.visibility = View.VISIBLE
            } else {
                moodNote.visibility = View.GONE
            }
        }
    }
}
