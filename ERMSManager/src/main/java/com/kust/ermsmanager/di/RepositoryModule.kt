package com.kust.ermsmanager.di

import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson
import com.kust.ermsmanager.data.repositories.*
import com.kust.ermsmanager.services.NotificationService
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

    @Provides
    @Singleton
    fun provideEmployeeRepository(
        auth: FirebaseAuth,
        database: FirebaseFirestore,
        @Named("employeeProfile")
        firebaseStorage: StorageReference
    ) : EmployeeRepository {
        return EmployeeRepositoryImpl(auth, database, firebaseStorage)
    }

    @Provides
    @Singleton
    fun provideTaskRepository(
        database: FirebaseFirestore,
        sharedPreferences: SharedPreferences
    ) : TaskRepository {
        return TaskRepositoryImpl(database, sharedPreferences)
    }

    @Provides
    @Singleton
    fun provideAttendanceRepository (
        database : FirebaseDatabase,
        auth : FirebaseAuth
    ) : AttendanceRepository {
        return AttendanceRepositoryImpl(database, auth)
    }

    @Provides
    @Singleton
    fun provideNotificationRepository(): NotificationService {
        return NotificationService()
    }
}