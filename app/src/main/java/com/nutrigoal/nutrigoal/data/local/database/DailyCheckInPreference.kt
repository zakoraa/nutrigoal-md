package com.nutrigoal.nutrigoal.data.local.database

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

val Context.dailyCheckInDataStore: DataStore<Preferences> by preferencesDataStore(name = "daily_check_in")

class DailyCheckInPreference private constructor(private val dataStore: DataStore<Preferences>) {

    private suspend fun getLastCheckInDate(): String? {
        val preferences = dataStore.data.first()
        return preferences[LAST_CHECK_IN_DATE_KEY]
    }

    suspend fun saveCheckInDate() {
        Log.d("FLORAAAA", "JALAN TAKKK?? ")

        val currentDate = getCurrentDate()
        dataStore.edit { preferences ->
            preferences[LAST_CHECK_IN_DATE_KEY] = currentDate
        }
    }

    suspend fun hasCheckedInToday(): Boolean {
        val lastCheckInDate = getLastCheckInDate()
        val currentDate = getCurrentDate()

        val currentTime = System.currentTimeMillis()
        val nineAMToday = getNineAMTimestamp(currentDate)

        val checkDate = if (currentTime < nineAMToday) {
            getPreviousDayDate(currentDate)
        } else {
            currentDate
        }

        return lastCheckInDate == checkDate
    }

    private fun getPreviousDayDate(date: String): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currentDate = dateFormat.parse(date)
        val calendar = Calendar.getInstance().apply {
            if (currentDate != null) {
                time = currentDate
            }
            add(Calendar.DAY_OF_YEAR, -1)
        }
        return dateFormat.format(calendar.time)
    }

    private fun getNineAMTimestamp(date: String): Long {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val nineAMDate = "$date 09:00:00"
        return dateFormat.parse(nineAMDate)?.time ?: 0
    }


    private fun getNextDayDate(date: String): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currentDate = dateFormat.parse(date)
        val calendar = Calendar.getInstance().apply {
            if (currentDate != null) {
                time = currentDate
            }
            add(Calendar.DAY_OF_YEAR, 1)
        }
        return dateFormat.format(calendar.time)
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }

    suspend fun clearCheckInDate() {
        Log.d("FLORAAAA", "DE HEKKK TAKKK?? ")
        dataStore.edit { preferences ->
            preferences.remove(LAST_CHECK_IN_DATE_KEY)
        }
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
