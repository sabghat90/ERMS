package com.kust.ermsmanager.ui.event

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.kust.ermsmanager.R
import com.kust.ermsmanager.data.models.EventModel
import com.kust.ermsmanager.databinding.FragmentEventDetailBinding
import com.kust.ermsmanager.utils.UiState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EventDetailFragment : Fragment() {
    private var _binding: FragmentEventDetailBinding? = null
    private val binding get() = _binding!!

    // event object
    private lateinit var event: EventModel

    private val eventViewModel: EventViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding =
            FragmentEventDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateUi()
        observer()

        binding.btnDeleteEvent.setOnClickListener {
            eventViewModel.deleteEvent(event)
        }
    }

    private fun observer() {
        eventViewModel.deleteEvent.observe(viewLifecycleOwner) {
            when(it) {
                is UiState.Loading -> {
                    binding.btnDeleteEvent.isEnabled = false
                    binding.btnDeleteEvent.text = ""
                }
                is UiState.Success -> {
                    binding.btnDeleteEvent.isEnabled = true
                    binding.btnDeleteEvent.text = getString(R.string.delete)
                    requireActivity().onBackPressed()
                }
                is UiState.Error -> {
                    binding.btnDeleteEvent.isEnabled = true
                    binding.btnDeleteEvent.text = getString(R.string.delete)
                }
            }
        }
    }

    private fun updateUi() {
        event = arguments?.getParcelable<EventModel>("event")!!

        with(binding) {
            tvEventName.text = event.title
            tvEventDescription.text = event.description
            tvCreatedDateTime.text = event.dateCreated + " " + event.timeCreated
            tvEventDateTime.text = event.date + " " + event.time
            tvLocation.text = event.location
            tvEventType.text = event.type
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}