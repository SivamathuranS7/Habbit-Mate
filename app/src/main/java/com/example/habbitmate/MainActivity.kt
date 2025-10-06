package com.example.habbitmate

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {
    
    private lateinit var viewPager: ViewPager2
    private lateinit var bottomNavigation: BottomNavigationView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        
        setupViewPager()
        setupBottomNavigation()

        // Handle intent extra to open a specific page when returning from other activities
        val page = intent?.getIntExtra(com.example.habbitmate.activities.EXTRA_PAGE, -1) ?: -1
        if (page in 0..2) {
            viewPager.post { viewPager.currentItem = page }
        }
    }
    
    private fun setupViewPager() {
        viewPager = findViewById(R.id.viewPager)
        val adapter = MainPagerAdapter(this)
        viewPager.adapter = adapter
    }
    
    private fun setupBottomNavigation() {
        bottomNavigation = findViewById(R.id.bottomNavigation)
        
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_habits -> {
                    viewPager.currentItem = 0
                    true
                }
                R.id.nav_mood -> {
                    viewPager.currentItem = 1
                    true
                }
                R.id.nav_settings -> {
                    viewPager.currentItem = 2
                    true
                }
                else -> false
            }
        }
        
        // Sync ViewPager with BottomNavigation
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when (position) {
                    0 -> bottomNavigation.selectedItemId = R.id.nav_habits
                    1 -> bottomNavigation.selectedItemId = R.id.nav_mood
                    2 -> bottomNavigation.selectedItemId = R.id.nav_settings
                }
            }
        })
    }
}