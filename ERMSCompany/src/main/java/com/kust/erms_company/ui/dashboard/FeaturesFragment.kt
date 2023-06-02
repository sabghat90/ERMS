package com.kust.erms_company.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.kust.erms_company.R
import com.kust.erms_company.databinding.FragmentFeaturesBinding
import com.kust.erms_company.ui.auth.AuthViewModel
import com.kust.erms_company.ui.auth.RegistrationActivity
import com.kust.ermslibrary.models.Feature
import com.kust.ermslibrary.utils.toast
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class FeaturesFragment : Fragment() {

    private var _binding: FragmentFeaturesBinding? = null
    private val binding get() = _binding!!

    private val authViewModel : AuthViewModel by viewModels()
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


        // rest the nav controller to the home fragment

        val features = mutableListOf<Feature>()

        features.add(Feature("Add Employee", R.drawable.ic_add_emp))
        features.add(Feature("Manage Employee", R.drawable.ic_manage_emp))
        features.add(Feature("Complaints", R.drawable.ic_report))
        features.add(Feature("Logout", R.drawable.ic_logout))

        adapter.features = features
        val layout = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
        binding.rvFeatures.layoutManager = layout
        binding.rvFeatures.adapter = adapter

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
                        findNavController().navigate(R.id.action_featuresFragment_to_complaintListingFragment)
                    }
                    3 -> {
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}