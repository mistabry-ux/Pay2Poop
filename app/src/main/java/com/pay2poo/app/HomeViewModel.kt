package com.pay2poo.app

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.*
import com.pay2poo.app.data.AppDatabase
import com.pay2poo.app.data.PoopSession
import kotlinx.coroutines.launch
import java.util.Calendar

class HomeViewModel(app: Application) : AndroidViewModel(app) {

    private val db = AppDatabase.getInstance(app)
    val profile = db.userProfileDao().getProfile()

    private val _isSessionActive = MutableLiveData(false)
    val isSessionActive: LiveData<Boolean> = _isSessionActive

    private val _sessionStartTime = MutableLiveData(0L)
    val sessionStartTime: LiveData<Long> = _sessionStartTime

    private val _liveEarnings = MutableLiveData(0.0)
    val liveEarnings: LiveData<Double> = _liveEarnings

    private val handler = Handler(Looper.getMainLooper())
    private val earningsUpdater = object : Runnable {
        override fun run() {
            val start = _sessionStartTime.value ?: 0L
            if (start > 0) {
                val elapsedMins = (System.currentTimeMillis() - start) / 60_000.0
                val rate = profile.value?.perMinuteRate ?: 0.0
                _liveEarnings.value = elapsedMins * rate
                handler.postDelayed(this, 1000)
            }
        }
    }

    private val todayStart: Long
        get() {
            val cal = Calendar.getInstance()
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            return cal.timeInMillis
        }

    val todaySessionCount = db.poopSessionDao().getSessionsThisWeek(todayStart)
    val todayEarnings: LiveData<Double> = db.poopSessionDao().getEarningsThisWeek(todayStart).map { it ?: 0.0 }

    fun toggleSession() {
        if (_isSessionActive.value == true) {
            stopSession()
        } else {
            startSession()
        }
    }

    private fun startSession() {
        _sessionStartTime.value = System.currentTimeMillis()
        _isSessionActive.value = true
        _liveEarnings.value = 0.0
        handler.post(earningsUpdater)
    }

    private fun stopSession() {
        handler.removeCallbacks(earningsUpdater)
        val start = _sessionStartTime.value ?: return
        val end = System.currentTimeMillis()
        val durationMins = (end - start) / 60_000.0
        val earnings = durationMins * (profile.value?.perMinuteRate ?: 0.0)

        viewModelScope.launch {
            db.poopSessionDao().insertSession(
                PoopSession(startTime = start, endTime = end, durationMinutes = durationMins, earnings = earnings)
            )
        }
        _isSessionActive.value = false
        _sessionStartTime.value = 0L
        _liveEarnings.value = 0.0
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacks(earningsUpdater)
    }

    class Factory(private val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(app) as T
        }
    }
}
