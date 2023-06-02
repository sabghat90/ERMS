package com.kust.erms_company.ui.company

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.kust.erms_company.R
import com.kust.erms_company.databinding.FragmentProfileBinding
import com.kust.erms_company.ui.auth.AuthViewModel
import com.kust.ermslibrary.models.Company
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val authViewModel : AuthViewModel by viewModels()
    private var company = Company()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observer()
        updateUi()

        binding.btnUpdate.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_updateProfileFragment)
        }

    }

    private fun observer() {
        authViewModel.getSession {
            if (it != null) {
                company = it
            }
        }
    }

    private fun updateUi() {
        binding.profileView.apply {
            name.text = company.name
            tvEmail.text = company.email
            tvPhone.text = company.phone
            tvWebsite.text = company.website
            tvCountry.text = company.country
            tvState.text = company.state
            tvFullAddress.text = company.address

            Glide.with(requireContext())
                .load(company.profilePicture)
                .into(imgLogo)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}