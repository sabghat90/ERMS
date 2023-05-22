package com.kust.ermsemployee.ui.attendance

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kust.ermsemployee.data.model.AttendanceModel
import com.kust.ermsemployee.databinding.AttendanceItemBinding

class AttendanceListingAdapter :
    ListAdapter<AttendanceModel, AttendanceListingAdapter.AttendanceViewHolder>(DiffUtilCallback()) {

    var attendanceList: MutableList<AttendanceModel> = arrayListOf()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AttendanceListingAdapter.AttendanceViewHolder {
        val binding =
            AttendanceItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AttendanceViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: AttendanceListingAdapter.AttendanceViewHolder,
        position: Int
    ) {
        val attendance = attendanceList[position]
        holder.bind(attendance)
    }

    inner class AttendanceViewHolder(private val binding: AttendanceItemBinding):
    RecyclerView.ViewHolder(binding.root){
        fun bind(attendance: AttendanceModel){
            with(binding) {
                tvEmployeeName.text = attendance.employeeName
                tvDate.text = attendance.date
                tvStatus.text = attendance.status
                tvTime.text = attendance.time
            }
        }
    }

    class DiffUtilCallback : DiffUtil.ItemCallback<AttendanceModel>() {
        override fun areItemsTheSame(oldItem: AttendanceModel, newItem: AttendanceModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: AttendanceModel,
            newItem: AttendanceModel
        ): Boolean {
            return oldItem == newItem
        }
    }
}