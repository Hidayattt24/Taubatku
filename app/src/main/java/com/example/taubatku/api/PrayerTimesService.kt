package com.example.taubatku.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

data class PrayerTimesResponse(
    val data: PrayerData
)

data class PrayerData(
    val timings: PrayerTimings,
    val date: DateInfo,
    val meta: Meta
)

data class Meta(
    val timezone: String,
    val latitude: Double,
    val longitude: Double
)

data class PrayerTimings(
    val Fajr: String,
    val Sunrise: String,
    val Dhuhr: String,
    val Asr: String,
    val Maghrib: String,
    val Isha: String
)

data class DateInfo(
    val readable: String,
    val timestamp: String,
    val hijri: HijriDate
)

data class HijriDate(
    val date: String,
    val month: HijriMonth,
    val year: String
)

data class HijriMonth(
    val number: Int,
    val en: String
)

interface PrayerTimesService {
    @GET("v1/timings/{timestamp}")
    fun getPrayerTimes(
        @Path("timestamp") timestamp: String,
        @Query("latitude") latitude: Double = 5.5482904, // Banda Aceh latitude
        @Query("longitude") longitude: Double = 95.3237559, // Banda Aceh longitude
        @Query("method") method: Int = 11, // Using Sihat/Kemenag method for Indonesia
        @Query("timezone") timezone: String = "Asia/Jakarta",
        @Query("adjustment") adjustment: Int = 1 // Adjust for timezone
    ): Call<PrayerTimesResponse>
} 