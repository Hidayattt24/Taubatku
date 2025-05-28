package com.example.taubatku.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    companion object {
        private const val PREF_NAME = "TaubatkuPref"
        private const val IS_FIRST_TIME = "IsFirstTime"
        private const val IS_LOGGED_IN = "IsLoggedIn"
        private const val KEY_USERNAME = "username"
    }

    fun setFirstTimeStatus(isFirstTime: Boolean) {
        editor.putBoolean(IS_FIRST_TIME, isFirstTime)
        editor.commit()
    }

    fun isFirstTime(): Boolean {
        return sharedPreferences.getBoolean(IS_FIRST_TIME, true)
    }

    fun setLogin(username: String) {
        editor.putBoolean(IS_LOGGED_IN, true)
        editor.putString(KEY_USERNAME, username)
        editor.commit()
    }

    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(IS_LOGGED_IN, false)
    }

    fun logout() {
        editor.clear()
        editor.commit()
    }
}