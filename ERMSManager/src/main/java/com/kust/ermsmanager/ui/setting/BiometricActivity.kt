package com.kust.ermsmanager.ui.setting

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.kust.ermslibrary.utils.SharedPreferencesConstants
import com.kust.ermslibrary.utils.UiState
import com.kust.ermsmanager.data.repositories.BiometricRepository
import com.kust.ermsmanager.data.repositories.BiometricRepositoryImpl
import com.kust.ermsmanager.databinding.ActivityBiometricBinding
import com.kust.ermsmanager.ui.dashboard.DashboardActivity
import com.kust.ermslibrary.utils.BiometricAuthentication
import dagger.hilt.android.AndroidEntryPoint
import com.kust.ermslibrary.R as LibraryR

@AndroidEntryPoint
class BiometricActivity : AppCompatActivity() {

    private lateinit var binding : ActivityBiometricBinding

    private lateinit var biometricAuthentication: BiometricAuthentication

    // get BiometricRepository
    private val biometricRepository : BiometricRepository by lazy {
        BiometricRepositoryImpl(this.getSharedPreferences(SharedPreferencesConstants.BIOMETRIC, 0))
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBiometricBinding.inflate(layoutInflater)
        setContentView(binding.root)

        biometricAuthentication = BiometricAuthentication(this)

        // check from BiometricRepository if biometric is enabled or not
        val biometricState = biometricRepository.getBiometricState()
        if (biometricState is UiState.Success) {
            if (biometricState.data) {
                biometricAuthentication.authenticate()
                biometricAuthentication.getAuthenticationResult().observe(this) {
                    if (it) {
                        val loadDashBoardActivity =
                            Intent(this@BiometricActivity, DashboardActivity::class.java)
                        startActivity(loadDashBoardActivity)
                        finish()
                    } else {
                        binding.tvBiometric.text = getString(LibraryR.string.authentication_failed)
                    }
                }
            } else {
                val loadDashBoardActivity = Intent(this, DashboardActivity::class.java)
                startActivity(loadDashBoardActivity)
                finish()
            }
        }
    }
}