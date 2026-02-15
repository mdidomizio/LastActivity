package com.example.lastactive.ui

import android.app.Application
import android.icu.util.Calendar
import android.os.Build
import android.text.format.DateUtils
import androidx.annotation.RequiresApi
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.example.lastactive.LastActiveRepository
import com.example.lastactive.R
import java.text.SimpleDateFormat
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
class HomeViewModel(
    application: Application
) : AndroidViewModel(application), DefaultLifecycleObserver {
    private val repository = LastActiveRepository(application)
    private val _activityStatus = mutableStateOf("No Activity yet")
    val activityStatus: State<String> = _activityStatus

    init {
        ProcessLifecycleOwner.get()
            .lifecycle
            .addObserver(this)

        updateActivityText()
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        val now = System.currentTimeMillis()
        repository.saveLastActive(now)
    }

    override fun onStart(owner: LifecycleOwner) {
        updateActivityText()
    }

    private fun updateActivityText() {
        val timestamp =  repository.getLastActive()
        val context = getApplication<Application>()
        if (timestamp == null) {
            _activityStatus.value = context.getString(R.string.not_active_yet)
            return
        }
        val now = Calendar.getInstance()
        val lastActive = Calendar.getInstance().apply {
            timeInMillis = timestamp
        }

        when {
            DateUtils.isToday(timestamp) -> {
                val format = SimpleDateFormat("HH:mm", Locale.getDefault())
                _activityStatus.value =
                    context.getString(R.string.last_active_today, format.format(timestamp))
            }

            isYesterday(lastActive, now) -> {
                _activityStatus.value = context.getString(R.string.last_active_yesterday)
            }
            else -> {
                val format = SimpleDateFormat("MMM dd", Locale.getDefault())
                _activityStatus.value =
                    context.getString(R.string.last_active_old, format.format(timestamp))

            }
        }
    }

    private fun isYesterday(
        last: Calendar,
        now: Calendar
    ): Boolean {
        val yesterday = (now.clone() as Calendar).apply{ add(Calendar.DAY_OF_YEAR, -1) }
        return yesterday.get(Calendar.YEAR) == last.get(Calendar.YEAR) &&
                yesterday.get(Calendar.DAY_OF_YEAR) == last.get(Calendar.DAY_OF_YEAR)
    }
}