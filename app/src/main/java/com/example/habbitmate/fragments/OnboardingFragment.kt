package com.example.habbitmate.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.habbitmate.R

class OnboardingFragment : Fragment() {
    
    companion object {
        private const val ARG_POSITION = "position"
        
        fun newInstance(position: Int): OnboardingFragment {
            val fragment = OnboardingFragment()
            val args = Bundle()
            args.putInt(ARG_POSITION, position)
            fragment.arguments = args
            return fragment
        }
    }
    
    private var position: Int = 0
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            position = it.getInt(ARG_POSITION)
        }
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_onboarding, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val imageView = view.findViewById<ImageView>(R.id.imageViewOnboarding)
        val textTitle = view.findViewById<TextView>(R.id.textTitleOnboarding)
        val textDescription = view.findViewById<TextView>(R.id.textDescriptionOnboarding)
        
        when (position) {
            0 -> {
                imageView.setImageResource(R.drawable.ic_habits)
                textTitle.text = "Track Your Habits"
                textDescription.text = "Build healthy habits and track your daily progress with our intuitive habit tracking system."
            }
            1 -> {
                imageView.setImageResource(R.drawable.ic_mood)
                textTitle.text = "Monitor Your Mood"
                textDescription.text = "Log your emotions and track your mental well-being with our comprehensive mood journal."
            }
            2 -> {
                imageView.setImageResource(R.drawable.ic_settings)
                textTitle.text = "Stay Active"
                textDescription.text = "Track your physical activities and monitor your fitness progress with detailed analytics."
            }
        }
    }
}
