package com.kust.erms_company.ui.company

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.kust.erms_company.data.model.CompanyModel
import com.kust.erms_company.databinding.FragmentCompanyProfileBinding
import com.kust.erms_company.utils.UiState
import com.kust.erms_company.utils.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CompanyProfileFragment : Fragment() {

    private val TAG = "CompanyProfileFragment"

    private var _binding: FragmentCompanyProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var companyObj: CompanyModel

    private lateinit var viewModel: CompanyViewModel

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


        viewModel = ViewModelProvider(this)[CompanyViewModel::class.java]

        progressDialog.setMessage("Loading...")
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)

        observer()

        updateUi()

    }

    private fun observer() {
        viewModel.getCompanyDetails.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    progressDialog.show()
                }
                is UiState.Success -> {
                    progressDialog.dismiss()
                    companyObj = state.data[0]
                    updateUi()
                }
                is UiState.Error -> {
                    progressDialog.dismiss()
                    toast(state.error)
                }
            }
        }
    }

    private fun updateUi() {
        companyObj = arguments?.getParcelable("company")!!
        binding.profileView.apply {
            companyName.text = companyObj.name
            tvEmail.text = companyObj.email
            tvPhone.text = companyObj.phone
            tvWebsite.text = companyObj.website
            tvCountry.text = companyObj.country
            tvState.text = "companyObj.state"
            tvFullAddress.text = "companyObj.fullAddress"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}