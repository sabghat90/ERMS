package com.kust.erms_company.ui.company

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.kust.erms_company.databinding.FragmentUpdateProfileBinding
import com.kust.erms_company.ui.auth.AuthViewModel
import com.kust.ermslibrary.models.Company
import com.kust.ermslibrary.utils.UiState
import com.kust.ermslibrary.utils.hideKeyboard
import com.kust.ermslibrary.utils.isNull
import com.kust.ermslibrary.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import com.kust.erms_company.R as CompanyR
import com.kust.ermslibrary.R as LibraryR

@AndroidEntryPoint
class UpdateProfileFragment : Fragment() {

    private var _binding: FragmentUpdateProfileBinding? = null
    private val binding get() = _binding!!

    private val companyViewModel: CompanyViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    private lateinit var companyObj: Company

    private var imageUri: Uri? = null
    private lateinit var uploadedImageUrl: String
    private val PICK_IMAGE_REQUEST = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentUpdateProfileBinding.inflate(layoutInflater, container, false)
        updateSpinners()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authViewModel.getSession {
            if (it != null) {
                companyObj = it
                updateUI()
            }
        }

        observer()

        binding.imgProfile.setOnClickListener {
            pickImageFromGallery()
        }

        binding.btnUpdateProfile.setOnClickListener {
            hideKeyboard()
            if (validation()) {
                if (!imageUri.isNull) {
                    imageUri?.let { uri ->
                        companyViewModel.uploadImage(uri) {
                            when (it) {
                                is UiState.Loading -> {
                                    binding.progressBar.show()
                                    binding.btnUpdateProfile.text = ""
                                }

                                is UiState.Success -> {
                                    binding.progressBar.hide()
                                    binding.btnUpdateProfile.text = getString(LibraryR.string.update)
                                    uploadedImageUrl = it.data.toString()
                                    lifecycleScope.launch {
                                        updateUserSession()
                                        companyViewModel.updateCompanyDetails(getCompanyObj())
                                    }
                                }

                                is UiState.Error -> {
                                    binding.progressBar.hide()
                                    binding.btnUpdateProfile.text = getString(LibraryR.string.update)
                                    toast(it.error.toString())
                                }
                            }
                        }
                    }
                } else {
                    lifecycleScope.launch {
                        updateUserSession()
                        companyViewModel.updateCompanyDetails(getCompanyObj())
                    }
                }
            }
        }
    }

    private fun updateUserSession() {
        authViewModel.storeUserSession(companyObj.id)
    }

    private fun observer() {
        companyViewModel.updateCompanyDetails.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Loading -> {
                    binding.progressBar.show()
                    binding.btnUpdateProfile.text = ""
                }

                is UiState.Success -> {
                    binding.progressBar.hide()
                    binding.btnUpdateProfile.text = getString(LibraryR.string.update)
                    toast("Profile updated successfully")
                }

                is UiState.Error -> {
                    binding.progressBar.hide()
                    binding.btnUpdateProfile.text = getString(LibraryR.string.update)
                    toast(it.error.toString())
                }
            }
        }

        authViewModel.storeUserSession.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Loading -> {
                    binding.progressBar.show()
                    binding.btnUpdateProfile.text = ""
                }

                is UiState.Success -> {
                    binding.progressBar.hide()
                    binding.btnUpdateProfile.text = getString(LibraryR.string.update)
                    findNavController().navigate(CompanyR.id.action_updateProfileFragment_to_profileFragment)
                }

                is UiState.Error -> {
                    binding.progressBar.hide()
                    binding.btnUpdateProfile.text = getString(LibraryR.string.update)
                    toast(it.error.toString())
                }
            }
        }
    }

    private fun updateUI() {
        uploadedImageUrl = companyObj.profilePicture
        companyObj.let {
            with(binding) {
                textViewName.setText(it.name)
                textViewPhone.setText(it.phone)
                dropDownCity.setText(it.city)
                dropDownState.setText(it.state)
                dropDownCountry.setText(it.country)
                textViewAddress.setText(it.address)
                etWebsite.setText(it.website)

                Glide.with(requireContext())
                    .load(it.profilePicture)
                    .placeholder(LibraryR.drawable.avatar2)
                    .into(imgProfile)
            }
        }
    }

    private fun getCompanyObj(): Company {
        val address = binding.textViewAddress.text.toString().trim()
        val state = binding.dropDownState.text.toString().trim()
        val city = binding.dropDownCity.text.toString().trim()
        val country = binding.dropDownCountry.text.toString().trim()
        return Company(
            name = binding.textViewName.text.toString().trim(),
            phone = binding.textViewPhone.text.toString().trim(),
            city = binding.dropDownCity.text.toString().trim(),
            state = binding.dropDownState.text.toString().trim(),
            country = binding.dropDownCountry.text.toString().trim(),
            address = binding.textViewAddress.text.toString().trim(),
            website = binding.etWebsite.text.toString().trim(),
            profilePicture = uploadedImageUrl,
            fullAddress = "$address, $city, $state, $country"
        )
    }

    private fun validation(): Boolean {
        var isValid = true

        val errorMessage = "This field is required"

        if (binding.textViewName.text.toString().trim().isEmpty()) {
            binding.textViewName.error = errorMessage
            binding.textViewName.requestFocus()
            isValid = false
        }

        // name should be alphabetic only and should not contain any special character or number
        if (!binding.textViewName.text.toString().trim().matches(Regex("^[a-zA-Z ]+\$"))) {
            binding.textViewName.error = "Name should be alphabetic only"
            binding.textViewName.requestFocus()
            isValid = false
        }

        if (binding.textViewPhone.text.toString().trim().isEmpty()) {
            binding.textViewPhone.error = errorMessage
            binding.textViewPhone.requestFocus()
            isValid = false
        }

        if (binding.etWebsite.text.toString().trim().isEmpty()) {
            binding.etWebsite.error = errorMessage
            binding.etWebsite.requestFocus()
            isValid = false
        }

        if (binding.dropDownCity.text.toString().trim().isEmpty()) {
            binding.dropDownCity.error = errorMessage
            binding.dropDownCity.requestFocus()
            isValid = false
        }

        if (binding.dropDownState.text.toString().trim().isEmpty()) {
            binding.dropDownState.error = errorMessage
            binding.dropDownState.requestFocus()
            isValid = false
        }

        if (binding.dropDownCountry.text.toString().trim().isEmpty()) {
            binding.dropDownCountry.error = errorMessage
            binding.dropDownCountry.requestFocus()
            isValid = false
        }

        if (binding.textViewAddress.text.toString().trim().isEmpty()) {
            binding.textViewAddress.error = errorMessage
            binding.textViewAddress.requestFocus()
            isValid = false
        }

        return isValid
    }

    private fun updateSpinners() {
        val spinnerDataForCities = resources.getStringArray(LibraryR.array.cities)
        val arrayAdapterForCities: ArrayAdapter<String> = ArrayAdapter<String>(
            requireContext(),
            CompanyR.layout.dropdown_menu_item,
            spinnerDataForCities
        )
        binding.dropDownCity.setAdapter(arrayAdapterForCities)

        val spinnerDataForStates = resources.getStringArray(LibraryR.array.provinces)
        val arrayAdapterForStates: ArrayAdapter<String> = ArrayAdapter<String>(
            requireContext(),
            CompanyR.layout.dropdown_menu_item,
            spinnerDataForStates
        )
        binding.dropDownState.setAdapter(arrayAdapterForStates)

        val spinnerDataForCountries = resources.getStringArray(LibraryR.array.countries)
        val arrayAdapterForCountries: ArrayAdapter<String> = ArrayAdapter<String>(
            requireContext(),
            CompanyR.layout.dropdown_menu_item,
            spinnerDataForCountries
        )
        binding.dropDownCountry.setAdapter(arrayAdapterForCountries)
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST &&
            resultCode == Activity.RESULT_OK &&
            data != null && data.data != null
        ) {

            imageUri = data.data!!
            // Load the image using Glide
            Glide.with(this).load(imageUri).into(binding.imgProfile)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}