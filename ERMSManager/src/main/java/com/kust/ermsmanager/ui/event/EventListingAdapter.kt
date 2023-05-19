package com.kust.ermsmanager.ui.event

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kust.ermsmanager.data.models.EventModel
import com.kust.ermsmanager.databinding.EventItemBinding

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
        notifyDataSetChanged()
    }

    inner class EventViewHolder(private val binding: EventItemBinding):
        RecyclerView.ViewHolder(binding.root) {
        fun bind(event: EventModel) {

            binding.eventName.text = event.title
            binding.tvDateCreated.text = event.dateCreated
            binding.eventType.text = event.type
            binding.tvEventDate.text = event.date

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