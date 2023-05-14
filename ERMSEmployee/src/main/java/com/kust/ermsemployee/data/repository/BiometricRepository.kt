package com.kust.ermsemployee.data.repository

import com.kust.ermsemployee.utils.UiState

interface BiometricRepository {
    // enable or disable biometric state in the shared preferences
    fun setBiometricState(state: Boolean): UiState<String>
    fun getBiometricState(): UiState<Boolean>
}