package com.example.habbitmate

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.habbitmate.fragments.HabitTrackerFragment
import com.example.habbitmate.fragments.MoodJournalFragment
import com.example.habbitmate.fragments.SettingsFragment

class MainPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    
    override fun getItemCount(): Int = 3
    
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HabitTrackerFragment()
            1 -> MoodJournalFragment()
            2 -> SettingsFragment()
            else -> HabitTrackerFragment()
        }
    }
}
