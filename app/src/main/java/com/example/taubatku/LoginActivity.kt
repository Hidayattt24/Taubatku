package com.example.taubatku

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var googleSignInClient: GoogleSignInClient
    
    private lateinit var usernameInput: TextInputEditText
    private lateinit var passwordInput: TextInputEditText
    private lateinit var btnSignIn: MaterialButton
    private lateinit var googleSignInButton: MaterialCardView
    private lateinit var progressIndicator: CircularProgressIndicator
    private lateinit var signUpLink: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("611038963099-ghsan8j72611gkiu8eo0tni8p5qv09ms.apps.googleusercontent.com")
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Initialize views
        initializeViews()
        setupClickListeners()
    }

    private fun initializeViews() {
        usernameInput = findViewById(R.id.usernameInput)
        passwordInput = findViewById(R.id.passwordInput)
        btnSignIn = findViewById(R.id.btnSignIn)
        googleSignInButton = findViewById(R.id.googleSignInButton)
        progressIndicator = findViewById(R.id.progressIndicator)
        signUpLink = findViewById(R.id.signUpLink)
    }

    private fun setupClickListeners() {
        btnSignIn.setOnClickListener {
            val email = usernameInput.text.toString()
            val password = passwordInput.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            signInWithEmail(email, password)
        }

        googleSignInButton.setOnClickListener {
            signInWithGoogle()
        }

        signUpLink.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }

    private fun signInWithEmail(email: String, password: String) {
        showLoading(true)
        
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                showLoading(false)
                if (task.isSuccessful) {
                    // Sign in success
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(this, "Authentication failed: ${task.exception?.message}",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            account.idToken?.let { token ->
                firebaseAuthWithGoogle(token)
            } ?: run {
                showLoading(false)
                Toast.makeText(this, "Error: No ID token present", Toast.LENGTH_SHORT).show()
            }
        } catch (e: ApiException) {
            showLoading(false)
            Toast.makeText(this, "Google sign in failed: ${e.message}\nError code: ${e.statusCode}", Toast.LENGTH_LONG).show()
        }
    }

    private fun signInWithGoogle() {
        showLoading(true)
        val signInIntent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                showLoading(false)
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = auth.currentUser
                    // Save user info to Firestore
                    user?.let { firebaseUser ->
                        val userMap = hashMapOf(
                            "username" to firebaseUser.displayName,
                            "email" to firebaseUser.email,
                            "profileImage" to (firebaseUser.photoUrl?.toString() ?: "")
                        )
                        
                        db.collection("users").document(firebaseUser.uid)
                            .set(userMap)
                            .addOnSuccessListener {
                                startActivity(Intent(this, MainActivity::class.java))
                                finish()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Error saving user data: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    val error = task.exception
                    Toast.makeText(this, "Authentication failed: ${error?.message}\nError type: ${error?.javaClass?.simpleName}",
                        Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun showLoading(show: Boolean) {
        progressIndicator.visibility = if (show) View.VISIBLE else View.GONE
        btnSignIn.isEnabled = !show
        googleSignInButton.isEnabled = !show
    }
}