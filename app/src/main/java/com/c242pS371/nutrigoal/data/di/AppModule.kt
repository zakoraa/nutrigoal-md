package com.c242pS371.nutrigoal.data.di

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.room.Room
import com.c242pS371.nutrigoal.BuildConfig
import com.c242pS371.nutrigoal.data.local.database.AuthPreference
import com.c242pS371.nutrigoal.data.local.database.NotificationDao
import com.c242pS371.nutrigoal.data.local.database.NotificationLocalEntityRoomDatabase
import com.c242pS371.nutrigoal.data.local.database.SettingPreference
import com.c242pS371.nutrigoal.data.local.database.authDataStore
import com.c242pS371.nutrigoal.data.local.database.settingDataStore
import com.c242pS371.nutrigoal.data.local.repository.NotificationRepository
import com.c242pS371.nutrigoal.data.remote.repository.AuthRepository
import com.c242pS371.nutrigoal.data.remote.repository.HistoryRepository
import com.c242pS371.nutrigoal.data.remote.repository.SurveyRepository
import com.c242pS371.nutrigoal.data.remote.retrofit.SurveyService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        } else {
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE)
        }
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }


    @Provides
    @Singleton
    fun provideExecutorService(): ExecutorService {
        return Executors.newSingleThreadExecutor()
    }

    @Provides
    @Singleton
    fun provideNotificationDatabase(context: Context): NotificationLocalEntityRoomDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            NotificationLocalEntityRoomDatabase::class.java,
            "notification_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideNotificationDao(database: NotificationLocalEntityRoomDatabase): NotificationDao {
        return database.NotificationDao()
    }

    @Provides
    @Singleton
    fun provideContext(application: Application): Context = application.applicationContext

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideAuthRepository(
        context: Context,
        auth: FirebaseAuth,
        authPreference: AuthPreference,
        settingPreference: SettingPreference,
    ): AuthRepository =
        AuthRepository(context, auth, authPreference, settingPreference)

    @Provides
    @Singleton
    fun provideNotificationRepository(
        notificationDao: NotificationDao,
    ): NotificationRepository {
        return NotificationRepository(notificationDao)
    }

    @Provides
    @Singleton
    fun provideSurveyService(retrofit: Retrofit): SurveyService {
        return retrofit.create(SurveyService::class.java)
    }

    @Provides
    @Singleton
    fun provideSurveyRepository(surveyService: SurveyService): SurveyRepository =
        SurveyRepository(surveyService)

    @Provides
    @Singleton
    fun provideHistoryRepository(firestore: FirebaseFirestore): HistoryRepository =
        HistoryRepository(firestore)

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
