package com.example.habbitmate.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.habbitmate.MainActivity
import com.example.habbitmate.R
import com.example.habbitmate.adapters.OnboardingPagerAdapter
import com.google.android.material.button.MaterialButton

class OnboardingActivity : AppCompatActivity() {
    
    private lateinit var viewPager: ViewPager2
    private lateinit var buttonNext: MaterialButton
    private lateinit var buttonSkip: MaterialButton
    private lateinit var buttonGetStarted: MaterialButton
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)
        
        setupViews()
        setupViewPager()
        setupButtons()
    }
    
    private fun setupViews() {
        viewPager = findViewById(R.id.viewPagerOnboarding)
        buttonNext = findViewById(R.id.buttonNext)
        buttonSkip = findViewById(R.id.buttonSkip)
        buttonGetStarted = findViewById(R.id.buttonGetStarted)
    }
    
    private fun setupViewPager() {
        val adapter = OnboardingPagerAdapter(this)
        viewPager.adapter = adapter
        
        // Handle page changes
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateButtons(position)
            }
        })
    }
    
    private fun setupButtons() {
        buttonNext.setOnClickListener {
            if (viewPager.currentItem < 2) {
                viewPager.currentItem = viewPager.currentItem + 1
            }
        }
        
        buttonSkip.setOnClickListener {
            navigateToMain()
        }
        
        buttonGetStarted.setOnClickListener {
            navigateToMain()
        }
    }
    
    private fun updateButtons(position: Int) {
        when (position) {
            0, 1 -> {
                // First and second screens - show Next and Skip
                buttonNext.visibility = android.view.View.VISIBLE
                buttonSkip.visibility = android.view.View.VISIBLE
                buttonGetStarted.visibility = android.view.View.GONE
            }
            2 -> {
                // Last screen - show only Get Started
                buttonNext.visibility = android.view.View.GONE
                buttonSkip.visibility = android.view.View.GONE
                buttonGetStarted.visibility = android.view.View.VISIBLE
            }
        }
    }
    
    private fun navigateToMain() {
        // After onboarding, navigate to Login screen
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
