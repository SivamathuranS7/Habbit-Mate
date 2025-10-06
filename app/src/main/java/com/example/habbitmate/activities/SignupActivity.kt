package com.example.habbitmate.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.habbitmate.R

class SignupActivity : AppCompatActivity() {

    private lateinit var btnSignup: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        btnSignup = findViewById(R.id.btnSignup)

        btnSignup.setOnClickListener {
            // For simplicity, after signup go to MainActivity
            val intent = Intent(this, com.example.habbitmate.MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
