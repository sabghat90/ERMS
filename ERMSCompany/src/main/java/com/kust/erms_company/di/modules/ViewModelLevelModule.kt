package com.kust.erms_company.di.modules

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.kust.erms_company.data.repositroy.ChatRepository
import com.kust.erms_company.data.repositroy.ChatRepositoryImpl
import com.kust.erms_company.data.repositroy.CompanyRepository
import com.kust.erms_company.data.repositroy.CompanyRepositoryImpl
import com.kust.erms_company.data.repositroy.ComplaintRepository
import com.kust.erms_company.data.repositroy.ComplaintRepositoryImpl
import com.kust.erms_company.data.repositroy.EmployeeRepository
import com.kust.erms_company.data.repositroy.EmployeeRepositoryImpl
import com.kust.ermslibrary.utils.FirebaseStorageConstants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(ViewModelComponent::class)
object ViewModelLevelModule {

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
    fun provideEmployeeRepository(
        auth: FirebaseAuth,
        database: FirebaseFirestore,
    ): EmployeeRepository {
        return EmployeeRepositoryImpl(auth, database)
    }

    @Provides
    @ViewModelScoped
    fun provideCompanyRepository(
        auth: FirebaseAuth,
        database: FirebaseFirestore,
        @Named(FirebaseStorageConstants.COMPANY_PROFILE)
        storage: StorageReference
    ): CompanyRepository {
        return CompanyRepositoryImpl(auth, database, storage)
    }

    @Provides
    @ViewModelScoped
    fun provideComplaintRepository(
        database: FirebaseFirestore,
        auth: FirebaseAuth
    ) : ComplaintRepository {
        return ComplaintRepositoryImpl(database, auth)
    }
}