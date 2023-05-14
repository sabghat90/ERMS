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

    private val _changePassword = MutableLiveData<UiState<String>>()
    val changePassword : LiveData<UiState<String>>
        get() = _changePassword

    init {
        _isUserLoggedIn.value = authRepository.isUserLoggedIn()
    }


    fun login(email: String, password: String) {
        _login.value = UiState.Loading
        authRepository.login(email, password) {
            _login.value = it
        }
    }


    fun forgotPassword(email: String) {
        _forgotPassword.value = UiState.Loading
        authRepository.forgotPassword(email) {
            _forgotPassword.value = it
        }
    }

    fun logout(result: () -> Unit) {
        _logout.value = UiState.Loading
        authRepository.logout(result)
        _login.value = UiState.Success("Logout Successful")
    }

    fun getSession(result: (EmployeeModel?) -> Unit) {
        authRepository.getUserSession(result)
    }

    fun changePassword(newPassword: String) {
        _changePassword.value = UiState.Loading
        authRepository.changePassword(newPassword) {
            _changePassword.value = it
        }
    }
}