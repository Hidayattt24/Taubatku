package com.example.taubatku

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class JournalEditorActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_journal_editor)

        val btnBack = findViewById<ImageButton>(R.id.btn_back)
        val etTitle = findViewById<TextInputEditText>(R.id.et_title)
        val etContent = findViewById<TextInputEditText>(R.id.et_content)
        val btnSave = findViewById<MaterialButton>(R.id.btn_save)

        btnBack.setOnClickListener {
            finish()
        }

        btnSave.setOnClickListener {
            val title = etTitle.text.toString()
            val content = etContent.text.toString()

            if (title.isNotEmpty() && content.isNotEmpty()) {
                // Implement save journal logic here
                finish()
            }
        }
    }
}