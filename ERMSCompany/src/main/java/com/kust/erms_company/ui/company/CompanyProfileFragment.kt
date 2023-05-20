package com.kust.erms_company.ui.company

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.kust.erms_company.data.model.CompanyModel
import com.kust.erms_company.databinding.FragmentCompanyProfileBinding
import com.kust.erms_company.ui.auth.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CompanyProfileFragment : Fragment() {

    private var _binding: FragmentCompanyProfileBinding? = null
    private val binding get() = _binding!!

    private val authViewModel : AuthViewModel by viewModels()

    private var companyModel = CompanyModel()

    private val progressDialog by lazy {
        ProgressDialog(requireContext())
    }


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

        progressDialog.setMessage("Loading...")
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)

        observer()
        updateUi()

    }

    private fun observer() {
        authViewModel.getSession {
            if (it != null) {
                companyModel = it
            }
        }
    }

    private fun updateUi() {
        binding.profileView.apply {
            companyName.text = companyModel.name
            tvEmail.text = companyModel.email
            tvPhone.text = companyModel.phone
            tvWebsite.text = companyModel.website
            tvCountry.text = companyModel.country
            tvState.text = "companyObj.state"
            tvFullAddress.text = "companyObj.fullAddress"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}