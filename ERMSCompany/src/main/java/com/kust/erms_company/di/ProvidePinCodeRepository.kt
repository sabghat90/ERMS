package com.kust.erms_company.di

import android.content.SharedPreferences
import com.kust.erms_company.data.repositroy.PinCodeRepository
import com.kust.erms_company.data.repositroy.PinCodeRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent

@InstallIn(FragmentComponent::class)
@Module
object ProvidePinCodeRepository {

    @Provides
    fun providePinCodeRepository(
        sharedPreferences: SharedPreferences
    ): PinCodeRepository {
        return PinCodeRepositoryImpl(sharedPreferences)
    }
}