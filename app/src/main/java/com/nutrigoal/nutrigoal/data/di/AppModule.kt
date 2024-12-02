package com.nutrigoal.nutrigoal.data.di

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.google.firebase.auth.FirebaseAuth
import com.nutrigoal.nutrigoal.data.local.database.AuthPreference
import com.nutrigoal.nutrigoal.data.local.database.SettingPreference
import com.nutrigoal.nutrigoal.data.local.database.authDataStore
import com.nutrigoal.nutrigoal.data.local.database.settingDataStore
import com.nutrigoal.nutrigoal.data.remote.repository.AuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideContext(application: Application): Context = application.applicationContext

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideAuthRepository(auth: FirebaseAuth, authPreference: AuthPreference): AuthRepository =
        AuthRepository(auth, authPreference)

    @Provides
    @Singleton
    @AuthDataStore
    fun provideAuthDataStore(context: Context): DataStore<Preferences> = context.authDataStore

    @Provides
    @Singleton
    @SettingDataStore
    fun provideSettingDataStore(context: Context): DataStore<Preferences> = context.settingDataStore


    @Provides
    @Singleton
    fun provideAuthPreference(@AuthDataStore authDataStore: DataStore<Preferences>): AuthPreference =
        AuthPreference.getInstance(authDataStore)

    @Provides
    @Singleton
    fun provideSettingPreference(@SettingDataStore settingDataStore: DataStore<Preferences>): SettingPreference =
        SettingPreference.getInstance(settingDataStore)
}
