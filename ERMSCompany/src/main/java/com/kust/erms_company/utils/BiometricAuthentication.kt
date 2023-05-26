package com.kust.erms_company.utils

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData

class BiometricAuthentication(private val context: Context) {

    private val authenticationResult: MutableLiveData<Boolean> = MutableLiveData()


    @RequiresApi(Build.VERSION_CODES.P)
    fun authenticate() {
        val biometricManager = BiometricManager.from(context)
        if (biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS) {
            val biometricPrompt = createBiometricPrompt()
            val promptInfo = createPromptInfo()
            biometricPrompt.authenticate(promptInfo)
        } else {
            authenticationResult.postValue(false)
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun createBiometricPrompt(): BiometricPrompt {
        val activity = context as FragmentActivity
        return BiometricPrompt(activity, activity.mainExecutor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    authenticationResult.postValue(false)
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    authenticationResult.postValue(true)
                }

                override fun onAuthenticationFailed() {
                    authenticationResult.postValue(false)
                }
            })
    }

    private fun createPromptInfo(): BiometricPrompt.PromptInfo {
        return BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric Authentication")
            .setSubtitle("Confirm your biometric credentials")
            .setNegativeButtonText("Cancel")
            .build()
    }

    fun getAuthenticationResult(): MutableLiveData<Boolean> {
        return authenticationResult
    }
}
