package com.kust.erms_company.data.repositroy

import com.kust.erms_company.utils.UiState

interface BiometricRepository {
    // enable or disable biometric state in the shared preferences
    fun setBiometricState(state: Boolean): UiState<String>
    fun getBiometricState(): UiState<Boolean>
}