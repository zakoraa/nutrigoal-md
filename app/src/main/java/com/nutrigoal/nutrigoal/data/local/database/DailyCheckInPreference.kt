package com.nutrigoal.nutrigoal.data.local.database

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

val Context.dailyCheckInDataStore: DataStore<Preferences> by preferencesDataStore(name = "daily_check_in")

class DailyCheckInPreference private constructor(private val dataStore: DataStore<Preferences>) {

    private suspend fun getLastCheckInDate(): String? {
        val preferences = dataStore.data.first()
        return preferences[LAST_CHECK_IN_DATE_KEY]
    }

    suspend fun saveCheckInDate() {
        val currentDate = getCurrentDate()
        dataStore.edit { preferences ->
            preferences[LAST_CHECK_IN_DATE_KEY] = currentDate
        }
    }

    suspend fun hasCheckedInToday(): Boolean {
        val lastCheckInDate = getLastCheckInDate()
        val currentDate = getCurrentDate()
        return lastCheckInDate == currentDate
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }

    companion object {
        @Volatile
        private var INSTANCE: DailyCheckInPreference? = null
        private val LAST_CHECK_IN_DATE_KEY = stringPreferencesKey("last_check_in_date")

        fun getInstance(dataStore: DataStore<Preferences>): DailyCheckInPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = DailyCheckInPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}
