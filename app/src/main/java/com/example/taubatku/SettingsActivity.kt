package com.example.taubatku

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val btnChangePhoto = findViewById<MaterialButton>(R.id.btn_change_photo)
        val btnSaveChanges = findViewById<MaterialButton>(R.id.btn_save_changes)
        val btnLogout = findViewById<MaterialButton>(R.id.btn_logout)
        val etUsername = findViewById<TextInputEditText>(R.id.et_username)
        val etPassword = findViewById<TextInputEditText>(R.id.et_password)

        bottomNav.selectedItemId = R.id.nav_settings

        bottomNav.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_prayer -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                    false
                }
                R.id.nav_journal -> {
                    startActivity(Intent(this, JurnalActivity::class.java))
                    finish()
                    false
                }
                R.id.nav_settings -> true
                else -> false
            }
        }

        btnChangePhoto.setOnClickListener {
            // Implement photo picker logic here
            Toast.makeText(this, "Change photo clicked", Toast.LENGTH_SHORT).show()
        }

        btnSaveChanges.setOnClickListener {
            val username = etUsername.text.toString()
            val password = etPassword.text.toString()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                // Implement save changes logic here
                Toast.makeText(this, "Changes saved", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        btnLogout.setOnClickListener {
            // Implement logout logic here
            startActivity(Intent(this, LoginActivity::class.java))
            finishAffinity()
        }
    }
}