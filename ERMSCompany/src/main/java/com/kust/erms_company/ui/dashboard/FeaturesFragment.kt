package com.kust.erms_company.ui.dashboard

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.kust.erms_company.R
import com.kust.erms_company.data.model.CompanyModel
import com.kust.erms_company.data.model.FeatureModel
import com.kust.erms_company.databinding.FragmentFeaturesBinding
import com.kust.erms_company.ui.auth.AuthViewModel
import com.kust.erms_company.ui.auth.RegistrationActivity
import com.kust.erms_company.ui.company.CompanyViewModel
import com.kust.erms_company.utils.UiState
import com.kust.erms_company.utils.toast
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class FeaturesFragment : Fragment() {

    private var _binding: FragmentFeaturesBinding? = null
    private val binding get() = _binding!!

    private val progressDialog : ProgressDialog by lazy {
        ProgressDialog(requireContext())
    }
    private val authViewModel : AuthViewModel by viewModels()
    private val companyViewModel : CompanyViewModel by viewModels()
    private var companyObj : CompanyModel? = null
    private val adapter by lazy { FeaturesListingAdapter() }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentFeaturesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        progressDialog.setMessage("Loading...")
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)

        observer()

        val features = mutableListOf<FeatureModel>()

        features.add(FeatureModel("Add Employee", R.drawable.ic_add))
        features.add(FeatureModel("Manage Employee", R.drawable.ic_manage_emp))
        features.add(FeatureModel("Select Manager", R.drawable.ic_select_emp))
        features.add(FeatureModel("Setting", R.drawable.ic_setting))
        features.add(FeatureModel("Profile", R.drawable.ic_profile))
        features.add(FeatureModel("Logout", R.drawable.ic_logout))

        adapter.features = features
        val layout = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
        binding.rvFeatures.layoutManager = layout
        binding.rvFeatures.adapter = adapter

        companyViewModel.getCompanyDetails


        adapter.setOnItemClickListener(object : FeaturesListingAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                when (position) {
                    0 -> {
                        findNavController().navigate(R.id.action_featuresFragment_to_addEmployeeFragment)
                    }
                    1 -> {
                        findNavController().navigate(R.id.action_featuresFragment_to_manageEmployeeFragment)
                    }
                    2 -> {
                        findNavController().navigate(R.id.action_featuresFragment_to_manageEmployeeFragment)
                    }
                    3 -> {

                    }
                    4 -> {

                        findNavController().navigate(R.id.action_featuresFragment_to_companyProfileFragment, Bundle().apply {
                            putParcelable("company", companyObj)
                        })
                    }
                    5 -> {
                        authViewModel.logout {
                            val intent = Intent(requireContext(), RegistrationActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        }
                    }
                }
            }
        })
    }

    private fun observer () {
        companyViewModel.getCompanyDetails.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Loading -> {
                    progressDialog.show()
                }
                is UiState.Success -> {
                    progressDialog.hide()
                    companyObj = it.data[0]
                }
                is UiState.Error -> {
                    progressDialog.hide()
                    requireActivity().toast(it.error)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}