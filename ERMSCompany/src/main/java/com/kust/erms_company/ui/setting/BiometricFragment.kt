package com.kust.erms_company.ui.setting

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.kust.erms_company.R
import com.kust.erms_company.databinding.FragmentBiometricBinding
import com.kust.erms_company.ui.dashboard.FeaturesFragment
import java.util.concurrent.Executor


class BiometricFragment : Fragment() {

    private var _binding : FragmentBiometricBinding? = null
    private val binding get() = _binding!!

    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentBiometricBinding.inflate(inflater, container, false)

        checkDeviceHasBiometric()

        executor = ContextCompat.getMainExecutor(requireContext())
        biometricPrompt = BiometricPrompt(requireActivity(), executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    binding.tvBiometric.text = "Authentication error: $errString"
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    binding.tvBiometric.text = getString(R.string.authentication_succeeded)

                    val navController = findNavController()
                    navController.popBackStack(R.id.biometricFragment, true)
                    navController.navigate(R.id.featuresFragment)

                }

                override fun onAuthenticationFailed() {
                    binding.tvBiometric.text = getString(R.string.authentication_failed)
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric login for ERMS Company")
            .setSubtitle("Log in using your biometric credential")
            .setNegativeButtonText("Cancel")
            .build()

        binding.ivBiometric.setOnClickListener {
            biometricPrompt.authenticate(promptInfo)
        }

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun checkDeviceHasBiometric() {
        val biometricManager = BiometricManager.from(requireContext())
        when (biometricManager.canAuthenticate()) {
            BiometricManager.BIOMETRIC_SUCCESS ->
                binding.tvBiometric.text = getString(R.string.you_can_use_biometric)
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
                binding.tvBiometric.text = getString(R.string.no_biometric_feature)
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                binding.tvBiometric.text = getString(R.string.biometric_features_are_currently_unavailable)
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                binding.tvBiometric.text =
                    getString(R.string.the_user_hasn_t_associated_any_biometric_credentials)
                val enrollmentIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                    putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                        BIOMETRIC_STRONG or DEVICE_CREDENTIAL)

                }

                startActivityForResult(enrollmentIntent, 1)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}