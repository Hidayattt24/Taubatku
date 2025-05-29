package com.example.taubatku

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton

class JurnalActivity : AppCompatActivity() {
    private val TAG = "JurnalActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_jurnal)

        try {
            // Hide action bar
            supportActionBar?.hide()

            setupBottomNavigation()
            setupFab()
        } catch (e: Exception) {
            Log.e(TAG, "Error in onCreate", e)
        }
    }

    private fun setupBottomNavigation() {
        try {
            val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
            bottomNav.selectedItemId = R.id.nav_journal

            bottomNav.setOnItemSelectedListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.nav_prayer -> {
                        try {
                            val intent = Intent(this, MainActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                            startActivity(intent)
                            true
                        } catch (e: Exception) {
                            Log.e(TAG, "Error navigating to Prayer", e)
                            false
                        }
                    }
                    R.id.nav_journal -> {
                        // Already on journal screen
                        true
                    }
                    R.id.nav_settings -> {
                        try {
                            val intent = Intent(this, SettingsActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                            startActivity(intent)
                            true
                        } catch (e: Exception) {
                            Log.e(TAG, "Error navigating to Settings", e)
                            false
                        }
                    }
                    else -> false
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up bottom navigation", e)
        }
    }

    private fun setupFab() {
        val fabAdd = findViewById<ExtendedFloatingActionButton>(R.id.fab_add)
        fabAdd.setOnClickListener {
            startActivity(Intent(this, JournalEditorActivity::class.java))
        }
    }
}