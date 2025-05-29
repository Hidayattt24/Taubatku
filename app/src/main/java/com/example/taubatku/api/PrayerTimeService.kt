package com.example.taubatku.api

import com.example.taubatku.data.PrayerTimeResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface PrayerTimeService {
    @GET("v1/timings")
    suspend fun getPrayerTimes(
        @Query("latitude") latitude: Double = 5.5483, // Banda Aceh latitude
        @Query("longitude") longitude: Double = 95.3238, // Banda Aceh longitude
        @Query("method") method: Int = 3, // Muslim World League method
        @Query("school") school: Int = 1, // Shafi'i
        @Query("timestamp") timestamp: String? = null
    ): PrayerTimeResponse

    companion object {
        const val BASE_URL = "https://api.aladhan.com/"
    }
} 