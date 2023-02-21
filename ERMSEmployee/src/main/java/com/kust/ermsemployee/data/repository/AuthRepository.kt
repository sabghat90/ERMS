package com.kust.ermsemployee.data.repository

import com.kust.ermsemployee.utils.UiState

interface AuthRepository {
    fun login(email : String, password : String, result : (UiState<String>) -> Unit)
    fun forgotPassword(email : String, result : (UiState<String>) -> Unit)
    fun logout(result : () -> Unit)
    fun validateUser(email : String) : Boolean
//    fun isEmployeeLoggedIn(result: (UiState<String>) -> Unit)
//    fun updateCompanyInformation(companyModel: CompanyModel, result : (UiState<String>) -> Unit)
    fun isUserLoggedIn() : Boolean
}