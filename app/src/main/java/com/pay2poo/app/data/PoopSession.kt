package com.pay2poo.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "poop_sessions")
data class PoopSession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val startTime: Long,
    val endTime: Long,
    val durationMinutes: Double,
    val earnings: Double,
    val note: String = ""
) {
    val formattedDuration: String
        get() {
            val totalSecs = (durationMinutes * 60).toLong()
            val mins = totalSecs / 60
            val secs = totalSecs % 60
            return if (mins > 0) "${mins}m ${secs}s" else "${secs}s"
        }
}
