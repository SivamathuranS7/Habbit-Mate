package com.example.habbitmate.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.habbitmate.R

class SignupActivity : AppCompatActivity() {

    private lateinit var btnSignup: Button
    private lateinit var linkLoginBottom: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

    btnSignup = findViewById(R.id.btnSignup)
    linkLoginBottom = findViewById(R.id.linkLoginBottom)

        btnSignup.setOnClickListener {
            // For simplicity, after signup go to MainActivity
            val intent = Intent(this, com.example.habbitmate.MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        linkLoginBottom.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
