package com.kust.erms_company.ui.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.kust.erms_company.BuildConfig
import com.kust.erms_company.R
import com.kust.erms_company.data.repositroy.BiometricRepository
import com.kust.erms_company.data.repositroy.BiometricRepositoryImpl
import com.kust.erms_company.databinding.FragmentSettingBinding
import com.kust.erms_company.utils.SharedPreferencesConstants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingFragment : Fragment() {

    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateUi()

        binding.biometricSwitch.setOnCheckedChangeListener { _, isChecked ->
            biometricRepository.setBiometricState(isChecked)
        }

        binding.privacyPolicyLayout.setOnClickListener {
            findNavController().navigate(R.id.action_settingFragment_to_privacyPolicyFragment)
        }

        binding.projectProposal.setOnClickListener {
            findNavController().navigate(R.id.action_settingFragment_to_projectProposalFragment)
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
        if (biometricState is com.kust.erms_company.utils.UiState.Success) {
            binding.biometricSwitch.isChecked = biometricState.data
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}