package com.example.taubatku.data

import com.google.gson.annotations.SerializedName

data class PrayerTimeResponse(
    @SerializedName("data")
    val data: PrayerData
)

data class PrayerData(
    @SerializedName("timings")
    val timings: Timings,
    @SerializedName("date")
    val date: DateInfo
)

data class Timings(
    @SerializedName("Fajr")
    val fajr: String,
    @SerializedName("Sunrise")
    val sunrise: String,
    @SerializedName("Dhuhr")
    val dhuhr: String,
    @SerializedName("Asr")
    val asr: String,
    @SerializedName("Maghrib")
    val maghrib: String,
    @SerializedName("Isha")
    val isha: String
)

data class DateInfo(
    @SerializedName("readable")
    val readable: String,
    @SerializedName("timestamp")
    val timestamp: String
) 