package com.c242pS371.nutrigoal.data.local.database

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.settingDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingPreference private constructor(private val dataStore: DataStore<Preferences>) {

    fun getThemeSetting(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[THEME_KEY] ?: false
        }
    }

    suspend fun saveThemeSetting(isDarkModeActive: Boolean) {
        dataStore.edit { preferences ->
            preferences[THEME_KEY] = isDarkModeActive
        }
    }

    fun getDailyReminderSetting(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[DAILY_REMINDER_KEY] ?: false
        }
    }

    suspend fun saveDailyReminderSetting(isEnabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[DAILY_REMINDER_KEY] = isEnabled
        }
    }

    suspend fun clearSettings() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    companion object {
        private val THEME_KEY = booleanPreferencesKey("theme_setting")
        private val DAILY_REMINDER_KEY = booleanPreferencesKey("daily_reminder_setting")
        @Volatile
        private var INSTANCE: SettingPreference? = null

        fun getInstance(dataStore: DataStore<Preferences>): SettingPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = SettingPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }

}