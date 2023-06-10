package com.kust.ermsmanager.ui.event

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.kust.ermslibrary.utils.UiState
import com.kust.ermslibrary.utils.hide
import com.kust.ermslibrary.utils.show
import com.kust.ermslibrary.utils.toast
import com.kust.ermsmanager.databinding.FragmentEventListingBinding
import dagger.hilt.android.AndroidEntryPoint
import com.kust.ermsmanager.R as ManagerR

@AndroidEntryPoint
class EventListingFragment : Fragment() {

    private var _binding: FragmentEventListingBinding? = null
    private val binding get() = _binding!!

    private val eventViewModel: EventViewModel by viewModels()


    private val adapter by lazy {
        EventListingAdapter(
            context = requireContext(),
            onItemClicked = { _, event ->
                val bundle = Bundle()
                bundle.putParcelable("event", event)
                findNavController().navigate(
                    ManagerR.id.action_eventListingFragment_to_eventDetailFragment,
                    bundle
                )
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding =
            FragmentEventListingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observer()

        eventViewModel.getEventList()

        binding.rvEvents.layoutManager = LinearLayoutManager(requireContext())
        binding.rvEvents.adapter = adapter

        binding.fabCreateEvent.setOnClickListener {
            findNavController().navigate(ManagerR.id.action_eventListingFragment_to_createEventFragment)
        }
    }

    private fun observer() {
        eventViewModel.getEventList.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Loading -> {
                    binding.shimmerLayout.startShimmer()
                }
                is UiState.Success -> {
                    if (it.data.isEmpty()) {
                        binding.shimmerLayout.stopShimmer()
                        binding.shimmerLayout.hide()
                        binding.tvEventListStatus.show()
                        binding.rvEvents.hide()
                        binding.imageView7.show()
                    } else {
                        binding.shimmerLayout.stopShimmer()
                        binding.shimmerLayout.hide()
                        binding.rvEvents.show()
                        adapter.submitList(it.data)
                    }
                }
                is UiState.Error -> {
                    binding.shimmerLayout.stopShimmer()
                    binding.shimmerLayout.hide()
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