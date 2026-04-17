package com.pay2poo.app

import android.app.Application
import androidx.lifecycle.*
import com.pay2poo.app.data.AppDatabase
import com.pay2poo.app.data.UserProfile
import kotlinx.coroutines.launch

class SetupViewModel(app: Application) : AndroidViewModel(app) {

    private val db = AppDatabase.getInstance(app)
    val existingProfile = db.userProfileDao().getProfile()

    fun saveProfile(age: Int, occupation: String, hoursPerWeek: Float, annualSalary: Double) {
        viewModelScope.launch {
            db.userProfileDao().insertOrUpdate(
                UserProfile(age = age, occupation = occupation, hoursPerWeek = hoursPerWeek, annualSalary = annualSalary)
            )
        }
    }

    class Factory(private val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return SetupViewModel(app) as T
        }
    }
}
