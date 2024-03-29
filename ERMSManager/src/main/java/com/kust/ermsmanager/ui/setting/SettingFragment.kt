package com.kust.ermsmanager.ui.setting

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.biometric.BiometricManager
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.kust.ermsmanager.BuildConfig
import com.kust.ermsmanager.R
import com.kust.ermsmanager.data.repositories.BiometricRepository
import com.kust.ermsmanager.data.repositories.BiometricRepositoryImpl
import com.kust.ermsmanager.databinding.FragmentSettingBinding
import com.kust.ermslibrary.utils.SharedPreferencesConstants
import com.kust.ermslibrary.utils.UiState
import com.kust.ermslibrary.utils.toast
import com.kust.ermsmanager.ui.auth.AuthActivity
import com.kust.ermsmanager.ui.auth.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingFragment : Fragment() {

    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!

    private val authViewModel: AuthViewModel by viewModels()

    private val biometricRepository : BiometricRepository by lazy {
        BiometricRepositoryImpl(requireActivity().getSharedPreferences(SharedPreferencesConstants.BIOMETRIC, 0))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateUi()

        binding.biometricSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (checkDeviceHasBiometric()) {
                if (isChecked) {
                    biometricRepository.setBiometricState(true)
                } else {
                    biometricRepository.setBiometricState(false)
                }
            } else {
                binding.biometricSwitch.isChecked = false
                val intent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                    putExtra(
                        Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                        BiometricManager.Authenticators.BIOMETRIC_STRONG
                    )
                }
                startActivity(intent)
            }
        }

        binding.privacyPolicyLayout.setOnClickListener {
            findNavController().navigate(R.id.action_settingFragment_to_privacyPolicyFragment)
        }

        binding.projectProposal.setOnClickListener {
            findNavController().navigate(R.id.action_settingFragment_to_projectProposalFragment)
        }

        binding.changePasswordLayout.setOnClickListener {
            findNavController().navigate(R.id.action_settingFragment_to_changePasswordFragment)
        }

        binding.logoutCard.setOnClickListener {
            authViewModel.logout {
                toast("Logout success")
                val intent = Intent(requireContext(), AuthActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }

        getVersionCodeAndName()
    }

    private fun getVersionCodeAndName() {
        val versionName = BuildConfig.VERSION_NAME
        val versionCode = BuildConfig.VERSION_CODE
        binding.version.text = "Version $versionName ($versionCode)"
    }

    private fun updateUi() {
        val biometricState = biometricRepository.getBiometricState()
        if (biometricState is UiState.Success) {
            binding.biometricSwitch.isChecked = biometricState.data
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun checkDeviceHasBiometric() : Boolean {
        val biometricManager = BiometricManager.from(requireContext())
        when (biometricManager.canAuthenticate()) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                toast("You can use the fingerprint sensor to login")
                return true
            }

            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                toast("The device don't have fingerprint sensor")
                return false
            }


            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                toast("The biometric sensor is currently unavailable")
                return false
            }

            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                toast("Your device don't have any fingerprint saved, please check your security settings")
                val enrollmentIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                    putExtra(
                        Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                        BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL
                    )
                }
                ActivityCompat.startActivityForResult(
                    context as FragmentActivity,
                    enrollmentIntent,
                    0,
                    null
                )
            }

            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> {
                toast("The device is running a previous version of Android and doesn't have the latest security updates installed")
            }

            BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED -> {
                toast("The device doesn't support biometric authentication")
            }

            BiometricManager.BIOMETRIC_STATUS_UNKNOWN -> {
                toast("Biometric status unknown")
            }
        }
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}