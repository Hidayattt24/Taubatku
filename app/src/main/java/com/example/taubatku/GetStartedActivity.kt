package com.example.taubatku

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class GetStartedActivity : AppCompatActivity() {
    private val TAG = "GetStartedActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.get_started)
            Log.d(TAG, "GetStartedActivity created")

            // Hide action bar
            supportActionBar?.hide()

            val btnSignup = findViewById<MaterialButton>(R.id.btn_signup)
            val btnLogin = findViewById<MaterialButton>(R.id.btn_login)

            if (btnSignup == null || btnLogin == null) {
                Log.e(TAG, "Failed to find buttons: signup=${btnSignup != null}, login=${btnLogin != null}")
                Toast.makeText(this, "Error initializing screen", Toast.LENGTH_SHORT).show()
                return
            }

            btnSignup.setOnClickListener {
                try {
                    Log.d(TAG, "Navigating to SignupActivity")
                    startActivity(Intent(this, SignupActivity::class.java))
                } catch (e: Exception) {
                    Log.e(TAG, "Error starting SignupActivity", e)
                    Toast.makeText(this, "Error navigating to signup", Toast.LENGTH_SHORT).show()
                }
            }

            btnLogin.setOnClickListener {
                try {
                    Log.d(TAG, "Navigating to LoginActivity")
                    startActivity(Intent(this, LoginActivity::class.java))
                } catch (e: Exception) {
                    Log.e(TAG, "Error starting LoginActivity", e)
                    Toast.makeText(this, "Error navigating to login", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in onCreate", e)
            Toast.makeText(this, "Error initializing screen", Toast.LENGTH_SHORT).show()
        }
    }
}