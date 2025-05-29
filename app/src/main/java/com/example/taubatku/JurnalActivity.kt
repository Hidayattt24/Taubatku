package com.example.taubatku

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taubatku.adapter.JournalAdapter
import com.example.taubatku.data.Journal
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class JurnalActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var journalAdapter: JournalAdapter
    private lateinit var welcomeText: TextView
    private lateinit var createJournalButton: MaterialButton
    private lateinit var fabAdd: FloatingActionButton
    private val TAG = "JurnalActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_jurnal)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Check if user is signed in
        if (auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            overridePendingTransition(0, 0)
            finish()
            return
        }

        // Initialize views
        initializeViews()

        // Set welcome message
        welcomeText.text = "Hi! ${auth.currentUser?.displayName ?: "User"} 👋"

        // Setup other components
        setupRecyclerView()
        setupBottomNavigation()
        loadJournals()

        // Setup click listeners
        createJournalButton.setOnClickListener {
            startActivity(Intent(this, JournalEditorActivity::class.java))
        }

        fabAdd.setOnClickListener {
            startActivity(Intent(this, JournalEditorActivity::class.java))
        }
    }

    private fun initializeViews() {
        welcomeText = findViewById(R.id.welcomeText)
        createJournalButton = findViewById(R.id.btn_create_journal)
        fabAdd = findViewById(R.id.fab_add)
    }

    private fun setupRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.journal_list)
        journalAdapter = JournalAdapter(
            onJournalClick = { journal ->
                // Handle journal click
                val intent = Intent(this, JournalEditorActivity::class.java).apply {
                    putExtra("journalId", journal.id)
                }
                startActivity(intent)
            },
            onDeleteClick = { journal ->
                // Show confirmation dialog
                AlertDialog.Builder(this)
                    .setTitle("Delete Journal")
                    .setMessage("Are you sure you want to delete this journal?")
                    .setPositiveButton("Delete") { _, _ ->
                        deleteJournal(journal)
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        )

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@JurnalActivity)
            adapter = journalAdapter
        }
    }

    private fun deleteJournal(journal: Journal) {
        db.collection("journals")
            .document(journal.id)
            .delete()
            .addOnSuccessListener {
                journalAdapter.removeJournal(journal)
                Toast.makeText(this, "Journal deleted successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error deleting journal: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadJournals() {
        val userId = auth.currentUser?.uid ?: return
        
        db.collection("journals")
            .whereEqualTo("userId", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val journals = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(Journal::class.java)?.copy(id = doc.id)
                    }
                    journalAdapter.updateJournals(journals)
                }
            }
    }

    private fun setupBottomNavigation() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.selectedItemId = R.id.nav_journal

        bottomNav.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_prayer -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.nav_journal -> true
                R.id.nav_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadJournals()
    }
}