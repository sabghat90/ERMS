package com.kust.ermsmanager.ui.event

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.kust.ermslibrary.models.Event
import com.kust.ermslibrary.utils.ConvertDateAndTimeFormat
import com.kust.ermsmanager.R
import com.kust.ermsmanager.databinding.EventItemBinding
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

class EventListingAdapter(
    val context: Context,
    val onItemClicked: (Int, Event) -> Unit
) : ListAdapter<Event, EventListingAdapter.EventViewHolder>(DiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding =
            EventItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class EventViewHolder(private val binding: EventItemBinding):
        RecyclerView.ViewHolder(binding.root) {
        fun bind(event: Event) {

            val eventDate = event.eventDate
            val format = SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.getDefault())
            try {
                val date = format.parse(eventDate)
                val currentDate = Timestamp.now().toDate()

                if (date != null) {
                    if (date.before(currentDate)) {
                        binding.tvEventStatus.text = context.getString(R.string.expired)
                        binding.tvEventStatus.setTextColor(binding.root.context.getColor(android.R.color.holo_red_dark))
                    } else {
                        binding.tvEventStatus.text = context.getString(R.string.upcoming)
                        // change status color to green
                        binding.tvEventStatus.setTextColor(binding.root.context.getColor(android.R.color.holo_green_dark))
                    }
                } else {
                    // Handle invalid date format
                }
            } catch (e: ParseException) {
                e.printStackTrace()
                // Handle date parsing error
            }

            val creationDate = event.dateCreated

            val eventDateFormatted = ConvertDateAndTimeFormat().formatDate(eventDate)
            val eventTimeFormatted = ConvertDateAndTimeFormat().formatTime(eventDate)
            val creationDateFormatted = ConvertDateAndTimeFormat().formatDate(creationDate)
            val creationTimeFormatted = ConvertDateAndTimeFormat().formatTime(creationDate)


            binding.eventName.text = event.title
            binding.tvDateCreated.text = context.getString(R.string.date_time, creationDateFormatted, creationTimeFormatted)
            binding.eventType.text = event.type
            binding.tvEventDate.text = context.getString(R.string.date_time, eventDateFormatted, eventTimeFormatted)

            binding.eventCard.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClicked(position, getItem(position))
                }
            }
        }
    }

    class DiffUtilCallback : DiffUtil.ItemCallback<Event>() {
        override fun areItemsTheSame(oldItem: Event, newItem: Event): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Event, newItem: Event): Boolean {
            return oldItem == newItem
        }
    }
}