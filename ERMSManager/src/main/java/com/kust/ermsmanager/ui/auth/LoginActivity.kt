package com.kust.ermsmanager.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.kust.ermsmanager.R
import com.kust.ermsmanager.databinding.ActivityLoginBinding
import com.kust.ermsmanager.ui.dashboard.DashboardActivity
import com.kust.ermsmanager.ui.setting.BiometricActivity
import com.kust.ermsmanager.utils.UiState
import com.kust.ermsmanager.utils.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        observer()

        if (authViewModel.isUserLoggedIn.value == true) {
            val intent = Intent(this, BiometricActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.btnLogin.setOnClickListener {
            if (validation()) {
                authViewModel.login(
                    email = binding.editTextEmail.text.toString().trim(),
                    password = binding.editTextPassword.text.toString()
                )
            }
        }
    }

    private fun observer() {
        authViewModel.login.observe(this) {uiState ->
            when (uiState) {
                is UiState.Error -> {
                    binding.progressBar.hide()
                    binding.btnLogin.text = getString(R.string.login)
                    toast(uiState.error)
                }
                UiState.Loading -> {
                    binding.progressBar.show()
                    binding.btnLogin.text = ""
                }
                is UiState.Success -> {
                    binding.progressBar.hide()
                    binding.btnLogin.text = getString(R.string.login)
                    toast(uiState.data)
                    val intent = Intent(this, DashboardActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }

    private fun validation(): Boolean {
        if (binding.editTextEmail.text.toString().isEmpty()) {
            binding.editTextEmail.error = "Email is required"
            binding.editTextEmail.requestFocus()
            return false
        }
        if (binding.editTextPassword.text.toString().isEmpty()) {
            binding.editTextPassword.error = "Password is required"
            binding.editTextPassword.requestFocus()
            return false
        }
        return true
    }
}
