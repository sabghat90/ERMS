package com.kust.ermsmanager.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kust.ermsmanager.data.repositories.AuthRepository
import com.kust.ermsmanager.data.repositories.AuthRepositoryImpl
import com.kust.ermsmanager.data.repositories.EmployeeRepository
import com.kust.ermsmanager.data.repositories.EmployeeRepositoryImpl
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
        database: FirebaseFirestore
    ) : AuthRepository {
        return AuthRepositoryImpl(auth, database)
    }

    @Provides
    @Singleton
    fun provideEmployeeRepository(
        auth: FirebaseAuth,
        database: FirebaseFirestore
    ) : EmployeeRepository {
        return EmployeeRepositoryImpl(auth, database)
    }
}