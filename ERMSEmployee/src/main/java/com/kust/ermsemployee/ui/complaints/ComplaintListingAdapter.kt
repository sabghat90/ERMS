package com.kust.ermsemployee.ui.complaints

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kust.ermsemployee.databinding.ComplaintItemBinding
import com.kust.ermslibrary.models.Complaint

class ComplaintListingAdapter(
    val context: Context,
    val onItemClicked: (Int, Complaint) -> Unit
) : ListAdapter<Complaint, ComplaintListingAdapter.ComplaintViewHolder>(DiffUtilCallback()) {

    var complaintList: MutableList<Complaint> = arrayListOf()

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
        fun bind(complaint: Complaint) {

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

    class DiffUtilCallback : DiffUtil.ItemCallback<Complaint>() {
        override fun areItemsTheSame(oldItem: Complaint, newItem: Complaint): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Complaint, newItem: Complaint): Boolean {
            return oldItem == newItem
        }
    }
}