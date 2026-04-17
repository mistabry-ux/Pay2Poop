package com.pay2poo.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val age: Int,
    val occupation: String,
    val hoursPerWeek: Float,
    val annualSalary: Double
) {
    val hourlyRate: Double
        get() = annualSalary / (52.0 * hoursPerWeek)

    val perMinuteRate: Double
        get() = hourlyRate / 60.0
}
