package com.kust.erms_company.di

import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.kust.erms_company.data.repositroy.*
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
        database: FirebaseFirestore,
    ): EmployeeRepository {
        return EmployeeRepositoryImpl(auth, database)
    }

    @Provides
    @Singleton
    fun provideCompanyRepository(
        auth: FirebaseAuth,
        database: FirebaseFirestore,
    ): CompanyRepository {
        return CompanyRepositoryImpl(auth, database)
    }
}