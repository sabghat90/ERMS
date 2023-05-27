package com.kust.ermsemployee.ui.events

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.kust.ermsemployee.R
import com.kust.ermsemployee.data.model.EventModel
import com.kust.ermsemployee.databinding.FragmentEventListingBinding
import com.kust.ermsemployee.utils.UiState
import com.kust.ermsemployee.utils.hide
import com.kust.ermsemployee.utils.show
import com.kust.ermsemployee.utils.toast
import dagger.hilt.android.AndroidEntryPoint

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
                    R.id.action_eventListingFragment_to_eventDetailFragment,
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

        binding.rvEvents.layoutManager = LinearLayoutManager(requireContext())
        binding.rvEvents.adapter = adapter

    }

    private fun observer() {
        eventViewModel.getEventList.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Loading -> {
                    binding.shimmerLayout.startShimmer()
                }
                is UiState.Success -> {
                    binding.shimmerLayout.stopShimmer()
                    binding.shimmerLayout.hide()
                    if (it.data.isEmpty()) {
                        binding.tvEventListStatus.show()
                        binding.rvEvents.hide()
                    } else {
                        binding.rvEvents.show()
                        adapter.eventList = it.data as MutableList<EventModel>
                        adapter.submitList(it.data)
                        Handler(Looper.getMainLooper()).postDelayed({
                            adapter.updateEventList(it.data)
                        }, 1000)
                    }
                }
                is UiState.Error -> {
                    binding.shimmerLayout.stopShimmer()
                    binding.shimmerLayout.hide()
                    binding.rvEvents.hide()
                    binding.tvEventListStatus.show()
                    binding.tvEventListStatus.text = getString(R.string.something_went_wrong)
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