package com.kust.ermsemployee.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.kust.ermsemployee.R
import com.kust.ermsemployee.databinding.FragmentFeatureBinding
import com.kust.ermsemployee.ui.auth.AuthActivity
import com.kust.ermsemployee.ui.auth.AuthViewModel
import com.kust.ermslibrary.models.Feature
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class FeaturesFragment : Fragment() {

    private var _binding: FragmentFeatureBinding? = null
    private val binding get() = _binding!!

    private val authViewModel: AuthViewModel by viewModels()

    private val adapter by lazy { FeatureListingAdapter() }


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

        binding.rvFeatures.visibility = View.VISIBLE

        val features = mutableListOf<Feature>()

        features.add(Feature("View Employees", R.drawable.avatar2))
        features.add(Feature("Employee Ranking", R.drawable.avatar2))
        features.add(Feature("View Attendance", R.drawable.avatar2))
        features.add(Feature("Task", R.drawable.avatar2))
        features.add(Feature("Events", R.drawable.avatar2))
        features.add(Feature("Complaints", R.drawable.avatar2))
        features.add(Feature("Logout", R.drawable.avatar2))

        adapter.features = features

        val layout = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)

        binding.rvFeatures.layoutManager = layout

        binding.rvFeatures.adapter = adapter

        adapter.setOnItemClickListener(object : FeatureListingAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                when (position) {
                    0 -> {
                        Toast.makeText(requireContext(), "View Employees", Toast.LENGTH_SHORT)
                            .show()
                    }
                    1 -> {
                        findNavController().navigate(R.id.action_featureFragment_to_employeeRankListingFragment)
                    }
                    2 -> {
                        findNavController().navigate(R.id.action_featureFragment_to_viewAttendanceFragment)
                    }
                    3 -> {
                        findNavController().navigate(R.id.action_featureFragment_to_taskListingFragment)
                    }
                    4 -> {
                        findNavController().navigate(R.id.action_featureFragment_to_eventListingFragment)
                    }
                    5 -> {
                        findNavController().navigate(R.id.action_featureFragment_to_complaintListingFragment)
                    }
                    6 -> {
                        Toast.makeText(requireContext(), "Logout", Toast.LENGTH_SHORT).show()
                        // Logout
                        authViewModel.logout {
                            val intent = Intent(requireContext(), AuthActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
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