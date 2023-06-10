package com.kust.ermsmanager.ui.profile

import android.app.Activity
import android.app.ProgressDialog
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
import com.bumptech.glide.Glide
import com.kust.ermslibrary.models.Employee
import com.kust.ermslibrary.utils.UiState
import com.kust.ermsmanager.R as ManagerR
import com.kust.ermslibrary.R as LibraryR
import com.kust.ermsmanager.databinding.FragmentUpdateProfileBinding
import com.kust.ermsmanager.ui.auth.AuthViewModel
import com.kust.ermsmanager.ui.employee.EmployeeViewModel
import com.kust.ermslibrary.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class UpdateProfileFragment : Fragment() {

    private var _binding : FragmentUpdateProfileBinding? = null
    private val binding get() = _binding!!

    private val employeeViewModel : EmployeeViewModel by viewModels()
    private val authViewModel : AuthViewModel by viewModels()

    @Inject
    lateinit var employee : Employee

    private lateinit var imageUri : Uri
    private lateinit var uploadedImageUri : Uri
    private val PICK_IMAGE_REQUEST = 1

    private val dialog: ProgressDialog by lazy {
        ProgressDialog(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentUpdateProfileBinding.inflate(layoutInflater, container, false)

        dialog.setCanceledOnTouchOutside(false)
        dialog.setMessage("Loading...")
        dialog.setCancelable(false)

        updateSpinners()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observer()
        updateUI()

        binding.imgProfile.setOnClickListener {
            pickImageFromGallery()
        }

        binding.btnUpdateProfile.setOnClickListener {
            employeeViewModel.updateEmployee(getEmployeeObj())
        }
    }

    private fun observer() {
        employeeViewModel.updateEmployee.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }

                is UiState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    toast(it.data.toString())
                }

                is UiState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    toast(it.error.toString())
                }
            }
        }

        authViewModel.getSession {
            employee.apply {
                id = it?.id.toString()
                name = it?.name.toString()
                phone = it?.phone.toString()
                gender = it?.gender.toString()
                dob = it?.dob.toString()
                city = it?.city.toString()
                state = it?.state.toString()
                address = it?.address.toString()
            }
        }
    }

    private fun updateUI() {
        employee.let {
            with(binding) {
                textViewName.setText(it.name)
                textViewPhone.setText(it.phone)
                dropDownGender.setText(it.gender)
                btnDateOfBirth.text = it.dob
                dropDownCity.setText(it.city)
                dropDownState.setText(it.state)
                dropDownCountry.setText(it.country)
                textViewAddress.setText(it.address)
            }
        }
    }

    private fun getEmployeeObj() : Employee {
        return Employee(
            name = binding.textViewName.text.toString().trim(),
            email = employee.email,
            phone = binding.textViewPhone.text.toString(),
            gender = binding.dropDownGender.text.toString(),
            dob = binding.btnDateOfBirth.text.toString(),
            city = binding.dropDownCity.text.toString(),
            state = binding.dropDownState.text.toString(),
            country = binding.dropDownCountry.text.toString(),
            address = binding.textViewAddress.text.toString(),
            profilePicture = uploadedImageUri.toString()
        )
    }

    private fun validation() : Boolean {
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

        if (binding.dropDownGender.text.toString().trim().isEmpty()) {
            binding.dropDownGender.error = errorMessage
            binding.dropDownGender.requestFocus()
            isValid = false
        }

        if (binding.btnDateOfBirth.text.toString().trim().isEmpty()) {
            binding.btnDateOfBirth.error = errorMessage
            binding.btnDateOfBirth.requestFocus()
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
        val arrayAdapterForCities : ArrayAdapter<String> = ArrayAdapter<String>(
            requireContext(),
            ManagerR.layout.dropdown_menu_item,
            spinnerDataForCities
        )
        binding.dropDownCity.setAdapter(arrayAdapterForCities)

        val spinnerDataForStates = resources.getStringArray(LibraryR.array.provinces)
        val arrayAdapterForStates : ArrayAdapter<String> = ArrayAdapter<String>(
            requireContext(),
            ManagerR.layout.dropdown_menu_item,
            spinnerDataForStates
        )
        binding.dropDownState.setAdapter(arrayAdapterForStates)

        val spinnerDataForCountries = resources.getStringArray(LibraryR.array.countries)
        val arrayAdapterForCountries : ArrayAdapter<String> = ArrayAdapter<String>(
            requireContext(),
            ManagerR.layout.dropdown_menu_item,
            spinnerDataForCountries
        )
        binding.dropDownCountry.setAdapter(arrayAdapterForCountries)

        val spinnerDataForGender = resources.getStringArray(LibraryR.array.gender)
        val arrayAdapterForGender : ArrayAdapter<String> = ArrayAdapter<String>(
            requireContext(),
            ManagerR.layout.dropdown_menu_item,
            spinnerDataForGender
        )
        binding.dropDownGender.setAdapter(arrayAdapterForGender)
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST &&
            resultCode == Activity.RESULT_OK &&
            data != null && data.data != null) {

            imageUri = data.data!!
            // Load the image using Glide
            Glide.with(this).load(imageUri).into(binding.imgProfile)
        }

        employeeViewModel.uploadImage(imageUri) {
            when (it) {
                is UiState.Loading -> {
                    dialog.show()
                }

                is UiState.Success -> {
                    dialog.hide()
                    toast("Profile Image Uploaded!")
                    uploadedImageUri = it.data
                }

                is UiState.Error -> {
                    dialog.hide()
                    toast("Error: ${it.error}")
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}