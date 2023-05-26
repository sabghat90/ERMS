package com.kust.ermsemployee.ui.event

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.firebase.Timestamp
import com.kust.ermsemployee.R
import com.kust.ermsemployee.data.model.EventModel
import com.kust.ermsemployee.databinding.FragmentEventDetailBinding
import com.kust.ermsemployee.utils.ConvertDateAndTimeFormat
import dagger.hilt.android.AndroidEntryPoint
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

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
    }

    private fun updateUi() {
        event = arguments?.getParcelable("event")!!

        val eventDate = event.eventDate
        val format = SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.getDefault())
        try {
            val date = format.parse(eventDate)
            val currentDate = Timestamp.now().toDate()

            if (date != null) {
                if (date.before(currentDate)) {
                    binding.tvStatusMessage.text = getString(R.string.expired)
                    binding.tvStatusMessage.setTextColor(binding.root.context.getColor(android.R.color.holo_red_dark))
                } else {
                    binding.tvStatusMessage.text = getString(R.string.upcoming)
                    // change status color to green
                    binding.tvStatusMessage.setTextColor(binding.root.context.getColor(android.R.color.holo_green_dark))
                }
            } else {
                // Handle invalid date format
            }
        } catch (e: ParseException) {
            e.printStackTrace()
            // Handle date parsing error
        }

        val creationDateFormatted = ConvertDateAndTimeFormat().formatDate(event.dateCreated)
        val creationTimeFormatted = ConvertDateAndTimeFormat().formatTime(event.dateCreated)

        val eventDateFormatted = ConvertDateAndTimeFormat().formatDate(event.eventDate)
        val eventTimeFormatted = ConvertDateAndTimeFormat().formatTime(event.eventDate)


        with(binding) {
            tvEventName.text = event.title
            tvEventDescription.text = event.description
            tvCreatedDateTime.text = getString(R.string.date_time, creationDateFormatted, creationTimeFormatted)
            tvEventDateTime.text = getString(R.string.date_time, eventDateFormatted, eventTimeFormatted)
            tvLocation.text = event.location
            tvEventType.text = event.type
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}