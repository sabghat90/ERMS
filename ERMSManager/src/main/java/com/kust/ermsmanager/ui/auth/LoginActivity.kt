package com.kust.ermsmanager.ui.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.kust.ermsmanager.databinding.ActivityLoginBinding
import com.kust.ermsmanager.ui.dashboard.DashboardActivity
import com.kust.ermsmanager.utils.UiState
import com.kust.ermsmanager.utils.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private val TAG = "LoginActivity"

    private lateinit var binding : ActivityLoginBinding
    private val viewModel : AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        observer()

        binding.btnLogin.setOnClickListener {
            if (validation()){
                viewModel.login(binding.editTextEmail.text.toString(), binding.editTextPassword.text.toString())
            }
        }
    }

    private fun observer() {
        viewModel.login.observe(this) { state ->
            when(state){
                is UiState.Loading -> {
                    binding.btnLogin.text = ""
                    binding.progressBar.show()
                }
                is UiState.Error -> {
                    binding.btnLogin.text = "Login"
                    binding.progressBar.hide()
                    toast(state.error)
                }
                is UiState.Success -> {
                    binding.btnLogin.text = "Login"
                    binding.progressBar.hide()
                    toast(state.data)
                    val intent = Intent(this, DashboardActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }

    private fun validation () : Boolean {
        if (binding.editTextEmail.text.toString().isEmpty()){
            binding.editTextEmail.error = "Email is required"
            binding.editTextEmail.requestFocus()
            return false
        }
        if (binding.editTextPassword.text.toString().isEmpty()){
            binding.editTextPassword.error = "Password is required"
            binding.editTextPassword.requestFocus()
            return false
        }
        return true
    }
}