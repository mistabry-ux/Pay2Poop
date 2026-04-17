package com.pay2poo.app.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface PoopSessionDao {
    @Query("SELECT * FROM poop_sessions ORDER BY startTime DESC")
    fun getAllSessions(): LiveData<List<PoopSession>>

    @Query("SELECT * FROM poop_sessions ORDER BY startTime DESC LIMIT 10")
    fun getRecentSessions(): LiveData<List<PoopSession>>

    @Query("SELECT COUNT(*) FROM poop_sessions")
    fun getTotalSessionCount(): LiveData<Int>

    @Query("SELECT SUM(earnings) FROM poop_sessions")
    fun getTotalEarnings(): LiveData<Double?>

    @Query("SELECT AVG(durationMinutes) FROM poop_sessions")
    fun getAverageDuration(): LiveData<Double?>

    @Query("SELECT SUM(durationMinutes) FROM poop_sessions")
    fun getTotalDuration(): LiveData<Double?>

    @Query("SELECT COUNT(*) FROM poop_sessions WHERE startTime >= :weekStart")
    fun getSessionsThisWeek(weekStart: Long): LiveData<Int>

    @Query("SELECT SUM(earnings) FROM poop_sessions WHERE startTime >= :weekStart")
    fun getEarningsThisWeek(weekStart: Long): LiveData<Double?>

    @Insert
    suspend fun insertSession(session: PoopSession): Long

    @Delete
    suspend fun deleteSession(session: PoopSession)

    @Query("DELETE FROM poop_sessions")
    suspend fun deleteAllSessions()
}
