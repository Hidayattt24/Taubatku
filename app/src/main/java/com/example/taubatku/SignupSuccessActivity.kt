package com.example.taubatku

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class SignupSuccessActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup_success)

        // Hide action bar
        supportActionBar?.hide()

        // Get username from intent
        val username = intent.getStringExtra("username") ?: "User"
        
        // Update welcome message
        val welcomeMessage = findViewById<TextView>(R.id.welcomeMessage)
        welcomeMessage.text = "Welcome, $username!"

        // Handle continue button
        val btnContinue = findViewById<MaterialButton>(R.id.btnContinue)
        btnContinue.setOnClickListener {
            navigateToMain()
        }

        // Auto navigate after delay
        Handler(Looper.getMainLooper()).postDelayed({
            if (!isFinishing) {
                navigateToMain()
            }
        }, 2000) // 2 seconds delay
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        // Disable back button
        return
    }
} 