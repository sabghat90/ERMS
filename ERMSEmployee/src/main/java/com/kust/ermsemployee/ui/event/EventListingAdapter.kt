package com.kust.ermsemployee.ui.event

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.kust.ermsemployee.R
import com.kust.ermsemployee.data.model.EventModel
import com.kust.ermsemployee.databinding.EventItemBinding
import com.kust.ermsemployee.utils.ConvertDateAndTimeFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

class EventListingAdapter(
    val context: Context,
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

    // receive event call from handler to update event list
    fun updateEventList(eventList: MutableList<EventModel>) {
        this.eventList = eventList
        notifyDataSetChanged()
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