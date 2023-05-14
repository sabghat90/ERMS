package com.kust.ermsmanager.data.repositories

import com.kust.ermsmanager.utils.UiState

interface BiometricRepository {
    // enable or disable biometric state in the shared preferences
    fun setBiometricState(state: Boolean): UiState<String>
    fun getBiometricState(): UiState<Boolean>
}