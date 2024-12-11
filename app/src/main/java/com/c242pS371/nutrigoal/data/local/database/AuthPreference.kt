package com.c242pS371.nutrigoal.data.local.database

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.c242pS371.nutrigoal.data.local.entity.UserLocalEntity
import kotlinx.coroutines.flow.first

val Context.authDataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

class AuthPreference private constructor(private val dataStore: DataStore<Preferences>) {

    suspend fun getUserSession(): UserLocalEntity {
        val preferences = dataStore.data.first()
        return UserLocalEntity(
            preferences[USER_ID] ?: "",
            preferences[IS_LOGIN_KEY] ?: false
        )
    }

    suspend fun saveSession(userLocalEntity: UserLocalEntity) {
        dataStore.edit { preferences ->
            preferences[USER_ID] = userLocalEntity.id
            preferences[IS_LOGIN_KEY] = true
        }
    }

    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: AuthPreference? = null
        private val USER_ID = stringPreferencesKey("id")
        private val IS_LOGIN_KEY = booleanPreferencesKey("isLogin")

        fun getInstance(dataStore: DataStore<Preferences>): AuthPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = AuthPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}
