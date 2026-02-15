package com.example.lastactive.ui

import android.app.Application
import android.icu.util.Calendar
import android.os.Build
import android.text.format.DateUtils
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.example.lastactive.LastActiveRepository
import com.example.lastactive.R
import java.text.SimpleDateFormat
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
class HomeViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val repository = LastActiveRepository(application)
    var activityStatus by mutableStateOf("")
        private set

    init {
        refreshStatus()
    }

    fun saveBackgroundTimestamp() {
        repository.saveLastActive(System.currentTimeMillis())
    }

    fun refreshStatus() {
        val timestamp = repository.getLastActive()
        val context = getApplication<Application>().applicationContext

        if (timestamp == null) {
            activityStatus = context.getString(R.string.not_active_yet)
            return
        }

        when {
            DateUtils.isToday(timestamp) -> {
                val format = SimpleDateFormat("HH:mm", Locale.getDefault())
                activityStatus = context.getString(R.string.last_active_today, format.format(timestamp))
            }

            isYesterday(timestamp) -> {
                activityStatus = context.getString(R.string.last_active_yesterday)
            }

            else -> {
                val format = SimpleDateFormat("MMM dd", Locale.getDefault())
                activityStatus = context.getString(R.string.last_active_old, format.format(timestamp))
            }
        }
    }

    private fun isYesterday(timestamp: Long): Boolean {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -1)

        val yesterdayYear = calendar.get(Calendar.YEAR)
        val yesterdayDay = calendar.get(Calendar.DAY_OF_YEAR)

        val last = Calendar.getInstance().apply {
            timeInMillis = timestamp
        }
        return yesterdayYear == last.get(Calendar.YEAR) &&
                yesterdayDay == last.get(Calendar.DAY_OF_YEAR)
    }
}
