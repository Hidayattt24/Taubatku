package com.example.taubatku

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class WelcomeActivity : AppCompatActivity() {
    private val TAG = "WelcomeActivity"
    private var currentScreen = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showWelcomeScreen1()
    }

    private fun showWelcomeScreen1() {
        try {
            setContentView(R.layout.welcome_screen_1)
            supportActionBar?.hide()
            
            findViewById<ImageView>(R.id.btn_next)?.let { button ->
                button.setOnClickListener {
                    Log.d(TAG, "Navigating to welcome screen 2")
                    showWelcomeScreen2()
                }
            } ?: run {
                Log.e(TAG, "Next button not found in welcome_screen_1")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in showWelcomeScreen1", e)
        }
    }

    private fun showWelcomeScreen2() {
        try {
            setContentView(R.layout.welcome_screen_2)
            supportActionBar?.hide()
            
            findViewById<ImageView>(R.id.btn_next)?.let { button ->
                button.setOnClickListener {
                    Log.d(TAG, "Navigating to welcome screen 3")
                    showWelcomeScreen3()
                }
            }
            
            findViewById<ImageView>(R.id.btn_prev)?.let { button ->
                button.setOnClickListener {
                    Log.d(TAG, "Going back to welcome screen 1")
                    showWelcomeScreen1()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in showWelcomeScreen2", e)
        }
    }

    private fun showWelcomeScreen3() {
        try {
            setContentView(R.layout.welcome_screen_3)
            supportActionBar?.hide()
            
            findViewById<ImageView>(R.id.btn_next)?.let { button ->
                button.setOnClickListener {
                    Log.d(TAG, "Attempting to navigate to GetStartedActivity")
                    try {
                        val intent = Intent(this@WelcomeActivity, GetStartedActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(intent)
                        finish()
                    } catch (e: Exception) {
                        Log.e(TAG, "Error starting GetStartedActivity", e)
                        Toast.makeText(this, "Error navigating to next screen", Toast.LENGTH_SHORT).show()
                    }
                }
            } ?: run {
                Log.e(TAG, "Next button not found in welcome_screen_3")
            }
            
            findViewById<ImageView>(R.id.btn_prev)?.let { button ->
                button.setOnClickListener {
                    Log.d(TAG, "Going back to welcome screen 2")
                    showWelcomeScreen2()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in showWelcomeScreen3", e)
            Toast.makeText(this, "Error loading screen", Toast.LENGTH_SHORT).show()
        }
    }
} 