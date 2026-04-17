package com.pay2poo.app

import android.app.Application
import androidx.lifecycle.*
import com.pay2poo.app.data.AppDatabase
import com.pay2poo.app.data.PoopSession
import kotlinx.coroutines.launch
import java.util.Calendar

class MetricsViewModel(app: Application) : AndroidViewModel(app) {

    private val db = AppDatabase.getInstance(app)

    val totalSessions = db.poopSessionDao().getTotalSessionCount()
    val totalEarnings: LiveData<Double> = db.poopSessionDao().getTotalEarnings().map { it ?: 0.0 }
    val totalDuration: LiveData<Double> = db.poopSessionDao().getTotalDuration().map { it ?: 0.0 }
    val avgDuration: LiveData<Double> = db.poopSessionDao().getAverageDuration().map { it ?: 0.0 }
    val recentSessions = db.poopSessionDao().getRecentSessions()

    private val weekStart: Long
        get() {
            val cal = Calendar.getInstance()
            cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            return cal.timeInMillis
        }

    val weekSessions = db.poopSessionDao().getSessionsThisWeek(weekStart)
    val weekEarnings: LiveData<Double> = db.poopSessionDao().getEarningsThisWeek(weekStart).map { it ?: 0.0 }

    fun deleteSession(session: PoopSession) {
        viewModelScope.launch { db.poopSessionDao().deleteSession(session) }
    }

    fun resetAll() {
        viewModelScope.launch {
            db.poopSessionDao().deleteAllSessions()
            db.userProfileDao().deleteAll()
        }
    }

    class Factory(private val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return MetricsViewModel(app) as T
        }
    }
}
