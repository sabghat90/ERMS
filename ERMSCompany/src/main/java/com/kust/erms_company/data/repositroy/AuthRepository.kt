package com.kust.erms_company.data.repositroy

import com.google.android.gms.tasks.Task
import com.kust.ermslibrary.models.Company
import com.kust.ermslibrary.utils.UiState

interface AuthRepository {
    fun registerCompany(email : String, password : String, company: Company, result : (UiState<String>) -> Unit)
    fun loginCompany(email : String, password : String, result : (UiState<String>) -> Unit)
    fun storeFCMToken(id : String, result : (UiState<String>) -> Unit)
    fun validateUser(id: String): Task<Boolean>
    fun forgotPassword(email : String, result : (UiState<String>) -> Unit)
    fun logoutCompany(result : () -> Unit)
    fun updateCompanyInformation(company: Company, result : (UiState<String>) -> Unit)
    fun isUserLoggedIn() : Boolean

    fun storeUserSession(id: String, result: (Company?) -> Unit)
    fun getUserSession(result : (Company?) -> Unit)
    fun changePassword(newPassword: String, result: (UiState<String>) -> Unit)
}