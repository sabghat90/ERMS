package com.kust.ermsemployee.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kust.ermsemployee.data.repository.AuthRepository
import com.kust.ermslibrary.models.Company
import com.kust.ermslibrary.models.Employee
import com.kust.ermslibrary.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _login = MutableLiveData<UiState<String>>()
    val login: LiveData<UiState<String>>
        get() = _login

    private val _signUp = MutableLiveData<UiState<String>>()
    val signUp : LiveData<UiState<String>>
    get() = _signUp

    private val _forgotPassword = MutableLiveData<UiState<String>>()
    val forgotPassword: LiveData<UiState<String>>
        get() = _forgotPassword

    private val _isUserLoggedIn = MutableLiveData<Boolean>()
    val isUserLoggedIn: LiveData<Boolean>
        get() = _isUserLoggedIn

    private val _changePassword = MutableLiveData<UiState<String>>()
    val changePassword : LiveData<UiState<String>>
        get() = _changePassword

    private val _storeUserSession = MutableLiveData<UiState<Employee>>()
    val storeUserSession : LiveData<UiState<Employee>>
        get() = _storeUserSession

    init {
        _isUserLoggedIn.value = repository.isUserLoggedIn()
    }

    fun login(
        email: String,
        password: String
    ) {
        _login.value = UiState.Loading
        repository.login(email, password) {
            _login.value = it
        }
    }

    fun signUp(
        email: String,
        password: String,
        employee: Employee
    ) {
        _signUp.value = UiState.Loading
        repository.signUp(email, password, employee) {
            _signUp.value = it
        }
    }

    fun forgotPassword(
        email: String
    ) {
        _forgotPassword.value = UiState.Loading
        repository.forgotPassword(email) {
            _forgotPassword.value = it
        }
    }

    fun logout (result : () -> Unit) {
        repository.logout(result)
        _login.value = UiState.Success("Logout Successful")
    }

    fun getSession(result: (Employee?) -> Unit) {
        repository.getUserSession(result)
    }

    fun changePassword(newPassword: String) {
        _changePassword.value = UiState.Loading
        repository.changePassword(newPassword) {
            _changePassword.value = it
        }
    }

    fun storeUserSession(id: String) {
        _storeUserSession.value = UiState.Loading
        repository.storeUserSession(id) {
            _storeUserSession.value = it?.let { it1 -> UiState.Success(it1) }
        }
    }
}