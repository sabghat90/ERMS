package com.kust.erms_company.data.repositroy

import android.content.SharedPreferences
import com.kust.ermslibrary.utils.SharedPreferencesConstants
import com.kust.ermslibrary.utils.UiState

class BiometricRepositoryImpl(
    private val sharedPreferences: SharedPreferences
) : BiometricRepository {

        override fun setBiometricState(state: Boolean): UiState<String> {
            sharedPreferences.edit().putBoolean(SharedPreferencesConstants.BIOMETRIC, state).apply()
            return UiState.Success("Success")
        }

        override fun getBiometricState(): UiState<Boolean> {
            val biometric = sharedPreferences.getBoolean(SharedPreferencesConstants.BIOMETRIC, false)
            return UiState.Success(biometric)
        }
}