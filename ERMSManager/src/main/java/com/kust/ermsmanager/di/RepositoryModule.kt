package com.kust.ermsmanager.di

import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.kust.ermsmanager.data.repositories.*
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
        database: FirebaseFirestore,
        sharedPreferences: SharedPreferences,
        gson: Gson
    ) : AuthRepository {
        return AuthRepositoryImpl(auth, database, sharedPreferences, gson)
    }

    @Provides
    @Singleton
    fun provideEmployeeRepository(
        auth: FirebaseAuth,
        database: FirebaseFirestore
    ) : EmployeeRepository {
        return EmployeeRepositoryImpl(auth, database)
    }

    @Provides
    @Singleton
    fun provideTaskRepository(
        auth: FirebaseAuth,
        database: FirebaseFirestore
    ) : TaskRepository {
        return TaskRepositoryImpl(auth, database)
    }
}