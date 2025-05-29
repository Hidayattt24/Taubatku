package com.example.taubatku.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.taubatku.R
import java.util.*

class CalendarAdapter(private val days: List<String>) :
    RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {

    private val today = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

    class CalendarViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dayText: TextView = view.findViewById(R.id.dayText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.calendar_day_item, parent, false)
        return CalendarViewHolder(view)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        val day = days[position]
        holder.dayText.text = day
        
        if (day.isNotEmpty()) {
            // Reset to default style
            holder.dayText.setTextAppearance(R.style.CalendarDay)
            
            // Apply today's style if it's today's date
            if (day.toInt() == today) {
                holder.dayText.setTextAppearance(R.style.CalendarDayToday)
            }
        }
    }

    override fun getItemCount() = days.size
} 