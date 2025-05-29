package com.example.taubatku

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taubatku.adapter.CalendarAdapter
import java.text.SimpleDateFormat
import java.util.*

class MonthlyCalendarActivity : AppCompatActivity() {
    private lateinit var monthYearText: TextView
    private lateinit var calendarRecyclerView: RecyclerView
    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_monthly_calendar)

        // Initialize views
        monthYearText = findViewById(R.id.monthYearText)
        calendarRecyclerView = findViewById(R.id.calendarRecyclerView)

        // Setup back button
        findViewById<ImageButton>(R.id.backButton).setOnClickListener {
            finish()
        }

        // Setup calendar
        setupCalendar()
    }

    private fun setupCalendar() {
        // Set month and year text
        val dateFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        monthYearText.text = dateFormat.format(calendar.time)

        // Get days in month
        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        
        // Get first day of month
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1

        // Create days list
        val days = mutableListOf<String>()
        
        // Add empty spaces for days before first day of month
        repeat(firstDayOfWeek) {
            days.add("")
        }
        
        // Add days of month
        for (i in 1..daysInMonth) {
            days.add(i.toString())
        }

        // Setup RecyclerView
        calendarRecyclerView.layoutManager = GridLayoutManager(this, 7)
        calendarRecyclerView.adapter = CalendarAdapter(days)
    }
} 