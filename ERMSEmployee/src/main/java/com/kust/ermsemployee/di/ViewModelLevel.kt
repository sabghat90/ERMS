package com.kust.ermsemployee.di

import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson
import com.kust.ermsemployee.data.repository.AttendanceRepository
import com.kust.ermsemployee.data.repository.AttendanceRepositoryImpl
import com.kust.ermsemployee.data.repository.ChatRepository
import com.kust.ermsemployee.data.repository.ChatRepositoryImpl
import com.kust.ermsemployee.data.repository.CompanyRepository
import com.kust.ermsemployee.data.repository.CompanyRepositoryImpl
import com.kust.ermsemployee.data.repository.ComplaintRepository
import com.kust.ermsemployee.data.repository.ComplaintRepositoryImpl
import com.kust.ermsemployee.data.repository.EmployeeRepository
import com.kust.ermsemployee.data.repository.EmployeeRepositoryImpl
import com.kust.ermsemployee.data.repository.EventRepository
import com.kust.ermsemployee.data.repository.EventRepositoryImpl
import com.kust.ermsemployee.data.repository.TaskRepository
import com.kust.ermsemployee.data.repository.TaskRepositoryImpl
import com.kust.ermslibrary.utils.FirebaseStorageConstants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Named

@Module
@InstallIn(ViewModelComponent::class)
object ViewModelLevel {

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
    fun provideAttendanceRepository(
        firebaseDatabase: FirebaseDatabase,
        auth: FirebaseAuth,
        sharedPreferences: SharedPreferences
    ) : AttendanceRepository {
        return AttendanceRepositoryImpl(firebaseDatabase, auth, sharedPreferences)
    }

    @Provides
    @ViewModelScoped
    fun provideTaskRepository(
        database: FirebaseFirestore,
        firebaseAuth: FirebaseAuth,
        sharedPreferences: SharedPreferences
    ) : TaskRepository {
        return TaskRepositoryImpl(database,firebaseAuth, sharedPreferences)
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
    fun provideComplaintRepository(
        database: FirebaseFirestore,
        auth: FirebaseAuth
    ) : ComplaintRepository {
        return ComplaintRepositoryImpl(database, auth)
    }

    @Provides
    @ViewModelScoped
    fun provideCompanyRepository (
        database: FirebaseFirestore,
        sharedPreferences: SharedPreferences
    ) : CompanyRepository {
        return CompanyRepositoryImpl(database, sharedPreferences)
    }
}