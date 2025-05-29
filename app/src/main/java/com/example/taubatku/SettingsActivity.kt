package com.example.taubatku

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class SettingsActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    
    private lateinit var profileImage: ShapeableImageView
    private lateinit var btnChangePhoto: MaterialButton
    private lateinit var etUsername: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var btnSaveChanges: MaterialButton
    private lateinit var btnLogout: MaterialButton
    
    private var selectedImageUri: Uri? = null
    private val PICK_IMAGE_REQUEST = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        storage = Firebase.storage

        // Initialize views
        initializeViews()
        setupBottomNavigation()
        loadUserData()

        // Setup click listeners
        setupClickListeners()
    }

    private fun initializeViews() {
        profileImage = findViewById(R.id.profile_image)
        btnChangePhoto = findViewById(R.id.btn_change_photo)
        etUsername = findViewById(R.id.et_username)
        etPassword = findViewById(R.id.et_password)
        btnSaveChanges = findViewById(R.id.btn_save_changes)
        btnLogout = findViewById(R.id.btn_logout)
    }

    private fun setupClickListeners() {
        btnChangePhoto.setOnClickListener {
            openImagePicker()
        }

        btnSaveChanges.setOnClickListener {
            updateProfile()
        }

        btnLogout.setOnClickListener {
            showLogoutConfirmation()
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data
            uploadImage()
        }
    }

    private fun uploadImage() {
        if (selectedImageUri == null) return
        
        val userId = auth.currentUser?.uid ?: return
        val storageRef = storage.reference.child("profile_images/$userId.jpg")

        storageRef.putFile(selectedImageUri!!)
            .addOnSuccessListener { taskSnapshot ->
                storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    // Update Firestore with image URL
                    db.collection("users").document(userId)
                        .update("profileImage", downloadUri.toString())
                        .addOnSuccessListener {
                            // Load the new image
                            loadProfileImage(downloadUri.toString())
                            Toast.makeText(this, "Profile photo updated successfully", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { exception ->
                            Toast.makeText(this, "Failed to update profile: ${exception.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Failed to upload image: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadProfileImage(imageUrl: String) {
        Glide.with(this)
            .load(imageUrl)
            .placeholder(R.drawable.default_profile)
            .into(profileImage)
    }

    private fun loadUserData() {
        val userId = auth.currentUser?.uid ?: return
        
        db.collection("users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    etUsername.setText(document.getString("username"))
                    val profileImageUrl = document.getString("profileImage")
                    if (!profileImageUrl.isNullOrEmpty()) {
                        loadProfileImage(profileImageUrl)
                    }
                }
            }
    }

    private fun updateProfile() {
        val userId = auth.currentUser?.uid ?: return
        val newUsername = etUsername.text.toString()
        val newPassword = etPassword.text.toString()

        val updates = hashMapOf<String, Any>()
        
        if (newUsername.isNotEmpty()) {
            updates["username"] = newUsername
        }

        // Update Firestore
        if (updates.isNotEmpty()) {
            db.collection("users").document(userId)
                .update(updates)
                .addOnSuccessListener {
                    Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to update profile: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }

        // Update password if provided
        if (newPassword.isNotEmpty()) {
            auth.currentUser?.updatePassword(newPassword)
                ?.addOnSuccessListener {
                    Toast.makeText(this, "Password updated successfully", Toast.LENGTH_SHORT).show()
                    etPassword.text?.clear()
                }
                ?.addOnFailureListener {
                    Toast.makeText(this, "Failed to update password: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun showLogoutConfirmation() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Logout") { _, _ ->
                logout()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun logout() {
        auth.signOut()
        startActivity(Intent(this, LoginActivity::class.java))
        finishAffinity()
    }

    private fun setupBottomNavigation() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.selectedItemId = R.id.nav_settings

        bottomNav.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_prayer -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.nav_journal -> {
                    startActivity(Intent(this, JurnalActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.nav_settings -> true
                else -> false
            }
        }
    }
}