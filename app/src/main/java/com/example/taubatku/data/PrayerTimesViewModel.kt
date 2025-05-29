package com.example.taubatku.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.taubatku.api.PrayerTimings
import java.text.SimpleDateFormat
import java.util.*

class PrayerTimesViewModel : ViewModel() {
    private val repository = PrayerTimesRepository()
    private val _prayerTimes = MutableLiveData<PrayerTimings>()
    val prayerTimes: LiveData<PrayerTimings> = _prayerTimes

    private val _upcomingPrayer = MutableLiveData<String>()
    val upcomingPrayer: LiveData<String> = _upcomingPrayer

    private val _upcomingPrayerTime = MutableLiveData<String>()
    val upcomingPrayerTime: LiveData<String> = _upcomingPrayerTime

    private val _timeUntilNextPrayer = MutableLiveData<String>()
    val timeUntilNextPrayer: LiveData<String> = _timeUntilNextPrayer

    fun fetchPrayerTimes() {
        val timestamp = System.currentTimeMillis() / 1000
        repository.getPrayerTimes(timestamp.toString()) { response ->
            response?.let {
                _prayerTimes.postValue(it.data.timings)
                updateUpcomingPrayer(it.data.timings)
            }
        }
    }

    private fun updateUpcomingPrayer(timings: PrayerTimings) {
        val timeZone = TimeZone.getTimeZone("Asia/Jakarta")
        val currentTime = Calendar.getInstance(timeZone)
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault()).apply {
            this.timeZone = timeZone
        }
        
        // List of main prayers (excluding Sunrise as it's not a prayer time)
        val prayerTimes = listOf(
            "Fajr" to timeFormat.parse(timings.Fajr),
            "Dhuhr" to timeFormat.parse(timings.Dhuhr),
            "Asr" to timeFormat.parse(timings.Asr),
            "Maghrib" to timeFormat.parse(timings.Maghrib),
            "Isha" to timeFormat.parse(timings.Isha)
        )

        // Find upcoming prayer
        val upcomingPrayerIndex = prayerTimes.indexOfFirst { (_, time) ->
            val prayerCal = Calendar.getInstance(timeZone).apply {
                time?.let { prayerTime ->
                    this.time = prayerTime
                    set(
                        currentTime.get(Calendar.YEAR),
                        currentTime.get(Calendar.MONTH),
                        currentTime.get(Calendar.DAY_OF_MONTH)
                    )
                }
            }
            currentTime.timeInMillis < prayerCal.timeInMillis
        }.let { index ->
            if (index == -1) 0 else index // If no upcoming prayer found today, show first prayer of next day
        }

        val upcomingPrayer = prayerTimes[upcomingPrayerIndex]
        _upcomingPrayer.postValue(upcomingPrayer.first)
        _upcomingPrayerTime.postValue(timeFormat.format(upcomingPrayer.second!!))
        
        // Calculate time until upcoming prayer
        val upcomingPrayerCal = Calendar.getInstance(timeZone)
        upcomingPrayer.second?.let { time ->
            upcomingPrayerCal.time = time
            upcomingPrayerCal.set(
                currentTime.get(Calendar.YEAR),
                currentTime.get(Calendar.MONTH),
                currentTime.get(Calendar.DAY_OF_MONTH)
            )
            
            // If upcoming prayer is tomorrow
            if (upcomingPrayerCal.before(currentTime)) {
                upcomingPrayerCal.add(Calendar.DAY_OF_MONTH, 1)
            }
            
            val diffMillis = upcomingPrayerCal.timeInMillis - currentTime.timeInMillis
            
            val hours = diffMillis / (60 * 60 * 1000)
            val minutes = (diffMillis % (60 * 60 * 1000)) / (60 * 1000)
            
            // Format the time in a more readable way
            val timeUntil = when {
                hours > 0 -> "$hours jam ${minutes} menit"
                minutes > 0 -> "$minutes menit"
                else -> "kurang dari 1 menit"
            }
            
            _timeUntilNextPrayer.postValue(timeUntil)
        }
    }
} 