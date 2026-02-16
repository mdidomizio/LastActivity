package com.example.lastactive.ui

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.icu.util.Calendar
import android.os.Build
import android.text.format.DateUtils
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.lastactive.LastActiveRepository
import com.example.lastactive.R
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
class HomeViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val repository = LastActiveRepository(application)
    @SuppressLint("StaticFieldLeak")
    val context: Context? = getApplication<Application>().applicationContext
    var activityStatus: StateFlow<String> =
        repository.lastActiveFlow
            .map { timestamp ->
                formatTimestamp(timestamp)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = context?.getString(R.string.not_active_yet) ?: ""
            )
    fun saveBackgroundTimestamp(){
        viewModelScope.launch {
            repository.saveLastActive(System.currentTimeMillis())
        }
    }

    private fun formatTimestamp(timestamp: Long?): String {
        if (timestamp == null) return context?.getString(R.string.not_active_yet) ?: ""

        return when {
            DateUtils.isToday(timestamp) -> {
                val format = SimpleDateFormat("HH:mm", Locale.getDefault())
                context?.getString(
                    R.string.last_active_today,
                    format.format(timestamp)
                ) ?: ""
            }

            isYesterday(timestamp) -> {
                context?.getString(R.string.last_active_yesterday) ?: ""
            }

            else -> {
                val format = SimpleDateFormat("MMM dd", Locale.getDefault())
                context?.getString(
                    R.string.last_active_old,
                    format.format(timestamp)
                ) ?: ""
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
