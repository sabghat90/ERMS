package com.kust.ermsemployee.data.repository

import android.content.SharedPreferences
import com.kust.ermsemployee.utils.SharedPreferencesConstants
import com.kust.ermsemployee.utils.UiState

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