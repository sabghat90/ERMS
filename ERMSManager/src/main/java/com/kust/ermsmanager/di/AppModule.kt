package com.kust.ermsmanager.di

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.kust.ermslibrary.services.NotificationService
import com.kust.ermslibrary.utils.SharedPreferencesConstants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    @Provides
    @Singleton
    fun provideSharedPref(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences(SharedPreferencesConstants.LOCAL_SHARED_PREF,Context.MODE_PRIVATE)
    }

    @Singleton
    @Provides
    fun provideGson(): Gson {
        return Gson()
    }

    @Provides
    @Singleton
    fun provideNotificationRepository(): NotificationService {
        return NotificationService()
    }
}