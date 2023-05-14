package com.kust.ermsmanager.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.kust.ermsmanager.utils.FirebaseStorageConstants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth() : FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseFirestore() : FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    @Named("companyProfile")
    fun provideFirebaseStorage() : StorageReference {
        return FirebaseStorage.getInstance().getReference(FirebaseStorageConstants.COMPANY_PROFILE)
    }

    @Provides
    @Singleton
    @Named("employeeProfile")
    fun provideFirebaseStorageEmployee() : StorageReference {
        return FirebaseStorage.getInstance().getReference(FirebaseStorageConstants.EMPLOYEE_PROFILE)
    }

    @Provides
    @Singleton
    fun provideFirebaseDatabase() : FirebaseDatabase {
        return FirebaseDatabase.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseMessaging() : FirebaseMessaging {
        return FirebaseMessaging.getInstance()
    }

}