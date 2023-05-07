package com.kust.erms_company.data.repositroy

import com.google.android.gms.tasks.Task
import com.kust.erms_company.data.model.CompanyModel
import com.kust.erms_company.utils.UiState

interface AuthRepository {
    fun registerCompany(email : String, password : String, companyModel: CompanyModel, result : (UiState<String>) -> Unit)
    fun loginCompany(email : String, password : String, result : (UiState<String>) -> Unit)
    fun validateUser(id: String): Task<Boolean>
    fun forgotPassword(email : String, result : (UiState<String>) -> Unit)
    fun logoutCompany(result : () -> Unit)
    fun updateCompanyInformation(companyModel: CompanyModel, result : (UiState<String>) -> Unit)
    fun isUserLoggedIn() : Boolean

    fun storeUserSession(id: String, result: (CompanyModel?) -> Unit)
    fun getUserSession(result : (CompanyModel?) -> Unit)
    fun changePassword(newPassword: String, result: (UiState<String>) -> Unit)
}