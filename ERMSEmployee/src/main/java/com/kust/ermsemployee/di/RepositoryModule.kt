package com.kust.ermsemployee.di

import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson
import com.kust.ermsemployee.data.repository.AttendanceRepository
import com.kust.ermsemployee.data.repository.AttendanceRepositoryImpl
import com.kust.ermsemployee.data.repository.AuthRepository
import com.kust.ermsemployee.data.repository.AuthRepositoryImpl
import com.kust.ermsemployee.data.repository.TaskRepository
import com.kust.ermsemployee.data.repository.TaskRepositoryImpl
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
        gson: Gson,
        firebaseMessaging: FirebaseMessaging
    ) : AuthRepository {
        return AuthRepositoryImpl(auth, database, sharedPreferences, gson, firebaseMessaging)
    }

    @Provides
    @Singleton
    fun provideAttendanceRepository(
        firebaseDatabase: FirebaseDatabase,
        auth: FirebaseAuth
    ) : AttendanceRepository {
        return AttendanceRepositoryImpl(firebaseDatabase, auth)
    }

    @Provides
    @Singleton
    fun provideTaskRepository(
        database: FirebaseFirestore,
        firebaseAuth: FirebaseAuth,
        sharedPreferences: SharedPreferences
    ) : TaskRepository {
        return TaskRepositoryImpl(database,firebaseAuth, sharedPreferences)
    }
}