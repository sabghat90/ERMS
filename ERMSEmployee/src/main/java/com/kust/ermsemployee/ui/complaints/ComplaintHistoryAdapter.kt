package com.kust.ermsemployee.ui.complaints

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kust.ermsemployee.databinding.ComplaintHistoryItemBinding
import com.kust.ermslibrary.models.ComplaintHistory

class ComplaintHistoryAdapter:
    ListAdapter<ComplaintHistory, ComplaintHistoryAdapter.HistoryViewHolder>(DiffUtilCallback()) {

    var historyList = mutableListOf<ComplaintHistory>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ComplaintHistoryItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class HistoryViewHolder(private val binding: ComplaintHistoryItemBinding):
        RecyclerView.ViewHolder(binding.root) {

            fun bind(historyModel: ComplaintHistory) {
                binding.tvMsg.text = historyModel.message
            }
    }

    class DiffUtilCallback : DiffUtil.ItemCallback<ComplaintHistory>() {
        override fun areItemsTheSame(oldItem: ComplaintHistory, newItem: ComplaintHistory): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ComplaintHistory, newItem: ComplaintHistory): Boolean {
            return oldItem == newItem
        }
    }
}