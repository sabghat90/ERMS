package com.kust.ermsmanager.data.repositories

import com.google.android.gms.tasks.Task
import com.kust.ermsmanager.data.models.EmployeeModel
import com.kust.ermsmanager.utils.UiState

interface AuthRepository {
    /// Login with email and password
    fun login(email : String, password : String, result : (UiState<String>) -> Unit)
    fun forgotPassword(email : String, result : (UiState<String>) -> Unit)
    fun logout(result : () -> Unit)
    fun validateUser(id: String): Task<Boolean>
    fun isUserLoggedIn() : Boolean
    fun storeUserSession(id: String, result: (EmployeeModel?) -> Unit)
    fun getUserSession(result : (EmployeeModel?) -> Unit)
    fun changePassword(newPassword: String, result: (UiState<String>) -> Unit)
}