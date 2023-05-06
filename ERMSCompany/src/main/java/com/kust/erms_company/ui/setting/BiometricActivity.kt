package com.kust.erms_company.ui.setting

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.kust.erms_company.R
import com.kust.erms_company.data.repositroy.BiometricRepository
import com.kust.erms_company.data.repositroy.BiometricRepositoryImpl
import com.kust.erms_company.databinding.ActivityBiometricBinding
import com.kust.erms_company.ui.dashboard.DashBoardActivity
import com.kust.erms_company.utils.SharedPreferencesConstants
import com.kust.erms_company.utils.UiState
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.Executor

@AndroidEntryPoint
class BiometricActivity : AppCompatActivity() {

    companion object {
        const val TAG = "BiometricFragment"
    }

    private lateinit var binding : ActivityBiometricBinding

    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    // get BiometricRepository
    private val biometricRepository : BiometricRepository by lazy {
        BiometricRepositoryImpl(this.getSharedPreferences(SharedPreferencesConstants.BIOMETRIC, 0))
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBiometricBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // check from BiometricRepository if biometric is enabled or not
        val biometricState = biometricRepository.getBiometricState()
        if (biometricState is UiState.Success) {
            if (biometricState.data) {
                checkDeviceHasBiometric()
                activateBiometric()
            } else {
                val loadDashBoardActivity = Intent(this, DashBoardActivity::class.java)
                startActivity(loadDashBoardActivity)
                finish()
            }
        }
    }

    private fun activateBiometric() {

        executor = ContextCompat.getMainExecutor(this)

        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    binding.tvBiometric.text = "Authentication error: $errString"
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    binding.tvBiometric.text = getString(R.string.authentication_succeeded)
                    val loadDashBoardActivity = Intent(this@BiometricActivity, DashBoardActivity::class.java)
                    startActivity(loadDashBoardActivity)
                    finish()
                }

                override fun onAuthenticationFailed() {
                    binding.tvBiometric.text = getString(R.string.authentication_failed)
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("ERMS Company")
            .setSubtitle("Log in using your biometric credential")
            .setNegativeButtonText("Cancel")
            .build()

        binding.ivBiometric.setOnClickListener {
            biometricPrompt.authenticate(promptInfo)
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun checkDeviceHasBiometric() {
        val biometricManager = BiometricManager.from(this)
        when (biometricManager.canAuthenticate()) {
            BiometricManager.BIOMETRIC_SUCCESS ->
                binding.tvBiometric.text = getString(R.string.you_can_use_biometric)

            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
                binding.tvBiometric.text = getString(R.string.no_biometric_feature)

            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                binding.tvBiometric.text =
                    getString(R.string.biometric_features_are_currently_unavailable)

            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                binding.tvBiometric.text =
                    getString(R.string.the_user_hasn_t_associated_any_biometric_credentials)
                val enrollmentIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                    putExtra(
                        Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                        BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL
                    )
                }
                startActivityForResult(enrollmentIntent, 1)
            }
        }
    }
}