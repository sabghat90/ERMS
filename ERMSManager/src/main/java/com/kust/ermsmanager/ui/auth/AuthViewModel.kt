package com.kust.ermsmanager.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kust.ermsmanager.data.models.EmployeeModel
import com.kust.ermsmanager.data.repositories.AuthRepository
import com.kust.ermsmanager.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _signUp = MutableLiveData<UiState<String>>()
    val signUp: LiveData<UiState<String>>
        get() = _signUp

    private val _login = MutableLiveData<UiState<String>>()
    val login: LiveData<UiState<String>>
        get() = _login

    private val _forgotPassword = MutableLiveData<UiState<String>>()
    val forgotPassword: LiveData<UiState<String>>
        get() = _forgotPassword

    private val _logout = MutableLiveData<UiState<String>>()
    val logout: LiveData<UiState<String>>
        get() = _logout

    private val _isUserLoggedIn = MutableLiveData<Boolean>()
    val isUserLoggedIn: LiveData<Boolean>
        get() = _isUserLoggedIn

    init {
        _isUserLoggedIn.value = authRepository.isUserLoggedIn()
    }

    fun signUp(email: String, password: String, employeeModel: EmployeeModel) {
        authRepository.signUp(email, password, employeeModel) {
            _signUp.value = it
        }
    }

    fun login(email: String, password: String) {
        authRepository.login(email, password) {
            _login.value = it
        }
    }


    fun forgotPassword(email: String) {
        authRepository.forgotPassword(email) {
            _forgotPassword.value = it
        }
    }

    fun logout(result: () -> Unit) {
        authRepository.logout(result)
        _login.value = UiState.Success("Logout Successful")
    }
}