package com.kust.ermsmanager.di.modules

import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson
import com.kust.ermslibrary.services.NotificationService
import com.kust.ermsmanager.data.repositories.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object RepositoryModule {

    @Provides
    @Singleton
    fun provideAuthRepository(
        auth: FirebaseAuth,
        database: FirebaseFirestore,
        sharedPreferences: SharedPreferences,
        gson: Gson,
        firebaseMessaging: FirebaseMessaging
    ) : AuthRepository {
        return AuthRepositoryImpl(auth, database, sharedPreferences, gson, firebaseMessaging)
    }
}