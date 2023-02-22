package com.kust.ermsmanager.data.repositories

import com.kust.ermsmanager.utils.UiState

interface AuthRepository {
    fun login(email : String, password : String, result : (UiState<String>) -> Unit)
    fun forgotPassword(email : String, result : (UiState<String>) -> Unit)
    fun logout(result : () -> Unit)
    fun validateUser(email : String) : Boolean
    fun isUserLoggedIn() : Boolean
}