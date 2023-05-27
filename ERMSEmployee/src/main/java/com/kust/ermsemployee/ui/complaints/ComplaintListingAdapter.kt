package com.kust.ermsemployee.ui.complaints

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kust.ermsemployee.data.model.ComplaintModel
import com.kust.ermsemployee.databinding.ComplaintItemBinding

class ComplaintListingAdapter(
    val context: Context,
    val onItemClicked: (Int, ComplaintModel) -> Unit
) : ListAdapter<ComplaintModel, ComplaintListingAdapter.ComplaintViewHolder>(DiffUtilCallback()) {

    var complaintList: MutableList<ComplaintModel> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComplaintViewHolder {
        val binding =
            ComplaintItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ComplaintViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ComplaintViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ComplaintViewHolder(private val binding: ComplaintItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(complaint: ComplaintModel) {

            binding.tvComplaintTitle.text = complaint.title
            binding.tvDateTime.text = complaint.dateCreated.toString()
            binding.tvStatus.text = complaint.status

            binding.complaintCard.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClicked(position, complaint)
                }
            }
        }
    }

    class DiffUtilCallback : DiffUtil.ItemCallback<ComplaintModel>() {
        override fun areItemsTheSame(oldItem: ComplaintModel, newItem: ComplaintModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ComplaintModel, newItem: ComplaintModel): Boolean {
            return oldItem == newItem
        }
    }
}