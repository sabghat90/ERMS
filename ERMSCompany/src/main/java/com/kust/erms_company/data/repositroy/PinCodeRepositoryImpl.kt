package com.kust.erms_company.data.repositroy

import android.content.SharedPreferences
import com.kust.erms_company.utils.UiState

class PinCodeRepositoryImpl(
    private val sharedPreferences: SharedPreferences
) : PinCodeRepository {
    override fun getPinCode(): UiState<String> {
        return UiState.Success(sharedPreferences.getString("pinCode", "")!!)
    }

    override fun setPinCode(pinCode: String): UiState<String> {
        sharedPreferences.edit().putString("pinCode", pinCode).apply()
        return UiState.Success("Pin code set successfully")
    }
}