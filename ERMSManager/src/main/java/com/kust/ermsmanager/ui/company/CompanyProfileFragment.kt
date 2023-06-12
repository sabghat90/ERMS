package com.kust.ermsmanager.ui.company

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.kust.ermslibrary.models.Company
import com.kust.ermslibrary.utils.UiState
import com.kust.ermslibrary.utils.hide
import com.kust.ermslibrary.utils.toast
import com.kust.ermsmanager.databinding.FragmentCompanyProfileBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CompanyProfileFragment : Fragment() {
    private var _binding : FragmentCompanyProfileBinding? = null
    private val binding get() = _binding!!

    private val companyViewModel: CompanyViewModel by viewModels()
    @Inject
    lateinit var companyObj: Company

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentCompanyProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observer()
        updateUi(companyObj)

        lifecycleScope.launch {
            companyViewModel.getCompanyProfile()
        }
    }

    private fun updateUi(company: Company) {
        binding.profileLayout.apply {
            name.text = company.name
            tvEmail.text = company.email
            tvPhone.text = company.phone
            tvWebsite.text = company.website
            tvCountry.text = company.country
            tvState.text = company.state
            tvFullAddress.text = company.address

            Glide.with(requireContext())
                .load(company.profilePicture)
                .placeholder(com.kust.ermslibrary.R.drawable.avatar2)
                .into(imgLogo)
        }

        // hide unnecessary views
        binding.profileLayout.apply {
            tvDepartment.hide()
            tvJobTitle.hide()
            tvSalary.hide()
            tvJoiningDate.hide()
            textView9.hide()
            view4.hide()
            textView15.hide()
            textView17.hide()
            textView19.hide()
            textView21.hide()
        }
    }

    private fun observer() {
        companyViewModel.getCompanyProfile.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Loading -> {
                    toast("Loading...")
                }
                is UiState.Success -> {
                    companyObj = it.data[0]
                    updateUi(companyObj)
                }
                is UiState.Error -> {
                    toast(it.error)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}