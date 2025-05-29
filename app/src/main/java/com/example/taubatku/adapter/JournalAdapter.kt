package com.example.taubatku.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.taubatku.R
import com.example.taubatku.data.Journal
import com.google.android.material.button.MaterialButton
import java.text.SimpleDateFormat
import java.util.*

class JournalAdapter(
    private val journals: MutableList<Journal> = mutableListOf(),
    private val onJournalClick: (Journal) -> Unit,
    private val onDeleteClick: (Journal) -> Unit
) : RecyclerView.Adapter<JournalAdapter.JournalViewHolder>() {

    class JournalViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleText: TextView = view.findViewById(R.id.journal_title)
        val dateText: TextView = view.findViewById(R.id.journal_date)
        val openButton: MaterialButton = view.findViewById(R.id.btn_open)
        val deleteButton: MaterialButton = view.findViewById(R.id.btn_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JournalViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_journal, parent, false)
        return JournalViewHolder(view)
    }

    override fun onBindViewHolder(holder: JournalViewHolder, position: Int) {
        val journal = journals[position]
        holder.titleText.text = journal.title
        
        // Format the date
        val sdf = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
        val date = journal.createdAt.toDate()
        holder.dateText.text = sdf.format(date)

        holder.openButton.setOnClickListener {
            onJournalClick(journal)
        }

        holder.deleteButton.setOnClickListener {
            onDeleteClick(journal)
        }
    }

    override fun getItemCount() = journals.size

    fun updateJournals(newJournals: List<Journal>) {
        journals.clear()
        journals.addAll(newJournals)
        notifyDataSetChanged()
    }

    fun removeJournal(journal: Journal) {
        val position = journals.indexOfFirst { it.id == journal.id }
        if (position != -1) {
            journals.removeAt(position)
            notifyItemRemoved(position)
        }
    }
} 