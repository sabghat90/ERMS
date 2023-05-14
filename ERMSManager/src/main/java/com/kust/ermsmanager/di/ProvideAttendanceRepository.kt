package com.kust.ermsmanager.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.kust.ermsmanager.data.repositories.AttendanceRepository
import com.kust.ermsmanager.data.repositories.AttendanceRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Singleton

@InstallIn(FragmentComponent::class)
@Module
object ProvideAttendanceRepository {


}