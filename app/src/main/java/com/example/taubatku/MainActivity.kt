package com.example.taubatku

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.example.taubatku.api.PrayerTimeService
import com.example.taubatku.data.PrayerTimesViewModel
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
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
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
    private val viewModel: PrayerTimesViewModel by viewModels()
    private val handler = Handler(Looper.getMainLooper())
    private val updateRunnable = object : Runnable {
        override fun run() {
            viewModel.fetchPrayerTimes()
            handler.postDelayed(this, 1000) // Update every second
        }
    }

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

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Initialize views
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

        // Load user data
        loadUserData()
        setupBottomNavigation()
        setupObservers()
        startUpdates()
    }

    private fun loadUserData() {
        val userId = auth.currentUser?.uid ?: return
        
        db.collection("users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val username = document.getString("username") ?: "User"
                    welcomeText.text = "Assalamu'alaikum, $username"
                }
            }
    }

    private fun setupObservers() {
        // Update current prayer name and time
        viewModel.upcomingPrayer.observe(this) { prayer ->
            currentPrayerName.text = prayer
            
            // Reset all cards to default background
            listOf("Fajr", "Sunrise", "Dhuhr", "Asr", "Maghrib", "Isha").forEach { prayerName ->
                val cardId = resources.getIdentifier("${prayerName.lowercase()}Card", "id", packageName)
                findViewById<CardView>(cardId)?.setCardBackgroundColor(
                    getColor(R.color.prayer_card_default)
                )
            }

            // Highlight upcoming prayer card
            val cardId = resources.getIdentifier("${prayer.lowercase()}Card", "id", packageName)
            findViewById<CardView>(cardId)?.setCardBackgroundColor(
                getColor(R.color.white)
            )
        }

        viewModel.upcomingPrayerTime.observe(this) { time ->
            currentPrayerTime.text = time
        }

        viewModel.timeUntilNextPrayer.observe(this) { timeUntil ->
            nextPrayerText.text = "Waktu tersisa $timeUntil"
        }

        // Update all prayer times
        viewModel.prayerTimes.observe(this) { timings ->
            fajrTime.text = timings.Fajr
            sunriseTime.text = timings.Sunrise
            dhuhrTime.text = timings.Dhuhr
            asrTime.text = timings.Asr
            maghribTime.text = timings.Maghrib
            ishaTime.text = timings.Isha
        }
    }

    private fun startUpdates() {
        handler.post(updateRunnable)
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
        loadUserData() // Reload user data when returning to this screen
        // Check if user is still authenticated
        if (auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        // Refresh prayer times
        fetchPrayerTimes()
    }

    private fun fetchPrayerTimes() {
        viewModel.fetchPrayerTimes()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateRunnable)
    }
}