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

class SignupActivity : AppCompatActivity() {

    private lateinit var btnSignup: Button
    private lateinit var linkLoginBottom: TextView
    private lateinit var editName: EditText
    private lateinit var editEmailSignup: EditText
    private lateinit var editPasswordSignup: EditText
    private lateinit var editConfirmPasswordSignup: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

    btnSignup = findViewById(R.id.btnSignup)
    linkLoginBottom = findViewById(R.id.linkLoginBottom)
    editName = findViewById(R.id.editName)
    editEmailSignup = findViewById(R.id.editEmailSignup)
    editPasswordSignup = findViewById(R.id.editPasswordSignup)
    editConfirmPasswordSignup = findViewById(R.id.editConfirmPasswordSignup)

    val preferencesHelper = PreferencesHelper(this)

        btnSignup.setOnClickListener {
            val name = editName.text.toString().trim()
            val email = editEmailSignup.text.toString().trim()
            val password = editPasswordSignup.text.toString()
            val confirm = editConfirmPasswordSignup.text.toString()

            if (email.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirm) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Save credentials locally (for demo). In production, use secure server auth.
            preferencesHelper.saveUserCredentials(email, password, if (name.isEmpty()) null else name)

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
