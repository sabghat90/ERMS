package com.kust.ermsmanager.ui.event

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.kust.ermsmanager.data.models.EventModel
import com.kust.ermsmanager.databinding.EventItemBinding
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EventListingAdapter(
    val onItemClicked: (Int, EventModel) -> Unit
) : ListAdapter<EventModel, EventListingAdapter.EventViewHolder>(DiffUtilCallback()) {

    var eventList: MutableList<EventModel> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding =
            EventItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(eventList[position])
    }

    fun updateTaskList(list: MutableList<EventModel>) {
        eventList = list
    }

    inner class EventViewHolder(private val binding: EventItemBinding):
        RecyclerView.ViewHolder(binding.root) {
        fun bind(event: EventModel) {

            val eventDate = event.eventDate
            val format = SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.getDefault())
            try {
                val date = format.parse(eventDate)
                val currentDate = Timestamp.now().toDate()

                if (date != null) {
                    if (date.before(currentDate)) {
                        binding.tvEventStatus.text = "Expired"
                        binding.tvEventStatus.setTextColor(binding.root.context.getColor(android.R.color.holo_red_dark))
                    } else {
                        binding.tvEventStatus.text = "Upcoming"
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
            val parseCreationDate = Timestamp(Date(creationDate))
            val parseEventDate = Timestamp(Date(event.eventDate))
            val dateFormat = SimpleDateFormat("dd/MM/yyyy")
            val timeFormat = SimpleDateFormat("hh:mm a")
            val creationDateFormatted = dateFormat.format(parseCreationDate.toDate())
            val creationTimeFormatted = timeFormat.format(parseCreationDate.toDate())
            val eventDateFormatted = dateFormat.format(parseEventDate.toDate())
            val eventTimeFormatted = timeFormat.format(parseEventDate.toDate())


            binding.eventName.text = event.title
            binding.tvDateCreated.text = """$creationDateFormatted at $creationTimeFormatted"""
            binding.eventType.text = event.type
            binding.tvEventDate.text = """$eventDateFormatted at $eventTimeFormatted"""

            binding.eventCard.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClicked(position, eventList[position])
                }
            }
        }
    }

    class DiffUtilCallback : DiffUtil.ItemCallback<EventModel>() {
        override fun areItemsTheSame(oldItem: EventModel, newItem: EventModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: EventModel, newItem: EventModel): Boolean {
            return oldItem == newItem
        }
    }
}