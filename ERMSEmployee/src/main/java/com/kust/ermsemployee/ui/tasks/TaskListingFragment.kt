package com.kust.ermsemployee.ui.tasks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.kust.ermsemployee.R
import com.kust.ermsemployee.databinding.FragmentTaskListingBinding
import com.kust.ermslibrary.models.Task
import com.kust.ermslibrary.utils.UiState
import com.kust.ermslibrary.utils.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TaskListingFragment : Fragment() {

    private var _binding : FragmentTaskListingBinding? = null
    private val binding get() = _binding!!

    private val taskViewModel : TaskViewModel by viewModels()

    private val adapter by lazy {
        TaskListingAdapter(
            context = requireContext(),
            onItemClicked = { _, task ->
                findNavController().navigate(R.id.action_taskListingFragment_to_taskDetailFragment, Bundle().apply {
                    putParcelable("task", task)
                })
            })
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentTaskListingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observer()

        binding.rvTaskListing.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTaskListing.adapter = adapter
    }

    // observer with UiState to handle the state of the data
    private fun observer() {
        taskViewModel.getTasks.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Loading -> {
                    binding.shimmerLayout.startShimmer()
                }
                is UiState.Success -> {
                    binding.shimmerLayout.stopShimmer()
                    binding.shimmerLayout.visibility = View.GONE
                    if (it.data.isEmpty()) {
                        binding.tvDataState.visibility = View.VISIBLE
                        binding.rvTaskListing.visibility = View.GONE
                    } else {
                        binding.rvTaskListing.visibility = View.VISIBLE
                        adapter.submitList(it.data)
                    }
                }
                is UiState.Error -> {
                    binding.shimmerLayout.stopShimmer()
                    binding.shimmerLayout.visibility = View.GONE
                    binding.tvDataState.visibility = View.VISIBLE
                    binding.tvDataState.text = getString(R.string.something_went_wrong)
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