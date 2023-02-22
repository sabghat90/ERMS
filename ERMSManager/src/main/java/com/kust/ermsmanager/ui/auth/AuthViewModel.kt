package com.kust.ermsmanager.ui.auth

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kust.ermsmanager.data.repositories.AuthRepository
import com.kust.ermsmanager.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _login = MutableLiveData<UiState<String>>()
    val login = _login

    private val _forgotPassword = MutableLiveData<UiState<String>>()
    val forgotPassword = _forgotPassword

    private val _logout = MutableLiveData<UiState<String>>()
    val logout = _logout

    fun login(email : String, password : String){
        authRepository.login(email, password) {
            _login.value = it
        }
    }

    fun forgotPassword(email : String){
        authRepository.forgotPassword(email) {
            _forgotPassword.value = it
        }
    }

    fun logout(){
        authRepository.logout {
            _logout.value = UiState.Success("Logout Success")
        }
    }
}