package com.example.taubatku

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.example.taubatku.api.PrayerTimeService
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var welcomeText: TextView
    private lateinit var currentPrayerName: TextView
    private lateinit var currentPrayerTime: TextView
    private lateinit var nextPrayerText: TextView
    private lateinit var fajrTime: TextView
    private lateinit var sunriseTime: TextView
    private lateinit var dhuhrTime: TextView
    private lateinit var asrTime: TextView
    private lateinit var maghribTime: TextView
    private lateinit var ishaTime: TextView
    private val TAG = "MainActivity"

    private val prayerTimeService: PrayerTimeService by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        Retrofit.Builder()
            .baseUrl(PrayerTimeService.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PrayerTimeService::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        try {
            // Initialize Firebase Auth
            auth = Firebase.auth

            // Hide action bar
            supportActionBar?.hide()

            // Get current user
            val user = auth.currentUser
            if (user == null) {
                // Not signed in, launch the Login activity
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                return
            }

            // Initialize views
            initializeViews()

            // Update welcome message with username
            val username = user.displayName ?: "User"
            welcomeText.text = "Assalamu'alaikum, $username"

            // Fetch prayer times
            fetchPrayerTimes()

            setupBottomNavigation()
        } catch (e: Exception) {
            Log.e(TAG, "Error in onCreate", e)
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun initializeViews() {
        welcomeText = findViewById(R.id.welcomeText)
        currentPrayerName = findViewById(R.id.currentPrayerName)
        currentPrayerTime = findViewById(R.id.currentPrayerTime)
        nextPrayerText = findViewById(R.id.nextPrayerText)
        fajrTime = findViewById(R.id.fajrTime)
        sunriseTime = findViewById(R.id.sunriseTime)
        dhuhrTime = findViewById(R.id.dhuhrTime)
        asrTime = findViewById(R.id.asrTime)
        maghribTime = findViewById(R.id.maghribTime)
        ishaTime = findViewById(R.id.ishaTime)
    }

    private fun fetchPrayerTimes() {
        lifecycleScope.launch {
            try {
                val response = prayerTimeService.getPrayerTimes()
                val timings = response.data.timings

                // Update UI with prayer times
                updatePrayerTimes(timings)
                updateCurrentPrayer(timings)
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching prayer times", e)
            }
        }
    }

    private fun updatePrayerTimes(timings: com.example.taubatku.data.Timings) {
        fajrTime.text = convertTo12HourFormat(timings.fajr)
        sunriseTime.text = convertTo12HourFormat(timings.sunrise)
        dhuhrTime.text = convertTo12HourFormat(timings.dhuhr)
        asrTime.text = convertTo12HourFormat(timings.asr)
        maghribTime.text = convertTo12HourFormat(timings.maghrib)
        ishaTime.text = convertTo12HourFormat(timings.isha)
    }

    private fun updateCurrentPrayer(timings: com.example.taubatku.data.Timings) {
        val currentTime = Calendar.getInstance(TimeZone.getTimeZone("Asia/Jakarta"))
        val prayerTimes = mapOf(
            "Fajr" to parseTime(timings.fajr),
            "Sunrise" to parseTime(timings.sunrise),
            "Dhuhr" to parseTime(timings.dhuhr),
            "Asr" to parseTime(timings.asr),
            "Maghrib" to parseTime(timings.maghrib),
            "Isha" to parseTime(timings.isha)
        ).toSortedMap()

        var currentPrayer = "Isha" // Default to Isha
        var nextPrayer = "Fajr"
        var nextPrayerTime = prayerTimes["Fajr"] ?: return

        for ((name, time) in prayerTimes) {
            if (currentTime.before(time)) {
                nextPrayer = name
                nextPrayerTime = time
                break
            }
            currentPrayer = name
        }

        // Update UI
        currentPrayerName.text = currentPrayer
        currentPrayerTime.text = when (currentPrayer) {
            "Fajr" -> timings.fajr
            "Sunrise" -> timings.sunrise
            "Dhuhr" -> timings.dhuhr
            "Asr" -> timings.asr
            "Maghrib" -> timings.maghrib
            else -> timings.isha
        }

        // Calculate time until next prayer
        val timeDiff = nextPrayerTime.timeInMillis - currentTime.timeInMillis
        val hours = timeDiff / (1000 * 60 * 60)
        val minutes = (timeDiff % (1000 * 60 * 60)) / (1000 * 60)
        val seconds = (timeDiff % (1000 * 60)) / 1000

        nextPrayerText.text = "Next Prayer in ${hours}:${String.format("%02d", minutes)}:${String.format("%02d", seconds)}"
    }

    private fun parseTime(timeStr: String): Calendar {
        val cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Jakarta"))
        val timeParts = timeStr.split(":")
        cal.set(Calendar.HOUR_OF_DAY, timeParts[0].toInt())
        cal.set(Calendar.MINUTE, timeParts[1].toInt())
        cal.set(Calendar.SECOND, 0)
        return cal
    }

    private fun convertTo12HourFormat(time24: String): String {
        val inputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val outputFormat = SimpleDateFormat("hh:mm", Locale.getDefault())
        val date = inputFormat.parse(time24) ?: return time24
        return outputFormat.format(date)
    }

    private fun setupBottomNavigation() {
        try {
            val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
            bottomNav.selectedItemId = R.id.nav_prayer

            bottomNav.setOnItemSelectedListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.nav_prayer -> {
                        true
                    }
                    R.id.nav_journal -> {
                        try {
                            val intent = Intent(this, JurnalActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                            startActivity(intent)
                            true
                        } catch (e: Exception) {
                            Log.e(TAG, "Error navigating to Journal", e)
                            false
                        }
                    }
                    R.id.nav_settings -> {
                        try {
                            val intent = Intent(this, SettingsActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                            startActivity(intent)
                            true
                        } catch (e: Exception) {
                            Log.e(TAG, "Error navigating to Settings", e)
                            false
                        }
                    }
                    else -> false
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up bottom navigation", e)
        }
    }

    override fun onResume() {
        super.onResume()
        // Check if user is still authenticated
        if (auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        // Refresh prayer times
        fetchPrayerTimes()
    }
}