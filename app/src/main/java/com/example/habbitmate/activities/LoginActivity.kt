package com.example.habbitmate.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.habbitmate.R
import com.example.habbitmate.data.PreferencesHelper

class LoginActivity : AppCompatActivity() {

    private lateinit var btnLogin: Button
    private lateinit var linkSignup: TextView
    private lateinit var editEmail: EditText
    private lateinit var editPassword: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btnLogin = findViewById(R.id.btnLogin)
    // signup link moved to bottom of the layout
    linkSignup = findViewById(R.id.linkSignupBottom)
        editEmail = findViewById(R.id.editEmail)
        editPassword = findViewById(R.id.editPassword)

        val preferencesHelper = PreferencesHelper(this)

        btnLogin.setOnClickListener {
            val email = editEmail.text.toString().trim()
            val password = editPassword.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!preferencesHelper.isUserRegistered()) {
                Toast.makeText(this, "No user found. Please sign up.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val ok = preferencesHelper.verifyUserCredentials(email, password)
            if (ok) {
                val intent = Intent(this, com.example.habbitmate.MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show()
            }
        }

        linkSignup.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }
    }
}
