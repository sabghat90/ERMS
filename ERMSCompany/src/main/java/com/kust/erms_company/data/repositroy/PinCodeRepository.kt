package com.kust.erms_company.data.repositroy

import com.kust.erms_company.utils.UiState

interface PinCodeRepository {
    // get and set pin code and also return result to the view model using UiState class
    fun getPinCode(): UiState<String>
    fun setPinCode(pinCode: String): UiState<String>
}