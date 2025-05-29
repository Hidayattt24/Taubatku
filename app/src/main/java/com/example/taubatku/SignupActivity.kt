package com.example.taubatku

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SignupActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore
    private val TAG = "SignupActivity"

    // UI components
    private lateinit var emailInput: TextInputEditText
    private lateinit var usernameInput: TextInputEditText
    private lateinit var passwordInput: TextInputEditText
    private lateinit var btnSignUp: MaterialButton
    private lateinit var progressIndicator: CircularProgressIndicator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        // Initialize Firebase Auth
        auth = Firebase.auth

        // Hide action bar
        supportActionBar?.hide()

        // Initialize UI components
        emailInput = findViewById(R.id.emailInput)
        usernameInput = findViewById(R.id.usernameInput)
        passwordInput = findViewById(R.id.passwordInput)
        btnSignUp = findViewById(R.id.btnSignUp)
        progressIndicator = findViewById(R.id.progressIndicator)
        val signInLink = findViewById<TextView>(R.id.signInLink)

        btnSignUp.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val username = usernameInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            when {
                email.isEmpty() -> {
                    emailInput.error = "Email is required"
                    emailInput.requestFocus()
                    return@setOnClickListener
                }
                username.isEmpty() -> {
                    usernameInput.error = "Username is required"
                    usernameInput.requestFocus()
                    return@setOnClickListener
                }
                password.isEmpty() -> {
                    passwordInput.error = "Password is required"
                    passwordInput.requestFocus()
                    return@setOnClickListener
                }
                password.length < 6 -> {
                    passwordInput.error = "Password must be at least 6 characters"
                    passwordInput.requestFocus()
                    return@setOnClickListener
                }
            }

            // Show loading state
            showLoading(true)

            // Create user with email and password
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign up success, update user profile and save additional data
                        Log.d(TAG, "createUserWithEmail:success")
                        val user = auth.currentUser

                        // Update user profile with username
                        val profileUpdates = userProfileChangeRequest {
                            displayName = username
                        }

                        user?.updateProfile(profileUpdates)
                            ?.addOnCompleteListener { profileTask ->
                                if (profileTask.isSuccessful) {
                                    // Try to save to Firestore, but proceed even if it fails
                                    tryToSaveUserData(user.uid, email, username)
                                } else {
                                    Log.e(TAG, "Profile update failed", profileTask.exception)
                                    // Even if profile update fails, proceed to success screen
                                    navigateToSuccessScreen(username)
                                }
                            }
                    } else {
                        // If sign up fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
                        showLoading(false)
                        Toast.makeText(
                            baseContext,
                            "Registration failed: ${task.exception?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }

        signInLink.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun tryToSaveUserData(userId: String, email: String, username: String) {
        val user = hashMapOf(
            "userId" to userId,
            "email" to email,
            "username" to username,
            "createdAt" to System.currentTimeMillis()
        )

        db.collection("users")
            .document(userId)
            .set(user)
            .addOnSuccessListener {
                Log.d(TAG, "User data saved successfully")
                navigateToSuccessScreen(username)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error saving user data", e)
                // Even if Firestore save fails, proceed to success screen
                navigateToSuccessScreen(username)
            }
    }

    private fun navigateToSuccessScreen(username: String) {
        showLoading(false)
        val intent = Intent(this, SignupSuccessActivity::class.java)
        intent.putExtra("username", username)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    private fun showLoading(isLoading: Boolean) {
        btnSignUp.isEnabled = !isLoading
        btnSignUp.text = if (isLoading) "" else "Sign Up"
        progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}