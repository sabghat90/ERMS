package com.kust.ermsemployee.data.repository

import com.google.android.gms.tasks.Task
import com.kust.ermslibrary.models.Employee
import com.kust.ermslibrary.utils.UiState

interface AuthRepository {
    fun login(email : String, password : String, result : (UiState<String>) -> Unit)
    fun storeFCMToken(id : String, result : (UiState<String>) -> Unit)
    fun signUp(email: String, password: String, employee: Employee, result: (UiState<String>) -> Unit)
    fun updateEmployeeInfo(employee: Employee, result: (UiState<String>) -> Unit)
    fun forgotPassword(email : String, result : (UiState<String>) -> Unit)
    fun logout(result : () -> Unit)
    fun validateUser(id: String): Task<Boolean>
    fun isUserLoggedIn() : Boolean
    fun storeUserSession(email: String, result: (Employee?) -> Unit)
    fun getUserSession(result : (Employee?) -> Unit)
    fun changePassword(newPassword: String, result: (UiState<String>) -> Unit)
}