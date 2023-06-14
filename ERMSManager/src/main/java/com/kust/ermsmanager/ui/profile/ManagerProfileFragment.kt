package com.kust.ermsmanager.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.kust.ermslibrary.models.Employee
import com.kust.ermslibrary.utils.ConvertDateAndTimeFormat
import com.kust.ermsmanager.databinding.FragmentManagerProfileBinding
import com.kust.ermsmanager.ui.auth.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ManagerProfileFragment : Fragment() {

    private var _binding : FragmentManagerProfileBinding? = null
    private val binding get() = _binding!!

    private val authViewModel: AuthViewModel by viewModels()
    @Inject
    lateinit var employee : Employee

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentManagerProfileBinding.inflate(layoutInflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observer()
        updateUI()

        binding.btnUpdateProfile.setOnClickListener {
            findNavController().navigate(com.kust.ermsmanager.R.id.action_managerProfileFragment_to_updateProfileFragment)
        }
    }

    private fun observer() {
        authViewModel.getSession {
            if (it != null) {
                employee = it
            }
        }
    }

    private fun updateUI() {
        val date = ConvertDateAndTimeFormat().formatDate(employee.joiningDate)
        binding.profile.apply {
            name.text = employee.name
            tvEmail.text = employee.email
            tvPhone.text = employee.phone
            tvWebsite.text = employee.website
            tvCountry.text = employee.country
            tvState.text = employee.state
            tvFullAddress.text = employee.address
            tvDepartment.text = employee.department
            tvJobTitle.text = employee.jobTitle
            tvSalary.text = employee.salary.toString()
            tvJoiningDate.text = date
            tvFullAddress.text = employee.city + ", " + employee.state + ", " + employee.country
        }

        Glide.with(requireContext())
            .load(employee.profilePicture)
            .into(binding.profile.imgLogo)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}