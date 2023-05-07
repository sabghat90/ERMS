package com.kust.erms_company.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kust.erms_company.data.model.CompanyModel
import com.kust.erms_company.data.repositroy.AuthRepository
import com.kust.erms_company.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _register = MutableLiveData<UiState<String>>()
    val register : LiveData<UiState<String>>
        get() = _register

    private val _login = MutableLiveData<UiState<String>>()
    val login : LiveData<UiState<String>>
        get() = _login

    private val _forgotPassword = MutableLiveData<UiState<String>>()
    val forgotPassword : LiveData<UiState<String>>
        get() = _forgotPassword

    private val _isUserLoggedIn = MutableLiveData<Boolean>()
    val isUserLoggedIn : LiveData<Boolean>
        get() = _isUserLoggedIn

    private val _changePassword = MutableLiveData<UiState<String>>()
    val changePassword : LiveData<UiState<String>>
        get() = _changePassword

    init {
        _isUserLoggedIn.value = authRepository.isUserLoggedIn()
    }

    fun register (
        email: String,
        password: String,
        companyModel : CompanyModel
    ) {
        _register.value = UiState.Loading
        authRepository.registerCompany(email, password, companyModel) {
            _register.value = it
        }
    }

    fun login (
        email: String,
        password: String
    ) {
        _login.value = UiState.Loading
        authRepository.loginCompany(email, password) {
            _login.value = it
        }
    }

    fun forgotPassword (
        email: String
    ) {
        _forgotPassword.value = UiState.Loading
        authRepository.forgotPassword(email) {
            _forgotPassword.value = it
        }
    }

    fun logout (result : () -> Unit) {
        authRepository.logoutCompany(result)
        _login.value = UiState.Success("Logout Successful")
    }

    fun getSession(result: (CompanyModel?) -> Unit) {
        authRepository.getUserSession(result)
    }

    fun changePassword(newPassword: String) {
        _changePassword.value = UiState.Loading
        authRepository.changePassword(newPassword) {
            _changePassword.value = it
        }
    }
}