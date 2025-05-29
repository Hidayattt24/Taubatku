package com.example.taubatku

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class JournalEditorActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var titleEdit: EditText
    private lateinit var contentEdit: EditText
    private lateinit var backButton: ImageButton
    private lateinit var saveButton: MaterialButton
    private var journalId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_journal_editor)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Initialize views
        titleEdit = findViewById(R.id.et_title)
        contentEdit = findViewById(R.id.et_content)
        backButton = findViewById(R.id.btn_back)
        saveButton = findViewById(R.id.btn_save)

        // Get journal ID if editing existing journal
        journalId = intent.getStringExtra("journalId")
        
        // Load journal data if editing
        journalId?.let { loadJournal(it) }

        // Setup click listeners
        backButton.setOnClickListener { finish() }
        saveButton.setOnClickListener { saveJournal() }
    }

    private fun loadJournal(journalId: String) {
        db.collection("journals").document(journalId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    titleEdit.setText(document.getString("title"))
                    contentEdit.setText(document.getString("content"))
                }
            }
    }

    private fun saveJournal() {
        val title = titleEdit.text.toString().trim()
        val content = contentEdit.text.toString().trim()
        val userId = auth.currentUser?.uid

        if (title.isEmpty()) {
            titleEdit.error = "Title is required"
            return
        }

        if (content.isEmpty()) {
            contentEdit.error = "Content is required"
            return
        }

        if (userId == null) {
            Toast.makeText(this, "Please sign in to save journal", Toast.LENGTH_SHORT).show()
            return
        }

        val journalData = hashMapOf(
            "title" to title,
            "content" to content,
            "userId" to userId,
            "updatedAt" to Timestamp.now()
        )

        if (journalId == null) {
            // Create new journal
            journalData["createdAt"] = Timestamp.now()
            db.collection("journals")
                .add(journalData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Journal saved successfully", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error saving journal: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            // Update existing journal
            db.collection("journals")
                .document(journalId!!)
                .update(journalData as Map<String, Any>)
                .addOnSuccessListener {
                    Toast.makeText(this, "Journal updated successfully", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error updating journal: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}