package com.example.habbitmate.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.habbitmate.R

class LoginActivity : AppCompatActivity() {

    private lateinit var btnLogin: Button
    private lateinit var linkSignup: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btnLogin = findViewById(R.id.btnLogin)
    // signup link moved to bottom of the layout
    linkSignup = findViewById(R.id.linkSignupBottom)

        btnLogin.setOnClickListener {
            // For now, go to MainActivity after login (replace with real auth)
            val intent = Intent(this, com.example.habbitmate.MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        linkSignup.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }
    }
}
