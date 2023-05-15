package com.kust.ermsemployee.data.repository

import com.google.android.gms.tasks.Task
import com.kust.ermsemployee.data.model.EmployeeModel
import com.kust.ermsemployee.utils.UiState

interface AuthRepository {
    fun login(email : String, password : String, result : (UiState<String>) -> Unit)
    fun storeFCMToken(id : String, result : (UiState<String>) -> Unit)
    fun signUp(email: String, password: String, employeeModel: EmployeeModel, result: (UiState<String>) -> Unit)
    fun updateEmployeeInfo(employeeModel: EmployeeModel, result: (UiState<String>) -> Unit)
    fun forgotPassword(email : String, result : (UiState<String>) -> Unit)
    fun logout(result : () -> Unit)
    fun validateUser(id: String): Task<Boolean>
    fun isUserLoggedIn() : Boolean
    fun storeUserSession(email: String, result: (EmployeeModel?) -> Unit)
    fun getUserSession(result : (EmployeeModel?) -> Unit)
    fun changePassword(newPassword: String, result: (UiState<String>) -> Unit)
}