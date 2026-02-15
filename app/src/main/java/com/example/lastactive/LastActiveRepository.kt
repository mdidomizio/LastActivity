package com.example.lastactive

import android.content.Context

class LastActiveRepository (context: Context){
    private val prefs =
        context.getSharedPreferences("last_active_prefs", Context.MODE_PRIVATE)
    fun saveLastActive(timestamp: Long) {
        prefs.edit()
            .putLong("last_active", timestamp)
            .apply()
    }

    fun getLastActive(): Long? {
        val value = prefs.getLong("last_active", -1L)
        return if (value == -1L) null else value
    }
}