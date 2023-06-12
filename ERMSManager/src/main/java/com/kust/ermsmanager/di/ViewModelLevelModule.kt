package com.kust.ermsmanager.di

import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson
import com.kust.ermslibrary.utils.FirebaseStorageConstants
import com.kust.ermsmanager.data.repositories.AttendanceRepository
import com.kust.ermsmanager.data.repositories.AttendanceRepositoryImpl
import com.kust.ermsmanager.data.repositories.ChatRepository
import com.kust.ermsmanager.data.repositories.ChatRepositoryImpl
import com.kust.ermsmanager.data.repositories.CompanyRepository
import com.kust.ermsmanager.data.repositories.CompanyRepositoryImpl
import com.kust.ermsmanager.data.repositories.ComplaintRepository
import com.kust.ermsmanager.data.repositories.ComplaintRepositoryImpl
import com.kust.ermsmanager.data.repositories.EmployeeRepository
import com.kust.ermsmanager.data.repositories.EmployeeRepositoryImpl
import com.kust.ermsmanager.data.repositories.EventRepository
import com.kust.ermsmanager.data.repositories.EventRepositoryImpl
import com.kust.ermsmanager.data.repositories.TaskRepository
import com.kust.ermsmanager.data.repositories.TaskRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Named

@Module
@InstallIn(ViewModelComponent::class)
object ViewModelLevelModule {

    @Provides
    @ViewModelScoped
    fun provideTaskRepository(
        database: FirebaseFirestore,
        sharedPreferences: SharedPreferences
    ) : TaskRepository {
        return TaskRepositoryImpl(database, sharedPreferences)
    }

    @Provides
    @ViewModelScoped
    fun provideChatRepository(
        database: FirebaseDatabase,
        auth: FirebaseAuth
    ) : ChatRepository {
        return ChatRepositoryImpl(database, auth)
    }

    @Provides
    @ViewModelScoped
    fun provideEventRepository(
        database: FirebaseFirestore,
        sharedPreferences: SharedPreferences
    ) : EventRepository {
        return EventRepositoryImpl(database, sharedPreferences)
    }

    @Provides
    @ViewModelScoped
    fun provideEmployeeRepository(
        auth: FirebaseAuth,
        database: FirebaseFirestore,
        @Named(FirebaseStorageConstants.EMPLOYEE_PROFILE)
        firebaseStorage: StorageReference,
        sharedPreferences: SharedPreferences,
        gson: Gson
    ) : EmployeeRepository {
        return EmployeeRepositoryImpl(auth, database, firebaseStorage, sharedPreferences, gson)
    }

    @Provides
    @ViewModelScoped
    fun provideAttendanceRepository (
        database: FirebaseDatabase
    ) : AttendanceRepository {
        return AttendanceRepositoryImpl(database)
    }

    @Provides
    @ViewModelScoped
    fun provideCompanyRepository (
        database: FirebaseFirestore,
        sharedPreferences: SharedPreferences,
        gson: Gson
    ) : CompanyRepository {
        return CompanyRepositoryImpl(database, sharedPreferences, gson)
    }

    @Provides
    @ViewModelScoped
    fun provideComplaintRepository(
        database: FirebaseFirestore,
        auth: FirebaseAuth,
        sharedPreferences: SharedPreferences
    ) : ComplaintRepository {
        return ComplaintRepositoryImpl(database, auth, sharedPreferences)
    }
}