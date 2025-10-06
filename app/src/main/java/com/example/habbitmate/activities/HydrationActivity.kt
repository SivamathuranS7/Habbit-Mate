package com.example.habbitmate.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.habbitmate.R
import com.example.habbitmate.fragments.HydrationReminderFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

const val EXTRA_PAGE = "extra_page"

class HydrationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hydration)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.hydration_fragment_container, HydrationReminderFragment())
                .commit()
        }

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationHydration)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                com.example.habbitmate.R.id.nav_habits -> {
                    // Open MainActivity and show Habits page
                    val intent = Intent(this, com.example.habbitmate.MainActivity::class.java)
                    intent.putExtra(EXTRA_PAGE, 0)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                    finish()
                    true
                }
                com.example.habbitmate.R.id.nav_mood -> {
                    val intent = Intent(this, com.example.habbitmate.MainActivity::class.java)
                    intent.putExtra(EXTRA_PAGE, 1)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                    finish()
                    true
                }
                com.example.habbitmate.R.id.nav_settings -> {
                    val intent = Intent(this, com.example.habbitmate.MainActivity::class.java)
                    intent.putExtra(EXTRA_PAGE, 2)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                    finish()
                    true
                }
                else -> false
            }
        }
    }
}
