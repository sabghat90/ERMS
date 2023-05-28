package com.kust.erms_company.ui.complaints

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kust.erms_company.data.model.ComplaintHistoryModel
import com.kust.erms_company.databinding.ComplaintHistoryItemBinding

class ComplaintHistoryAdapter:
    ListAdapter<ComplaintHistoryModel, ComplaintHistoryAdapter.HistoryViewHolder>(DiffUtilCallback()) {

    var historyList = mutableListOf<ComplaintHistoryModel>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ComplaintHistoryItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class HistoryViewHolder(private val binding: ComplaintHistoryItemBinding):
        RecyclerView.ViewHolder(binding.root) {

            fun bind(historyModel: ComplaintHistoryModel) {
                binding.tvMsg.text = historyModel.message
            }
    }

    class DiffUtilCallback : DiffUtil.ItemCallback<ComplaintHistoryModel>() {
        override fun areItemsTheSame(oldItem: ComplaintHistoryModel, newItem: ComplaintHistoryModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ComplaintHistoryModel, newItem: ComplaintHistoryModel): Boolean {
            return oldItem == newItem
        }
    }
}