package com.kust.ermsemployee.ui.setting

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.kust.ermsemployee.R
import com.kust.ermsemployee.data.repository.BiometricRepository
import com.kust.ermsemployee.data.repository.BiometricRepositoryImpl
import com.kust.ermsemployee.databinding.ActivityBiometricBinding
import com.kust.ermsemployee.ui.dashboard.DashboardActivity
import com.kust.ermsemployee.utils.BiometricAuthentication
import com.kust.ermslibrary.utils.SharedPreferencesConstants
import com.kust.ermslibrary.utils.UiState
import dagger.hilt.android.AndroidEntryPoint

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
                        binding.tvBiometric.text = getString(R.string.authentication_failed)
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