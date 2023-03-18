package com.kust.ermsmanager.ui.dashboard

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.kust.ermsmanager.R
import com.kust.ermsmanager.data.models.FeatureModel
import com.kust.ermsmanager.databinding.FragmentFeatureBinding
import com.kust.ermsmanager.ui.auth.AuthViewModel
import com.kust.ermsmanager.ui.employee.EmployeeViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class FeaturesFragment : Fragment() {

    private var _binding: FragmentFeatureBinding? = null
    private val binding get() = _binding!!

    private val employeeViewModel : EmployeeViewModel by viewModels()
    private val authViewModel : AuthViewModel by viewModels()

    private val adapter by lazy { FeaturesListingAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentFeatureBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val features = mutableListOf<FeatureModel>()

        features.add(FeatureModel("View Employees", R.drawable.avatar2))
        features.add(FeatureModel("Manage Employee", R.drawable.avatar2))
        features.add(FeatureModel("Mark Attendance", R.drawable.avatar2))
        features.add(FeatureModel("Task", R.drawable.avatar2))
        features.add(FeatureModel("Events", R.drawable.avatar2))
        features.add(FeatureModel("Setting", R.drawable.avatar2))
        features.add(FeatureModel("Profile", R.drawable.avatar2))
        features.add(FeatureModel("Logout", R.drawable.avatar2))

        adapter.features = features

        val layout = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)

        binding.rvFeature.layoutManager = layout

        binding.rvFeature.adapter = adapter

        adapter.setOnItemClickListener(object : FeaturesListingAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                when (position) {
                    0 -> {
                        findNavController().navigate(R.id.action_featureFragment_to_employeeListingFragment)
                    }
                    1 -> Toast.makeText(requireContext(), "Manage Employee", Toast.LENGTH_SHORT).show()
                    2 -> Toast.makeText(requireContext(), "Mark Attendance", Toast.LENGTH_SHORT).show()
                    3 -> Toast.makeText(requireContext(), "Task", Toast.LENGTH_SHORT).show()
                    4 -> Toast.makeText(requireContext(), "Events", Toast.LENGTH_SHORT).show()
                    5 -> Toast.makeText(requireContext(), "Setting", Toast.LENGTH_SHORT).show()
                    6 -> Toast.makeText(requireContext(), "Profile", Toast.LENGTH_SHORT).show()
                    7 -> {
                        authViewModel.logout() {
                            val intent = Intent(requireContext(), LoginActivity::class.java)
                            startActivity(intent)
                            requireActivity().finish()
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