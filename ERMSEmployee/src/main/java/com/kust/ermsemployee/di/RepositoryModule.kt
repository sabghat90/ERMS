package com.kust.ermsemployee.di

import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.kust.ermsemployee.data.repository.AuthRepository
import com.kust.ermsemployee.data.repository.AuthRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
object RepositoryModule {

    @Provides
    @Singleton
    fun provideAuthRepository(
        auth: FirebaseAuth,
        database : FirebaseFirestore,
        sharedPreferences: SharedPreferences,
        gson: Gson
    ) : AuthRepository {
        return AuthRepositoryImpl(auth, database, sharedPreferences, gson)
    }
}