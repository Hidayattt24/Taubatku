package com.example.taubatku.data

import com.example.taubatku.api.PrayerTimesResponse
import com.example.taubatku.api.PrayerTimesService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PrayerTimesRepository {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.aladhan.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val service = retrofit.create(PrayerTimesService::class.java)

    fun getPrayerTimes(timestamp: String, callback: (PrayerTimesResponse?) -> Unit) {
        service.getPrayerTimes(timestamp).enqueue(object : retrofit2.Callback<PrayerTimesResponse> {
            override fun onResponse(
                call: retrofit2.Call<PrayerTimesResponse>,
                response: retrofit2.Response<PrayerTimesResponse>
            ) {
                if (response.isSuccessful) {
                    callback(response.body())
                } else {
                    callback(null)
                }
            }

            override fun onFailure(call: retrofit2.Call<PrayerTimesResponse>, t: Throwable) {
                callback(null)
            }
        })
    }
} 